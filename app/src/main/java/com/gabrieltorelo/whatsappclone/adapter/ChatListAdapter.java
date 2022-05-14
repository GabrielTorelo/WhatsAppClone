package com.gabrieltorelo.whatsappclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.model.ChatList;
import com.gabrieltorelo.whatsappclone.model.user.Users;
import com.gabrieltorelo.whatsappclone.service.ColorClickService;
import com.gabrieltorelo.whatsappclone.view.activities.chat.ChatActivity;
import com.gabrieltorelo.whatsappclone.view.activities.dialog.DialogViewUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {

    private List<ChatList> list;
    private Context context;

    public ChatListAdapter(List<ChatList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_chat_list, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final ChatList chatList = list.get(position);

        holder.tvName.setText(chatList.getUserName());

        holder.tvDesc.setText(chatList.getDescriptionMessage());
        holder.tvDate.setText(chatList.getDateMessage());

        if (!chatList.getUrlProfile().equals("")) {
            Glide.with(context).load(chatList.getUrlProfile()).into(holder.profile);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorClickService.setColorClicked(context, "SHORT", "GRAY", "WHITE",
                        false, null, null, null, null, null,
                        holder.itemView);
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userID", chatList.getUserID())
                        .putExtra("userName", chatList.getUserName())
//                        .putExtra("userStatus", Users.getStatus()) /* CHANGE TO USER STATUS
                        .putExtra("imageProfile", chatList.getUrlProfile())
                        .putExtra("userNumber", chatList.getUserPhone())
                        .putExtra("userBio", chatList.getUserBio())
                        .putExtra("userBioDate", chatList.getUserBioDate()));
            }
        });

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogViewUser(context, chatList);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDesc, tvDate;
        private CircularImageView profile;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvName = itemView.findViewById(R.id.tv_name);
            profile = itemView.findViewById(R.id.image_profile);
        }
    }
}
