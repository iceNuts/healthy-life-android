package com.blue_stingray.healthy_life_app.net.form;

import com.blue_stingray.healthy_life_app.model.Application;

public class GoalForm {

    private final Double hours;
    private final String day;
    private final AppForm app;
    private final Integer device_id;

    public GoalForm(Application app, Double hours, String day, Integer device_id) {
        this.hours = hours;
        this.day = day;
        this.app = new AppForm(app);
        this.device_id = device_id;
    }

    private class AppForm {
        private String package_name;
        private String name;
        private String version;

        public AppForm(Application app) {
            this.package_name = app.getPackageName();
            this.name = app.getName();
            this.version = app.getVersion();
        }
    }

}
