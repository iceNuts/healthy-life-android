package com.blue_stingray.healthy_life_app.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.blue_stingray.healthy_life_app.BuildConfig;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.StatForm;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.storage.db.DatabaseHelper;
import com.blue_stingray.healthy_life_app.receiver.SelfAttachingReceiver;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.activity.BlockerActivity;
import com.google.inject.Inject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.service.RoboService;

import static com.blue_stingray.healthy_life_app.storage.db.DatabaseHelper.*;
import com.blue_stingray.healthy_life_app.model.Stat;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import retrofit.android.AndroidLog;

/**
 * Periodically logs application changes
 */

public class ApplicationLoggingService extends RoboService {

    @Inject private DatabaseHelper dbHelper;
    private ApplicationChangeReceiver appChangeReceiver;
    private ScreenStateReceiver screenStateReceiver;
    private RemoteLoggingReceiver remoteLoggingReceiver;
    private SQLiteDatabase db = null;
    private ComponentName lastComponent;
    @Inject private SharedPreferencesHelper prefs;
    private DataHelper dataHelper;
    @Inject private RestInterface rest;

    private Thread remoteLoggingThread;
    private Thread timeCountThread;
    private TimedRunnable timedRunnable;
    private static final int POLL_DELAY_MS = 60*1000;//30*60*1000;
    private static final int SEC_POLL_DELAY = 1000;
    @Inject private LocalBroadcastManager localBroadcastManager;

    private final int STARTFLAG = 1001;
    private final int ENDFLAG = 1002;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (db == null) {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            dbHelper = new DatabaseHelper(getApplicationContext());
            db = dbHelper.getWritableDatabase();
            dataHelper = DataHelper.getInstance(getApplicationContext());
            appChangeReceiver = new ApplicationChangeReceiver();
            screenStateReceiver = new ScreenStateReceiver();
            remoteLoggingReceiver = new RemoteLoggingReceiver();
            registerReceiver(screenStateReceiver, screenStateReceiver.buildIntentFilter());
            startRemoteLogging();
            startTimeCount();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(((Object)this).getClass().getSimpleName() + " does not support binding");
    }

