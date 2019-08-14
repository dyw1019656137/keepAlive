package com.dyw.keepalive;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.Toast;

import com.dyw.keepalive.service.CoreService;
import com.dyw.keepalive.service.GpsService;
import com.dyw.keepalive.service.MusicService;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity {

    private ScheduledThreadPoolExecutor mScheduled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startService(new Intent(getApplicationContext(), GpsService.class));
                startService(new Intent(getApplicationContext(), CoreService.class));
                startService(new Intent(getApplicationContext(), MusicService.class));
//
//                if (mScheduled == null) {
////                    mScheduled = new ScheduledThreadPoolExecutor(1);
////                    mScheduled.scheduleAtFixedRate(new Runnable() {
////                        @Override
////                        public void run() {
////                            ShortcutBadger.applyCount(MainActivity.this, 1);
////                        }
////                    }, 0, 2000, TimeUnit.MILLISECONDS);
////                }
//                int num = 1 + (int) (Math.random() * (99 - 1 + 1));
//                Toast.makeText(MainActivity.this,"随机数为："+num,Toast.LENGTH_SHORT).show();
//                ShortcutBadger.applyCount(MainActivity.this, num);

            }
        });

        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mScheduled != null) {
                    mScheduled.shutdown();
                    mScheduled = null;
                }

                ShortcutBadger.applyCount(MainActivity.this, 0);

            }
        });

    }
}
