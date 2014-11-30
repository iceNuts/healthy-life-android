package com.blue_stingray.healthy_life_app.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.ui.activity.BaseActivity;
import com.blue_stingray.healthy_life_app.ui.activity.MainActivity;

import android.content.pm.PackageManager.NameNotFoundException;

/**
 * Created by BillZeng on 11/30/14.
 */
public class AlertActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_FOREGROUND);
        String packageName = getIntent().getStringExtra("name");
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( packageName, 0);
        } catch (final NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        int seconds = getIntent().getIntExtra("remaining", 0);
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
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("App Usage Alert")
                .setContentText(applicationName + " has " + String.valueOf(hrs) + " hours, " + String.valueOf(minutes) + " minutes and " + String.valueOf(seconds) + " seconds left, tap to request more time.");
        Intent lifelineIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(lifelineIntent);
        PendingIntent lifelinePendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(lifelinePendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 10001;
        mNotificationManager.notify(mId, mBuilder.build());
        finish();
    }
}
