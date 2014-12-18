package com.blue_stingray.healthy_life_app.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.blue_stingray.healthy_life_app.BuildConfig;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by BillZeng on 12/14/14.
 */

// Singleton

public class GcmHelper {

    private GoogleCloudMessaging gcm;
    private String regid;
    private static GcmHelper instance = null;
    private SharedPreferencesHelper prefs;


    public static synchronized GcmHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GcmHelper();
            instance.prefs = new SharedPreferencesHelper(context);
            instance.gcm = GoogleCloudMessaging.getInstance(context);
        }
        return instance;
    }

    public void getRegistrationId() {
        String registrationId = prefs.getGCMRegId();
        Log.d("GCM", registrationId);
        if (registrationId.isEmpty()) {
            registerInBackground();
        }
    }

    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    instance.regid = gcm.register(BuildConfig.GCM_APP_ID);
                    Log.d("GCM", instance.regid);
                    instance.prefs.setGCMRegId(instance.regid);
                } catch (IOException ex) {
                    msg = "Error : " + ex.getMessage();
                    Log.d("GCM", msg);
                }
                return msg;
            }
        }.execute(null, null, null);
    }

}














