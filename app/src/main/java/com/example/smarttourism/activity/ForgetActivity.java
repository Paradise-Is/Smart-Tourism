package com.example.smarttourism.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smarttourism.R;

public class ForgetActivity extends Activity {
    private TextView backBt;
    private EditText emailText;
    private Button forgetBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建登录界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);
        //获取组件
        backBt=(TextView)this.findViewById(R.id.backBt);
        emailText=(EditText)this.findViewById(R.id.email);
        forgetBt=(Button)this.findViewById(R.id.forgetBt);
        //返回按钮响应
        backBt.setOnClickListener(new BackBtLister());
        //管理员登录按钮响应
        forgetBt.setOnClickListener(new ForgetBtLister());
    }

    private class BackBtLister implements View.OnClickListener{
        @Override
        public void onClick(View v){
            //实现监听按钮，点击跳转
            Intent intent = new Intent();
            //前面为目前页面，后面为要跳转的下一个页面
            intent.setClass(ForgetActivity.this,AdminLoginActivity.class);
            startActivity(intent);
        }
    }

    private class ForgetBtLister implements View.OnClickListener{
        @Override
        public void onClick(View v){

        }
    }
}
