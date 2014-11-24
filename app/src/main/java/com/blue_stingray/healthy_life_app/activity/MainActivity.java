package com.blue_stingray.healthy_life_app.activity;

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
import com.blue_stingray.healthy_life_app.fragment.AlertsFragment;
import com.blue_stingray.healthy_life_app.fragment.LifelineRequestFragment;
import com.blue_stingray.healthy_life_app.fragment.ManageGoalsFragment;
import com.blue_stingray.healthy_life_app.fragment.ProfileFragment;
import com.blue_stingray.healthy_life_app.fragment.RatingsFragment;
import com.blue_stingray.healthy_life_app.fragment.SettingsFragment;
import com.blue_stingray.healthy_life_app.misc.FragmentHelper;

/**
 * Main activity for starts
 */
public class MainActivity extends BaseActivity {

    private final String[] drawerItems = new String[] {
            "Profile",
            "Usage Alerts",
            "My Rating",
            "Lifeline Requests",
            "Manage Goals",
            "Settings"
    };
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, drawerItems);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.list_slidermenu);
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
                FragmentHelper.injectFragment(new ProfileFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 1:
                FragmentHelper.injectFragment(new AlertsFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 2:
                FragmentHelper.injectFragment(new RatingsFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 3:
                FragmentHelper.injectFragment(new LifelineRequestFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 4:
                FragmentHelper.injectFragment(new ManageGoalsFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
            case 5:
                FragmentHelper.injectFragment(new SettingsFragment(), getSupportFragmentManager(), R.id.frame_container);
                break;
        }

        drawerLayout.closeDrawers();
    }

}
