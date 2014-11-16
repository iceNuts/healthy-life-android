package com.blue_stingray.healthy_life_app.receiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.blue_stingray.healthy_life_app.activity.AdminRemovalActivity;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.misc.Intents;
import com.google.inject.Inject;

/**
 * Receives change notifications and will detect when a user attempts to remove the application as a device admin
 */
public class AdminRemovalDetectionReceiver extends SelfAttachingReceiver {

    @Inject
    private SharedPreferencesHelper prefs;

    public AdminRemovalDetectionReceiver(Context context) {
        super(context, buildIntentFilter());
    }

    private static IntentFilter buildIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intents.Monitor.ACTIVITY_CHANGE);
        filter.addAction(Intents.Monitor.APP_CHANGE);
        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO Verify it works correctly when going directly from settings
        ComponentName component = intent.getParcelableExtra(Intents.Monitor.Extra.COMPONENT_NAME);
        if(prefs.isDeviceLocked() && "com.android.settings.Settings$DeviceAdminSettingsActivity".equals(component.getClassName())) {
            Intent launchIntent = new Intent(context, AdminRemovalActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }
    }
}
