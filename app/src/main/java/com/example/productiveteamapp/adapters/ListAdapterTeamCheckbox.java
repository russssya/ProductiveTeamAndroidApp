package com.example.productiveteamapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.productiveteamapp.R;
import com.example.productiveteamapp.TeamData;

import java.util.ArrayList;

public class ListAdapterTeamCheckbox extends ArrayAdapter<TeamData> {

    public ListAdapterTeamCheckbox(@NonNull Context context, ArrayList<TeamData> arrayList){
        super(context, R.layout.item_team_checkbox, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TeamData team=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_team_checkbox, parent, false);
        }
        TextView task=convertView.findViewById(R.id.view_item_team_check);
        task.setText(team.name);
        return convertView;
    }

}
