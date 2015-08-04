package com.blue_stingray.healthy_life_app.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;

import java.lang.reflect.Field;
import java.util.List;

import roboguice.service.RoboService;

import javax.inject.Inject;

/**
 * Service that detects application and activity changes. It broadcasts the changes to the LocalBroadcastManager.
 */
public class ApplicationDetectionService extends RoboService {

    @Inject
    private LocalBroadcastManager localBroadcastManager;
    @Inject
    private ActivityManager activityManager;
    private DataHelper dataHelper;

    private static final int POLL_DELAY_MS = 1000;
    private final String LOG_TAG = getClass().getSimpleName();
    private Thread activityPollThread;
    private boolean ISSTARTED = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (!ISSTARTED) {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            dataHelper = DataHelper.getInstance(getApplicationContext());
            startDetection();
            ISSTARTED = true;
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support binding");
    }

    // write back to database to store the time usage

    @Override
    public void onDestroy() {
        super.onDestroy();
        activityPollThread.interrupt();
        restartService();
    }

    // fix stopping service but not restart error; no need for Android 5.0

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        restartService();
        super.onTaskRemoved(rootIntent);
    }

    private void restartService() {
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
    }

    /**
     * Start detection of application and activity changes in a new thread
     */
    private void startDetection() {
        activityPollThread = new Thread(new Runnable() {

            private ComponentName lastComponent;

            @Override
            public void run() {
//                Log.i(LOG_TAG, "Application Detection has started");


                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(POLL_DELAY_MS);
                    } catch (InterruptedException e) {
                        break;
                    }
                    ComponentName currentComponent = getTopAppComponent();

                    // always sending a surface component state
                    if (currentComponent != null) {
                        Intent surfaceBroadcast = new Intent();
                        surfaceBroadcast.setAction("surfaceApp");
                        surfaceBroadcast.putExtra(getString(R.string.component_name), currentComponent);
                        localBroadcastManager.sendBroadcast(surfaceBroadcast);
                    }

                    if(currentComponent != null && !(currentComponent.equals(lastComponent))) {

                        Intent broadcast = new Intent();

                        if (lastComponent == null || !lastComponent.getPackageName().equals(currentComponent.getPackageName())) {
                            broadcast.setAction(getString(R.string.app_change));
                        } else {
                            broadcast.setAction(getString(R.string.activity_change));
                        }

                        broadcast.putExtra(getString(R.string.component_name), currentComponent);

                        localBroadcastManager.sendBroadcast(broadcast);
                        lastComponent = currentComponent;

                    }
                }
            }
        });
        activityPollThread.start();
    }

    // works for L and 4.0+

    private ComponentName getTopAppComponent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final int PROCESS_STATE_TOP = 2;
            ActivityManager.RunningAppProcessInfo currentInfo = null;
            Field field = null;
            try {
                field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            ActivityManager am = (ActivityManager) this.getSystemService(getApplicationContext().ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo app : appList) {
                if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                        app.importanceReasonCode == 0 ) {
                    Integer state = null;
                    try {
                        state = field.getInt(app);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (state != null && state == PROCESS_STATE_TOP) {
                        currentInfo = app;
                        break;
                    }
                }
            }
            ComponentName currentComponent = new ComponentName(currentInfo.pkgList[0], "");
            return currentComponent;
        }
        else {
            ActivityManager.RecentTaskInfo currentTask = activityManager.getRecentTasks(1, ActivityManager.RECENT_IGNORE_UNAVAILABLE).get(0);
            return currentTask.baseIntent.getComponent();
        }
    }
}
