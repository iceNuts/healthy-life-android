package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RestInterfaceProvider;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
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
        findPreference("update user profile").setOnPreferenceClickListener(new OnUpdateUserListener());
        findPreference("update user passwd").setOnPreferenceClickListener(new OnChangePasswordListener());
    }

    private class OnLogoutListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "Settings", "Logging out...");

            rest.destroySession(new RetrofitDialogCallback<Object>(
                    getActivity(),
                    loading
            ) {
                @Override
                public void onSuccess(Object o, Response response) {
                    dataHelper.removeGoals();
                    prefs.setUserID("");
                    prefs.setUserEditLock(false);
                    prefs.setUserPasswdToken("");
                    ((BaseActivity) getActivity()).prefs.setState(SharedPreferencesHelper.State.NONE);
                    startActivity(new Intent(getActivity(), StartActivity.class));
                }

                @Override
                public void onFailure(RetrofitError error) {

                    DialogHelper.createServerErrorDialog(getActivity()).show();
                }
            });

            return true;
        }
    }

    private class OnUpdateUserListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            ViewHelper.injectFragment(new UpdateUserFragment(), getFragmentManager(), R.id.frame_container);
            return true;
        }
    }

    private class OnChangePasswordListener implements Preference.OnPreferenceClickListener {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            // show alert
            if (prefs.getUserPasswd().equals(prefs.md5(""))) {
                DialogHelper.createDismissiveDialog(
                        getActivity(),
                        R.string.we_are_sorry,
                        R.string.no_password_option).show();
                return true;
            }
            final Dialog authDialog = new Dialog(getActivity());
            authDialog.setTitle("Old Password");
            authDialog.setContentView(R.layout.password_alert_dialog);
            final EditText passwdTextView = (EditText) authDialog.findViewById(R.id.passwordField);
            passwdTextView.setTextColor(Color.BLACK);
            passwdTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        if (prefs.verifyUserPasswdToken(passwdTextView.getText().toString())) {
                            authDialog.cancel();
                            ViewHelper.injectFragment(new ChangePasswordFragment(), getFragmentManager(), R.id.frame_container);
                        }
                        // show wrong password
                        else {
                            passwdTextView.setError("Password is wrong");
                        }
                        return true;
                    }
                    return false;
                }
            });
            authDialog.show();
            return true;
        }
    }
}
