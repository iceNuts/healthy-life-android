package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.model.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class AlertListAdapter extends BaseListAdapter<Alert> {

    public AlertListAdapter(Activity activity, List<Alert> alerts) {
        super(activity, alerts, R.layout.alert_list_row);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Alert alert = data.get(position);

        if(alert.getTargetType().equals("Application") || alert.getTargetType().equals("Goal"))
        {

            // render Application alert
            return getApplicationView(position, convertView, parent);
        }
        else if(alert.getTargetType().equals("UsageReport"))
        {

            // render UsageReport alert
            return getUsageReportView(position, convertView, parent);
        }
        else
        {

            // render an empty view, since we don't recognize this alert type
            return getEmptyView(position, convertView, parent);
        }

//        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
//        ((TextView) convertView.findViewById(R.id.subject)).setText(alert.getSubject());
//        ((TextView) convertView.findViewById(R.id.action)).setText(alert.getAction());
//        ((TextView) convertView.findViewById(R.id.target)).setText(alert.getTarget());
//        ((TextView) convertView.findViewById(R.id.created_at)).setText(formatter.format(alert.getCreatedAt()));
    }

    private View getApplicationView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, inflater.inflate(R.layout.alert_list_row, parent, false), parent);
    }

    private View getUsageReportView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, inflater.inflate(R.layout.alert_usage_list_row, parent, false), parent);
    }

    private View getEmptyView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, inflater.inflate(R.layout.empty, parent, false), parent);
    }

}
