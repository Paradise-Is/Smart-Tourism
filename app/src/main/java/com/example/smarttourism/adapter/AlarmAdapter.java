package com.example.smarttourism.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarttourism.R;
import com.example.smarttourism.activity.UserInfoActivity;
import com.example.smarttourism.activity.UserLocationActivity;
import com.example.smarttourism.entity.Alarm;

import java.util.List;

public class AlarmAdapter extends ArrayAdapter<Alarm> {
    //保存上下文
    private Context mContext;

    public AlarmAdapter(@NonNull Context context, int resource, @NonNull List<Alarm> objects) {
        super(context, resource, objects);
        this.mContext = context;
    }

    //每个子项被滚动到屏幕内的时候会被调用
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //用局部变量取当前 Alarm
        final Alarm alarm = getItem(position);
        //为每一个子项加载设定的布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.emergency_item, parent, false);
        //分别获取实例
        TextView alarm_username = view.findViewById(R.id.alarmUsername);
        TextView alarm_date = view.findViewById(R.id.alarmDate);
        TextView infoBt = view.findViewById(R.id.infoBt);
        TextView locationBt = view.findViewById(R.id.locationBt);
        //在子项上显示内容
        alarm_username.setText(alarm.getAlarm_username());
        alarm_date.setText(alarm.getAlarm_date());
        //实现按钮响应
        infoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                intent.putExtra("username", alarm.getAlarm_username());
                mContext.startActivity(intent);
            }
        });
        locationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserLocationActivity.class);
                intent.putExtra("username", alarm.getAlarm_username());
                //防止位置为null，安全处理
                double lat = alarm.getAlarm_latitude() != null ? alarm.getAlarm_latitude() : 0.0;
                double lon = alarm.getAlarm_longitude() != null ? alarm.getAlarm_longitude() : 0.0;
                intent.putExtra("latitude", lat);
                intent.putExtra("longitude", lon);
                mContext.startActivity(intent);
            }
        });
        return view;
    }
}
