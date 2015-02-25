package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.form.UserForm;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
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

    @Inject
    private SharedPreferencesHelper prefs;

    private int lockGoalFlag;

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
            final User user = users.get((int) v.getTag());
            final Dialog authDialog = new Dialog(getActivity());
            authDialog.setTitle("Authorization");
            authDialog.setContentView(R.layout.password_alert_dialog);
            final EditText passwdTextView = (EditText) authDialog.findViewById(R.id.passwordField);
            passwdTextView.setTextColor(Color.BLACK);
            passwdTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        if (prefs.verifyUserPasswdToken(passwdTextView.getText().toString())) {
                            authDialog.cancel();
                            lockGoalFlag = -1;
                            rest.getUser(
                                user.getId(),
                                new Callback<User>() {
                                    @Override
                                    public void success(User user, Response response) {
                                        if (user.canEdit()) {
                                            lockGoalFlag = 1;
                                            final String[] options = getResources().getStringArray(R.array.user_lock_selection);
                                            DialogHelper.createSingleSelectionDialog(getActivity(), user.getName(), R.array.user_lock_selection, new UserSelectionDialogClickListener(user, options)).show();
                                        }
                                        else {
                                            lockGoalFlag = 0;
                                            final String[] options = getResources().getStringArray(R.array.user_unlock_selection);
                                            DialogHelper.createSingleSelectionDialog(getActivity(), user.getName(), R.array.user_unlock_selection, new UserSelectionDialogClickListener(user, options)).show();
                                        }
                                    }
                                    @Override
                                    public void failure(RetrofitError error) {
                                        lockGoalFlag = -1;
                                        final String[] options = getResources().getStringArray(R.array.user_unavailable_selection);
                                        DialogHelper.createSingleSelectionDialog(getActivity(), user.getName(), R.array.user_unavailable_selection, new UserSelectionDialogClickListener(user, options)).show();
                                    }
                                }
                            );
                        }
                        // show wrong password
                        else {
                            passwdTextView.setError("Password is wrong");
                        }
                        return true;
                    }
                    return false;
                }
            });
            authDialog.show();
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

            // Edit Goals
            if(item.equals(options[0]))
            {
                Fragment manageGoalsFragment = new ManageGoalsFragment();
                manageGoalsFragment.setArguments(bundle);
                ViewHelper.injectFragment(manageGoalsFragment, getActivity().getSupportFragmentManager(), R.id.frame_container);
            }
            // Unlock/Lock Goals
            else if(item.equals(options[1]))
            {
                // Do nothing
                if (lockGoalFlag == -1) {

                }
                // unlock goal
                else if (lockGoalFlag == 0) {
                    final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "unlocking...");
                    rest.updateUser(
                            this.user.getId(),
                            new UserForm(
                                    1
                            ),
                            new Callback<User>() {
                                @Override
                                public void success(User user, Response response) {
                                    loading.cancel();
                                    Toast.makeText(getActivity(), "Successful", Toast.LENGTH_LONG);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    loading.cancel();
                                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG);
                                }
                            }
                    );
                }
                // lock goal
                else if (lockGoalFlag == 1) {
                    final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "locking...");
                    rest.updateUser(
                        this.user.getId(),
                        new UserForm(
                            0
                        ),
                        new Callback<User>() {
                            @Override
                            public void success(User user, Response response) {
                                loading.cancel();
                                Toast.makeText(getActivity(), "Successful", Toast.LENGTH_LONG);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                loading.cancel();
                                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG);
                            }
                        }
                    );
                }
            }
            // Alerts
            else if(item.equals(options[2]))
            {
                Fragment alertsFragment = new AlertsFragment();
                alertsFragment.setArguments(bundle);
                ViewHelper.injectFragment(alertsFragment, getActivity().getSupportFragmentManager(), R.id.frame_container);

            }
            // Usage
            else if(item.equals(options[3]))
            {
                Fragment userOverviewFragment = new UserOverviewFragment();
                userOverviewFragment.setArguments(bundle);
                ViewHelper.injectFragment(userOverviewFragment, getActivity().getSupportFragmentManager(), R.id.frame_container);
            }
            // Edit User
            else if(item.equals(options[4]))
            {
                Fragment editUserFragment = new EditUserFragment();
                editUserFragment.setArguments(bundle);
                ViewHelper.injectFragment(editUserFragment, getActivity().getSupportFragmentManager(), R.id.frame_container);
            }
            // Remove User
            else if(item.equals(options[5]))
            {
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
