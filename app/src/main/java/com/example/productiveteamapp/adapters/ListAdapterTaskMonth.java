package com.example.productiveteamapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productiveteamapp.R;
import com.example.productiveteamapp.Task;

import java.util.ArrayList;

public class ListAdapterTaskMonth extends RecyclerView.Adapter<ListAdapterTaskMonth.ViewHolder> {

    ArrayList<Task> taskArrayList;
    LayoutInflater inflater;
    static ItemClickListener clickListener;

    public ListAdapterTaskMonth(Context context, ArrayList<Task> taskArrayList) {
        this.taskArrayList = taskArrayList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task=taskArrayList.get(position);
        holder.task_name.setText(task.name);
        holder.team_name.setText(task.team_name);
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView task_name, team_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task_name=itemView.findViewById(R.id.view_item_task);
            team_name=itemView.findViewById(R.id.view_task_team);
            itemView.setOnClickListener(this);
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
