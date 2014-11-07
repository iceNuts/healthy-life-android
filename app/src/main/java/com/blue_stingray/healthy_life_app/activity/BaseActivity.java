package com.blue_stingray.healthy_life_app.activity;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Bundle;
import com.blue_stingray.healthy_life_app.db.DatabaseHelper;
import com.blue_stingray.healthy_life_app.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.receiver.UninstallBlockingAdminReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

        int gmsStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(gmsStatus != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(gmsStatus)) {
                Dialog gmsDialog = GooglePlayServicesUtil.getErrorDialog(gmsStatus, this, 0);
                gmsDialog.setCancelable(false);
                gmsDialog.show();
            } else {
                GooglePlayServicesUtil.showErrorNotification(gmsStatus, this);
                finish();
            }
        } else if(!isDeviceAdmin() && prefs.isDeviceLocked()) {
            promptForAdmin();
            finish();
        }
    }

    /**
     * Whether the application is registered as a device administrator
     */
    protected boolean isDeviceAdmin() {
        return devicePolicyManager.isAdminActive(adminReceiverName);
    }

    /**
     * Starts an activity to add Healthy App as a device admin
     */
    protected void promptForAdmin() {
        startActivity(UninstallBlockingAdminReceiver.getAddIntent(this));
    }
}