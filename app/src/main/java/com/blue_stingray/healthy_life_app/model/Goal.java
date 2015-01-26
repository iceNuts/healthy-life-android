package com.blue_stingray.healthy_life_app.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.util.Time;

public class Goal {

    private String packageName;
    private String hours;
    private String day;
    private Application app;

    private transient float timeLimit;
    private transient float limitDay;
    public transient DataHelper dataHelper;

    public Goal(Context context) {
        this.dataHelper = DataHelper.getInstance(context);
    }

    public Application getApp() {
        return app;
    }

    public String getPackageName() {
        if(packageName == null) {
            packageName = getApp().getPackageName();
        }
        
        return packageName;
    }

    public int getGoalTime() {
        if(hours == null) {
            hours = String.valueOf(timeLimit);
        }

        return (int) Float.parseFloat(hours);
    }

    public String getDay() {
        if(day == null) {
            day = Time.dayTranslate((int) limitDay);
        }

        return day;
    }

    public float getLimitDay() {
        return limitDay;
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

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setTimeLimit(float timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setLimitDay(float limitDay) {
        this.limitDay = limitDay;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
