package com.blue_stingray.healthy_life_app.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.blue_stingray.healthy_life_app.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.receiver.AdminRemovalDetectionReceiver;
import com.blue_stingray.healthy_life_app.receiver.SampleAppChangeReceiver;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Handles managing background services and recievers
 */
@Singleton
public class ServiceStarter {

    @Inject private SharedPreferencesHelper prefs;
    private final Context applicationContext;
    private boolean started;

    /**
     * Construct a new Service Starter
     *
     * @param context any context
     */
    @Inject
    public ServiceStarter(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    /**
     * Start all services and receivers. Will not start services multiple times if called more than once
     */
    public void startServices() {
        synchronized (applicationContext) {
            if (started || !prefs.isDeviceLocked()) {
                return;
            }

            applicationContext.startService(new Intent(applicationContext, ApplicationDetectionService.class));
            new SampleAppChangeReceiver(applicationContext);
            new AdminRemovalDetectionReceiver(applicationContext);

            started = true;
            Log.i(getClass().getSimpleName(), "Services Started");
        }
    }
}
