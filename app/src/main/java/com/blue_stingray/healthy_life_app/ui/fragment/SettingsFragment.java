package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RestInterfaceProvider;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.activity.BaseActivity;
import com.blue_stingray.healthy_life_app.ui.activity.LoginActivity;
import com.blue_stingray.healthy_life_app.ui.activity.RegistrationActivity;
import com.blue_stingray.healthy_life_app.ui.activity.StartActivity;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;
import com.blue_stingray.healthy_life_app.ui.fragment.support.v4.PreferenceFragment;
import com.google.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Provides the authenticated users setting information.
 */
public class SettingsFragment extends PreferenceFragment {

    @Inject
    private RestInterface rest;

    private DataHelper dataHelper;

    @Inject
    public SharedPreferencesHelper prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.title_user_settings);
        addPreferencesFromResource(R.xml.main_prefs);
        dataHelper = DataHelper.getInstance(getActivity());

        findPreference("logout").setOnPreferenceClickListener(new OnLogoutListener());
    }

    private class OnLogoutListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Settings", "Logging out...");

            rest.destroySession(new Callback<Object>() {
                @Override
                public void success(Object o, Response response) {
                    dataHelper.removeGoals();
                    prefs.setUserID("");
                    ((BaseActivity) getActivity()).prefs.setState(SharedPreferencesHelper.State.NONE);
                    startActivity(new Intent(getActivity(), StartActivity.class));
                    loading.dismiss();
                }

                @Override
                public void failure(RetrofitError error) {
                    loading.dismiss();

                    DialogHelper.createServerErrorDialog(getActivity()).show();
                }
            });

            return true;
        }
    }

}
