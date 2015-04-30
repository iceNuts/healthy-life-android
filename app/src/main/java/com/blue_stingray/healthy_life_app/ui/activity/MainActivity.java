package com.blue_stingray.healthy_life_app.ui.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.ui.adapter.DrawerAdapter;
import com.blue_stingray.healthy_life_app.ui.fragment.AlertsFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.DetailedPhoneUsageFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.LeaderboardFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.LifelineRequestFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ManageGoalsFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ManageMentorFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ManageUsersFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.MentorNotificationFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.ProfileFragment;
import com.blue_stingray.healthy_life_app.ui.fragment.SettingsFragment;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.fragment.SplashFragment;
import com.blue_stingray.healthy_life_app.ui.widget.DrawerItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Main activity for starts
 */
public class MainActivity extends BaseActivity {

    private User authUser;

    private ArrayList drawerItems;

    private DrawerLayout drawerLayout;

    private SharedPreferences preferences;

    private DrawerAdapter adapter;

    private ListView drawerListView;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private boolean viewStatusChanged;

    final private int lifelineIndexMagicNumber = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // test only
//        prefs.setNewLifelineRequest(true);

        preferences = getSharedPreferences("main", 0);
        authUser = ((App) getApplication()).getAuthUser(this);
        if(authUser == null) {
            ViewHelper.unauthorized(this);
            finish();
            return;
        }

        viewStatusChanged = false;

        setupDrawerItems();
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

    @Override
    public void onResume() {
        super.onResume();

        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            ViewHelper.injectFragment(new ProfileFragment(), getSupportFragmentManager(), R.id.frame_container);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
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
                Class fragmentClass = ((DrawerItem) drawerItems.get(i)).className;
                ViewHelper.injectFragment((Fragment) fragmentClass.newInstance(), getSupportFragmentManager(), R.id.frame_container);
                break;
            }
        }
        // lifeline request
        if (position == lifelineIndexMagicNumber) {
            viewStatusChanged = true;
            prefs.setNewLifelineRequest(false);
        }
        drawerLayout.closeDrawers();
    }

    private void setupDrawerItems() {
        drawerItems = new ArrayList<DrawerItem>() {{
            add(new DrawerItem(ProfileFragment.class, "fa-home", "Home"));
            add(new DrawerItem(DetailedPhoneUsageFragment.class, "fa-bar-chart", "Daily Usage"));
            add(new DrawerItem(ManageGoalsFragment.class, "fa-android", "Manage Goals"));
            add(new DrawerItem(LifelineRequestFragment.class, "fa-flag", "Lifeline Requests", true));
            add(new DrawerItem(ManageUsersFragment.class, "fa-users", "Manage Users", true));
        }};
        // check if it is admin
        if (prefs.getUserLevel() == 1) {
            drawerItems.add(new DrawerItem(ManageMentorFragment.class, "fa-gamepad", "Friends", true));
        }
        drawerItems.add(new DrawerItem(LeaderboardFragment.class, "fa-trophy", "Leaderboard"));
        drawerItems.add(new DrawerItem(AlertsFragment.class, "fa-warning", "Alerts"));
        drawerItems.add(new DrawerItem(SettingsFragment.class, "fa-gear", "Settings", true));
        // remove unused options
        if(!authUser.isAdmin()) {

            List<DrawerItem> toRemove = new ArrayList<>();

            for(Object item : drawerItems) {
                DrawerItem drawerItem = (DrawerItem) item;

                if(drawerItem.isAdmin) {
                    toRemove.add(drawerItem);
                }
            }

            drawerItems.removeAll(toRemove);
        }
    }

    private void setupDrawerView() {
        adapter = new DrawerAdapter(this, drawerItems, R.layout.drawer_list_item, lifelineIndexMagicNumber, prefs);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.list_slidermenu);
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        drawerListView.setAdapter(adapter);
    }

    private void setupDrawer() {
        setupDrawerView();
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                0,  /* "open drawer" description */
                0  /* "close drawer" description */
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (viewStatusChanged || prefs.getNewLifelineRequest() == true) {
                    setupDrawerView();
                    viewStatusChanged = false;
                }
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    private void showSplashFragment() {
        boolean firstRun = preferences.getBoolean("firstRun", true);
        try {
            // open daily usage
            if (getIntent().getAction().equals("OPEN_DAILY_USAGE")) {
                showDetailedUsageInfo(1);
                return;
            }
            // open lifeline request
            else if (getIntent().getAction().equals("OPEN_LIFELINE_REQUEST")) {
                ViewHelper.injectFragment(new LifelineRequestFragment(), getSupportFragmentManager(), R.id.frame_container);
                return;
            }
            // open mentor request
            else if (getIntent().getAction().equals("OPEN_MENTOR_REQUEST")) {
                ViewHelper.injectFragment(new MentorNotificationFragment(), getSupportFragmentManager(), R.id.frame_container);
                return;
            }
        } catch (Exception e) {
            // skip
            if (firstRun) {
                ViewHelper.injectFragment(new ProfileFragment(), getSupportFragmentManager(), R.id.frame_container);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("firstRun", false);
                editor.commit();
            }
        }
    }

    private void showDetailedUsageInfo(int dayCount) {
        Bundle bundle = new Bundle();
        bundle.putInt("DayCount", dayCount);
        bundle.putInt("Option", 0);
        DetailedPhoneUsageFragment fragment = new DetailedPhoneUsageFragment();
        fragment.setArguments(bundle);
        ViewHelper.injectFragment(fragment, getSupportFragmentManager(), R.id.frame_container);
    }

}
