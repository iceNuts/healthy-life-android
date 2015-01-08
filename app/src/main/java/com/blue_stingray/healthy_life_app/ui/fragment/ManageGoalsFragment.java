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
import android.widget.ListView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Device;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.ui.adapter.AppGoalListAdapter;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.storage.cache.Cache;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

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

    private ProgressDialog loadingDialog;

    private ArrayList<Application> apps;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_goals, container, false);
        getActivity().setTitle(R.string.title_manage_goals);

        if(getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            getActivity().setTitle(getActivity().getTitle() + " - " + user.getName());

            // TODO handle user specfic applications
            rest.getUserDevices(user.getId(), new Callback<List<Device>>() {
                @Override
                public void success(List<Device> devices, Response response) {

                    for(Device device : devices) {
                        rest.getDeviceApps(device.id, new Callback<List<Application>>() {
                            @Override
                            public void success(List<Application> applications, Response response) {}

                            @Override
                            public void failure(RetrofitError error) {}
                        });
                    }

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        } else {
            loadingDialog = ProgressDialog.show(getActivity(), "", "Loading Applications...", true);
            new CreateList().start();
            setHasOptionsMenu(true);
        }

        return view;
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

    private void createList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AppGoalListAdapter adapter = new AppGoalListAdapter(getActivity(), apps);
                appList.setAdapter(adapter);
                appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                });
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
            } finally {
                createList();
                loadingDialog.dismiss();
            }
        }
    }

}
