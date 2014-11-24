package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Alert;
import java.util.List;

public class AlertListAdapter extends BaseListAdapter<Alert> {

    public AlertListAdapter(Activity activity, List<Alert> alerts) {
        super(activity, alerts, R.layout.alert_list_row);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        Alert alert = data.get(position);
        ((TextView) convertView.findViewById(R.id.alert_name)).setText(Html.fromHtml("<font color='#0099CC'>" + alert.subject + "</font> "));

        return convertView;
    }

}
