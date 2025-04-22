package com.example.smarttourism.adapter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smarttourism.R;
import com.example.smarttourism.entity.Coach;
import com.example.smarttourism.util.DBHelper;

import java.util.Arrays;
import java.util.List;

public class CoachAdapter extends ArrayAdapter<Coach> {
    //保存上下文
    private Context mContext;
    private DBHelper dbHelper;
    //回调字段
    private DataChangedListener dataChangedListener;

    // 修改构造器，增加 listener 参数
    public CoachAdapter(@NonNull Context context, int resource, @NonNull List<Coach> objects, DataChangedListener listener) {
        super(context, resource, objects);
        this.mContext = context;
        this.dataChangedListener = listener;
    }

    //每个子项被滚动到屏幕内的时候会被调用
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //用局部变量取当前 Coach
        final Coach coach = getItem(position);
        //为每一个子项加载设定的布局
        View view = LayoutInflater.from(getContext()).inflate(R.layout.coach_item, parent, false);
        //分别获取实例
        TextView id = view.findViewById(R.id.coachId);
        TextView coach_license = view.findViewById(R.id.coachLicense);
        TextView coach_capacity = view.findViewById(R.id.coachCapacity);
        TextView status = view.findViewById(R.id.coachStatus);
        TextView editBt = view.findViewById(R.id.editBt);
        TextView locationBt = view.findViewById(R.id.locationBt);
        //实现数据库功能
        dbHelper = new DBHelper(mContext);
        dbHelper.open();
        //在子项上显示内容
        id.setText(String.valueOf(coach.getId()));
        coach_license.setText(coach.getCoach_license());
        coach_capacity.setText(coach.getCoach_capacity());
        status.setText(coach.getStatus());
        //实现按钮响应
        editBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框，显示投诉详情
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("游览车状态修改");
                //创建对话框布局
                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(50, 40, 50, 10);
                int textSize = 16;
                //车牌号文本
                TextView licenseView = new TextView(mContext);
                licenseView.setText("游览车车牌号: " + coach.getCoach_license());
                licenseView.setTextSize(textSize);
                licenseView.setPadding(0, 20, 0, 30);
                layout.addView(licenseView);
                //载客量文本
                TextView capacityView = new TextView(mContext);
                capacityView.setText("游览车载客量: " + coach.getCoach_capacity());
                capacityView.setTextSize(textSize);
                capacityView.setPadding(0, 10, 0, 30);
                layout.addView(capacityView);
                //状态标签
                TextView statusLabel = new TextView(mContext);
                statusLabel.setText("游览车状态:");
                statusLabel.setTextSize(textSize);
                statusLabel.setPadding(0, 10, 0, 20);
                layout.addView(statusLabel);
                //创建下拉框，用于修改处理状态
                Spinner statusSpinner = new Spinner(mContext);
                String[] statusOptions = {"运行中", "待命中", "维修中"};
                statusSpinner.setScrollBarSize(textSize);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, statusOptions);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                statusSpinner.setAdapter(adapter);
                statusSpinner.setPadding(0, 10, 0, 30);
                // 设置默认选中项
                int index = Arrays.asList(statusOptions).indexOf(coach.getStatus());
                if (index >= 0) {
                    statusSpinner.setSelection(index);
                }
                layout.addView(statusSpinner);
                builder.setView(layout);
                builder.setPositiveButton("更改游览车状态", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String newStatus = statusSpinner.getSelectedItem().toString();
                        ContentValues values = new ContentValues();
                        values.put("status", newStatus);
                        dbHelper.getDatabase().update("Coach", values, "id=?", new String[]{String.valueOf(coach.getId())});
                        if (dataChangedListener != null) {
                            dataChangedListener.onDataChanged();
                        }
                        Toast.makeText(mContext, "处理状态变更", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("删除该游览车信息", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.getDatabase().delete("Coach", "id=?", new String[]{String.valueOf(coach.getId())});
                        if (dataChangedListener != null) {
                            dataChangedListener.onDataChanged();
                        }
                        Toast.makeText(mContext, "该游览车信息已删除", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNeutralButton("取消", null);
                builder.show();
            }
        });
        locationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "相关功能请期待后续开发", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    //回调接口
    public interface DataChangedListener {
        void onDataChanged();
    }
}

