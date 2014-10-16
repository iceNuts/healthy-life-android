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
import roboguice.service.RoboService;

import static com.blue_stingray.healthy_life_app.db.DatabaseHelper.*;

/**
 * Periodically logs application changes
 */
public class ApplicationLoggingService extends RoboService {

    @Inject private DatabaseHelper dbHelper;
    private long lastLogTime;
    private ComponentName lastComponent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        new ApplicationChangeReceiver();
        ScreenStateReceiver screenStateReceiver = new ScreenStateReceiver();
        registerReceiver(screenStateReceiver, screenStateReceiver.buildIntentFilter());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support binding");
    }

    private void logLast(final ComponentName application, final long newLogTime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                try {
                    ContentValues cv = new ContentValues();
                    cv.put(END_TIME, newLogTime);
                    if(application.equals(lastComponent)) {
                        db.update(APPLICATION_USAGE_TABLE, cv, "? = ?", new String[]{END_TIME, Long.toString(lastLogTime)});
                        return;
                    }
                    db.update(APPLICATION_USAGE_TABLE, cv, "? = ?", new String[]{END_TIME, Long.toString(lastLogTime - 1)});


                    Cursor appIdCursor = db.rawQuery("SELECT id FROM ? WHERE ? = ?", new String[]{APPLICATION_TABLE, PACKAGE_NAME, lastComponent.getPackageName()});
                    if(appIdCursor.getCount() == 0) {
                        Log.w(getClass().getSimpleName(), "Unable to find package in db: " + application.getPackageName());
                    } else {
                        ContentValues newStat = new ContentValues();
                        newStat.put(APPLICATION_ID, appIdCursor.getInt(0));
                        newStat.put(START_TIME, newLogTime);
                        newStat.put(END_TIME, newLogTime);
                        db.insert(APPLICATION_USAGE_TABLE, null, newStat);
                    }
                    appIdCursor.close();
                } finally {
                    db.endTransaction();
                    db.close();
                    lastLogTime = newLogTime;
                    lastComponent = application;
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
            if (lastComponent != null) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
            }
        }

    }
}
