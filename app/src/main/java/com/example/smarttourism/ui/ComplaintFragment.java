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

public class ComplaintFragment extends Fragment {
    private TabLayout tabComplaint;
    private ViewPager viewPager;
    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private TabPagerAdapter tabPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_complaint, container, false);
        //获取组件
        tabComplaint = (TabLayout) view.findViewById(R.id.tab_complaint);
        viewPager = (ViewPager) view.findViewById(R.id.pager_complaint);
        initContent();
        initTab();
        return view;
    }

    //初始化标签栏格式
    private void initTab() {
        tabComplaint.setTabMode(TabLayout.MODE_FIXED);
        tabComplaint.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.black), ContextCompat.getColor(requireContext(), R.color.purple));
        tabComplaint.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.purple));
        ViewCompat.setElevation(tabComplaint, 20);
        tabComplaint.setupWithViewPager(viewPager);
    }

    //初始化标签栏内容
    private void initContent() {
        tabIndicators = new ArrayList<>();
        tabIndicators.add("未处理");
        tabIndicators.add("处理中");
        tabIndicators.add("已处理");
        tabFragments = new ArrayList<>();
        for (String s : tabIndicators) {
            tabFragments.add(ComplaintStatusFragment.newInstance(s));
        }
        tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
    }

    public class TabPagerAdapter extends FragmentPagerAdapter {
        public TabPagerAdapter(FragmentManager fm) {
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
            if(position==0)
                return "未处理";
            else if(position==1)
                return "处理中";
            else
                return "已处理";
        }
    }
}
