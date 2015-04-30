package com.blue_stingray.healthy_life_app.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.blue_stingray.healthy_life_app.storage.db.DataHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Application implements Serializable {

    public transient ResolveInfo info;
    public transient PackageManager pm;
    public transient DataHelper dataHelper;
    public transient Context context;

    private String id;
    private String device_id;
    private String package_name;
    private String name;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String version;
    private String color;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private Goal[] active_goals;
    private String deviceName;

    /**
     * @param pm PackageManager
     * @param info ResolveInfo
     */
    public Application(Context context, PackageManager pm, ResolveInfo info) {
        this.info = info;
        this.pm = pm;
        this.dataHelper = DataHelper.getInstance(context);
    }

    public Application(PackageManager pm, String package_name) {

        this.name = getApplicationName(pm);
        this.package_name = package_name;
        this.version = getApplicationVersion(pm);
    }

    public void setDeviceID(String device_id) {
        this.device_id = device_id;
    }

    public String getDeviceID() {return this.device_id;}

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public String getApplicationVersion(PackageManager pm) {
        try {
            return pm.getPackageInfo(package_name, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "0";
        }
    }

    public String getApplicationName(PackageManager pm) {
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( this.getPackageName(), 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return applicationName;
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
        if(dataHelper == null) {
            Goal today = null;

            for(Goal goal : active_goals) {
//                Log.i("healthy", "Today " + Calendar.DAY_OF_WEEK + " vs " + goal.getLimitDay());
                if(goal.getLimitDay() == Calendar.DAY_OF_WEEK) {
                    today = goal;
                    break;
                }
            }

            return today;
        }

        return dataHelper.getGoal(context, getPackageName());
    }

    public List<Goal> getGoals() {
        if(dataHelper != null) {
            List<Goal> list = dataHelper.getGoals(context, getPackageName());
            active_goals = list.toArray(new Goal[list.size()]);
            return Arrays.asList(active_goals);
        }

        return Arrays.asList(active_goals);
    }

    public String getPackageName() {
        if(info == null) {
            return package_name;
        }

        package_name = info.activityInfo.packageName;
        return info.activityInfo.packageName;
    }

    public Integer getDeviceId() {
        if(device_id != null) {
            return Integer.parseInt(device_id);
        }

        return -1;
    }

    public void setActiveGoals(Goal[] goals) {
        this.active_goals = goals;
    }

    public Goal[] getActiveGoals() {
        return active_goals;
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
