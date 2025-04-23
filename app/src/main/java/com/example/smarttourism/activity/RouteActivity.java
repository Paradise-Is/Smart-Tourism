package com.example.smarttourism.activity;

import static com.example.smarttourism.util.MapUtil.convertToLatLng;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.smarttourism.R;
import com.example.smarttourism.overlay.BusRouteOverlay;
import com.example.smarttourism.overlay.DrivingRouteOverlay;
import com.example.smarttourism.overlay.RideRouteOverlay;
import com.example.smarttourism.overlay.WalkRouteOverlay;
import com.example.smarttourism.util.MapUtil;

public class RouteActivity extends Activity implements AMapLocationListener, LocationSource, RouteSearch.OnRouteSearchListener {
    //出行方式数组
    private static final String[] travelModeArray = {"步行出行", "骑行出行", "驾车出行", "公交出行"};
    //出行方式值
    private static int TRAVEL_MODE = 0;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //城市
    private String city;
    //用户当前位置
    private Double startLatitude, startLongitude;
    //目标点位置
    private Double endLatitude, endLongitude;
    //起点及终点
    private LatLonPoint startPoint, endPoint;
    //路线搜索对象
    private RouteSearch routeSearch;
    //地图控制器
    private AMap aMap = null;
    //位置更改监听
    private LocationSource.OnLocationChangedListener mListener;
    //定义一个UiSettings对象
    private UiSettings mUiSettings;
    //定位样式
    private MyLocationStyle myLocationStyle = new MyLocationStyle();
    private MapView mapView;
    private ImageView backBt;
    private ConstraintLayout layBottom;
    private TextView timeTv;
    private TextView detailTv;
    //数组适配器
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //构建路径规划界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        //获取组件
        mapView = findViewById(R.id.map_view);
        backBt = findViewById(R.id.backBt);
        layBottom = findViewById(R.id.layBottom);
        timeTv = findViewById(R.id.timeTv);
        detailTv = findViewById(R.id.detailTv);
        //获取目标点位置
        Intent intent = getIntent();
        endLatitude = intent.getDoubleExtra("endLatitude", 0.0);
        endLongitude = intent.getDoubleExtra("endLongitude", 0.0);
        //设置终点
        endPoint = MapUtil.convertToLatLonPoint(new LatLng(endLatitude, endLongitude));
        //初始化定位
        initLocation();
        //初始化地图
        initMap(savedInstanceState);
        //启动定位
        mLocationClient.startLocation();
        //初始化路线
        initRoute();
        //初始化出行方式
        initTravelMode();
        //实现点击按钮响应
        backBt.setOnClickListener(new BackBtListener());
    }

    //初始化定位
    private void initLocation() {
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLocationClient != null) {
            mLocationClient.setLocationListener(this);
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocationLatest(true);
            mLocationOption.setNeedAddress(true);
            mLocationOption.setHttpTimeOut(20000);
            mLocationOption.setLocationCacheEnable(false);
            mLocationClient.setLocationOption(mLocationOption);
        }
    }

    //初始化地图
    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        aMap = mapView.getMap();
        //实例化UiSettings类对象
        mUiSettings = aMap.getUiSettings();
        //隐藏缩放按钮 默认显示
        mUiSettings.setZoomControlsEnabled(false);
        //显示比例尺 默认不显示
        mUiSettings.setScaleControlsEnabled(true);
        //设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
        //设置定位监听
        aMap.setLocationSource(this);
        //设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
    }

    //初始化路线
    private void initRoute() {
        try {
            routeSearch = new RouteSearch(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        routeSearch.setRouteSearchListener(this);
    }

    //初始化出行方式
    private void initTravelMode() {
        Spinner spinner = findViewById(R.id.spinner);
        //将可选内容与ArrayAdapter连接起来
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, travelModeArray);
        //设置下拉列表的风格
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(arrayAdapter);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TRAVEL_MODE = position;
                //模式切换后，重新画一次路线
                if (startPoint != null && endPoint != null) {
                    //清除地图和底部信息
                    aMap.clear();
                    layBottom.setVisibility(View.GONE);
                    startRouteSearch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //地址
                String address = aMapLocation.getAddress();
                //获取纬度
                startLatitude = aMapLocation.getLatitude();
                //获取经度
                startLongitude = aMapLocation.getLongitude();
                //设置起点
                startPoint = MapUtil.convertToLatLonPoint(new LatLng(startLatitude, startLongitude));
                //获取城市
                city = aMapLocation.getCity();
                //停止定位后，本地定位服务并不会被销毁
                mLocationClient.stopLocation();
                //显示地图定位结果
                if (mListener != null) {
                    // 显示系统图标
                    mListener.onLocationChanged(aMapLocation);
                }
                //开始路径搜索
                startRouteSearch();
            } else {
                //定位失败时
                Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁定位客户端，同时销毁本地定位服务。
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        mapView.onDestroy();
    }

    //公交规划路径结果
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int code) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (code != AMapException.CODE_AMAP_SUCCESS) {
            Toast.makeText(this, "错误码；" + code, Toast.LENGTH_SHORT).show();
            return;
        }
        if (busRouteResult == null || busRouteResult.getPaths() == null) {
            Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (busRouteResult.getPaths().isEmpty()) {
            Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            return;
        }
        final BusPath busPath = busRouteResult.getPaths().get(0);
        if (busPath == null) {
            return;
        }
        // 绘制路线
        BusRouteOverlay busRouteOverlay = new BusRouteOverlay(this, aMap, busPath, busRouteResult.getStartPos(), busRouteResult.getTargetPos());
        busRouteOverlay.removeFromMap();
        busRouteOverlay.addToMap();
        busRouteOverlay.zoomToSpan();
        int dis = (int) busPath.getDistance();
        int dur = (int) busPath.getDuration();
        String des = MapUtil.getFriendlyTime(dur) + "(" + MapUtil.getFriendlyLength(dis) + ")";
        //显示公交花费时间
        timeTv.setText(des);
        layBottom.setVisibility(View.VISIBLE);
        //跳转到路线详情页面
        detailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteActivity.this,
                        RouteDetailActivity.class);
                intent.putExtra("type", 3);
                intent.putExtra("path", busPath);
                startActivity(intent);
            }
        });
    }

    //驾车规划路径结果
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int code) {
        //清理地图上的所有覆盖物
        aMap.clear();
        if (code != AMapException.CODE_AMAP_SUCCESS) {
            Toast.makeText(this, "错误码；" + code, Toast.LENGTH_SHORT).show();
            return;
        }
        if (driveRouteResult == null || driveRouteResult.getPaths() == null) {
            Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (driveRouteResult.getPaths().isEmpty()) {
            Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            return;
        }
        final DrivePath drivePath = driveRouteResult.getPaths().get(0);
        if (drivePath == null) {
            return;
        }
        //绘制路线
        DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, aMap, drivePath, driveRouteResult.getStartPos(), driveRouteResult.getTargetPos(), null);
        drivingRouteOverlay.removeFromMap();
        drivingRouteOverlay.addToMap();
        drivingRouteOverlay.zoomToSpan();
        int dis = (int) drivePath.getDistance();
        int dur = (int) drivePath.getDuration();
        String des = MapUtil.getFriendlyTime(dur) + "(" + MapUtil.getFriendlyLength(dis) + ")";
        //显示驾车花费时间
        timeTv.setText(des);
        layBottom.setVisibility(View.VISIBLE);
        //跳转到路线详情页面
        detailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteActivity.this,
                        RouteDetailActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("path", drivePath);
                startActivity(intent);
            }
        });
    }

    //步行规划路径结果
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int code) {
        //清理地图上的所有覆盖物
        aMap.clear();
        if (code != AMapException.CODE_AMAP_SUCCESS) {
            Toast.makeText(this, "错误码；" + code, Toast.LENGTH_SHORT).show();
            return;
        }
        if (walkRouteResult == null || walkRouteResult.getPaths() == null) {
            Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (walkRouteResult.getPaths().isEmpty()) {
            Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            return;
        }
        final WalkPath walkPath = walkRouteResult.getPaths().get(0);
        if (walkPath == null) {
            return;
        }
        //绘制路线
        WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
        walkRouteOverlay.removeFromMap();
        walkRouteOverlay.addToMap();
        walkRouteOverlay.zoomToSpan();
        int dis = (int) walkPath.getDistance();
        int dur = (int) walkPath.getDuration();
        String des = MapUtil.getFriendlyTime(dur) + "(" + MapUtil.getFriendlyLength(dis) + ")";
        //显示步行花费时间
        timeTv.setText(des);
        layBottom.setVisibility(View.VISIBLE);
        //跳转到路线详情页面
        detailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteActivity.this,
                        RouteDetailActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("path", walkPath);
                startActivity(intent);
            }
        });
    }

    //骑行规划路径结果
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int code) {
        //清理地图上的所有覆盖物
        aMap.clear();
        if (code != AMapException.CODE_AMAP_SUCCESS) {
            Toast.makeText(this, "错误码；" + code, Toast.LENGTH_SHORT).show();
            return;
        }
        if (rideRouteResult == null || rideRouteResult.getPaths() == null) {
            Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rideRouteResult.getPaths().isEmpty()) {
            Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            return;
        }
        final RidePath ridePath = rideRouteResult.getPaths().get(0);
        if (ridePath == null) {
            return;
        }
        RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(this, aMap, ridePath, rideRouteResult.getStartPos(), rideRouteResult.getTargetPos());
        rideRouteOverlay.removeFromMap();
        rideRouteOverlay.addToMap();
        rideRouteOverlay.zoomToSpan();
        int dis = (int) ridePath.getDistance();
        int dur = (int) ridePath.getDuration();
        String des = MapUtil.getFriendlyTime(dur) + "(" + MapUtil.getFriendlyLength(dis) + ")";
        //显示骑行花费时间
        timeTv.setText(des);
        layBottom.setVisibility(View.VISIBLE);
        //跳转到路线详情页面
        detailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteActivity.this,
                        RouteDetailActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("path", ridePath);
                startActivity(intent);
            }
        });
    }


    private void startRouteSearch() {
        //在地图上添加起点Marker
        aMap.addMarker(new MarkerOptions().position(convertToLatLng(startPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.start)));
        //在地图上添加终点Marker
        aMap.addMarker(new MarkerOptions().position(convertToLatLng(endPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.end)));
        //搜索路线构建路径的起终点
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        //出行方式判断
        switch (TRAVEL_MODE) {
            //步行
            case 0:
                //构建步行路线搜索对象
                RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
                // 异步路径规划步行模式查询
                routeSearch.calculateWalkRouteAsyn(query);
                break;
            //骑行
            case 1:
                //构建骑行路线搜索对象
                RouteSearch.RideRouteQuery rideQuery = new RouteSearch.RideRouteQuery(fromAndTo, RouteSearch.WalkDefault);
                //骑行规划路径计算
                routeSearch.calculateRideRouteAsyn(rideQuery);
                break;
            //驾车
            case 2:
                //构建驾车路线搜索对象  剩余三个参数分别是：途经点、避让区域、避让道路
                RouteSearch.DriveRouteQuery driveQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.WalkDefault, null, null, "");
                //驾车规划路径计算
                routeSearch.calculateDriveRouteAsyn(driveQuery);
                break;
            //公交
            case 3:
                //构建驾车路线搜索对象 第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算,1表示计算
                RouteSearch.BusRouteQuery busQuery = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BusLeaseWalk, city, 0);
                //公交规划路径计算
                routeSearch.calculateBusRouteAsyn(busQuery);
                break;
            default:
                break;
        }
    }

    private class BackBtListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //直接关闭当前页面
            finish();
        }
    }
}

