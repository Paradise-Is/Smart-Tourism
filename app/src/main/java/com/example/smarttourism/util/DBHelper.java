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
    //创建用户信息表（用户名，密码，电子邮箱，头像，昵称，性别，电话，个人简介，生日）
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
    //创建管理员信息表（用户名，密码）
    private static final String CREATE_Admin = "create table if not exists Admin("
            + "username text primary key,"
            + "password text)";
    //创建投诉记录表（id，投诉者用户名，投诉类型，投诉内容，投诉日期，投诉者联系方式，投诉状态）
    private static final String CREATE_Complaint = "create table if not exists Complaint("
            + "id integer primary key autoincrement, "
            + "complaint_username text not null, "
            + "complaint_type text not null, "
            + "complaint_content text not null, "
            + "complaint_date text not null, "
            + "complaint_contact text not null, "
            + "status text not null);";
    //创建报警记录表（id，报警者用户名，报警日期，报警位置纬度，报警位置经度）
    private static final String CREATE_Alarm = "create table if not exists Alarm("
            + "id integer primary key autoincrement, "
            + "alarm_username text not null, "
            + "alarm_date text not null,"
            + "alarm_latitude text, "
            + "alarm_longitude text); ";
    //创建攻略内容表（id，攻略标题，攻略内容，攻略发布日期，攻略发布者用户名）
    private static final String CREATE_Guide = "create table if not exists Guide("
            + "id integer primary key autoincrement, "
            + "guide_title text not null, "
            + "guide_content text not null, "
            + "guide_date text not null, "
            + "guide_username text not null);";
    //创建讲解员信息表（id，讲解员姓名，讲解员性别，讲解员年龄，讲解员照片，讲解员联系方式）
    private static final String CREATE_Docent = "create table if not exists Docent("
            + "id integer primary key autoincrement, "
            + "docent_name text not null, "
            + "docent_gender text not null, "
            + "docent_age text not null, "
            + "docent_photo text not null, "
            + "docent_phone text not null);";
    //创建游览车信息表（id，车牌号，载客量，游览车纬度，游览车经度，游览车状态）
    private static final String CREATE_Coach = "create table if not exists Coach("
            + "id integer primary key autoincrement, "
            + "coach_license text not null, "
            + "coach_capacity text not null, "
            + "gps_latitude text, "
            + "gps_longitude text, "
            + "status text not null);";
    //创建东湖景区景点信息表（id，景点名，景点描述，景点票价，景点纬度，景点经度，景点照片）
    private static final String CREATE_Sight = "create table if not exists Sight("
            + "id integer primary key autoincrement, "
            + "name text not null, "
            + "description text, "
            + "price text, "
            + "latitude text, "
            + "longitude text, "
            + "image text);";
    //创建东湖景区景点评价表（id，评论者用户名，景点标识，评论内容，评论日期）
    private static final String CREATE_SightComment = "create table if not exists SightComment("
            + "comment_id integer primary key autoincrement, "
            + "comment_username text not null, "
            + "sight_id integer not null, "
            + "comment_text text not null, "
            + "comment_date text not null);";
    //创建东湖景区景点票据表（id，购买者用户名，景点标识，购买数量，购买单价，合计金额，购买日期）
    private static final String CREATE_SightPurchase = "create table if not exists SightPurchase("
            + "purchase_id integer primary key autoincrement, "
            + "purchase_username text not null, "
            + "sight_id integer not null, "
            + "quantity text not null, "
            + "price text not null, "
            + "total text not null, "
            + "purchase_date text not null);";
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
        db.execSQL(CREATE_Sight);
        db.execSQL(CREATE_SightComment);
        db.execSQL(CREATE_SightPurchase);
        //初始化创建一个管理员账号
        db.execSQL("insert into Admin(username,password)values('admin','123456')");
        //初始化景区景点信息
        insertSightsInfo(db);
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
        db.execSQL("drop table if exists Sight");
        db.execSQL("drop table if exists SightComment");
        db.execSQL("drop table if exists SightPurchase");
        onCreate(db);
    }

    private void insertSightsInfo(SQLiteDatabase db) {
        db.execSQL("INSERT INTO Sight (name, description, latitude, longitude, image_path) VALUES " +
                "('磨山','东湖北岸核心风景区，有梅园、樱园等','25','30.5411','114.4124','moshan')," +
                "('听涛','紧邻磨山的湖畔步道，环境优雅','35','30.5387','114.4163','tingtao')"
        );
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
