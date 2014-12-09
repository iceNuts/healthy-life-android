package com.blue_stingray.healthy_life_app.net.form;

public class GoalForm {

    private final int hours;
    private final String package_id;
    private final String day;

    public GoalForm(String package_id, int hours, String day) {
        this.hours = hours;
        this.package_id = package_id;
        this.day = day;
    }

}
