package com.blue_stingray.healthy_life_app.service;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import com.blue_stingray.healthy_life_app.db.DatabaseHelper;
import com.blue_stingray.healthy_life_app.misc.Intents;
import com.blue_stingray.healthy_life_app.receiver.SelfAttachingReceiver;
import com.google.inject.Inject;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import roboguice.service.RoboService;

import static com.blue_stingray.healthy_life_app.db.DatabaseHelper.*;

/**
 * Periodically logs application changes
 */
public class ApplicationLoggingService extends RoboService {

    @Inject private DatabaseHelper dbHelper;
    private ComponentName lastComponent;
    private Map<String, String> lastTime;
    private ApplicationChangeReceiver appChangeReceiver;
    private ScreenStateReceiver screenStateReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        appChangeReceiver = new ApplicationChangeReceiver();
        screenStateReceiver = new ScreenStateReceiver();
        registerReceiver(screenStateReceiver, screenStateReceiver.buildIntentFilter());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support binding");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(appChangeReceiver);
        unregisterReceiver(screenStateReceiver);

        // Doing something else to notify
    }

    private Map<String, String> currentTime() {
        Map<String, String> timeInfo = new HashMap<String, String>();
        Calendar c = Calendar.getInstance();
        timeInfo.put("year", String.valueOf(c.get(Calendar.YEAR)));
        timeInfo.put("month", String.valueOf(c.get(Calendar.MONTH)));
        timeInfo.put("day", String.valueOf(c.get(Calendar.DATE)));
        timeInfo.put("day_of_week", String.valueOf(c.get(Calendar.DAY_OF_WEEK)));
        timeInfo.put("timestamp", String.valueOf(new Timestamp(new Date().getTime())));
        return timeInfo;
    }

    private void logAppUsage(final ComponentName application, final Map<String, String> logTime, final boolean startFlag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    // log the end time
                    if (!startFlag) {
                        // judge if there is a gap between lastTime and logTime
                        String startTimestamp =  lastTime.get("timestamp");
                        String startDate = lastTime.get("day");
                        // start & end time are in the same day
                        if (startDate == logTime.get("day")) {
                            // simply update the end time field
                            ContentValues cv = new ContentValues();
                            cv.put(END_TIME, logTime.get("timestamp"));
                            db.update(APPLICATION_USAGE_TABLE, cv, "?=?", new String[]{END_TIME, startTimestamp});
                        }
                        // split the time
                        else {
                            ContentValues cv = new ContentValues();
                            String dateString = logTime.get("year")+"-"+logTime.get("month")+"-"+logTime.get("day")+" 0:0:0.0";
                            String firstPeriodEndTime = String.valueOf(Timestamp.valueOf(dateString));
                            cv.put(END_TIME, firstPeriodEndTime);
                            db.update(APPLICATION_USAGE_TABLE, cv, "?=?", new String[]{END_TIME, startTimestamp});

                            ContentValues newStat = new ContentValues();
                            newStat.put(USAGE_YEAR, logTime.get("year"));
                            newStat.put(USAGE_MONTH, logTime.get("month"));
                            newStat.put(USAGE_DAY, logTime.get("day"));
                            newStat.put(USAGE_DAY_OF_WEEK, logTime.get("day_of_week"));
                            newStat.put(START_TIME, firstPeriodEndTime);
                            newStat.put(END_TIME, logTime.get("timestamp"));
                            newStat.put(PACKAGE_NAME, application.getPackageName());
                            newStat.put(USER_ID, "test");
                            db.insert(APPLICATION_USAGE_TABLE, null, newStat);
                        }
                    }
                    // log start time
                    else {
                        ContentValues newStat = new ContentValues();
                        newStat.put(USAGE_YEAR, logTime.get("year"));
                        newStat.put(USAGE_MONTH, logTime.get("month"));
                        newStat.put(USAGE_DAY, logTime.get("day"));
                        newStat.put(USAGE_DAY_OF_WEEK, logTime.get("day_of_week"));
                        newStat.put(START_TIME, logTime.get("timestamp"));
                        newStat.put(END_TIME, logTime.get("timestamp"));
                        newStat.put(PACKAGE_NAME, application.getPackageName());
                        newStat.put(USER_ID, "test");
                        db.insert(APPLICATION_USAGE_TABLE, null, newStat);
                    }
                } finally {
                    db.endTransaction();
                    db.close();
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
            super(ApplicationLoggingService.this, new IntentFilter(Intents.Monitor.APP_CHANGE));
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ComponentName currentComponent = intent.getParcelableExtra(Intents.Monitor.Extra.COMPONENT_NAME);
            if (currentComponent != null) {
                Map<String, String> currentTime = currentTime();
                if (lastComponent != null) {
                    if (lastComponent.getPackageName()==currentComponent.getPackageName())
                        return;
                    logAppUsage(currentComponent, currentTime, true);
                    logAppUsage(lastComponent, currentTime, false);
                } else {
                    logAppUsage(currentComponent, currentTime, true);
                }
                lastComponent = currentComponent;
                lastTime = currentTime;
            }
        }

    }
}
