package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.UserListAdapter;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;
import com.blue_stingray.healthy_life_app.ui.widget.LinearList;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class ManageUsersFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.users)
    private ListView userList;

    private List<User> users = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_users, container, false);
        getActivity().setTitle(R.string.title_manage_users);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.manage_users_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_user:
                ViewHelper.injectFragment(new CreateUserFragment(), getFragmentManager(), R.id.frame_container);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createList() {

        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...");

        rest.getMyUsers(new Callback<List<User>>() {
            @Override
            public void success(List<User> usersList, Response response) {
                loading.cancel();

                users = usersList;
                final UserListAdapter adapter = new UserListAdapter(getActivity(), users);
                userList.setAdapter(adapter);
                userList.setOnItemClickListener(new UserListClickListener());
            }

            @Override
            public void failure(RetrofitError error) {
                loading.cancel();
            }
        });
    }

    private class UserListClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User user = users.get(position);
            final String[] options = getResources().getStringArray(R.array.user_selection);
            DialogHelper.createSingleSelectionDialog(getActivity(), user.getName(), R.array.user_selection, new UserSelectionDialogClickListener(options)).show();
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
                case "Remove":
                    Toast.makeText(getActivity(), "TODO", Toast.LENGTH_SHORT).show();
                    break;
            }

            dialog.cancel();
        }

    }

}
