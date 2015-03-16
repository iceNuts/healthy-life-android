package com.blue_stingray.healthy_life_app.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.*;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.ui.activity.AlertActivity;
import com.blue_stingray.healthy_life_app.ui.activity.BlockerActivity;
import com.blue_stingray.healthy_life_app.receiver.SelfAttachingReceiver;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.ui.activity.MainActivity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import roboguice.service.RoboService;

/**
 * Created by BillZeng on 11/23/14.
 */

// Blocking the App and constantly receiving the usage time

public class ApplicationBlockerService  extends RoboService {

    private ApplicationChangeReceiver appChangeReceiver = null;
    private ApplicationDynamicReceiver applicationDynamicReceiver = null;
    private DataHelper dataHelper;
    private Timer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (appChangeReceiver == null) {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            dataHelper = DataHelper.getInstance(getApplicationContext());
            appChangeReceiver = new ApplicationChangeReceiver();
            applicationDynamicReceiver = new ApplicationDynamicReceiver();

            // fire 10 am notification
            Calendar c = Calendar.getInstance();
            timer = new Timer();
            timer.schedule(
                    new UsageReportNotification(),
                    new Date(
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH),
                            10,
                            0
                    ),
                    24*60*60*1000
            );
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        restartService();
        super.onDestroy();
    }

    // fix stopping service but not restart error; no need for Android 5.0

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        restartService();
        super.onTaskRemoved(rootIntent);
    }

    private void restartService() {
        Intent restartServiceIntent = new Intent(getApplicationContext(), ((Object)this).getClass());
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

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(((Object)this).getClass().getSimpleName() + " does not support binding");
    }

    // Triggered when the surface app change

    private class ApplicationChangeReceiver extends SelfAttachingReceiver {

        public ApplicationChangeReceiver() {
            super(ApplicationBlockerService.this, new IntentFilter(getString(R.string.app_change)));
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ComponentName currentComponent = intent.getParcelableExtra(getString(R.string.component_name));
            if (dataHelper.isGoal(currentComponent.getPackageName())) {

                // Calculate the time

                String packageName = currentComponent.getPackageName();
                int seconds = dataHelper.packageRemainingTime(currentComponent.getPackageName());
                final PackageManager pm = getApplicationContext().getPackageManager();
                ApplicationInfo ai;
                try {
                    ai = pm.getApplicationInfo( packageName, 0);
                } catch (final PackageManager.NameNotFoundException e) {
                    ai = null;
                }
                final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                int hrs = 0;
                int minutes = 0;
                if (seconds >= 60) {
                    minutes = seconds / 60;
                    seconds = seconds % 60;
                }
                if (minutes >= 60) {
                    hrs = minutes / 60;
                    minutes = minutes % 60;
                }

                // fire a notification

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(applicationName)
                                .setContentText(String.valueOf(hrs) + " hours, " + String.valueOf(minutes) + " minutes and " + String.valueOf(seconds) + " seconds left, tap to request more time.");
                Intent lifelineIntent = new Intent(getApplicationContext(), MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(lifelineIntent);
                PendingIntent lifelinePendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(lifelinePendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                int mId = 10001;
                mNotificationManager.notify(mId, mBuilder.build());
            }
        }
    }

    // Constantly check if the surface app is over time

    private class ApplicationDynamicReceiver extends SelfAttachingReceiver {

        private Integer currentSec;
        private ComponentName lastComponent;

        public ApplicationDynamicReceiver() {
            super(ApplicationBlockerService.this, new IntentFilter("surfaceApp"));
            currentSec = 0;
            lastComponent = null;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ComponentName currentComponent = intent.getParcelableExtra(getString(R.string.component_name));
            if (lastComponent == null || !lastComponent.getPackageName().equals(currentComponent.getPackageName())) {
                lastComponent = currentComponent;
                currentSec = 0;
            }
            if (dataHelper.isGoal(currentComponent.getPackageName())) {
                BigDecimal ratio = dataHelper.getRemainigTimeRatio(currentComponent.getPackageName(), currentSec);

                // Kick out blocking

                if (ratio.floatValue() == 0) {
                    Intent dialogIntent = new Intent(getBaseContext(), BlockerActivity.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    dialogIntent.putExtra("packageName", currentComponent.getPackageName());
                    getApplication().startActivity(dialogIntent);
                }
                // 75% time used warning fire notification
                else if (ratio.floatValue() == 0.25) {
                    NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("Healthy App")
                                .setContentText("You have 25% time remaining.");
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    int mId = 10003;
                    mNotificationManager.notify(mId, mBuilder.build());
                    final PackageManager pm = getApplicationContext().getPackageManager();
                    ApplicationInfo ai;
                    try {
                        ai = pm.getApplicationInfo( currentComponent.getPackageName(), 0);
                    } catch (final PackageManager.NameNotFoundException e) {
                        ai = null;
                    }
                    final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                    dataHelper.createAlert(applicationName, "PlaceHolder", "You have only 25% time remaining.");
                }
            }
            // same as thread polling sec
            currentSec++;
        }
    }

    private void fireClickableNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSubText("Click to view")
                        .setContentText("Yesterday Phone Usage Report is available");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 10003;
        Intent dailyReportIntent = new Intent(this, MainActivity.class);
        dailyReportIntent.setAction("OPEN_DAILY_USAGE");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                dailyReportIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private class UsageReportNotification extends TimerTask {

        @Override
        public void run() {
            fireClickableNotification();
        }
    }

}
