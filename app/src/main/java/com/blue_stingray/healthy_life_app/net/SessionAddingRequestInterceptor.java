package com.blue_stingray.healthy_life_app.net;

import android.content.SharedPreferences;
import retrofit.RequestInterceptor;

//TODO
public class SessionAddingRequestInterceptor implements RequestInterceptor {

    private String requestToken;

    @Override
    public void intercept(RequestFacade requestFacade) {
        //SharedPreferences
        requestFacade.addHeader("Authorization", "HL");
    }
}
