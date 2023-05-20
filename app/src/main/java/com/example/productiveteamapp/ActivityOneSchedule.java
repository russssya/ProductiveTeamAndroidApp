package com.example.productiveteamapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productiveteamapp.adapters.TableAdapterSchedule;
import com.example.productiveteamapp.notification.FcmNotificationSender;
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

public class ActivityOneSchedule extends AppCompatActivity implements TableAdapterSchedule.ItemClickListener{
    TextView title, member_name;
    ArrayList<TextView> days, days_for_member;
    EditText time_monday, time_tuesday, time_wednesday, time_thursday, time_friday, time_saturday, time_sunday;
    ImageButton but_back, but_popup;
    Button button_save_schedule, button_close_dialog;
    Date currentDate;
    DateFormat dateFormat;
    DatabaseReference scheduleTable;
    RecyclerView recyclerViewSchedule;
    TableAdapterSchedule adapterSchedule;
    ArrayList<Schedule> scheduleArrayList;
    String team_name, team_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_schedule);
        Init();
        getScheduleFromDB();
    }

    private void Init(){
        title=findViewById(R.id.title_schedule_name);
        but_back=findViewById(R.id.button_back_schedule);
        but_popup=findViewById(R.id.popup_one_schedule);

        days=new ArrayList<>();
        days.add(findViewById(R.id.monday));
        days.add(findViewById(R.id.tuesday));
        days.add(findViewById(R.id.wednesday));
        days.add(findViewById(R.id.thursday));
        days.add(findViewById(R.id.friday));
        days.add(findViewById(R.id.saturday));
        days.add(findViewById(R.id.sunday));

        recyclerViewSchedule=findViewById(R.id.recycler_schedule);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        scheduleArrayList=new ArrayList<>();
        adapterSchedule=new TableAdapterSchedule(getApplicationContext(), scheduleArrayList);
        recyclerViewSchedule.setAdapter(adapterSchedule);
        adapterSchedule.setOnClickListener(this);

        InitDates(days);

        Intent intent=getIntent();
        team_name=intent.getStringExtra(Constants.TEAM_NAME);
        team_code=intent.getStringExtra(Constants.TEAM_CODE);

        scheduleTable=FirebaseDatabase.getInstance().getReference("Teams")
                .child(team_code).child("Schedule");

        title.setText(team_name);

        but_back.setOnClickListener(v -> finish());
    }

    private void getScheduleFromDB(){
        scheduleTable.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(scheduleArrayList.size()>0) scheduleArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    Schedule schedule=ds.getValue(Schedule.class);
                    scheduleArrayList.add(schedule);
                }
                adapterSchedule.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivityOneSchedule.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view, int position){
        Schedule schedule=scheduleArrayList.get(position);
        showDialogCreateSchedule(schedule);
    }

    private void showDialogCreateSchedule(Schedule schedule){
        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityOneSchedule.this);
        LayoutInflater inflater=getLayoutInflater();
        @SuppressLint("InflateParams")
        View view=inflater.inflate(R.layout.dialog_create_schedule, null);
        InitDialogCreateSchedule(view, schedule);
        InitDates(days_for_member);
        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.show();
        button_save_schedule.setOnClickListener(v ->{
            Schedule new_schedule=new Schedule(schedule.name, schedule.code, time_monday.getText().toString(),
                    time_tuesday.getText().toString(), time_wednesday.getText().toString(),
                    time_thursday.getText().toString(), time_friday.getText().toString(),
                    time_saturday.getText().toString(), time_sunday.getText().toString());
            scheduleTable.child(schedule.code).setValue(new_schedule);
            FcmNotificationSender.sendNotification(team_code, team_name, "Зміни у графіку " + schedule.name);
            dialog.dismiss();
        });
        button_close_dialog.setOnClickListener(v -> dialog.dismiss());
    }

    private void InitDialogCreateSchedule(View view, Schedule schedule){
        member_name=view.findViewById(R.id.member_schedule_name);
        button_save_schedule=view.findViewById(R.id.button_save_schedule);
        button_close_dialog=view.findViewById(R.id.button_close_create_schedule);
        days_for_member=new ArrayList<>();
        days_for_member.add(view.findViewById(R.id.monday_date));
        days_for_member.add(view.findViewById(R.id.tuesday_date));
        days_for_member.add(view.findViewById(R.id.wednesday_date));
        days_for_member.add(view.findViewById(R.id.thursday_date));
        days_for_member.add(view.findViewById(R.id.friday_date));
        days_for_member.add(view.findViewById(R.id.saturday_date));
        days_for_member.add(view.findViewById(R.id.sunday_date));
        time_monday=view.findViewById(R.id.monday_work_time);
        time_tuesday=view.findViewById(R.id.tuesday_work_time);
        time_wednesday=view.findViewById(R.id.wednesday_work_time);
        time_thursday=view.findViewById(R.id.thursday_work_time);
        time_friday=view.findViewById(R.id.friday_work_time);
        time_saturday=view.findViewById(R.id.saturday_work_time);
        time_sunday=view.findViewById(R.id.sunday_work_time);
        member_name.setText(schedule.name);
        time_monday.setText(schedule.monday);
        time_tuesday.setText(schedule.tuesday);
        time_wednesday.setText(schedule.wednesday);
        time_thursday.setText(schedule.thursday);
        time_friday.setText(schedule.friday);
        time_saturday.setText(schedule.saturday);
        time_sunday.setText(schedule.sunday);
    }

    private void InitDates(ArrayList<TextView> days){
        Calendar calendar=Calendar.getInstance();
        int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK);
        while(dayOfWeek>1){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            dayOfWeek--;
        }
        for (TextView day: days) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            currentDate=calendar.getTime();
            dateFormat=new SimpleDateFormat("dd.MM", Locale.getDefault());
            day.setText(dateFormat.format(currentDate));
        }
    }
}
