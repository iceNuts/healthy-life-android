package com.blue_stingray.healthy_life_app.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.UserListAdapter;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;

import java.util.ArrayList;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class ManageUsersFragment extends RoboFragment {

    private final ArrayList<User> users = new ArrayList<User>();

    @InjectView(R.id.users)
    private ListView userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_users, container, false);
        getActivity().setTitle(R.string.title_manage_users);
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

        final UserListAdapter adapter = new UserListAdapter(getActivity(), users);
        userList.setAdapter(adapter);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = users.get(position);
                final String[] options = getResources().getStringArray(R.array.user_selection);

                DialogHelper.createSingleSelectionDialog(getActivity(), user.name, R.array.user_selection, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = options[which];

                        switch(item) {
                            case "Trackable Apps":
                                ViewHelper.injectFragment(new ManageGoalsFragment(), getActivity().getSupportFragmentManager(), R.id.frame_container);
                                break;
                            case "Alerts":
                                ViewHelper.injectFragment(new AlertsFragment(), getActivity().getSupportFragmentManager(), R.id.frame_container);
                                break;
                            case "Usage Statistics":
                                ViewHelper.injectFragment(new UserOverviewFragment(), getActivity().getSupportFragmentManager(), R.id.frame_container);
                                break;
                        }

                        dialog.cancel();
                    }

                }).show();
            }

        });
    }

}
