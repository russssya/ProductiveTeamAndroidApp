package com.example.productiveteamapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productiveteamapp.R;
import com.example.productiveteamapp.TeamData;

import java.util.List;

public class ListAdapterScheduleTeams extends RecyclerView.Adapter<ListAdapterScheduleTeams.ViewHolder>{
    final LayoutInflater inflater;
    final List<TeamData> listTeams;
    static ItemClickListener clickListener;

    public ListAdapterScheduleTeams(Context context, List<TeamData> listTeams){
        this.listTeams=listTeams;
        this.inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeamData team=listTeams.get(position);
        holder.team.setText(team.name);
    }

    @Override
    public int getItemCount() {return listTeams.size();}

    public void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView team;
        ViewHolder(View view){
            super(view);
            team=view.findViewById(R.id.view_team_schedule);
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
