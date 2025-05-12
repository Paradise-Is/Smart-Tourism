package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
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

public class AdminLoginActivity extends Activity {
    private EditText usernameText;
    private EditText passwordText;
    private TextView backBt;
    private Button adminLoginBt;
    private ImageView ifPwdShow;
    //输入框密码是否是隐藏，默认为false(隐藏)
    private boolean isHide = false;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建管理员登录界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);
        //获取组件
        usernameText=(EditText)this.findViewById(R.id.username);
        passwordText=(EditText)this.findViewById(R.id.password);
        backBt=(TextView)this.findViewById(R.id.backBt);
        adminLoginBt=(Button)this.findViewById(R.id.adminLoginBt);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
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
        //返回按钮响应
        backBt.setOnClickListener(new BackBtLister());
        //管理员登录按钮响应
        adminLoginBt.setOnClickListener(new AdminLoginBtLister());
    }

    private class BackBtLister implements View.OnClickListener{
        @Override
        public void onClick(View v){
            //实现监听按钮，点击跳转
            Intent intent = new Intent();
            //前面为目前页面，后面为要跳转的下一个页面
            intent.setClass(AdminLoginActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }

    private class AdminLoginBtLister implements View.OnClickListener{
        @Override
        public void onClick(View v){
            String username=usernameText.getText().toString();
            String password=passwordText.getText().toString();
            //实现登录逻辑
            if(username.equals("")){
                Toast.makeText(AdminLoginActivity.this, "用户名输入不能为空", Toast.LENGTH_SHORT).show();
            }
            else if(password.equals("")){
                Toast.makeText(AdminLoginActivity.this, "密码输入不能为空", Toast.LENGTH_SHORT).show();
            }
            else {
                String selection = "username=?";
                String[] selectionArgs = {username};
                //查询对应项
                Cursor cursor = dbHelper.getDatabase().query("Admin", null, selection, selectionArgs, null, null, null);
                //游标移动进行校验
                if (cursor.moveToNext()) {
                    //从数据库获取密码进行校验
                    @SuppressLint("Range")
                    String dbPassword = cursor.getString(cursor.getColumnIndex("password"));
                    //关闭游标
                    cursor.close();
                    if (password.equals(dbPassword)) {
                        Toast.makeText(AdminLoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        //携带用户名数据跳转
                        Intent intent = new Intent();
                        intent.putExtra("username", username);
                        intent.setClass(AdminLoginActivity.this, AdminMainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AdminLoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminLoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
