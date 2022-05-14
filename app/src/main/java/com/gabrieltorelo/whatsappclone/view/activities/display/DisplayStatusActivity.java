package com.gabrieltorelo.whatsappclone.view.activities.display;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityDisplayStatusBinding;
import com.gabrieltorelo.whatsappclone.view.activities.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DisplayStatusActivity extends AppCompatActivity {

    private ActivityDisplayStatusBinding binding;
    private String userID, imageStatus, statusDate, textStatus, viewCount;
    private int numStatus;
    int progressBarValue = 0;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_display_status);

        hideToolBr();
        initialize();
        getUserInfo();
        initBtnClick();
    }

    public void hideToolBr(){
        int newUiOptions = getWindow().getDecorView().getSystemUiVisibility();

        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void initialize() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        imageStatus = intent.getStringExtra("imageStatus");
        statusDate = intent.getStringExtra("statusDate");
        textStatus = intent.getStringExtra("textStatus");
        viewCount = intent.getStringExtra("viewCount");
        numStatus = intent.getIntExtra("numStatus", 0);
    }

    private void initBtnClick() {
        binding.layoutIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.layoutReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Responder Click", Toast.LENGTH_SHORT).show();
            }
        });
        binding.layoutStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Estatísticas Click", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "More Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users").document(userID).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String userName = documentSnapshot.getString("userName");
                        String imageProfile = documentSnapshot.getString("imageProfile");

                        binding.tvName.setText(userName);

                        if (!imageProfile.equals("")){
                            Glide.with(getApplicationContext()).load(imageProfile).into(binding.imageProfile);
                        }

                        if (userID.equals(firebaseUser.getUid())){
                            binding.layoutStatistics.setVisibility(View.VISIBLE);
                            binding.layoutReply.setVisibility(View.GONE);
                            binding.btnMore.setVisibility(View.GONE);
                        }
                        else{
                            binding.layoutStatistics.setVisibility(View.GONE);
                            binding.layoutReply.setVisibility(View.VISIBLE);
                            binding.btnMore.setVisibility(View.VISIBLE);
                        }

                    }
                });
        Glide.with(getApplicationContext()).load(imageStatus).into(binding.imageView);
        binding.tvDateStatus.setText(statusDate);
        binding.tvDesc.setText(textStatus);
        if (textStatus.equals("")){
            binding.tvDesc.setVisibility(View.GONE);
        }
        binding.viewsCount.setText(viewCount);
        nextStatus();
    }

    public void nextStatus(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressBarValue < 10000)
                {
                    progressBarValue++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.progressHorizontal.setProgress(progressBarValue);
                        }
                    });
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Log.e("ProgressBar", "run: ", e);
                    }
                }
                if (progressBarValue == 10000){
                    numStatus = numStatus - 1;
                    if (numStatus == 0){
                        finish();
                    }
                    else {
                        /* INCLUDE NEXT STATUS */
                    }
                }
            }
        }).start();
    }
}