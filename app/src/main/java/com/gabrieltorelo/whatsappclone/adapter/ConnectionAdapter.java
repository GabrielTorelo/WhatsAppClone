package com.gabrieltorelo.whatsappclone.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.model.user.Connection;

import java.util.List;

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {
    private List<Connection> list;
    private Context context;

    public ConnectionAdapter(List<Connection> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setList(List<Connection> list){
        this.list = list;
        Toast.makeText(context, "LIST: "+list.get(1), Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textStatus = itemView.findViewById(R.id.tv_chat_status);
        }

        void bind(final Connection connection){
            if (connection.getStatus() != null && !connection.getStatus().equals("")) {
                textStatus.setText(connection.getStatus());
            }
            else {
                textStatus.setVisibility(View.GONE);
            }
        }
    }
}
