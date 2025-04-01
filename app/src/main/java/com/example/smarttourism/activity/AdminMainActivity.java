package com.example.smarttourism.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.ui.AdminHomeFragment;
import com.example.smarttourism.ui.AnalysisFragment;
import com.example.smarttourism.ui.CoachFragment;
import com.example.smarttourism.ui.ComplaintFragment;
import com.example.smarttourism.ui.DocentFragment;
import com.example.smarttourism.ui.EmergencyFragment;
import com.example.smarttourism.ui.PasswordFragment;
import com.google.android.material.navigation.NavigationView;

public class AdminMainActivity extends AppCompatActivity {
    //侧滑抽屉布局，用来管理主界面和侧边栏
    private DrawerLayout drawerLayout;
    //侧边栏中的菜单视图
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建管理员主页界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);
        //获取组件
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        //默认加载主页
        if (savedInstanceState == null) {
            //创建片段实例
            AdminHomeFragment defaultFragment = new AdminHomeFragment();
            //构建传递给片段的参数
            Bundle args = new Bundle();
            //可以通过 getArguments() 来获取这个传递过来的文本数据。
            args.putString("text", navigationView.getMenu().findItem(R.id.nav_home).getTitle().toString());
            //将参数设置给片段
            defaultFragment.setArguments(args);
            //加载默认片段到容器中
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, defaultFragment)
                    .commit();
            //设置菜单项为选中状态
            navigationView.setCheckedItem(R.id.nav_home);
        }
        //实现菜单项监听
        navigationView.setNavigationItemSelectedListener(new NavItemSelectedListener());
    }

    private class NavItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //处理菜单项的点击事件
            item.setChecked(true);
            //初始化参数
            Fragment selectedFragment = null;
            Bundle args = new Bundle();
            //处理不同选项点击事件
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new AdminHomeFragment();
                    args.putString("text", item.getTitle().toString());
                    break;
                case R.id.nav_analysis:
                    selectedFragment = new AnalysisFragment();
                    args.putString("text", item.getTitle().toString());
                    break;
                case R.id.nav_emergency:
                    selectedFragment = new EmergencyFragment();
                    args.putString("text", item.getTitle().toString());
                    break;
                case R.id.nav_docent:
                    selectedFragment = new DocentFragment();
                    args.putString("text", item.getTitle().toString());
                    break;
                case R.id.nav_complaint:
                    selectedFragment = new ComplaintFragment();
                    args.putString("text", item.getTitle().toString());
                    break;
                case R.id.nav_coach:
                    selectedFragment = new CoachFragment();
                    args.putString("text", item.getTitle().toString());
                    break;
                case R.id.nav_password:
                    selectedFragment = new PasswordFragment();
                    args.putString("text", item.getTitle().toString());
                    break;
                case R.id.nav_back:
                    break;
            }
            if (selectedFragment != null) {
                selectedFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_content, selectedFragment)
                        .commit();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    }
}
