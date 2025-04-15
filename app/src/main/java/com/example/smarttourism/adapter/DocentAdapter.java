package com.example.smarttourism.adapter;

import android.content.Context;
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
import com.example.smarttourism.entity.Docent;

import java.io.File;
import java.util.List;

public class DocentAdapter extends ArrayAdapter<Docent> {
    public DocentAdapter(@NonNull Context context, int resource, @NonNull List<Docent> objects) {
        super(context, resource, objects);
    }

    //每个子项被滚动到屏幕内的时候会被调用
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //得到当前项的实例
        Docent docent = getItem(position);
        //为每一个子项加载设定的布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.docent_item, parent, false);
        //分别获取实例
        ImageView docent_photo = view.findViewById(R.id.docentPhoto);
        TextView docent_name = view.findViewById(R.id.docentName);
        TextView docent_gender = view.findViewById(R.id.docentGender);
        TextView docent_age = view.findViewById(R.id.docentAge);
        TextView docent_phone = view.findViewById(R.id.docentPhone);
        //将相关信息显示到界面上
        File imgFile = new File(docent.getDocent_photo());
        docent_photo.setImageDrawable(null);
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        docent_photo.setImageBitmap(bitmap);
        docent_name.setText(docent.getDocent_name());
        docent_gender.setText(docent.getDocent_gender());
        docent_age.setText(docent.getDocent_age());
        docent_phone.setText(docent.getDocent_phone());
        return view;
    }
}
