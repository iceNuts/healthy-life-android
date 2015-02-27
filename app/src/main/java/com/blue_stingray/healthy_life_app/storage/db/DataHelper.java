package com.blue_stingray.healthy_life_app.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.util.Log;

import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.net.form.StatForm;
import com.google.inject.Inject;

import java.io.Serializable;
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
    private PackageManager pm;
    private ArrayList<String> apps;
    private CountDownTimer GoalCacheTimer;
    private boolean isLoading;

    public static synchronized DataHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DataHelper();
            instance.dbHelper = new DatabaseHelper(context);
            instance.db = instance.dbHelper.getWritableDatabase();
            instance.prefs = new SharedPreferencesHelper(context);
            instance.blockedList = new HashMap<>();
            instance.extendList = new HashMap<>();
            instance.pm = context.getPackageManager();
            instance.apps = new ArrayList<>();
            instance.isLoading = false;
            instance.GoalCacheTimer = new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    instance.isLoading = false;
                    instance.__loadGoalCache();
                }
            };
        }
        instance.load3rdPartyPackageNames(context);
        instance.loadGoalCache();
        return instance;
    }

    private void load3rdPartyPackageNames(Context context) {
        if (context == null)
            return;
        apps.clear();
        final PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveApps = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        for(ResolveInfo resolveInfo : resolveApps) {
            apps.add(resolveInfo.activityInfo.packageName);
        }
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
                    newStat.put(USER_ID, prefs.getUserID());
                    // Delete old goal
                    int ret = instance.db.delete(GOAL_TABLE, "package_name=? and limit_day=? and user_id=?", new String[]{
                            packageName,
                            pairs.getKey().toString(),
                            prefs.getUserID()
                    });
                    // Insert new goal
                    long retValue = instance.db.insert(GOAL_TABLE, null, newStat);
                    it.remove();
                }
                instance.db.setTransactionSuccessful();
                instance.db.endTransaction();
            }
        }).start();
        instance.loadGoalCache();
    }

    // GoalCache key is packageName+dayOfWeek, other cache is only packageName as key

    private void loadGoalCache() {

        if (instance.goalCache == null && !isLoading) {
            __loadGoalCache();
        }

        if (!isLoading) {
            isLoading = true;
            GoalCacheTimer.start();
        }
    }

    private void __loadGoalCache() {
        HashMap<String, Integer> cache = new HashMap<>();
        Cursor goalCursor = db.rawQuery(
                "SELECT * FROM goal_table WHERE user_id=\"" + prefs.getUserID() + "\"",
                new String[]{
                }
        );
        try {
            goalCursor.moveToFirst();
            while (goalCursor.isAfterLast() == false) {
                String packageName = goalCursor.getString(goalCursor.getColumnIndex(PACKAGE_NAME));
                Integer hrs = goalCursor.getInt(goalCursor.getColumnIndex(TIME_LIMIT));
                Integer day = goalCursor.getInt(goalCursor.getColumnIndex(LIMIT_DAY));
                cache.put(packageName + String.valueOf(day), hrs);
                goalCursor.moveToNext();
            }
        }
        finally {
            goalCursor.close();
        }
        instance.goalCache = cache;
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
                "SELECT * FROM goal_table WHERE package_name=\"" + packageName + "\" AND limit_day=\"" + day + "\""+"AND user_id=\""+prefs.getUserID()+"\"",
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
        else {
            goalCursor.close();
        }
        return null;
    }

    public List<Goal> getGoals(Context context, String packageName) {
        ArrayList<Goal> goals = new ArrayList<>();
        Log.d("CursorWindow", "getGoals Start");
        Cursor goalCursor = db.rawQuery(
                "SELECT * FROM goal_table WHERE package_name=\"" + packageName + "\"" + "AND user_id=\"" + prefs.getUserID() + "\"",
                new String[]{
                }
        );

        try {
            while (goalCursor.moveToNext()) {
                Goal goal = new Goal(context);
                goal.setPackageName(goalCursor.getString(goalCursor.getColumnIndex(PACKAGE_NAME)));
                goal.setTimeLimit(goalCursor.getInt(goalCursor.getColumnIndex(TIME_LIMIT)));
                goal.setLimitDay(goalCursor.getInt(goalCursor.getColumnIndex(LIMIT_DAY)));
                goals.add(goal);
            }
            Log.d("CursorWindow", "getGoals End");
        }
        finally {
            goalCursor.close();
        }
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
        Integer totalTime = 0;
        try {
            appUsageCursor.moveToFirst();
            while (appUsageCursor.isAfterLast() == false) {
                Integer start_time = appUsageCursor.getInt(appUsageCursor.getColumnIndex("start_time"));
                Integer end_time = appUsageCursor.getInt(appUsageCursor.getColumnIndex("end_time"));

                // Avoid some unfinished recording
                if (end_time == -1) {
                    appUsageCursor.moveToNext();
                    continue;
                }
                totalTime += (end_time - start_time);
                appUsageCursor.moveToNext();
            }
        }
        finally {
            appUsageCursor.close();
        }
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


        if (extendList.containsKey(packageName)) {
            blockedList.put(packageName, goalTime-totalTime);
            if (extendList.get(packageName)+goalTime-totalTime <= 0) {
                return round(0, 3);
            }
            else {
                float ratio = (extendList.get(packageName)+goalTime-totalTime) / (float)(extendList.get(packageName)+goalTime);
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
                "SELECT * FROM application_usage WHERE start_time >= ? and end_time <> -1 and user_id=?",
                new String[]{
                        timestamp,
                        prefs.getUserID()
                }
        );
        try {
            statCursor.moveToFirst();
            while (statCursor.isAfterLast() == false) {
                String packageName = statCursor.getString(statCursor.getColumnIndex(PACKAGE_NAME));
                // Get app
                Application app = new Application(pm, packageName);
                Integer start_time = statCursor.getInt(statCursor.getColumnIndex(START_TIME));
                Integer end_time = statCursor.getInt(statCursor.getColumnIndex(END_TIME));
                // Ignore corner case, this could be caused by opening an app but blocked instantly
                if (end_time - start_time < 3) {
                    statCursor.moveToNext();
                    continue;
                }
                statForms.add(new StatForm(
                        app,
                        String.valueOf(start_time),
                        String.valueOf(end_time)
                ));
                statCursor.moveToNext();
            }
        }
        finally {
            statCursor.close();
        }
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
    }

    // Alert is not well formatted checkout the alert model

    public ArrayList<Alert> getAlertList() {
        ArrayList alerts = new ArrayList<>();
        Cursor alertCursor = db.rawQuery(
                "SELECT * FROM alert_record where user_id=?",
                new String[]{
                        prefs.getUserID()
                }
        );
        try {
            alertCursor.moveToFirst();
            while (alertCursor.isAfterLast() == false) {
                String app_name = alertCursor.getString(alertCursor.getColumnIndex(APPLICATION_NAME));
                String user_name = alertCursor.getString(alertCursor.getColumnIndex(USER_NAME));
                String subject = alertCursor.getString(alertCursor.getColumnIndex(ALERT_SUBJECT));
                alerts.add(new Alert(subject));
                alertCursor.moveToNext();
            }
        }
        finally {
            alertCursor.close();
        }
        Collections.reverse(alerts);
        return alerts;
    }

    public void createAlert(String appName, String userName, String subject) {
        instance.db.beginTransaction();
        ContentValues newStat = new ContentValues();
        newStat.put(APPLICATION_NAME, appName);
        newStat.put(USER_NAME, userName);
        newStat.put(ALERT_SUBJECT, subject);
        newStat.put(USER_ID, prefs.getUserID());
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
    public List<PhoneUsageTuple> getRecentPhoneWakeUpTimes(int option) {
        instance.db.beginTransaction();
        List<PhoneUsageTuple> list = new ArrayList<>();
        List<Map<String, String>> recentDays = getRecentDaysInCalendar(option);
        for (int i = 0; i < recentDays.size(); i++) {
            Map<String, String> day = recentDays.get(i);
            Cursor phoneUsageCursor = db.rawQuery(
                    "SELECT * FROM wake_up_record WHERE usage_year = ? and usage_month = ? and usage_day = ? and usage_day_of_week = ? and user_id = ?",
                    new String[]{
                        day.get("year"),
                        day.get("month"),
                        day.get("day"),
                        day.get("day_of_week"),
                        prefs.getUserID()
                    }
            );
            try {
                int phoneUsageCount = phoneUsageCursor.getCount();
                PhoneUsageTuple<String, Integer> tuple = new PhoneUsageTuple(
                        String.valueOf(Integer.valueOf(day.get("month")) + 1) + "/" + day.get("day"),
                        phoneUsageCount);
                list.add(tuple);
            }
            finally {
                phoneUsageCursor.close();
            }
        }
        instance.db.endTransaction();
        return list;
    }


    // fetch user total wake up usage time
    public List<PhoneUsageTuple> getRecentPhoneUsageHours(int option) {
        instance.db.beginTransaction();
        List<PhoneUsageTuple> list = new ArrayList<>();
        List<Map<String, String>> recentDays = getRecentDaysInCalendar(option);
        for (int i = 0; i < recentDays.size(); i++) {
            Map<String, String> day = recentDays.get(i);
            Cursor phoneUsageCursor = db.rawQuery(
                    "SELECT * FROM application_usage WHERE usage_year=? and usage_month=? and usage_day=? and usage_day_of_week=? and user_id=? and end_time <> -1",
                    new String[]{
                            day.get("year"),
                            day.get("month"),
                            day.get("day"),
                            day.get("day_of_week"),
                            prefs.getUserID()
                    }
            );
            try {
                phoneUsageCursor.moveToFirst();
                Integer totalSec = 0;
                while (!phoneUsageCursor.isAfterLast()) {
                    Integer startTime = Integer.valueOf(phoneUsageCursor.getString(phoneUsageCursor.getColumnIndex(START_TIME)));
                    Integer endTime = Integer.valueOf(phoneUsageCursor.getString(phoneUsageCursor.getColumnIndex(END_TIME)));
                    if (endTime != -1) {
                        totalSec += (endTime - startTime);
                    }
                    phoneUsageCursor.moveToNext();
                }
                PhoneUsageTuple<String, Float> tuple = new PhoneUsageTuple(
                        String.valueOf(Integer.valueOf(day.get("month")) + 1) + "/" + day.get("day"),
                        totalSec / 60f);
                list.add(tuple);
            }
            finally {
                phoneUsageCursor.close();
            }
        }
        instance.db.endTransaction();
        return list;
    }

    public List<DetailPhoneUsageTuple> getDetailedPhoneUsage(int i, int option) {
        instance.db.beginTransaction();
        List<DetailPhoneUsageTuple> list = new ArrayList<>();
        List<Map<String, String>> recentDays = getRecentDaysInCalendar(option);
        Map<String, String> day = recentDays.get(i);
        Cursor phoneUsageCursor = db.rawQuery(
                "SELECT * FROM application_usage WHERE usage_year=? and usage_month=? and usage_day=? and usage_day_of_week=? and user_id=? and end_time <> -1",
                new String[]{
                        day.get("year"),
                        day.get("month"),
                        day.get("day"),
                        day.get("day_of_week"),
                        prefs.getUserID()
                }
        );
        try {
            phoneUsageCursor.moveToFirst();
            while (!phoneUsageCursor.isAfterLast()) {
                Integer startTime = Integer.valueOf(phoneUsageCursor.getString(phoneUsageCursor.getColumnIndex(START_TIME)));
                Integer endTime = Integer.valueOf(phoneUsageCursor.getString(phoneUsageCursor.getColumnIndex(END_TIME)));
                Integer usedTime = endTime - startTime;
                String packageName = phoneUsageCursor.getString(phoneUsageCursor.getColumnIndex(PACKAGE_NAME));
                Integer usedSec = usedTime / 60;
                boolean notFound = true;
                for (DetailPhoneUsageTuple tp: list) {
                    if (packageName.equals(tp.packageName)) {
                        tp.value = (Integer)tp.value+usedSec;
                        notFound = false;
                        break;
                    }
                }
                if (notFound) {
                    DetailPhoneUsageTuple<String, Integer> tuple = new DetailPhoneUsageTuple(packageName, usedSec);
                    list.add(tuple);
                }
                phoneUsageCursor.moveToNext();
            }
        }
        finally {
            phoneUsageCursor.close();
        }
        instance.db.endTransaction();
        return list;
    }

    private List<Map<String, String>> getRecentDaysInCalendar(int option) {
        int limit = 0;
        int days = 5;
        switch (option) {
            case 0:
                days = 5;
                break;
            case 1:
                days = 10;
                break;
            case 2:
                days = 15;
                break;
            default:
                days = 5;
                break;
        }
        List<Map<String, String>> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        while (limit < days) {
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

    public boolean is3rdParty(String packageName) {
        return apps.contains(packageName);
    }


    public class PhoneUsageTuple<X, Y> {
        public  X key;
        public  Y value;
        public PhoneUsageTuple(X key, Y value) {
            this.key = key;
            this.value = value;
        }
    }

    public class DetailPhoneUsageTuple<X, Y>{
        public  X packageName;
        public  Y value;
        public DetailPhoneUsageTuple(X packageName, Y value) {
            this.packageName = packageName;
            this.value = value;
        }
    }

}

