package com.example.smarttourism.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarttourism.R;
import com.example.smarttourism.entity.Purchase;
import com.example.smarttourism.util.DBHelper;

import java.util.List;

public class PurchaseAdapter extends ArrayAdapter<Purchase> {
    private DBHelper dbHelper;

    public PurchaseAdapter(@NonNull Context context, int resource, @NonNull List<Purchase> objects) {
        super(context, resource, objects);
        dbHelper = DBHelper.getInstance(context);
        dbHelper.open();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //得到当前项的实例
        Purchase purchase = getItem(position);
        //为每一个子项加载设定的布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.purchase_item, parent, false);
        //分别获取实例
        ImageView sightImg = view.findViewById(R.id.sightImg);
        TextView sightName = view.findViewById(R.id.sightName);
        TextView sightQuantity = view.findViewById(R.id.sightQuantity);
        TextView sightTotal = view.findViewById(R.id.sightTotal);
        TextView sightDate = view.findViewById(R.id.sightDate);
        //获取景点信息
        int sight_id = purchase.getSight_id();
        String selection = "id=?";
        String[] selectionArgs = {String.valueOf(sight_id)};
        Cursor cursor = dbHelper.getDatabase().query("Sight", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range")
            String dbName = cursor.getString(cursor.getColumnIndex("name"));
            sightName.setText(dbName + "——景点票");
            @SuppressLint("Range")
            String dbImage = cursor.getString(cursor.getColumnIndex("image"));
            if (dbImage != null && !dbImage.isEmpty()) {
                //通过反射方式获得 drawable 目录里对应的资源 ID
                int resId = getContext().getResources().getIdentifier(dbImage, "drawable", getContext().getPackageName());
                if (resId != 0) {
                    sightImg.setImageResource(resId);
                } else {
                    //没有找到对应资源
                    Toast.makeText(getContext(), "系统出错，未找到景点图片资源：", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "系统出错，无法加载景点图片", Toast.LENGTH_SHORT).show();
            }
        }
        cursor.close();
        sightQuantity.setText(purchase.getQuantity());
        sightTotal.setText("￥" + purchase.getTotal());
        sightDate.setText(purchase.getPurchase_date());
        return view;
    }
}
