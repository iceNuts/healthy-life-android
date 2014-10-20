package com.blue_stingray.healthy_life_app.net;

import android.content.SharedPreferences;
import com.blue_stingray.healthy_life_app.db.SharedPreferencesHelper;
import com.google.inject.Inject;
import retrofit.RequestInterceptor;

/**
 * Adds session token to requests when logged in
 */
public class SessionAddingRequestInterceptor implements RequestInterceptor {

    private SharedPreferencesHelper prefs;

    @Inject
    public SessionAddingRequestInterceptor(SharedPreferencesHelper prefs) {
        this.prefs = prefs;
    }

    @Override
    public void intercept(RequestFacade requestFacade) {
        if (prefs.isLoggedIn()) {
            requestFacade.addHeader("Authorization", "HL " + prefs.getSession());
        }
    }
}
