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

public class MinePasswordActivity extends Activity {
    //管理员用户名
    private String username;
    private TextView backBt;
    private EditText oldPasswordText;
    private EditText newPasswordText;
    private EditText renewPasswordText;
    private Button editPasswordBt;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建修改用户密码界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_password);
        //获取组件
        backBt = (TextView) this.findViewById(R.id.backBt);
        oldPasswordText = (EditText) this.findViewById(R.id.old_password);
        newPasswordText = (EditText) this.findViewById(R.id.new_password);
        renewPasswordText = (EditText) this.findViewById(R.id.renew_password);
        editPasswordBt = (Button) this.findViewById(R.id.editPasswordBt);
        //实现数据库功能
        dbHelper = new DBHelper(this);
        dbHelper.open();
        //获取管理员用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //返回按钮响应
        backBt.setOnClickListener(new BackBtLister());
        //修改密码按钮响应
        editPasswordBt.setOnClickListener(new EditPasswordBtListener());

    }

    private class BackBtLister implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class EditPasswordBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String oldPassword = oldPasswordText.getText().toString();
            String newPassword = newPasswordText.getText().toString();
            String renewPassword = renewPasswordText.getText().toString();
            if (oldPassword.isEmpty()) {
                Toast.makeText(MinePasswordActivity.this, "旧密码输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (newPassword.isEmpty()) {
                Toast.makeText(MinePasswordActivity.this, "新密码输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (renewPassword.isEmpty()) {
                Toast.makeText(MinePasswordActivity.this, "再次确认密码输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(renewPassword)) {
                Toast.makeText(MinePasswordActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            } else {
                String selection = "username=?";
                String[] selectionArgs = {username};
                //查询对应项
                Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
                //游标移动进行校验
                if (cursor.moveToNext()) {
                    //从数据库获取密码进行校验
                    @SuppressLint("Range")
                    String dbPassword = cursor.getString(cursor.getColumnIndex("password"));
                    //关闭游标
                    cursor.close();
                    if (oldPassword.equals(dbPassword)) {
                        //修改密码
                        ContentValues values = new ContentValues();
                        values.put("password", newPassword);
                        dbHelper.getDatabase().update("User", values, "username = ?", new String[]{username});
                        Toast.makeText(MinePasswordActivity.this, "修改密码成功，请返回登录", Toast.LENGTH_SHORT).show();
                        //跳转回用户登录界面
                        Intent intent = new Intent(MinePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MinePasswordActivity.this, "密码输入不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MinePasswordActivity.this, "密码输入不正确", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
