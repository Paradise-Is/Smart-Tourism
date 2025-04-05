package com.example.smarttourism.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.activity.LoginActivity;
import com.example.smarttourism.activity.MineComplainActivity;
import com.example.smarttourism.activity.MineInfoActivity;
import com.example.smarttourism.activity.MinePasswordActivity;
import com.example.smarttourism.util.DBHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MineFragment extends Fragment {
    //用户用户名
    private String username;
    private ConstraintLayout infoBt;
    private ImageView headshot;
    private TextView tvNickname;
    private TextView tvEmail;
    private LinearLayout complainBt;
    private LinearLayout alarmBt;
    private LinearLayout passwordBt;
    private LinearLayout logoutBt;
    private LinearLayout backSysBt;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_mine, container, false);
        //获取组件
        infoBt = (ConstraintLayout) view.findViewById(R.id.infoBt);
        headshot = (ImageView) view.findViewById(R.id.headshot);
        tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
        tvEmail = (TextView) view.findViewById(R.id.tv_email);
        complainBt = (LinearLayout) view.findViewById(R.id.complainBt);
        alarmBt = (LinearLayout) view.findViewById(R.id.alarmBt);
        passwordBt = (LinearLayout) view.findViewById(R.id.passwordBt);
        logoutBt = (LinearLayout) view.findViewById(R.id.logoutBt);
        backSysBt = (LinearLayout) view.findViewById(R.id.backSysBt);
        //实现数据库功能
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        //获取从Activity传来的数据
        Bundle args = getArguments();
        username = args.getString("username");
        RefreshUserInfo();
        //实现按钮点击响应
        infoBt.setOnClickListener(new InfoBtListener());
        complainBt.setOnClickListener(new ComplainBtListener());
        alarmBt.setOnClickListener(new AlarmBtListener());
        passwordBt.setOnClickListener(new PasswordBtListener());
        logoutBt.setOnClickListener(new LogoutBtListener());
        backSysBt.setOnClickListener(new BackSysBtListener());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshUserInfo();
    }

    //刷新用户信息
    private void RefreshUserInfo() {
        //将用户名，头像及邮箱信息显示到界面上
        String selection = "username=?";
        String[] selectionArgs = {username};
        //根据用户查询到对应项
        Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            //从数据库获取数据进行界面显示
            @SuppressLint("Range")
            String dbEmail = cursor.getString(cursor.getColumnIndex("email"));
            tvEmail.setText(dbEmail);
            @SuppressLint("Range")
            String dbNickname = cursor.getString(cursor.getColumnIndex("nickname"));
            tvNickname.setText(dbNickname);
            @SuppressLint("Range")
            String dbHeadshot = cursor.getString(cursor.getColumnIndex("headshot"));
            //将头像数据显示到界面中
            if (dbHeadshot != null && !dbHeadshot.isEmpty()) {
                File imgFile = new File(dbHeadshot);
                if (imgFile.exists()) {
                    //删除旧图片的缓存
                    headshot.setImageDrawable(null);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    headshot.setImageBitmap(bitmap);
                } else {
                    //数据库中无头像数据时显示默认头像
                    headshot.setImageResource(R.mipmap.mine_headshot);
                }
            } else {
                headshot.setImageResource(R.mipmap.mine_headshot);
            }
        } else {
            Toast.makeText(getActivity(), "系统出错了┭┮﹏┭┮", Toast.LENGTH_SHORT).show();
        }
    }

    private class InfoBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent= new Intent(getActivity(), MineInfoActivity.class);
            intent.putExtra("username",username);
            startActivity(intent);
        }
    }

    private class ComplainBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent= new Intent(getActivity(), MineComplainActivity.class);
            intent.putExtra("username",username);
            startActivity(intent);
        }
    }

    private class AlarmBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //弹出弹窗，确认是否报警
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("是否确认要报警");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ContentValues values = new ContentValues();
                    values.put("alarm_username", username);
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                    values.put("alarm_date", currentDate);
                    values.put("status", "未处理");
                    long result = dbHelper.getDatabase().insert("Alarms", null, values);
                    if (result != -1) {
                        Toast.makeText(getActivity(), "一键报警成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "出错了", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNeutralButton("取消", null);
            builder.show();
        }
    }

    private class PasswordBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent= new Intent(getActivity(), MinePasswordActivity.class);
            intent.putExtra("username",username);
            startActivity(intent);
        }
    }

    private class LogoutBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //弹出弹窗，确认是否注销
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("是否确认要注销账号");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //从表User中删除指定用户的记录
                    dbHelper.getDatabase().delete("User", "username = ? ", new String[]{username});
                    Toast.makeText(getActivity(), "用户已注销", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            builder.setNeutralButton("取消", null);
            builder.show();
        }
    }

    private class BackSysBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "退出登录成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
