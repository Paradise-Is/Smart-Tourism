package com.example.smarttourism.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.ui.GuideFragment;
import com.example.smarttourism.ui.MineFragment;
import com.example.smarttourism.ui.SurroundingsFragment;
import com.example.smarttourism.ui.UserHomeFragment;
import com.example.smarttourism.util.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;

public class UserMainActivity extends AppCompatActivity {
    //底边栏的选项卡视图
    private BottomNavigationView bottomNavigationView;
    //用户用户名
    private String username;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建用户主页界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main);
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //获取组件
        bottomNavigationView = findViewById(R.id.tab_menu_bar);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //访客量统计
        markVisitOncePerDay();
        //默认加载首页
        if (savedInstanceState == null) {
            //创建片段实例
            UserHomeFragment defaultFragment = new UserHomeFragment();
            //构建传递给片段的参数
            Bundle args = new Bundle();
            //将用户名作为参数传递过去，可以通过getArguments()来获取这个传递过来的文本数据
            args.putString("username", username);
            //将参数设置给片段
            defaultFragment.setArguments(args);
            //加载默认片段到容器中
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_menu, defaultFragment)
                    .commit();
            //设置初始选项卡为选中首页状态
            bottomNavigationView.setSelectedItemId(R.id.tab_home);
        }
        //实现选项卡监听
        bottomNavigationView.setOnItemSelectedListener(new TabItemSelectedListener());
    }

    //标记该用户本人是否访问登录
    private void markVisitOncePerDay() {
        Calendar calendar = Calendar.getInstance();
        String today = calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH);
        String prefKey = "visit_" + username + "_" + today;
        SharedPreferences sp = getSharedPreferences("visitor_stats", MODE_PRIVATE);
        boolean counted = sp.getBoolean(prefKey, false);
        if (!counted) {
            //第一次，+1
            dbHelper.incrementToday();
            //标记已统计
            sp.edit().putBoolean(prefKey, true).apply();
        }
    }

    private class TabItemSelectedListener implements NavigationBarView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Bundle args = new Bundle();
            switch (item.getItemId()) {
                case R.id.tab_home:
                    selectedFragment = new UserHomeFragment();
                    break;
                case R.id.tab_guide:
                    selectedFragment = new GuideFragment();
                    break;
                case R.id.tab_surroundings:
                    selectedFragment = new SurroundingsFragment();
                    break;
                case R.id.tab_mine:
                    selectedFragment = new MineFragment();
                    break;
            }
            if (selectedFragment != null) {
                args.putString("username", username);
                selectedFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_menu, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        }
    }
}
