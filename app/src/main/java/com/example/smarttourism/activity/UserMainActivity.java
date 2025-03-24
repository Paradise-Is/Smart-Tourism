package com.example.smarttourism.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttourism.R;
import com.example.smarttourism.databinding.UserMainBinding;

public class UserMainActivity extends AppCompatActivity {
    private static final String TAG = "UserMainActivity";
    private UserMainBinding binding;
    // 请求权限意图
    private ActivityResultLauncher<String> requestPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //申请位置权限
        requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            // 权限申请结果
            Log.d(TAG, "权限申请结果: " + result);
            showMsg(result ? "已获取到权限" : "权限申请失败");
        });
        //构建用户主页界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main);
        //实现视图绑定
        binding = UserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 检查是否已经获取到定位权限
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 获取到权限
            Log.d(TAG, "onResume: 已获取到权限");
            showMsg("已获取到权限");
            ;
        } else {
            // 请求定位权限
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    //显示权限获取结果
    private void showMsg(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
