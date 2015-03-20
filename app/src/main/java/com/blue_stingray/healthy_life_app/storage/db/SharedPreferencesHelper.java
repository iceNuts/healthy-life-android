package com.blue_stingray.healthy_life_app.storage.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

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
    private final static String USER_ID = "USER_ID";
    private final static String CURRENT_USER = "CURRENT_USER";
    private final static String NEW_LIFELINE_REQUEST = "NEW_LIFELINE_REQUEST";
    private final String LOCK_KEY;
    private final String USER_EDIT_LOCK = "USER_EDIT_LOCK";
    private final String USER_EDIT_LOCK_TIMER = "USER_EDIT_LOCK_TIMER";
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

    public String getUserPasswd() {
        return prefs.getString(PASSWD_USER_TOKEN, "");
    }

    public void setUserID(String userid) {
        prefs.edit().putString(USER_ID, userid).apply();
    }

    public String getUserID() {
        return prefs.getString(USER_ID, "");
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

    public void setNewLifelineRequest(boolean flag) {
        prefs.edit().putBoolean(NEW_LIFELINE_REQUEST, flag).apply();
    }

    public boolean getNewLifelineRequest() {
        return prefs.getBoolean(NEW_LIFELINE_REQUEST, false);
    }

    public void setUserEditLock(boolean flag) {
        prefs.edit().putBoolean(USER_EDIT_LOCK, flag).apply();
    }

    public boolean getUserEditLock() {
        return prefs.getBoolean(USER_EDIT_LOCK, false);
    }

    public void setUserEditLockTimer() {
        prefs.edit().putString(USER_EDIT_LOCK_TIMER, String.valueOf(new Date().getTime() / 1000));
    }

    public boolean checkUserEditLockTimerExpired() {
        long now = new Date().getTime() / 1000;
        long past = Integer.valueOf(prefs.getString(USER_EDIT_LOCK_TIMER, "0"));
        return now - past >= 5*60;
    }

    public String getDeviceName() {
        return android.os.Build.MODEL;
    }

    public void setCurrentUser(User user) {
        prefs.edit().putString("id", user.id).apply();
        prefs.edit().putString("mentor_id", user.mentor_id).apply();
        prefs.edit().putString("name", user.name).apply();
        prefs.edit().putString("email", user.email).apply();
        prefs.edit().putString("created_at", user.created_at).apply();
        prefs.edit().putString("updated_at", user.updated_at).apply();
        prefs.edit().putString("deleted_at", user.deleted_at).apply();
        prefs.edit().putString("is_admin", user.is_admin).apply();
        prefs.edit().putString("age", user.age).apply();
        prefs.edit().putString("score", user.score).apply();
        prefs.edit().putString("percentage", user.percentage).apply();
        prefs.edit().putString("can_edit", user.can_edit).apply();
        prefs.edit().putInt("rank", user.rank).apply();
        prefs.edit().putBoolean("is_public", user.is_public).apply();
    }


    public User getCurrentUser() {
        if (getUserID().equals("")) {
            return null;
        }
        User user = new User(
                prefs.getString("id", null),
                prefs.getString("mentor_id", null),
                prefs.getString("name", null),
                prefs.getString("email", null),
                prefs.getString("created_at", null),
                prefs.getString("updated_at", null),
                prefs.getString("deleted_at", null),
                prefs.getString("is_admin", null),
                prefs.getString("age", null),
                prefs.getString("score", null),
                prefs.getString("percentage", null),
                prefs.getString("can_edit", null),
                prefs.getInt("rank", -1),
                prefs.getBoolean("is_public", false)
        );
        return user;
    }

}
