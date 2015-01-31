package com.blue_stingray.healthy_life_app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.blue_stingray.healthy_life_app.model.SessionDevice;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.SocialSessionForm;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.Account;
import com.google.inject.Inject;

import java.io.IOException;

import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.activity.RoboActivity;

/**
 * Created by BillZeng on 1/21/15.
 */
public class GoogleLoginActivity extends RoboActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleLogin";
    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;

    public static final String SCOPES = "https://www.googleapis.com/auth/plus.login";

    private Intent returnIntent;

    @Inject
    private RestInterface rest;

    @Inject
    public SharedPreferencesHelper prefs;

    private boolean mIntentInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        returnIntent = new Intent();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;
            if (mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.disconnect();
            }
            returnIntent.putExtra("google_result", "failed");
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d("Google", "Social Login Connected");
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token = null;
                try {
                    token = GoogleAuthUtil.getToken(
                        GoogleLoginActivity.this,
                        Plus.AccountApi.getAccountName(mGoogleApiClient),
                        "oauth2:"+SCOPES
                    );
                } catch (UserRecoverableAuthException e) {
                    e.printStackTrace();
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {

                Log.d(TAG, "Token Retrieved:" + token);
                returnIntent.putExtra("google_result", "ok");
                returnIntent.putExtra("token", token);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        };
        task.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress && connectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(
                        connectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN,
                        null,
                        0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
}

