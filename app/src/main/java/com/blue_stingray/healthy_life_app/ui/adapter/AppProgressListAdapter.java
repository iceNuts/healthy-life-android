package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.AppGoal;
import com.blue_stingray.healthy_life_app.model.Application;

import org.w3c.dom.Text;

import java.util.List;

public class AppProgressListAdapter extends BaseListAdapter<AppGoal> {

    public AppProgressListAdapter(Activity activity, List<AppGoal> apps) {
        super(activity, apps, R.layout.app_list_row_progress);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        AppGoal app = data.get(position);
        RoundCornerProgressBar progressBar = (RoundCornerProgressBar) convertView.findViewById(R.id.progress);

        ((TextView) convertView.findViewById(R.id.app_name)).setText(app.name);
        ((TextView) convertView.findViewById(R.id.time_remaining)).setText(app.remaining + " hours remaining");
        ((TextView) convertView.findViewById(R.id.hours_used)).setText(app.spent + " of " + app.total + " used");
        progressBar.setProgress(app.spent);
        progressBar.setMax(app.total);

        return convertView;
    }
}