package com.blue_stingray.healthy_life_app.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.blue_stingray.healthy_life_app.service.ServiceStarter;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.google.inject.Inject;


public class StartActivity extends BaseActivity {

    @Inject
    private ServiceStarter starter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start services in case being run for the first time
        starter.startServices();
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
