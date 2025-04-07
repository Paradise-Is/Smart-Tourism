package com.example.smarttourism.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarttourism.R;
import com.example.smarttourism.entity.Guide;
import com.example.smarttourism.util.DBHelper;

import java.io.File;
import java.util.List;

public class GuideAdapter extends ArrayAdapter<Guide> {
    private DBHelper dbHelper;

    public GuideAdapter(@NonNull Context context, int resource, @NonNull List<Guide> objects) {
        super(context, resource, objects);
    }

    //每个子项被滚动到屏幕内的时候会被调用
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //得到当前项的实例
        Guide guide = getItem(position);
        //为每一个子项加载设定的布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.guide_item, parent, false);
        //分别获取实例
        ImageView guide_headshot = view.findViewById(R.id.userHeadshot);
        TextView guide_title = view.findViewById(R.id.guideTitle);
        TextView guide_date = view.findViewById(R.id.guideDate);
        //实现数据库功能
        dbHelper = new DBHelper(getContext());
        dbHelper.open();
        //在子项上显示内容
        String guide_username = guide.getGuide_username();
        //将相关信息显示到界面上
        String selection = "username=?";
        String[] selectionArgs = {guide_username};
        //根据用户查询到对应项
        Cursor cursor = dbHelper.getDatabase().query("User", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            //从数据库获取数据进行界面显示
            @SuppressLint("Range")
            String dbHeadshot = cursor.getString(cursor.getColumnIndex("headshot"));
            //将头像数据显示到界面中
            if (dbHeadshot != null && !dbHeadshot.isEmpty()) {
                File imgFile = new File(dbHeadshot);
                if (imgFile.exists()) {
                    //删除旧图片的缓存
                    guide_headshot.setImageDrawable(null);
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    guide_headshot.setImageBitmap(bitmap);
                } else {
                    //数据库中无头像数据时显示默认头像
                    guide_headshot.setImageResource(R.mipmap.mine_headshot);
                }
            } else {
                guide_headshot.setImageResource(R.mipmap.mine_headshot);
            }
        }
        cursor.close();
        guide_title.setText(guide.getGuide_title());
        guide_date.setText(guide.getGuide_date());
        return view;
    }
}
