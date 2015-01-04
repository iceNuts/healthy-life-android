package com.blue_stingray.healthy_life_app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.activity.BaseActivity;
import com.blue_stingray.healthy_life_app.ui.activity.LoginActivity;
import com.blue_stingray.healthy_life_app.ui.activity.RegistrationActivity;
import com.blue_stingray.healthy_life_app.ui.activity.StartActivity;
import com.blue_stingray.healthy_life_app.ui.fragment.support.v4.PreferenceFragment;

/**
 * Provides the authenticated users setting information.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_user_settings);
        addPreferencesFromResource(R.xml.main_prefs);

        findPreference("logout").setOnPreferenceClickListener(new OnLogoutListener());
    }

    private class OnLogoutListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            ((BaseActivity) getActivity()).prefs.setState(SharedPreferencesHelper.State.NONE);
            startActivity(new Intent(getActivity(), StartActivity.class));
            return true;
        }
    }

}
