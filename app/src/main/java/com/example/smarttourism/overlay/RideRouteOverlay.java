package com.example.smarttourism.overlay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideStep;
import com.example.smarttourism.util.MapUtil;

import java.util.List;

//骑行路线图层类，在高德地图API里，如果要显示步行路线规划，可以用此类来创建骑行路线图层
public class RideRouteOverlay extends RouteOverlay {
    private PolylineOptions mPolylineOptions;
    private BitmapDescriptor walkStationDescriptor = null;
    private RidePath ridePath;

    //通过此构造函数创建骑行路线图层
    public RideRouteOverlay(Context context, AMap amap, RidePath path,
                            LatLonPoint start, LatLonPoint end) {
        super(context);
        this.mAMap = amap;
        this.ridePath = path;
        startPoint = MapUtil.convertToLatLng(start);
        endPoint = MapUtil.convertToLatLng(end);
    }

    //添加骑行路线到地图中
    public void addToMap() {

        initPolylineOptions();
        try {
            List<RideStep> ridePaths = ridePath.getSteps();
            for (int i = 0; i < ridePaths.size(); i++) {
                RideStep rideStep = ridePaths.get(i);
                LatLng latLng = MapUtil.convertToLatLng(rideStep.getPolyline().get(0));
                addRideStationMarkers(rideStep, latLng);
                addRidePolyLines(rideStep);
            }
            addStartAndEndMarker();
            showPolyline();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void addRidePolyLines(RideStep rideStep) {
        mPolylineOptions.addAll(MapUtil.convertArrList(rideStep.getPolyline()));
    }

    private void addRideStationMarkers(RideStep rideStep, LatLng position) {
        addStationMarker(new MarkerOptions()
                .position(position)
                .title("\u65B9\u5411:" + rideStep.getAction()
                        + "\n\u9053\u8DEF:" + rideStep.getRoad())
                .snippet(rideStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(walkStationDescriptor));
    }

    //初始化线段属性
    private void initPolylineOptions() {
        if (walkStationDescriptor == null) {
            walkStationDescriptor = getRideBitmapDescriptor();
        }
        mPolylineOptions = null;
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getDriveColor()).width(getRouteWidth());
    }

    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }
}


