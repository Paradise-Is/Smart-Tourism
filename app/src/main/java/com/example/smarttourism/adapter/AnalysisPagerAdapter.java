package com.example.smarttourism.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smarttourism.ui.ComplaintsBarFragment;
import com.example.smarttourism.ui.SalesPieFragment;
import com.example.smarttourism.ui.VisitorsLineFragment;

public class AnalysisPagerAdapter extends FragmentStateAdapter {
    public AnalysisPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new VisitorsLineFragment();
            case 1:
                return new SalesPieFragment();
            default:
                return new ComplaintsBarFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
