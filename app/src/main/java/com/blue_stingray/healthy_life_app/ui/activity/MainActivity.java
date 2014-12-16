package com.blue_stingray.healthy_life_app.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.ui.adapter.DrawerAdapter;
import com.blue_stingray.healthy_life_app.ui.fragment.AlertsFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.LeaderboardFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.LifelineRequestFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ManageGoalsFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ManageUsersFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ProfileFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.SettingsFragment;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.blue_stingray.healthy_life_app.ui.fragment.SplashFragment;
import com.blue_stingray.healthy_life_app.ui.widget.DrawerItem;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main activity for starts
 */
public class MainActivity extends BaseActivity {

    private final DrawerItem[] drawerItems = new DrawerItem[] {
            new DrawerItem("fa-bullhorn", "Profile"),
            new DrawerItem("fa-globe", "Alerts"),
            new DrawerItem("fa-flag", "Lifeline Requests"),
            new DrawerItem("fa-bar-chart", "Manage Goals"),
            new DrawerItem("fa-users", "Manage Users"),
            new DrawerItem("fa-trophy", "Leaderboard"),
            new DrawerItem("fa-gear", "Settings")
    };
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerAdapter adapter = new DrawerAdapter(this, Arrays.asList(drawerItems), R.layout.drawer_list_item);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerListView = (ListView) findViewById(R.id.list_slidermenu);
        drawerListView.setAdapter(adapter);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                null,  /* nav drawer icon to replace 'Up' caret */
                0,  /* "open drawer" description */
                0  /* "close drawer" description */
        );

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        ViewHelper.injectFragment(new SplashFragment(), getSupportFragmentManager(), R.id.frame_container);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        switch(position) {
            case 0:
                ViewHelper.injectFragment(new ProfileFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 1:
                ViewHelper.injectFragment(new AlertsFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 2:
                ViewHelper.injectFragment(new LifelineRequestFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 3:
                ViewHelper.injectFragment(new ManageGoalsFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 4:
                ViewHelper.injectFragment(new ManageUsersFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 5:
                ViewHelper.injectFragment(new LeaderboardFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 6:
                ViewHelper.injectFragment(new SettingsFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
        }

        drawerLayout.closeDrawers();
    }

}
