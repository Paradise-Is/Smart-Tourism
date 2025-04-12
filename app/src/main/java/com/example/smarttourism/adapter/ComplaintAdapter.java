package com.example.smarttourism.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarttourism.R;
import com.example.smarttourism.entity.Complaint;

import java.util.List;

public class ComplaintAdapter extends ArrayAdapter<Complaint> {

    public ComplaintAdapter(@NonNull Context context, int resource, @NonNull List<Complaint> objects) {
        super(context, resource, objects);
    }

    //每个子项被滚动到屏幕内的时候会被调用
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //得到当前项的实例
        Complaint complaint = getItem(position);
        //为每一个子项加载设定的布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.complaint_item, parent, false);
        //分别获取实例
        TextView complaint_user = view.findViewById(R.id.complainUser);
        TextView complaint_type = view.findViewById(R.id.complainType);
        TextView complaint_content = view.findViewById(R.id.complainContent);
        TextView complaint_status = view.findViewById(R.id.complainStatus);
        TextView complaint_date = view.findViewById(R.id.complainDate);
        //在子项上显示内容
        complaint_user.setText(complaint.getComplaint_username());
        complaint_type.setText(complaint.getComplaint_type());
        complaint_content.setText(complaint.getComplaint_content());
        complaint_status.setText(complaint.getStatus());
        complaint_date.setText(complaint.getComplaint_date());
        return view;
    }
}
