package com.gabrieltorelo.whatsappclone.service;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gabrieltorelo.whatsappclone.model.StatusModel;
import com.gabrieltorelo.whatsappclone.view.activities.dialog.DialogReviewSendImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class FirebaseService {

    private Context context;
    public StorageReference riversRef;

    public FirebaseService(Context context) {
        this.context = context;
    }

    public void uploadImageToFireBaseStorage(String type, Uri uri, final OnCallBack onCallBack){
        if (type.equals("Status")){
            riversRef = FirebaseStorage.getInstance().getReference().child("Status/Images/"
                    + System.currentTimeMillis()+"."+getFileExtension(uri));
        }
        else{
            riversRef = FirebaseStorage.getInstance().getReference().child("Chats/Images/"
                    + System.currentTimeMillis()+"."+getFileExtension(uri));
        }
        riversRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                final String sdownload_url = String.valueOf(downloadUri);

                onCallBack.onUploadSuccess(sdownload_url);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCallBack.onUploadFailed(e);
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeInfo = MimeTypeMap.getSingleton();
        return mimeTypeInfo.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public interface OnCallBack{
        void onUploadSuccess(String imageUris);
        void onUploadFailed(Exception e);
    }
}
