package com.blue_stingray.healthy_life_app.net;

import android.util.Log;

import com.blue_stingray.healthy_life_app.BuildConfig;
import com.google.inject.Inject;
import com.google.inject.Provider;
import retrofit.RestAdapter;

/**
 * Builds a RestAdapter for use with Guice
 */
public class RestInterfaceProvider implements Provider<RestInterface> {

    @Inject
    private SessionAddingRequestInterceptor interceptor;

    @Override
    public RestInterface get() {
        return new RestAdapter.Builder()
                .setRequestInterceptor(interceptor)
                .setEndpoint(BuildConfig.ENDPOINT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String msg) {
                        Log.i("healthy", msg);
                    }
                })
                .build()
                .create(RestInterface.class);
    }
}
