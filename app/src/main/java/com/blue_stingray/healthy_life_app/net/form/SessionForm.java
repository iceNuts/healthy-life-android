package com.blue_stingray.healthy_life_app.net.form;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Form for logging in
 */
public class SessionForm {

    private final String email;
    private final String password;
    private final DeviceForm device;

    public SessionForm(Context context, CharSequence email, CharSequence password) {
        this.email = email.toString();
        this.password = password.toString();
        this.device = new DeviceForm(context);
    }

    public static final class DeviceForm {
        private final String device_id;
        private final String device_name;

        public DeviceForm(Context context) {
            this.device_id =  Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            this.device_name = Build.MANUFACTURER + " " + Build.MODEL;
        }
    }
}
