package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Application;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppGoalListAdapter extends BaseListAdapter<Application> {

    private Activity activity;

    public AppGoalListAdapter(Activity activity, List<Application> apps) {
        this(activity, apps, R.layout.app_list_row_simple_card);
    }

    public AppGoalListAdapter(Activity activity, List<Application> apps, int resource) {
        super(activity, apps, resource);

        this.activity = activity;

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

        // Set the icon if it exists, otherwise remove icon
        if(app.hasIcon()) {
            ((ImageView) convertView.findViewById(R.id.app_icon)).setImageDrawable(app.getIcon());
        } else {
            ((LinearLayout) convertView.findViewById(R.id.container)).removeView(convertView.findViewById(R.id.app_icon_container));
        }

        // Set if the app has a goal or not
        if(app.hasGoal()) {
            ((TextView) convertView.findViewById(R.id.message)).setText("Goal is set");
            convertView.findViewById(R.id.container).setBackgroundColor(activity.getResources().getColor(R.color.green_primary_light));
        } else {
            ((TextView) convertView.findViewById(R.id.message)).setText("No goal currently set");
            convertView.findViewById(R.id.container).setBackgroundColor(activity.getResources().getColor(R.color.white));
        }

        return convertView;
    }
}