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
import com.example.smarttourism.activity.LoginActivity;
import com.example.smarttourism.activity.RegisterActivity;
import com.example.smarttourism.util.DBHelper;

import java.util.regex.Pattern;

public class SettingFragment extends Fragment {
    //管理员用户名
    private String username;
    private EditText usernameText;
    private EditText passwordText;
    private Button userSettingBt;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_setting, container, false);
        //获取组件
        usernameText = (EditText) view.findViewById(R.id.username);
        passwordText = (EditText) view.findViewById(R.id.password);
        userSettingBt = (Button) view.findViewById(R.id.userSettingBt);
        //实现数据库功能
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        //获取从Activity传来的数据
        Bundle args = getArguments();
        username = args.getString("username");
        //实现按钮点击响应
        userSettingBt.setOnClickListener(new UserSettingBtListener());
        return view;
    }

    private class UserSettingBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();
            Pattern normalPattern = Pattern.compile("[a-zA-Z0-9]{4,16}$");
            if (username.isEmpty()) {
                Toast.makeText(getActivity(), "账号输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (!normalPattern.matcher(username).matches()) {
                Toast.makeText(getActivity(), "账号不符合规范，要求为4-16位字母或数字组合", Toast.LENGTH_LONG).show();
            } else if (password.equals("")) {
                Toast.makeText(getActivity(), "密码输入不能为空", Toast.LENGTH_SHORT).show();
            } else if (!normalPattern.matcher(password).matches()) {
                Toast.makeText(getActivity(), "密码不符合规范，要求为4-16位字母或数字组合", Toast.LENGTH_LONG).show();
            } else {
                ContentValues values = new ContentValues();
                values.put("username", username);
                values.put("password", password);
                //查询账号是否已经被设置过
                long message = dbHelper.getDatabase().insert("Admin", null, values);
                if (message != -1) {
                    Toast.makeText(getActivity(), "账号设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "该账号已被设置过", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
