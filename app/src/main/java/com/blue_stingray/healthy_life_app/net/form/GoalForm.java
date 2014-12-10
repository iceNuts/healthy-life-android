package com.blue_stingray.healthy_life_app.net.form;

public class GoalForm {

    private final int hours;
    private final String package_name;
    private final String day;

    public GoalForm(String package_name, int hours, String day) {
        this.hours = hours;
        this.package_name = package_name;
        this.day = day;
    }

}
