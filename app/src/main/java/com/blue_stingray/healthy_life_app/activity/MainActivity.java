package com.blue_stingray.healthy_life_app.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.db.DatabaseHelper;
import com.blue_stingray.healthy_life_app.service.ServiceStarter;
import com.google.inject.Inject;


public class MainActivity extends BaseActivity {

    @Inject
    private ServiceStarter starter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start services in case being run for the first time
        starter.startServices();
        if (!prefs.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        setContentView(R.layout.activity_main);
    }

    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_prefs);
        }
    }

}
