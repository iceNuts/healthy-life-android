package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Application;
import java.util.List;

public class AppPercentListAdapter extends BaseListAdapter<Application> {

    public AppPercentListAdapter(Activity activity, List<Application> apps) {
        this(activity, apps, R.layout.app_list_row_simple_card);
    }

    public AppPercentListAdapter(Activity activity, List<Application> apps, int resource) {
        super(activity, apps, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        Application app = data.get(position);

        ((TextView) convertView.findViewById(R.id.app_name)).setText(app.getName());
        ((ImageView) convertView.findViewById(R.id.app_icon)).setImageDrawable(app.getIcon());
        ((TextView) convertView.findViewById(R.id.message)).setText("20%");

        return convertView;
    }
}