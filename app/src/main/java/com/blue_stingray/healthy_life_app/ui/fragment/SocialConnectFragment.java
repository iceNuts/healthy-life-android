package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.SessionDevice;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.SocialSessionForm;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.activity.GoogleLoginActivity;
import com.blue_stingray.healthy_life_app.ui.activity.LoginActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.inject.Inject;

import retrofit.RetrofitError;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class SocialConnectFragment extends RoboFragment {

    private static final String TAG = "SocialConnectFragment";

    @InjectView(R.id.googleAuthButton)
    private SignInButton googleAuthButton;

    private UiLifecycleHelper uiHelper;

    @Inject
    private RestInterface rest;

    private Intent returnIntent;

    @Inject
    public SharedPreferencesHelper prefs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        returnIntent = new Intent();
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
        callFacebookLogout(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_social_connect, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.facebookAuthButton);
        authButton.setReadPermissions("email");
        authButton.setFragment(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        googleAuthButton.setOnClickListener(new GoogleAuthListener());
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // for google
        if (requestCode == 1) {

            if (resultCode == getActivity().RESULT_OK) {
                String token = data.getStringExtra("token");
                rest.googleLogin(
                        new SocialSessionForm(
                                getActivity(),
                                token,
                                prefs.getGCMRegId()
                        ),
                        new RetrofitDialogCallback<SessionDevice>(
                                getActivity(),
                                null
                        ) {
                            @Override
                            public void onSuccess(SessionDevice sessionDevice, retrofit.client.Response response) {
                                Log.d(TAG, "Logged In");
                                returnIntent.putExtra("google_result", "ok");
                                prefs.setDeviceId(sessionDevice.device.id);
                                prefs.setSession(sessionDevice.session.token);
                                prefs.setState(SharedPreferencesHelper.State.LOGGED_IN);
                                prefs.setUserLevel(sessionDevice.is_admin);
                                getActivity().setResult(Activity.RESULT_OK, returnIntent);
                                getActivity().finish();
                            }

                            @Override
                            public void onFailure(RetrofitError retrofitError) {
                                Log.d(TAG, "Failed");
                                returnIntent.putExtra("google_result", "failed");
                                getActivity().setResult(Activity.RESULT_OK, returnIntent);
                            }
                        }
                );
            }
        }
        // for fb
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            rest.facebookLogin(
                    new SocialSessionForm(
                            getActivity(),
                            session.getAccessToken(),
                            prefs.getGCMRegId()),
                    new RetrofitDialogCallback<SessionDevice>(
                            getActivity(),
                            null) {
                        @Override
                        public void onSuccess(SessionDevice sessionDevice, retrofit.client.Response response) {
                            Log.d(TAG, "Logged In");
                            returnIntent.putExtra("fb_result", "ok");
                            prefs.setDeviceId(sessionDevice.device.id);
                            prefs.setSession(sessionDevice.session.token);
                            prefs.setState(SharedPreferencesHelper.State.LOGGED_IN);
                            prefs.setUserLevel(sessionDevice.is_admin);
                            getActivity().setResult(Activity.RESULT_OK, returnIntent);
                            getActivity().finish();
                        }
                        @Override
                        public void onFailure(RetrofitError retrofitError) {
                            Log.d(TAG, "Failed");
                            returnIntent.putExtra("fb_result", "failed");
                            getActivity().setResult(Activity.RESULT_OK, returnIntent);
                        }
                    });
        } else if (state.isClosed()) {
        }
    }

    private static void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {

            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
                //clear your preferences if saved
            }
        } else {

            session = new Session(context);
            Session.setActiveSession(session);

            session.closeAndClearTokenInformation();
            //clear your preferences if saved

        }

    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private class GoogleAuthListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            startActivityForResult(
                    new Intent(getActivity(), GoogleLoginActivity.class),
                    1
            );
        }

    }

}
