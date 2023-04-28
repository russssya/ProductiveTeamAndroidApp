package com.example.productiveteamapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.productiveteamapp.adapters.ListAdapterTaskChronology;
import com.example.productiveteamapp.R;
import com.example.productiveteamapp.Task;
import com.example.productiveteamapp.TeamData;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentTasksChronology extends Fragment {
    DatabaseReference teamsTable, userTable, mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ArrayList<TeamData> selectTeams;
    ListView listView_tasks;
    ArrayList<Task> list_tasks;
    ListAdapterTaskChronology adapter;
    Date currentDate;
    DateFormat dateFormat;
    String dateToday, dateTomorrow, currentMonth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks_chronology, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init(view);
        getTasksFromDB();
    }

    private void Init(View view){
        selectTeams=new ArrayList<>();

        listView_tasks=view.findViewById(R.id.list_of_tasks_chronology);
        list_tasks=new ArrayList<>();
        adapter=new ListAdapterTaskChronology(requireContext(), list_tasks);
        listView_tasks.setAdapter(adapter);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        userTable= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        teamsTable=FirebaseDatabase.getInstance().getReference("Teams");

        Calendar calendar=Calendar.getInstance();
        dateFormat=new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        currentDate=calendar.getTime();
        dateToday=dateFormat.format(currentDate);

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        currentDate=calendar.getTime();
        dateTomorrow=dateFormat.format(currentDate);

        currentDate=calendar.getTime();
        dateFormat=new SimpleDateFormat("MM.yyyy", Locale.getDefault());
        currentMonth=dateFormat.format(currentDate);
    }

    private void getTasksFromDB(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list_tasks.size()>0) list_tasks.clear();
                for(DataSnapshot ds: snapshot.child("Users").child(currentUser.getUid()).child("Teams").getChildren()){
                    String this_code=ds.child("code").getValue(String.class);
                    assert this_code != null;
                    for(DataSnapshot this_team_task: snapshot.child("Teams").child(this_code).child("Tasks").getChildren()){
                        if(this_team_task.exists()){
                            Task task=this_team_task.getValue(Task.class);
                            assert task != null;
                            if(!(task.date.equals(dateToday))&&!(task.date.equals(dateTomorrow))
                            &&!(task.date.equals(currentMonth))){
                                list_tasks.add(task);
                            }
                        }
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
}
