package com.gabrieltorelo.whatsappclone.service;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gabrieltorelo.whatsappclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class CryptoService {
    private static DatabaseReference referenceUser;
    private static DatabaseReference referenceReceiver;
    private static FirebaseUser firebaseUser;

    public static void cryptoCode(String receiverID, final Context context){
        final String cryptoCode = cryptoGenerator();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceUser = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);
        referenceReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(receiverID).child(firebaseUser.getUid());

        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("cryptoCode").exists()){
                    referenceUser.child("cryptoCode").setValue(cryptoCode);
                    referenceReceiver.child("cryptoCode").setValue(cryptoCode);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, R.string.error_unexpected, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String cryptoGenerator(){
        int RandomNumber;
        int min = 10000;
        int max = 99999;
        String finalNum;
        Random Number;
        Number = new Random();
        finalNum = String.format("%s",Number.nextInt((max - min) + 1) + min);
        for (int i = 0; i <= 10; i++){
            RandomNumber = Number.nextInt((max - min) + 1) + min;
            finalNum = String.format("%s %s", finalNum, RandomNumber);
        }
        return finalNum;
    }
}
