package com.example.smarttourism.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttourism.R;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建初始界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }
}
