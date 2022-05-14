package com.gabrieltorelo.whatsappclone.view.activities.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.common.Common;
import com.gabrieltorelo.whatsappclone.model.ChatList;
import com.gabrieltorelo.whatsappclone.view.activities.chat.ChatActivity;
import com.gabrieltorelo.whatsappclone.view.activities.display.ViewProfileImageActivity;
import com.gabrieltorelo.whatsappclone.view.activities.profile.UserProfileActivity;

public class DialogViewUser {
    private Context context;

    public DialogViewUser(Context context, ChatList chatList){
        this.context = context;
        initialize(chatList);
    }

    private void initialize(final ChatList chatList) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.activity_dialog_view_user);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;

        dialog.getWindow().setAttributes(lp);

        final ImageButton btnChat, btnCall, btnVideoCall, btnInfo;
        final ImageView imageProfile;
        TextView userName;

        btnChat = dialog.findViewById(R.id.btn_chat);
        btnCall = dialog.findViewById(R.id.btn_call);
        btnVideoCall = dialog.findViewById(R.id.btn_video_call);
        btnInfo = dialog.findViewById(R.id.btn_info);
        imageProfile = dialog.findViewById(R.id.image_profile);
        userName = dialog.findViewById(R.id.tv_user_name);

        userName.setText(chatList.getUserName());
        Glide.with(context).load(chatList.getUrlProfile()).into(imageProfile);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userID", chatList.getUserID())
                        .putExtra("userName", chatList.getUserName())
                        .putExtra("userName", chatList.getUserName())
                        .putExtra("imageProfile", chatList.getUrlProfile()));
                dialog.dismiss();
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Call Click", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Video Call Click", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserProfileActivity.class)
                        .putExtra("userID", chatList.getUserID())
                        .putExtra("imageProfile", chatList.getUrlProfile())
                        .putExtra("userName", chatList.getUserName()));
                dialog.dismiss();
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageProfile.invalidate();

                Drawable dr = imageProfile.getDrawable();
                Common.IMAGE_BITMAP = ((GlideBitmapDrawable) dr.getCurrent()).getBitmap();

                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity) context, imageProfile, "Imagem");
                Intent intent = new Intent(context, ViewProfileImageActivity.class);

                context.startActivity(intent, activityOptionsCompat.toBundle());
            }
        });

        dialog.show();
    }
}
