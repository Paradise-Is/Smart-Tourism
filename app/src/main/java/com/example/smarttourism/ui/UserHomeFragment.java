package com.example.smarttourism.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.smarttourism.R;
import com.example.smarttourism.activity.SightInfoActivity;
import com.example.smarttourism.databinding.MenuHomeBinding;
import com.example.smarttourism.util.DBHelper;

public class UserHomeFragment extends Fragment implements AMapLocationListener, LocationSource, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener {
    //解析成功标识码
    private static final int PARSE_SUCCESS_CODE = 1000;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //用户用户名
    private String username;
    private MenuHomeBinding binding;
    //请求权限意图
    private ActivityResultLauncher<String> requestPermission;
    //声明地图控制器
    private AMap aMap = null;
    //声明地图定位监听
    private LocationSource.OnLocationChangedListener mListener = null;
    //地理编码搜索
    private GeocodeSearch geocodeSearch;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //实现视图绑定
        binding = MenuHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //申请权限
        requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            //权限申请结果
            showMsg(result ? "已获取到权限" : "权限申请失败");
        });
        //对view进行内边距设置，避免内容被遮挡
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.tab_home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //实现数据库功能
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        //获取从Activity传来的数据
        Bundle args = getArguments();
        username = args.getString("username");
        //初始化定位与地图
        initLocation();
        binding.mapView.onCreate(savedInstanceState);
        initMap();
        initSearch();
    }

    //初始化位置信息
    private void initLocation() {
        try {
            //初始化定位
            mLocationClient = new AMapLocationClient(requireContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy（高精度模式）。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //获取最近3s内精度最高的一次定位结果
            mLocationOption.setOnceLocationLatest(true);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置定位超时时间，单位是毫秒
            mLocationOption.setHttpTimeOut(6000);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //初始化地图
    private void initMap() {
        if (aMap == null) {
            aMap = binding.mapView.getMap();
            //设置定位监听
            aMap.setLocationSource(this);
            //设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
            aMap.setMyLocationEnabled(true);
            //地图控件设置
            UiSettings uiSettings = aMap.getUiSettings();
            //隐藏缩放按钮
            uiSettings.setZoomControlsEnabled(false);
            //设置地图点击事件
            aMap.setOnMapClickListener(this);
        }
    }

    private void initSearch() {
        //构造 GeocodeSearch 对象
        try {
            geocodeSearch = new GeocodeSearch(requireContext());
            //设置监听
            geocodeSearch.setOnGeocodeSearchListener(this);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    //开始定位
    private void startLocation() {
        if (mLocationClient != null) mLocationClient.startLocation();
    }

    //停止定位
    private void stopLocation() {
        if (mLocationClient != null) mLocationClient.stopLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        //绑定生命周期 onResume
        binding.mapView.onResume();
        //检查是否已经获取到定位权限
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //获取到权限
            startLocation();
        } else {
            //请求定位权限
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //绑定生命周期 onPause
        binding.mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //绑定生命周期 onSaveInstanceState
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //绑定生命周期 onDestroy
        binding.mapView.onDestroy();
    }

    //显示提示消息
    private void showMsg(CharSequence msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            showMsg("定位失败，无法获取您当前位置");
            return;
        }
        // 获取定位结果
        if (aMapLocation.getErrorCode() == 0) {
            // 定位成功
            showMsg("定位成功");
            // 停止定位
            stopLocation();
            // 显示地图定位结果
            if (mListener != null) {
                mListener.onLocationChanged(aMapLocation);
            }
        } else {
            // 定位失败
            showMsg("定位失败，错误：" + aMapLocation.getErrorInfo());
        }
    }

    //激活定位
    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        if (mListener == null) {
            mListener = onLocationChangedListener;
        }
        startLocation();
    }

    //禁用定位
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
    public void onMapClick(LatLng latLng) {
        //查询所有东湖景点
        Cursor cursor = dbHelper.getDatabase().query("Sight",
                new String[]{
                        "id", "latitude", "longitude"
                },
                null, null, null, null, null
        );
        boolean found = false;
        float[] result = new float[1];
        double clickLat = latLng.latitude;
        double clickLon = latLng.longitude;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double lon = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
            //计算两点距离（米）
            Location.distanceBetween(clickLat, clickLon, lat, lon, result);
            //100米范围内视作命中
            if (result[0] <= 100) {
                found = true;
                //跳转到景点详情页
                Intent intent = new Intent(requireContext(), SightInfoActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("sight_id", id);
                startActivity(intent);
                break;
            }
        }
        cursor.close();
        if (!found) {
            //如果均未命中，提示
            showMsg("此处不是东湖主要景点");
        }
    }

    //坐标转地址
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
        //解析result获取地址描述信息
        if (rCode == PARSE_SUCCESS_CODE) {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            //显示解析后的地址
            showMsg("地址：" + regeocodeAddress.getFormatAddress());
        } else {
            showMsg("获取地址失败");
        }
    }

    //地址转坐标
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {

    }

    //通过经纬度获取地址
    private void latLonToAddress(LatLng latLng) {
        //位置点（通过经纬度进行构建）
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        //逆编码查询（第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系）
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);
        //异步获取地址信息
        geocodeSearch.getFromLocationAsyn(query);
    }
}
