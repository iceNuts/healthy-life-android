package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.AppGoalListAdapter;
import com.blue_stingray.healthy_life_app.ui.adapter.AppPercentListAdapter;
import com.blue_stingray.healthy_life_app.ui.widget.LinearList;
import com.google.inject.Inject;
import java.util.ArrayList;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class UserOverviewFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.most_used)
    private LinearList mostUsedList;

    @InjectView(R.id.locked)
    private LinearList lockedList;

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
        createMostUsedList();
        createLockedList();
    }

    private void createMostUsedList() {
        AppPercentListAdapter adapter = new AppPercentListAdapter(getActivity(), apps.subList(0, 3), R.layout.app_list_row_simple);
        mostUsedList.setAdapter(adapter);
    }

    private void createLockedList() {
        AppGoalListAdapter adapter = new AppGoalListAdapter(getActivity(), apps.subList(0, 6));
        lockedList.setAdapter(adapter);
    }

}
