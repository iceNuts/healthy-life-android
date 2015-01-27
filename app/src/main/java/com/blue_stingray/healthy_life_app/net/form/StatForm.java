package com.blue_stingray.healthy_life_app.net.form;

public class StatForm {
    private final String package_name;
    private final String start;
    private final String stop;
    private final Integer device_id;

    public StatForm(String package_name, String start, String stop, Integer user_id) {
        this.package_name = package_name;
        this.start = start;
        this.stop = stop;
        this.device_id = user_id;
    }

    public StatForm(String package_name, String start, String stop) {
        this(package_name, start, stop, null);
    }
}
