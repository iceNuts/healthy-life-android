package com.blue_stingray.healthy_life_app.net;

import com.blue_stingray.healthy_life_app.BuildConfig;
import com.google.inject.Provider;
import retrofit.RestAdapter;

import java.net.HttpURLConnection;

/**
 * Builds a RestAdapter for use with Guice
 */
public class RestInterfaceProvider implements Provider<RestInterface> {

    @Override
    public RestInterface get() {
        return new RestAdapter.Builder()
                .setRequestInterceptor(new SessionAddingRequestInterceptor())
                .setEndpoint(BuildConfig.ENDPOINT_URL)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build()
                .create(RestInterface.class);
    }
}
