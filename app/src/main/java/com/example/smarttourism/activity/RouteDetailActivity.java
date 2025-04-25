package com.example.smarttourism.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.WalkPath;
import com.example.smarttourism.R;
import com.example.smarttourism.adapter.BusSegmentListAdapter;
import com.example.smarttourism.adapter.DriveSegmentListAdapter;
import com.example.smarttourism.adapter.RideSegmentListAdapter;
import com.example.smarttourism.adapter.WalkSegmentListAdapter;
import com.example.smarttourism.util.MapUtil;
import com.example.smarttourism.util.SchemeBusStep;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class RouteDetailActivity extends Activity {
    private MaterialToolbar toolbar;
    private TextView timeTv;
    private RecyclerView routeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建路径规划详情界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        //获取组件
        toolbar = this.findViewById(R.id.toolbar);
        timeTv = this.findViewById(R.id.timeTv);
        routeDetail = this.findViewById(R.id.routeDetail);
        initView();
    }

    //初始化视图
    private void initView() {
        toolbar.setNavigationOnClickListener(v -> finish());
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        switch (intent.getIntExtra("type", 0)) {
            //步行
            case 0:
                walkDetail(intent);
                break;
            //骑行
            case 1:
                rideDetail(intent);
                break;
            //驾车
            case 2:
                driveDetail(intent);
                break;
            //公交
            case 3:
                busDetail(intent);
                break;
            default:
                break;
        }
    }

    //步行详情
    private void walkDetail(Intent intent) {
        toolbar.setTitle("步行路线规划");
        WalkPath walkPath = intent.getParcelableExtra("path");
        String dur = MapUtil.getFriendlyTime((int) walkPath.getDuration());
        String dis = MapUtil.getFriendlyLength((int) walkPath.getDistance());
        timeTv.setText(dur + "(" + dis + ")");
        routeDetail.setLayoutManager(new LinearLayoutManager(this));
        routeDetail.setAdapter(new WalkSegmentListAdapter(walkPath.getSteps()));
    }

    //骑行详情
    private void rideDetail(Intent intent) {
        toolbar.setTitle("骑行路线规划");
        RidePath ridePath = intent.getParcelableExtra("path");
        String dur = MapUtil.getFriendlyTime((int) ridePath.getDuration());
        String dis = MapUtil.getFriendlyLength((int) ridePath.getDistance());
        timeTv.setText(dur + "(" + dis + ")");
        routeDetail.setLayoutManager(new LinearLayoutManager(this));
        routeDetail.setAdapter(new RideSegmentListAdapter(ridePath.getSteps()));
    }

    //驾车详情
    private void driveDetail(Intent intent) {
        toolbar.setTitle("驾车路线规划");
        DrivePath drivePath = intent.getParcelableExtra("path");
        String dur = MapUtil.getFriendlyTime((int) drivePath.getDuration());
        String dis = MapUtil.getFriendlyLength((int) drivePath.getDistance());
        timeTv.setText(dur + "(" + dis + ")");
        routeDetail.setLayoutManager(new LinearLayoutManager(this));
        routeDetail.setAdapter(new DriveSegmentListAdapter(drivePath.getSteps()));
    }

    //公交方案数据组装
    private List<SchemeBusStep> getBusSteps(List<BusStep> list) {
        List<SchemeBusStep> busStepList = new ArrayList<>();
        SchemeBusStep start = new SchemeBusStep(null);
        start.setStart(true);
        busStepList.add(start);
        for (BusStep busStep : list) {
            if (busStep.getWalk() != null && busStep.getWalk().getDistance() > 0) {
                SchemeBusStep walk = new SchemeBusStep(busStep);
                walk.setWalk(true);
                busStepList.add(walk);
            }
            if (busStep.getBusLine() != null) {
                SchemeBusStep bus = new SchemeBusStep(busStep);
                bus.setBus(true);
                busStepList.add(bus);
            }
            if (busStep.getRailway() != null) {
                SchemeBusStep railway = new SchemeBusStep(busStep);
                railway.setRailway(true);
                busStepList.add(railway);
            }

            if (busStep.getTaxi() != null) {
                SchemeBusStep taxi = new SchemeBusStep(busStep);
                taxi.setTaxi(true);
                busStepList.add(taxi);
            }
        }
        SchemeBusStep end = new SchemeBusStep(null);
        end.setEnd(true);
        busStepList.add(end);
        return busStepList;
    }

    //公交详情
    private void busDetail(Intent intent) {
        toolbar.setTitle("公交路线规划");
        BusPath busPath = intent.getParcelableExtra("path");
        String dur = MapUtil.getFriendlyTime((int) busPath.getDuration());
        String dis = MapUtil.getFriendlyLength((int) busPath.getDistance());
        timeTv.setText(dur + "(" + dis + ")");
        routeDetail.setLayoutManager(new LinearLayoutManager(this));
        routeDetail.setAdapter(new BusSegmentListAdapter(getBusSteps(busPath.getSteps())));
    }
}

