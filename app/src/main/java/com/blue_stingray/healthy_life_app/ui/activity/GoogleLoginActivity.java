package com.blue_stingray.healthy_life_app.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.blue_stingray.healthy_life_app.model.SessionDevice;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.SocialSessionForm;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.Account;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.inject.Inject;

import java.io.IOException;

import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.activity.RoboActivity;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by BillZeng on 1/21/15.
 */
public class GoogleLoginActivity extends RoboActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GoogleLogin";
    private static final int RC_SIGN_IN = 0;
    private static final int RC_REATUH = 10;

    private GoogleApiClient mGoogleApiClient;

    public static final String SCOPES = "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/plus.profile.emails.read https://www.googleapis.com/auth/plus.me";
    private Intent returnIntent;

    @Inject
    private RestInterface rest;

    @Inject
    public SharedPreferencesHelper prefs;

    private boolean mIntentInProgress;
    private boolean mSignInClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        returnIntent = new Intent();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(new Scope("https://www.googleapis.com/auth/plus.profile.emails.read"))
                .addScope(new Scope("https://www.googleapis.com/auth/plus.me"))
                .build();
        mSignInClicked = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
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
                        SCOPES
                    );
                    GoogleAuthUtil.invalidateToken(GoogleLoginActivity.this, token);
                    token = GoogleAuthUtil.getToken(
                            GoogleLoginActivity.this,
                            Plus.AccountApi.getAccountName(mGoogleApiClient),
                            SCOPES
                    );
                } catch (final GooglePlayServicesAvailabilityException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int statusCode = ((GooglePlayServicesAvailabilityException)e)
                                    .getConnectionStatusCode();
                            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                                    GoogleLoginActivity.this,
                                    100);
                            dialog.show();
                        }
                    });
                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), RC_SIGN_IN);
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mSignInClicked = false;
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                if (token != null) {
                    returnIntent.putExtra("google_result", "ok");
                    returnIntent.putExtra("token", token);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        };
        task.execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    private void resolveSignInError(ConnectionResult connectionResult) {
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIntentInProgress && connectionResult.hasResolution()) {
            if (mSignInClicked) {
                resolveSignInError(connectionResult);
            }
            else {
                finish();
            }
        }
    }

}

