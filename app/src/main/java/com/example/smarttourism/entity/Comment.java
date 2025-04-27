package com.example.smarttourism.entity;

public class Comment {
    private int comment_id;
    //评价者用户名
    private String comment_username;
    //评价景点标识
    private int sight_id;
    //评价内容
    private String comment_text;
    //评价时间
    private String comment_date;

    public Comment(int comment_id, String comment_username, int sight_id, String comment_text, String comment_date) {
        this.comment_id = comment_id;
        this.comment_username = comment_username;
        this.sight_id = sight_id;
        this.comment_text = comment_text;
        this.comment_date = comment_date;
    }

    public int getComment_id() {
        return comment_id;
    }

    public String getComment_username() {
        return comment_username;
    }

    public int getSight_id() {
        return sight_id;
    }

    public String getComment_text() {
        return comment_text;
    }

    public String getComment_date() {
        return comment_date;
    }
}
