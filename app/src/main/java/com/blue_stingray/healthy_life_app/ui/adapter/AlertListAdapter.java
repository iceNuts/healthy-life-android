package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.util.Time;

import org.w3c.dom.Text;

import java.util.List;

public class AlertListAdapter extends BaseListAdapter<Alert> {

    private Activity activity;
    private Alert alert;

    public AlertListAdapter(Activity activity, List<Alert> alerts) {
        super(activity, alerts, R.layout.alert_list_row);

        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        alert = data.get(position);
        Log.i("healthy", "Alert type : " + alert.getTargetType());

        if(alert.getTargetType().equals("Application"))
        {

            // render Application alert
            return getApplicationView(position, convertView, parent);
        }
        else if(alert.getTargetType().equals("UsageReport"))
        {

            // render UsageReport alert
            return getUsageReportView(position, convertView, parent);
        }
        else if(alert.getTargetType().equals("Goal"))
        {

            // render Goal alert
            return getGoalView(position, convertView, parent);
        }
        else
        {

            // render an empty view, since we don't recognize this alert type
            return getEmptyView(position, convertView, parent);
        }
    }

    private View getApplicationView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, inflater.inflate(R.layout.alert_list_row, parent, false), parent);

        ((TextView) view.findViewById(R.id.subject)).setText(alert.getSubject());
        ((TextView) view.findViewById(R.id.action)).setText(alert.getAction());
        ((TextView) view.findViewById(R.id.target)).setText(alert.getTarget());
        ((TextView) view.findViewById(R.id.created_at)).setText(Time.getPrettyTime(alert.getCreatedAt()));

        return view;
    }

    private View getUsageReportView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, inflater.inflate(R.layout.alert_usage_report_list_row, parent, false), parent);

        view.findViewById(R.id.alert_container).setOnClickListener(new LinkToListener(alert.getTarget() + "?id=" + alert.getTargetId()));
        ((TextView) view.findViewById(R.id.subject)).setText(alert.getSubject());
        ((TextView) view.findViewById(R.id.action)).setText("has a new");
        ((TextView) view.findViewById(R.id.target)).setText("usage report");
        ((TextView) view.findViewById(R.id.url)).setText(alert.getTarget());
        ((TextView) view.findViewById(R.id.created_at)).setText(Time.getPrettyTime(alert.getCreatedAt()));

        return view;
    }

    private View getGoalView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, inflater.inflate(R.layout.alert_goal_list_row, parent, false), parent);

        ((TextView) view.findViewById(R.id.subject)).setText(alert.getSubject());
        ((TextView) view.findViewById(R.id.action)).setText(alert.getAction());
        ((TextView) view.findViewById(R.id.target)).setText("goal");
        ((TextView) view.findViewById(R.id.goal_name)).setText(alert.getTarget());
        ((TextView) view.findViewById(R.id.created_at)).setText(Time.getPrettyTime(alert.getCreatedAt()));

        return view;
    }

    private View getEmptyView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, inflater.inflate(R.layout.empty, parent, false), parent);
    }

    private class LinkToListener implements View.OnClickListener {

        private String link;

        public LinkToListener(String link) {
            this.link = link;
        }

        @Override
        public void onClick(View v) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            activity.startActivity(browserIntent);
        }

    }

}
