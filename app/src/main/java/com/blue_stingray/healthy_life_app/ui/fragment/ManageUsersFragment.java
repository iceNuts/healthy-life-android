package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.Objects;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class ManageUsersFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.users)
    private LinearList userList;

    @InjectView(R.id.create_user_button)
    private Button createUserButton;

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
        createUserButton.setOnClickListener(new CreateUserListener());
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
                userList.setAdapter(new UserListAdapter(getActivity(), users), new UserListClickListener());
            }

            @Override
            public void failure(RetrofitError error) {
                loading.cancel();
            }
        });
    }

    private class UserListClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            User user = users.get((int) v.getTag());
            final String[] options = getResources().getStringArray(R.array.user_selection);
            DialogHelper.createSingleSelectionDialog(getActivity(), user.getName(), R.array.user_selection, new UserSelectionDialogClickListener(user, options)).show();
        }
    }

    private class UserSelectionDialogClickListener implements DialogInterface.OnClickListener {

        private User user;

        private String[] options;

        public UserSelectionDialogClickListener(User user, String[] options) {
            this.user = user;
            this.options = options;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String item = options[which];

            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);

            switch (item) {
                case "Trackable Apps":

                    Fragment manageGoalsFragment = new ManageGoalsFragment();
                    manageGoalsFragment.setArguments(bundle);

                    ViewHelper.injectFragment(manageGoalsFragment, getActivity().getSupportFragmentManager(), R.id.frame_container);
                    break;
                case "Alerts":

                    Fragment alertsFragment = new AlertsFragment();
                    alertsFragment.setArguments(bundle);

                    ViewHelper.injectFragment(alertsFragment, getActivity().getSupportFragmentManager(), R.id.frame_container);
                    break;
                case "Usage Statistics":
                    ViewHelper.injectFragment(new UserOverviewFragment(), getActivity().getSupportFragmentManager(), R.id.frame_container);
                    break;
                case "Edit":

                    Fragment editUserFragment = new EditUserFragment();
                    editUserFragment.setArguments(bundle);

                    ViewHelper.injectFragment(editUserFragment, getActivity().getSupportFragmentManager(), R.id.frame_container);
                    break;
                case "Remove":
                    final AlertDialog choiceDialog = DialogHelper.createYesNoDialog(getActivity(), "Are you sure?", "Yes", "No",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface choiceDialog, int which) {
                                rest.destroyUser(user.getId(), new Callback<Object>() {
                                    @Override
                                    public void success(Object o, Response response) {
                                        ViewHelper.injectFragment(new ManageUsersFragment(), getActivity().getSupportFragmentManager(), R.id.frame_container);
                                        choiceDialog.cancel();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {}
                                });
                            }
                        },
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface choiceDialog, int which) {
                                choiceDialog.cancel();
                            }
                        });

                    dialog.cancel();
                    choiceDialog.show();

                    break;
            }

            dialog.cancel();
        }

    }

    private class CreateUserListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ViewHelper.injectFragment(new CreateUserFragment(), getFragmentManager(), R.id.frame_container);
        }

    }

}
