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
import com.example.smarttourism.activity.DocentAddActivity;
import com.example.smarttourism.activity.DocentInfoActivity;
import com.example.smarttourism.activity.GuideInfoActivity;
import com.example.smarttourism.adapter.DocentAdapter;
import com.example.smarttourism.entity.Docent;
import com.example.smarttourism.util.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class DocentFragment extends Fragment {
    private String username;
    private ImageView addBt;
    private ListView docentList;
    private DBHelper dbHelper;
    //定义数据列表
    private List<Docent> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_docent, container, false);
        //获取组件
        addBt = (ImageView) view.findViewById(R.id.addBt);
        docentList = (ListView) view.findViewById(R.id.docentList);
        //实现数据库功能
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        //获取从Activity传来的数据
        Bundle args = getArguments();
        username = args.getString("username");
        RefreshDocentList();
        //实现按钮点击响应
        addBt.setOnClickListener(new AddBtListener());
        docentList.setOnItemClickListener(new DocentItemClickListener());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshDocentList();
    }

    //刷新讲解员列表信息
    private void RefreshDocentList() {
        //查询全部攻略
        data = new ArrayList<>();
        Cursor cursor = dbHelper.getDatabase().query("Docent", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                String docent_name = cursor.getString(cursor.getColumnIndex("docent_name"));
                @SuppressLint("Range")
                String docent_gender = cursor.getString(cursor.getColumnIndex("docent_gender"));
                @SuppressLint("Range")
                String docent_age = cursor.getString(cursor.getColumnIndex("docent_age"));
                @SuppressLint("Range")
                String docent_photo = cursor.getString(cursor.getColumnIndex("docent_photo"));
                @SuppressLint("Range")
                String docent_phone = cursor.getString(cursor.getColumnIndex("docent_phone"));
                data.add(new Docent(id, docent_name, docent_gender, docent_age, docent_photo, docent_phone));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //获取列表数据
        DocentAdapter adapter = new DocentAdapter(getActivity(), R.layout.docent_item, data);
        docentList.setAdapter(adapter);

    }

    private class AddBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), DocentAddActivity.class);
            startActivity(intent);
        }
    }

    private class DocentItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Docent docent = data.get(position);
            Intent intent = new Intent(getActivity(), DocentInfoActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("id", docent.getId());
            startActivity(intent);
        }
    }
}
