package com.blue_stingray.healthy_life_app.net;

import android.util.Log;

import com.blue_stingray.healthy_life_app.BuildConfig;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;

/**
 * Builds a RestAdapter for use with Guice
 */
public class RestInterfaceProvider implements Provider<RestInterface> {

    @Inject
    private SessionAddingRequestInterceptor interceptor;

    private RestAdapter adapter;

    private RestInterface service;

    private ExecutorService mExecutorService;

    @Override
    public RestInterface get() {
        mExecutorService = Executors.newCachedThreadPool();
        adapter = new RestAdapter.Builder()
                .setRequestInterceptor(interceptor)
                .setEndpoint(BuildConfig.ENDPOINT_URL)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .setLog(new RestAdapter.Log() {
//                    @Override
//                    public void log(String msg) {
//                        Log.i("healthy", msg);
//                    }
//                })
                .setExecutors(mExecutorService, new MainThreadExecutor())
                .build();
        service = adapter.create(RestInterface.class);
        return service;

    }

}
