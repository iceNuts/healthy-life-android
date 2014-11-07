package com.blue_stingray.healthy_life_app.net.form;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.math.BigInteger;
import java.util.Random;

/**
 * Form for logging in
 */
public class SessionForm {

    private final String email;
    private final String password;
    private final DeviceForm device;

    public SessionForm(Context context, CharSequence email, CharSequence password, String gcmId) {
        this.email = email.toString();
        this.password = password.toString();
        this.device = new DeviceForm(context, gcmId);
    }

    public static final class DeviceForm {
        private final String android_id;
        private final String name;
        private final String gcm_id;

        public DeviceForm(Context context, String gcmId) {
            this.android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            this.name = Build.MANUFACTURER + " " + Build.MODEL;
            this.gcm_id = gcmId;
        }
    }
}
