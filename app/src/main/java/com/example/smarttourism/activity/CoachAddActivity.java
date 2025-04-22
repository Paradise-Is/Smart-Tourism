package com.example.smarttourism.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

import java.util.regex.Pattern;

public class CoachAddActivity extends Activity {
    private ImageView backBt;
    private TextView addBt;
    private EditText licenseEt;
    private EditText capacityEt;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建游览车登记界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_add);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        addBt = (TextView) this.findViewById(R.id.addBt);
        licenseEt = (EditText) this.findViewById(R.id.licenseEt);
        capacityEt = (EditText) this.findViewById(R.id.capacityEt);
        //实现数据库功能
        dbHelper = new DBHelper(this);
        dbHelper.open();
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        addBt.setOnClickListener(new AddBtListener());
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class AddBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String license = licenseEt.getText().toString();
            String capacity = capacityEt.getText().toString();
            Pattern licensePattern = Pattern.compile("^(?:[\\u4E00-\\u9FA5][A-Z](?:[A-Z0-9]{5}|[DF][A-HJ-NP-Z0-9]\\d{4}))$");
            if (license.equals("")) {
                Toast.makeText(CoachAddActivity.this, "游览车车牌号不能为空", Toast.LENGTH_SHORT).show();
            } else if (!licensePattern.matcher(license).matches()) {
                Toast.makeText(CoachAddActivity.this, "车牌号不符合规范", Toast.LENGTH_SHORT).show();
            } else if (capacity.equals("")) {
                Toast.makeText(CoachAddActivity.this, "游览车载客量不能为空", Toast.LENGTH_SHORT).show();
            }  else {
                //构造游览车的各字段数据
                ContentValues values = new ContentValues();
                values.put("coach_license", license);
                values.put("coach_capacity", capacity);
                values.put("status", "待命中");
                //添加游览车信息
                long result = dbHelper.getDatabase().insert("Coach", null, values);
                if (result != -1) {
                    Toast.makeText(CoachAddActivity.this, "游览车登记成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CoachAddActivity.this, "出错了，登记失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
