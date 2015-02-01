package com.blue_stingray.healthy_life_app.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.net.form.StatForm;
import com.google.inject.Inject;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import roboguice.RoboGuice;

import static com.blue_stingray.healthy_life_app.storage.db.DatabaseHelper.*;


/**
 * Created by BillZeng on 11/24/14.
 */

/* Handle Database
*   blocklist stores app being blocked and its remainig time
*   extendlist stores app being extend and its total extended time
*   GoalCache stores goal time for each app, will be updated in time
*/

public class DataHelper {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private static DataHelper instance = null;
    private HashMap<String, Integer> goalCache;
    private SharedPreferencesHelper prefs;
    private HashMap<String, Integer> blockedList;
    private HashMap<String, Integer> extendList;

    public static synchronized DataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataHelper();
            instance.dbHelper = new DatabaseHelper(context);
            instance.db = instance.dbHelper.getWritableDatabase();
            instance.goalCache = instance.loadGoalCache();
            instance.blockedList = new HashMap<>();
            instance.extendList = new HashMap<>();
            instance.prefs = new SharedPreferencesHelper(context);
        }
        return instance;
    }

    // Please refer goal table in database helper

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

                    // Delete old goal
                    db.delete(GOAL_TABLE, "package_name=? and limit_day=?", new String[]{
                            packageName,
                            pairs.getKey().toString()
                    });

                    // Insert new goal
                    db.insert(GOAL_TABLE, null, newStat);
                    it.remove();
                }
                instance.db.setTransactionSuccessful();
                instance.db.endTransaction();
                instance.goalCache = instance.loadGoalCache();
            }
        }).start();
    }

    // GoalCache key is packageName+dayOfWeek, other cache is only packageName as key

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

    // Quick Access

    public boolean isGoal(String packageName) {
        String currentDayOfWeek = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        return instance.goalCache.containsKey(packageName+currentDayOfWeek);
    }

    public Integer getGoalTime(String packageName) {
        return getGoalTime(packageName, Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }

    public Integer getGoalTime(String packageName, int day) {
        return instance.goalCache.get(packageName + String.valueOf(Calendar.getInstance().get(day)));
    }

    public Goal getGoal(Context context, String packageName) {
        return getGoal(context, packageName, Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
    }

    public Goal getGoal(Context context, String packageName, int day) {
        Goal goal = new Goal(context);
        Cursor goalCursor = db.rawQuery(
                "SELECT * FROM goal_table WHERE package_name=\"" + packageName + "\" AND limit_day=\"" + day + "\"",
                new String[]{
                }
        );

        if(goalCursor.getCount() > 0) {
            goalCursor.moveToFirst();
            goal.setPackageName(goalCursor.getString(goalCursor.getColumnIndex(PACKAGE_NAME)));
            goal.setTimeLimit(goalCursor.getInt(goalCursor.getColumnIndex(TIME_LIMIT)));
            goal.setLimitDay(goalCursor.getInt(goalCursor.getColumnIndex(LIMIT_DAY)));
            goalCursor.close();

            return goal;
        }

        return null;
    }

    public List<Goal> getGoals(Context context, String packageName) {
        ArrayList<Goal> goals = new ArrayList<>();
        Cursor goalCursor = db.rawQuery(
                "SELECT * FROM goal_table WHERE package_name=\"" + packageName + "\"",
                new String[]{
                }
        );

        while(goalCursor.moveToNext()) {
            Goal goal = new Goal(context);
            goal.setPackageName(goalCursor.getString(goalCursor.getColumnIndex(PACKAGE_NAME)));
            goal.setTimeLimit(goalCursor.getInt(goalCursor.getColumnIndex(TIME_LIMIT)));
            goal.setLimitDay(goalCursor.getInt(goalCursor.getColumnIndex(LIMIT_DAY)));
            goals.add(goal);
        }

        goalCursor.close();
        return goals;
    }

    public void removeGoals() {
        db.delete(GOAL_TABLE, null, null);
    }

    public Integer packageRemainingTime(String packageName) {
        if (blockedList.containsKey(packageName)) {
            if (extendList.containsKey(packageName)) {
                // Check how much left for an app by extension+previous remaining time(could be < 0)
                if (extendList.get(packageName)+blockedList.get(packageName) < 0)
                    return 0;
                else
                    return extendList.get(packageName)+blockedList.get(packageName);
            }
            else{
                if (blockedList.get(packageName) < 0)
                    return 0;
                else
                    return blockedList.get(packageName);
            }
        }
        else {
            return 0;
        }
    }

    // Get a package total used recorded time, all time is counted by sec

    public Integer getDBRecordedTotalTime(String packageName) {
        Calendar cal = Calendar.getInstance();
        String currentYear = String.valueOf(cal.get(Calendar.YEAR));
        String currentMonth = String.valueOf(cal.get(Calendar.MONTH));
        String currentDay = String.valueOf(cal.get(Calendar.DATE));
        String currentDayOfWeek = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
        String session = prefs.getSession();

        Cursor appUsageCursor = db.rawQuery(
                "SELECT * FROM application_usage WHERE package_name=? and usage_year=? and usage_month=? and usage_day=? and usage_day_of_week=? and user_session=?",
                new String[]{
                        packageName,
                        currentYear,
                        currentMonth,
                        currentDay,
                        currentDayOfWeek,
                        session
                }
        );
        appUsageCursor.moveToFirst();
        Integer totalTime = 0;
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
        return totalTime;
    }

    // Helper function

    private static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    // Used in blocking service to calculate the time ratio

    public BigDecimal getRemainigTimeRatio(String packageName, Integer currentSec) {
        Calendar cal = Calendar.getInstance();
        String currentDayOfWeek = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));

        Integer totalTime = getDBRecordedTotalTime(packageName);

        // As we only stores number 1,2,3.. so here convert it to minutes.

        Integer goalTime = goalCache.get(packageName+currentDayOfWeek)*60;//*60;
        totalTime += currentSec;

        Log.d("Dynamic-GoalTime", String.valueOf(goalTime));
        Log.d("Dynamic-GoalTime", String.valueOf(totalTime));

        if (extendList.containsKey(packageName)) {
            Log.d("Dynamic-GoalTime", String.valueOf(extendList.get(packageName)));
            blockedList.put(packageName, goalTime-totalTime);
            if (extendList.get(packageName)+goalTime-totalTime <= 0) {
                return round(0, 3);
            }
            else {
                float ratio = (extendList.get(packageName)+goalTime-totalTime) / (float)(extendList.get(packageName)+goalTime);
                Log.d("Dynamic-GoalTime", String.valueOf(ratio));
                return round(ratio, 3);
            }
        }
        blockedList.put(packageName, goalTime-totalTime);
        if (goalTime-totalTime <= 0) {
            return round(0, 3);
        }
        else {
            float ratio = (goalTime-totalTime)/(float)goalTime;
            return round(ratio, 3);
        }
    }

    // Used in remote logging to server, get the valid records after a timestamp

    public ArrayList<StatForm> getLoggingRecordByTimestamp(String timestamp) {
        ArrayList<StatForm> statForms = new ArrayList<>();
        Cursor statCursor = db.rawQuery(
                "SELECT * FROM application_usage WHERE start_time >= ? and end_time <> -1",
                new String[]{
                    timestamp
                }
        );
        statCursor.moveToFirst();
        while(statCursor.isAfterLast() == false) {
            String packageName = statCursor.getString(statCursor.getColumnIndex(PACKAGE_NAME));
            Integer start_time = statCursor.getInt(statCursor.getColumnIndex(START_TIME));
            Integer end_time = statCursor.getInt(statCursor.getColumnIndex(END_TIME));
            // Ignore corner case, this could be caused by opening an app but blocked instantly
            if (end_time - start_time < 3) {
                statCursor.moveToNext();
                continue;
            }
            statForms.add(new StatForm(
                packageName,
                String.valueOf(start_time),
                String.valueOf(end_time)
            ));
            statCursor.moveToNext();
        }
        statCursor.close();
        return statForms;
    }

    /* extending the lifeline
     *  lifeline extension is only valid when the service is not restarted
     *  whenever the app is restarted, user has to request lifeline again
     */

    public void extendLifeline(String packageName) {
        Calendar cal = Calendar.getInstance();
        String currentDayOfWeek = String.valueOf(cal.get(Calendar.DAY_OF_WEEK));
        String key = packageName+currentDayOfWeek;
        if (blockedList.containsKey(packageName)) {
            blockedList.remove(packageName);
        }

        // As we only stores number 1,2,3.. so here convert it to minutes.

        if (goalCache.containsKey(key)) {
            extendList.put(
                    packageName,
                extendList.containsKey(packageName)?
                    extendList.get(packageName)+goalCache.get(key)*60
                :   goalCache.get(key)*60
            );
        }
        Log.d("GoalTime", String.valueOf(extendList.get(packageName)));
    }

    // Alert is not well formatted checkout the alert model

    public ArrayList<Alert> getAlertList() {
        ArrayList alerts = new ArrayList<>();
        Cursor alertCursor = db.rawQuery(
                "SELECT * FROM alert_record",
                new String[]{
                }
        );
        alertCursor.moveToFirst();
        while(alertCursor.isAfterLast() == false) {
            String app_name = alertCursor.getString(alertCursor.getColumnIndex(APPLICATION_NAME));
            String user_name = alertCursor.getString(alertCursor.getColumnIndex(USER_NAME));
            String subject = alertCursor.getString(alertCursor.getColumnIndex(ALERT_SUBJECT));
            alerts.add(new Alert(subject));
            alertCursor.moveToNext();
        }
        alertCursor.close();
        Collections.reverse(alerts);
        return alerts;
    }

    public void createAlert(String appName, String userName, String subject) {
        instance.db.beginTransaction();
        ContentValues newStat = new ContentValues();
        newStat.put(APPLICATION_NAME, appName);
        newStat.put(USER_NAME, userName);
        newStat.put(ALERT_SUBJECT, subject);
        db.insert(ALERT_RECORD_TABLE, null, newStat);
        instance.db.setTransactionSuccessful();
        instance.db.endTransaction();
    }

    // Android holds a different date/month count

    public Map<String, String> currentTime() {
        Map<String, String> timeInfo = new HashMap<String, String>();
        Calendar c = Calendar.getInstance();
        timeInfo.put("year", String.valueOf(c.get(Calendar.YEAR)));
        timeInfo.put("month", String.valueOf(c.get(Calendar.MONTH)));
        timeInfo.put("day", String.valueOf(c.get(Calendar.DATE)));
        timeInfo.put("day_of_week", String.valueOf(c.get(Calendar.DAY_OF_WEEK)));
        timeInfo.put("timestamp", String.valueOf(new Date().getTime()/1000));
        return timeInfo;
    }

    // fetch user wake up times latest 5 days
    public List<PhoneUsageTuple> getRecentPhoneWakeUpTimes() {
        instance.db.beginTransaction();
        List<PhoneUsageTuple> list = new ArrayList<>();
        List<Map<String, String>> recentDays = getRecentDaysInCalendar();
        for (int i = 0; i < recentDays.size(); i++) {
            Map<String, String> day = recentDays.get(i);
            Cursor phoneUsageCursor = db.rawQuery(
                    "SELECT * FROM wake_up_record WHERE usage_year = ? and usage_month = ? and usage_day = ? and usage_day_of_week = ?",
                    new String[]{
                        day.get("year"),
                        day.get("month"),
                        day.get("day"),
                        day.get("day_of_week")
                    }
            );
            int phoneUsageCount = phoneUsageCursor.getCount();
            PhoneUsageTuple<String, Integer> tuple = new PhoneUsageTuple(
                    day.get("month")+"/"+day.get("day"),
                    phoneUsageCount);
            list.add(tuple);
            phoneUsageCursor.close();
        }
        instance.db.endTransaction();
        return list;
    }


    // fetch user total wake up usage time
    public List<PhoneUsageTuple> getRecentPhoneUsageHours() {
        instance.db.beginTransaction();
        List<PhoneUsageTuple> list = new ArrayList<>();
        List<Map<String, String>> recentDays = getRecentDaysInCalendar();
        for (int i = 0; i < recentDays.size(); i++) {
            Map<String, String> day = recentDays.get(i);
            Cursor phoneUsageCursor = db.rawQuery(
                    "SELECT * FROM wake_up_record WHERE usage_year = ? and usage_month = ? and usage_day = ? and usage_day_of_week = ?",
                    new String[]{
                            day.get("year"),
                            day.get("month"),
                            day.get("day"),
                            day.get("day_of_week")
                    }
            );
            phoneUsageCursor.moveToFirst();
            Integer totalSec = 0;
            while (!phoneUsageCursor.isAfterLast()) {
                Integer startTime = Integer.valueOf(phoneUsageCursor.getString(phoneUsageCursor.getColumnIndex(START_TIME)));
                Integer endTime = Integer.valueOf(phoneUsageCursor.getString(phoneUsageCursor.getColumnIndex(END_TIME)));
                if (endTime != -1) {
                    totalSec += (endTime-startTime);
                }
                Log.d("TotalUsed", day.get("month")+"/"+day.get("day")+" "+String.valueOf(startTime)+" "+String.valueOf(endTime)+" "+String.valueOf(totalSec));
                phoneUsageCursor.moveToNext();
            }
            PhoneUsageTuple<String, Float> tuple = new PhoneUsageTuple(
                    day.get("month")+"/"+day.get("day"),
                    totalSec/60f);
            list.add(tuple);
            phoneUsageCursor.close();
        }
        instance.db.endTransaction();
        return list;
    }

    private List<Map<String, String>> getRecentDaysInCalendar() {
        int limit = 0;
        List<Map<String, String>> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        while (limit < 5) {
            Map<String, String> timeInfo = new HashMap<String, String>();
            timeInfo.put("year", String.valueOf(c.get(Calendar.YEAR)));
            timeInfo.put("month", String.valueOf(c.get(Calendar.MONTH)));
            timeInfo.put("day", String.valueOf(c.get(Calendar.DATE)));
            timeInfo.put("day_of_week", String.valueOf(c.get(Calendar.DAY_OF_WEEK)));
            timeInfo.put("timestamp", String.valueOf(new Date().getTime()/1000));
            list.add(timeInfo);
            c.add(Calendar.DAY_OF_MONTH, -1);
            limit += 1;
        }
        return list;
    }

    public class PhoneUsageTuple<X, Y> {
        public final X key;
        public final Y value;
        public PhoneUsageTuple(X key, Y value) {
            this.key = key;
            this.value = value;
        }
    }

}

