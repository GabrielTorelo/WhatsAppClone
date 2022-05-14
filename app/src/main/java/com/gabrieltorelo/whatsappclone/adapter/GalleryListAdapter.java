package com.gabrieltorelo.whatsappclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.model.CallList;
import com.gabrieltorelo.whatsappclone.model.chat.Chat;
import com.gabrieltorelo.whatsappclone.view.activities.display.ViewImageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.ViewHolder> {
    private List<Chat> list;
    private Context context;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private boolean viewMessage;

    public GalleryListAdapter(List<Chat> list, Context context) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<Chat> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        reference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private CardView moreFiles;
        private LinearLayout layoutImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_gallery);
            layoutImageView = itemView.findViewById(R.id.layout_imageView_gallery);
            moreFiles = itemView.findViewById(R.id.btn_more_files);
        }

        void bind(final Chat chat){
            switch (chat.getType()){
                case "IMAGE":
                    if (chat.getSender().equals(firebaseUser.getUid())){
                        viewMessage = true;
                    }
                    else {
                        viewMessage = false;
                    }

                    if (chat.getReceiverRemoved().equals("NO") || viewMessage){
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        if (getAdapterPosition() == 0){
                            imageView.setVisibility(View.VISIBLE);
                            moreFiles.setVisibility(View.GONE);
                            Glide.with(context).load(chat.getUrl()).into(imageView);
                            params.setMargins(30, 0, 0, 0);
                            layoutImageView.setLayoutParams(params);
                        }
                        else if (getAdapterPosition() == 12){
                            imageView.setVisibility(View.GONE);
                            moreFiles.setVisibility(View.VISIBLE);
                            moreFiles.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(context, "Galeria Click", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else if (getAdapterPosition() > 12){
                            layoutImageView.setVisibility(View.GONE);
                        }
                        else {
                            imageView.setVisibility(View.VISIBLE);
                            moreFiles.setVisibility(View.GONE);
                            Glide.with(context).load(chat.getUrl()).into(imageView);
                            params.setMargins(0, 0, 0, 0);
                            layoutImageView.setLayoutParams(params);
                        }
                        reference.child("Chat").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String id = dataSnapshot.child("id").getValue().toString();
                                    final String key = dataSnapshot.getKey();

                                    if (id.equals(chat.getId())){
                                        imageView.setRotation(Float.parseFloat(chat.getRotation()));
                                        imageView.setOnClickListener(new View.OnClickListener() {
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
                        imageView.setVisibility(View.GONE);
                        moreFiles.setVisibility(View.GONE);
                    }
                    break;
                default:
                    moreFiles.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
