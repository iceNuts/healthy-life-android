package com.blue_stingray.healthy_life_app.model;

public class Goal {

    private String id;
    private String application_id;
    private String packageName;

    private transient int timeLimit;
    private transient int limitDay;

    public String getPackageName() {
        return packageName;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getLimitDay() {
        return limitDay;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setLimitDay(int limitDay) {
        this.limitDay = limitDay;
    }
}
