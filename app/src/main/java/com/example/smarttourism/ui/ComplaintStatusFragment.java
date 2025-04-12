package com.example.smarttourism.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.adapter.ComplaintAdapter;
import com.example.smarttourism.entity.Complaint;
import com.example.smarttourism.util.DBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComplaintStatusFragment extends Fragment {
    private static final String EXTRA_CONTENT = "content";
    private String status;
    private ListView complaintList;
    private DBHelper dbHelper;
    //定义数据列表
    private List<Complaint> data = new ArrayList<>();

    public static ComplaintStatusFragment newInstance(String content) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_CONTENT, content);
        ComplaintStatusFragment tabContentFragment = new ComplaintStatusFragment();
        tabContentFragment.setArguments(arguments);
        return tabContentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.complaint_tab, container, false);
        //获取组件
        complaintList = (ListView) view.findViewById(R.id.complaintList);
        //获取处理状态
        status = getArguments().getString(EXTRA_CONTENT);
        //实现数据库功能
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        RefreshComplaintList();
        //实现按钮点击响应
        complaintList.setOnItemClickListener(new ComplainItemClickListener());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RefreshComplaintList();
    }

    //刷新攻略列表信息
    private void RefreshComplaintList() {
        //按处理状态查询信息
        data = new ArrayList<>();
        String selection = "status=?";
        String[] selectionArgs = {status};
        Cursor cursor = dbHelper.getDatabase().query("Complaint", null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range")
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range")
                String complaint_username = cursor.getString(cursor.getColumnIndex("complaint_username"));
                @SuppressLint("Range")
                String complaint_type = cursor.getString(cursor.getColumnIndex("complaint_type"));
                @SuppressLint("Range")
                String complaint_content = cursor.getString(cursor.getColumnIndex("complaint_content"));
                @SuppressLint("Range")
                String complaint_date = cursor.getString(cursor.getColumnIndex("complaint_date"));
                @SuppressLint("Range")
                String complaint_contact = cursor.getString(cursor.getColumnIndex("complaint_contact"));
                data.add(new Complaint(id, complaint_username, complaint_type, complaint_content, complaint_date, complaint_contact, status));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //获取列表数据
        ComplaintAdapter adapter = new ComplaintAdapter(getActivity(), R.layout.complaint_item, data);
        complaintList.setAdapter(adapter);
    }

    private class ComplainItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //获取列表数据
            Complaint selectedComplaint = data.get(position);
            //弹出对话框，显示投诉详情
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("投诉详情");
            //创建对话框布局
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);
            int textSize = 16;
            //投诉内容文本
            TextView contentView = new TextView(getActivity());
            contentView.setText("投诉内容: " + selectedComplaint.getComplaint_content());
            contentView.setTextSize(textSize);
            contentView.setPadding(0, 20, 0, 30);
            layout.addView(contentView);
            //联系方式文本
            TextView contactView = new TextView(getActivity());
            contactView.setText("联系方式: " + selectedComplaint.getComplaint_contact());
            contactView.setTextSize(textSize);
            contactView.setPadding(0, 10, 0, 30);
            layout.addView(contactView);
            //处理状态标签
            TextView statusLabel = new TextView(getActivity());
            statusLabel.setText("处理状态:");
            statusLabel.setTextSize(textSize);
            statusLabel.setPadding(0, 10, 0, 20);
            layout.addView(statusLabel);
            //创建下拉框，用于修改处理状态
            Spinner statusSpinner = new Spinner(getActivity());
            String[] statusOptions = {"未处理", "处理中", "已处理"};
            statusSpinner.setScrollBarSize(textSize);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, statusOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            statusSpinner.setAdapter(adapter);
            statusSpinner.setPadding(0, 10, 0, 30);
            // 设置默认选中项
            int index = Arrays.asList(statusOptions).indexOf(selectedComplaint.getStatus());
            if (index >= 0) {
                statusSpinner.setSelection(index);
            }
            layout.addView(statusSpinner);
            builder.setView(layout);
            builder.setPositiveButton("更改处理状态", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String newStatus = statusSpinner.getSelectedItem().toString();
                    ContentValues values = new ContentValues();
                    values.put("status", newStatus);
                    dbHelper.getDatabase().update("Complaint", values, "id=?", new String[]{String.valueOf(selectedComplaint.getId())});
                    RefreshComplaintList();
                    Toast.makeText(getActivity(), "处理状态变更", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNeutralButton("取消", null);
            builder.show();
        }
    }
}

