package com.blue_stingray.healthy_life_app.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.blue_stingray.healthy_life_app.R;

/**
 * Main activity for starts
 */
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
