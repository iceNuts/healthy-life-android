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
import com.blue_stingray.healthy_life_app.ui.adapter.AppGoalListAdapter;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.storage.cache.Cache;

import java.util.ArrayList;
import android.util.Log;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Provides a list of user install applications.
 */
public class ManageGoalsFragment extends RoboFragment {

    @InjectView(R.id.apps)
    private ListView appList;

    private ProgressDialog loadingDialog;
    private ArrayList<Application> apps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_goals, container, false);
        getActivity().setTitle(R.string.title_manage_goals);
        loadingDialog = ProgressDialog.show(getActivity(), "", "Loading Applications...", true);
        new CreateList().start();
        setHasOptionsMenu(true);
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

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("appinfo", app);

                        Fragment fragment = new AppUsageFragment();
                        fragment.setArguments(bundle);
                        ViewHelper.injectFragment(fragment, getFragmentManager(), R.id.frame_container);
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
