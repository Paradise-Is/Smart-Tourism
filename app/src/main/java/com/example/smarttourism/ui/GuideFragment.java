package com.example.smarttourism.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
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
    private ImageView searchBt;
    private EditText searchEt;
    private TextView emptyText;

    private ListView guideList;
    private DBHelper dbHelper;
    //定义数据列表
    private List<Guide> data = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_guide, container, false);
        //获取组件
        addBt = (ImageView) view.findViewById(R.id.addBt);
        searchBt = (ImageView) view.findViewById(R.id.searchBt);
        searchEt = (EditText) view.findViewById(R.id.searchEt);
        emptyText = (TextView) view.findViewById(R.id.emptyText);
        guideList = (ListView) view.findViewById(R.id.guideList);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(requireContext().getApplicationContext());
        dbHelper.open();
        //获取从Activity传来的数据
        Bundle args = getArguments();
        username = args.getString("username");
        //初始刷新显示显示全部列表
        queryAndRefresh(null, false);
        //实现按钮点击响应
        addBt.setOnClickListener(new AddBtListener());
        searchBt.setOnClickListener(new SearchBtListener());
        guideList.setOnItemClickListener(new GuideItemClickListener());
        //搜索框清空刷新
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    queryAndRefresh(null, true);
                } else {
                    //模糊查询
                    queryAndRefresh(keyword, true);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //回到页面刷新
        String text = searchEt.getText().toString().trim();
        queryAndRefresh(text.isEmpty() ? null : text, text.isEmpty() ? false : true);
    }

    //查询及刷新判断(true表示模糊查询，false表示精确查询)
    private void queryAndRefresh(@Nullable String keyword, boolean fuzzy) {
        data.clear();
        String selection;
        String[] args;
        if (keyword == null) {
            //不带关键字，查找全部攻略
            selection = null;
            args = null;

        } else if (fuzzy) {
            //模糊查询：标题或内容包含keyword
            selection = "guide_title LIKE ? OR guide_content LIKE ?";
            String like = "%" + keyword + "%";
            args = new String[]{like, like};

        } else {
            //精确查询：标题等于keyword或内容等于 keyword
            selection = "guide_title = ? OR guide_content = ?";
            args = new String[]{keyword, keyword};
        }
        Cursor cursor = dbHelper.getDatabase().query("Guide", null, selection, args, null, null, "guide_date DESC");
        while (cursor.moveToNext()) {
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
        }
        cursor.close();
        //获取列表数据
        GuideAdapter adapter = new GuideAdapter(getActivity(), R.layout.guide_item, data);
        guideList.setAdapter(adapter);
        //控制提示文本显示与否
        if (data.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            guideList.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            guideList.setVisibility(View.VISIBLE);
        }
    }

    private class AddBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), GuideAddActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }
    }

    private class SearchBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String keyword = searchEt.getText().toString().trim();
            if (keyword.isEmpty()) {
                queryAndRefresh(null, false);
            } else {
                //精确查询
                queryAndRefresh(keyword, false);
            }
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
