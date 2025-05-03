package com.example.smarttourism.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourism.R;
import com.example.smarttourism.adapter.SaleItemAdapter;
import com.example.smarttourism.entity.SaleItem;
import com.example.smarttourism.util.DBHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalesTimeFragment extends Fragment {
    private static final String ARG_PERIOD = "period";
    private String period;
    private PieChart salesPieChart;
    private RecyclerView listView;
    private DBHelper dbHelper;

    public static SalesTimeFragment newInstance(String period) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_PERIOD, period);
        SalesTimeFragment fragment = new SalesTimeFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_time, container, false);
        //获取组件
        salesPieChart = view.findViewById(R.id.salesPieChart);
        listView = view.findViewById(R.id.salesList);
        //获取数据范围
        period = requireArguments().getString(ARG_PERIOD);
        //实现数据库功能
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadDataFor(period);
    }

    private void loadDataFor(String period) {
        //调用 DBHelper里的方法，根据 period 查找数据
        Map<Integer, Float> map = dbHelper.sumSalesByPeriod(period);
        //获取所有景点映射
        Map<Integer, String> allSights = dbHelper.getAllSightNames();
        //构造饼图
        List<PieEntry> entries = new ArrayList<>();
        List<SaleItem> items = new ArrayList<>();
        for (Map.Entry<Integer, String> sight : allSights.entrySet()) {
            int sightId = sight.getKey();
            String name = sight.getValue();
            float amt = map.getOrDefault(sightId, 0f);
            // 只展示大于 0 的扇区
            if (amt > 0f) {
                entries.add(new PieEntry(amt, name));
            }
            items.add(new SaleItem(name, String.format(Locale.getDefault(), "%.2f", amt)));
        }
        if (entries.isEmpty()) {
            salesPieChart.clear();
            salesPieChart.setNoDataText("当前周期暂无销售数据");
        } else {
            PieDataSet set = new PieDataSet(entries, "景点销售额分布");
            // 丰富配色
            List<Integer> colors = new ArrayList<>();
            for (int c : ColorTemplate.MATERIAL_COLORS) colors.add(c);
            for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);
            for (int c : ColorTemplate.COLORFUL_COLORS) colors.add(c);
            for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
            for (int c : ColorTemplate.PASTEL_COLORS) colors.add(c);
            set.setColors(colors);
            set.setValueTextSize(12f);
            salesPieChart.setUsePercentValues(true);
            PieData data = new PieData(set);
            data.setValueFormatter(new PercentFormatter(salesPieChart));
            data.setValueTextSize(12f);
            salesPieChart.clear();
            salesPieChart.setData(data);
            salesPieChart.getDescription().setEnabled(false);
            salesPieChart.animateY(600);
            salesPieChart.invalidate();
        }
        //装配 RecyclerView 列表
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(new SaleItemAdapter(requireContext(), items));
    }
}
