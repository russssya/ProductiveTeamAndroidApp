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
import com.example.productiveteamapp.R;
import com.example.productiveteamapp.Task;
import com.example.productiveteamapp.TeamData;
import com.example.productiveteamapp.adapters.ListAdapterTaskMonthDone;
import com.example.productiveteamapp.adapters.ListAdapterTaskMonth;
import com.example.productiveteamapp.adapters.ListAdapterTeamCheckbox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FragmentTasksMonth extends Fragment implements ListAdapterTaskMonth.ItemClickListener{
    FloatingActionButton fab;
    DatabaseReference teamsTable, userTable, mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ArrayList<TeamData> selectTeams;
    RecyclerView recyclerViewTasks, recyclerView_tasks_done;
    ArrayList<Task> list_tasks, list_tasks_done;
    ListView list_teams_checkbox;
    ListAdapterTaskMonth adapterTaskMonth;
    ListAdapterTaskMonthDone adapterTaskMonthDone;
    Date currentDate;
    DateFormat dateFormat;
    String currentMonth;
    EditText edit_task;
    Button but_create_task, but_close_create;
    ArrayList<TeamData> list_teams;
    ListAdapterTeamCheckbox adapterTeamCheckbox;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks_month, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        getTasksFromDB();
    }

    private void Init(View view){
        fab=view.findViewById(R.id.fab_month_task);
        selectTeams=new ArrayList<>();

        recyclerViewTasks=view.findViewById(R.id.list_month_tasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        list_tasks=new ArrayList<>();
        adapterTaskMonth=new ListAdapterTaskMonth(requireContext(), list_tasks);
        recyclerViewTasks.setAdapter(adapterTaskMonth);
        adapterTaskMonth.setClickListener(this);

        recyclerView_tasks_done=view.findViewById(R.id.list_done_tasks_month);
        recyclerView_tasks_done.setLayoutManager(new LinearLayoutManager(getContext()));
        list_tasks_done=new ArrayList<>();
        adapterTaskMonthDone=new ListAdapterTaskMonthDone(requireContext(), list_tasks_done);
        recyclerView_tasks_done.setAdapter(adapterTaskMonthDone);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        userTable= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        teamsTable=FirebaseDatabase.getInstance().getReference("Teams");

        currentDate=new Date();
        dateFormat=new SimpleDateFormat("MM.yyyy", Locale.getDefault());
        currentMonth=dateFormat.format(currentDate);

        fab.setOnClickListener(v -> showDialogCreateTaskMonth());
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
                            if(task.date.equals(currentMonth)){
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
                adapterTaskMonth.notifyDataSetChanged();
                adapterTaskMonthDone.notifyDataSetChanged();
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

    private void showDialogCreateTaskMonth(){
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
        adapterTeamCheckbox=new ListAdapterTeamCheckbox(requireContext(), list_teams);
        list_teams_checkbox.setAdapter(adapterTeamCheckbox);
        getTeamsFromDB(list_teams, adapterTeamCheckbox);
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
                Task task=new Task(task_name, currentMonth, "", team.name, team.code, false);
                DatabaseReference pushedRef=teamsTable.child(team.code).child("Tasks").push();
                task.code= pushedRef.getKey();
                pushedRef.setValue(task);
            }
            Toast.makeText(getContext(), "Задача створена", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        but_close_create.setOnClickListener(v -> dialog.dismiss());
    }
}
