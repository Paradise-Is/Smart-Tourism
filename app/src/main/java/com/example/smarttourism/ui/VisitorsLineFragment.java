package com.example.smarttourism.ui;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VisitorsLineFragment extends Fragment {
    private LineChart chart;
    private RadioGroup timeGroup;
    private TextView averageTv;
    private TextView totalTv;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitors_line, container, false);
        //获取组件
        chart = view.findViewById(R.id.visitorsLineChart);
        timeGroup = view.findViewById(R.id.timeGroup);
        averageTv = view.findViewById(R.id.averageTv);
        totalTv = view.findViewById(R.id.totalTv);
        //实现数据库功能
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        //默认展示近7日
        showDayChart();
        //设置监听
        timeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.dayBt:
                        showDayChart();
                        break;
                    case R.id.monthBt:
                        showMonthChart();
                        break;
                    case R.id.yearBt:
                        showYearChart();
                        break;
                }
            }
        });
        //显示近7日总计访客量及近7日平均访客量
        int total = dbHelper.sumLastNDays(7);
        float avg = dbHelper.avgLastNDays(7);
        averageTv.setText(String.format(Locale.getDefault(), "%.1f", avg));
        totalTv.setText(String.valueOf(total));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showDayChart() {
        List<Pair<String, Integer>> data = dbHelper.getLastNDaysCounts(7);
        updateChart(data);
    }

    private void showMonthChart() {
        List<Pair<String, Integer>> data = dbHelper.getLastNMonthsCounts(7);
        updateChart(data);
    }

    private void showYearChart() {
        List<Pair<String, Integer>> data = dbHelper.getLastNYearsCounts(7);
        updateChart(data);
    }

    private void updateChart(List<Pair<String, Integer>> data) {
        //准备 Entry 列表
        List<Entry> entries = new ArrayList<>();
        final List<String> labels = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Pair<String, Integer> p = data.get(i);
            entries.add(new Entry(i, p.second));
            labels.add(p.first);
        }
        //构建 DataSet
        LineDataSet set = new LineDataSet(entries, "访客量");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCircleRadius(4f);
        set.setLineWidth(2f);
        set.setValueTextSize(10f);
        //构建 LineData 并设置到 chart
        LineData lineData = new LineData(set);
        chart.setData(lineData);
        //X轴标签替换
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        // 把标签按索引映射
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        //刷新
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
    }
}