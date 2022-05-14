package com.gabrieltorelo.whatsappclone.view.activities.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gabrieltorelo.whatsappclone.BuildConfig;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivitySetUserInfoBinding;
import com.gabrieltorelo.whatsappclone.model.user.Users;
import com.gabrieltorelo.whatsappclone.view.MainActivity;
import com.gabrieltorelo.whatsappclone.view.activities.profile.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

public class SetUserInfoActivity extends AppCompatActivity {

    private ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private BottomSheetDialog bottomSheetDialog;
    private int IMAGE_GALLERY_REQUEST = 111;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_user_info);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            getInfo();
                        } else {
                            Log.w("TAG", "Erro inteiro, reinicie o aplicativo e tente novamente!", task.getException());
                        }
                    }
                });


        progressDialog = new ProgressDialog(this);
        initButtonClick();
    }

    private void initButtonClick() {

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.editName.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Insira o nome de usuário", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadToFirebase();
                }
            }
        });

        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPickPhoto();

            }
        });
    }

    private void showBottomSheetPickPhoto() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick, null);

        ((View) view.findViewById(R.id.ln_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });
        ((View) view.findViewById(R.id.ln_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
                bottomSheetDialog.dismiss();
            }
        });
        ((View) view.findViewById(R.id.ln_removePhoto)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Foto Removida", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });


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

    private void openGallery() {
        Intent intent =  new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione a imagem"), IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            imageUri = data.getData();
            Glide.with(SetUserInfoActivity.this).load(imageUri).into(binding.imageProfile);
        }

        if (requestCode == 440
                && resultCode == RESULT_OK){

            Glide.with(SetUserInfoActivity.this).load(imageUri).into(binding.imageProfile);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeInfo = MimeTypeMap.getSingleton();
        return mimeTypeInfo.getExtensionFromMimeType(contentResolver.getType(uri));
    }

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
                    hashMap.put("userName", binding.editName.getText().toString());

                    progressDialog.dismiss();
                    db.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro inteiro, reinicie o aplicativo e tente novamente!",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void getInfo() {
        db.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String userName = documentSnapshot.getString("userName");
                        String imageProfile = documentSnapshot.getString("imageProfile");

                        binding.editName.setText(userName);

                        if (!imageProfile.equals("")){
                            Glide.with(SetUserInfoActivity.this).load(imageProfile).into(binding.imageProfile);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Get Data", "onFailure: ERROR - "+e.getMessage());
            }
        });
    }
}