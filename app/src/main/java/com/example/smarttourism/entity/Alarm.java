package com.example.smarttourism.entity;

public class Alarm {
    private int id;
    //报警者用户名
    private String alarm_username;
    //报警时间
    private String alarm_date;
    //报警方位
    private Double alarm_latitude;
    private Double alarm_longitude;

    public Alarm(int id, String alarm_username, String alarm_date, Double alarm_latitude, Double alarm_longitude) {
        this.id = id;
        this.alarm_username = alarm_username;
        this.alarm_date = alarm_date;
        this.alarm_latitude = alarm_latitude;
        this.alarm_longitude = alarm_longitude;
    }

    public int getId() {
        return id;
    }

    public String getAlarm_username() {
        return alarm_username;
    }

    public String getAlarm_date() {
        return alarm_date;
    }

    public Double getAlarm_latitude() {
        return alarm_latitude;
    }

    public Double getAlarm_longitude() {
        return alarm_longitude;
    }
}
