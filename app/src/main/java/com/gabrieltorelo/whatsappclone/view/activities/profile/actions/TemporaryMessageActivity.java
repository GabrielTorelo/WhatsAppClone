package com.gabrieltorelo.whatsappclone.view.activities.profile.actions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityTemporaryMessageBinding;
import com.gabrieltorelo.whatsappclone.util.TextViewUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class TemporaryMessageActivity extends AppCompatActivity {

    private ActivityTemporaryMessageBinding binding;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private String receiverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_temporary_message);

        initialize();
        initActionClick();
        initToolbar();
    }

    private void initialize() {
        Intent intent = getIntent();
        receiverID = intent.getStringExtra("userID");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);

        if (receiverID != null) {
            binding.openLink.setMovementMethod(LinkMovementMethod.getInstance());
            TextViewUtils.stripUnderlines(binding.openLink);
        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                /* GET TEMPORARY MESSAGE */
                if (snapshot.child("messageTemporary").exists()) {
                    binding.radioEnabledTemporary.setChecked(true);
                }
                else {
                    binding.radioDisabledTemporary.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initActionClick() {
        binding.radioGroupTemporary.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId)
                {
                    case R.id.radio_enabled_temporary :
                        reference.child("messageTemporary").setValue("YES");
                        break;
                    default :
                        reference.child("messageTemporary").removeValue();
                        break;
                }
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
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}