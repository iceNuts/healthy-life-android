package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.AppGoalListAdapter;
import com.blue_stingray.healthy_life_app.ui.widget.LinearList;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class UserOverviewFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.locked)
    private LinearList lockedList;

    @InjectView(R.id.locked_apps_text)
    private TextView lockedAppsText;

    private View view;

    ArrayList<Application> apps = new ArrayList<>();

    private User authUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_overview, container, false);

        apps = Application.createFromUserApplications(getActivity());

        authUser = ((App) getActivity().getApplication()).getAuthUser(getActivity());
        if(authUser == null) {
            ViewHelper.unauthorized(getActivity());
            return null;
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Overview - " + authUser.getName());
        createLockedList();
    }

    private void createLockedList() {
        rest.getUserLockedApps(authUser.getId(), new Callback<List<Application>>() {
            @Override
            public void success(List<Application> apps, Response response) {
                AppGoalListAdapter adapter = new AppGoalListAdapter(getActivity(), apps);
                lockedList.setAdapter(adapter);
                lockedAppsText.setText(String.valueOf(apps.size()));
            }

            @Override
            public void failure(RetrofitError error) {}
        });
    }

}
