package com.blue_stingray.healthy_life_app.model;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Application implements Serializable {

    public transient ResolveInfo info;
    public transient PackageManager pm;

    /**
     * @param pm PackageManager
     * @param info ResolveInfo
     */
    public Application(PackageManager pm, ResolveInfo info) {
        this.info = info;
        this.pm = pm;
    }

    /**
     * Get the applications label
     * @return String name
     */
    public String getName() {
        return info.loadLabel(pm).toString();
    }

    /**
     * Get the applications icon Drawable
     * @return Drawable icon
     */
    public Drawable getIcon() {
        return info.loadIcon(pm);
    }

    /**
     * TODO
     * Check if this application has any goals yet
     * @return boolean
     */
    public boolean hasGoal() {
        return false;
    }

    /**
     *
     * @param activity Activity to get package manager from
     * @return ArrayList<Application> list of user installed applications
     */
    public static ArrayList<Application> createFromUserApplications(Activity activity) {
        ArrayList<Application> apps = new ArrayList<>();
        final PackageManager pm = activity.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveApps = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA);

        for(ResolveInfo resolveInfo : resolveApps) {
            apps.add(new Application(pm, resolveInfo));
        }

        return apps;
    }

}
