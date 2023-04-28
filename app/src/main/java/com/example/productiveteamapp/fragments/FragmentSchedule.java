package com.example.productiveteamapp.fragments;

import android.annotation.SuppressLint;
import android.app.usage.ConfigurationStats;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productiveteamapp.ActivityOneSchedule;
import com.example.productiveteamapp.Constants;
import com.example.productiveteamapp.R;
import com.example.productiveteamapp.TeamData;
import com.example.productiveteamapp.adapters.ListAdapterScheduleTeams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentSchedule extends Fragment implements ListAdapterScheduleTeams.ItemClickListener{
    RecyclerView recyclerViewTeams;
    ArrayList<TeamData> listTeams;
    ListAdapterScheduleTeams adapter;
    DatabaseReference teamsTable, userTable, mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        getTeamsFromDB();
    }

    private void Init(View view){
        recyclerViewTeams=view.findViewById(R.id.recycler_of_schedules);
        recyclerViewTeams.setLayoutManager(new LinearLayoutManager(getContext()));
        listTeams=new ArrayList<>();
        adapter=new ListAdapterScheduleTeams(getContext(), listTeams);
        recyclerViewTeams.setAdapter(adapter);
        adapter.setClickListener(this);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        userTable= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        teamsTable=FirebaseDatabase.getInstance().getReference("Teams");
    }

    private void getTeamsFromDB(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listTeams.size()>0) listTeams.clear();
                for(DataSnapshot ds: snapshot.child("Users").child(currentUser.getUid()).child("Teams").getChildren()){
                    String this_code=ds.child("code").getValue(String.class);
                    if(snapshot.exists()){
                        assert this_code != null;
                        DataSnapshot this_team=snapshot.child("Teams").child(this_code);
                        TeamData team =this_team.child("TeamData").getValue(TeamData.class);
                        assert team != null;
                        listTeams.add(team);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        TeamData team=listTeams.get(position);
        Intent intent=new Intent(getActivity(), ActivityOneSchedule.class);
        intent.putExtra(Constants.TEAM_NAME, team.name);
        intent.putExtra(Constants.TEAM_CODE, team.code);
        startActivity(intent);
    }
}
