package com.example.smarttourism.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.smarttourism.R;

public class GuideAddActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建编写攻略界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_add);
    }
}
