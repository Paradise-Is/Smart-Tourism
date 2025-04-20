package com.example.smarttourism.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class SurroundingsFragment extends Fragment {
    private EditText searchEt;
    private TextView searchBt;
    private TabLayout mapTab;
    private ViewPager mapPager;
    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private TabPagerAdapter tabPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_surroundings, container, false);
        //获取组件
        searchEt = (EditText) view.findViewById(R.id.searchEt);
        searchBt = (TextView) view.findViewById(R.id.searchBt);
        mapTab = (TabLayout) view.findViewById(R.id.mapTab);
        mapPager = (ViewPager) view.findViewById(R.id.mapPager);
        initContent();
        initTab();
        //搜索按钮点击事件
        searchBt.setOnClickListener(new SearchBtListener());
        return view;
    }

    //初始化标签栏格式
    private void initTab() {
        mapTab.setTabMode(TabLayout.MODE_FIXED);
        mapTab.setTabTextColors(ContextCompat.getColor(requireContext(), R.color.black), ContextCompat.getColor(requireContext(), R.color.purple));
        mapTab.setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.purple));
        ViewCompat.setElevation(mapTab, 20);
        mapTab.setupWithViewPager(mapPager);
    }

    //初始化标签栏内容
    private void initContent() {
        tabIndicators = new ArrayList<>();
        tabIndicators.add("当前周边");
        tabIndicators.add("景区景点");
        tabIndicators.add("景区餐饮");
        tabIndicators.add("景区酒店");
        tabFragments = new ArrayList<>();
        for (String s : tabIndicators) {
            tabFragments.add(SightsFragment.newInstance(s));
        }
        tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        mapPager.setAdapter(tabPagerAdapter);
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
            if (position == 0)
                return "当前周边";
            else if (position == 1)
                return "景区景点";
            else if (position == 2)
                return "景区餐饮";
            else
                return "景区酒店";
        }
    }

    private class SearchBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String keyword = searchEt.getText().toString().trim();
            if (keyword.isEmpty()) {
                Toast.makeText(getActivity(), "请输入搜索关键字", Toast.LENGTH_SHORT).show();
            } else {
                int idx = mapPager.getCurrentItem();
                SightsFragment f = (SightsFragment) tabFragments.get(idx);
                f.doSearch(keyword);
            }
        }
    }
}
