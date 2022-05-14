package com.gabrieltorelo.whatsappclone.view.activities.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivitySettingsBinding;
import com.gabrieltorelo.whatsappclone.view.activities.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            getInfo();
        }

        initClickAction();
        initToolbar();
    }

    private void initClickAction() {
        binding.lnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
            }
        });

        binding.layoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Conta Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.layoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Conversas Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.layoutNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Notificações Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.layoutDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Armazenamento e dados Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.layoutHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Ajuda Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.layoutInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Convide seus amigos Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName = documentSnapshot.get("userName").toString();
                String bio = documentSnapshot.get("bio").toString();
                String imageProfile = documentSnapshot.get("imageProfile").toString();

                binding.tvName.setText(userName);
                binding.tvBio.setText(bio);

                if (!imageProfile.equals("")){
                    Glide.with(SettingsActivity.this).load(imageProfile).into(binding.imageProfile);
                }
                else{
                    Glide.with(SettingsActivity.this).load(R.drawable.person_no_picture)
                            .into(binding.imageProfile);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Get Data", "onFailure: ERROR - "+e.getMessage());
            }
        });
    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}