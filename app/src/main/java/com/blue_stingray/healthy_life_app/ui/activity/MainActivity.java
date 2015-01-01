package com.blue_stingray.healthy_life_app.ui.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.ui.adapter.DrawerAdapter;
import com.blue_stingray.healthy_life_app.ui.fragment.AlertsFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.LeaderboardFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.LifelineRequestFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ManageGoalsFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ManageUsersFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ProfileFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.SettingsFragment;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.fragment.SplashFragment;
import com.blue_stingray.healthy_life_app.ui.widget.DrawerItem;
import java.util.ArrayList;

/**
 * Main activity for starts
 */
public class MainActivity extends BaseActivity {

    private User authUser;

    private ArrayList drawerItems = new ArrayList<DrawerItem>() {{
        add(new DrawerItem("fa-bullhorn", "Profile"));
        add(new DrawerItem("fa-globe", "Alerts"));
        add(new DrawerItem("fa-flag", "Lifeline Requests"));
        add(new DrawerItem("fa-bar-chart", "Manage Goals"));
        add(new DrawerItem("fa-users", "Manage Users"));
        add(new DrawerItem("fa-trophy", "Leaderboard"));
    }};

    private ArrayList activities = new ArrayList<Class>() {{
        add(ProfileFragment.class);
        add(AlertsFragment.class);
        add(LifelineRequestFragment.class);
        add(ManageGoalsFragment.class);
        add(ManageUsersFragment.class);
        add(LeaderboardFragment.class);
    }};

    private DrawerLayout drawerLayout;

    private SharedPreferences preferences;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("main", 0);
        authUser = ((App) getApplication()).getAuthUser(this);
        if(authUser == null) {
            ViewHelper.unauthorized(this);
            finish();
            return;
        }

        setupDrawer();
        showSplashFragment();
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
            try {
                selectItem(position);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectItem(int position) throws IllegalAccessException, InstantiationException {
        for(int i = 0; i < drawerItems.size(); i++) {
            if(position == i) {
                Class fragmentClass = (Class) activities.get(i);
                ViewHelper.injectFragment((Fragment) fragmentClass.newInstance(), getSupportFragmentManager(), R.id.frame_container);
                break;
            }
        }

        drawerLayout.closeDrawers();
    }

    private void setupDrawer() {
        DrawerAdapter adapter = new DrawerAdapter(this, drawerItems, R.layout.drawer_list_item);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerListView = (ListView) findViewById(R.id.list_slidermenu);
        drawerListView.setAdapter(adapter);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                0,  /* "open drawer" description */
                0  /* "close drawer" description */
        );
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        if(authUser.isAdmin()) {
            activities.remove(4);
            drawerItems.remove(4);
        }
    }

    private void showSplashFragment() {
        boolean firstRun = preferences.getBoolean("firstRun", true);
        if (firstRun) {
            ViewHelper.injectFragment(new SplashFragment(), getSupportFragmentManager(), R.id.frame_container);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstRun", false);
            editor.commit();
        }
    }

}
