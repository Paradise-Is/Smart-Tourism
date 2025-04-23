package com.example.smarttourism.entity;

//存储位置信息
public class AddressBean {
    //经度
    private Double longitude;
    //纬度
    private Double latitude;
    //信息标题
    private String title;
    //信息内容
    private String text;

    public AddressBean(Double lon, Double lat, String title, String text) {
        this.longitude = lon;
        this.latitude = lat;
        this.title = title;
        this.text = text;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
