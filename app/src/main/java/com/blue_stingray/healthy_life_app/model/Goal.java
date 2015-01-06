package com.blue_stingray.healthy_life_app.model;

public class Goal {

    private String id;
    private String application_id;
    private String packageName;

    private transient float timeLimit;
    private transient float limitDay;

    public String getPackageName() {
        return packageName;
    }

    public float getTimeLimit() {
        return timeLimit;
    }

    public float getLimitDay() {
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
