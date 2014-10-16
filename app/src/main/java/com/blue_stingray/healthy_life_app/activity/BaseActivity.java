package com.blue_stingray.healthy_life_app.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.db.DatabaseHelper;
import com.blue_stingray.healthy_life_app.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.receiver.UninstallBlockingAdminReceiver;
import com.google.inject.Inject;
import roboguice.activity.RoboActivity;

/**
 * Base activity providing common functionality used by all other activities
 */
public abstract class BaseActivity extends RoboActivity {
    protected final String LOG_TAG = getClass().getSimpleName();
    @Inject
    protected DatabaseHelper databaseHelper;
    @Inject
    protected SharedPreferencesHelper prefs;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminReceiverName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        adminReceiverName = new ComponentName(this, UninstallBlockingAdminReceiver.class);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isAdmin() && prefs.isDeviceLocked()) {
            startActivity(UninstallBlockingAdminReceiver.getAddIntent(this));
        }
    }

    /**
     * Whether the application is registered as a device administrator
     */
    private boolean isAdmin() {
        return devicePolicyManager.isAdminActive(adminReceiverName);
    }
}
