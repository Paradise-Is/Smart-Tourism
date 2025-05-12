package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

import java.io.File;

public class UserInfoActivity extends Activity {
    //用户用户名
    private String username;
    private ImageView backBt;
    private ImageView headshot;
    private TextView nicknameInfo;
    private TextView genderInfo;
    private TextView introductionInfo;
    private TextView birthdayInfo;
    private TextView phoneInfo;
    private TextView emailInfo;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建显示报警用户信息界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        headshot = (ImageView) this.findViewById(R.id.headshot);
        nicknameInfo = (TextView) this.findViewById(R.id.nicknameInfo);
        genderInfo = (TextView) this.findViewById(R.id.genderInfo);
        introductionInfo = (TextView) this.findViewById(R.id.introductionInfo);
        birthdayInfo = (TextView) this.findViewById(R.id.birthdayInfo);
        phoneInfo = (TextView) this.findViewById(R.id.phoneInfo);
        emailInfo = (TextView) this.findViewById(R.id.emailInfo);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //将相关信息显示到界面上
        String selection = "username=?";
        String[] selectionArgs = {username};
        //根据用户查询到对应项
        Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            //从数据库获取数据进行界面显示
            @SuppressLint("Range")
            String dbHeadshot = cursor.getString(cursor.getColumnIndex("headshot"));
            //将头像数据显示到界面中
            if (dbHeadshot != null && !dbHeadshot.isEmpty()) {
                File imgFile = new File(dbHeadshot);
                if (imgFile.exists()) {
                    //删除旧图片的缓存
                    headshot.setImageDrawable(null);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    headshot.setImageBitmap(bitmap);
                } else {
                    //数据库中无头像数据时显示默认头像
                    headshot.setImageResource(R.mipmap.mine_headshot);
                }
            } else {
                headshot.setImageResource(R.mipmap.mine_headshot);
            }
            @SuppressLint("Range")
            String dbNickname = cursor.getString(cursor.getColumnIndex("nickname"));
            //将昵称数据显示到界面中
            if (dbNickname != null && !dbNickname.isEmpty()) {
                nicknameInfo.setText(dbNickname);
            }
            @SuppressLint("Range")
            String dbGender = cursor.getString(cursor.getColumnIndex("gender"));
            //将性别数据显示到界面中
            if (dbGender != null && !dbGender.isEmpty()) {
                genderInfo.setText(dbGender);
            }
            @SuppressLint("Range")
            String dbIntroduction = cursor.getString(cursor.getColumnIndex("introduction"));
            //将简介数据显示到界面中
            if (dbIntroduction != null && !dbIntroduction.isEmpty()) {
                introductionInfo.setText(dbIntroduction);
            }
            @SuppressLint("Range")
            String dbBirthday = cursor.getString(cursor.getColumnIndex("birthday"));
            //将生日数据显示到界面中
            if (dbBirthday != null && !dbBirthday.isEmpty()) {
                birthdayInfo.setText(dbBirthday);
            }
            @SuppressLint("Range")
            String dbPhone = cursor.getString(cursor.getColumnIndex("phone"));
            //将电话数据显示到界面中
            if (dbPhone != null && !dbPhone.isEmpty()) {
                phoneInfo.setText(dbPhone);
            }
            @SuppressLint("Range")
            String dbEmail = cursor.getString(cursor.getColumnIndex("email"));
            //将邮箱数据显示到界面中
            if (dbEmail != null && !dbEmail.isEmpty()) {
                emailInfo.setText(dbEmail);
            }
        } else {
            Toast.makeText(UserInfoActivity.this, "系统出错了┭┮﹏┭┮", Toast.LENGTH_SHORT).show();
        }
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }
}
