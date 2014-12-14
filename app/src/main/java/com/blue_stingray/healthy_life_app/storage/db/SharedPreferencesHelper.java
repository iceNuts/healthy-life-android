package com.blue_stingray.healthy_life_app.storage.db;

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

    private final static String SESSION_KEY = "session";
    private final static String STATE_KEY = "state";
    private final static String GCM_REGID = "GCM_REGID";
    private final String LOCK_KEY;
    private SharedPreferences prefs;

    public static enum State {
        NONE(0),
        LOGGED_IN(1),
        READY(2);

        public final int val;

        State(int val) {
            this.val = val;
        }

        public static State from(int val) {
            switch (val) {
                case 0:
                    return NONE;
                case 1:
                    return LOGGED_IN;
                case 2:
                    return READY;
                default:
                    throw new IllegalArgumentException("invalid val");
            }
        }
    }

    @Inject
    public SharedPreferencesHelper(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        LOCK_KEY = context.getString(R.string.lock_prefs_key);
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

    public boolean isLoggedIn() {
        return getSession() != null;
    }

    public String getSession() {
        return prefs.getString(SESSION_KEY, null);
    }

    public void setGCMRegId(String regId) {
        prefs.edit().putString(GCM_REGID, regId).apply();
    }

    public String getGCMRegId() { return prefs.getString(GCM_REGID, null);}

    public void setSession(String session) {
        if (session == null) {
            throw  new NullPointerException("session must not be null");
        }
        prefs
                .edit()
                .putString(SESSION_KEY, session)
                .apply();
    }

    public void setState(State state) {
        if (state == null) {
            throw new NullPointerException("state may not be null");
        }
        prefs
                .edit()
                .putInt(STATE_KEY, state.val)
                .apply();
    }

    public State getState() {
        int current = prefs.getInt(STATE_KEY, 0);
        return State.from(current);
    }
}
