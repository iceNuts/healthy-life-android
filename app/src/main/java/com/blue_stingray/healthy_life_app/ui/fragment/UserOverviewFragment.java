package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.ui.adapter.UserListAdapter;
import com.blue_stingray.healthy_life_app.ui.widget.LinearList;

import java.util.ArrayList;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class UserOverviewFragment extends RoboFragment {

    private final ArrayList<Application> lockedApps = new ArrayList<>();

    @InjectView(R.id.locked)
    private LinearList lockedList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_overview, container, false);
        getActivity().setTitle(R.string.title_user_overview);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createList();
    }

    public void createList() {

        // create dummy list
//        lockedApps.add(null);
//        lockedApps.add(null);
//        lockedApps.add(null);


//        final UserListAdapter adapter = new UserListAdapter(getActivity(), users, R.layout.user_list_row_simple);
//        userList.setAdapter(adapter);
    }

}
