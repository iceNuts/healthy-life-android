package com.blue_stingray.healthy_life_app.service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.blue_stingray.healthy_life_app.R;

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

    private static final int POLL_DELAY_MS = 500;
    private final String LOG_TAG = getClass().getSimpleName();
    private Thread activityPollThread;
    private boolean ISSTARTED = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (!ISSTARTED) {
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
