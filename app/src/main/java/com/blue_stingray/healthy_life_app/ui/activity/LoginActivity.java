package com.blue_stingray.healthy_life_app.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.form.validation.ValidationRule;
import com.blue_stingray.healthy_life_app.model.SessionDevice;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.net.form.SessionForm;
import com.google.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectView;

import java.math.BigInteger;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Workaround for monospace passwords and need to support ICS
        passwordField.setTypeface(Typeface.DEFAULT);

        validationManager = new FormValidationManager();
        validationManager.addField(emailField, ValidationRule.newEmailValidationRule(this));
        validationManager.addField(passwordField, ValidationRule.newPasswordValidationRule(this));

        loginButton.setOnClickListener(new LoginButtonListener());
    }


    private class LoginButtonListener extends FormSubmitClickListener {

        public LoginButtonListener() {
            super(LoginActivity.this, validationManager, R.string.logging_in);
        }

        @Override
        protected void submit() {
            rest.createSession(
                    new SessionForm(
                            LoginActivity.this,
                            emailField.getText(),
                            passwordField.getText(),
                            prefs.getGCMRegId()),
                    new RetrofitDialogCallback<SessionDevice>(
                            LoginActivity.this,
                            progressDialog) {
                @Override
                public void onSuccess(SessionDevice sessionDevice, Response response) {
                    prefs.setSession(sessionDevice.session.token);
                    prefs.setState(SharedPreferencesHelper.State.LOGGED_IN);
                    startActivity(new Intent(LoginActivity.this, StartActivity.class));
                    finish();
                }

                @Override
                public void onFailure(RetrofitError retrofitError) {
                    DialogHelper.createDismissiveDialog(LoginActivity.this, R.string.incorrect_credentials_title, R.string.incorrect_credentials_description).show();
                }
            });
        }
    }

    public void showRegister(View v) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }

}
