package com.blue_stingray.healthy_life_app.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.misc.Dialogs;
import com.blue_stingray.healthy_life_app.misc.FormValidationManager;
import com.blue_stingray.healthy_life_app.misc.ValidationRule;
import com.blue_stingray.healthy_life_app.model.Session;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.net.form.SessionForm;
import com.google.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectView;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.passwordField)
    private EditText passwordField;

    @InjectView(R.id.emailField)
    private EditText emailField;

    @InjectView(R.id.registerButton)
    private Button loginButton;

    @InjectView(R.id.create_admin)
    private TextView registerButton;

    @Inject
    private RestInterface rest;

    private FormValidationManager validationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Workaround for monospace passwords and need to support ICS
        passwordField.setTypeface(Typeface.DEFAULT);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
            }
        });

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
            rest.createSession(new SessionForm(emailField.getText(), passwordField.getText()), new RetrofitDialogCallback<Session>(LoginActivity.this, progressDialog) {
                @Override
                public void onSuccess(Session session, Response response) {

                }

                @Override
                public void onFailure(RetrofitError retrofitError) {
                    Dialogs.newDismissiveDialog(LoginActivity.this, R.string.incorrect_credentials_title, R.string.incorrect_credentials_descritpion).show();
                }
            });
        }
    }

}
