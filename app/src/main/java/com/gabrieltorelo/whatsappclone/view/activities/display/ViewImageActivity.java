package com.gabrieltorelo.whatsappclone.view.activities.display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityViewImageBinding;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewImageActivity extends AppCompatActivity {

    private ActivityViewImageBinding binding;
    private String chatID, userSenderID, date, time, imageView, favorite;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private DatabaseReference reference;
    private String bitmapPath = "";
    public int i, pos = 0;
    private float rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_image);

        initBtnClick();
        initialize();
    }

    private void initialize() {
        Intent intent = getIntent();
        setSupportActionBar(binding.toolbar);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        chatID = intent.getStringExtra("chatID");
        userSenderID = intent.getStringExtra("userSenderID");
        favorite = intent.getStringExtra("favorite");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        imageView = intent.getStringExtra("imageView");
        pos = intent.getIntExtra("position", 0);
        rotation = Float.parseFloat(intent.getStringExtra("rotation"));

        if (firebaseUser.getUid().equals(userSenderID)){
            binding.tvName.setText(R.string.you);
        }
        else {
            firestore.collection("Users").document(userSenderID).get().addOnSuccessListener(
                    new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String userName = documentSnapshot.getString("userName");
                            binding.tvName.setText(userName);
                        }
                    });
        }

        if (date.equals(DataTimeService.getCurrentDate())){
            binding.tvDate.setText(String.format("%s, %s",
                    getResources().getString(R.string.today),
                    time));
        }
        else if(date.equals(DataTimeService.getBeforeDate())){
            binding.tvDate.setText(String.format("%s, %s",
                    getResources().getString(R.string.yesterday),
                    time));
        }
        else{
            binding.tvDate.setText(String.format("%s, %s",
                    date,
                    time));
        }
        if (!imageView.equals("")) {
            Glide.with(getApplicationContext()).load(imageView).into(binding.imageView);
            binding.imageView.setRotation(rotation);
        }
        else{
            finish();
            Toast.makeText(getApplicationContext(), R.string.view_image_error, Toast.LENGTH_SHORT).show();
        }
        if (favorite.equals("YES")){
            binding.imageFavorite.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_baseline_star_24, null));
            i = 1;
        }
        else {
            binding.imageFavorite.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_baseline_star_border_24, null));
            i = 0;
        }
    }

    private void initBtnClick() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* CREATE GALLERY MEDIAS */
        binding.layoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Galeria Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFavorite();
            }
        });

        /* CREATE SHARE INTERNAL IMAGE*/
        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Compartilhar Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFavorite(){
        if (i == 0){
            binding.imageFavorite.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_baseline_star_24, null));
            reference.child("Chat").child(chatID).child("favorite").setValue("YES");
            i = 1;
        }
        else {
            binding.imageFavorite.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_baseline_star_border_24, null));
            reference.child("Chat").child(chatID).child("favorite").setValue("NO");
            i = 0;
        }
    }

    private void showDialogRemoveMessage(){
        View checkBoxView = View.inflate(this, R.layout.frame_checkbox_delete, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox_delete);
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewImageActivity.this);
        builder.setMessage(R.string.view_image_remove_picture).setView(checkBoxView);
        if (userSenderID.equals(firebaseUser.getUid())){
            builder.setPositiveButton(R.string.removeForAllCaps, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    removeForAllPicture();
                }
            });
        }
        else{
            builder.setPositiveButton(R.string.removeForMeCaps, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    removeForMePicture();
                }
            });
        }

        builder.setNegativeButton(R.string.cancelCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void removeForMePicture() {
        reference.child("Chat").child(chatID).child("receiverRemoved").setValue("YES");
        Toast.makeText(getApplicationContext(), R.string.view_image_message_me_removed, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void removeForAllPicture(){
        reference.child("Chat").child(chatID).removeValue();
        Toast.makeText(getApplicationContext(), R.string.view_image_message_all_removed, Toast.LENGTH_SHORT).show();
        finish();
    }

    /* CREATE RECALL RECYCLERVIEW POSITION OF SPECIFIED */
    private void showInChat(){
        finish();
        Toast.makeText(getApplicationContext(),"A posição é: "+pos, Toast.LENGTH_SHORT).show();
    }

    public void shareExternal() {
        Intent shareExternal = new Intent(Intent.ACTION_SEND);
        shareExternal.setType("image/*");
        shareExternal.putExtra(Intent.EXTRA_STREAM, createBitmap());
        startActivity(Intent.createChooser(shareExternal, DataTimeService.getCurrentTime()));
    }

    public void showInGallery(){
        Intent shareExternal = new Intent(Intent.ACTION_VIEW);
        shareExternal.setDataAndType(createBitmap(), "image/*");
        startActivity(shareExternal);
    }

    /* RESOLVE DUPLICATED IMAGE ON SHARE/VIEWGALLERY */
    public Uri createBitmap(){
        Bitmap bitmap = ((GlideBitmapDrawable)binding.imageView.getDrawable().getCurrent()).getBitmap();
        if (bitmapPath.equals("")){
            bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                    DataTimeService.getCurrentTime(), null);
        }
        Uri uri = Uri.parse(bitmapPath);
        return uri;
    }

    public void setRotation(){
        rotation = rotation+90;
        if (rotation == 360){
            rotation = 0;
        }
        binding.imageView.setRotation(rotation);
        reference.child("Chat").child(chatID).child("rotation").setValue(String.format("%s", rotation));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_view_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            /* CREATE GALLERY MEDIAS */
            case R.id.action_all_media : Toast.makeText(getApplicationContext(), "Ação: Todas as mídias", Toast.LENGTH_SHORT).show(); break;

            case R.id.action_show_message : showInChat(); break;
            case R.id.action_share : shareExternal(); break;
            case R.id.action_view_gallery : showInGallery(); break;
            case R.id.action_spin : setRotation(); break;
            case R.id.action_delete : showDialogRemoveMessage(); break;

            /* CREATE CROP ACTIVITY */
            case R.id.action_change_picture : Toast.makeText(getApplicationContext(), "Ação: Minha foto de perfil", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_change_group_image : Toast.makeText(getApplicationContext(), "Ação: Imagem do grupo", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_change_wallpaper : Toast.makeText(getApplicationContext(), "Ação: Papel de parede", Toast.LENGTH_SHORT).show(); break;
        }
        return super.onOptionsItemSelected(item);
    }
}