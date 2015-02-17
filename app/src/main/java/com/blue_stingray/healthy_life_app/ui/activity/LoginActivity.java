package com.blue_stingray.healthy_life_app.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.form.validation.ValidationRule;
import com.blue_stingray.healthy_life_app.model.SessionDevice;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.net.form.SessionForm;
import com.blue_stingray.healthy_life_app.util.Time;
import com.google.inject.Inject;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectView;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Activity for user login
 */
public class LoginActivity extends BaseActivity {

    @InjectView(R.id.passwordField)
    private EditText passwordField;

    @InjectView(R.id.emailField)
    private EditText emailField;

    @InjectView(R.id.login)
    private Button loginButton;

    @Inject
    private RestInterface rest;

    private FormValidationManager validationManager;

    private ProgressDialog progressDialog;

    private DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.dataHelper = DataHelper.getInstance(this);

        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            if(extras.get("toast") != null) {
                Toast.makeText(this, extras.get("toast").toString(), Toast.LENGTH_LONG).show();
            }
        }

        //Workaround for monospace passwords and need to support ICS
        passwordField.setTypeface(Typeface.DEFAULT);

        validationManager = new FormValidationManager();
        validationManager.addField(emailField, ValidationRule.newEmailValidationRule(this));
        validationManager.addField(passwordField, ValidationRule.newPasswordValidationRule(this));

        loginButton.setOnClickListener(new LoginButtonListener());

        SharedPreferences preferences = getSharedPreferences("main", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstRun", true);
        editor.commit();

//        // set field empty on tap
//        emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    emailField.setHint("");
//                }
//                else {
//                    emailField.setHint("email");
//                }
//            }
//        });
//
//        passwordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    passwordField.setHint("");
//                }
//                else {
//                    passwordField.setHint("password");
//                }
//            }
//        });
    }


    private class LoginButtonListener extends FormSubmitClickListener {

        public LoginButtonListener() {
            super(LoginActivity.this, validationManager, R.string.logging_in);
        }

        @Override
        protected void submit() {
            ProgressDialog.show(LoginActivity.this, "", "Logging in...");
            rest.createSession(
                    new SessionForm(
                            LoginActivity.this,
                            emailField.getText(),
                            passwordField.getText(),
                            prefs.getGCMRegId()),
                    new Callback<SessionDevice>() {
                @Override
                public void success(SessionDevice sessionDevice, Response response) {
                    prefs.setDeviceId(sessionDevice.device.id);
                    prefs.setSession(sessionDevice.session.token);
                    prefs.setState(SharedPreferencesHelper.State.LOGGED_IN);
                    prefs.setUserLevel(sessionDevice.is_admin);
                    prefs.setUserPasswdToken(passwordField.getText().toString());
                    prefs.setUserID(sessionDevice.session.user_id);
                    getAuthUser();
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    DialogHelper.createDismissiveDialog(LoginActivity.this, R.string.incorrect_credentials_title, R.string.incorrect_credentials_description).show();
                }
            });
        }
    }

    private void getAuthUser() {
        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...");
        // get the auth user
        rest.getMyUser(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                ((App) getApplication()).setAuthUser(user);

                // sync my goals
                rest.getMyGoals(new Callback<List<Goal>>() {
                    @Override
                    public void success(List<Goal> goals, Response response) {
                        for(Goal goal : goals) {
                            // TO FIX
                            // data type error when login in with Brian account
                            // 0.2 ? int type
                            HashMap<Integer, Integer> newGoalMap = new HashMap<>();
                            newGoalMap.put(Time.dayTranslate(goal.getDay()), goal.getGoalTime());
                            dataHelper.createNewGoal(goal.getApp().getPackageName(), newGoalMap);
                        }
                        progressDialog.cancel();
                        startActivity(new Intent(LoginActivity.this, StartActivity.class));
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("healthy", "Login /goal error");
                        Log.i("healthy", error.getCause().toString());
                        progressDialog.cancel();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("healthy", "Login /user/me error");
                Log.i("healthy", error.getCause().toString());
            }
        });
    }

    public void showRegister(View v) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

    public void showSocialConnect(View v) {
        startActivityForResult(
                new Intent(LoginActivity.this, SocialConnectActivity.class),
                1
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String fbReturnStatus = data.getStringExtra("fb_result");
                String googleReturnStatus = data.getStringExtra("google_result");
                if (fbReturnStatus != null && fbReturnStatus.equals("ok") ) {
                    getAuthUser();
                }
                else if (googleReturnStatus != null && googleReturnStatus.equals("ok")) {
                    getAuthUser();
                }
                else {
                    // show failed
                    DialogHelper.createDismissiveDialog(LoginActivity.this, R.string.incorrect_credentials_title, R.string.incorrect_credentials_description).show();
                }
            }
        }
    }
}
