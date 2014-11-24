package com.blue_stingray.healthy_life_app.service;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.storage.db.DatabaseHelper;
import com.blue_stingray.healthy_life_app.receiver.SelfAttachingReceiver;
import com.google.inject.Inject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import roboguice.service.RoboService;

import static com.blue_stingray.healthy_life_app.storage.db.DatabaseHelper.*;

/**
 * Periodically logs application changes
 */

public class ApplicationLoggingService extends RoboService {

    @Inject private DatabaseHelper dbHelper;
    private ApplicationChangeReceiver appChangeReceiver;
    private ScreenStateReceiver screenStateReceiver;
    private SQLiteDatabase db = null;
    private ComponentName lastComponent;

    private final int STARTFLAG = 1001;
    private final int ENDFLAG = 1002;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (db == null) {
            dbHelper = new DatabaseHelper(getApplicationContext());
            db = dbHelper.getWritableDatabase();
            appChangeReceiver = new ApplicationChangeReceiver();
            screenStateReceiver = new ScreenStateReceiver();
            registerReceiver(screenStateReceiver, screenStateReceiver.buildIntentFilter());
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support binding");
    }

    @Override
    public void onDestroy() {
        db.close();
        dbHelper.close();
        unregisterReceiver(screenStateReceiver);

        // Doing something else to notify
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
                                "SELECT * FROM application_usage WHERE package_name = ? AND end_time = ?",
                                new String[]{
                                    application.getPackageName(),
                                    "-1"
                                }
                        );

                        int appUsageCount = appUsageCursor.getCount();

                        // FAILED TO Find

                        if (appUsageCount == 0) {
                        }

                        // ERROR DELETE ALL

                        else if (appUsageCount > 1) {
                            db.delete(APPLICATION_USAGE_TABLE, "package_name=? and end_time=?", new String[]{
                                application.getPackageName(),
                                "-1"
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
                                String zeroTime = String.valueOf(c.getTimeInMillis()/1000);

                                // Update the one-day bound

                                ContentValues updateValues = new ContentValues();
                                updateValues.put(END_TIME, zeroTime);
                                db.update(APPLICATION_USAGE_TABLE, updateValues, "package_name=? and end_time=?", new String[]{
                                        application.getPackageName(),
                                        "-1"
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
                                newUsage.put(USER_ID, "0");
                                db.insertOrThrow(APPLICATION_USAGE_TABLE, null, newUsage);

                            }
                            // BEST : NO Need to Split
                            else {
                                ContentValues updateValues = new ContentValues();
                                updateValues.put(END_TIME, logTime.get("timestamp"));
                                db.update(APPLICATION_USAGE_TABLE, updateValues, "package_name=? and end_time=?", new String[]{
                                        application.getPackageName(),
                                        "-1"
                                });
                            }
                        }
                        appUsageCursor.close();
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
                        newStat.put(USER_ID, "0");
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


    private class ScreenStateReceiver extends BroadcastReceiver {

        public IntentFilter buildIntentFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            return filter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

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
                Log.d("Logging", currentComponent.getPackageName());
                if (lastComponent != currentComponent) {
                    // Log the CURRENT APP START TIME
                    logAppUsage(currentComponent, currentTime, STARTFLAG);
                    // Log the LAST APP END TIME
                    if (lastComponent != null) {
                        Log.d("Logging", lastComponent.getPackageName());
                        logAppUsage(lastComponent, currentTime, ENDFLAG);
                    }
                }
                lastComponent = currentComponent;
            }
        }

    }
}
