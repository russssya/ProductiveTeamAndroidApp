package com.example.productiveteamapp;

public class Task {
    public String name, date, code, team_name, team_code;
    public boolean done;

    public Task(String name, String date, String code, String team_name, String team_code, boolean done){
        this.name=name;
        this.date=date;
        this.code=code;
        this.team_name=team_name;
        this.team_code=team_code;
        this.done=done;
    }

    public Task(){}
}
