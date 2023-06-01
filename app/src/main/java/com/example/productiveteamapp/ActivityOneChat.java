package com.example.productiveteamapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productiveteamapp.adapters.ListAdapterMessage;
import com.example.productiveteamapp.notification.FcmNotificationSender;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class ActivityOneChat extends AppCompatActivity {
    ImageButton but_back, but_popup;
    TextView view_chat_name;
    FloatingActionButton but_send;
    EditText edit_message;
    RecyclerView recyclerViewMessages;
    ListAdapterMessage adapterMessage;
    ArrayList<Message> list_messages;
    DatabaseReference chatTable, mDatabase;
    String chat_name, chat_code, team_code, user_name, date;
    DateFormat dateFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_chat);
        Init();
        getMessagesFromDB();
    }

    private void Init(){
        but_back=findViewById(R.id.button_back_chats);
        but_popup=findViewById(R.id.popup_one_chat);
        view_chat_name=findViewById(R.id.title_chat_name);
        but_send=findViewById(R.id.button_send);
        edit_message=findViewById(R.id.edit_new_message);

        recyclerViewMessages=findViewById(R.id.list_of_messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        list_messages=new ArrayList<>();
        adapterMessage=new ListAdapterMessage(this, list_messages);
        recyclerViewMessages.setAdapter(adapterMessage);

        recyclerViewMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        Intent intent=getIntent();
        assert intent!=null;
        chat_name=intent.getStringExtra(Constants.CHAT_NAME);
        chat_code=intent.getStringExtra(Constants.CHAT_CODE);
        team_code=intent.getStringExtra(Constants.TEAM_CODE);
        user_name=intent.getStringExtra(Constants.USER_NAME);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        chatTable=FirebaseDatabase.getInstance().getReference("Teams").child(team_code)
                .child("Chats").child(chat_code);

        dateFormat=new SimpleDateFormat("dd MM yyyy HH:mm", Locale.getDefault());

        view_chat_name.setText(chat_name);
        but_send.setOnClickListener(v -> sendMessage());
        but_back.setOnClickListener(v -> finish());
        but_popup.setOnClickListener(v -> showPopup(but_popup));
    }

    private void getMessagesFromDB(){
        chatTable.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list_messages.size()>0) list_messages.clear();
                for(DataSnapshot ds: snapshot.child("Messages").getChildren()){
                    Message message=ds.getValue(Message.class);
                    list_messages.add(message);
                }
                adapterMessage.notifyDataSetChanged();
                scrollToBottom();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivityOneChat.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToBottom() {
        recyclerViewMessages.scrollToPosition(list_messages.size() - 1);
    }

    private void sendMessage(){
        date=dateFormat.format(new Date());
        String text=edit_message.getText().toString();
        if(!text.isEmpty()){
            Message message=new Message(text, user_name, date);
            chatTable.child("Messages").push().setValue(message);
            edit_message.setText("");
            FcmNotificationSender.sendNotification(team_code, chat_name, user_name + ": " + text);
        }
    }

    private void showDialogClearChat(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityOneChat.this);
        builder.setMessage(R.string.sure_clear_chat)
                .setPositiveButton(R.string.yes, (dialog, which) ->
                        chatTable.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.child("Messages").getRef().removeValue();
                                finish();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ActivityOneChat.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }))
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showDialogDeleteChat(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityOneChat.this);
        builder.setMessage(R.string.sure_delete_chat)
                .setPositiveButton(R.string.yes, (dialog, which) ->
                        chatTable.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeValue();
                                finish();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ActivityOneChat.this, "Error", Toast.LENGTH_SHORT).show();
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
                case R.id.clear_chat:
                    showDialogClearChat();
                    return true;
                case R.id.delete_chat:
                    showDialogDeleteChat();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.inflate(R.menu.popup_chat);
        popupMenu.show();
    }
}
