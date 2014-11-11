package com.blue_stingray.healthy_life_app.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.blue_stingray.healthy_life_app.R;

import java.util.ArrayList;
import java.util.List;

public class ManageGoalsFragment extends Fragment {

    private ProgressDialog loadingDialog;
    private ListView appList;
    private ArrayList<String> apps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_goals, container,false);
        appList = (ListView) view.findViewById(R.id.apps);
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
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_create_goal:
                Fragment fragment = new CreateGoalFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, apps);
                appList.setAdapter(adapter);
            }
        });
    }

    private class CreateList extends Thread {
        @Override
        public void run() {
            try {
                final PackageManager pm = getActivity().getPackageManager();
                apps = new ArrayList<>();
                List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                for (ApplicationInfo packageInfo : packages) {
                    apps.add(packageInfo.loadLabel(pm).toString());
                }
            } finally {
                createList();
                loadingDialog.dismiss();
            }
        }
    }

}
