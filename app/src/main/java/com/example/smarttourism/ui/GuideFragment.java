package com.example.smarttourism.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.activity.GuideAddActivity;
import com.example.smarttourism.activity.GuideInfoActivity;
import com.example.smarttourism.adapter.GuideAdapter;
import com.example.smarttourism.entity.Guide;
import com.example.smarttourism.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class GuideFragment extends Fragment {
    private String username;
    private ImageView addBt;
    private ListView guideList;
    private DBHelper dbHelper;
    //定义数据列表
    private List<Guide> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_guide, container, false);
        //获取组件
        addBt = (ImageView) view.findViewById(R.id.addBt);
        guideList = (ListView) view.findViewById(R.id.guideList);
        //实现数据库功能
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        //获取从Activity传来的数据
        Bundle args = getArguments();
        username = args.getString("username");
        RefreshGuideList();
        //实现按钮点击响应
        addBt.setOnClickListener(new AddBtListener());
        guideList.setOnItemClickListener(new GuideItemClickListener());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshGuideList();
    }

    //刷新攻略列表信息
    private void RefreshGuideList() {
        //查询全部攻略
        data = new ArrayList<>();
        Cursor cursor = dbHelper.getDatabase().query("Guide", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                String guide_title = cursor.getString(cursor.getColumnIndex("guide_title"));
                @SuppressLint("Range")
                String guide_content = cursor.getString(cursor.getColumnIndex("guide_content"));
                @SuppressLint("Range")
                String guide_date = cursor.getString(cursor.getColumnIndex("guide_date"));
                @SuppressLint("Range")
                String guide_username = cursor.getString(cursor.getColumnIndex("guide_username"));
                data.add(new Guide(id, guide_title, guide_content, guide_date, guide_username));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //获取列表数据
        GuideAdapter adapter = new GuideAdapter(getActivity(), R.layout.guide_item, data);
        guideList.setAdapter(adapter);
    }

    private class AddBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), GuideAddActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    private class GuideItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Guide guide = data.get(position);
            Intent intent = new Intent(getActivity(), GuideInfoActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("id", guide.getId());
            startActivity(intent);
        }
    }
}
