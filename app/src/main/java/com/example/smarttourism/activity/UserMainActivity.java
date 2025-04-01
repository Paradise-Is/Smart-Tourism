package com.example.smarttourism.activity;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class UserMainActivity extends AppCompatActivity {
    //底边栏的选项卡视图
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建用户主页界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main);
        //获取组件
        bottomNavigationView = findViewById(R.id.tab_menu_bar);
        //默认加载首页
        if (savedInstanceState == null) {
            //创建片段实例
            UserHomeFragment defaultFragment = new UserHomeFragment();
            //构建传递给片段的参数
            Bundle args = new Bundle();
            //可以通过 getArguments() 来获取这个传递过来的文本数据。
            args.putString("text", bottomNavigationView.getMenu().findItem(R.id.tab_home).getTitle().toString());
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

    private class TabItemSelectedListener implements NavigationBarView.OnItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
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
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_menu, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        }
    }
}
