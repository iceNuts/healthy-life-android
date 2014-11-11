package com.blue_stingray.healthy_life_app.fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.blue_stingray.healthy_life_app.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_prefs);
    }
}
