package com.gabrieltorelo.whatsappclone.menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.adapter.ChatListAdapter;
import com.gabrieltorelo.whatsappclone.databinding.FragmentChatsBinding;
import com.gabrieltorelo.whatsappclone.model.ChatList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment";

    public ChatsFragment() {

    }

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private Handler handler = new Handler();

    private List<ChatList> list;

    private FragmentChatsBinding binding;

    private ArrayList<String> allUserID;

    private ChatListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false);

        list = new ArrayList<>();
        allUserID = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(list, getContext());
        binding.recyclerView.setAdapter(adapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null){
            getChatList();
        }
        else{
            binding.lnInvite.setVisibility(View.VISIBLE);
            binding.progressCircular.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }

    private void getChatList() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                allUserID.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String userID = snapshot.child("chatId").getValue().toString();

                    binding.progressCircular.setVisibility(View.GONE);
                    allUserID.add(userID);
                }
                getUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUserInfo() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID : allUserID){
                    firestore.collection("Users").document(userID).get().addOnSuccessListener(
                            new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.d(TAG, "onSuccess: ddd"+documentSnapshot.getString("userName"));
                                    try {
                                        ChatList chat = new ChatList(
                                                documentSnapshot.getString("userID"),
                                                documentSnapshot.getString("userName"),
                                                documentSnapshot.getString("bio"),
                                                documentSnapshot.getString("bioDate"),
                                                "Carregando...",
                                                "",
                                                documentSnapshot.getString("imageProfile"),
                                                documentSnapshot.getString("userPhone")
                                        );
                                        list.add(chat);
                                    }catch (Exception e){
                                        Log.d(TAG, "onSuccess: "+e.getMessage());
                                    }

                                    if (adapter != null){
                                        adapter.notifyItemInserted(0);
                                        adapter.notifyDataSetChanged();

                                        Log.d(TAG, "onSuccess: adapter -> "+adapter.getItemCount());
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: ERROR -> "+e.getMessage());
                        }
                    });
                }
            }
        });
    }
}