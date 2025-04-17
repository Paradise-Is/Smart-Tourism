package com.example.smarttourism.entity;

//存储位置信息
public class AddressBean {
    //经度
    private double longitude;
    //纬度
    private double latitude;
    //信息标题
    private String title;
    //信息内容
    private String text;

    public AddressBean(double lon, double lat, String title, String text) {
        this.longitude = lon;
        this.latitude = lat;
        this.title = title;
        this.text = text;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
