package com.gabrieltorelo.whatsappclone.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gabrieltorelo.whatsappclone.interfaces.OnReadStatusCallBack;
import com.gabrieltorelo.whatsappclone.model.user.Connection;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConnectionService {
    private Context context;
    private DatabaseReference connectionReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private String receiverID;
    private String status;

    public ConnectionService(Context context, String receiverID) {
        this.context = context;
        this.receiverID = receiverID;
    }

    public ConnectionService(Context context) {
        this.context = context;
    }

    public void readStatusData(final OnReadStatusCallBack onReadStatusCallBack){
        DatabaseReference userConnection = FirebaseDatabase.getInstance().getReference();
        userConnection.child("UserConnection").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Connection> list = new ArrayList<>();
                list.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Connection connection = snapshot.getValue(Connection.class);

                    if (connection.getUserId().equals(receiverID)){
//                        String userID = connection.getUserid();
//                        String userStatus = connection.getStatus();
//                        Toast.makeText(context,
//                                "ReceiverID: "+receiverID+
//                                "UserID: "+userID+
//                                "UserStatus: "+userStatus, Toast.LENGTH_LONG).show();
                        list.add(connection);
                    }
                }
                onReadStatusCallBack.onReadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onReadStatusCallBack.onReadFailed();
            }
        });
    }

    public void manageConnections(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        connectionReference = firebaseDatabase.getReference()
                .child("UserConnection").child(firebaseUser.getUid());

        DatabaseReference infoConnected = firebaseDatabase.getReference(".info/connected");
        infoConnected.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if(connected) {
                    Connection connection = new Connection(
                            "Online",
                            firebaseUser.getUid(),
                            "now",
                            "now"
                    );

                    Connection disconnection = new Connection(
                            "Offline",
                            firebaseUser.getUid(),
                            DataTimeService.getCurrentDate(),
                            DataTimeService.getCurrentTime()
                    );

                    connectionReference.setValue(connection)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.w("Status -> connection", "onSuccess: ");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Status -> connection", "onFailure: "+e.getMessage());
                        }
                    });

                    connectionReference.onDisconnect().setValue(disconnection)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.w("Status -> disconnection", "onSuccess: ");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Status -> disconnection", "onFailure: "+e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Um erro inesperado foi detectado. " +
                                "Reinicie o aplicativo e tente novamente! CODE: "+error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
