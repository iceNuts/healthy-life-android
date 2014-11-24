package com.blue_stingray.healthy_life_app.ui.fragment;

import android.os.Bundle;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.ui.fragment.support.v4.PreferenceFragment;

/**
 * Provides the authenticated users setting information.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Settings");
        addPreferencesFromResource(R.xml.main_prefs);
    }
}
