package com.example.smarttourism.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.smarttourism.R;

public class MineComplainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建用户投诉界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_complain);
    }
}
