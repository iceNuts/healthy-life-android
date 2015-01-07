package com.blue_stingray.healthy_life_app.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.form.validation.ValidationRule;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.net.form.UserForm;
import com.google.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.inject.InjectView;

/**
 * Activity for user registration
 */
public class RegistrationActivity extends BaseActivity {

    @InjectView(R.id.passwordField)
    private EditText passwordField;

    @InjectView(R.id.confirmPasswordField)
    private EditText confirmPasswordField;

    @InjectView(R.id.emailField)
    private EditText emailField;

    @InjectView(R.id.nameField)
    private EditText nameField;

    @InjectView(R.id.register)
    private Button registerButton;

    @Inject
    private RestInterface rest;

    private FormValidationManager validationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Workaround for monospace passwords and need to support ICS
        passwordField.setTypeface(Typeface.DEFAULT);
        confirmPasswordField.setTypeface(Typeface.DEFAULT);

        validationManager = new FormValidationManager();
        validationManager.addField(nameField, ValidationRule.requiredValidationRule(this, R.string.missing_name));
        validationManager.addField(emailField, ValidationRule.newEmailValidationRule(this));
        validationManager.addField(passwordField, ValidationRule.newPasswordValidationRule(this));
        validationManager.addField(confirmPasswordField, ValidationRule.newConfirmPasswordValidationRule(this, passwordField), passwordField);

        registerButton.setOnClickListener(new RegisterButtonListener());
    }


    private class RegisterButtonListener extends FormSubmitClickListener {

        public RegisterButtonListener() {
            super(RegistrationActivity.this, validationManager, R.string.creating_user);
        }

        @Override
        protected void submit() {
            rest.createUser(new UserForm(nameField.getText().toString(), emailField.getText().toString(), passwordField.getText().toString()), new RetrofitDialogCallback<User>(RegistrationActivity.this, progressDialog) {
                @Override
                public void onSuccess(User user, Response response) {
                    AlertDialog successDialog = DialogHelper.createDismissiveDialog(RegistrationActivity.this, R.string.registration_success_title, R.string.registration_success_description);
                    successDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    successDialog.show();

                }

                @Override
                public void onFailure(RetrofitError retrofitError) {
                    emailField.setError(getString(R.string.email_in_use));
                }
            });
        }
    }

    public void showLogin(View v) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }

}
