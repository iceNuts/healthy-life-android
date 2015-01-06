package com.blue_stingray.healthy_life_app.model;

public class AppUsage {

    private Integer[] data;
    private String pointStart;
    private int pointInterval;
    private float percentageUse;

    public float getPercentageUse() {
        return percentageUse;
    }

    public String getPercentageUseFormatted() {
        return (int) (percentageUse * 100) + "%";
    }

    public Integer[] getData() {
        return data;
    }

}
