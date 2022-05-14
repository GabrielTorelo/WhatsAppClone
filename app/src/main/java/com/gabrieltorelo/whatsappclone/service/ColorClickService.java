package com.gabrieltorelo.whatsappclone.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.gabrieltorelo.whatsappclone.R;

public class ColorClickService {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public static void setColorClicked(final Context context, String duration, String coverColor, String backgroundColor,
                                       boolean radius, final LinearLayout linearLayout, final ImageView imageView,
                                       final CardView cardView, final  TextView textView, final Switch switchView,
                                       final View itemView){
        String type = "";
        int bgColor = 0;
        int color = 0;
        Handler h = new Handler();

        switch (coverColor){
            case "GRAY" :
                color = R.color.colorClickGray;
                break;
            case "PRIMARY_DARK" :
                color = R.color.colorPrimaryDark;
                break;
        }

        if (linearLayout != null){
            if (radius){
                linearLayout.setBackground(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.bg_linearlayout_radius, null));
                Drawable lin = linearLayout.getBackground();
                lin.setColorFilter(context.getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            }
            else {
                linearLayout.setBackgroundColor(context.getResources().getColor(color));
            }
            type = "lLayout";
        }
        else if (imageView != null){
            if (radius){
                imageView.setBackground(ResourcesCompat.getDrawable(context.getResources(),
                        R.drawable.bg_linearlayout_radius, null));
                Drawable img = imageView.getBackground();
                img.setColorFilter(context.getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            }
            else {
                imageView.setBackgroundColor(context.getResources().getColor(color));
            }
            type = "imgView";
        }
        else if (cardView != null){
            cardView.setBackgroundColor(context.getResources().getColor(color));
            type = "cView";
        }
        else if (textView != null){
            textView.setBackgroundColor(context.getResources().getColor(color));
            type = "tView";
        }
        else if (switchView != null){
            switchView.setBackgroundColor(context.getResources().getColor(color));
            type = "sView";
        }
        else if (itemView != null){
            itemView.setBackgroundColor(context.getResources().getColor(color));
            type = "iView";
        }

        switch (backgroundColor){
            case "PRIMARY" :
                bgColor = R.color.colorPrimary;
                break;
            case "WHITE" :
                bgColor = R.color.colorBackGround;
                break;
        }

        final String finalType = type;
        final int finalColor = bgColor;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                switch (finalType){
                    case "lLayout" :
                        linearLayout.setBackgroundColor(context.getResources().getColor(finalColor));
                        break;
                    case "imgView" :
                        imageView.setBackgroundColor(context.getResources().getColor(finalColor));
                        break;
                    case "cView" :
                        cardView.setBackgroundColor(context.getResources().getColor(finalColor));
                        break;
                    case "tView" :
                        textView.setBackgroundColor(context.getResources().getColor(finalColor));
                        break;
                    case "sView" :
                        switchView.setBackgroundColor(context.getResources().getColor(finalColor));
                        break;
                    case "iView" :
                        itemView.setBackgroundColor(context.getResources().getColor(finalColor));
                        break;
                }
            }
        };

        switch (duration){
            case "SHORT" :
                h.postDelayed(r, 200);
                break;
            case "LONG" :
                h.postDelayed(r, 1000);
                break;
            case "DEFAULT" :
                h.postDelayed(r, 500);
                break;
        }
    }
}
