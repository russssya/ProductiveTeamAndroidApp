package com.example.productiveteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityPrimary extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText edit_email_enter, edit_pass_enter,edit_name,edit_email_reg,edit_pass_reg;
    Button button_registration,button_register,button_enter;
    LinearLayout enter_form,reg_form;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_primary);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Init1();
    }

    private void Init1(){
        enter_form=findViewById(R.id.enter_form);
        reg_form=findViewById(R.id.registration_form);
        edit_email_enter=findViewById(R.id.edit_email_enter);
        edit_pass_enter=findViewById(R.id.edit_pass_enter);
        button_enter=findViewById(R.id.enter_button);
        button_registration=findViewById(R.id.registration_button);
        mAuth=FirebaseAuth.getInstance();

        button_registration.setOnClickListener(v ->{
            enter_form.setVisibility(View.INVISIBLE);
            reg_form.setVisibility(View.VISIBLE);
            Init2();
        });

        button_enter.setOnClickListener(v ->{
            String email=edit_email_enter.getText().toString();
            String pass=edit_pass_enter.getText().toString();
            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(ActivityPrimary.this,"Success",Toast.LENGTH_SHORT).show();
                    StartMainActivity();
                }
                else{
                    Toast.makeText(ActivityPrimary.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void Init2(){
        edit_name=findViewById(R.id.edit_name_reg);
        edit_email_reg=findViewById(R.id.edit_email_reg);
        edit_pass_reg=findViewById(R.id.edit_pass_reg);
        button_register=findViewById(R.id.signUp_button);
        mDatabase= FirebaseDatabase.getInstance().getReference("Users");

        button_register.setOnClickListener(v ->{
            String name=edit_name.getText().toString();
            String email=edit_email_reg.getText().toString();
            String pass=edit_pass_reg.getText().toString();
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(ActivityPrimary.this,"Success",Toast.LENGTH_SHORT).show();
                    User user=new User(name,email);
                    FirebaseUser currentUser=mAuth.getCurrentUser();
                    assert currentUser != null;
                    mDatabase.child(currentUser.getUid()).child("User").setValue(user);
                    StartMainActivity();
                }
                else{
                    Toast.makeText(ActivityPrimary.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void StartMainActivity(){
        Intent intent=new Intent();
        intent.setClass(ActivityPrimary.this, ActivityMain.class);
        startActivity(intent);
    }

}
