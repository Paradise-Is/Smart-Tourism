package com.example.smarttourism.overlay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkStep;

import java.util.List;

//步行路线图层类，在高德地图API里，如果要显示步行路线规划，可以用此类来创建步行路线图层
public class WalkRouteOverlay extends RouteOverlay {
    private PolylineOptions mPolylineOptions;
    private BitmapDescriptor walkStationDescriptor = null;
    private WalkPath walkPath;

    //通过此构造函数创建步行路线图层
    public WalkRouteOverlay(Context context, AMap amap, WalkPath path,
                            LatLonPoint start, LatLonPoint end) {
        super(context);
        this.mAMap = amap;
        this.walkPath = path;
        startPoint = AMapServicesUtil.convertToLatLng(start);
        endPoint = AMapServicesUtil.convertToLatLng(end);
    }

    //添加步行路线到地图中
    public void addToMap() {
        initPolylineOptions();
        try {
            List<WalkStep> walkPaths = walkPath.getSteps();
            for (int i = 0; i < walkPaths.size(); i++) {
                WalkStep walkStep = walkPaths.get(i);
                LatLng latLng = AMapServicesUtil.convertToLatLng(walkStep
                        .getPolyline().get(0));
                addWalkStationMarkers(walkStep, latLng);
                addWalkPolyLines(walkStep);
            }
            addStartAndEndMarker();
            showPolyline();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //检查这一步的最后一点和下一步的起始点之间是否存在空隙
    private void checkDistanceToNextStep(WalkStep walkStep, WalkStep walkStep1) {
        LatLonPoint lastPoint = getLastWalkPoint(walkStep);
        LatLonPoint nextFirstPoint = getFirstWalkPoint(walkStep1);
        if (!(lastPoint.equals(nextFirstPoint))) {
            addWalkPolyLine(lastPoint, nextFirstPoint);
        }
    }

    private LatLonPoint getLastWalkPoint(WalkStep walkStep) {
        return walkStep.getPolyline().get(walkStep.getPolyline().size() - 1);
    }

    private LatLonPoint getFirstWalkPoint(WalkStep walkStep) {
        return walkStep.getPolyline().get(0);
    }

    private void addWalkPolyLine(LatLonPoint pointFrom, LatLonPoint pointTo) {
        addWalkPolyLine(AMapServicesUtil.convertToLatLng(pointFrom), AMapServicesUtil.convertToLatLng(pointTo));
    }

    private void addWalkPolyLine(LatLng latLngFrom, LatLng latLngTo) {
        mPolylineOptions.add(latLngFrom, latLngTo);
    }

    private void addWalkPolyLines(WalkStep walkStep) {
        mPolylineOptions.addAll(AMapServicesUtil.convertArrList(walkStep.getPolyline()));
    }

    private void addWalkStationMarkers(WalkStep walkStep, LatLng position) {
        addStationMarker(new MarkerOptions()
                .position(position)
                .title("\u65B9\u5411:" + walkStep.getAction()
                        + "\n\u9053\u8DEF:" + walkStep.getRoad())
                .snippet(walkStep.getInstruction()).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(walkStationDescriptor));
    }

    private void initPolylineOptions() {
        if (walkStationDescriptor == null) {
            walkStationDescriptor = getWalkBitmapDescriptor();
        }
        mPolylineOptions = null;
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getWalkColor()).width(getRouteWidth());
    }

    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }
}


