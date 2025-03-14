package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

public class LoginActivity extends Activity {
    private EditText usernameText;
    private EditText passwordText;
    private Button loginBt;
    private TextView adminLoginBt;
    private TextView registerBt;
    private TextView forgetBt;
    private ImageView ifPwdShow;
    //输入框密码是否是隐藏，默认为true
    private boolean isHide = false;
    private DBHelper dbHelper;

    //App启动执行
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建登录界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //获取组件
        usernameText=(EditText)this.findViewById(R.id.username);
        passwordText=(EditText)this.findViewById(R.id.password);
        loginBt=(Button)this.findViewById(R.id.loginBt);
        adminLoginBt=(TextView)this.findViewById(R.id.adminLoginBt);
        registerBt=(TextView)this.findViewById(R.id.registerBt);
        forgetBt=(TextView)this.findViewById(R.id.forgetBt);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(this);
        dbHelper.open();
        //实现密码显示与隐藏
        ifPwdShow=findViewById(R.id.hide_or_display);
        ifPwdShow.setImageResource(R.mipmap.password_hide);
        //动态修改TransformationMethod实现密码是否可见
        ifPwdShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isHide) {
                    ifPwdShow.setImageResource(R.mipmap.password_display);
                    isHide = true;
                    //密码可见
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    ifPwdShow.setImageResource(R.mipmap.password_hide);
                    isHide = false;
                    //密码不可见
                    passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        //登录账号按钮响应
        loginBt.setOnClickListener(new LoginBtListener());
        //管理员登录按钮响应
        adminLoginBt.setOnClickListener(new AdminLoginBtListener());
        //立即注册按钮响应
        registerBt.setOnClickListener(new RegisterBtListener());
        //忘记密码按钮响应
        forgetBt.setOnClickListener(new ForgetBtListener());
    }

    private class LoginBtListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String username=usernameText.getText().toString();
            String password=passwordText.getText().toString();
            //实现登录逻辑
            if(username.equals("")){
                Toast.makeText(LoginActivity.this, "用户名输入不能为空", Toast.LENGTH_SHORT).show();
            }
            else if(password.equals("")){
                Toast.makeText(LoginActivity.this, "密码输入不能为空", Toast.LENGTH_SHORT).show();
            }
            else{
                String selection = "username=?";
                String[] selectionArgs = {username};
                //查询对应项
                Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
                //游标移动进行校验
                if(cursor.moveToNext()) {
                    //从数据库获取密码进行校验
                    @SuppressLint("Range")
                    String dbPassword = cursor.getString(cursor.getColumnIndex("password"));
                    //关闭游标
                    cursor.close();
                    if(password.equals(dbPassword)) {
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        //携带用户名数据跳转
                        Intent intent = new Intent();
                        intent.putExtra("username",username);
                        intent.setClass(LoginActivity.this,UserMainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class AdminLoginBtListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //实现监听按钮，点击跳转
            Intent intent = new Intent();
            //前面为目前页面，后面为要跳转的下一个页面
            intent.setClass(LoginActivity.this,AdminLoginActivity.class);
            startActivity(intent);
        }
    }

    private class RegisterBtListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //实现监听按钮，点击跳转
            Intent intent = new Intent();
            //前面为目前页面，后面为要跳转的下一个页面
            intent.setClass(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
    }

    private class ForgetBtListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //实现监听按钮，点击跳转
            Intent intent = new Intent();
            //前面为目前页面，后面为要跳转的下一个页面
            intent.setClass(LoginActivity.this,ForgetActivity.class);
            startActivity(intent);
        }
    }
}
