package com.blue_stingray.healthy_life_app.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.blue_stingray.healthy_life_app.storage.db.DataHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Application implements Serializable {

    public transient ResolveInfo info;
    public transient PackageManager pm;
    public transient DataHelper dataHelper;
    public transient Context context;

    private String id;
    private String deviceId;
    private String packageName;
    private String name;
    private String version;
    private String color;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private Goal[] active_goals;

    /**
     * @param pm PackageManager
     * @param info ResolveInfo
     */
    public Application(Context context, PackageManager pm, ResolveInfo info) {
        this.info = info;
        this.pm = pm;
        this.dataHelper = DataHelper.getInstance(context);
    }

    public String getId() {
        return id;
    }

    /**
     * Get the applications label
     * @return String name
     */
    public String getName() {
        if(name == null) {
            name = info.loadLabel(pm).toString();
        }

        return name;
    }

    /**
     * Checks if the application can pull an icon from the package manager
     * @return boolean
     */
    public boolean hasIcon() {
        return !(info == null || pm == null);
    }

    /**
     * Get the applications icon Drawable
     * @return Drawable icon
     */
    public Drawable getIcon() {
        if(!hasIcon()) {
            return null;
        }

        return info.loadIcon(pm);
    }

    /**
     * Check if this application has any goals yet
     * @return boolean
     */
    public boolean hasGoal() {
        if(dataHelper == null) {
            return !(active_goals == null || active_goals.length == 0);
        }

        return dataHelper.isGoal(getPackageName());
    }

    public Goal getGoal() {
        return dataHelper.getGoal(context, getPackageName());
    }

    public String getPackageName() {
        if(info == null) {
            return null;
        }

        return info.activityInfo.packageName;
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
            apps.add(new Application(activity, pm, resolveInfo));
        }

        return apps;
    }

}
