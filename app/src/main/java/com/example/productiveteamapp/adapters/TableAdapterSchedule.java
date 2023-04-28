package com.example.productiveteamapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productiveteamapp.R;
import com.example.productiveteamapp.Schedule;

import java.util.List;

public class TableAdapterSchedule extends RecyclerView.Adapter<TableAdapterSchedule.ViewHolder> {

    Context context;
    List<Schedule> scheduleList;
    static ItemClickListener clickListener;

    public TableAdapterSchedule(Context context, List<Schedule> scheduleList){
        this.context=context;
        this.scheduleList=scheduleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_row_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule=scheduleList.get(position);
        holder.name.setText(schedule.name);
        holder.monday.setText(schedule.monday);
        holder.tuesday.setText(schedule.tuesday);
        holder.wednesday.setText(schedule.wednesday);
        holder.thursday.setText(schedule.thursday);
        holder.friday.setText(schedule.friday);
        holder.saturday.setText(schedule.saturday);
        holder.sunday.setText(schedule.sunday);
    }

    @Override
    public int getItemCount() {return scheduleList.size();}

    public void setOnClickListener(ItemClickListener itemClickListener){
        clickListener=itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name, monday, tuesday, wednesday, thursday, friday, saturday, sunday;
        public ViewHolder(View view){
            super(view);
            name=view.findViewById(R.id.member_schedule);
            monday=view.findViewById(R.id.time_monday);
            tuesday=view.findViewById(R.id.time_tuesday);
            wednesday=view.findViewById(R.id.time_wednesday);
            thursday=view.findViewById(R.id.time_thursday);
            friday=view.findViewById(R.id.time_friday);
            saturday=view.findViewById(R.id.time_saturday);
            sunday=view.findViewById(R.id.time_sunday);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null){
                clickListener.onClick(v, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }
}
