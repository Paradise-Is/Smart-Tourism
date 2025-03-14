package com.example.smarttourism.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.smarttourism.R;

public class AdminMainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建用户主页界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);
    }
}
