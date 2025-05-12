package com.example.smarttourism.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SightPurchaseActivity extends Activity {
    //用户用户名
    private String username;
    //景点id
    private int sight_id;
    private ImageView backBt;
    private ImageView sightImg;
    private TextView sightName;
    private TextView sightPrice;
    //单票价格
    private double price;
    //初始票数
    private int ticketCount = 1;
    private ImageView minusBt;
    private TextView quantity;
    private ImageView addBt;
    private TextView total;
    private Button payBt;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建景点票购买界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sight_purchase);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        sightImg = (ImageView) this.findViewById(R.id.sightImg);
        sightName = (TextView) this.findViewById(R.id.sightName);
        sightPrice = (TextView) this.findViewById(R.id.sightPrice);
        minusBt = (ImageView) this.findViewById(R.id.minusBt);
        quantity = (TextView) this.findViewById(R.id.quantity);
        addBt = (ImageView) this.findViewById(R.id.addBt);
        total = (TextView) this.findViewById(R.id.total);
        payBt = (Button) this.findViewById(R.id.payBt);
        //获取用户用户名和景点id
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        sight_id = intent.getIntExtra("sight_id", 1);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //获取景点信息
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(sight_id)};
        Cursor cursor = dbHelper.getDatabase().query("Sight", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range")
            String dbName = cursor.getString(cursor.getColumnIndex("name"));
            sightName.setText(dbName + "——景点票");
            @SuppressLint("Range")
            String dbPrice = cursor.getString(cursor.getColumnIndex("price"));
            price = Double.valueOf(dbPrice);
            sightPrice.setText("￥" + dbPrice);
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
        //初始景点票数量与总价显示
        quantity.setText(String.valueOf(ticketCount));
        updateTotal();
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
        minusBt.setOnClickListener(new MinusBtListener());
        addBt.setOnClickListener(new AddBtListener());
        payBt.setOnClickListener(new PayBtListener());
    }

    //更新总额
    private void updateTotal() {
        double t = price * ticketCount;
        // 保留两位小数
        total.setText(String.format(Locale.getDefault(), "￥%.2f", t));
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }

    private class MinusBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (ticketCount > 1) {
                ticketCount--;
                quantity.setText(String.valueOf(ticketCount));
                updateTotal();
            } else {
                Toast.makeText(SightPurchaseActivity.this, "票数不能小于1", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AddBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ticketCount++;
            quantity.setText(String.valueOf(ticketCount));
            updateTotal();
        }
    }

    private class PayBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ContentValues values = new ContentValues();
            values.put("purchase_username", username);
            values.put("sight_id", sight_id);
            values.put("quantity", ticketCount);
            values.put("price", price);
            values.put("total", ticketCount * price);
            String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            values.put("purchase_date", currentDate);
            long id = dbHelper.getDatabase().insert("SightPurchase", null, values);
            if (id != -1) {
                Toast.makeText(SightPurchaseActivity.this, "支付成功！", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(SightPurchaseActivity.this, "下单失败，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
