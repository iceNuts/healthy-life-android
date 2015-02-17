package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;

import java.util.List;

/**
 * Created by BillZeng on 2/16/15.
 */
public class DetailedPhoneUsageListAdapter extends BaseListAdapter<DataHelper.DetailPhoneUsageTuple> {

    Activity activity;
    private List<DataHelper.DetailPhoneUsageTuple> appUsagelist;
    private DataHelper.DetailPhoneUsageTuple currentAppUsage;
    private RestInterface rest;
    private PackageManager pkgMgr;

    public DetailedPhoneUsageListAdapter(Activity activity, List<DataHelper.DetailPhoneUsageTuple> list, RestInterface rest) {
        super(activity, list, R.layout.app_usage_list_row);
        Log.d("Usage", list.toString());
        this.activity = activity;
        this.appUsagelist = list;
        this.rest = rest;
        pkgMgr = activity.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        currentAppUsage = appUsagelist.get(position);
        String packageName = (String)currentAppUsage.packageName;
        int minutes = (int)currentAppUsage.value;

        ApplicationInfo ai;

        try {
            ai = pkgMgr.getApplicationInfo(packageName, 0);
            final String applicationName = (String) (ai != null ? pkgMgr.getApplicationLabel(ai) : "(unknown)");
            ((TextView) convertView.findViewById(R.id.app_name)).setText(applicationName);

            // Set the icon if it exists, otherwise remove icon
            ((ImageView) convertView.findViewById(R.id.app_icon)).setImageDrawable(ai.loadIcon(pkgMgr));

            String usedTime;

            if (minutes > 60) {
                usedTime = String.valueOf(minutes/60)+"hr"+String.valueOf(minutes%60)+"min";
            }
            else {
                usedTime = String.valueOf(minutes)+"min";
            }

            ((TextView) convertView.findViewById(R.id.total_hours)).setText(usedTime);
            convertView.findViewById(R.id.container).setBackgroundColor(activity.getResources().getColor(R.color.green_primary_light));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}




















