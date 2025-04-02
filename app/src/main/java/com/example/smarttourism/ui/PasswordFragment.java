package com.example.smarttourism.ui;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.activity.AdminLoginActivity;
import com.example.smarttourism.util.DBHelper;

public class PasswordFragment extends Fragment {
    //管理员用户名
    private String username;
    private EditText oldPasswordText;
    private EditText newPasswordText;
    private EditText renewPasswordText;
    private Button editPasswordBt;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_password, container, false);
        //获取组件
        oldPasswordText = (EditText) view.findViewById(R.id.old_password);
        newPasswordText = (EditText) view.findViewById(R.id.new_password);
        renewPasswordText = (EditText) view.findViewById(R.id.renew_password);
        editPasswordBt = (Button) view.findViewById(R.id.editPasswordBt);
        //实现数据库功能
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        //获取从Activity传来的数据
        Bundle args = getArguments();
        username = args.getString("username");
        //实现按钮点击响应
        editPasswordBt.setOnClickListener(new EditPasswordBtListener());
        return view;
    }

    private class EditPasswordBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String oldPassword = oldPasswordText.getText().toString();
            String newPassword = newPasswordText.getText().toString();
            String renewPassword = renewPasswordText.getText().toString();
            if (oldPassword.isEmpty()) {
                Toast.makeText(getActivity(), "旧密码输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (newPassword.isEmpty()) {
                Toast.makeText(getActivity(), "新密码输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (renewPassword.isEmpty()) {
                Toast.makeText(getActivity(), "再次确认密码输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(renewPassword)) {
                Toast.makeText(getActivity(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            } else {
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
                    if (oldPassword.equals(dbPassword)) {
                        //修改密码
                        ContentValues values = new ContentValues();
                        values.put("password", newPassword);
                        dbHelper.getDatabase().update("Admin", values, "username = ?", new String[]{username});
                        Toast.makeText(getActivity(), "修改密码成功，请返回登录", Toast.LENGTH_SHORT).show();
                        //跳转回管理员登录界面
                        Intent intent = new Intent(getActivity(), AdminLoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "密码输入不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "密码输入不正确", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
