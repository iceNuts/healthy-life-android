package com.blue_stingray.healthy_life_app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import roboguice.RoboGuice;

import javax.inject.Inject;

/**
 * Broadcast Receiver that attaches itself to the local broadcast manager
 */
public abstract class SelfAttachingReceiver extends BroadcastReceiver {

    @Inject
    private LocalBroadcastManager localBroadcastManager;

    public SelfAttachingReceiver(Context context, IntentFilter filter) {
        RoboGuice.getInjector(context.getApplicationContext()).injectMembers(this);
        localBroadcastManager.registerReceiver(this, filter);
    }
}
