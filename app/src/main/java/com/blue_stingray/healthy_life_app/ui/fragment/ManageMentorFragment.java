package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
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

/**
 * Created by BillZeng on 2/8/15.
 */
public class ManageMentorFragment extends RoboFragment {

    @Inject
    public SharedPreferencesHelper prefs;

    @Inject
    public RestInterface rest;

    @InjectView(R.id.create_mentor_button)
    private Button createMentorButton;

    @InjectView(R.id.mentors)
    private LinearList userList;

    @InjectView(R.id.blank_message)
    private View blankMessage;

    @InjectView(R.id.textView13)
    private TextView blankTextView;

    private BroadcastReceiver memNotificationReceiver;
    private Menu menu;
    private List<User> mentors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_mentor, container, false);
        getActivity().setTitle(R.string.title_manage_mentor);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mentors = new ArrayList<>();
        memNotificationReceiver = new MemNotificationReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                memNotificationReceiver,
                new IntentFilter("mem_notification"));
        createMentorButton.setOnClickListener(new CreateMentorListener());
        createList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.manage_mentor_fragment_actions, menu);
        this.menu = menu;
        showNotificationStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_mentor_notification:
                prefs.setMentorNotificationStatus(false);
                ViewHelper.injectFragment(new MentorNotificationFragment(), getFragmentManager(), R.id.frame_container);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createList() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...");
        rest.getMyUser(
            new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    try {
                        // has mentor
                        Integer mentorId = user.getMentorId();
                        createMentorButton.setVisibility(View.GONE);
                        // Get User information
                        rest.getUser(
                            mentorId,
                            new Callback<User>() {
                                @Override
                                public void success(User user, Response response) {
                                    loading.cancel();
                                    // parsing user info
                                    mentors.add(user);
                                    userList.setAdapter(new UserListAdapter(getActivity(), mentors), new MentorListClickListener());
                                    blankMessage.setVisibility(View.GONE);
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    loading.cancel();
                                    // show bad request page
                                    blankTextView.setText(R.string.network_error);
                                    blankMessage.setVisibility(View.VISIBLE);
                                }
                            }
                        );
                    }
                    catch (Exception e) {
                        // there is no mentor
                        // show bad request page
                        loading.cancel();
                        createMentorButton.setVisibility(View.VISIBLE);
                        blankTextView.setText(R.string.no_mentor);
                        blankMessage.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    loading.cancel();
                    blankTextView.setText(R.string.network_error);
                    createMentorButton.setVisibility(View.GONE);
                    blankMessage.setVisibility(View.VISIBLE);
                }
            }
        );
    }

    private void showNotificationStatus() {
        if (false == prefs.getMentorNotificationStatus()) {
            menu.getItem(menu.size()-1).setIcon(getResources().getDrawable(R.drawable.ic_arrow_forward_white));
        }
        // there is a notification
        else {
            menu.getItem(menu.size()-1).setIcon(getResources().getDrawable(R.drawable.ic_arrow_forward_white_dot));
        }
    }

    private class CreateMentorListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ViewHelper.injectFragment(new CreateMentorFragment(), getFragmentManager(), R.id.frame_container);
        }
    }

    private class MemNotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            prefs.setMentorNotificationStatus(true);
            showNotificationStatus();
        }
    }

    private class MentorListClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            User mentor = mentors.get((int) v.getTag());
            final String[] options = getResources().getStringArray(R.array.mentor_selection);
            DialogHelper.createSingleSelectionDialog(
                getActivity(),
                mentor.getName(),
                R.array.mentor_selection,
                new MentorSelectionDialogClickListener(mentor, options)
            ).show();
        }
    }

    private class MentorSelectionDialogClickListener implements DialogInterface.OnClickListener {

        private User user;

        private String[] options;

        public MentorSelectionDialogClickListener(User user, String[] options) {
            this.user = user;
            this.options = options;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String item = options[which];

            // remove mentor
            if (item.equals(options[0])) {
                final AlertDialog choiceDialog = DialogHelper.createYesNoDialog(getActivity(), "Are you sure?", "Yes", "No",
                        new DialogInterface.OnClickListener() {

                            // do removing mentor
                            @Override
                            public void onClick(final DialogInterface choiceDialog, int which) {
                                rest.removeMentor(
                                    new Callback() {
                                        @Override
                                        public void success(Object o, Response response) {
                                            choiceDialog.cancel();
                                            Toast.makeText(
                                                getActivity(),
                                                R.string.mentor_removed_success,
                                                Toast.LENGTH_SHORT
                                            ).show();
                                            // show no mentor page
                                            // show button
                                            createMentorButton.setVisibility(View.VISIBLE);
                                            blankMessage.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            choiceDialog.cancel();
                                            Toast.makeText(
                                                getActivity(),
                                                R.string.mentor_removed_failure,
                                                Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }
                                );
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

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(memNotificationReceiver);
        super.onDestroyView();
    }
}
