package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RestInterfaceProvider;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
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

    @InjectView(R.id.blank_message)
    private LinearLayout blankMessage;

    private DataHelper dataHelper;

    private User user;

    private List<Alert> allAlerts;

    private ProgressDialog loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        dataHelper = DataHelper.getInstance(getActivity());

        loading = ProgressDialog.show(getActivity(), "Alerts", "Loading...");

        if(getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_usage_alerts);

        if(user != null)
        {
            loadUserAlerts();
        }
        else
        {
            loadAuthUserAlerts();
        }
    }

    /**
     * Create the alert list
     */
    public void createList() {
        if(allAlerts.size() > 0) {
            blankMessage.setVisibility(View.GONE);
            AlertListAdapter adapter = new AlertListAdapter(getActivity(), allAlerts);
            alertList.setAdapter(adapter);
        }

        loading.cancel();
    }

    /**
     * Load in alerts from the currently authenticated user
     */
    public void loadAuthUserAlerts() {
        rest.getAlerts(new RetrofitDialogCallback<List<Alert>>(
            getActivity(),
            null
        ) {
            @Override
            public void onSuccess(List<Alert> alerts, Response response) {
                allAlerts = alerts;
                createList();
            }

            @Override
            public void onFailure(RetrofitError error) {
                loading.cancel();
            }
        });
    }

    /**
     * Load in a child users alerts
     */
    public void loadUserAlerts() {
        getActivity().setTitle(getActivity().getTitle() + " - " + user.getName());

        rest.getUserAlerts(
                user.getId(),
                new RetrofitDialogCallback<List<Alert>>(
                        getActivity(),
                        null
                ) {
            @Override
            public void onSuccess(List<Alert> alerts, Response response) {
                allAlerts = alerts;
                createList();
            }

            @Override
            public void onFailure(RetrofitError error) {
                loading.cancel();
            }
        });
    }
}
