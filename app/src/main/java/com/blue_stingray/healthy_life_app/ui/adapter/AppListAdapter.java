package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Application;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private PackageManager pm;
    private LayoutInflater inflater;
    private List<Application> apps;

    public AppListAdapter(Activity activity, List<Application> apps) {
        this.pm = activity.getPackageManager();
        Collections.sort(apps, new Comparator<Application>() {
            @Override
            public int compare(Application lhs, Application rhs) {
                return lhs.getName().compareTo(rhs.getName());
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

        Application app = apps.get(position);
        ((TextView) convertView.findViewById(R.id.app_name)).setText(app.getName());
        ((ImageView) convertView.findViewById(R.id.app_icon)).setImageDrawable(app.getIcon());

        return convertView;
    }
}