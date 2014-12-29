package com.blue_stingray.healthy_life_app.vendor.guice;

import android.support.v4.content.LocalBroadcastManager;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RestInterfaceProvider;
import com.blue_stingray.healthy_life_app.receiver.LocalBroadcastManagerProvider;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * Module to define Guice dependencies
 */
public class GuiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(LocalBroadcastManager.class).toProvider(LocalBroadcastManagerProvider.class).in(Singleton.class);
        binder.bind(RestInterface.class).toProvider(RestInterfaceProvider.class).in(Singleton.class);
    }
}
