package com.dyw.keepalive.service;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.dyw.keepalive.R;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.dyw.keepalive.service.GpsService.TAG;

public class MusicService extends Service {

    private String TAG = MusicService.class.getSimpleName();

    private MediaPlayer mMediaPlayer;

    private NotificationManager notificationManager;
    private String notificationId = "serviceid";
    private String notificationName = "servicename";

    private ScheduledThreadPoolExecutor mScheduled;

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: 启动服务");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(1, getNotification());

        if (mScheduled == null) {
            mScheduled = new ScheduledThreadPoolExecutor(1);
            mScheduled.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    wakeUpAndUnlock();
                }
            }, 0, 2000, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);
        mMediaPlayer.setLooping(true);
        playMusic();
        return START_STICKY;
    }

    private void playMusic() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            Log.d(TAG, "playMusic: 启动后台播放音乐");
            mMediaPlayer.start();
        }
    }

    private void stopMusic() {
        if (mMediaPlayer != null) {
            Log.d(TAG, "stopMusic: 关闭后台播放音乐");
            mMediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
        Log.d(TAG, "onDestroy: 停止服务");
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

    /**
     * 唤醒手机屏幕并解锁
     */
    public void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            @SuppressLint("InvalidWakeLockTag")
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            // 点亮屏幕
            wl.acquire(10000);
            // 释放
            wl.release();
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("unLock");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        // 解锁
        keyguardLock.disableKeyguard();
    }
}
