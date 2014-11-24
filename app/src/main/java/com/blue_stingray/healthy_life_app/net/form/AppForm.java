package com.blue_stingray.healthy_life_app.net.form;

public class AppForm {

    private final String package_name;
    private final String name;
    private final String version;
    private final int icon_id;

    public AppForm(CharSequence package_name, CharSequence name, CharSequence version, int icon_id) {
        this.package_name = package_name.toString();
        this.name = name.toString();
        this.version = version.toString();
        this.icon_id = icon_id;
    }

}
