package com.blue_stingray.healthy_life_app.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.util.Time;

import java.io.Serializable;

public class Goal implements Serializable {

    private String packageName;
    private String hours;
    private String day;
    private Double usedToday;
    private Double timeRemaining;
    private Application app;

    private transient Float timeLimit;
    private transient Float limitDay;
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
            day = Time.dayTranslate(limitDay.intValue());
        }

        return day;
    }

    public float getLimitDay() {
        if(limitDay == null) {
            limitDay = (float) Time.dayTranslate(day);
        }

        return limitDay;
    }

    public double getTimeUsedSeconds() {
        if(usedToday == null) {
            usedToday = dataHelper.getDBRecordedTotalTime(getPackageName()).doubleValue();
        }

        return usedToday;
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
