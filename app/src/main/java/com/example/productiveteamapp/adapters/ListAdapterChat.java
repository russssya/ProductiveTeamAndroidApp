package com.example.productiveteamapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productiveteamapp.Chat;
import com.example.productiveteamapp.R;

import java.util.List;

public class ListAdapterChat extends RecyclerView.Adapter<ListAdapterChat.ViewHolder> {

    LayoutInflater inflater;
    List<Chat> chatList;
    static ItemClickListener clickListener;

    public ListAdapterChat(Context context, List<Chat> chatList){
        this.chatList=chatList;
        this.inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat=chatList.get(position);
        holder.chat_name.setText(chat.chat_name);
        holder.team_name.setText(chat.team_name);
        holder.date.setText(chat.date);
    }

    @Override
    public int getItemCount() { return chatList.size();}

    public void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView chat_name, team_name, date;
        ViewHolder(View view){
            super(view);
            chat_name=view.findViewById(R.id.chat_name);
            team_name=view.findViewById(R.id.chat_team_name);
            date=view.findViewById(R.id.chat_time_message);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }
}
