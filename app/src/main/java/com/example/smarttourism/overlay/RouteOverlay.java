package com.example.smarttourism.overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.smarttourism.R;

import java.util.ArrayList;
import java.util.List;

//路线图层叠加
public class RouteOverlay {
    protected List<Marker> stationMarkers = new ArrayList<Marker>();
    protected List<Polyline> allPolyLines = new ArrayList<Polyline>();
    protected Marker startMarker;
    protected Marker endMarker;
    protected LatLng startPoint;
    protected LatLng endPoint;
    protected AMap mAMap;
    protected boolean nodeIconVisible = true;
    private Context mContext;
    private Bitmap startBit, endBit, busBit, walkBit, driveBit;

    public RouteOverlay(Context context) {
        mContext = context;
    }

    //去掉BusRouteOverlay上所有的Marker。
    public void removeFromMap() {
        if (startMarker != null) {
            startMarker.remove();
        }
        if (endMarker != null) {
            endMarker.remove();
        }
        for (Marker marker : stationMarkers) {
            marker.remove();
        }
        for (Polyline line : allPolyLines) {
            line.remove();
        }
        destroyBit();
    }

    private void destroyBit() {
        if (startBit != null) {
            startBit.recycle();
            startBit = null;
        }
        if (endBit != null) {
            endBit.recycle();
            endBit = null;
        }
        if (busBit != null) {
            busBit.recycle();
            busBit = null;
        }
        if (walkBit != null) {
            walkBit.recycle();
            walkBit = null;
        }
        if (driveBit != null) {
            driveBit.recycle();
            driveBit = null;
        }
    }

    //给起点Marker设置图标，并返回更换图标的图片
    protected BitmapDescriptor getStartBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.start);
    }

    //给终点Marker设置图标，并返回更换图标的图片
    protected BitmapDescriptor getEndBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.end);
    }

    //给公交Marker设置图标，并返回更换图标的图片
    protected BitmapDescriptor getBusBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.map_bus);
    }

    //给步行Marker设置图标，并返回更换图标的图片
    protected BitmapDescriptor getWalkBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.map_man);
    }

    //给骑行Marker设置图标，并返回更换图标的图片
    protected BitmapDescriptor getRideBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.map_bike);
    }

    //给自驾Marker设置图标，并返回更换图标的图片
    protected BitmapDescriptor getDriveBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.map_car);
    }

    protected void addStartAndEndMarker() {
        startMarker = mAMap.addMarker((new MarkerOptions())
                .position(startPoint).icon(getStartBitmapDescriptor())
                .title("\u8D77\u70B9"));
        endMarker = mAMap.addMarker((new MarkerOptions()).position(endPoint)
                .icon(getEndBitmapDescriptor()).title("\u7EC8\u70B9"));
    }

    //移动镜头到当前的视角
    public void zoomToSpan() {
        if (startPoint != null) {
            if (mAMap == null) {
                return;
            }
            try {
                LatLngBounds bounds = getLatLngBounds();
                mAMap.animateCamera(CameraUpdateFactory
                        .newLatLngBounds(bounds, 100));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startPoint.latitude, startPoint.longitude));
        b.include(new LatLng(endPoint.latitude, endPoint.longitude));
        return b.build();
    }

    //路段节点图标控制显示接口
    public void setNodeIconVisibility(boolean visible) {
        try {
            nodeIconVisible = visible;
            if (this.stationMarkers != null && this.stationMarkers.size() > 0) {
                for (int i = 0; i < this.stationMarkers.size(); i++) {
                    this.stationMarkers.get(i).setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void addStationMarker(MarkerOptions options) {
        if (options == null) {
            return;
        }
        Marker marker = mAMap.addMarker(options);
        if (marker != null) {
            stationMarkers.add(marker);
        }

    }

    protected void addPolyLine(PolylineOptions options) {
        if (options == null) {
            return;
        }
        Polyline polyline = mAMap.addPolyline(options);
        if (polyline != null) {
            allPolyLines.add(polyline);
        }
    }

    protected float getRouteWidth() {
        return 18f;
    }

    protected int getWalkColor() {
        return Color.parseColor("#6db74d");
    }

    //自定义路线颜色
    protected int getBusColor() {
        return Color.parseColor("#537edc");
    }

    protected int getDriveColor() {
        return Color.parseColor("#537edc");
    }
}


