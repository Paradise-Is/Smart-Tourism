package com.example.smarttourism.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ComplaintsBarFragment extends Fragment {
    private BarChart barChart;
    private TextView totalTv;
    private TextView rateTv;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_bar, container, false);
        //获取组件
        barChart = view.findViewById(R.id.complaintsBarChart);
        totalTv = view.findViewById(R.id.totalTv);
        rateTv = view.findViewById(R.id.rateTv);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(requireContext().getApplicationContext());
        dbHelper.open();
        //构建页面数据
        setupBarChart();
        return view;
    }

    private void setupBarChart() {
        //从数据库读取三种状态的投诉数
        int unprocessed = dbHelper.countComplaintsByStatus("未处理");
        int processing = dbHelper.countComplaintsByStatus("处理中");
        int processed = dbHelper.countComplaintsByStatus("已处理");
        int total = dbHelper.countAllComplaints();
        //构建BarEntry列表
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, unprocessed));
        entries.add(new BarEntry(1f, processing));
        entries.add(new BarEntry(2f, processed));
        //创建DataSet并设置配色
        BarDataSet set = new BarDataSet(entries, "投诉状态");
        set.setColors(
                //红色—未处理
                Color.parseColor("#FF5252"),
                //黄色—处理中
                Color.parseColor("#FFEB3B"),
                //绿色—已处理
                Color.parseColor("#4CAF50")
        );
        set.setValueTextSize(12f);
        //装入BarData并绑定给图表
        BarData data = new BarData(set);
        data.setBarWidth(0.5f);
        barChart.setData(data);
        //配置X轴标签
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                Arrays.asList("未处理", "处理中", "已处理")
        ));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        //隐藏右侧Y轴，禁用描述文字
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(800);
        barChart.invalidate();
        //显示投诉总数及占比
        totalTv.setText(String.valueOf(total));
        float rate = total > 0 ? (processed * 100f / total) : 0f;
        rateTv.setText(String.format(Locale.getDefault(), "%.1f%%", rate));
    }
}
