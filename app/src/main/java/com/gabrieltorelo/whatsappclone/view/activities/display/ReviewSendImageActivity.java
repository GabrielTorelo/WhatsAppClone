package com.gabrieltorelo.whatsappclone.view.activities.display;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityReviewSendImageBinding;
import com.gabrieltorelo.whatsappclone.manager.ChatService;
import com.gabrieltorelo.whatsappclone.model.StatusModel;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.gabrieltorelo.whatsappclone.service.FirebaseService;
import com.gabrieltorelo.whatsappclone.view.activities.chat.ChatActivity;
import com.gabrieltorelo.whatsappclone.view.activities.status.AddStatusActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.UUID;

public class ReviewSendImageActivity extends AppCompatActivity {

    private ActivityReviewSendImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_send_image);

        initToolbar();
        initBtnClick();
    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initBtnClick() {
        binding.btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cortar Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReviewSendImageActivity.this, "Emoji Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnAddText.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                Toast.makeText(ReviewSendImageActivity.this, "Add Texto click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnAddDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReviewSendImageActivity.this, "Lápis Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnActionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Adicionar outras Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSend.setEnabled(false);
            }
        });
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