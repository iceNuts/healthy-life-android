package com.blue_stingray.healthy_life_app.net.form;

/**
 * Created by BillZeng on 12/8/14.
 */
public class StatForm {
    private final String package_name;
    private final String start;
    private final String stop;

    public StatForm(String package_name, String start, String stop) {
        this.package_name = package_name;
        this.start = start;
        this.stop = stop;
    }
}
