package com.gabrieltorelo.whatsappclone.view.activities.profile.actions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.CursorLoader;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityNotifyBinding;
import com.gabrieltorelo.whatsappclone.view.activities.profile.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.ViewListener;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;

public class NotifyActivity extends AppCompatActivity {

    private ActivityNotifyBinding binding;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private String receiverID, vibration, pop_up, light, ringTone,
            vibrationMessage, vibrationCall, soundPath, ringingPath,
            sNone, rNone;
    private View vibrationView;
    private Uri currentTone;
    private Ringtone defaultCallRingtone, defaultMessageRingtone;
    public boolean checkedEnable, available;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notify);

        initialize();
        initActionClick();
        initToolbar();
    }

    private void initialize() {
        Intent intent = getIntent();
        receiverID = intent.getStringExtra("userID");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid()).child(receiverID);
        defaultCallRingtone = RingtoneManager.getRingtone(getApplicationContext(),
                Settings.System.DEFAULT_RINGTONE_URI);
        defaultMessageRingtone = RingtoneManager.getRingtone(getApplicationContext(),
                Settings.System.DEFAULT_NOTIFICATION_URI);
        rNone = "NotNULL"; sNone = "NotNULL";

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                /* GET CUSTOM POP UP*/
                if (Build.VERSION.SDK_INT < 29) {
                    if (snapshot.child("customPopUp").exists()) {
                        String customPopUp = snapshot.child("customPopUp").getValue().toString();
                        switch (customPopUp) {
                            case "ON":
                                binding.tvPopUpSelected.setText(R.string.popUp_on);
                                pop_up = "ON";
                                break;
                            case "OFF":
                                binding.tvPopUpSelected.setText(R.string.popUp_off);
                                pop_up = "OFF";
                                break;
                            case "EVER":
                                binding.tvPopUpSelected.setText(R.string.popUp_ever);
                                pop_up = "EVER";
                                break;
                        }
                    } else {
                        binding.tvPopUpSelected.setText(R.string.popUp_no);
                        pop_up = "NO";
                    }
                    available = true;
                    binding.tvPopUpSelected.setEnabled(true);
                }
                else {
                    available = false;
                    binding.tvPopUpSelected.setEnabled(false);
                }

                /* CUSTOM TAG */
                if (snapshot.child("customNotify").exists()) {
                    binding.checkboxEnablePersonality.setChecked(true);
                    checkedEnable = getCustoms(true);
                }
                else{
                    binding.checkboxEnablePersonality.setChecked(false);
                    checkedEnable = getCustoms(false);
                }

                /* GET CUSTOM RINGING MESSAGES */
                if (snapshot.child("customSound").exists() && snapshot.child("customSoundUrl").exists()) {
                    String customSound = snapshot.child("customSound").getValue().toString();
                    String customSoundUrl = snapshot.child("customSoundUrl").getValue().toString();
                    switch (customSound){
                        case "NONE" :
                            binding.tvSoundSelected.setText(R.string.silence);
                            soundPath = null;
                            sNone = null;
                            break;
                        default:
                            if (!customSoundUrl.contains("content://media") && sNone != null) {
                                File sound = new File(customSoundUrl);
                                if (sound.exists()) {
                                    binding.tvSoundSelected.setText(customSound);
                                    soundPath = customSoundUrl;
                                } else {
                                    reference.child("customSound").removeValue();
                                    reference.child("customSoundUrl").removeValue();
                                }
                            }
                            else {
                                binding.tvSoundSelected.setText(customSound);
                                soundPath = customSoundUrl;
                                sNone = "NotNULL";
                            }
                            break;
                    }
                }
                else {
                    binding.tvSoundSelected.setText(defaultMessageRingtone.getTitle(getApplicationContext()));
                    soundPath = "content://settings/system/ringtone";
                }

                /* GET CUSTOM VIBRATE MESSAGES*/
                if (snapshot.child("customMsgVibrate").exists()) {
                    String customMsgVibrate = snapshot.child("customMsgVibrate").getValue().toString();
                    switch (customMsgVibrate){
                        case "DEFAULT" :
                                binding.tvMessageVibrationSelected.setText(R.string.standard);
                                vibrationMessage = "DEFAULT";
                            break;
                        case "SHORT" :
                                binding.tvMessageVibrationSelected.setText(R.string.short_);
                                vibrationMessage = "SHORT";
                            break;
                        case "LONG" :
                                binding.tvMessageVibrationSelected.setText(R.string.long_);
                                vibrationMessage = "LONG";
                            break;
                    }
                }
                else {
                    binding.tvMessageVibrationSelected.setText(R.string.disable);
                    vibrationMessage = "OFF";
                }

                /* GET CUSTOM LIGHT */
                if (snapshot.child("customLight").exists()) {
                    String customLight = snapshot.child("customLight").getValue().toString();
                    switch (customLight){
                        case "WHITE" :
                            binding.tvLightSeleted.setText(R.string.light_white);
                            light = "WHITE";
                            break;
                        case "RED" :
                            binding.tvLightSeleted.setText(R.string.light_red);
                            light = "RED";
                            break;
                        case "YELLOW" :
                            binding.tvLightSeleted.setText(R.string.light_yellow);
                            light = "YELLOW";
                            break;
                        case "GREEN" :
                            binding.tvLightSeleted.setText(R.string.green);
                            light = "GREEN";
                            break;
                        case "CYAN" :
                            binding.tvLightSeleted.setText(R.string.cyan);
                            light = "CYAN";
                            break;
                        case "BLUE" :
                            binding.tvLightSeleted.setText(R.string.blue);
                            light = "BLUE";
                            break;
                        case "PURPLE" :
                            binding.tvLightSeleted.setText(R.string.light_purple);
                            light = "PURPLE";
                            break;
                    }
                }
                else {
                    binding.tvLightSeleted.setText(R.string.light_null);
                    light = "NONE";
                }

                /* GET CUSTOM PRIORITY */
                if (snapshot.child("customPriority").exists()) {
                    binding.checkboxPriority.setChecked(true);
                }
                else{
                    binding.checkboxPriority.setChecked(false);
                }

                /* GET CUSTOM RINGING CALL */
                if (snapshot.child("customRingtone").exists() && snapshot.child("customRingtoneUrl").exists()) {
                    String customRingtone = snapshot.child("customRingtone").getValue().toString();
                    String customRingtoneUrl = snapshot.child("customRingtoneUrl").getValue().toString();
                    switch (customRingtone){
                        case "NONE" :
                            binding.tvRingingSelected.setText(R.string.silence);
                            ringingPath = null;
                            rNone = null;
                            break;
                        default:
                            if (!customRingtoneUrl.contains("content://media") && rNone != null) {
                                File sound = new File(customRingtoneUrl);
                                if (sound.exists()) {
                                    binding.tvRingingSelected.setText(customRingtone);
                                    ringingPath = customRingtoneUrl;
                                } else {
                                    reference.child("customRingtone").removeValue();
                                    reference.child("customRingtoneUrl").removeValue();
                                }
                            }
                            else {
                                binding.tvRingingSelected.setText(customRingtone);
                                ringingPath = customRingtoneUrl;
                                rNone = "NotNULL";
                            }
                            break;
                    }
                }
                else {
                    binding.tvRingingSelected.setText(defaultCallRingtone.getTitle(getApplicationContext()));
                    ringingPath = "content://settings/system/ringtone";
                }

                /* GET CUSTOM VIBRATE CALL*/
                if (snapshot.child("customCallVibrate").exists()) {
                    String customCallVibrate = snapshot.child("customCallVibrate").getValue().toString();
                    switch (customCallVibrate){
                        case "DEFAULT" :
                            binding.tvCallVibrationSelected.setText(R.string.standard);
                            vibrationCall = "DEFAULT";
                            break;
                        case "SHORT" :
                            binding.tvCallVibrationSelected.setText(R.string.short_);
                            vibrationCall = "SHORT";
                            break;
                        case "LONG" :
                            binding.tvCallVibrationSelected.setText(R.string.long_);
                            vibrationCall = "LONG";
                            break;
                    }
                }
                else {
                    binding.tvCallVibrationSelected.setText(R.string.disable);
                    vibrationCall = "OFF";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), R.string.error_unexpected, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initActionClick() {
        binding.layoutEnablePersonality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableCustom();
            }
        });

        binding.checkboxEnablePersonality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableCustom();
            }
        });
    }

    private void resetConfig(){
        reference.child("customNotify").removeValue();
        reference.child("customSound").removeValue();
        reference.child("customSoundUrl").removeValue();
        reference.child("customMsgVibrate").removeValue();
        reference.child("customPopUp").removeValue();
        reference.child("customLight").removeValue();
        reference.child("customPriority").removeValue();
        reference.child("customRingtone").removeValue();
        reference.child("customRingtoneUrl").removeValue();
        reference.child("customCallVibrate").removeValue();

    }

    private boolean getCustoms(boolean checked){
        if (checked){
            binding.tvSound.setEnabled(true);
            binding.layoutSound.setEnabled(true);
            binding.tvSound.setTextColor(getResources().getColor(R.color.colorBlack));
            binding.tvMessageVibration.setEnabled(true);
            binding.layoutVibrationMessage.setEnabled(true);
            binding.tvMessageVibration.setTextColor(getResources().getColor(R.color.colorBlack));
            binding.layoutPopUp.setEnabled(true);
            if (available){
                binding.tvPopUp.setEnabled(true);
                binding.tvPopUp.setTextColor(getResources().getColor(R.color.colorBlack));
            }
            binding.tvLight.setEnabled(true);
            binding.layoutLight.setEnabled(true);
            binding.tvLight.setTextColor(getResources().getColor(R.color.colorBlack));
            binding.tvPriority.setEnabled(true);
            binding.layoutPriority.setEnabled(true);
            binding.tvPriority.setTextColor(getResources().getColor(R.color.colorBlack));
            binding.checkboxPriority.setEnabled(true);
            binding.checkboxPriority.setEnabled(true);
            binding.checkboxPriority.setTextColor(getResources().getColor(R.color.colorBlack));
            binding.tvRinging.setEnabled(true);
            binding.layoutRinging.setEnabled(true);
            binding.tvRinging.setTextColor(getResources().getColor(R.color.colorBlack));
            binding.tvCallVibration.setEnabled(true);
            binding.layoutVibrationCall.setEnabled(true);
            binding.tvCallVibration.setTextColor(getResources().getColor(R.color.colorBlack));

            binding.layoutSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogRingtone("MESSAGE");
                }
            });

            binding.layoutVibrationMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogVibration("MESSAGE");
                }
            });

            binding.layoutPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogPop_up(available);
                }
            });

            binding.layoutLight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogLight();
                }
            });

            binding.layoutPriority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enablePriority("LAYOUT");
                }
            });

            binding.checkboxPriority.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enablePriority("CHECKBOX");
                }
            });

            binding.layoutRinging.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogRingtone("CALL");
                }
            });

            binding.layoutVibrationCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogVibration("CALL");
                }
            });
        }
        else {
            binding.tvSound.setEnabled(false);
            binding.layoutSound.setEnabled(false);
            binding.tvSound.setTextColor(getResources().getColor(R.color.colorTextDisable));
            binding.tvMessageVibration.setEnabled(false);
            binding.layoutVibrationMessage.setEnabled(false);
            binding.tvMessageVibration.setTextColor(getResources().getColor(R.color.colorTextDisable));
            binding.layoutPopUp.setEnabled(false);
            if (available){
                binding.tvPopUp.setEnabled(false);
                binding.tvPopUp.setTextColor(getResources().getColor(R.color.colorTextDisable));
            }
            binding.tvLight.setEnabled(false);
            binding.layoutLight.setEnabled(false);
            binding.tvLight.setTextColor(getResources().getColor(R.color.colorTextDisable));
            binding.tvPriority.setEnabled(false);
            binding.layoutPriority.setEnabled(false);
            binding.tvPriority.setTextColor(getResources().getColor(R.color.colorTextDisable));
            binding.checkboxPriority.setEnabled(false);
            binding.checkboxPriority.setEnabled(false);
            binding.checkboxPriority.setTextColor(getResources().getColor(R.color.colorTextDisable));
            binding.tvRinging.setEnabled(false);
            binding.layoutRinging.setEnabled(false);
            binding.tvRinging.setTextColor(getResources().getColor(R.color.colorTextDisable));
            binding.tvCallVibration.setEnabled(false);
            binding.layoutVibrationCall.setEnabled(false);
            binding.tvCallVibration.setTextColor(getResources().getColor(R.color.colorTextDisable));
        }
        return checked;
    }

    private void showDialogLight(){
        View lightView = View.inflate(this, R.layout.frame_radio_light, null);
        RadioGroup radioGroupLight = lightView.findViewById(R.id.radio_group_light);
        RadioButton radioBtnDisableLight = lightView.findViewById(R.id.radio_disable_light);
        RadioButton radioBtnWhiteLight = lightView.findViewById(R.id.radio_white_light);
        RadioButton radioBtnRedLight = lightView.findViewById(R.id.radio_red_light);
        RadioButton radioBtnYellowLight = lightView.findViewById(R.id.radio_yellow_light);
        RadioButton radioBtnGreenLight = lightView.findViewById(R.id.radio_green_light);
        RadioButton radioBtnCyanLight = lightView.findViewById(R.id.radio_cyan_light);
        RadioButton radioBtnBlueLight = lightView.findViewById(R.id.radio_blue_light);
        RadioButton radioBtnPurpleLight = lightView.findViewById(R.id.radio_purple_light);
        AlertDialog.Builder builder = new AlertDialog.Builder(NotifyActivity.this);
        builder.setTitle(R.string.light).setView(lightView);
        switch (light){
            case "WHITE" :
                radioBtnWhiteLight.setChecked(true);
                break;
            case "RED" :
                radioBtnRedLight.setChecked(true);
                break;
            case "YELLOW" :
                radioBtnYellowLight.setChecked(true);
                break;
            case "GREEN" :
                radioBtnGreenLight.setChecked(true);
                break;
            case "CYAN" :
                radioBtnCyanLight.setChecked(true);
                break;
            case "BLUE" :
                radioBtnBlueLight.setChecked(true);
                break;
            case "PURPLE" :
                radioBtnPurpleLight.setChecked(true);
                break;
            default:
                radioBtnDisableLight.setChecked(true);
                break;
        }

        builder.setNegativeButton(R.string.cancelCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        radioGroupLight.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                alertDialog.dismiss();
            }
        });
    }

    private void showDialogPop_up(boolean available){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotifyActivity.this);
        if (available) {
            View popUpView = View.inflate(this, R.layout.frame_radio_pop_up, null);
            RadioGroup radioGroupPopUp = popUpView.findViewById(R.id.radio_group_pop_up);
            RadioButton radioBtnDisablePopUp = popUpView.findViewById(R.id.radio_disable_pop_up);
            RadioButton radioBtnOnPopUp = popUpView.findViewById(R.id.radio_on_pop_up);
            RadioButton radioBtnOffPopUp = popUpView.findViewById(R.id.radio_off_pop_up);
            RadioButton radioBtnEverPopUp = popUpView.findViewById(R.id.radio_ever_pop_up);
            builder.setTitle(R.string.notify_pop_up).setView(popUpView);
            switch (pop_up){
                case "ON" :
                    radioBtnOnPopUp.setChecked(true);
                    break;
                case "OFF" :
                    radioBtnOffPopUp.setChecked(true);
                    break;
                case "EVER" :
                    radioBtnEverPopUp.setChecked(true);
                    break;
                default:
                    radioBtnDisablePopUp.setChecked(true);
                    break;
            }

            builder.setNegativeButton(R.string.cancelCaps, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();

            radioGroupPopUp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    alertDialog.dismiss();
                }
            });
        }
        else {
            builder.setMessage(R.string.notify_no_pop_up);
            builder.setNeutralButton(R.string.LearnMoreCaps, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.parse("https://duckduckgo.com/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

            builder.setNegativeButton(R.string.okCaps, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void showDialogVibration(String type){
        switch (type){
            case "MESSAGE" :
                vibrationView = View.inflate(this, R.layout.frame_radio_vibration_message, null);
                vibration = vibrationMessage;
                break;
            case "CALL" :
                vibrationView = View.inflate(this, R.layout.frame_radio_vibration_call, null);
                vibration = vibrationCall;
                break;
        }
        RadioGroup radioGroup = vibrationView.findViewById(R.id.radio_group);
        RadioButton radioBtnDisable = vibrationView.findViewById(R.id.radio_disable);
        RadioButton radioBtnStandard = vibrationView.findViewById(R.id.radio_standard);
        RadioButton radioBtnShort = vibrationView.findViewById(R.id.radio_short);
        RadioButton radioBtnLong = vibrationView.findViewById(R.id.radio_long);
        AlertDialog.Builder builder = new AlertDialog.Builder(NotifyActivity.this);
        builder.setTitle(R.string.vibration).setView(vibrationView);
        switch (vibration){
            case "DEFAULT" :
                radioBtnStandard.setChecked(true);
                break;
            case "SHORT" :
                radioBtnShort.setChecked(true);
                break;
            case "LONG" :
                radioBtnLong.setChecked(true);
                break;
            default:
                radioBtnDisable.setChecked(true);
                break;
        }

        builder.setNegativeButton(R.string.cancelCaps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                alertDialog.dismiss();
            }
        });
    }

    private void showDialogRingtone(String type){
        int CODE = 123;
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        switch (type){
            case "MESSAGE" :
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.notify_sound));
                ringTone = soundPath;
                CODE = 999;
                break;
            case "CALL" :
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.ringing));
                ringTone = ringingPath;
                CODE = 888;
                break;
        }

        if (ringTone != null) {
            currentTone = Uri.parse(ringTone);
        }
        else {
            currentTone = null;
        }

        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.notify_choose_action)), CODE);
    }

    private void enableCustom(){
        if (checkedEnable){
            reference.child("customNotify").removeValue();
        }
        else {
            reference.child("customNotify").setValue("YES");
        }
        getCustoms(checkedEnable);
    }

    private void enablePriority(String local){
        if (local.equals("LAYOUT")) {
            if (!binding.checkboxPriority.isChecked()) {
                reference.child("customPriority").setValue("YES");
            } else {
                reference.child("customPriority").removeValue();
            }
        }
        else {
            if (binding.checkboxPriority.isChecked()) {
                reference.child("customPriority").setValue("YES");
            } else {
                reference.child("customPriority").removeValue();
            }
        }
    }

    private void initToolbar() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_notify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            case android.R.id.home : finish(); break;
            case R.id.action_reset : resetConfig(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == RESULT_OK) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                String sound = String.format("%s", uri.getQueryParameter("title"));
                Uri soundExternalPath = data.getData();
                soundPath = "content://media" + uri.getPath();
                if (soundExternalPath != null){
                    String[] soundExternal = { MediaStore.Audio.Media.DATA, MediaStore.Audio.AudioColumns.DISPLAY_NAME };
                    @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(soundExternalPath, soundExternal, null, null, null);
                    assert cursor != null;
                    int url_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    int title_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    cursor.moveToFirst();
                    String url = cursor.getString(url_index);
                    String title = cursor.getString(title_index);

                    reference.child("customSound").setValue(title);
                    reference.child("customSoundUrl").setValue(url);
                }
                else if (sound.equals("null")) {
                    reference.child("customSound").removeValue();
                    reference.child("customSoundUrl").removeValue();
                } else {
                    reference.child("customSound").setValue(sound);
                    reference.child("customSoundUrl").setValue(soundPath);
                }
            }
            else {
                reference.child("customSound").setValue("NONE");
                reference.child("customSoundUrl").setValue("NONE");
            }
        }
        else if (requestCode == 888 && resultCode == RESULT_OK){
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                String ringtone = String.format("%s", uri.getQueryParameter("title"));
                Uri ringingExternalPath = data.getData();
                ringingPath = "content://media" + uri.getPath();
                if (ringingExternalPath != null){
                    String[] ringingExternal = { MediaStore.Audio.Media.DATA, MediaStore.Audio.AudioColumns.DISPLAY_NAME };
                    @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(ringingExternalPath, ringingExternal, null, null, null);
                    assert cursor != null;
                    int url_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    int title_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    cursor.moveToFirst();
                    String url = cursor.getString(url_index);
                    String title = cursor.getString(title_index);

                    reference.child("customRingtone").setValue(title);
                    reference.child("customRingtoneUrl").setValue(url);
                }
                else if (ringtone.equals("null")) {
                    reference.child("customRingtone").removeValue();
                    reference.child("customRingtoneUrl").removeValue();
                } else {
                    reference.child("customRingtone").setValue(ringtone);
                    reference.child("customRingtoneUrl").setValue(ringingPath);
                }
            }
            else {
                reference.child("customRingtone").setValue("NONE");
                reference.child("customRingtoneUrl").setValue("NONE");
            }
        }
    }

    public void onRadioBtnVibrationMessageClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_disable:
                if (checked) {
                    reference.child("customMsgVibrate").removeValue();
                }
                break;
            case R.id.radio_standard:
                if (checked) {
                    reference.child("customMsgVibrate").setValue("DEFAULT");
                }
                break;
            case R.id.radio_short:
                if (checked) {
                    reference.child("customMsgVibrate").setValue("SHORT");
                }
                break;
            case R.id.radio_long:
                if (checked) {
                    reference.child("customMsgVibrate").setValue("LONG");
                }
                break;
        }
    }

    public void onRadioBtnVibrationCallClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_disable:
                if (checked) {
                    reference.child("customCallVibrate").removeValue();
                }
                break;
            case R.id.radio_standard:
                if (checked) {
                    reference.child("customCallVibrate").setValue("DEFAULT");
                }
                break;
            case R.id.radio_short:
                if (checked) {
                    reference.child("customCallVibrate").setValue("SHORT");
                }
                break;
            case R.id.radio_long:
                if (checked) {
                    reference.child("customCallVibrate").setValue("LONG");
                }
                break;
        }
    }

    public void onRadioBtnPopUpClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_disable_pop_up:
                if (checked) {
                    reference.child("customPopUp").removeValue();
                }
                break;
            case R.id.radio_on_pop_up:
                if (checked) {
                    reference.child("customPopUp").setValue("ON");
                }
                break;
            case R.id.radio_off_pop_up:
                if (checked) {
                    reference.child("customPopUp").setValue("OFF");
                }
                break;
            case R.id.radio_ever_pop_up:
                if (checked) {
                    reference.child("customPopUp").setValue("EVER");
                }
                break;
        }
    }

    public void onRadioBtnLightClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radio_disable_light:
                if (checked) {
                    reference.child("customLight").removeValue();
                }
                break;
            case R.id.radio_white_light:
                if (checked) {
                    reference.child("customLight").setValue("WHITE");
                }
                break;
            case R.id.radio_red_light:
                if (checked) {
                    reference.child("customLight").setValue("RED");
                }
                break;
            case R.id.radio_yellow_light:
                if (checked) {
                    reference.child("customLight").setValue("YELLOW");
                }
                break;
            case R.id.radio_green_light:
                if (checked) {
                    reference.child("customLight").setValue("GREEN");
                }
                break;
            case R.id.radio_cyan_light:
                if (checked) {
                    reference.child("customLight").setValue("CYAN");
                }
                break;
            case R.id.radio_blue_light:
                if (checked) {
                    reference.child("customLight").setValue("BLUE");
                }
                break;
            case R.id.radio_purple_light:
                if (checked) {
                    reference.child("customLight").setValue("PURPLE");
                }
                break;
        }
    }
}