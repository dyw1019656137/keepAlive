package com.dyw.keepalive;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;


import java.util.List;

import static com.dyw.keepalive.service.GpsService.TAG;

public class GPSUtil {
    // GSP
    private static LocationManager locationManager;
    private static Criteria criteria;
    private static String provider;
    private static int getTime = 10000;// 每10秒获得一次
    private static int distance = 5;// 距离5米以上

    public static final String[] PERMISSION_LOCATION = new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};

    public static boolean checkGPS(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);// 获取位置管理服务
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    // 初始化
    public static GPS getGPS(Context context) {
        GPS gps = null;
        if (checkGPS(context)) {
            if (locationListener!=null){
                locationManager=null;
            }
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
            criteria.setAltitudeRequired(false);// 显示海拔
            criteria.setBearingRequired(false);// 显示方向
            criteria.setSpeedRequired(false);// 显示速度
            criteria.setCostAllowed(false);// 不允许有花费
            criteria.setPowerRequirement(Criteria.POWER_LOW);// 低功耗
            provider = locationManager.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                PermissionUtil.startRequestPermissions(context, PERMISSION_LOCATION, PermissionUtil.REQUESTCODE_PERMISSION_LOCATION);
                return gps;
            } else {
//                Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
                Location location = getLastKnownLocation(); // 通过GPS获取位置
                gps = getLocationGPS(location);
                locationManager.requestLocationUpdates(provider, getTime, distance, locationListener);// 位置变化监听
            }
        }
        return gps;
    }


    private static LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private static GPS getLocationGPS(Location location) {
        // 获取GPS相应的数据
        if (location != null) {
            GPS gps = new GPS();
            gps.Latitude = location.getLatitude();
            gps.Longitude = location.getLongitude();
            return gps;
        }
        return null;
    }

    private static Location getLastKnownLocation() {

        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
