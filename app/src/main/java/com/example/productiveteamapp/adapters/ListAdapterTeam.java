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
import com.example.productiveteamapp.TeamData;

import java.util.ArrayList;

public class ListAdapterTeam extends ArrayAdapter<TeamData> {

    public ListAdapterTeam(@NonNull Context context, ArrayList<TeamData> arrayList){
        super(context, R.layout.item_team,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TeamData team=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_team, parent, false);
        }
        TextView team_name=convertView.findViewById(R.id.view_item_team);
        team_name.setText(team.name);
        return convertView;
    }
}
