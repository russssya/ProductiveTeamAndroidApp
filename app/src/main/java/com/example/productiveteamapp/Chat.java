package com.example.productiveteamapp;


public class Chat {
    public String chat_name, chat_code, team_name, team_code, date;

    public Chat(String chat_name, String chat_code, String team_name, String team_code, String date){
        this.chat_name=chat_name;
        this.chat_code=chat_code;
        this.team_name=team_name;
        this.team_code=team_code;
        this.date=date;
    }

    public Chat(){}
}
