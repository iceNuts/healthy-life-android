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
import com.blue_stingray.healthy_life_app.ui.widget.LinearList;

import java.util.ArrayList;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class ManageUsersFragment extends RoboFragment {

    private final ArrayList<User> users = new ArrayList<User>();

    @InjectView(R.id.users)
    private LinearList userList;

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
        users.add(new User("Walter White"));
        users.add(new User("Dexter Morgan"));
        users.add(new User("Sherlock Holmes"));
        users.add(new User("Eric Cartman"));
        users.add(new User("Barney Stinson"));
        users.add(new User("Dean Winchester"));
        users.add(new User("John Locke"));

        final UserListAdapter adapter = new UserListAdapter(getActivity(), users);
        userList.setAdapter(adapter, new UserListClickListener());
    }

    private class UserListClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            User user = users.get((Integer) v.getTag());
            final String[] options = getResources().getStringArray(R.array.user_selection);
            DialogHelper.createSingleSelectionDialog(getActivity(), user.name, R.array.user_selection, new UserSelectionDialogClickListener(options)).show();
        }

    }

    private class UserSelectionDialogClickListener implements DialogInterface.OnClickListener {

        private String[] options;

        public UserSelectionDialogClickListener(String[] options) {
            this.options = options;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String item = options[which];

            switch (item) {
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

    }

}
