package com.blue_stingray.healthy_life_app;

import android.app.Application;
import com.blue_stingray.healthy_life_app.misc.GuiceModule;
import roboguice.RoboGuice;

/**
 * Application initialization
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new GuiceModule());

        // Start all the services on launch
    }
}
