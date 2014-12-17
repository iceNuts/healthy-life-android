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

import roboguice.service.RoboService;

/**
 * Created by BillZeng on 11/23/14.
 */
public class ApplicationBlockerService  extends RoboService {

    private ApplicationChangeReceiver appChangeReceiver = null;
    private ApplicationDynamicReceiver applicationDynamicReceiver = null;
    private DataHelper dataHelper;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (appChangeReceiver == null) {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            dataHelper = DataHelper.getInstance(getApplicationContext());
            appChangeReceiver = new ApplicationChangeReceiver();
            applicationDynamicReceiver = new ApplicationDynamicReceiver();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
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

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(((Object)this).getClass().getSimpleName() + " does not support binding");
    }

    private class ApplicationChangeReceiver extends SelfAttachingReceiver {

        public ApplicationChangeReceiver() {
            super(ApplicationBlockerService.this, new IntentFilter(getString(R.string.app_change)));
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ComponentName currentComponent = intent.getParcelableExtra(getString(R.string.component_name));
            if (dataHelper.isGoal(currentComponent.getPackageName())) {
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

    private class ApplicationDynamicReceiver extends SelfAttachingReceiver {


        public ApplicationDynamicReceiver() {
            super(ApplicationBlockerService.this, new IntentFilter("surfaceApp"));
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ComponentName currentComponent = intent.getParcelableExtra(getString(R.string.component_name));
            if (dataHelper.isGoalSatisfied(currentComponent.getPackageName())) {
                Intent dialogIntent = new Intent(getBaseContext(), BlockerActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.putExtra("packageName", currentComponent.getPackageName());
                getApplication().startActivity(dialogIntent);
            }
        }
    }
}
