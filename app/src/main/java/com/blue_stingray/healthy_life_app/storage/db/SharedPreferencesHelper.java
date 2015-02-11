package com.blue_stingray.healthy_life_app.storage.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.blue_stingray.healthy_life_app.R;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides preference manipulation ability
 */
@Singleton
public class SharedPreferencesHelper {

    private final static String SESSION_KEY = "session";
    private final static String STATE_KEY = "state";
    private final static String GCM_REGID = "GCM_REGID";
    private final static String USER_LEVEL = "IS_ADMIN";
    private final static String DEVICE_ID = "DEVICE_ID";
    private final static String MEN_NOTI_STATUS = "MEN_NOTI_STATUS";
    private final static String PASSWD_USER_TOKEN = "PASSWD_USER_TOKEN";
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

    public String getGCMRegId() { return prefs.getString(GCM_REGID, "");}

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

    public void setUserLevel(int userLevel) {
        prefs.edit().putInt(USER_LEVEL, userLevel).apply();
    }

    public int getUserLevel() {
        return prefs.getInt(USER_LEVEL, 0);
    }

    public State getState() {
        int current = prefs.getInt(STATE_KEY, 0);
        return State.from(current);
    }

    public void setDeviceId(int id) {
        prefs.edit().putInt(DEVICE_ID, id).apply();
    }

    public int getDeviceId() {
        return prefs.getInt(DEVICE_ID, 0);
    }

    public void setMentorNotificationStatus(boolean status) {prefs.edit().putBoolean(MEN_NOTI_STATUS, status).apply();}

    public boolean getMentorNotificationStatus() {return prefs.getBoolean(MEN_NOTI_STATUS, false);}

    public void setUserPasswdToken(String passwdToken) {
        prefs.edit().putString(PASSWD_USER_TOKEN, md5(passwdToken)).apply();
    }

    public boolean verifyUserPasswdToken(String passwdToken) {
        String token = prefs.getString(PASSWD_USER_TOKEN, "");
        if (token.equals(md5(passwdToken))) {
            return true;
        }
        return false;
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
