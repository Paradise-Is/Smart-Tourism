package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourism.R;
import com.example.smarttourism.adapter.CommentAdapter;
import com.example.smarttourism.entity.Comment;
import com.example.smarttourism.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class SightInfoActivity extends Activity {
    //用户用户名
    private String username;
    //景点id
    private int sight_id;
    private Double latitude, longitude;
    private ImageView backBt;
    private ImageView sightImg;
    private TextView sightName;
    private TextView sightPrice;
    private TextView sightDescription;
    private RecyclerView commentList;
    private TextView emptyComments;
    private RelativeLayout commentBt;
    private Button bookBt;
    private Button navigateBt;
    private CommentAdapter commentAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建景区景点信息界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sight_info);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        sightImg = (ImageView) this.findViewById(R.id.sightImg);
        sightName = (TextView) this.findViewById(R.id.sightName);
        sightPrice = (TextView) this.findViewById(R.id.sightPrice);
        sightDescription = (TextView) this.findViewById(R.id.sightDescription);
        commentList = (RecyclerView) this.findViewById(R.id.commentList);
        emptyComments = (TextView) findViewById(R.id.emptyComments);
        commentBt = (RelativeLayout) this.findViewById(R.id.commentBt);
        bookBt = (Button) this.findViewById(R.id.bookBt);
        navigateBt = (Button) this.findViewById(R.id.navigateBt);
        //获取用户用户名和景点id
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        sight_id = intent.getIntExtra("sight_id", 1);
        //实现数据库功能
        dbHelper = new DBHelper(this);
        dbHelper.open();
        //获取景点信息
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(sight_id)};
        Cursor cursor = dbHelper.getDatabase().query("Sight", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range")
            String dbName = cursor.getString(cursor.getColumnIndex("name"));
            sightName.setText(dbName);
            @SuppressLint("Range")
            String dbDescription = cursor.getString(cursor.getColumnIndex("description"));
            sightDescription.setText(dbDescription);
            @SuppressLint("Range")
            String dbPrice = cursor.getString(cursor.getColumnIndex("price"));
            sightPrice.setText("票价" + dbPrice + "元");
            @SuppressLint("Range")
            String dbLatitude = cursor.getString(cursor.getColumnIndex("latitude"));
            latitude = Double.valueOf(dbLatitude);
            @SuppressLint("Range")
            String dbLongitude = cursor.getString(cursor.getColumnIndex("longitude"));
            longitude = Double.valueOf(dbLongitude);
            @SuppressLint("Range")
            String dbImage = cursor.getString(cursor.getColumnIndex("image"));
            if (dbImage != null && !dbImage.isEmpty()) {
                //通过反射方式获得 drawable 目录里对应的资源 ID
                int resId = getResources().getIdentifier(dbImage, "drawable", getPackageName());
                if (resId != 0) {
                    sightImg.setImageResource(resId);
                } else {
                    //没有找到对应资源
                    Toast.makeText(this, "系统出错，未找到景点图片资源：", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "系统出错，无法加载景点图片", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
        //RecyclerView初始化
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.setNestedScrollingEnabled(false);
        commentAdapter = new CommentAdapter(this, username, () -> RefreshCommentList());
        commentList.setAdapter(commentAdapter);
        //实现评论列表
        RefreshCommentList();
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        commentBt.setOnClickListener(new CommentBtListener());
        bookBt.setOnClickListener(new BookBtListener());
        navigateBt.setOnClickListener(new NavigateBtListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新评论列表
        RefreshCommentList();
    }

    private void RefreshCommentList() {
        List<Comment> comments = new ArrayList<>();
        //查找符合条件的评论
        Cursor cur = dbHelper.getDatabase().query("SightComment", null,
                "sight_id=?", new String[]{String.valueOf(sight_id)},
                null, null, "comment_date DESC");
        while (cur.moveToNext()) {
            @SuppressLint("Range")
            int comment_id = cur.getInt(cur.getColumnIndex("comment_id"));
            @SuppressLint("Range")
            String comment_username = cur.getString(cur.getColumnIndex("comment_username"));
            @SuppressLint("Range")
            String comment_text = cur.getString(cur.getColumnIndex("comment_text"));
            @SuppressLint("Range")
            String comment_date = cur.getString(cur.getColumnIndex("comment_date"));
            comments.add(new Comment(comment_id, comment_username, sight_id, comment_text, comment_date));
        }
        cur.close();
        // 如果没有任何评论，就隐藏 RecyclerView，显示emptyComments
        if (comments.isEmpty()) {
            commentList.setVisibility(View.GONE);
            emptyComments.setVisibility(View.VISIBLE);
        } else {
            emptyComments.setVisibility(View.GONE);
            commentList.setVisibility(View.VISIBLE);
            //传数据并刷新
            commentAdapter.setData(comments);
        }
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class CommentBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //跳转到编写评论页面
            Intent intent = new Intent(SightInfoActivity.this, SightCommentActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("sight_id", sight_id);
            startActivity(intent);
        }
    }

    private class BookBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //跳转到在线订票页面
            Intent intent = new Intent(SightInfoActivity.this, SightPurchaseActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("sight_id", sight_id);
            startActivity(intent);
        }
    }

    private class NavigateBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SightInfoActivity.this, RouteActivity.class);
            //传入景点坐标坐标
            intent.putExtra("endLatitude", latitude);
            intent.putExtra("endLongitude", longitude);
            startActivity(intent);
        }
    }
}
