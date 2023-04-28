package com.example.productiveteamapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productiveteamapp.Message;
import com.example.productiveteamapp.R;

import java.util.List;

public class ListAdapterMessage extends RecyclerView.Adapter<ListAdapterMessage.ViewHolder> {
    LayoutInflater inflater;
    List<Message> messages;

    public ListAdapterMessage(Context context, List<Message> messages){
        this.inflater=LayoutInflater.from(context);
        this.messages=messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message=messages.get(position);
        holder.user_name.setText(message.user_name);
        holder.time_message.setText(message.time);
        holder.text_message.setText(message.text);
    }

    @Override
    public int getItemCount() {return messages.size();}

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView user_name, time_message, text_message;
        ViewHolder(View view){
            super(view);
            user_name=view.findViewById(R.id.view_user_name);
            time_message=view.findViewById(R.id.view_time_message);
            text_message=view.findViewById(R.id.view_text_message);
        }
    }
}
