package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GuideAddActivity extends Activity {
    //用户用户名
    private String username;
    private ImageView backBt;
    private EditText titleText;
    private TextView wordTotalText;
    private EditText contentText;
    private Button publishBt;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建编写攻略界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_add);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        titleText = (EditText) this.findViewById(R.id.titleEt);
        wordTotalText = (TextView) this.findViewById(R.id.wordTotalEt);
        contentText = (EditText) this.findViewById(R.id.contentEt);
        publishBt = (Button) this.findViewById(R.id.publishBt);
        //实现数据库功能
        dbHelper = new DBHelper(this);
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //计算标题剩余字数
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordTotalText.setText(String.valueOf(30 - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        publishBt.setOnClickListener(new PublishBtListener());
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class PublishBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String title = titleText.getText().toString();
            String content = contentText.getText().toString();
            if (title.equals("")) {
                Toast.makeText(GuideAddActivity.this, "攻略标题不能为空", Toast.LENGTH_SHORT).show();
            } else if (content.equals("")) {
                Toast.makeText(GuideAddActivity.this, "攻略内容不能为空", Toast.LENGTH_SHORT).show();
            } else {
                //构造投诉记录的各字段数据
                ContentValues values = new ContentValues();
                values.put("guide_title", title);
                values.put("guide_content", content);
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                values.put("guide_date", currentDate);
                values.put("guide_username", username);
                //发布攻略
                long result = dbHelper.getDatabase().insert("Guide", null, values);
                if (result != -1) {
                    Toast.makeText(GuideAddActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(GuideAddActivity.this, "出错了，发布失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