    @Override
    public void onDestroy() {
        db.close();
        dbHelper.close();
        unregisterReceiver(screenStateReceiver);

        // Doing something else to notify
        restartService();
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

    // Android holds a different date/month count

    private Map<String, String> currentTime() {
        Map<String, String> timeInfo = new HashMap<String, String>();
        Calendar c = Calendar.getInstance();
        timeInfo.put("year", String.valueOf(c.get(Calendar.YEAR)));
        timeInfo.put("month", String.valueOf(c.get(Calendar.MONTH)));
        timeInfo.put("day", String.valueOf(c.get(Calendar.DATE)));
        timeInfo.put("day_of_week", String.valueOf(c.get(Calendar.DAY_OF_WEEK)));
        timeInfo.put("timestamp", String.valueOf(new Date().getTime()/1000));
        return timeInfo;
    }

    private void logAppUsage(final ComponentName application, final Map<String, String> logTime, final int flag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.beginTransaction();
                try {

                    // log the end time

                    if (flag == ENDFLAG) {

                        // Begin Finding

                        Cursor appUsageCursor = db.rawQuery(
                                "SELECT * FROM application_usage WHERE package_name = ? AND end_time = ? and user_id = ?",
                                new String[]{
                                    application.getPackageName(),
                                    "-1",
                                    prefs.getUserID()
                                }
                        );

                        try {

                            int appUsageCount = appUsageCursor.getCount();

                            // FAILED TO Find

                            if (appUsageCount == 0) {
                            }

                            // ERROR DELETE ALL
                            // Store every records by start_time -1 when the app is still open, replace -1 by end_time
                            // But some accidents may occur result in a few -1 ending records

                            else if (appUsageCount > 1) {
                                db.delete(APPLICATION_USAGE_TABLE, "package_name=? and end_time=? and user_id=?", new String[]{
                                        application.getPackageName(),
                                        "-1",
                                        prefs.getUserID()
                                });
                            }

                            // ONLY ONE ENTRY NICE :-)

                            else {

                                appUsageCursor.moveToFirst();
                                String lastDay = appUsageCursor.getString(appUsageCursor.getColumnIndex(USAGE_DAY));

                                // SPLIT TIME :(

                                if (!lastDay.equals(logTime.get("day"))) {

                                    // Generate the ZERO timestamp

                                    Calendar c = Calendar.getInstance();
                                    c.set(Calendar.YEAR, Integer.valueOf(logTime.get("year")));
                                    c.set(Calendar.MONTH, Integer.valueOf(logTime.get("month")));
                                    c.set(Calendar.DATE, Integer.valueOf(logTime.get("day")));
                                    c.set(Calendar.MINUTE, 0);
                                    c.set(Calendar.HOUR_OF_DAY, 0);
                                    c.set(Calendar.SECOND, 0);
                                    c.set(Calendar.MILLISECOND, 0);
                                    String zeroTime = String.valueOf(c.getTimeInMillis() / 1000);

                                    // Update the one-day bound

                                    ContentValues updateValues = new ContentValues();
                                    updateValues.put(END_TIME, zeroTime);
                                    db.update(APPLICATION_USAGE_TABLE, updateValues, "package_name=? and end_time=? and user_id=?", new String[]{
                                            application.getPackageName(),
                                            "-1",
                                            prefs.getUserID()
                                    });

                                    // Insert a new day

                                    ContentValues newUsage = new ContentValues();
                                    newUsage.put(USAGE_YEAR, logTime.get("year"));
                                    newUsage.put(USAGE_MONTH, logTime.get("month"));
                                    newUsage.put(USAGE_DAY, logTime.get("day"));
                                    newUsage.put(USAGE_DAY_OF_WEEK, logTime.get("day_of_week"));
                                    newUsage.put(START_TIME, zeroTime);
                                    newUsage.put(END_TIME, logTime.get("timestamp"));
                                    newUsage.put(PACKAGE_NAME, application.getPackageName());
                                    newUsage.put(USER_SESSION, prefs.getSession());
                                    newUsage.put(USER_ID, prefs.getUserID());
                                    db.insertOrThrow(APPLICATION_USAGE_TABLE, null, newUsage);

                                }
                                // BEST : NO Need to Split
                                else {
                                    ContentValues updateValues = new ContentValues();
                                    updateValues.put(END_TIME, logTime.get("timestamp"));
                                    db.update(APPLICATION_USAGE_TABLE, updateValues, "package_name=? and end_time=? and user_id=?", new String[]{
                                            application.getPackageName(),
                                            "-1",
                                            prefs.getUserID()
                                    });
                                }
                            }
                        }
                        finally {
                            appUsageCursor.close();
                        }
                    }

                    // log start time

                    else if (flag == STARTFLAG) {
                        ContentValues newStat = new ContentValues();
                        newStat.put(USAGE_YEAR, logTime.get("year"));
                        newStat.put(USAGE_MONTH, logTime.get("month"));
                        newStat.put(USAGE_DAY, logTime.get("day"));
                        newStat.put(USAGE_DAY_OF_WEEK, logTime.get("day_of_week"));
                        newStat.put(START_TIME, logTime.get("timestamp"));
                        newStat.put(END_TIME, "-1");
                        newStat.put(PACKAGE_NAME, application.getPackageName());
                        newStat.put(USER_SESSION, prefs.getSession());
                        newStat.put(USER_ID, prefs.getUserID());
                        db.insertOrThrow(APPLICATION_USAGE_TABLE, null, newStat);
                    }
                }

                // Clean up

                finally {
                    db.setTransactionSuccessful();
                    db.endTransaction();
                }

            }
        }).start();
    }

    private void logPhoneUsage(final Map<String, String> logTime, final int flag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.beginTransaction();
                try {

                    // log the end time

                    if (flag == ENDFLAG) {

                        // Begin Finding

                        Cursor phoneUsageCursor = db.rawQuery(
                                "SELECT * FROM wake_up_record WHERE end_time = ? and user_id=?",
                                new String[]{
                                        "-1",
                                        prefs.getUserID()
                                }
                        );

                        try {

                            int phoneUsageCount = phoneUsageCursor.getCount();
                            // FAILED TO Find

                            if (phoneUsageCount == 0) {
                            }

                            // ERROR DELETE ALL
                            // Store every records by start_time -1 when the app is still open, replace -1 by end_time
                            // But some accidents may occur result in a few -1 ending records

                            else if (phoneUsageCount > 1) {
                                db.delete(WAKE_UP_RECORD_TABLE, "end_time=? and user_id=?", new String[]{
                                        "-1",
                                        prefs.getUserID()
                                });
                            }

                            // ONLY ONE ENTRY NICE :-)

                            else {

                                phoneUsageCursor.moveToFirst();
                                String lastDay = phoneUsageCursor.getString(phoneUsageCursor.getColumnIndex(USAGE_DAY));

                                String startTime = phoneUsageCursor.getString(phoneUsageCursor.getColumnIndex(START_TIME));

                                // SPLIT TIME :(

                                if (!lastDay.equals(logTime.get("day"))) {

                                    // Generate the ZERO timestamp

                                    Calendar c = Calendar.getInstance();
                                    c.set(Calendar.YEAR, Integer.valueOf(logTime.get("year")));
                                    c.set(Calendar.MONTH, Integer.valueOf(logTime.get("month")));
                                    c.set(Calendar.DATE, Integer.valueOf(logTime.get("day")));
                                    c.set(Calendar.MINUTE, 0);
                                    c.set(Calendar.HOUR_OF_DAY, 0);
                                    c.set(Calendar.SECOND, 0);
                                    c.set(Calendar.MILLISECOND, 0);
                                    String zeroTime = String.valueOf(c.getTimeInMillis() / 1000);

                                    // Update the one-day bound

                                    ContentValues updateValues = new ContentValues();
                                    updateValues.put(END_TIME, zeroTime);
                                    db.update(WAKE_UP_RECORD_TABLE, updateValues, "end_time=? and user_id=?", new String[]{
                                            "-1",
                                            prefs.getUserID()
                                    });

                                    // Insert a new day

                                    ContentValues newUsage = new ContentValues();
                                    newUsage.put(USAGE_YEAR, logTime.get("year"));
                                    newUsage.put(USAGE_MONTH, logTime.get("month"));
                                    newUsage.put(USAGE_DAY, logTime.get("day"));
                                    newUsage.put(USAGE_DAY_OF_WEEK, logTime.get("day_of_week"));
                                    newUsage.put(START_TIME, zeroTime);
                                    newUsage.put(END_TIME, logTime.get("timestamp"));
                                    newUsage.put(USER_SESSION, prefs.getSession());
                                    newUsage.put(USER_ID, prefs.getUserID());
                                    db.insertOrThrow(WAKE_UP_RECORD_TABLE, null, newUsage);

                                }
                                // BEST : NO Need to Split
                                else {
                                    ContentValues updateValues = new ContentValues();
                                    updateValues.put(END_TIME, logTime.get("timestamp"));
                                    db.update(WAKE_UP_RECORD_TABLE, updateValues, "end_time=? and user_id=?", new String[]{
                                            "-1",
                                            prefs.getUserID()
                                    });
                                }
                            }
                        }
                        finally {
                            phoneUsageCursor.close();
                        }
                    }

                    // log start time

                    else if (flag == STARTFLAG) {
                        ContentValues newStat = new ContentValues();
                        newStat.put(USAGE_YEAR, logTime.get("year"));
                        newStat.put(USAGE_MONTH, logTime.get("month"));
                        newStat.put(USAGE_DAY, logTime.get("day"));
                        newStat.put(USAGE_DAY_OF_WEEK, logTime.get("day_of_week"));
                        newStat.put(START_TIME, logTime.get("timestamp"));
                        newStat.put(END_TIME, "-1");
                        newStat.put(USER_SESSION, prefs.getSession());
                        newStat.put(USER_ID, prefs.getUserID());
                        db.insertOrThrow(WAKE_UP_RECORD_TABLE, null, newStat);
                    }
                }

                // Clean up

                finally {
                    db.setTransactionSuccessful();
                    db.endTransaction();
                }

            }
        }).start();
    }

    // this may be useful to monitor the screen is off and don't count the usage time

    private class ScreenStateReceiver extends BroadcastReceiver {

        public IntentFilter buildIntentFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            return filter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Screen", intent.getAction());

            if (intent.getAction() == "android.intent.action.SCREEN_ON") {
                timedRunnable.onResume();
            }
            else {
                timedRunnable.onPause();
            }
        }
    }

    private class TimedRunnable implements Runnable {
        private Object mPauseLock;
        private boolean mPaused;

        public TimedRunnable() {
            mPauseLock = new Object();
            mPaused = false;
        }

        public void run() {
            int timedCount = 0;
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(SEC_POLL_DELAY);
                } catch (InterruptedException e) {
                    break;
                }
                // 5400 seconds = 90 min
                if (timedCount == 5400) {
                    timedCount = 0;
                    Intent dialogIntent = new Intent(getBaseContext(), BlockerActivity.class);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    dialogIntent.putExtra("AlertInfo", "You've used your phone for 90 minutes today. Take a break!");
                    getApplication().startActivity(dialogIntent);
                }
                timedCount++;
                synchronized (mPauseLock) {
                    while (mPaused) {
                        try {
                            mPauseLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        public void onPause() {
            Log.d("Screen", "on pause");
            synchronized (mPauseLock) {
                mPaused = true;
                // record screen off
                Map<String, String> currentTime = currentTime();
                Log.d("currentTime", currentTime.get("timestamp"));
                logPhoneUsage(currentTime, ENDFLAG);
            }
        }

        public void onResume() {
            Log.d("Screen", "on resume");
            synchronized (mPauseLock) {
                mPaused = false;
                mPauseLock.notifyAll();
                // record screen start
                Map<String, String> currentTime = currentTime();
                Log.d("currentTime", currentTime.get("timestamp"));
                logPhoneUsage(currentTime, STARTFLAG);
            }
        }
    }

    private void startTimeCount() {

        timedRunnable = new TimedRunnable();
        timeCountThread = new Thread(timedRunnable);
        timeCountThread.start();
    }

    private void fireNotification(String subject) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(subject);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 10002;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    // log usage whenever surface app change
    // this could guarantee each goal app usage is recorded correctly

    private class ApplicationChangeReceiver extends SelfAttachingReceiver {

        public ApplicationChangeReceiver() {
            super(ApplicationLoggingService.this, new IntentFilter(getString(R.string.app_change)));
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            ComponentName currentComponent = intent.getParcelableExtra(getString(R.string.component_name));
            if (currentComponent != null) {
                Map<String, String> currentTime = currentTime();
                // App Changed
                if (lastComponent != currentComponent) {
                    // Log the CURRENT APP START TIME
//                    if (dataHelper.isGoal(currentComponent.getPackageName()))
//                    now need to record all time used
                    logAppUsage(currentComponent, currentTime, STARTFLAG);
                    // Log the LAST APP END TIME
                    if (lastComponent != null) {
                        logAppUsage(lastComponent, currentTime, ENDFLAG);
                    }
                }
                lastComponent = currentComponent;
            }
        }

    }

    // Async update logging stats

    private void startRemoteLogging() {
        remoteLoggingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(POLL_DELAY_MS);
                    } catch (InterruptedException e) {
                        break;
                    }
                    // checking the last update time
                    String lastTimeStamp = "0";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    try {
                        // set up session header, don't use rest api call whose callback is run on main thread, will not be called properly
                        request.setHeader("Authorization", "HL "+prefs.getSession());
                        request.setURI(new URI(BuildConfig.ENDPOINT_URL+"/stat/lastUpdate"));
                        HttpResponse response = httpClient.execute(request);
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                response.getEntity().getContent()));
                        lastTimeStamp = in.readLine();
                        Intent broadcast = new Intent();
                        broadcast.setAction(getString(R.string.remote_logging));
                        broadcast.putExtra("lastTimestamp", lastTimeStamp);
                        // need last update timestamp
                        localBroadcastManager.sendBroadcast(broadcast);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        remoteLoggingThread.start();
    }

    // Push the logging to server

    private class RemoteLoggingReceiver extends SelfAttachingReceiver {

        public RemoteLoggingReceiver() {
            super(ApplicationLoggingService.this, new IntentFilter(getString(R.string.remote_logging)));
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String lastTimeStamp = intent.getStringExtra("lastTimestamp");
            if (lastTimeStamp == null) {
                lastTimeStamp = "0";
            }
            // remote logging latest data
            ArrayList<StatForm> stats = dataHelper.getLoggingRecordByTimestamp(lastTimeStamp);
            if (stats.size() > 0) {
                rest.createStats(
                        stats,
                        new RetrofitDialogCallback<Stat>(
                                getApplicationContext(),
                                null) {
                            @Override
                            public void onSuccess(Stat stat, Response response) {/*not much to do*/}
                            @Override
                            public void onFailure(RetrofitError retrofitError) {/*not much to do*/}
                        }
                );
            }
        }
    }

}






















