package com.blue_stingray.healthy_life_app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blue_stingray.healthy_life_app.net.GcmHelper;
import com.blue_stingray.healthy_life_app.service.ServiceStarter;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.google.inject.Inject;


public class StartActivity extends BaseActivity {

    @Inject
    private ServiceStarter starter;
    private GcmHelper gcmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gcmHelper = GcmHelper.getInstance(this);
        gcmHelper.getRegistrationId();
        // Start services in case being run for the first time
        starter.startServices();

// TEST only
//        prefs.setState(SharedPreferencesHelper.State.NONE);
        switch (prefs.getState()) {
            case NONE:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case LOGGED_IN:
                startActivity(new Intent(this, PostLoginActivity.class));
                break;
            case READY:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

        finish();
    }

}
