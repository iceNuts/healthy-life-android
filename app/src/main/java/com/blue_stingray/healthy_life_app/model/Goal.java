package com.blue_stingray.healthy_life_app.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.blue_stingray.healthy_life_app.storage.db.DataHelper;

public class Goal {

    private String id;
    private String application_id;
    private String packageName;
    private String day;
    private String hours;
    private String timeRemaining;
    private String usedToday;

    private transient float timeLimit;
    private transient float limitDay;
    public transient DataHelper dataHelper;

    public Goal(Context context) {
        this.dataHelper = DataHelper.getInstance(context);
    }

    public String getPackageName() {
        return packageName;
    }

    public float getGoalTime() {
        return timeLimit;
    }

    public double getTimeUsedSeconds() {
        return dataHelper.getDBRecordedTotalTime(getPackageName());
    }

    public double getTimeUsedMinutes() {
        return getTimeUsedSeconds() / 60.0;
    }

    public double getTimeUsedHours() {
        return getTimeUsedMinutes() / 60.0;
    }

    public float getDay() {
        return limitDay;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setTimeLimit(float timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setLimitDay(float limitDay) {
        this.limitDay = limitDay;
    }
}
