package com.blue_stingray.healthy_life_app.net.form;

import com.blue_stingray.healthy_life_app.model.Application;

public class StatForm {
    private final AppForm app;
    private final String start;
    private final String stop;

    public StatForm(Application app, String start, String stop) {
        this.app = new AppForm(app);
        this.start = start;
        this.stop = stop;
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
