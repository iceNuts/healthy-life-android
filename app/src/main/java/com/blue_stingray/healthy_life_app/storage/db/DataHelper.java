package com.blue_stingray.healthy_life_app.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import roboguice.RoboGuice;

import static com.blue_stingray.healthy_life_app.storage.db.DatabaseHelper.*;


/**
 * Created by BillZeng on 11/24/14.
 */
public class DataHelper {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static DataHelper instance = null;
    private HashMap<String, Integer> goalCache;
    private SharedPreferencesHelper prefs;
    private HashMap<String, Integer> blockedList;

    public static synchronized DataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataHelper();
            instance.dbHelper = new DatabaseHelper(context);
            instance.db = instance.dbHelper.getWritableDatabase();
            instance.goalCache = instance.loadGoalCache();
            instance.blockedList = new HashMap<>();
            instance.prefs = new SharedPreferencesHelper(context);
        }
        return instance;
    }

    public void createNewGoal(final String packageName, final HashMap<Integer, Integer> dayMap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                instance.db.beginTransaction();
                Iterator it = dayMap.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    ContentValues newStat = new ContentValues();
                    newStat.put(PACKAGE_NAME, packageName);
                    newStat.put(LIMIT_DAY, pairs.getKey().toString());
                    newStat.put(TIME_LIMIT, pairs.getValue().toString());
                    db.insert(GOAL_TABLE, null, newStat);
                    it.remove();
                }
                instance.db.setTransactionSuccessful();
                instance.db.endTransaction();
                instance.goalCache = instance.loadGoalCache();
            }
        }).start();
    }

    private HashMap<String, Integer> loadGoalCache() {
        HashMap<String, Integer> cache = new HashMap<>();
        Cursor goalCursor = db.rawQuery(
                "SELECT * FROM goal_table",
                new String[]{
                }
        );
        goalCursor.moveToFirst();
        while(goalCursor.isAfterLast() == false) {
            String packageName = goalCursor.getString(goalCursor.getColumnIndex(PACKAGE_NAME));
            Integer hrs = goalCursor.getInt(goalCursor.getColumnIndex(TIME_LIMIT));
            Integer day = goalCursor.getInt(goalCursor.getColumnIndex(LIMIT_DAY));
            cache.put(packageName+String.valueOf(day), hrs);
            goalCursor.moveToNext();
        }
        goalCursor.close();
        return cache;
    }

    public boolean isGoal(String packageName) {
        String currentDayOfWeek = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        return instance.goalCache.containsKey(packageName+currentDayOfWeek);
    }

    public Integer packageRemainingTime(String packageName) {
        return blockedList.containsKey(packageName)? blockedList.get(packageName) : 0;
    }

    public boolean isGoalSatisfied(String packageName) {
        if (!isGoal(packageName)) {
            return false;
        }
        if (blockedList.containsKey(packageName) && blockedList.get(packageName) <= 0) {
            return true;
        }
        Calendar cal = Calendar.getInstance();
        String currentYear = String.valueOf(cal.get(Calendar.YEAR));
        String currentMonth = String.valueOf(cal.get(Calendar.MONTH));
        String currentDay = String.valueOf(cal.get(Calendar.DATE));
        String currentDayOfWeek = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
        String session = prefs.getSession();

        Cursor appUsageCursor = db.rawQuery(
                "SELECT * FROM application_usage WHERE usage_year=? and usage_month=? and usage_day=? and usage_day_of_week=? and user_session=?",
                new String[]{
                        currentYear,
                        currentMonth,
                        currentDay,
                        currentDayOfWeek,
                        session
                }
        );
        appUsageCursor.moveToFirst();
        Integer totalTime = 0;
        Integer goalTime = goalCache.get(packageName+currentDayOfWeek)*60*60;
        while(appUsageCursor.isAfterLast() == false) {
            Integer start_time = appUsageCursor.getInt(appUsageCursor.getColumnIndex("start_time"));
            Integer end_time = appUsageCursor.getInt(appUsageCursor.getColumnIndex("end_time"));

            // Avoid some unfinished recording
            if (end_time == -1) {
                appUsageCursor.moveToNext();
                continue;
            }
            totalTime += (end_time-start_time);
            appUsageCursor.moveToNext();
        }
        appUsageCursor.close();
        blockedList.put(packageName, (goalTime-totalTime));
        return blockedList.get(packageName) <= 0;
    }

}
