package com.dyw.keepalive.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.dyw.keepalive.service.GpsService.TAG;

public class CoreService extends Service {

    private ScheduledThreadPoolExecutor mScheduled;

    public CoreService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mScheduled == null) {
            mScheduled = new ScheduledThreadPoolExecutor(1);
            mScheduled.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (!isServiceRunning(getApplicationContext(),"com.dyw.keepalive.service.MusicService")){
                        Log.d(TAG, "run: 启动 GpsService");
                        startService(new Intent(getApplicationContext(),MusicService.class));
                    }
                }
            }, 0, 500, TimeUnit.MILLISECONDS);
        }
        return START_STICKY;
    }

    public static boolean isServiceRunning(Context context,String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            String name = info.service.getClassName();
            if (name.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
