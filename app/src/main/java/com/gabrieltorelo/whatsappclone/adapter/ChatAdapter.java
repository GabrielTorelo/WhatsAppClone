package com.gabrieltorelo.whatsappclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.model.chat.Chat;
import com.gabrieltorelo.whatsappclone.service.AudioService;
import com.gabrieltorelo.whatsappclone.service.DataTimeService;
import com.gabrieltorelo.whatsappclone.view.activities.display.ViewImageActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chat> list;
    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private ValueEventListener seenListener;
    private ImageView tmpImagePlayChat;
    private AudioService audioService;
    private boolean viewMessage;
    public int pos = 0;
    private int audioTime, progressBarValue = 0;
    private long longAudioTime = 0;
    private Handler handler = new Handler();

    public ChatAdapter(List<Chat> list, Context context) {
        this.list = list;
        this.context = context;
        this.audioService = new AudioService(context);
    }

    public void setList(List<Chat> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        reference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        if (viewType == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

//    private void seenMessage(final String userID){
//        reference = FirebaseDatabase.getInstance().getReference("Chat");
//        seenListener = reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getReceiver().equals(firebaseUser.getUid())
//                            && chat.getSender().equals(userID)){
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("visualize", "YES");
//                        snapshot.getRef().updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage, textImageMessage,
                textDateMessage, textDateImage, textDateVoice,
                dateMessages;
        private Chronometer textDuration;
        private ProgressBar progressHorizontal;
        private ImageView imageMessage, imageProfile, imagePlayChat,
                imageMic, imageVisualizeMessage, imageVisualizeImage,
                imageFavorite, imageGradient;
        private RelativeLayout layoutImage;
        private LinearLayout layoutText, layoutVoice,
                layoutDateMessages, layoutImageDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.tv_text_message);
            textDateMessage = itemView.findViewById(R.id.tv_date_msg);
            textDateImage = itemView.findViewById(R.id.tv_date_image);
            textDateVoice = itemView.findViewById(R.id.tv_date_voice);
            textImageMessage = itemView.findViewById(R.id.tv_text_image_message);
            textDuration = itemView.findViewById(R.id.tv_duration);
            progressHorizontal = itemView.findViewById(R.id.progress_line);
            dateMessages = itemView.findViewById(R.id.tv_date_message);
            layoutText = itemView.findViewById(R.id.layout_text);
            layoutImage = itemView.findViewById(R.id.layout_image);
            layoutVoice = itemView.findViewById(R.id.layout_voice);
            layoutDateMessages = itemView.findViewById(R.id.layout_date_messages);
            layoutImageDesc = itemView.findViewById(R.id.layout_image_desc);
            imageMessage = itemView.findViewById(R.id.image_chat);
            imageProfile = itemView.findViewById(R.id.image_voice_profile);
            imagePlayChat = itemView.findViewById(R.id.tv_play_chat);
            imageMic = itemView.findViewById(R.id.tv_image_mic);
            imageVisualizeMessage = itemView.findViewById(R.id.tv_visualize_message);
            imageVisualizeImage = itemView.findViewById(R.id.tv_visualize_image);
            imageFavorite = itemView.findViewById(R.id.image_favorite);
            imageGradient = itemView.findViewById(R.id.image_gradient);

              /* ALTERAR PARA O LAYOUT VIEWIMAGECHAT */
            //            imageMessage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    imageMessage.invalidate();
//
//                    Drawable dr = imageMessage.getDrawable();
//                    Common.IMAGE_BITMAP = ((GlideBitmapDrawable) dr.getCurrent()).getBitmap();
//
//                    ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            (Activity) context, imageMessage, "Imagem");
//                    Intent intent = new Intent(context, ViewImageActivity.class);
//                    context.startActivity(intent, activityOptionsCompat.toBundle());
//                }
//            });
        }

        void bind(final Chat chat){
            if (chat.getDate().equals(DataTimeService.getCurrentDate())){
                dateMessages.setText("HOJE");
            }
            else if (chat.getDate().equals(DataTimeService.getBeforeDate())){
                dateMessages.setText("ONTEM");
            }
            else{
                dateMessages.setText(chat.getDate());
            }

            switch (chat.getType()){
                case "TEXT" :
                    layoutImage.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);

                    if (chat.getSender().equals(firebaseUser.getUid())){
                        viewMessage = true;
                    }
                    else {
                        viewMessage = false;
                    }

                    if (chat.getReceiverRemoved().equals("NO") || viewMessage) {
                        layoutText.setVisibility(View.VISIBLE);
                        layoutDateMessages.setVisibility(View.VISIBLE);

                        textMessage.setText(chat.getTextMessage());
                        textDateMessage.setText(chat.getTime());

                        if (firebaseUser.getUid().equals(chat.getSender()) && chat.getVisualize().equals("YES")) {
                            imageVisualizeMessage.setColorFilter(0xFF25B1F8);
                        }
                    }
                    else {
                        layoutText.setVisibility(View.GONE);
                        layoutDateMessages.setVisibility(View.GONE);
                    }

                    break;

                case "IMAGE" :
                    layoutText.setVisibility(View.GONE);
                    layoutVoice.setVisibility(View.GONE);

                    if (chat.getSender().equals(firebaseUser.getUid())){
                        viewMessage = true;
                    }
                    else {
                        viewMessage = false;
                    }

                    if (chat.getReceiverRemoved().equals("NO") || viewMessage){
                        layoutDateMessages.setVisibility(View.VISIBLE);
                        layoutImage.setVisibility(View.VISIBLE);
                        Glide.with(context).load(chat.getUrl()).into(imageMessage);
                        textDateImage.setText(chat.getTime());
                        textImageMessage.setText(chat.getTextMessage());
                        if(chat.getTextMessage().equals("")){
                            textImageMessage.setVisibility(View.GONE);
                            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250,
                                    context.getResources().getDisplayMetrics());
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
                            params.addRule(RelativeLayout.BELOW, 0);
                            layoutImageDesc.setLayoutParams(params);
                            imageFavorite.setColorFilter(Color.WHITE);
                            textDateImage.setTextColor(Color.WHITE);
                            if (chat.getSender().equals(firebaseUser.getUid())) {
                                imageVisualizeImage.setColorFilter(Color.WHITE);
                            }
                            imageGradient.setVisibility(View.VISIBLE);
                            layoutImageDesc.setGravity(Gravity.BOTTOM);
                        }
                        else {
                            textImageMessage.setVisibility(View.VISIBLE);
                            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250,
                                    context.getResources().getDisplayMetrics());
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.addRule(RelativeLayout.BELOW, R.id.card_image_chat);
                            layoutImageDesc.setLayoutParams(params);
                            imageFavorite.setColorFilter(context.getResources().getColor(R.color.colorTextLightGray));
                            textDateImage.setTextColor(context.getResources().getColor(R.color.colorTextLightGray));
                            if (chat.getSender().equals(firebaseUser.getUid())) {
                                imageVisualizeImage.setColorFilter(context.getResources().getColor(R.color.colorTextLightGray));
                            }
                            imageGradient.setVisibility(View.GONE);
                            layoutImageDesc.setGravity(0);
                        }
                        if (firebaseUser.getUid().equals(chat.getSender()) && chat.getVisualize().equals("YES")){
                            imageVisualizeImage.setColorFilter(0xFF25B1F8);
                        }
                        reference.child("Chat").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String id = dataSnapshot.child("id").getValue().toString();
                                    final String key = dataSnapshot.getKey();

                                    if (id.equals(chat.getId())){
                                        imageMessage.setRotation(Float.parseFloat(chat.getRotation()));
                                        imageMessage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                context.startActivity(new Intent(context, ViewImageActivity.class)
                                                        .putExtra("chatID", key)
                                                        .putExtra("userSenderID", chat.getSender())
                                                        .putExtra("favorite", chat.getFavorite())
                                                        .putExtra("date", chat.getDate())
                                                        .putExtra("time", chat.getTime())
                                                        .putExtra("imageView", chat.getUrl())
                                                        .putExtra("position", getAdapterPosition())
                                                        .putExtra("rotation", chat.getRotation()));
                                            }
                                        });

                                        if (chat.getFavorite().equals("YES")){
                                            imageFavorite.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            imageFavorite.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else {
                        layoutDateMessages.setVisibility(View.GONE);
                        layoutImage.setVisibility(View.GONE);
                    }

                    break;

                case "VOICE":
                    layoutText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.GONE);

                    if (chat.getSender().equals(firebaseUser.getUid())){
                        viewMessage = true;
                    }
                    else {
                        viewMessage = false;
                    }

                    if (chat.getReceiverRemoved().equals("NO") || viewMessage) {
                        layoutDateMessages.setVisibility(View.VISIBLE);
                        layoutVoice.setVisibility(View.VISIBLE);
                        textDateVoice.setText(chat.getTime());
                        firestore.collection("Users").document(chat.getSender()).get().addOnSuccessListener(
                                new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String image = documentSnapshot.getString("imageProfile");
                                        assert image != null;
                                        if(!image.equals("")) {
                                            Glide.with(context).load(image).into(imageProfile);
                                        }
                                        else {
                                            Glide.with(context).load(R.drawable.person_no_picture).into(imageProfile);
                                        }
                                    }
                                });

                        audioTime = Integer.parseInt(chat.getDuration());
                        longAudioTime = Long.parseLong(chat.getDuration())/1000;
                        setAudioTime(longAudioTime);

                        imagePlayChat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (tmpImagePlayChat != null) {
                                    tmpImagePlayChat.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                                            R.drawable.ic_baseline_play_arrow_24, null));
                                }

                                progressHorizontal.setMax(audioTime);
                                imagePlayChat.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                                        R.drawable.ic_baseline_pause_24, null));
                                textDuration.setBase(SystemClock.elapsedRealtime());
                                textDuration.start();
                                audioBar(audioTime);

                                audioService.playAudioFromUrl(chat.getUrl(), new AudioService.OnPlayCallBack() {
                                    @Override
                                    public void onFinished() {
                                        imagePlayChat.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),
                                                R.drawable.ic_baseline_play_arrow_24, null));
                                        textDuration.stop();
