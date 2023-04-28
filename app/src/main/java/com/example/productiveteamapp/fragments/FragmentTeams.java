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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.productiveteamapp.ActivityOneTeam;
import com.example.productiveteamapp.Constants;
import com.example.productiveteamapp.Schedule;
import com.example.productiveteamapp.adapters.ListAdapterTeam;
import com.example.productiveteamapp.R;
import com.example.productiveteamapp.TeamData;
import com.example.productiveteamapp.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentTeams extends Fragment {
    Button button_create,button_join;
    ListView listView;
    ArrayList<TeamData> listData;
    ArrayList<TeamData> listTemp;
    ListAdapterTeam adapterTeam;
    DatabaseReference teamsTable, userTable, mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teams,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        getTeamFromDB();
        setOnClickListItem();
    }

    private void Init(View view){
        button_create=view.findViewById(R.id.button_create);
        button_join=view.findViewById(R.id.button_join);

        listView=view.findViewById(R.id.list_of_teams);
        listData= new ArrayList<>();
        listTemp=new ArrayList<>();
        adapterTeam=new ListAdapterTeam(requireContext(), listData);
        listView.setAdapter(adapterTeam);
        listView.setClickable(true);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        userTable= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        teamsTable=FirebaseDatabase.getInstance().getReference("Teams");
        getUserFromDB();

        button_create.setOnClickListener(v -> showDialogCreateTeam());

        button_join.setOnClickListener(v -> showDialogJoinTeam());
    }

    private void getUserFromDB(){
        userTable.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getTeamFromDB(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size()>0) listData.clear();
                if(listTemp.size()>0) listTemp.clear();
                for(DataSnapshot ds: snapshot.child("Users").child(currentUser.getUid()).child("Teams").getChildren()){
                    String this_code=ds.child("code").getValue(String.class);
                    if(snapshot.exists()){
                        assert this_code != null;
                        DataSnapshot this_team=snapshot.child("Teams").child(this_code);
                        TeamData team =this_team.child("TeamData").getValue(TeamData.class);
                        assert team != null;
                        listData.add(team);
                        listTemp.add(team);
                    }
                }
                adapterTeam.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListItem(){
        listView.setOnItemClickListener((parent, view, position, id) -> {
            TeamData team=listTemp.get(position);
            Intent intent=new Intent(getActivity(), ActivityOneTeam.class);
            intent.putExtra(Constants.TEAM_NAME, team.name);
            intent.putExtra(Constants.TEAM_DESC, team.description);
            intent.putExtra(Constants.TEAM_CODE, team.code);
            startActivity(intent);
        });
    }

    private void showDialogCreateTeam(){
        EditText edit_team_name, edit_team_desc;
        Button but_create_team, but_close_create;
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        LayoutInflater inflater= requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view=inflater.inflate(R.layout.dialog_create_team,null);
        edit_team_name=view.findViewById(R.id.edit_team_name);
        edit_team_desc=view.findViewById(R.id.edit_team_description);
        but_create_team=view.findViewById(R.id.button_create_team);
        but_close_create=view.findViewById(R.id.button_close_create);
        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.show();
        but_create_team.setOnClickListener(v ->{

            String name=edit_team_name.getText().toString();
            String desc=edit_team_desc.getText().toString();
            DatabaseReference pushedRef=teamsTable.push();
            TeamData team=new TeamData(pushedRef.getKey(), name, desc);
            pushedRef.child("TeamData").setValue(team);
            pushedRef.child("Manager").setValue(user);
            DatabaseReference pushedRefSchedule=pushedRef.child("Schedule").push();
            Schedule schedule=new Schedule(user.name, pushedRefSchedule.getKey());
            pushedRefSchedule.setValue(schedule);
            String code=pushedRef.getKey();
            assert code != null;
            userTable.child("Teams").child(code).child("code").setValue(code);
            Toast.makeText(getContext(),"Команда створена",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        but_close_create.setOnClickListener(v -> dialog.dismiss());
    }

    private void showDialogJoinTeam(){
        EditText edit_code_join;
        Button but_join_team, but_close_join;
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        LayoutInflater inflater= requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view=inflater.inflate(R.layout.dialog_join_team,null);
        edit_code_join=view.findViewById(R.id.edit_code_join);
        but_join_team=view.findViewById(R.id.button_join_team);
        but_close_join=view.findViewById(R.id.button_close_join);
        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.show();
        but_join_team.setOnClickListener(v ->{
            String code=edit_code_join.getText().toString();
            teamsTable.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        userTable.child("Teams").child(code).child("code").setValue(code);
                        teamsTable.child(code).child("Members").push().setValue(user);
                        DatabaseReference pushedRefSchedule=teamsTable.child(code).child("Schedule").push();
                        Schedule schedule=new Schedule(user.name, pushedRefSchedule.getKey());
                        pushedRefSchedule.setValue(schedule);
                        Toast.makeText(getContext(),"Ви приєднались до команди",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Команди не існує", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
        });
        but_close_join.setOnClickListener(v-> dialog.dismiss());
    }
}
