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
        //构造饼图
        List<PieEntry> entries = new ArrayList<>();
        List<SaleItem> items = new ArrayList<>();
        for (Map.Entry<Integer, Float> e : map.entrySet()) {
            int sightId = e.getKey();
            float amt = e.getValue();
            String name = dbHelper.getSightName(sightId);
            entries.add(new PieEntry(amt, name));
            items.add(new SaleItem(name, String.format(Locale.getDefault(), "%.2f", amt)));
        }
        PieDataSet set = new PieDataSet(entries, period + " 景点销售额分布");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setValueTextSize(12f);
        salesPieChart.setData(new PieData(set));
        salesPieChart.getDescription().setEnabled(false);
        salesPieChart.animateY(600);
        //装配 RecyclerView 列表
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(new SaleItemAdapter(requireContext(), items));
    }
}
