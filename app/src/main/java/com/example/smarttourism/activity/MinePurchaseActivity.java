package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smarttourism.R;
import com.example.smarttourism.adapter.PurchaseAdapter;
import com.example.smarttourism.entity.Purchase;
import com.example.smarttourism.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class MinePurchaseActivity extends Activity {
    //用户用户名
    private String username;
    private ImageView backBt;
    private ListView purchaseList;
    private TextView emptyText;
    //定义数据列表
    private List<Purchase> data = new ArrayList<>();
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建用户投诉界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_purchase);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        purchaseList = (ListView) this.findViewById(R.id.purchaseList);
        emptyText = (TextView) findViewById(R.id.emptyText);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        RefreshPurchaseList();
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshPurchaseList();
    }

    //刷新购票列表信息
    private void RefreshPurchaseList() {
        //查询全部攻略
        data = new ArrayList<>();
        String selection = "purchase_username=?";
        String[] selectionArgs = {username};
        Cursor cursor = dbHelper.getDatabase().query("SightPurchase", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int purchase_id = cursor.getInt(cursor.getColumnIndex("purchase_id"));
                @SuppressLint("Range")
                int sight_id = cursor.getInt(cursor.getColumnIndex("sight_id"));
                @SuppressLint("Range")
                String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                @SuppressLint("Range")
                String price = cursor.getString(cursor.getColumnIndex("price"));
                @SuppressLint("Range")
                String total = cursor.getString(cursor.getColumnIndex("total"));
                @SuppressLint("Range")
                String purchase_date = cursor.getString(cursor.getColumnIndex("purchase_date"));
                data.add(new Purchase(purchase_id, username, sight_id, quantity, price, total, purchase_date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //获取列表数据
        PurchaseAdapter adapter = new PurchaseAdapter(MinePurchaseActivity.this, R.layout.purchase_item, data);
        purchaseList.setAdapter(adapter);
        // 控制提示文本显示与否
        if (data.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            purchaseList.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            purchaseList.setVisibility(View.VISIBLE);
        }
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }
}
