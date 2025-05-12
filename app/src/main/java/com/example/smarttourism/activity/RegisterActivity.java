package com.example.smarttourism.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

import java.util.regex.Pattern;

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
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建注册界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        //获取组件
        backBt = (TextView) this.findViewById(R.id.backBt);
        emailText = (EditText) this.findViewById(R.id.email);
        usernameText = (EditText) this.findViewById(R.id.username);
        passwordText = (EditText) this.findViewById(R.id.password);
        rePasswordText = (EditText) this.findViewById(R.id.re_password);
        registerBt = (Button) this.findViewById(R.id.registerBt);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //实现密码显示与隐藏
        ifPwdShow1 = findViewById(R.id.hide_or_display_a);
        ifPwdShow2 = findViewById(R.id.hide_or_display_b);
        ifPwdShow1.setImageResource(R.mipmap.password_hide);
        ifPwdShow2.setImageResource(R.mipmap.password_hide);
        //动态修改TransformationMethod实现密码是否可见
        ifPwdShow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isHide1) {
                    ifPwdShow1.setImageResource(R.mipmap.password_display);
                    isHide1 = true;
                    //密码可见
                    passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
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
                if (!isHide2) {
                    ifPwdShow2.setImageResource(R.mipmap.password_display);
                    isHide2 = true;
                    //密码可见
                    rePasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
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

    private class BackBtLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //实现监听按钮，点击跳转
            Intent intent = new Intent();
            //前面为目前页面，后面为要跳转的下一个页面
            intent.setClass(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private class RegisterBtLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String email = emailText.getText().toString();
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();
            String rePassword = rePasswordText.getText().toString();
            Pattern normalPattern = Pattern.compile("[a-zA-Z0-9]{4,16}$");
            Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
            String selection = "email=?";
            String[] selectionArgs = {email};
            //查询邮箱是否已经被注册过
            Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
            if (email.equals("")) {
                Toast.makeText(RegisterActivity.this, "邮箱输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (!emailPattern.matcher(email).matches()) {
                Toast.makeText(getApplicationContext(), "无效的邮箱", Toast.LENGTH_LONG).show();
            } else if (cursor.getCount() != 0) {
                Toast.makeText(RegisterActivity.this, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
            } else if (username.equals("")) {
                Toast.makeText(RegisterActivity.this, "用户名输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (!normalPattern.matcher(username).matches()) {
                Toast.makeText(getApplicationContext(), "用户名不符合规范，要求为4-16位字母或数字组合", Toast.LENGTH_LONG).show();
            } else if (password.equals("")) {
                Toast.makeText(RegisterActivity.this, "密码输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (!normalPattern.matcher(password).matches()) {
                Toast.makeText(getApplicationContext(), "密码不符合规范，要求为4-16位字母或数字组合", Toast.LENGTH_LONG).show();
            } else if (rePassword.equals("")) {
                Toast.makeText(RegisterActivity.this, "确认密码输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(rePassword)) {
                Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            } else {
                ContentValues values = new ContentValues();
                values.put("username", username);
                values.put("password", password);
                values.put("email", email);
                values.put("nickname","user_"+username);
                //查询用户名是否已经被注册过
                long message = dbHelper.getDatabase().insert("User", null, values);
                if (message != -1) {
                    Toast.makeText(RegisterActivity.this, "注册成功，请返回登录", Toast.LENGTH_SHORT).show();
                    //跳转回登录页面
                    Intent intent = new Intent();
                    intent.setClass(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "用户已存在", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
