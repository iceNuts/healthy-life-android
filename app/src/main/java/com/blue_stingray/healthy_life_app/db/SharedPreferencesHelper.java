package com.blue_stingray.healthy_life_app.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.blue_stingray.healthy_life_app.R;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Provides preference manipulation ability
 */
@Singleton
public class SharedPreferencesHelper {

    private final String LOCK_KEY;
    private final String SESSION_KEY;
    private SharedPreferences prefs;

    @Inject
    public SharedPreferencesHelper(Context context) {
        LOCK_KEY = context.getString(R.string.lock_prefs_key);
        SESSION_KEY = context.getString(R.string.session_prefs_key);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isDeviceLocked() {
        return prefs.getBoolean(LOCK_KEY, false);
    }

    public void setLocked(boolean locked) {
        prefs
                .edit()
                .putBoolean(LOCK_KEY, locked)
                .apply();
    }

    public String getSession() {
        return prefs.getString(SESSION_KEY, null);
    }

    public void setSession(String session) {
        prefs
                .edit()
                .putString(SESSION_KEY, session)
                .apply();
    }
}
