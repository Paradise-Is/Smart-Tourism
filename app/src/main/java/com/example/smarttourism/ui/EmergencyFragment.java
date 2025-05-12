package com.example.smarttourism.ui;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.adapter.AlarmAdapter;
import com.example.smarttourism.entity.Alarm;
import com.example.smarttourism.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class EmergencyFragment extends Fragment {
    private String username;
    private ListView alarmList;
    private DBHelper dbHelper;
    //定义数据列表
    private List<Alarm> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_emergency, container, false);
        //获取组件
        alarmList = (ListView) view.findViewById(R.id.alarmList);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(requireContext().getApplicationContext());
        dbHelper.open();
        //获取从Activity传来的数据
        Bundle args = getArguments();
        username = args.getString("username");
        RefreshAlarmList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshAlarmList();
    }

    //刷新讲解员列表信息
    private void RefreshAlarmList() {
        //查询全部攻略
        data = new ArrayList<>();
        Cursor cursor = dbHelper.getDatabase().query("Alarm", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                String alarm_username = cursor.getString(cursor.getColumnIndex("alarm_username"));
                @SuppressLint("Range")
                String alarm_date = cursor.getString(cursor.getColumnIndex("alarm_date"));
                @SuppressLint("Range")
                String alarm_latitude = cursor.getString(cursor.getColumnIndex("alarm_latitude"));
                @SuppressLint("Range")
                String alarm_longitude = cursor.getString(cursor.getColumnIndex("alarm_longitude"));
                data.add(new Alarm(id, alarm_username, alarm_date, Double.valueOf(alarm_latitude), Double.valueOf(alarm_longitude)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //获取列表数据
        AlarmAdapter adapter = new AlarmAdapter(getActivity(), R.layout.emergency_item, data);
        alarmList.setAdapter(adapter);

    }
}
