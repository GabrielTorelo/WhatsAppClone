package com.gabrieltorelo.whatsappclone.view.activities.profile.actions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityCryptographyBinding;
import com.gabrieltorelo.whatsappclone.service.CryptoService;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.gabrieltorelo.whatsappclone.tools.CameraPreview;
import com.gabrieltorelo.whatsappclone.tools.CodeScanner;
import com.gabrieltorelo.whatsappclone.util.TextViewUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Random;

public class CryptographyActivity extends AppCompatActivity {

    private ActivityCryptographyBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String receiverID, userName, codeBD, codeQR,
            codeRe0, codeRe1, codeRe2, codeReFinal;
    private Bitmap bitmap;
    private String bitmapPath = "";
    private static boolean scanCam = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cryptography);

        initActionClick();
        initToolbar();
        initialize();
    }

    private void initialize() {
        Intent intent = getIntent();
        receiverID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);

        if (receiverID != null && userName != null) {
            binding.openLink.setMovementMethod(LinkMovementMethod.getInstance());
//            binding.openLink.setText(getString(R.string.CryptographyMessageAndLearnMore, userName));
            TextViewUtils.stripUnderlines(binding.openLink);
            binding.tvName.setText(String.format("%s, %s", getString(R.string.you), userName));

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("cryptoCode").exists()) {
                        codeBD = snapshot.child("cryptoCode").getValue().toString();
                        codeRe0 = codeBD.substring(0, 23);
                        codeRe1 = codeBD.substring(23, 47);
                        codeRe2 = codeBD.substring(47, 71);
                        codeReFinal = String.format(" %s\n%s\n%s", codeRe0, codeRe1, codeRe2);

                        qrCodeGenerator(codeBD);
                        binding.tvCode.setText(codeReFinal);
                    } else {
                        CryptoService.cryptoCode(receiverID, getApplicationContext());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), R.string.error_unexpected,
                            Toast.LENGTH_SHORT).show();
                }
            });
            changeScreen();
        }
        else {
            finish();
            Toast.makeText(this, R.string.error_unexpected,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initActionClick() {
        binding.scanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCam = true;
                changeScreen();
            }
        });
    }

    private void changeScreen(){
        if (scanCam){
            binding.nestedScroll.setVisibility(View.GONE);
            binding.layoutWrongContact.setVisibility(View.GONE);
//            binding.cPreviewCode.setVisibility(View.VISIBLE);
//            binding.layoutCoverCam.setVisibility(View.VISIBLE);
            binding.scanCode.setText(R.string.crypto_scan_cam);
            binding.scanCode.setGravity(Gravity.CENTER);
            binding.scanCode.setTextColor(getResources().getColor(R.color.colorTextLightGray));
            binding.scanCode.setTextSize(15);
            IntentIntegrator intentIntegrator = new IntentIntegrator(CryptographyActivity.this);
            intentIntegrator.setPrompt("");
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setCaptureActivity(CodeScanner.class);
            intentIntegrator.initiateScan();
        }
        else {
            binding.nestedScroll.setVisibility(View.VISIBLE);
            binding.imageViewQrCode.setVisibility(View.VISIBLE);
            binding.imageViewQrCodeVerify.setVisibility(View.GONE);
            binding.layoutWrongContact.setVisibility(View.GONE);
//            binding.cPreviewCode.setVisibility(View.GONE);
//            binding.layoutCoverCam.setVisibility(View.GONE);
            binding.scanCode.setText(R.string.crypto_scan_code_Caps);
            binding.scanCode.setGravity(Gravity.END);
            binding.scanCode.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
            binding.scanCode.setTextSize(14);
        }
    }

    private void qrCodeGenerator(String code){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = null;
        codeQR = code+" {"+firebaseUser.getUid()+"}";

        try {
            bitMatrix = multiFormatWriter.encode(codeQR,
                    BarcodeFormat.QR_CODE, 500, 500);
        } catch (WriterException e) {
            Toast.makeText(getApplicationContext(), R.string.error_unexpected,
                    Toast.LENGTH_SHORT).show();
        }
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        bitmap = barcodeEncoder.createBitmap(bitMatrix);
        binding.imageViewQrCode.setImageBitmap(bitmap);
    }

    private void shareCode(){
        Intent shareExternal = new Intent(Intent.ACTION_SEND);
        shareExternal.setType("image/*");
        shareExternal.putExtra(Intent.EXTRA_STREAM, createBitmap());
        shareExternal.putExtra(Intent.EXTRA_TEXT, getString(R.string.crypto_share_code, codeReFinal));
        startActivity(Intent.createChooser(shareExternal, DataTimeService.getCurrentTime()));
    }

    /* RESOLVE DUPLICATED IMAGE ON SHARE */
    public Uri createBitmap(){
        if (bitmapPath.equals("")){
            bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                    DataTimeService.getCurrentTime(), null);
        }
        Uri uri = Uri.parse(bitmapPath);
        return uri;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String cryptoFinal = codeQR.substring(0, 71) + " {"+receiverID+"}";
        Handler h = new Handler();

        scanCam = false;
        changeScreen();

        if (intentResult.getContents() != null && intentResult.getContents().length() == 102){
            if (intentResult.getContents().substring(73, 101).equals(receiverID)){
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
                if (intentResult.getContents().equals(cryptoFinal)){
                    binding.cardViewQrCodeVerify.startAnimation(animation);
                    binding.cardViewQrCodeVerify.setVisibility(View.VISIBLE);
                    binding.imageViewQrCodeVerify.setVisibility(View.VISIBLE);
                    binding.cardViewQrCodeVerify.setCardBackgroundColor(getResources().getColor(R.color.colorGreen));
                    binding.imageViewQrCodeVerify.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_baseline_check_100, null));
                }
                else {
                    binding.cardViewQrCodeVerify.startAnimation(animation);
                    binding.cardViewQrCodeVerify.setVisibility(View.VISIBLE);
                    binding.imageViewQrCodeVerify.setVisibility(View.VISIBLE);
                    binding.cardViewQrCodeVerify.setCardBackgroundColor(getResources().getColor(R.color.colorCallReceived));
                    binding.imageViewQrCodeVerify.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_baseline_close_100, null));
                }
            }
            else {
                binding.nestedScroll.setVisibility(View.GONE);
                binding.layoutWrongContact.setVisibility(View.VISIBLE);
                binding.tvWrongContact.setText(getString(R.string.crypto_scan_wrong_contact, userName, userName));
            }
        }
        else {
//            Toast.makeText(this, getString(R.string.error_unexpected), Toast.LENGTH_SHORT).show();
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
                binding.cardViewQrCodeVerify.startAnimation(animation);
                binding.cardViewQrCodeVerify.setVisibility(View.GONE);
            }
        };
        h.postDelayed(r, 3000);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            if (scanCam){
                scanCam = false;
                changeScreen();
            }
            else {
                finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_cryptography, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            case android.R.id.home :
                if (scanCam){
                    scanCam = false;
                    changeScreen();
                }
                else {
                    finish();
                }
                break;

            case R.id.menu_share :
                if (scanCam){
                    scanCam = false;
                    changeScreen();
                }
                shareCode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}