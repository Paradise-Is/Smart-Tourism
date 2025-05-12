package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

public class GuideEditActivity extends Activity {
    //用户用户名
    private String username;
    private int id;
    private ImageView backBt;
    private ImageView deleteBt;
    private EditText titleText;
    private TextView wordTotalText;
    private EditText contentText;
    private Button editBt;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建编写攻略界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_edit);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        deleteBt = (ImageView) this.findViewById(R.id.deleteBt);
        titleText = (EditText) this.findViewById(R.id.titleEt);
        wordTotalText = (TextView) this.findViewById(R.id.wordTotalTv);
        contentText = (EditText) this.findViewById(R.id.contentEt);
        editBt = (Button) this.findViewById(R.id.editBt);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        id = intent.getIntExtra("id", 1);
        RefreshGuideInfo();
        //计算标题剩余字数
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordTotalText.setText(String.valueOf(30 - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        deleteBt.setOnClickListener(new DeleteBtListener());
        editBt.setOnClickListener(new EditBtListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshGuideInfo();
    }

    //刷新攻略内容
    private void RefreshGuideInfo() {
        //获取攻略内容
        String guide_id = String.valueOf(id);
        String selection = "id=?";
        String[] selectionArgs = {guide_id};
        Cursor cursor = dbHelper.getDatabase().query("Guide", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range")
            String dbTitle = cursor.getString(cursor.getColumnIndex("guide_title"));
            titleText.setText(dbTitle);
            @SuppressLint("Range")
            String dbContent = cursor.getString(cursor.getColumnIndex("guide_content"));
            contentText.setText(dbContent);
        }
        cursor.close();
        wordTotalText.setText(String.valueOf(30 - titleText.getText().length()));
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class DeleteBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GuideEditActivity.this);
            builder.setMessage("是否确认要删除该攻略");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //从表User中删除指定用户的记录
                    dbHelper.getDatabase().delete("Guide", "id = ? ", new String[]{String.valueOf(id)});
                    Toast.makeText(GuideEditActivity.this, "成功删除该攻略", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            builder.setNeutralButton("取消", null);
            builder.show();
        }
    }

    private class EditBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String title = titleText.getText().toString();
            String content = contentText.getText().toString();
            if (title.equals("")) {
                Toast.makeText(GuideEditActivity.this, "攻略标题不能为空", Toast.LENGTH_SHORT).show();
            } else if (content.equals("")) {
                Toast.makeText(GuideEditActivity.this, "攻略内容不能为空", Toast.LENGTH_SHORT).show();
            } else {
                //构造攻略信息的各字段数据
                ContentValues values = new ContentValues();
                values.put("guide_title", title);
                values.put("guide_content", content);
                dbHelper.getDatabase().update("Guide", values, "id = ?", new String[]{String.valueOf(id)});
                Toast.makeText(GuideEditActivity.this, "修改攻略成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
