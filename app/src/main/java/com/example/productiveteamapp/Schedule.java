package com.example.productiveteamapp;

public class Schedule {
    public String name, code, monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    public Schedule(String name, String code, String monday, String tuesday, String wednesday, String thursday, String friday,
                    String saturday, String sunday){
        this.name=name;
        this.code=code;
        this.monday=monday;
        this.tuesday=tuesday;
        this.wednesday=wednesday;
        this.thursday=thursday;
        this.friday=friday;
        this.saturday=saturday;
        this.sunday=sunday;
    }

    public Schedule(String name, String code){
        this.name=name;
        this.code=code;
        monday="none";
        tuesday="none";
        wednesday="none";
        thursday="none";
        friday="none";
        saturday="none";
        sunday="none";
    }

    public Schedule(){}
}
