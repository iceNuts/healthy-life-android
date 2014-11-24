package com.blue_stingray.healthy_life_app.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.blue_stingray.healthy_life_app.R;

/**
 * Device Admin Receiver that functionally does very little apart from stopping force quitting and uninstallation
 */
public class UninstallBlockingAdminReceiver extends DeviceAdminReceiver {

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.device_admin_disable);
    }

    public static Intent getAddIntent(Context context) {
        ComponentName name = new ComponentName(context, UninstallBlockingAdminReceiver.class);
        Intent addAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        addAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, name);
        addAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, context.getString(R.string.device_admin_request));
        return addAdminIntent;
    }
}
