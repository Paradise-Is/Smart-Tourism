package com.example.smarttourism.entity;

public class Docent {
    private int id;
    //讲解员姓名
    private String docent_name;
    //讲解员性别
    private String docent_gender;
    //讲解员年龄
    private String docent_age;
    //讲解员照片
    private String docent_photo;
    //讲解员电话号码
    private String docent_phone;

    public Docent(int id, String docent_name, String docent_gender, String docent_age, String docent_photo, String docent_phone) {
        this.id = id;
        this.docent_name = docent_name;
        this.docent_gender = docent_gender;
        this.docent_age = docent_age;
        this.docent_photo = docent_photo;
        this.docent_phone = docent_phone;
    }

    public int getId() {
        return id;
    }

    public String getDocent_name() {
        return docent_name;
    }

    public String getDocent_gender() {
        return docent_gender;
    }

    public String getDocent_age() {
        return docent_age;
    }

    public String getDocent_photo() {
        return docent_photo;
    }

    public String getDocent_phone() {
        return docent_phone;
    }
}
