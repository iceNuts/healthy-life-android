package com.blue_stingray.healthy_life_app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.vendor.guice.GuiceModule;
import com.blue_stingray.healthy_life_app.storage.cache.Cache;

import roboguice.RoboGuice;

/**
 * Application initialization
 */
public class App extends Application {

    /**
     * Authenticated user
     */
    private User authUser;

    /**
     * Cache for applications on the users phone
     */
    public Cache<String, com.blue_stingray.healthy_life_app.model.Application> appCache;

    private SharedPreferencesHelper prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        appCache = new Cache<>();
        RoboGuice.setUseAnnotationDatabases(false);
        RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(this), new GuiceModule());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /**
     * Get the currently logged in user
     * @return User
     */
    public User getAuthUser(Activity context) {
        prefs = new SharedPreferencesHelper(this);
        // load auth user
        authUser = prefs.getCurrentUser();
        return authUser;
    }

    /**
     * Set the currently logged in user
     * @param user User
     */
    public void setAuthUser(User user) {
        Log.d("USER", user.toString());
        // store user
        prefs = new SharedPreferencesHelper(this);
        prefs.setCurrentUser(user);
        authUser = user;
    }


}
