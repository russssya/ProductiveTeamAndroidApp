package com.example.productiveteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityChangeTeam extends AppCompatActivity {
    ImageButton but_back;
    EditText edit_name, edit_desc;
    Button but_change;
    String new_name, new_desc, team_code, team_name, team_desc;
    DatabaseReference teamTable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_team);
        Init();
    }

    private void Init(){
        but_back=findViewById(R.id.button_back_one_team);
        edit_name=findViewById(R.id.edit_name_change);
        edit_desc=findViewById(R.id.edit_desc_change);
        but_change=findViewById(R.id.change_info_button);

        Intent intent=getIntent();
        assert intent!=null;
        team_code=intent.getStringExtra(Constants.TEAM_CODE);
        team_name=intent.getStringExtra(Constants.TEAM_NAME);
        team_desc=intent.getStringExtra(Constants.TEAM_DESC);
        edit_name.setText(team_name);
        edit_desc.setText(team_desc);

        teamTable= FirebaseDatabase.getInstance().getReference("Teams").child(team_code).child("TeamData");

        but_change.setOnClickListener(v ->{
            new_name=edit_name.getText().toString();
            new_desc=edit_desc.getText().toString();
            if(new_name.isEmpty() || new_desc.isEmpty()){
                Toast.makeText(this, "Пусте поле", Toast.LENGTH_SHORT).show();
            }
            else{
                teamTable.child("name").setValue(new_name);
                teamTable.child("desc").setValue(new_desc);
                Toast.makeText(this, "Інформацію змінено!", Toast.LENGTH_SHORT).show();
            }
        });
        but_back.setOnClickListener(v -> finish());
    }
}
