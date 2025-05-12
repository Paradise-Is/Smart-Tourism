package com.example.smarttourism.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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

public class SightCommentActivity extends Activity {
    //用户用户名
    private String username;
    //景点id
    private int sight_id;
    private ImageView backBt;
    private EditText textEt;
    private TextView countText;
    private Button submitBt;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建景点评论编写界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sight_comment);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        textEt = (EditText) this.findViewById(R.id.wordEt);
        countText = (TextView) this.findViewById(R.id.wordCountTv);
        submitBt = (Button) this.findViewById(R.id.submitBt);
        //获取用户用户名和景点id
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        sight_id = intent.getIntExtra("sight_id", 1);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //计算评论字数
        textEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countText.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        submitBt.setOnClickListener(new SubmitBtListener());
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class SubmitBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String text = textEt.getText().toString();
            if (text.equals("")) {
                Toast.makeText(SightCommentActivity.this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
            } else {
                //构造投诉记录的各字段数据
                ContentValues values = new ContentValues();
                values.put("comment_username", username);
                values.put("sight_id", sight_id);
                values.put("comment_text", text);
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                values.put("comment_date", currentDate);
                //添加投诉记录
                long result = dbHelper.getDatabase().insert("SightComment", null, values);
                if (result != -1) {
                    Toast.makeText(SightCommentActivity.this, "评论成功，感谢您对景区的评价", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SightCommentActivity.this, "出错了，评论失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
