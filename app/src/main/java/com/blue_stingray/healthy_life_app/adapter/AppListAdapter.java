package com.blue_stingray.healthy_life_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private PackageManager pm;
    private Activity activity;
    private LayoutInflater inflater;
    private List<ApplicationInfo> apps;

    public AppListAdapter(Activity activity, List<ApplicationInfo> apps) {
        this.activity = activity;
        this.pm = this.activity.getPackageManager();
        Collections.sort(apps, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
                return lhs.loadLabel(pm).toString().compareTo(rhs.loadLabel(pm).toString());
            }
        });
        this.apps = apps;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int position) {
        return apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.app_list_row, parent, false);
        }

        TextView appName = (TextView) convertView.findViewById(R.id.app_name);
        ImageView appIcon = (ImageView) convertView.findViewById(R.id.app_icon);

        ApplicationInfo info = apps.get(position);

        appIcon.setImageDrawable(info.loadIcon(pm));
        appName.setText(info.loadLabel(pm).toString());

        return convertView;
    }
}
