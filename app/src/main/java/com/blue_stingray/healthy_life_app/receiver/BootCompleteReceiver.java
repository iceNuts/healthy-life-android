package com.blue_stingray.healthy_life_app.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.blue_stingray.healthy_life_app.service.ServiceStarter;
import roboguice.receiver.RoboBroadcastReceiver;

import javax.inject.Inject;

/**
 * Starts services on phone start
 */
public class BootCompleteReceiver extends RoboBroadcastReceiver {

    @Inject
    private ServiceStarter starter;

    @Override
    protected void handleReceive(Context context, Intent intent) {
        super.handleReceive(context, intent);

        Log.d(getClass().getSimpleName(), "Observed Boot Completed");
        starter.startServices();
    }
}
