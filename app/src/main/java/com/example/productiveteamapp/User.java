package com.example.productiveteamapp;

import com.google.firebase.auth.FirebaseUser;

public class User {

    public String name, email;

    public User(String name, String email){
        this.name=name;
        this.email=email;
    }

    public User(){}
}
