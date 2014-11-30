package com.blue_stingray.healthy_life_app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Lifeline;
import java.util.List;

public class LifelineRequestListAdapter extends BaseListAdapter<Lifeline> {

    Activity activity;

    public LifelineRequestListAdapter(Activity activity, List<Lifeline> data) {
        super(activity, data, R.layout.lifeline_request_list_row);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);

        ((TextView) convertView.findViewById(R.id.user)).setText("Brian Rehg");
        ((TextView) convertView.findViewById(R.id.app_name)).setText("Facebook");
        ((TextView) convertView.findViewById(R.id.last_stat)).setText("9:06 on 10/14/14");

        convertView.findViewById(R.id.deny).setOnClickListener(new DenyClickListener());
        convertView.findViewById(R.id.approve).setOnClickListener(new AcceptClickListener());

        return convertView;
    }

    public class DenyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(activity, "Deny", Toast.LENGTH_SHORT).show();
        }
    }

    public class AcceptClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(activity, "Accept", Toast.LENGTH_SHORT).show();
        }
    }

}
