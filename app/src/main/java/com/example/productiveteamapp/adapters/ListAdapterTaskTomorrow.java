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

import java.util.List;

public class ListAdapterTaskTomorrow extends RecyclerView.Adapter<ListAdapterTaskTomorrow.ViewHolder>{

    final LayoutInflater inflater;
    final List<Task> tasks;
    static ItemClickListener clickListener;

    public ListAdapterTaskTomorrow(Context context, List<Task> tasks){
        this.tasks = tasks;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListAdapterTaskTomorrow.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterTaskTomorrow.ViewHolder holder, int position) {
        Task task=tasks.get(position);
        holder.task_name.setText(task.name);
        holder.team_name.setText(task.team_name);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setClickListener(ListAdapterTaskTomorrow.ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView task_name, team_name;
        ViewHolder(View view){
            super(view);
            task_name=view.findViewById(R.id.view_item_task);
            team_name=view.findViewById(R.id.view_task_team);
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
