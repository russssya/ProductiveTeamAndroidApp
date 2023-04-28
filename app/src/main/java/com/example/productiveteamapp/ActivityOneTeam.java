package com.example.productiveteamapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.example.productiveteamapp.adapters.ListAdapterMember;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityOneTeam extends AppCompatActivity {
    TextView view_name, view_desc, view_manager;
    ListView members;
    ArrayList<User> listData;
    ListAdapterMember listAdapterMember;
    ImageButton button_back, button_popup;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference userTable, teamsTable, mDataBase;
    String name, desc, code;
    User manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_team);
        Init();
    }

    private void Init(){
        view_name=findViewById(R.id.view_team_name);
        view_desc=findViewById(R.id.view_team_desc);
        view_manager=findViewById(R.id.view_team_manager);
        members=findViewById(R.id.list_of_members);
        listData=new ArrayList<>();
        listAdapterMember=new ListAdapterMember(ActivityOneTeam.this, listData);
        members.setAdapter(listAdapterMember);
        members.setClickable(true);
        button_back=findViewById(R.id.button_back_teams);
        button_popup=findViewById(R.id.popup_one_team);

        Intent intent=getIntent();
        assert intent!=null;
        name=intent.getStringExtra(Constants.TEAM_NAME);
        desc=intent.getStringExtra(Constants.TEAM_DESC);
        code=intent.getStringExtra(Constants.TEAM_CODE);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        mDataBase=FirebaseDatabase.getInstance().getReference();
        userTable= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        teamsTable=FirebaseDatabase.getInstance().getReference("Teams");

        getDataMembers();
        getDataManager();

        view_name.setText(name);
        view_desc.setText(desc);

        button_back.setOnClickListener(v -> finish());
        button_popup.setOnClickListener(v ->{
            if(Objects.equals(currentUser.getEmail(), manager.email)){
                showManagerPopUp(button_popup);
            }
            else{
                showMemberPopUp(button_popup);
            }
        });
    }

    private void getDataManager(){
        teamsTable.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    manager = snapshot.child("Manager").getValue(User.class);
                    assert manager != null;
                    view_manager.setText(manager.name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivityOneTeam.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDataMembers(){
        teamsTable.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size()>0) listData.clear();
                for(DataSnapshot ds: snapshot.child("Members").getChildren()){
                    User member=ds.getValue(User.class);
                    assert member != null;
                    listData.add(member);
                }
                listAdapterMember.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivityOneTeam.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void showManagerPopUp(View view){
        PopupMenu popupMenu=new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.edit_team_info:
                    Intent intent=new Intent(this, ActivityChangeTeam.class);
                    intent.putExtra(Constants.TEAM_CODE, code);
                    intent.putExtra(Constants.TEAM_NAME, name);
                    intent.putExtra(Constants.TEAM_DESC, desc);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.delete_team:
                    showDeleteDialog();
                    return true;
                case R.id.get_code_team:
                    showCodeDialog();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.inflate(R.menu.popup_teams_manager);
        popupMenu.show();
    }

    @SuppressLint("NonConstantResourceId")
    private void showMemberPopUp(View view){
        PopupMenu popupMenu=new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.leave_team:
                    showLeaveDialog();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.inflate(R.menu.popup_teams_member);
        popupMenu.show();
    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityOneTeam.this);
        builder.setMessage(R.string.sure_delete)
                .setPositiveButton(R.string.yes, (dialog, which) ->
                        mDataBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.child("Teams").child(code).getRef().removeValue();
                        snapshot.child("Users").child(currentUser.getUid()).child("Teams").child(code).getRef().removeValue();
                        Toast.makeText(ActivityOneTeam.this, "Команда видалена", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                }))
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showLeaveDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityOneTeam.this);
        builder.setMessage(R.string.sure_leave)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    userTable.child("Teams").child(code).removeValue();
                    Toast.makeText(ActivityOneTeam.this, "Ви залишили команду", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showCodeDialog(){
        EditText edit_show_code;
        Button but_copy, but_close_show;
        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityOneTeam.this);
        LayoutInflater inflater= getLayoutInflater();
        @SuppressLint("InflateParams")
        View view=inflater.inflate(R.layout.dialog_show_code,null);
        edit_show_code=view.findViewById(R.id.edit_show_code);
        but_copy=view.findViewById(R.id.button_copy);
        but_close_show=view.findViewById(R.id.button_close_show);
        edit_show_code.setText(code);
        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.show();
        but_copy.setOnClickListener(v ->{
            ClipboardManager clipboardManager=(ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            String text=edit_show_code.getText().toString();
            ClipData clipData=ClipData.newPlainText("code", text);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(ActivityOneTeam.this, "Код в буфері обміну", Toast.LENGTH_SHORT).show();
        });
        but_close_show.setOnClickListener(v -> dialog.dismiss());
    }
}
