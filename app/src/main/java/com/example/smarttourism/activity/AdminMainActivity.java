package com.example.smarttourism.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
    //用于在工具栏上显示和控制抽屉的开关图标
    private ActionBarDrawerToggle toggle;
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
            AdminHomeFragment defaultFragment = new AdminHomeFragment();
            Bundle args = new Bundle();
            args.putString("text", navigationView.getMenu().findItem(R.id.nav_home).getTitle().toString());
            defaultFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, defaultFragment)
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        //实现菜单项监听
        navigationView.setNavigationItemSelectedListener(new NavItemSelectedListener());
    }

    //重写onOptionsItemSelected方法，处理工具栏上的按钮点击事件
    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    //重写onBackPressed方法，当前是抽屉打开状态，则先关闭抽屉，否则执行默认的返回键行为
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
            switch(item.getItemId()){
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
