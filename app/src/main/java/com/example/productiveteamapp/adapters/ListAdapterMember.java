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
import com.example.productiveteamapp.User;

import java.util.ArrayList;

public class ListAdapterMember extends ArrayAdapter<User> {
    public ListAdapterMember(@NonNull Context context, ArrayList<User> arrayList){
        super(context, R.layout.item_member,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User member=getItem(position);
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_member, parent, false);
        }
        TextView user_name=convertView.findViewById(R.id.view_item_member);
        user_name.setText(member.name);
        return convertView;
    }
}
