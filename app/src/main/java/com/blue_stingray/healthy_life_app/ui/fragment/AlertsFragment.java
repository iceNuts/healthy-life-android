package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RestInterfaceProvider;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.AlertListAdapter;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Provides a list of alerts.
 */
public class AlertsFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.alert_list)
    private ListView alertList;

    private DataHelper dataHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        dataHelper = DataHelper.getInstance(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_usage_alerts);
        createList();
    }

    public void createList() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Alerts", "Loading...");

        rest.getAlerts(new Callback<List<Alert>>() {
            @Override
            public void success(List<Alert> alerts, Response response) {
                loading.cancel();

                AlertListAdapter adapter = new AlertListAdapter(getActivity(), alerts);
                alertList.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {
                loading.cancel();
            }
        });
    }
}
