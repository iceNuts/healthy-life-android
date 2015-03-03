package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.adapter.AppGoalListAdapter;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.storage.cache.Cache;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_goals, container, false);
        getActivity().setTitle(R.string.title_manage_goals);

        rest.getUser(
            Integer.valueOf(prefs.getUserID()),
            new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    loadGoalView(user.canEdit());
                }

                @Override
                public void failure(RetrofitError error) {}
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

            rest.getUserDevices(user.getId(), new Callback<List<Device>>() {
                @Override
                public void success(final List<Device> devices, Response response) {

                    if(devices.size() > 0) {

                        for(Device device : devices) {
                            rest.getDeviceApps(device.id, new Callback<List<Application>>() {
                                @Override
                                public void success(List<Application> applications, Response response) {
                                    updateList(applications, devices.size());
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    loadingDialog.dismiss();
                                }
                            });
                        }

                    } else {
                        loadingDialog.dismiss();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    loadingDialog.dismiss();
                }
            });

        } else {
            new CreateList().start();
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.manage_goals_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_goal:
                ViewHelper.injectFragment(new CreateGoalFragment(), getFragmentManager(), R.id.frame_container);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private synchronized void updateList(List<Application> apps, int requestLimit) {
        this.apps.addAll(apps);

        if(++requestsMade == requestLimit) {
            createList();
        }
    }

    private void createList() {
        final AppGoalListAdapter adapter = new AppGoalListAdapter(getActivity(), apps);
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
                final AppGoalListAdapter adapter = new AppGoalListAdapter(getActivity(), apps);
                appList.setAdapter(adapter);
                blankMessage.setVisibility(View.GONE);
                blockMessage.setVisibility(View.GONE);
                appList.setOnItemClickListener(new AuthOnClickListener());
            }
        });
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
            Log.d("App Adapter View", String.valueOf(position));
            Log.d("App Adapter View", String.valueOf(apps.get(position)));

            Application app = apps.get(position);

            if(app.hasGoal()) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                bundle.putSerializable("appinfo", app);

                Fragment fragment = new AppUsageFragment();
                fragment.setArguments(bundle);
                ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
            } else {

                Toast.makeText(getActivity(), "No Goal Set", Toast.LENGTH_LONG).show();
            }

        }

    }

    private class AuthOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Log.d("App Adapter View", String.valueOf(position));
            Log.d("App Adapter View", String.valueOf(apps.get(position)));

            Application app = apps.get(position);

            if(app.hasGoal()) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("appinfo", app);

                Fragment fragment = new AppUsageFragment();
                fragment.setArguments(bundle);
                ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
            } else {

                Bundle bundle = new Bundle();
                bundle.putString("appName", app.getName());

                Fragment fragment = new CreateGoalFragment();
                fragment.setArguments(bundle);

                ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
            }

        }
    }

}
