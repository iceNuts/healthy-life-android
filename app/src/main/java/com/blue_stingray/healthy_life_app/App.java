package com.blue_stingray.healthy_life_app;

import android.app.Application;

import com.blue_stingray.healthy_life_app.vendor.guice.GuiceModule;
import com.blue_stingray.healthy_life_app.storage.cache.Cache;

import roboguice.RoboGuice;

/**
 * Application initialization
 */
public class App extends Application {

    public Cache<String, com.blue_stingray.healthy_life_app.model.Application> appCache;

    @Override
    public void onCreate() {
        super.onCreate();
        appCache = new Cache<>();
        RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(this), new GuiceModule());
    }

}
