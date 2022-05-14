package com.gabrieltorelo.whatsappclone.view.activities.display;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gabrieltorelo.whatsappclone.BuildConfig;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.common.Common;
import com.gabrieltorelo.whatsappclone.databinding.ActivityViewProfileImageBinding;
import com.gabrieltorelo.whatsappclone.view.activities.profile.ProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ViewProfileImageActivity extends AppCompatActivity {

    private ActivityViewProfileImageBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    private BottomSheetDialog bottomSheetDialog;
    private ProgressDialog progressDialog;

    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;
    private boolean picture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_profile_image);
        binding.imageView.setImageBitmap(Common.IMAGE_BITMAP);

        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        if (firebaseUser != null){
            getInfo();
        }

        initBtnClick();
        initToolbar();
    }

    private void initBtnClick() {
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPickPhoto();
            }
        });

        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Compartilhar Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String imageProfile = documentSnapshot.getString("imageProfile");

                        if (!imageProfile.equals("")){
                            Glide.with(ViewProfileImageActivity.this).load(imageProfile).into(binding.imageView);
                            picture = true;
                        }
                        else{
                            finish();
                            picture = false;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Get Data", "onFailure: ERROR - "+e.getMessage());
            }
        });
    }

    private void showDialogRemovePicture(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileImageActivity.this);
        builder.setMessage(R.string.profile_PictureRemove);
        builder.setPositiveButton(R.string.removeCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                removePicture();
            }
        });
        builder.setNegativeButton(R.string.cancelCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showBottomSheetPickPhoto() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick, null);
        View layoutRemovePicture = view.findViewById(R.id.ln_removePhoto);
        View layoutGallery = view.findViewById(R.id.ln_gallery);
        View layoutCamera = view.findViewById(R.id.ln_camera);

        layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });
        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
                bottomSheetDialog.dismiss();
            }
        });

        if (picture) {
            layoutRemovePicture.setVisibility(View.VISIBLE);
            layoutRemovePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogRemovePicture();
                    bottomSheetDialog.dismiss();
                }
            });
        }
        else {
            layoutRemovePicture.setVisibility(View.GONE);
        }

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog = null;
            }
        });
        bottomSheetDialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, 221);
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 222);
        }
        else {
            openCamera();
        }
    }

    private void removePicture(){
        firestore.collection("Users").document(firebaseUser.getUid()).update("imageProfile", "")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), R.string.profile_PictureRemoved, Toast.LENGTH_SHORT).show();
                        getInfo();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), R.string.profile_PictureRemovedError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            imageUri = data.getData();

            uploadToFirebase();
        }

        if (requestCode == 440
                && resultCode == RESULT_OK){

            uploadToFirebase();
        }
    }

    /* CHANGE TO FIREBASE SERVICE */
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeInfo = MimeTypeMap.getSingleton();
        return mimeTypeInfo.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /* CHANGE TO FIREBASE SERVICE */
    private void uploadToFirebase() {
        if (imageUri != null){
            progressDialog.setMessage("Por favor aguarde...");
            progressDialog.show();

            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("imagesProfile/"
                    + System.currentTimeMillis()+"."+getFileExtension(imageUri));
            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadUri = uriTask.getResult();

                    final String sdownload_url = String.valueOf(downloadUri);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageProfile", sdownload_url);

                    progressDialog.dismiss();
                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), R.string.profile_ChangePicture, Toast.LENGTH_SHORT).show();
                                    getInfo();
                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.profile_ChangePictureError, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void openGallery() {
        Intent intent =  new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, ""+R.string.profile_SelectPicture), IMAGE_GALLERY_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        try {
            File file = File.createTempFile("IMG_" + timeStamp, ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,  imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 440);
        } catch (IOException e) {
            e.printStackTrace();
        }
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