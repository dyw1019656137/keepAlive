package com.dyw.keepalive.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.dyw.keepalive.GPS;
import com.dyw.keepalive.GPSUtil;
import com.dyw.keepalive.LocationUtils;
import com.dyw.keepalive.R;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GpsService extends Service {

    public static String TAG = "-----GpsService-----";

    private NotificationManager notificationManager;
    private String notificationId = "serviceid";
    private String notificationName = "servicename";

    private ScheduledThreadPoolExecutor mScheduled;

    public GpsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: startService");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1,getNotification());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: endService");
        if (mScheduled != null){
            mScheduled = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mScheduled == null) {
            mScheduled = new ScheduledThreadPoolExecutor(1);
            mScheduled.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    //展示悬浮窗
//                    getGPSLocation();
//                    reportLocation();
                }
            }, 0, 500, TimeUnit.MILLISECONDS);
        }
        return START_STICKY;
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("开启定位监听")
                .setContentText("定位中");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(notificationId);
        }
        Notification notification = builder.build();
        return notification;
    }

    public void getGPSLocation() {
        Location gps = LocationUtils.getBestLocation(getApplicationContext(),null);
        if (gps == null) {
            //设置定位监听，因为GPS定位，第一次进来可能获取不到，通过设置监听，可以在有效的时间范围内获取定位信息
            LocationUtils.addLocationListener(getApplicationContext(), LocationManager.GPS_PROVIDER, new LocationUtils.ILocationListener() {
                @Override
                public void onSuccessLocation(Location location) {
                    if (location != null) {
                        Log.d(TAG, "onSuccessLocation: "+"gps onSuccessLocation location:  lat==" + location.getLatitude() + "     lng==" + location.getLongitude());
                    } else {
                        Log.d(TAG, "onSuccessLocation: "+"gps location is null");
                    }
                }
            });
        } else {
            Log.d(TAG, "getGPSLocation: "+"gps location: lat==" + gps.getLatitude() + "  lng==" + gps.getLongitude());
        }
    }

    private void reportLocation() {
//        if (isOPen(getApplicationContext())) {
//            openGPS(getApplicationContext());
//        }
        GPS gps = GPSUtil.getGPS(getBaseContext());
        if (gps != null) {
            double x = gps.Longitude;
            double y = gps.Latitude;
            Log.d(TAG, "reportLocation: "+"84 x:" + x + ",y:" + y);
        } else {
            Log.d(TAG, "reportLocation: "+"x:0,y:0");
        }
    }

    public boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
            Log.d(TAG, "openGPS: "+e.toString());
        }
    }
}