//                                        setAudioTime(longAudioTime);

                                        reference.child("Chat").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    String id = dataSnapshot.child("id").getValue().toString();
                                                    String key = dataSnapshot.getKey();

                                                    if (id.equals(chat.getId())) {
                                                        reference.child("Chat").child(key).child("visualize").setValue("YES");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(context, R.string.error_unexpected, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                                tmpImagePlayChat = imagePlayChat;
                            }
                        });
                    }
                    else {
                        layoutDateMessages.setVisibility(View.GONE);
                        layoutVoice.setVisibility(View.GONE);
                    }
                    if (chat.getVisualize().equals("YES")) {
                        imageMic.setColorFilter(0xFF25B1F8);
                    }
                    else {
                        imageMic.setColorFilter(0xFFA1A1A1);
                    }

                    break;
            }
        }

        private void setAudioTime(long longAudioTime){
            if (longAudioTime < 10) {
                textDuration.setText(String.format("00:0%s", longAudioTime));
            }
            else if (longAudioTime < 60) {
                textDuration.setText(String.format("00:%s", longAudioTime));
            }
            else{
                double doubleAudioTime = (Double.parseDouble(String.valueOf(longAudioTime)))/60;
                String stringAudioTime = String.format(Locale.getDefault(), "%.2f",doubleAudioTime);
                textDuration.setText(String.format("0%s", stringAudioTime.replace(".",":")));
            }
        }

        private void audioBar(final int audioTime){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(progressBarValue < audioTime)
                    {
                        progressBarValue++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressHorizontal.setProgress(progressBarValue);
                            }
                        });
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Log.e("ProgressBar", "run: ", e);
                        }
                    }
                    if (progressBarValue == audioTime){
                        progressBarValue = 0;
                    }
                }
            }).start();
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (list.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}
