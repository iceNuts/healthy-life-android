package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Alert;
import java.util.List;

public class AlertListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Alert> alerts;

    public AlertListAdapter(Activity activity, List<Alert> alerts) {
        this.alerts = alerts;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return alerts.size();
    }

    @Override
    public Object getItem(int position) {
        return alerts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.alert_list_row, parent, false);
        }

        Alert alert = alerts.get(position);
        ((TextView) convertView.findViewById(R.id.alert_name)).setText(Html.fromHtml("<font color='#0099CC'>" + alert.subject + "</font> "));

        return convertView;
    }

}
