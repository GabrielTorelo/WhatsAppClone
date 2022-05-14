package com.gabrieltorelo.whatsappclone.view.activities.status;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityAddStatusBinding;
import com.gabrieltorelo.whatsappclone.model.StatusModel;
import com.gabrieltorelo.whatsappclone.service.FirebaseService;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.UUID;

public class AddStatusActivity extends AppCompatActivity {

    private ActivityAddStatusBinding binding;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    public int i, j, k, w = 0;
    private int xDelta, yDelta;
    private int orientation;
    private ViewGroup mainLayout;
    private Bitmap picture = StatusActivity.bitmap;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_status);
        mainLayout = (RelativeLayout) findViewById(R.id.layout_principal);

        initialize();
        initBtnClick();
        initToolbar();
    }

    private void initialize(){
        binding.imageView.setImageBitmap(picture);
        orientation = this.getResources().getConfiguration().orientation;
        progressDialog = new ProgressDialog(AddStatusActivity.this);
        progressDialog.setMessage("Por favor aguarde...");
        progressDialog.setCancelable(false);

        if (orientation != Configuration.ORIENTATION_PORTRAIT) {
            binding.layoutFilters.setVisibility(View.GONE);
        }
        else {
            binding.layoutFilters.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initBtnClick() {
        binding.btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cortar Click", Toast.LENGTH_SHORT).show();
                setKeyboard(false);
            }
        });

        binding.btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = btnActions(binding.btnEmoji, binding.btnAddText,  binding.btnAddDraw, i, false); j = 0; k = 0;
            }
        });

        binding.btnAddText.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                i = 0; j = btnActions(binding.btnAddText, binding.btnEmoji, binding.btnAddDraw,  j, true); k = 0;
                if (j != 0){
                    binding.editAddText.setOnTouchListener(onTouchListener());
                }
            }
        });

        binding.btnAddDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 0; j = 0; k = btnActions(binding.btnAddDraw, binding.btnEmoji, binding.btnAddText, k, false);
            }
        });

        binding.layoutFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeyboard(false);
                Toast.makeText(getApplicationContext(), "Filtros Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnActionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeyboard(false);
                Toast.makeText(getApplicationContext(), "Adicionar outras Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSend.setEnabled(false);
                progressDialog.show();
                setStatus();
                setKeyboard(false);
            }
        });
    }

    public int btnActions(CardView btn1, CardView btn2, CardView btn3, int var, boolean keyboard){
        btn2.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
        btn3.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (var == 0){
            btn1.setCardBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
            setKeyboard(keyboard);
            var = 1;
        }
        else {
            btn1.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
            setKeyboard(false);
            var = 0;
        }
        return var;
    }

    public void setStatus(){
        final Uri imageUri = getImageUri(getApplicationContext(), picture);
        new FirebaseService(AddStatusActivity.this).uploadImageToFireBaseStorage("Status", imageUri,
                new FirebaseService.OnCallBack() {
                    @Override
                    public void onUploadSuccess(String imageUris) {
                        StatusModel status = new StatusModel(
                                UUID.randomUUID().toString(),
                                FirebaseAuth.getInstance().getUid(),
                                DataTimeService.getCurrentDate(),
                                DataTimeService.getCurrentTime(),
                                imageUris,
                                binding.editImageMessage.getText().toString(),
                                "0",
                                "NO"
                        );

                        reference.child("StatusDaily").push().setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),
                                        "Compartilhando atualização de status...", Toast.LENGTH_LONG).show();
                                finish();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Erro ao compartilhar atualização de status", Toast.LENGTH_LONG).show();
                                finish();
                                progressDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onUploadFailed(Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Erro ao compartilhar atualização de status", Toast.LENGTH_LONG).show();
                        finish();
                        progressDialog.dismiss();
                    }
                });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext
                .getContentResolver(), inImage, String.valueOf(Calendar.getInstance()
                .getTimeInMillis()), "WhatsAppClone Status Daily");
        return Uri.parse(path);
    }

    /* RESOLVE THE FOCUS, WHEN CLICKING ON EDIT TEXT */
    private void setKeyboard(boolean choose){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(choose){
            imm.toggleSoftInputFromWindow(binding.editAddText.getWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
            binding.editAddText.setVisibility(View.VISIBLE);
            binding.editAddText.requestFocus();
        }
        else {
            imm.hideSoftInputFromWindow(binding.editAddText.getWindowToken(), 0);
            binding.editAddText.clearFocus();
        }
    }

    /* RESOLVE THE RIGHT MARGIN, BROKEN THE TEXT */
    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN :
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();
                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_MOVE :
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                return true;
            }
        };
    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            setKeyboard(false);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}