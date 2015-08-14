package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Device;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.DeleteGoalForm;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.AppGoalListAdapter;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.storage.cache.Cache;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.util.Log;
import android.widget.Toast;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Provides a list of user install applications.
 */
public class ManageGoalsFragment extends RoboFragment {

    @Inject
    private RestInterface rest;

    @InjectView(R.id.apps)
    private ListView appList;

    @InjectView(R.id.blank_message)
    private LinearLayout blankMessage;

    @InjectView(R.id.block_message)
    private LinearLayout blockMessage;

    @Inject
    private SharedPreferencesHelper prefs;

    private ProgressDialog loadingDialog;

    private ArrayList<Application> apps;

    private User user;

    private int requestsMade;

    private DataHelper dataHelper;

    private AppGoalListAdapter adapter;

    private boolean canEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_goals, container, false);
        getActivity().setTitle(R.string.title_manage_goals);
        dataHelper = DataHelper.getInstance(getActivity());

        rest.getUser(
            Integer.valueOf(prefs.getUserID()),
            new RetrofitDialogCallback<User>(
                    getActivity(),
                    null
            ) {
                @Override
                public void onSuccess(User user, Response response) {
                    canEdit = user.canEdit();
                    loadGoalView(user.canEdit());
                }

                @Override
                public void onFailure(RetrofitError error) {}
            }
        );

        return view;
    }

    private void loadGoalView(boolean canEditFlag) {

        // user itself or child goals
        if (getArguments() == null) {
            if (!canEditFlag) {
                blankMessage.setVisibility(View.GONE);
                return;
            }
        }

        loadingDialog = ProgressDialog.show(getActivity(), "", "Loading Applications...", true);
        requestsMade = 0;
        apps = new ArrayList<>();

        if(getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            getActivity().setTitle(getActivity().getTitle() + " - " + user.getName());

            rest.getUserDevices(user.getId(), new RetrofitDialogCallback<List<Device>>(
                    getActivity(),
                    null
            ) {
                @Override
                public void onSuccess(final List<Device> devices, Response response) {

                    if(devices.size() > 0) {

                        for(final Device device : devices) {
                            rest.getDeviceApps(
                                    device.id,
                                    new RetrofitDialogCallback<List<Application>>(
                                            getActivity(),
                                            loadingDialog
                                    ) {
                                @Override
                                public void onSuccess(List<Application> applications, Response response) {
                                    for (Application app : applications) {
                                        app.setDeviceID(String.valueOf(device.id));
                                        app.setDeviceName(device.name);
                                    }
                                    updateList(applications, devices.size());
                                }

                                @Override
                                public void onFailure(RetrofitError error) {
                                }
                            });
                        }

                    } else {
                        loadingDialog.dismiss();
                    }

                }

                @Override
                public void onFailure(RetrofitError error) {
                    loadingDialog.dismiss();
                }
            });

        } else {
            new CreateList().start();
        }
    }

    private synchronized void updateList(List<Application> apps, int requestLimit) {
        this.apps.addAll(apps);

        if(++requestsMade == requestLimit) {
            createList();
        }
    }

    private void createList() {
        filterApps();
        adapter = new AppGoalListAdapter(getActivity(), apps);
        appList.setAdapter(adapter);
        appList.setOnItemClickListener(new ChildOnClickListener());
        blankMessage.setVisibility(View.GONE);
        blockMessage.setVisibility(View.GONE);
        loadingDialog.dismiss();
    }

    private void createAuthList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                filterApps();
                adapter = new AppGoalListAdapter(getActivity(), apps);
                appList.setAdapter(adapter);
                blankMessage.setVisibility(View.GONE);
                blockMessage.setVisibility(View.GONE);
                appList.setOnItemClickListener(new AuthOnClickListener());
            }
        });
    }

    private void filterApps() {
        // remove healthy app
        for (Application app : apps) {
            if (app.getPackageName().equals(getActivity().getPackageName())) {
                apps.remove(app);
                break;
            }
        }
    }

    private class CreateList extends Thread {
        @Override
        public void run() {
            try {
                Cache<String, Application> appCache = ((App) getActivity().getApplication()).appCache;

                if(appCache.size() > 0) {
                    apps = new ArrayList<Application>(appCache.snapshot().values());
                } else {
                    apps = Application.createFromUserApplications(getActivity());

                    // populate app cache
                    for(Application app : apps) {
                        app.setDeviceName("Current Device");
                        app.setDeviceID(String.valueOf(prefs.getDeviceId()));
                        appCache.put(app.getName(), app);
                    }
                }

                // applications that have goals first
                Collections.sort(apps, new Comparator<Application>() {
                    @Override
                    public int compare(Application lhs, Application rhs) {
                        return Boolean.compare(lhs.hasGoal(), rhs.hasGoal());
                    }
                });
            } finally {
                createAuthList();
                loadingDialog.dismiss();
            }
        }
    }


    private class ChildOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            Application app = apps.get(position);

            if(app.hasGoal()) {

                DialogHelper.createSingleSelectionDialog(getActivity(), app.getName(), R.array.manage_goal_long_click, new ManageGoalDialogClickListener(app)).show();

            } else {

                Toast.makeText(getActivity(), "No Goal Set", Toast.LENGTH_LONG).show();
            }

        }

    }

    private class ManageGoalDialogClickListener implements DialogInterface.OnClickListener {

        private Application app;

        public ManageGoalDialogClickListener(Application app) {
            this.app = app;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // App Usage
            if (which == 0) {
                Bundle bundle = new Bundle();
                if (user == null) {
                    user = prefs.getCurrentUser();
                }
                bundle.putSerializable("user", user);
                bundle.putSerializable("appinfo", app);
                Fragment fragment = new AppUsageFragment();
                fragment.setArguments(bundle);
                ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
            }
            // update goal
            else if (which == 1) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("appinfo", app);
                if (user == null) {
                    user = prefs.getCurrentUser();
                }
                bundle.putSerializable("user", user);
                bundle.putString("userID", String.valueOf(user.getId()));
                Fragment fragment = new EditGoalFragment();
                fragment.setArguments(bundle);
                ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
            }
            // delete goal
            else if (which == 2) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                removeUserGoal(
                                        app.getDeviceID(),
                                        app.getPackageName()
                                );
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure?")
                        .setPositiveButton("YES", dialogClickListener)
                        .setNegativeButton("NO", dialogClickListener)
                        .show();
            }
        }
    }

    private void removeUserGoal(final String _deviceID, final String _packageName) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Deleting...");
        rest.removeGoal(
                new DeleteGoalForm(
                    _deviceID,
                    _packageName
                ),
                new RetrofitDialogCallback<Object>(
                    getActivity(),
                    loading
                ){
                    @Override
                    public void onSuccess(Object o, Response response) {
                        if (user == null || user.getId() == prefs.getCurrentUser().getId()) {
                            if (user == null) {
                                user = prefs.getCurrentUser();
                            }
                            dataHelper.removeGoal(
                                    String.valueOf(user.getId()),
                                    _packageName
                            );
                        }
                        redrawFragment();
                        Toast.makeText(getActivity(), "Delete Successfully", Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onFailure(RetrofitError retrofitError) {
                        Toast.makeText(getActivity(), "Delete Failed", Toast.LENGTH_LONG);
                    }
                });
    }

    private class AuthOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            Application app = apps.get( position );

            if(app.hasGoal()) {

                DialogHelper.createSingleSelectionDialog(getActivity(), app.getName(), R.array.manage_goal_long_click, new ManageGoalDialogClickListener(app)).show();

            } else {

                Bundle bundle = new Bundle();
                if (user == null) {
                    user = prefs.getCurrentUser();
                }
                bundle.putString("userID", String.valueOf(user.getId()));
                bundle.putString("appName", app.getName());

                Fragment fragment = new CreateGoalFragment();
                fragment.setArguments(bundle);

                ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
            }

        }
    }

    private void redrawFragment() {
        Fragment frg = null;
        frg = getFragmentManager().findFragmentById(R.id.frame_container);
        final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

}
