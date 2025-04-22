package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

public class ForgetActivity extends Activity {
    private TextView backBt;
    private EditText emailText;
    private Button forgetBt;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建找回密码界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);
        //获取组件
        backBt = (TextView) this.findViewById(R.id.backBt);
        emailText = (EditText) this.findViewById(R.id.email);
        forgetBt = (Button) this.findViewById(R.id.forgetBt);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(this);
        dbHelper.open();
        //返回按钮响应
        backBt.setOnClickListener(new BackBtLister());
        //找回密码按钮响应
        forgetBt.setOnClickListener(new ForgetBtLister());
    }

    private class BackBtLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //实现监听按钮，点击跳转
            Intent intent = new Intent();
            //前面为目前页面，后面为要跳转的下一个页面
            intent.setClass(ForgetActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        }
    }

    private class ForgetBtLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String email = emailText.getText().toString();
            String selection = "email=?";
            String[] selectionArgs = {email};
            //查询邮箱是否存在
            Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
            if (email.equals("")) {
                Toast.makeText(ForgetActivity.this, "邮箱输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (cursor.getCount() == 0) {
                Toast.makeText(ForgetActivity.this, "该邮箱未被注册过", Toast.LENGTH_SHORT).show();
            } else {
                //游标移动进行校验
                if (cursor.moveToNext()) {
                    //从数据库中获取用户名
                    @SuppressLint("Range")
                    String dbUsername = cursor.getString(cursor.getColumnIndex("username"));
                    //关闭游标
                    cursor.close();
                    ContentValues values = new ContentValues();
                    values.put("password", "123456");
                    dbHelper.getDatabase().update("User", values, "username = ?", new String[]{dbUsername});
                    Toast.makeText(ForgetActivity.this, "您的用户名为" + dbUsername + "，密码已重置为123456，请返回登录", Toast.LENGTH_SHORT).show();
                    //跳转回登录页面
                    Intent intent = new Intent();
                    intent.setClass(ForgetActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        }
    }
}
