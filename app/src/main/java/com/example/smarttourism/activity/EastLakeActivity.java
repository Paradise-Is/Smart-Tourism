package com.example.smarttourism.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.example.smarttourism.R;
import com.example.smarttourism.util.DBHelper;

import java.util.List;

public class EastLakeActivity extends Activity implements AMap.OnMapClickListener {
    String username;
    private MapView mapView;
    private AMap aMap;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.east_lake);
        //获取组件
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        //实现数据库功能
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.open();
        //获取用户用户名
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        //获取 AMap 对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        //设置初始视图和限制范围
        setupEastLakeMap();
    }

    private void setupEastLakeMap() {
        //东湖中心点坐标
        LatLng center = new LatLng(30.5537, 114.4120);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10f));
        //限制用户只能在此矩形范围内拖拽/缩放
        LatLngBounds wuhanBounds = new LatLngBounds(
                //西南角位置
                new LatLng(30.5170, 114.335),
                //东北角位置
                new LatLng(30.6041, 114.4805)
        );
        aMap.setMapStatusLimits(wuhanBounds);
        //禁用旋转、倾斜
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        //隐藏缩放按钮
        uiSettings.setZoomControlsEnabled(false);
        //设置地图点击事件
        aMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        double clickLon = latLng.longitude;
        double clickLat = latLng.latitude;
        //调用空间查询，半径100米内有哪些 POI
        List<Pair<Integer, String>> nearby = dbHelper.queryPOIsNearby(clickLon, clickLat, 500);
        if (nearby.isEmpty()) {
            // 没有找到任何景点
            Toast.makeText(EastLakeActivity.this, "此处不是东湖主要景点", Toast.LENGTH_SHORT).show();
        } else if (nearby.size() == 1) {
            //刚好一个景点，直接跳转
            int sightId = nearby.get(0).first;
            Intent intent = new Intent(EastLakeActivity.this, SightInfoActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("sight_id", sightId);
            startActivity(intent);
        } else {
            //超过一个景点，弹窗允许用户选择
            String[] names = new String[nearby.size()];
            for (int i = 0; i < nearby.size(); i++) {
                names[i] = nearby.get(i).second;
            }
            new AlertDialog.Builder(EastLakeActivity.this)
                    .setTitle("请选择景点")
                    .setItems(names, (dialog, which) -> {
                        int sightId = nearby.get(which).first;
                        Intent intent = new Intent(EastLakeActivity.this, SightInfoActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("sight_id", sightId);
                        startActivity(intent);
                    }).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
