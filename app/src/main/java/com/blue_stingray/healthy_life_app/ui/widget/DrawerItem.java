package com.blue_stingray.healthy_life_app.ui.widget;

public class DrawerItem {

    public Class className;
    public String icon;
    public String title;
    public boolean isAdmin;

    public DrawerItem(Class className, String icon, String title, boolean isAdmin) {
        this.className = className;
        this.icon = icon;
        this.title = title;
        this.isAdmin = isAdmin;
    }

    public DrawerItem(Class className, String icon, String title) {
        this(className, icon, title, false);
    }

}
