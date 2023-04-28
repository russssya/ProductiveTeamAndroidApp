package com.example.productiveteamapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.productiveteamapp.R;
import com.example.productiveteamapp.Task;

import java.util.ArrayList;

public class ListAdapterTaskChronology extends ArrayAdapter<Task> {

    public ListAdapterTaskChronology(@NonNull Context context, ArrayList<Task> arrayList){
        super(context, R.layout.item_task_chronology, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Task task=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_task_chronology, parent, false);
        }
        TextView task_name=convertView.findViewById(R.id.task_name_chronology);
        task_name.setText(task.name);
        TextView team_name=convertView.findViewById(R.id.team_name_chronology);
        team_name.setText(task.team_name);
        TextView task_date=convertView.findViewById(R.id.task_date);
        task_date.setText(task.date);
        TextView task_done=convertView.findViewById(R.id.task_doing);
        if(task.done){
            task_done.setText("Виконано");
        }
        else{
            task_done.setText("Не виконано");
        }
        return convertView;
    }
}
