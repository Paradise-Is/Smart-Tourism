package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smarttourism.R;
import com.example.smarttourism.adapter.GuideAdapter;
import com.example.smarttourism.entity.Guide;
import com.example.smarttourism.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class MineGuideActivity extends Activity {
    //用户用户名
    private String username;
    private ImageView backBt;
    private ListView guideList;
    private TextView emptyText;
    //定义数据列表
    private List<Guide> data = new ArrayList<>();
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建用户投诉界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_guide);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        guideList = (ListView) this.findViewById(R.id.guideList);
        emptyText = (TextView)findViewById(R.id.emptyText);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        RefreshGuideList();
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        guideList.setOnItemClickListener(new GuideItemClickListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshGuideList();
    }

    //刷新攻略列表信息
    private void RefreshGuideList() {
        //查询全部攻略
        data = new ArrayList<>();
        String selection = "guide_username=?";
        String[] selectionArgs = {username};
        Cursor cursor = dbHelper.getDatabase().query("Guide", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                String guide_title = cursor.getString(cursor.getColumnIndex("guide_title"));
                @SuppressLint("Range")
                String guide_content = cursor.getString(cursor.getColumnIndex("guide_content"));
                @SuppressLint("Range")
                String guide_date = cursor.getString(cursor.getColumnIndex("guide_date"));
                @SuppressLint("Range")
                String guide_username = cursor.getString(cursor.getColumnIndex("guide_username"));
                data.add(new Guide(id, guide_title, guide_content, guide_date, guide_username));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //获取列表数据
        GuideAdapter adapter = new GuideAdapter(MineGuideActivity.this, R.layout.guide_item, data);
        guideList.setAdapter(adapter);
        //控制提示文本显示与否
        if (data.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            guideList.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            guideList.setVisibility(View.VISIBLE);
        }
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class GuideItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Guide guide = data.get(position);
            Intent intent = new Intent(MineGuideActivity.this, GuideEditActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("id", guide.getId());
            startActivity(intent);
        }
    }
}
