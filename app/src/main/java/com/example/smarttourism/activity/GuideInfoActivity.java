package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

import java.io.File;

public class GuideInfoActivity extends Activity {
    //用户用户名
    private String username;
    private int id;
    private ImageView backBt;
    private ImageView userHeadshotImg;
    private TextView usernameText;
    private TextView titleText;
    private TextView dateText;
    private TextView contentText;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建编写攻略界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_info);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        userHeadshotImg = (ImageView) this.findViewById(R.id.userHeadshot);
        usernameText = (TextView) this.findViewById(R.id.username);
        titleText = (TextView) this.findViewById(R.id.title);
        dateText = (TextView) this.findViewById(R.id.date);
        contentText = (TextView) this.findViewById(R.id.content);
        //实现数据库功能
        dbHelper = new DBHelper(this);
        dbHelper.open();
        //获取用户用户名和id
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        id = intent.getIntExtra("id", 1);
        //获取攻略内容
        String guide_id = String.valueOf(id);
        String selection = "id=?";
        String[] selectionArgs = {guide_id};
        Cursor cursor = dbHelper.getDatabase().query("Guide", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range")
            String dbTitle = cursor.getString(cursor.getColumnIndex("guide_title"));
            titleText.setText(dbTitle);
            @SuppressLint("Range")
            String dbContent = cursor.getString(cursor.getColumnIndex("guide_content"));
            contentText.setText(dbContent);
            @SuppressLint("Range")
            String dbDate = cursor.getString(cursor.getColumnIndex("guide_date"));
            dateText.setText(dbDate);
            @SuppressLint("Range")
            String dbUsername = cursor.getString(cursor.getColumnIndex("guide_username"));
            //将相关信息显示到界面上
            String userSelection = "username=?";
            String[] userSelectionArgs = {dbUsername};
            //根据用户查询到对应项
            Cursor userCursor = dbHelper.getDatabase().query("User", null, userSelection, userSelectionArgs, null, null, null);
            if (userCursor.moveToNext()) {
                //从数据库获取数据进行界面显示
                @SuppressLint("Range")
                String dbNickname = userCursor.getString(userCursor.getColumnIndex("nickname"));
                usernameText.setText(dbNickname);
                @SuppressLint("Range")
                String dbHeadshot = userCursor.getString(userCursor.getColumnIndex("headshot"));
                //将头像数据显示到界面中
                if (dbHeadshot != null && !dbHeadshot.isEmpty()) {
                    File imgFile = new File(dbHeadshot);
                    if (imgFile.exists()) {
                        //删除旧图片的缓存
                        userHeadshotImg.setImageDrawable(null);
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        userHeadshotImg.setImageBitmap(bitmap);
                    } else {
                        //数据库中无头像数据时显示默认头像
                        userHeadshotImg.setImageResource(R.mipmap.mine_headshot);
                    }
                } else {
                    userHeadshotImg.setImageResource(R.mipmap.mine_headshot);
                }
            }
            userCursor.close();
        }
        cursor.close();
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
