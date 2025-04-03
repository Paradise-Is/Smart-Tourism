package com.example.smarttourism.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.smarttourism.R;

public class MineInfoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建显示用户个人信息界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_info);
    }

    private class BackBtLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }
}
