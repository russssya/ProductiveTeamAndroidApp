package com.example.productiveteamapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityOneTask extends AppCompatActivity {
    TextView view_task_name, view_task_date, view_team_name;
    Button but_execute, but_move_task;
    String task_name, task_date, task_code, team_name, team_code;
    ImageButton button_back, button_popup;
    DatabaseReference tasksTable, taskTable;
    Date currentDate;
    DateFormat dateFormat;
    String dateAnother;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_task);
        Init();
    }

    private void Init(){
        view_task_name=findViewById(R.id.task_name_activity);
        view_task_date=findViewById(R.id.task_date_activity);
        view_team_name=findViewById(R.id.team_task_activity);
        but_execute=findViewById(R.id.button_task_done);
        but_move_task=findViewById(R.id.button_move_task);
        button_back=findViewById(R.id.button_back_tasks);
        button_popup=findViewById(R.id.popup_one_task);

        Intent intent=getIntent();
        assert intent!=null;
        task_name=intent.getStringExtra(Constants.TASK_NAME);
        task_date=intent.getStringExtra(Constants.TASK_DATE);
        task_code=intent.getStringExtra(Constants.TASK_CODE);
        team_name=intent.getStringExtra(Constants.TEAM_NAME);
        team_code=intent.getStringExtra(Constants.TEAM_CODE);

        tasksTable=FirebaseDatabase.getInstance().getReference("Teams").child(team_code).child("Tasks");
        taskTable=FirebaseDatabase.getInstance().getReference("Teams").child(team_code).child("Tasks")
                        .child(task_code);

        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        currentDate=calendar.getTime();
        dateFormat=new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateAnother=dateFormat.format(currentDate);

        view_task_name.setText(task_name);
        view_task_date.setText(task_date);
        view_team_name.setText(team_name);

        button_back.setOnClickListener(v -> finish());
        but_execute.setOnClickListener(v -> executeTask());
        but_move_task.setOnClickListener(v -> moveTaskToAnotherDate());
        button_popup.setOnClickListener(v -> showPopup(button_popup));
    }

    private void executeTask(){
        taskTable.child("done").setValue(true);
        Toast.makeText(this, "Завдання виконано", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void moveTaskToAnotherDate(){
        Calendar cal=Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH);
        int mDay = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog=new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    cal.set(year, month, dayOfMonth);
                    dateAnother = dateFormat.format(cal.getTime());
                    taskTable.child("date").setValue(dateAnother);
                    view_task_date.setText(dateAnother);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showDeleteDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityOneTask.this);
        builder.setMessage(R.string.sure_delete_task)
                .setPositiveButton(R.string.yes, (dialog, which) ->
                        tasksTable.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.child(task_code).getRef().removeValue();
                                finish();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        }))
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    @SuppressLint("NonConstantResourceId")
    private void showPopup(View view){
        PopupMenu popupMenu=new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.delete_task:
                    showDeleteDialog();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.inflate(R.menu.popup_task);
        popupMenu.show();
    }
}
