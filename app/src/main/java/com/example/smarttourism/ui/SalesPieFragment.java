package com.example.smarttourism.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.smarttourism.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class SalesPieFragment extends Fragment {
    private TabLayout timeTab;
    private ViewPager chartPager;
    private SalesPagerAdapter adapter;
    private List<String> tabIndicators;
    private List<Fragment> tabFragments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_pie, container, false);
        //获取组件
        timeTab = (TabLayout) view.findViewById(R.id.timeTab);
        chartPager = (ViewPager) view.findViewById(R.id.chartPager);
        //初始化
        initTab();
        initContent();
        return view;
    }

    //初始化标签栏格式
    private void initTab() {
        timeTab.setTabMode(TabLayout.MODE_FIXED);
        timeTab.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.black), ContextCompat.getColor(requireContext(), R.color.purple));
        timeTab.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.purple));
        ViewCompat.setElevation(timeTab, 20);
        timeTab.setupWithViewPager(chartPager);
    }

    //初始化标签栏内容
    private void initContent() {
        tabIndicators = new ArrayList<>();
        tabIndicators.add("日");
        tabIndicators.add("周");
        tabIndicators.add("月");
        tabIndicators.add("年");
        tabFragments = new ArrayList<>();
        for (String s : tabIndicators) {
            tabFragments.add(SalesTimeFragment.newInstance(s));
        }
        adapter = new SalesPieFragment.SalesPagerAdapter(getChildFragmentManager());
        chartPager.setAdapter(adapter);
    }

    public class SalesPagerAdapter extends FragmentPagerAdapter {
        public SalesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "日";
                case 1:
                    return "周";
                case 2:
                    return "月";
                default:
                    return "年";
            }
        }
    }
}
