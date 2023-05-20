package com.example.productiveteamapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.productiveteamapp.fragments.FragmentDiscussing;
import com.example.productiveteamapp.fragments.FragmentSettings;
import com.example.productiveteamapp.fragments.FragmentTasks;
import com.example.productiveteamapp.fragments.FragmentTeams;
import com.example.productiveteamapp.fragments.FragmentSchedule;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private static final String TAG="FCM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, ActivityPrimary.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer=findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this, drawer,toolbar,
                R.string.nav_draw_open, R.string.nav_draw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentTeams()).commit();
            navigationView.setCheckedItem(R.id.nav_teams);
        }

        TextView user_name = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        assert currentUser != null;
        user_name.setText(currentUser.getEmail());

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                Log.d(TAG, "retrieve token successful : " + token);
            } else {
                Log.w(TAG, "token should not be null...");
            }
        });

        boolean areNotificationsEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled();

        if (areNotificationsEnabled) {
            Toast.makeText(ActivityMain.this,"Notifications enabled",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ActivityMain.this,"Notifications didn't enable",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_teams:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentTeams()).commit();
                break;
            case R.id.nav_schedule:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentSchedule()).commit();
                break;
            case R.id.nav_tasks:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentTasks()).commit();
                break;
            case R.id.nav_discussing:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentDiscussing()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentSettings()).commit();
                break;
            case R.id.nav_exit:
                showExitDialog();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    private void showExitDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ActivityMain.this);
        builder.setMessage(R.string.sure_exit)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    mAuth.signOut();
                    Toast.makeText(this, "Вихід", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    intent.setClass(ActivityMain.this, ActivityPrimary.class);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {

                });
        builder.create().show();
    }
}