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
                Log.i(LOG_TAG, "Application Detection has started");


                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(POLL_DELAY_MS);
                    } catch (InterruptedException e) {
                        break;
                    }
                    ActivityManager.RecentTaskInfo currentTask = activityManager.getRecentTasks(1, ActivityManager.RECENT_IGNORE_UNAVAILABLE).get(0);
                    ComponentName currentComponent = currentTask.baseIntent.getComponent();

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

                        Log.d(LOG_TAG, "App: " + currentComponent.getPackageName() + " Activity: " + currentComponent.getClassName());

                        broadcast.putExtra(getString(R.string.component_name), currentComponent);

                        localBroadcastManager.sendBroadcast(broadcast);
                        lastComponent = currentComponent;

                    }
                }
            }
        });
        activityPollThread.start();
    }
}
