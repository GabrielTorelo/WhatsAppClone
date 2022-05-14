package com.gabrieltorelo.whatsappclone.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.interfaces.OnReadChatCallBack;
import com.gabrieltorelo.whatsappclone.model.chat.Chat;
import com.gabrieltorelo.whatsappclone.service.CryptoService;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class ChatService {
    private Context context;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String receiverID;
    public boolean USER_UNBLOCK;

    public ChatService(Context context, String receiverID) {
        this.context = context;
        this.receiverID = receiverID;
    }

    public ChatService(Context context) {
        this.context = context;
    }

    public void readChatData(final OnReadChatCallBack onReadChatCallBack){
        reference.child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chat> list = new ArrayList<>();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat != null
                            && chat.getSender().equals(firebaseUser.getUid())
                            && chat.getReceiver().equals(receiverID) ||
                        chat != null
                            && chat.getSender().equals(receiverID)
                            && chat.getReceiver().equals(firebaseUser.getUid())
                    ) {
                        list.add(chat);
                    }
                }
                onReadChatCallBack.onReadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onReadChatCallBack.onReadFailed();
            }
        });
    }

    public boolean sendTextMessage(final String text, final String userName){
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);

        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("blocked").exists()){
                    showDialogUnBlockUser(userName);
                    USER_UNBLOCK = false;
                }
                else {
                    CryptoService.cryptoCode(receiverID, context);
                    Chat chat = new Chat(
                            UUID.randomUUID().toString(),
                            DataTimeService.getCurrentDate(),
                            DataTimeService.getCurrentTime(),
                            text,
                            "",
                            "TEXT",
                            "",
                            "NO",
                            "NO",
                            "",
                            firebaseUser.getUid(),
                            receiverID,
                            "NO"
                    );

                    reference.child("Chat").push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Send", "onSuccess: ");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Send", "onFailure: "+e.getMessage());
                        }
                    });

                    chatRef1.child("chatId").setValue(receiverID);
                    chatRef1.child("clean").removeValue();

                    DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList")
                            .child(receiverID).child(firebaseUser.getUid());
                    chatRef2.child("chatId").setValue(firebaseUser.getUid());

                    USER_UNBLOCK = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, context.getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show();
            }
        });

        return USER_UNBLOCK;
    }

    public boolean sendImage(final String imageUrl, final String description, final String userName){
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);

        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("blocked").exists()){
                    showDialogUnBlockUser(userName);
                    USER_UNBLOCK = false;
                }
                else {
                    CryptoService.cryptoCode(receiverID, context);
                    Chat chat = new Chat(
                            UUID.randomUUID().toString(),
                            DataTimeService.getCurrentDate(),
                            DataTimeService.getCurrentTime(),
                            ""+description,
                            imageUrl,
                            "IMAGE",
                            "0.0",
                            "NO",
                            "NO",
                            "",
                            firebaseUser.getUid(),
                            receiverID,
                            "NO"
                    );

                    reference.child("Chat").push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Send", "onSuccess: ");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Send", "onFailure: "+e.getMessage());
                        }
                    });

                    chatRef1.child("chatId").setValue(receiverID);
                    chatRef1.child("clean").removeValue();

                    DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList")
                            .child(receiverID).child(firebaseUser.getUid());
                    chatRef2.child("chatId").setValue(firebaseUser.getUid());

                    USER_UNBLOCK = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, context.getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show();
            }
        });

        return USER_UNBLOCK;
    }

    public boolean sendVoice(final String audioPath, final long audioTime, final String userName){
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);

        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("blocked").exists()){
                    showDialogUnBlockUser(userName);
                    USER_UNBLOCK = false;
                }
                else {
                    final Uri uriAudio = Uri.fromFile(new File(audioPath));
                    final StorageReference audioRef = FirebaseStorage.getInstance().getReference().child("Chats/Voices/" + System.currentTimeMillis());
                    audioRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot audioSnapshot) {
                            CryptoService.cryptoCode(receiverID, context);
                            Task<Uri> urlTask = audioSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            String voiceUrl = String.valueOf(downloadUrl);

                            Chat chat = new Chat(
                                    UUID.randomUUID().toString(),
                                    DataTimeService.getCurrentDate(),
                                    DataTimeService.getCurrentTime(),
                                    "",
                                    voiceUrl,
                                    "VOICE",
                                    "",
                                    "NO",
                                    "NO",
                                    ""+audioTime,
                                    firebaseUser.getUid(),
                                    receiverID,
                                    "NO"
                            );

                            reference.child("Chat").push().setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Send", "onSuccess: ");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Send", "onFailure: "+e.getMessage());
                                }
                            });

                            chatRef1.child("chatId").setValue(receiverID);
                            chatRef1.child("clean").removeValue();

                            DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList")
                                    .child(receiverID).child(firebaseUser.getUid());
                            chatRef2.child("chatId").setValue(firebaseUser.getUid());
                        }
                    });

                    USER_UNBLOCK = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, context.getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show();
            }
        });

        return USER_UNBLOCK;
    }

    public boolean userBlocked(final String userName){
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);
        final Semaphore semaphore = new Semaphore(0);
        final ArrayList<Boolean> loadedBookmarks = new ArrayList<Boolean>();

        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("blocked").exists()){
                    showDialogUnBlockUser(userName);
                    USER_UNBLOCK = false;
                } 
                else{
                    USER_UNBLOCK = true;
                }
                Log.e(String.valueOf(USER_UNBLOCK), "userBlocked1: ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, context.getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show();
            }
        });

        Log.e(String.valueOf(USER_UNBLOCK), "userBlocked2: ");
        return USER_UNBLOCK;
    }

    public boolean showDialogUnBlockUser(String userName) {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.chatService_unBlock_user, userName));
        builder.setPositiveButton(R.string.unBlockCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                userRef.child("blocked").removeValue();
                USER_UNBLOCK = true;
            }
        });

        builder.setNegativeButton(R.string.cancelCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                USER_UNBLOCK = false;
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return USER_UNBLOCK;
    }
}
