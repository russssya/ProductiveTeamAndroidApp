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

import com.example.productiveteamapp.ActivityOneTask;
import com.example.productiveteamapp.Constants;
import com.example.productiveteamapp.adapters.ListAdapterTaskToday;
import com.example.productiveteamapp.adapters.ListAdapterTaskTodayDone;
import com.example.productiveteamapp.adapters.ListAdapterTeamCheckbox;
import com.example.productiveteamapp.R;
import com.example.productiveteamapp.Task;
import com.example.productiveteamapp.TeamData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FragmentTasksToday extends Fragment implements ListAdapterTaskToday.ItemClickListener{
    FloatingActionButton fab;
    DatabaseReference teamsTable, userTable, mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ArrayList<TeamData> selectTeams;
    RecyclerView recyclerView_tasks, recyclerView_tasks_done;
    ListView list_teams_checkbox;
    ArrayList<Task> list_tasks, list_tasks_done;
    ListAdapterTaskToday adapterTaskToday;
    ListAdapterTaskTodayDone adapterTaskTodayDone;
    Date currentDate;
    DateFormat dateFormat;
    String dateToday;
    EditText edit_task;
    Button but_create_task, but_close_create;
    ArrayList<TeamData> list_teams;
    ListAdapterTeamCheckbox adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        getTasksFromDB();
    }

    private void Init(View view){
        fab=view.findViewById(R.id.fab_task_today);
        selectTeams=new ArrayList<>();

        recyclerView_tasks=view.findViewById(R.id.list_of_today_tasks);
        recyclerView_tasks.setLayoutManager(new LinearLayoutManager(getContext()));
        list_tasks=new ArrayList<>();
        adapterTaskToday=new ListAdapterTaskToday(requireContext(), list_tasks);
        recyclerView_tasks.setAdapter(adapterTaskToday);
        adapterTaskToday.setClickListener(this);

        recyclerView_tasks_done=view.findViewById(R.id.list_of_done_tasks);
        recyclerView_tasks_done.setLayoutManager(new LinearLayoutManager(getContext()));
        list_tasks_done=new ArrayList<>();
        adapterTaskTodayDone=new ListAdapterTaskTodayDone(requireContext(), list_tasks_done);
        recyclerView_tasks_done.setAdapter(adapterTaskTodayDone);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        userTable= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        teamsTable=FirebaseDatabase.getInstance().getReference("Teams");

        currentDate=new Date();
        dateFormat=new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateToday=dateFormat.format(currentDate);

        fab.setOnClickListener(v -> showDialogCreateTaskToday());
    }

    private void getTasksFromDB(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list_tasks.size()>0) list_tasks.clear();
                if(list_tasks_done.size()>0) list_tasks_done.clear();
                for(DataSnapshot ds: snapshot.child("Users").child(currentUser.getUid()).child("Teams").getChildren()){
                    String this_code=ds.child("code").getValue(String.class);
                    assert this_code != null;
                    for(DataSnapshot this_team_task: snapshot.child("Teams").child(this_code).child("Tasks").getChildren()){
                        if(this_team_task.exists()){
                            Task task=this_team_task.getValue(Task.class);
                            assert task != null;
                            if(task.date.equals(dateToday)){
                                if(task.done){
                                    list_tasks_done.add(task);
                                }
                                else{
                                    list_tasks.add(task);
                                }
                            }
                        }
                    }
                }
                adapterTaskToday.notifyDataSetChanged();
                adapterTaskTodayDone.notifyDataSetChanged();
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

    @Override
    public void onClick(View view, int position){
        Task task=list_tasks.get(position);
        Intent intent=new Intent(getActivity(), ActivityOneTask.class);
        intent.putExtra(Constants.TASK_NAME, task.name);
        intent.putExtra(Constants.TASK_DATE, task.date);
        intent.putExtra(Constants.TASK_CODE, task.code);
        intent.putExtra(Constants.TEAM_NAME, task.team_name);
        intent.putExtra(Constants.TEAM_CODE, task.team_code);
        startActivity(intent);
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

    private void showDialogCreateTaskToday(){
        selectTeams.clear();

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        LayoutInflater inflater=requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view=inflater.inflate(R.layout.dialog_create_task, null);

        edit_task=view.findViewById(R.id.edit_task);
        but_create_task=view.findViewById(R.id.button_create_task_today);
        but_close_create=view.findViewById(R.id.button_close_create_task);
        list_teams_checkbox=view.findViewById(R.id.list_teams_checkbox);
        list_teams=new ArrayList<>();
        adapter=new ListAdapterTeamCheckbox(requireContext(), list_teams);
        list_teams_checkbox.setAdapter(adapter);
        getTeamsFromDB(list_teams, adapter);
        setOnClickListTeamsItem(list_teams_checkbox);

        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.show();
        but_create_task.setOnClickListener(v ->{
            if(selectTeams.size()==0){
                Toast.makeText(getContext(), "Ви не обрали жодної команди", Toast.LENGTH_SHORT).show();
                return;
            }
            String task_name=edit_task.getText().toString();
            for(TeamData team: selectTeams){
                Task task=new Task(task_name, dateToday, "", team.name, team.code, false);
                DatabaseReference pushedRef=teamsTable.child(team.code).child("Tasks").push();
                task.code= pushedRef.getKey();
                pushedRef.setValue(task);
                sendNotification(team.name, task.name, team.name);
            }
            Toast.makeText(getContext(), "Задача створена", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        but_close_create.setOnClickListener(v -> dialog.dismiss());
    }

    private void sendNotification(String title, String body, String topic){
        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("message", body);

        RemoteMessage message = new RemoteMessage.Builder(topic)
                .setData(data)
                .build();

        FirebaseMessaging.getInstance().send(message);
    }
}
