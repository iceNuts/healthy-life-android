package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.AlertListAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import roboguice.fragment.RoboFragment;

/**
 * Provides a list of alerts.
 */
public class AlertsFragment extends RoboFragment {

    private ListView alertList;
    private DataHelper dataHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        getActivity().setTitle(R.string.title_usage_alerts);
        alertList = (ListView) view.findViewById(R.id.alert_list);
        dataHelper = DataHelper.getInstance(getActivity());
        createList();
        return view;
    }

    public void createList() {

        // dummy data
        ArrayList<Alert> alerts = dataHelper.getAlertList();
        final AlertListAdapter adapter = new AlertListAdapter(getActivity(), alerts);

        if(alertList != null) {
            alertList.setAdapter(adapter);
        }
    }
}
