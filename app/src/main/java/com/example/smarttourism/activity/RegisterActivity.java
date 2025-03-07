package com.example.smarttourism.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.smarttourism.R;

public class RegisterActivity extends Activity {
    private TextView backBt;
    private EditText emailText;
    private EditText usernameText;
    private EditText passwordText;
    private EditText rePasswordText;
    private Button registerBt;
    private ImageView ifPwdShow1;
    private ImageView ifPwdShow2;
    //输入框密码是否是显示，默认为false
    private boolean isHide1 = false;
    private boolean isHide2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建登录界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        //获取组件
        backBt=(TextView)this.findViewById(R.id.backBt);
        emailText=(EditText)this.findViewById(R.id.email);
        usernameText=(EditText)this.findViewById(R.id.username);
        passwordText=(EditText)this.findViewById(R.id.password);
        rePasswordText=(EditText)this.findViewById(R.id.re_password);
        registerBt=(Button)this.findViewById(R.id.registerBt);
        //实现密码显示与隐藏
        ifPwdShow1=findViewById(R.id.hide_or_display_a);
        ifPwdShow2=findViewById(R.id.hide_or_display_b);
        ifPwdShow1.setImageResource(R.mipmap.password_hide);
        ifPwdShow2.setImageResource(R.mipmap.password_hide);
        //动态修改TransformationMethod实现密码是否可见
        ifPwdShow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isHide1) {
                    ifPwdShow1.setImageResource(R.mipmap.password_display);
                    isHide1 = true;
                    //密码可见
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    ifPwdShow1.setImageResource(R.mipmap.password_hide);
                    isHide1 = false;
                    //密码不可见
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        ifPwdShow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isHide2) {
                    ifPwdShow2.setImageResource(R.mipmap.password_display);
                    isHide2 = true;
                    //密码可见
                    rePasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    ifPwdShow2.setImageResource(R.mipmap.password_hide);
                    isHide2 = false;
                    //密码不可见
                    rePasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        //返回按钮响应
        backBt.setOnClickListener(new BackBtLister());
        //管理员登录按钮响应
        registerBt.setOnClickListener(new RegisterBtLister());
    }

    private class BackBtLister implements View.OnClickListener{
        @Override
        public void onClick(View v){
            //实现监听按钮，点击跳转
            Intent intent = new Intent();
            //前面为目前页面，后面为要跳转的下一个页面
            intent.setClass(RegisterActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }

    private class RegisterBtLister implements View.OnClickListener{
        @Override
        public void onClick(View v){

        }
    }
}
