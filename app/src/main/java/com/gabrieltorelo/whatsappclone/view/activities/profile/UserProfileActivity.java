package com.gabrieltorelo.whatsappclone.view.activities.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.renderscript.ScriptGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.adapter.GalleryListAdapter;
import com.gabrieltorelo.whatsappclone.common.Common;
import com.gabrieltorelo.whatsappclone.databinding.ActivityUserProfileBinding;
import com.gabrieltorelo.whatsappclone.interfaces.OnReadChatCallBack;
import com.gabrieltorelo.whatsappclone.manager.ChatService;
import com.gabrieltorelo.whatsappclone.model.chat.Chat;
import com.gabrieltorelo.whatsappclone.service.ColorClickService;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.gabrieltorelo.whatsappclone.view.MainActivity;
import com.gabrieltorelo.whatsappclone.view.activities.chat.ChatActivity;
import com.gabrieltorelo.whatsappclone.view.activities.display.ViewImageActivity;
import com.gabrieltorelo.whatsappclone.view.activities.display.ViewProfileImageActivity;
import com.gabrieltorelo.whatsappclone.view.activities.profile.actions.CryptographyActivity;
import com.gabrieltorelo.whatsappclone.view.activities.profile.actions.NotifyActivity;
import com.gabrieltorelo.whatsappclone.view.activities.profile.actions.TemporaryMessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private ActivityUserProfileBinding binding;
    private GalleryListAdapter adapter;
    private ChatService chatService;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference, referenceChat;
    private ArrayList<Chat> list = new ArrayList<>();
    private String receiverID, userName, imageProfile, userNumber,
            userBio, userBioDate, monthOfBio;
    private int radioMuteClick = 2, radioMediaClick;
    private boolean radioCheck, userBlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);

        initActionClick();
        initToolbar();
        initialize();
    }

    private void initialize() {
        Intent intent = getIntent();
        receiverID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");
        userBio = intent.getStringExtra("userBio");
        userBioDate = intent.getStringExtra("userBioDate");
        imageProfile = intent.getStringExtra("imageProfile");
        userNumber = intent.getStringExtra("userNumber");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        referenceChat = FirebaseDatabase.getInstance().getReference("Chat");
        reference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);

        if (receiverID != null) {
            binding.toolbar.setTitle(userName);
            binding.tvBio.setText(userBio);
            monthOfBio = userBioDate.substring(3, 5);
            switch (monthOfBio) {
                case "01":
                    binding.tvDateBio.setText(getString(R.string.bio_january,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "02":
                    binding.tvDateBio.setText(getString(R.string.bio_february,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "03":
                    binding.tvDateBio.setText(getString(R.string.bio_march,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "04":
                    binding.tvDateBio.setText(getString(R.string.bio_april,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "05":
                    binding.tvDateBio.setText(getString(R.string.bio_may,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "06":
                    binding.tvDateBio.setText(getString(R.string.bio_june,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "07":
                    binding.tvDateBio.setText(getString(R.string.bio_july,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "08":
                    binding.tvDateBio.setText(getString(R.string.bio_august,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "09":
                    binding.tvDateBio.setText(getString(R.string.bio_september,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "10":
                    binding.tvDateBio.setText(getString(R.string.bio_october,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "11":
                    binding.tvDateBio.setText(getString(R.string.bio_november,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
                case "12":
                    binding.tvDateBio.setText(getString(R.string.bio_december,
                            userBioDate.substring(0, 2), userBioDate.substring(6, 10)));
                    break;
            }
            binding.tvPhone.setText(userNumber);
            chatService = new ChatService(this, receiverID);
            adapter = new GalleryListAdapter(list, this);

            if (imageProfile != null && !imageProfile.equals("")) {
                Glide.with(this).load(imageProfile).into(binding.imageProfile);
            } else {
                Glide.with(this).load(R.drawable.person_no_picture).into(binding.imageProfile);
            }

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    /* MUTE NOTIFY */
                    if (snapshot.child("muted").exists()) {
                        binding.switchMuteNotify.setChecked(true);
                    } else {
                        binding.switchMuteNotify.setChecked(false);
                    }

                    /* MEDIA VISIBILITY */
                    if (snapshot.child("mediaShow").exists()) {
                        radioCheck = false;
                        radioMediaClick = 2;
                    } else {
                        radioCheck = true;
                        radioMediaClick = 1;
                    }

                    /* TEMPORARY MESSAGES */
                    if (snapshot.child("messageTemporary").exists()) {
                        binding.tvMessageTemporary.setText(R.string.enabled);
                    } else {
                        binding.tvMessageTemporary.setText(R.string.disabled);
                    }

                    /* BLOCK USER */
                    if (snapshot.child("blocked").exists()) {
                        userBlocked = true;
                        binding.imageBlock.setColorFilter(getResources().getColor(R.color.colorTextGray));
                        binding.textBlock.setTextColor(getResources().getColor(R.color.colorTextGray));
                        binding.textBlock.setText(R.string.unBlock);
                    } else {
                        userBlocked = false;
                        binding.imageBlock.setColorFilter(getResources().getColor(R.color.colorCallReceived));
                        binding.textBlock.setTextColor(getResources().getColor(R.color.colorCallReceived));
                        binding.textBlock.setText(R.string.block);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), R.string.error_unexpected,
                            Toast.LENGTH_LONG).show();
                }
            });

            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(
                    UserProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
            binding.recyclerView.setLayoutManager(horizontalLayoutManager);
            binding.recyclerView.setAdapter(adapter);
            showCarousel();
        }
    }

    /* RESOLVE POSITIONS OF IMAGES */
    private void showCarousel() {
        chatService.readChatData(new OnReadChatCallBack() {
            @Override
            public void onReadSuccess(List<Chat> list) {
                adapter.setList(list);
            }

            @Override
            public void onReadFailed() {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initActionClick() {
        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imageProfile.invalidate();

                Drawable dr = binding.imageProfile.getDrawable();
                Common.IMAGE_BITMAP = ((GlideBitmapDrawable) dr.getCurrent()).getBitmap();

                ActivityOptionsCompat activityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                        UserProfileActivity.this, binding.imageProfile, "Imagem");
                Intent intent = new Intent(UserProfileActivity.this,
                        ViewProfileImageActivity.class);
                startActivity(intent, activityOptionsCompat.toBundle());
            }
        });

        /* CREATE GALLERY MEDIAS ACTIVITY */
        binding.layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Galeria Click", Toast.LENGTH_SHORT).show();
            }
        });

        binding.switchMuteNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, null, null, null,
                        binding.switchMuteNotify, null, null);
                boolean switchState = binding.switchMuteNotify.isChecked();
                if (switchState) {
                    showDialogMuteNotify();
                } else {
                    reference.child("muted").removeValue();
                    reference.child("mutedDate").removeValue();
                    reference.child("mutedTime").removeValue();
                    reference.child("mutedShowNotify").removeValue();
                }
            }
        });

        binding.personalityNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, null, null, null,
                        binding.personalityNotify, null, null);
                startActivity(new Intent(UserProfileActivity.this, NotifyActivity.class)
                        .putExtra("userID", receiverID));
            }
        });

        binding.mediaVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, null, null, null,
                        binding.mediaVisibility, null, null);
                showDialogMediaVisibility();
            }
        });

        binding.temporaryMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, binding.temporaryMessages, null, null,
                        null, null, null);
                startActivity(new Intent(UserProfileActivity.this, TemporaryMessageActivity.class)
                        .putExtra("userID", receiverID));
            }
        });

        /* REFACTOR QrCODE READER */
        binding.cryptoMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, binding.cryptoMessages, null, null,
                        null, null, null);
                startActivity(new Intent(UserProfileActivity.this, CryptographyActivity.class)
                        .putExtra("userID", receiverID)
                        .putExtra("userName", userName));
            }
        });

        binding.layoutPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "DEFAULT", "GRAY",
                        "WHITE", false, binding.layoutPhone, null, null,
                        null, null, null);
                Toast.makeText(UserProfileActivity.this,
                        getString(R.string.userProfile_phone_copied),
                        Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard =
                        (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.cellPhone), userNumber);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                return true;
            }
        });

        if (binding.layoutPhone.isLongClickable()) {
            binding.layoutPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                            "WHITE", false, binding.layoutPhone, null, null,
                            null, null, null);
                    finish();
                }
            });
        }

        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, null, null, binding.btnChat,
                        null, null, null);
                finish();
            }
        });

        /* CREATE VOICE CALL */
        binding.btnVoiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, null, null, binding.btnVoiceCall,
                        null, null, null);
                Toast.makeText(UserProfileActivity.this, "Chamada de Áudio Click",
                        Toast.LENGTH_SHORT).show();
            }
        });

        /* CREATE VIDEO CALL */
        binding.btnVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, null, null, binding.btnVideoCall,
                        null, null, null);
                Toast.makeText(UserProfileActivity.this, "Chamada de Vídeo Click",
                        Toast.LENGTH_SHORT).show();
            }
        });

        binding.userProfileBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, binding.userProfileBlock, null, null,
                        null, null, null);
                if (userBlocked) {
                    unBlockUser(userName);
                } else {
                    showDialogBlockUser();
                }
            }
        });

        binding.userProfileReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(getApplicationContext(), "SHORT", "GRAY",
                        "WHITE", false, binding.userProfileReport, null, null,
                        null, null, null);
                showDialogReportUser();
            }
        });
    }

    private void showDialogReportUser() {
        View reportView = View.inflate(this, R.layout.frame_checkbox_report, null);
        final CheckBox checkBox = reportView.findViewById(R.id.checkbox_report);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle(R.string.userProfile_report).setView(reportView);
        builder.setPositiveButton(R.string.reportCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                reportUser(checkBox);
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

    private void reportUser(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            referenceChat.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String receiver = dataSnapshot.child("receiver").getValue().toString();
                        String sender = dataSnapshot.child("sender").getValue().toString();
                        final String key = dataSnapshot.getKey();
                        assert key != null;

                        if (receiver.equals(firebaseUser.getUid()) && sender.equals(receiverID) ||
                                receiver.equals(receiverID) && sender.equals(firebaseUser.getUid())) {
                            referenceChat.child(key).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserProfileActivity.this, getString(R.string.error_unexpected),
                            Toast.LENGTH_LONG).show();
                }
            });

            reference.child("blocked").setValue("YES");
            reference.child("clean").setValue("YES");
        } else {
            reference.child("blocked").removeValue();
            reference.child("clean").removeValue();
        }
        startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
        Toast.makeText(getApplicationContext(), "Denuncia enviada", Toast.LENGTH_SHORT).show();
    }

    private void showDialogBlockUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setMessage(getString(R.string.userProfile_block, userName));
        builder.setPositiveButton(R.string.blockCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                blockUser(userName);
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

    private void blockUser(String userName) {
        reference.child("blocked").setValue("YES");
        Toast.makeText(getApplicationContext(), userName + " foi bloqueado",
                Toast.LENGTH_SHORT).show();
    }

    private void unBlockUser(String userName) {
        reference.child("blocked").removeValue();
        Toast.makeText(getApplicationContext(), userName + " foi desbloqueado",
                Toast.LENGTH_SHORT).show();
    }

    private void showDialogMediaVisibility() {
        View mediaView = View.inflate(this, R.layout.frame_radio_media, null);
        RadioButton radioBtnYes = mediaView.findViewById(R.id.radio_standard_yes);
        RadioButton radioBtnNo = mediaView.findViewById(R.id.radio_no);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        TextView customTitle = new TextView(getApplicationContext());
        customTitle.setText(R.string.userProfile_media_visibility);
        customTitle.setPadding(80, 40, 80, 40);
        customTitle.setTypeface(Typeface.DEFAULT_BOLD);
        customTitle.setTextSize(19);
        customTitle.setTextColor(getResources().getColor(R.color.colorBlack));
        builder.setCustomTitle(customTitle).setView(mediaView);
        if (radioCheck) {
            radioBtnYes.setChecked(true);
        } else {
            radioBtnNo.setChecked(true);
        }
        builder.setPositiveButton(R.string.okCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                downloadMediaRecent();
            }
        });

        builder.setNegativeButton(R.string.cancelCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                binding.switchMuteNotify.setChecked(false);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void downloadMediaRecent() {
        switch (radioMediaClick) {
            case 0:
            case 1:
                reference.child("mediaShow").removeValue();
                break;
            case 2:
                reference.child("mediaShow").setValue("NO");
                break;
        }
    }

    private void showDialogMuteNotify() {
        View muteView = View.inflate(this, R.layout.frame_checkbox_radio_mute, null);
        final CheckBox checkBox = muteView.findViewById(R.id.checkbox_mute);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle(R.string.userProfile_silence_notify).setView(muteView);
        builder.setPositiveButton(R.string.okCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                muteNotify(checkBox);
            }
        });

        builder.setNegativeButton(R.string.cancelCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                binding.switchMuteNotify.setChecked(false);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                binding.switchMuteNotify.setChecked(false);
            }
        });
    }

    private void muteNotify(CheckBox checkBox) {
        reference.child("muted").setValue("YES");
        if (checkBox.isChecked()) {
            reference.child("mutedShowNotify").setValue("YES");
        } else {
            reference.child("mutedShowNotify").setValue("NO");
        }
        switch (radioMuteClick) {
            case 0:
                reference.child("mutedDate").setValue(String.format("%s",
                        DataTimeService.getCurrentDate()));
                reference.child("mutedTime").setValue(String.format("%s",
                        DataTimeService.getNextHour(8)));
                break;
            case 1:
                reference.child("mutedDate").setValue(String.format("%s",
                        DataTimeService.getNextDate(7)));
                reference.child("mutedTime").setValue(String.format("%s",
                        DataTimeService.getCurrentTime()));
                break;
            case 2:
                reference.child("mutedDate").setValue("ALWAYS");
                reference.child("mutedTime").setValue("ALWAYS");
                break;
        }
        radioMuteClick = 2;
    }

    private void editContact() {
        Toast.makeText(getApplicationContext(), "Ação: Editar", Toast.LENGTH_SHORT).show();
        Intent editContact = new Intent();
        startActivity(editContact);
    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_share:
                Toast.makeText(getApplicationContext(), "Ação: Compartilhar", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_edit_contact:
                editContact();
                break;
            case R.id.action_view_contact_list:
                Toast.makeText(getApplicationContext(), "Ação: Ver na lista de contatos",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_security_code:
                Toast.makeText(getApplicationContext(), "Ação: Código de segurança",
                        Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRadioBtnMuteClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_eight_hours:
                if (checked) {
                    radioMuteClick = 0;
                }
                break;
            case R.id.radio_one_week:
                if (checked) {
                    radioMuteClick = 1;
                }
                break;
            case R.id.radio_always:
                if (checked) {
                    radioMuteClick = 2;
                }
                break;
        }
    }

    public void onRadioBtnMediaClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_standard_yes:
                if (checked) {
                    radioMediaClick = 0;
                }
                break;
            case R.id.radio_yes:
                if (checked) {
                    radioMediaClick = 1;
                }
                break;
            case R.id.radio_no:
                if (checked) {
                    radioMediaClick = 2;
                }
                break;
        }
    }
}