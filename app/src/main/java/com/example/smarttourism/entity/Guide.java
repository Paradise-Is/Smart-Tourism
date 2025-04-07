package com.example.smarttourism.entity;

public class Guide {
    private int id;
    //攻略标题
    private String guide_title;
    //攻略内容
    private String guide_content;
    //攻略发布时间
    private String guide_date;
    //攻略发布者用户名
    private String guide_username;

    public Guide(int id, String guide_title, String guide_content, String guide_date, String guide_username) {
        this.id = id;
        this.guide_title = guide_title;
        this.guide_content = guide_content;
        this.guide_date = guide_date;
        this.guide_username = guide_username;
    }

    public int getId() {
        return id;
    }

    public String getGuide_title() {
        return guide_title;
    }

    public String getGuide_content() {
        return guide_content;
    }

    public String getGuide_date() {
        return guide_date;
    }

    public String getGuide_username() {
        return guide_username;
    }

}
