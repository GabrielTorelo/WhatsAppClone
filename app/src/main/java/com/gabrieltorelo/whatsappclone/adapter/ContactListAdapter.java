package com.gabrieltorelo.whatsappclone.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.model.user.Users;
import com.gabrieltorelo.whatsappclone.view.activities.chat.ChatActivity;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private List<Users> list;
    private Context context;

    public ContactListAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users user = list.get(position);

        holder.username.setText(user.getUserName());
        holder.desc.setText(user.getBio());

        if (!user.getImageProfile().equals("")) {
            Glide.with(context).load(user.getImageProfile()).into(holder.imageProfile);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userID", user.getUserID())
                        .putExtra("userName", user.getUserName())
                        .putExtra("userNumber", user.getUserPhone())
                        .putExtra("imageProfile", user.getImageProfile())
                        .putExtra("userBio", user.getBio())
                        .putExtra("userBioDate", user.getBioDate()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return  list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageProfile;
        private TextView username, desc;
        private LinearLayout layoutNewGroup, layoutInviteFriend,
                layoutHelpContact, layoutNewContact;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.tv_name);
            desc = itemView.findViewById(R.id.tv_bio);
        }
    }
}
