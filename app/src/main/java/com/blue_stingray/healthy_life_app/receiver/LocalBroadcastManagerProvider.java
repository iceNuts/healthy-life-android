package com.blue_stingray.healthy_life_app.receiver;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provides a local broadcast manager
 */
public class LocalBroadcastManagerProvider implements Provider<LocalBroadcastManager> {
    private Context context;

    /**
     * Create a new LocalBroadcastManagerProvider
     *
     * @param context a context
     */
    @Inject
    public LocalBroadcastManagerProvider(final Context context) {
        this.context = context;
    }

    @Override
    public LocalBroadcastManager get() {
        return LocalBroadcastManager.getInstance(context);
    }
}
