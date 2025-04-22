package com.example.smarttourism.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    //数据库名
    private static final String DATABASE_NAME = "SmartTourism.db";
    //数据库版本号
    private static final int DATABASE_VERSION = 1;
    //创建用户信息表
    private static final String CREATE_User = "create table if not exists User("
            + "username text primary key,"
            + "password text,"
            + "email text,"
            + "headshot text,"
            + "nickname text,"
            + "gender text,"
            + "phone text,"
            + "introduction text,"
            + "birthday text)";
    //创建管理员信息表
    private static final String CREATE_Admin = "create table if not exists Admin("
            + "username text primary key,"
            + "password text)";
    //创建投诉记录表
    private static final String CREATE_Complaint = "create table if not exists Complaint("
            + "id integer primary key autoincrement, "
            + "complaint_username text not null, "
            + "complaint_type text not null, "
            + "complaint_content text not null, "
            + "complaint_date text not null, "
            + "complaint_contact text not null, "
            + "status text not null);";
    //创建报警记录表
    private static final String CREATE_Alarm = "create table if not exists Alarm("
            + "id integer primary key autoincrement, "
            + "alarm_username text not null, "
            + "alarm_date text not null,"
            + "alarm_latitude text, "
            + "alarm_longitude text); ";
    //创建攻略内容表
    private static final String CREATE_Guide = "create table if not exists Guide("
            + "id integer primary key autoincrement, "
            + "guide_title text not null, "
            + "guide_content text not null, "
            + "guide_date text not null, "
            + "guide_username text not null);";
    //创建讲解员信息表
    private static final String CREATE_Docent = "create table if not exists Docent("
            + "id integer primary key autoincrement, "
            + "docent_name text not null, "
            + "docent_gender text not null, "
            + "docent_age text not null, "
            + "docent_photo text not null, "
            + "docent_phone text not null);";
    //创建游览车信息表
    private static final String CREATE_Coach = "create table if not exists Coach("
            + "id integer primary key autoincrement, "
            + "coach_license text not null, "
            + "coach_capacity text not null, "
            + "gps_latitude text, "
            + "gps_longitude text, "
            + "status text not null);";
    //数据库操作实例
    private static DBHelper instance;
    private SQLiteDatabase database;

    //构造函数，创建数据库
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //获取数据库操作实例
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    //创建数据库表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_User);
        db.execSQL(CREATE_Admin);
        db.execSQL(CREATE_Complaint);
        db.execSQL(CREATE_Alarm);
        db.execSQL(CREATE_Guide);
        db.execSQL(CREATE_Docent);
        db.execSQL(CREATE_Coach);
        //初始化创建一个管理员账号
        db.execSQL("insert into Admin(username,password)values('admin','123456')");
    }

    //更新指定用户邮箱
    public void updateUserEmail(String username, String newEmail) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", newEmail);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户头像并将图片路径保存到数据库中
    public void updateUserHeadshot(String username, String headshotPath) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("headshot", headshotPath);
        // 更新User表中 username 对应的记录
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户昵称
    public void updateUserNickname(String username, String newNickname) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nickname", newNickname);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户性别
    public void updateUserGender(String username, String newGender) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("gender", newGender);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户电话
    public void updateUserPhone(String username, String newPhone) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", newPhone);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户简介
    public void updateUserIntroduction(String username, String newIntroduction) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("introduction", newIntroduction);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //更新指定用户生日
    public void updateUserBirthday(String username, String newBirthday) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("birthday", newBirthday);
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //数据库版本更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        db.execSQL("drop table if exists Admin");
        db.execSQL("drop table if exists Complaint");
        db.execSQL("drop table if exists Alarm");
        db.execSQL("drop table if exists Guide");
        db.execSQL("drop table if exists Docent");
        db.execSQL("drop table if exists Coach");
        onCreate(db);
    }

    //实现数据库连接
    public void open() {
        //获取可写数据库
        database = this.getWritableDatabase();
    }

    //数据库连接断开
    public void close() {
        if (database != null) {
            database.close();
        }
    }
}
