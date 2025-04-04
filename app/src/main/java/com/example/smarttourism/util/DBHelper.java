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
        //初始化创建一个管理员账号
        db.execSQL("insert into Admin(username,password)values('admin','123456')");
    }

    //更新指定用户的头像，并将路径到数据库中
    public void updateUserHeadshot(String username, String headshotPath) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("headshot", headshotPath);
        // 更新User表中 username 对应的记录
        database.update("User", values, "username = ?", new String[]{username});
        database.close();
    }

    //数据库版本更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        db.execSQL("drop table if exists Admin");
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
