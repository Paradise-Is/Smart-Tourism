package com.example.smarttourism.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.example.smarttourism.R;

public class UserLocationActivity extends Activity {
    //用户用户名
    private String username;
    //当前位置
    private Double currLat, currLon;
    private ImageView backBt;
    // 地图相关
    private MapView mapView;
    private AMap aMap;


    protected void onCreate(Bundle savedInstanceState) {
        //构建显示报警用户位置界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_location);
        //获取组件
        backBt = (ImageView) this.findViewById(R.id.backBt);
        mapView = findViewById(R.id.map_view);
        //获取当前位置
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currLat = intent.getDoubleExtra("latitude", 0.0);
        currLon = intent.getDoubleExtra("longitude", 0.0);
        //地图初始化
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        //隐藏缩放按钮
        aMap.getUiSettings().setZoomControlsEnabled(false);
        //禁用平移手势
        aMap.getUiSettings().setScrollGesturesEnabled(false);
        //定位并移动到指定位置
        LatLng location = new LatLng(currLat, currLon);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f));
        // 添加标记并显示信息窗
        Marker marker = aMap.addMarker(new MarkerOptions().position(location).title("用户" + username + "报警位置"));
        marker.showInfoWindow();
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //绑定生命周期 onPause
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //绑定生命周期 onDestroy
        mapView.onDestroy();
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }
}
