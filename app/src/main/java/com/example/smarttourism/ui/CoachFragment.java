package com.example.smarttourism.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.activity.CoachAddActivity;
import com.example.smarttourism.adapter.CoachAdapter;
import com.example.smarttourism.entity.Coach;
import com.example.smarttourism.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class CoachFragment extends Fragment {
    private String username;
    private ImageView addBt;
    private ListView coachList;
    private DBHelper dbHelper;
    //定义数据列表
    private List<Coach> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_coach, container, false);
        //获取组件
        addBt = (ImageView) view.findViewById(R.id.addBt);
        coachList = (ListView) view.findViewById(R.id.coachList);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(requireContext().getApplicationContext());
        dbHelper.open();
        //获取从Activity传来的数据
        Bundle args = getArguments();
        username = args.getString("username");
        RefreshCoachList();
        //实现按钮点击响应
        addBt.setOnClickListener(new AddBtListener());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshCoachList();
    }

    //刷新讲解员列表信息
    private void RefreshCoachList() {
        //查询全部攻略
        data = new ArrayList<>();
        Cursor cursor = dbHelper.getDatabase().query("Coach", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                String coach_license = cursor.getString(cursor.getColumnIndex("coach_license"));
                @SuppressLint("Range")
                String coach_capacity = cursor.getString(cursor.getColumnIndex("coach_capacity"));
                int latIdx = cursor.getColumnIndex("gps_latitude");
                @SuppressLint("Range")
                Double gps_latitude = cursor.isNull(latIdx) ? null : cursor.getDouble(latIdx);
                int lonIdx = cursor.getColumnIndex("gps_longitude");
                @SuppressLint("Range")
                Double gps_longitude = cursor.isNull(lonIdx) ? null : cursor.getDouble(lonIdx);
                @SuppressLint("Range")
                String status = cursor.getString(cursor.getColumnIndex("status"));
                data.add(new Coach(id, coach_license, coach_capacity, gps_latitude, gps_longitude, status));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //获取列表数据
        CoachAdapter adapter = new CoachAdapter(getActivity(), R.layout.coach_item, data, new CoachAdapter.DataChangedListener() {
            @Override
            public void onDataChanged() {
                //数据变更时刷新界面
                RefreshCoachList();
            }
        });
        coachList.setAdapter(adapter);

    }

    private class AddBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), CoachAddActivity.class);
            startActivity(intent);
        }
    }
}