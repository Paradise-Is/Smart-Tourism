package com.example.smarttourism.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smarttourism.R;
import com.example.smarttourism.adapter.AnalysisPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AnalysisFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AnalysisPagerAdapter adapter;
    private final String[] titles = { "景区访客量", "景点销售额", "景区投诉数" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_analysis, container, false);
        tabLayout  = (TabLayout) view.findViewById(R.id.analysisTab);
        viewPager  = (ViewPager2) view.findViewById(R.id.analysisPager);
        adapter = new AnalysisPagerAdapter(this);
        viewPager.setAdapter(adapter);
        //关联TabLayout与ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(titles[position])).attach();
        return view;
    }
}
