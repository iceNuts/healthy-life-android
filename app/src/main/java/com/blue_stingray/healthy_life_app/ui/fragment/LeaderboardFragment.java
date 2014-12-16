package com.blue_stingray.healthy_life_app.ui.fragment;

import android.content.DialogInterface;
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

public class LeaderboardFragment extends RoboFragment {

    private final ArrayList<User> users = new ArrayList<User>();

    @InjectView(R.id.users)
    private LinearList userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container,false);
        getActivity().setTitle(R.string.title_leaderboard);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createList();
    }

    public void createList() {

        // create dummy list
        users.add(new User("Dustin Sholtes"));
        users.add(new User("Brian Rehg"));
        users.add(new User("Rhys Murray"));
        users.add(new User("Walter White"));
        users.add(new User("Dexter Morgan"));
        users.add(new User("Sherlock Holmes"));
        users.add(new User("Eric Cartman"));
        users.add(new User("Barney Stinson"));
        users.add(new User("Dean Winchester"));
        users.add(new User("John Locke"));

        final UserListAdapter adapter = new UserListAdapter(getActivity(), users, R.layout.user_list_row_simple);
        userList.setAdapter(adapter);
    }

}
