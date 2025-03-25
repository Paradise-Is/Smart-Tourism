package com.example.smarttourism.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.example.smarttourism.R;
import com.example.smarttourism.databinding.UserMainBinding;

public class UserMainActivity extends AppCompatActivity implements AMapLocationListener, LocationSource {
    private static final String TAG = "UserMainActivity";
    private UserMainBinding binding;
    //请求权限意图
    private ActivityResultLauncher<String> requestPermission;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    // 声明地图控制器
    private AMap aMap = null;
    // 声明地图定位监听
    private LocationSource.OnLocationChangedListener mListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //申请位置权限
        requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            // 权限申请结果
            Log.d(TAG, "权限申请结果: " + result);
            showMsg(result ? "已获取到权限" : "权限申请失败");
        });
        //构建用户主页界面
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //实现视图绑定
        binding = UserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //根据系统窗口的内边距动态设置视图的padding，避免内容被遮挡
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //初始化定位
        initLocation();
        //绑定生命周期onCreate
        binding.mapView.onCreate(savedInstanceState);
        //初始化地图
        initMap();
    }

    //初始化位置信息
    private void initLocation() {
        try {
            //初始化定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
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
            // 设置定位监听
            aMap.setLocationSource(this);
            // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
            aMap.setMyLocationEnabled(true);
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
    protected void onResume() {
        super.onResume();
        // 绑定生命周期 onResume
        binding.mapView.onResume();
        // 检查是否已经获取到定位权限
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 获取到权限
            startLocation();
        } else {
            // 请求定位权限
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 绑定生命周期 onPause
        binding.mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // 绑定生命周期 onSaveInstanceState
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 绑定生命周期 onDestroy
        binding.mapView.onDestroy();
    }

    //显示提示消息
    private void showMsg(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null) {
            showMsg("定位失败，aMapLocation 为空");
            return;
        }
        // 获取定位结果
        if (aMapLocation.getErrorCode() == 0) {
            // 定位成功
            showMsg("定位成功");
//            aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//            aMapLocation.getLatitude();//获取纬度
//            aMapLocation.getLongitude();//获取经度
//            aMapLocation.getAccuracy();//获取精度信息
//            aMapLocation.getAddress();//详细地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//            aMapLocation.getCountry();//国家信息
//            aMapLocation.getProvince();//省信息
//            aMapLocation.getCity();//城市信息
//            aMapLocation.getDistrict();//城区信息
//            aMapLocation.getStreet();//街道信息
//            aMapLocation.getStreetNum();//街道门牌号信息
//            aMapLocation.getCityCode();//城市编码
//            aMapLocation.getAdCode();//地区编码
//            aMapLocation.getAoiName();//获取当前定位点的AOI信息
//            aMapLocation.getBuildingId();//获取当前室内定位的建筑物Id
//            aMapLocation.getFloor();//获取当前室内定位的楼层
//            aMapLocation.getGpsAccuracyStatus();//获取GPS的当前状态
            // 停止定位
            stopLocation();
            // 显示地图定位结果
            if (mListener != null) {
                mListener.onLocationChanged(aMapLocation);
            }
        } else {
            // 定位失败
            showMsg("定位失败，错误：" + aMapLocation.getErrorInfo());
            Log.e(TAG, "location Error, ErrCode:"
                    + aMapLocation.getErrorCode() + ", errInfo:"
                    + aMapLocation.getErrorInfo());
        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
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
}
