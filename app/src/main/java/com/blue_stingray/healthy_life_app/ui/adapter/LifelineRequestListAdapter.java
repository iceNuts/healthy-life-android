package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Lifeline;
import java.util.List;

public class LifelineRequestListAdapter extends BaseListAdapter<Lifeline> {

    public LifelineRequestListAdapter(Activity activity, List<Lifeline> data) {
        super(activity, data, R.layout.lifeline_request_list_row);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        ((TextView) convertView.findViewById(R.id.user)).setText("Brian Rehg");
        ((TextView) convertView.findViewById(R.id.app_name)).setText("Facebook");
        ((TextView) convertView.findViewById(R.id.last_stat)).setText("9:06 on 10/14/14");

        return convertView;
    }
}
