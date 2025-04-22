package com.example.smarttourism.entity;

public class Coach {
    private int id;
    //游览车车牌号
    private String coach_license;
    //游览车载客量
    private String coach_capacity;
    //当前游览车方位
    private Double gps_latitude;
    private Double gps_longitude;
    //游览车状态（运行中，待命中，维修中）
    private String status;

    public Coach(int id, String coach_license, String coach_capacity, Double gps_latitude, Double gps_longitude,String status) {
        this.id = id;
        this.coach_license = coach_license;
        this.coach_capacity = coach_capacity;
        this.gps_latitude = gps_latitude;
        this.gps_longitude = gps_longitude;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getCoach_license() {
        return coach_license;
    }

    public String getCoach_capacity() {
        return coach_capacity;
    }

    public Double getGps_latitude() {
        return gps_latitude;
    }

    public Double getGps_longitude() {
        return gps_longitude;
    }

    public String getStatus() {
        return status;
    }
}
