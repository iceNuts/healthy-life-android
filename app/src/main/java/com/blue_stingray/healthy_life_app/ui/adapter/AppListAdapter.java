package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Application;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppListAdapter extends BaseListAdapter<Application> {

    public AppListAdapter(Activity activity, List<Application> apps) {
        super(activity, apps, R.layout.app_list_row);
        Collections.sort(data, new Comparator<Application>() {
            @Override
            public int compare(Application lhs, Application rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        Application app = data.get(position);
        ((TextView) convertView.findViewById(R.id.app_name)).setText(app.getName());
        ((ImageView) convertView.findViewById(R.id.app_icon)).setImageDrawable(app.getIcon());

        return convertView;
    }
}