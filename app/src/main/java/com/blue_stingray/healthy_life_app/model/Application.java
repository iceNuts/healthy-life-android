package com.blue_stingray.healthy_life_app.model;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.Serializable;

public class Application implements Serializable {

    public ResolveInfo info;
    public PackageManager pm;

    public Application(PackageManager pm, ResolveInfo info) {
        this.info = info;
        this.pm = pm;
    }

    public String getName() {
        return info.loadLabel(pm).toString();
    }

    public boolean hasGoal() {
        return false;
    }

}
