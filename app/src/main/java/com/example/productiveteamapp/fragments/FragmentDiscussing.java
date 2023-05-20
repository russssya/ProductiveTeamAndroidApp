package com.example.productiveteamapp.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productiveteamapp.ActivityOneChat;
import com.example.productiveteamapp.Chat;
import com.example.productiveteamapp.Constants;
import com.example.productiveteamapp.R;
import com.example.productiveteamapp.TeamData;
import com.example.productiveteamapp.adapters.ListAdapterChat;
import com.example.productiveteamapp.adapters.ListAdapterTeamCheckbox;
import com.example.productiveteamapp.notification.FcmNotificationSender;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentDiscussing extends Fragment implements ListAdapterChat.ItemClickListener{
    RecyclerView recyclerViewChats;
    FloatingActionButton fab;
    ArrayList<Chat> list_chats;
    ListAdapterChat adapterChat;
    DatabaseReference teamsTable, userTable, mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ArrayList<TeamData> selectTeams, list_teams;
    ListView list_teams_checkbox;
    EditText edit_chat_name;
    Button but_create_chat, but_close_create_chat;
    ListAdapterTeamCheckbox adapter;
    String user_name;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discussing,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        getChatsFromDB();
    }

    private void Init(View view){
        fab=view.findViewById(R.id.fab_new_chat);
        selectTeams=new ArrayList<>();

        recyclerViewChats=view.findViewById(R.id.list_of_chats);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));
        list_chats=new ArrayList<>();
        adapterChat=new ListAdapterChat(requireContext(), list_chats);
        recyclerViewChats.setAdapter(adapterChat);
        adapterChat.setClickListener(this);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        userTable= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        teamsTable=FirebaseDatabase.getInstance().getReference("Teams");

        user_name=getUser_name();

        fab.setOnClickListener(v -> showDialogCreateChat());
    }

    @Override
    public void onClick(View view, int position) {
        Chat chat=list_chats.get(position);
        Intent intent=new Intent(getActivity(), ActivityOneChat.class);
        intent.putExtra(Constants.CHAT_NAME, chat.chat_name);
        intent.putExtra(Constants.CHAT_CODE, chat.chat_code);
        intent.putExtra(Constants.TEAM_CODE, chat.team_code);
        intent.putExtra(Constants.USER_NAME, user_name);
        startActivity(intent);
    }

    private void getChatsFromDB(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list_chats.size()>0) list_chats.clear();
                for(DataSnapshot ds: snapshot.child("Users").child(currentUser.getUid()).child("Teams").getChildren()){
                    String this_code=ds.child("code").getValue(String.class);
                    assert this_code != null;
                    for(DataSnapshot this_team_chat: snapshot.child("Teams").child(this_code).child("Chats").getChildren()){
                        if(this_team_chat.exists()){
                            Chat chat=this_team_chat.getValue(Chat.class);
                            assert chat != null;
                            list_chats.add(chat);
                        }
                    }
                }
                adapterChat.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTeamsFromDB(ArrayList<TeamData> listData, ListAdapterTeamCheckbox adapter){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size()>0) listData.clear();
                for(DataSnapshot ds: snapshot.child("Users").child(currentUser.getUid()).child("Teams").getChildren()){
                    String this_code=ds.child("code").getValue(String.class);
                    assert this_code != null;
                    DataSnapshot this_team=snapshot.child("Teams").child(this_code);
                    TeamData team =this_team.child("TeamData").getValue(TeamData.class);
                    assert team != null;
                    listData.add(team);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getUser_name() {
        userTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user_name=snapshot.child("User").child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
        return user_name;
    }

    @SuppressLint("ResourceAsColor")
    private void setOnClickListTeamsItem(ListView listView){
        listView.setOnItemClickListener((parent, view, position, id) -> {
            ImageView selected=view.findViewById(R.id.team_selected);
            TeamData team=(TeamData) parent.getItemAtPosition(position);
            if(selected.getVisibility()==View.VISIBLE){
                selected.setVisibility(View.INVISIBLE);
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
                selectTeams.remove(team);
            }
            else{
                selected.setVisibility(View.VISIBLE);
                view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.yellow));
                selectTeams.add(team);
            }
        });
    }

    private void showDialogCreateChat(){
        selectTeams.clear();

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        LayoutInflater inflater=requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view=inflater.inflate(R.layout.dialog_create_chat, null);

        edit_chat_name=view.findViewById(R.id.edit_chat_name);
        but_create_chat=view.findViewById(R.id.but_create_chat);
        but_close_create_chat=view.findViewById(R.id.but_close_create_chat);
        list_teams_checkbox=view.findViewById(R.id.list_teams_checkbox);
        list_teams=new ArrayList<>();
        adapter=new ListAdapterTeamCheckbox(requireContext(), list_teams);
        list_teams_checkbox.setAdapter(adapter);
        getTeamsFromDB(list_teams, adapter);
        setOnClickListTeamsItem(list_teams_checkbox);

        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.show();
        but_create_chat.setOnClickListener(v ->{
            if(selectTeams.size()==0){
                Toast.makeText(getContext(), "Ви не обрали жодної команди", Toast.LENGTH_SHORT).show();
                return;
            }
            String chat_name=edit_chat_name.getText().toString();
            for(TeamData team: selectTeams){
                Chat chat=new Chat(chat_name, "", team.name, team.code, "00");
                DatabaseReference pushedRef=teamsTable.child(team.code).child("Chats").push();
                chat.chat_code= pushedRef.getKey();
                pushedRef.setValue(chat);
                FcmNotificationSender.sendNotification(team.code, team.name, "Створено новий чат "+ chat_name);
            }
            Toast.makeText(getContext(), "Чат створено", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        but_close_create_chat.setOnClickListener(v -> dialog.dismiss());
    }
}
