package com.blue_stingray.healthy_life_app.net.form;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by BillZeng on 1/28/15.
 */
public class SocialSessionForm {

    private final String access_token;
    private final DeviceForm device;

    public SocialSessionForm(Context context, CharSequence accessToken, CharSequence gcmId) {
        this.access_token = accessToken.toString();
        this.device = new DeviceForm(context, (String) gcmId);
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
