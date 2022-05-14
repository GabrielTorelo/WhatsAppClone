package com.gabrieltorelo.whatsappclone.view.activities.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.adapter.ChatAdapter;
import com.gabrieltorelo.whatsappclone.adapter.ConnectionAdapter;
import com.gabrieltorelo.whatsappclone.databinding.ActivityChatBinding;
import com.gabrieltorelo.whatsappclone.interfaces.OnReadChatCallBack;
import com.gabrieltorelo.whatsappclone.manager.ChatService;
import com.gabrieltorelo.whatsappclone.manager.ConnectionService;
import com.gabrieltorelo.whatsappclone.model.chat.Chat;
import com.gabrieltorelo.whatsappclone.model.user.Connection;
import com.gabrieltorelo.whatsappclone.service.ColorClickService;
import com.gabrieltorelo.whatsappclone.service.FirebaseService;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.gabrieltorelo.whatsappclone.view.activities.dialog.DialogReviewSendImage;
import com.gabrieltorelo.whatsappclone.view.activities.profile.UserProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final int REQUEST_CORD_PERMISSION = 332;
    private int IMAGE_GALLERY_REQUEST = 111;
    private ActivityChatBinding binding;
    private ChatAdapter chatAdapter;
    private ConnectionAdapter connectionAdapter;
    private ChatService chatService;
    private ConnectionService connectionService;
    private List<Chat> list = new ArrayList<>();
    private List<Connection> listConnection = new ArrayList<>();
    private String receiverID, imageProfile, userName, userNumber,
            userBio, userBioDate, status;
    private ProgressDialog progressDialog;
    private String textSubtitle;
    private boolean isActionClipShow, TextMessageOK, ImageMessageOK, AudioMessageOK = false;
    private Uri imageUri;
    public static MediaRecorder mediaRecorder;
    private String audio_path;
    private String sTime;
    private long timer;
    private Object OnBlockCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat, null);
        setSupportActionBar(binding.toolbar);

        initialize();
        initBtnClick();
        readChat();
        readStatus();
    }

    private void initialize(){
        Intent intent = getIntent();
        receiverID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");
        userBio = intent.getStringExtra("userBio");
        userBioDate = intent.getStringExtra("userBioDate");
        imageProfile = intent.getStringExtra("imageProfile");
        userNumber = intent.getStringExtra("userNumber");

//        final DatabaseReference userConnection = firebaseDatabase.getReference().child("UserConnection").child(receiverID);
//        userConnection.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<Connection> listStatus = new ArrayList<>();
//
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Connection connection = dataSnapshot.getValue(Connection.class);
//
//                    listStatus.add(connection);
//                    status = "s: "+connection.getStatus();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                binding.tvStatus.setVisibility(View.GONE);
//            }
//        });


        connectionService = new ConnectionService(this, receiverID);
        chatService = new ChatService(this, receiverID);
//        boolean init = chatService.userBlocked(userName);

        if (receiverID != null){
            binding.tvName.setText(userName);

//            if (status != null) {
//                binding.tvChatStatus.setText(status);
//            }
//            else {
//                binding.tvChatStatus.setVisibility(View.GONE);
//            }

            if (imageProfile != null && !imageProfile.equals("")) {
                Glide.with(this).load(imageProfile).into(binding.imageProfile);
            }
        }

        binding.editMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        binding.editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.editMessage.setMaxLines(6);
                binding.btnCamera.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(binding.editMessage.getText().toString())){
                    binding.btnSend.setVisibility(View.VISIBLE);
                    binding.layoutRecordView.setVisibility(View.INVISIBLE);
                }
                else{
                    binding.btnSend.setVisibility(View.INVISIBLE);
                    binding.layoutRecordView.setVisibility(View.VISIBLE);
                    binding.editMessage.setMaxLines(1);
                    binding.btnCamera.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(list, this);
        connectionAdapter = new ConnectionAdapter(listConnection, this);
        binding.recyclerView.setAdapter(chatAdapter);

        binding.recordButton.setRecordView(binding.recordView);
        binding.recordView.setOnRecordListener(new OnRecordListener()  {
            @Override
            public void onStart() {
                binding.layoutTextView.setVisibility(View.INVISIBLE);

                if (!checkPermissionFromDevice()) {
                    startRecord();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(100);
                    }
                }
                else {
                    requestPermission();
                }
            }

            @Override
            public void onCancel() {
                binding.btnEmoji.setVisibility(View.INVISIBLE);
                binding.layoutTextView.setVisibility(View.VISIBLE);

                try{
                    mediaRecorder.reset();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(long recordTime) {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.layoutTextView.setVisibility(View.VISIBLE);
                timer = recordTime;
                sTime = getHumanTimeText(recordTime);
                stopRecord();
            }

            @Override
            public void onLessThanSecond() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.layoutTextView.setVisibility(View.VISIBLE);
            }
        });

        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_DENIED || record_audio_result == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_CORD_PERMISSION);
    }

    private void stopRecord(){
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                AudioMessageOK = chatService.sendVoice(audio_path, timer, userName);

            } else {
                Toast.makeText(getApplicationContext(), "Gravação nula -> ", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Erro na parada da Gravação -> "+e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecord(){
        setUpMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        }
        catch (IOException e){
            e.printStackTrace();
            Toast.makeText(ChatActivity.this, "Erro na Gravação, reinicie o aplicativo e tente novamente!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpMediaRecorder() {
        String path_save = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path = path_save;

        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        } catch (Exception e) {
            Log.d(TAG, "setUpMediaRecord: " + e.getMessage());
        }

    }

    private void readChat() {
        chatService.readChatData(new OnReadChatCallBack() {
            @Override
            public void onReadSuccess(List<Chat> list) {
                chatAdapter.setList(list);
            }

            @Override
            public void onReadFailed() {

            }
        });
    }

    /* ALTER TO connectionService.readStatusData*/
    private void readStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("UserConnection").child(receiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userStatus = snapshot.child("status").getValue().toString();
                String LastConnDate = snapshot.child("lastConnDate").getValue().toString();
                String LastConnTime = snapshot.child("lastConnTime").getValue().toString();
                if (userStatus.equals("Online")){
                    binding.tvChatStatus.setText(userStatus);
                }
                else{
                    if (LastConnDate.equals(DataTimeService.getCurrentDate())){
                        binding.tvChatStatus.setText(String.format("%s, %s",
                                getResources().getString(R.string.today),
                                LastConnTime));
                    }
                    else if(LastConnDate.equals(DataTimeService.getBeforeDate())){
                        binding.tvChatStatus.setText(String.format("%s, %s",
                                getResources().getString(R.string.yesterday),
                                LastConnTime));
                    }
                    else{
                        binding.tvChatStatus.setText(String.format("%s, %s",
                                LastConnDate,
                                LastConnTime));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
            }
        });

//        connectionService.readStatusData(new OnReadStatusCallBack() {
//            @Override
//            public void onReadSuccess(List<Connection> list1) {
//                try {
//                    connectionAdapter.setList(list1);
//                }
//                catch (Exception e){
//                    Toast.makeText(getApplicationContext(), "ERROR -> "+e.getMessage(), Toast.LENGTH_LONG).show();
//                    Log.d(TAG, "onReadSuccess: "+e.getMessage());
//                }
//            }
//
//            @Override
//            public void onReadFailed() {
//
//            }
//        });
    }

    private void initBtnClick(){
        binding.layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT",  "PRIMARY_DARK",
                        "PRIMARY", true, binding.layoutBack, null, null,
                        null, null, null);
                finish();
            }
        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.editMessage.getText().toString())){
                    if (chatService.userBlocked(userName)){
                        if (chatService.sendTextMessage(binding.editMessage.getText().toString(), userName)) {
                            binding.editMessage.setText("");
                        }
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "USER NÃO DESBLOQUEADO", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT",  "PRIMARY_DARK",
                        "PRIMARY", false, binding.openProfile, null, null,
                        null, null, null);
                startActivity(new Intent(ChatActivity.this, UserProfileActivity.class)
                .putExtra("userID", receiverID)
                .putExtra("imageProfile", imageProfile)
                .putExtra("userName", userName)
                .putExtra("userNumber", userNumber)
                .putExtra("userBio", userBio)
                .putExtra("userBioDate", userBioDate));
            }
        });

        binding.btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT",  "GRAY",
                        "WHITE", true, null, binding.imageViewEmoji, null,
                        null, null, null);
                Toast.makeText(getApplicationContext(), "Emoji Click: ",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT",  "GRAY",
                        "WHITE", true, null, binding.imageViewFile, null,
                        null, null, null);
                if (isActionClipShow){
                    binding.layoutClip.setVisibility(View.GONE);
                    isActionClipShow = false;
                }
                else{
                    binding.layoutClip.setVisibility(View.VISIBLE);
                    isActionClipShow = true;
                }
            }
        });

        binding.btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT",  "GRAY",
                        "WHITE", true, null, binding.imageViewCamera, null,
                        null, null, null);
                Toast.makeText(getApplicationContext(), "Câmera Click",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnClipActionDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Documento Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnClipActionCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Câmera Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnClipActionGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        binding.btnClipActionAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Áudio Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnClipActionLivingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Sala Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnClipActionLocalization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Localização Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnClipActionContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Contato Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent =  new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione a imagem"), IMAGE_GALLERY_REQUEST);
        binding.layoutClip.setVisibility(View.GONE);
        isActionClipShow = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null){

            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                reviewImage(bitmap);

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.error_image_upload, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void reviewImage(Bitmap bitmap){
        new DialogReviewSendImage(ChatActivity.this, bitmap).show(new DialogReviewSendImage.OnCallBack() {
            @Override
            public void OnButtonSendClick() {
                if (imageUri != null){
                    progressDialog = new ProgressDialog(ChatActivity.this);
                    progressDialog.setMessage("Por favor aguarde...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new FirebaseService(ChatActivity.this).uploadImageToFireBaseStorage("Chat", imageUri,
                            new FirebaseService.OnCallBack() {
                                @Override
                                public void onUploadSuccess(String imageUrl) {

                                    textSubtitle = "Texto gerado automaticamente";
                                    ImageMessageOK = chatService.sendImage(imageUrl, textSubtitle, userName);
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onUploadFailed(Exception e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            case R.id.menu_video_call : Toast.makeText(getApplicationContext(), "Chamada de Video Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.menu_voice_call : Toast.makeText(getApplicationContext(), "Chamada de Voz Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_view_contact : Toast.makeText(getApplicationContext(), "Ver contato Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_view_all_media : Toast.makeText(getApplicationContext(), "Mídia, links e docs Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_search : Toast.makeText(getApplicationContext(), "Pesquisar Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_silence_notify : Toast.makeText(getApplicationContext(), "Silenciar notificações Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_change_wallpaper : Toast.makeText(getApplicationContext(), "Papel de parede Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_report : Toast.makeText(getApplicationContext(), "Denunciar Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_block : Toast.makeText(getApplicationContext(), "Bloquear Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_clear_chat : Toast.makeText(getApplicationContext(), "Limpar conversa Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_export_chat : Toast.makeText(getApplicationContext(), "Exportar conversa Click", Toast.LENGTH_SHORT).show(); break;
            case R.id.action_add_shortcut : Toast.makeText(getApplicationContext(), "Adicionar atalho Click", Toast.LENGTH_SHORT).show(); break;
        }
        return super.onOptionsItemSelected(item);
    }
}