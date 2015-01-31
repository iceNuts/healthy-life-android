package com.blue_stingray.healthy_life_app.ui.fragment;

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

    @InjectView(R.id.goBackButton)
    private Button goBackButton;

    @InjectView(R.id.googleAuthButton)
    private SignInButton googleAuthButton;

    private UiLifecycleHelper uiHelper;

    @Inject
    private RestInterface rest;

    @Inject
    public SharedPreferencesHelper prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_social_connect, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.facebookAuthButton);
        authButton.setFragment(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goBackButton.setOnClickListener(new GoBackListener());
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

            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        Toast.makeText(getActivity(), "Hi, " + user.getFirstName() + "!", Toast.LENGTH_LONG).show();
                    }
                }
            });

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
                        }
                        @Override
                        public void onFailure(RetrofitError retrofitError) {
                            Log.d(TAG, "Failed");
                        }
                    });
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private class GoBackListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    private class GoogleAuthListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), GoogleLoginActivity.class));
        }

    }

}
