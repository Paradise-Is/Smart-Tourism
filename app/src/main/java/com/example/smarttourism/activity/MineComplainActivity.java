package com.example.smarttourism.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

public class MineComplainActivity extends Activity {
    //用户用户名
    private String username;
    private ImageView backBt;
    private DBHelper dbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建用户投诉界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_complain);
        //获取组件
        //实现数据库功能
        dbHelper = new DBHelper(this);
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
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
