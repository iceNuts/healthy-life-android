package com.blue_stingray.healthy_life_app.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.view.WindowManager;
import android.view.View;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.activity.BlockerActivity;
import com.blue_stingray.healthy_life_app.receiver.SelfAttachingReceiver;

import java.util.Map;

import roboguice.service.RoboService;

/**
 * Created by BillZeng on 11/23/14.
 */
public class ApplicationBlockerService  extends RoboService{

    private ApplicationChangeReceiver appChangeReceiver = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (appChangeReceiver == null) {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            appChangeReceiver = new ApplicationChangeReceiver();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT);

        // ensure fire off

        AlarmManager alarmService = (AlarmManager) getApplicationContext()
                .getSystemService(ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support binding");
    }

    private class ApplicationChangeReceiver extends SelfAttachingReceiver {

        public ApplicationChangeReceiver() {
            super(ApplicationBlockerService.this, new IntentFilter(getString(R.string.app_change)));
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ComponentName currentComponent = intent.getParcelableExtra(getString(R.string.component_name));
            Log.d("kill", currentComponent.getPackageName());
            if (currentComponent.getPackageName().equals("com.android.calendar")) {
                Intent dialogIntent = new Intent(getBaseContext(), BlockerActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(dialogIntent);
            }
        }
    }
}
