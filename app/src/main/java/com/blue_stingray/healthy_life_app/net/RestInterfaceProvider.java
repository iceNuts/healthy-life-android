package com.blue_stingray.healthy_life_app.net;

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
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build()
                .create(RestInterface.class);
    }
}
