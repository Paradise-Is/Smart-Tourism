package com.example.smarttourism.entity;

public class Complaint {
    private int id;
    //投诉者用户名
    private String complaint_username;
    //投诉类型
    private String complaint_type;
    //投诉内容
    private String complaint_content;
    //投诉提交时间
    private String complaint_date;
    //投诉者联系方式
    private String complaint_contact;
    //投诉状态
    private String status;

    public Complaint(int id, String complaint_username, String complaint_type, String complaint_content, String complaint_date, String complaint_contact, String status) {
        this.id = id;
        this.complaint_username = complaint_username;
        this.complaint_type = complaint_type;
        this.complaint_content = complaint_content;
        this.complaint_date = complaint_date;
        this.complaint_contact = complaint_contact;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getComplaint_username() {
        return complaint_username;
    }

    public String getComplaint_type() {
        return complaint_type;
    }

    public String getComplaint_content() {
        return complaint_content;
    }

    public String getComplaint_date() {
        return complaint_date;
    }

    public String getComplaint_contact() {
        return complaint_contact;
    }

    public String getStatus() {
        return status;
    }
}
