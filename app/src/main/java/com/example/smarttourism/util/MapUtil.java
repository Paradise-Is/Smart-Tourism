package com.example.smarttourism.util;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.example.smarttourism.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

//地图帮助类
public class MapUtil {
    //把LatLng对象转化为LatLonPoint对象
    public static LatLonPoint convertToLatLonPoint(LatLng latLng) {
        return new LatLonPoint(latLng.latitude, latLng.longitude);
    }

    //把LatLonPoint对象转化为LatLon对象
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    //把集合体的LatLonPoint转化为集合体的LatLng
    public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
        ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
        for (LatLonPoint point : shapes) {
            LatLng latLngTemp = convertToLatLng(point);
            lineShapes.add(latLngTemp);
        }
        return lineShapes;
    }

    public static String getFriendlyTime(int second) {
        if (second > 3600) {
            int hour = second / 3600;
            int miniate = (second % 3600) / 60;
            return hour + "小时" + miniate + "分钟";
        }
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "分钟";
        }
        return second + "秒";
    }

    public static String getFriendlyLength(int lenMeter) {
        // 10 km
        if (lenMeter > 10000) {
            int dis = lenMeter / 1000;
            return dis + ChString.Kilometer;
        }
        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + ChString.Kilometer;
        }
        if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + ChString.Meter;
        }
        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }
        return dis + ChString.Meter;
    }

    public static int getWalkActionID(String actionName) {
        if (actionName == null || actionName.equals("")) {
            return R.mipmap.dir13;
        }
        if ("左转".equals(actionName)) {
            return R.mipmap.dir2;
        }
        if ("右转".equals(actionName)) {
            return R.mipmap.dir1;
        }
        if ("向左前方".equals(actionName) || "靠左".equals(actionName) || actionName.contains("向左前方")) {
            return R.mipmap.dir6;
        }
        if ("向右前方".equals(actionName) || "靠右".equals(actionName) || actionName.contains("向右前方")) {
            return R.mipmap.dir5;
        }
        if ("向左后方".equals(actionName)|| actionName.contains("向左后方")) {
            return R.mipmap.dir7;
        }
        if ("向右后方".equals(actionName)|| actionName.contains("向右后方")) {
            return R.mipmap.dir8;
        }
        if ("直行".equals(actionName)) {
            return R.mipmap.dir3;
        }
        if ("通过人行横道".equals(actionName)) {
            return R.mipmap.dir9;
        }
        if ("通过过街天桥".equals(actionName)) {
            return R.mipmap.dir11;
        }
        if ("通过地下通道".equals(actionName)) {
            return R.mipmap.dir10;
        }
        return R.mipmap.dir13;
    }

    public static int getDriveActionID(String actionName) {
        if (actionName == null || actionName.isEmpty()) {
            return R.mipmap.dir3;
        }
        if ("左转".equals(actionName)) {
            return R.mipmap.dir2;
        }
        if ("右转".equals(actionName)) {
            return R.mipmap.dir1;
        }
        if ("向左前方行驶".equals(actionName) || "靠左".equals(actionName)) {
            return R.mipmap.dir6;
        }
        if ("向右前方行驶".equals(actionName) || "靠右".equals(actionName)) {
            return R.mipmap.dir5;
        }
        if ("向左后方行驶".equals(actionName) || "左转调头".equals(actionName)) {
            return R.mipmap.dir7;
        }
        if ("向右后方行驶".equals(actionName)) {
            return R.mipmap.dir8;
        }
        if ("直行".equals(actionName)) {
            return R.mipmap.dir3;
        }
        if ("减速行驶".equals(actionName)) {
            return R.mipmap.dir4;
        }
        return R.mipmap.dir3;
    }

}


