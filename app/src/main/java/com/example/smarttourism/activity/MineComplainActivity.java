package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class MineComplainActivity extends Activity {
    //用户用户名
    private String username;
    private ImageView backBt;
    private Spinner typeText;
    private EditText questionText;
    private TextView countText;
    private EditText contactText;
    private Button submitBt;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建用户投诉界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_complain);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        typeText = (Spinner) this.findViewById(R.id.typeSp);
        questionText = (EditText) this.findViewById(R.id.wordEt);
        countText = (TextView) this.findViewById(R.id.wordCountTv);
        contactText = (EditText) this.findViewById(R.id.contactEt);
        submitBt = (Button) this.findViewById(R.id.submitBt);
        //实现数据库功能
        dbHelper = new DBHelper(this);
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //设置默认联系方式为用户自己的电话
        String selection = "username=?";
        String[] selectionArgs = {username};
        Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            @SuppressLint("Range")
            String dbPhone = cursor.getString(cursor.getColumnIndex("phone"));
            //如果用户设置用户联系电话，则默认显示
            if (dbPhone != null && !dbPhone.isEmpty()) {
                contactText.setText(dbPhone);
            }
        }
        //下拉框的选项数据
        String[] complainTypes = {"景区服务问题", "景区管理问题", "其他问题"};
        //创建ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, complainTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //设置适配器
        typeText.setAdapter(adapter);
        //计算问题字数
        questionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countText.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        submitBt.setOnClickListener(new SubmitBtListener());
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class SubmitBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String type = typeText.getSelectedItem().toString();
            String question = questionText.getText().toString();
            String contact = contactText.getText().toString();
            Pattern phonePattern = Pattern.compile("(13[0-9]|14[57]|15[012356789]|18[02356789])\\d{8}");
            if (question.equals("")) {
                Toast.makeText(MineComplainActivity.this, "问题描述不能为空", Toast.LENGTH_SHORT).show();
            } else if (contact.equals("")) {
                Toast.makeText(MineComplainActivity.this, "联系方式不能为空", Toast.LENGTH_SHORT).show();
            } else if (!phonePattern.matcher(contact).matches()) {
                Toast.makeText(MineComplainActivity.this, "联系方式不符合规范", Toast.LENGTH_SHORT).show();
            } else {
                //构造投诉记录的各字段数据
                ContentValues values = new ContentValues();
                values.put("complaint_username", username);
                values.put("complaint_type", type);
                values.put("complaint_content", question);
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                values.put("complaint_date", currentDate);
                values.put("complaint_contact", contact);
                values.put("status", "未处理");
                //添加投诉记录
                long result = dbHelper.getDatabase().insert("Complaint", null, values);
                if (result != -1) {
                    Toast.makeText(MineComplainActivity.this, "投诉成功，感谢您的建议", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MineComplainActivity.this, "出错了，投诉失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
