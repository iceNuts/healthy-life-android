package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.net.form.UserForm;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.form.validation.ValidationRule;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.activity.MainActivity;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;
import com.google.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class CreateUserFragment extends RoboFragment {

    @InjectView(R.id.email)
    private EditText emailField;

    @InjectView(R.id.password)
    private EditText passwordField;

    @InjectView(R.id.confirm)
    private EditText confirmField;

    @InjectView(R.id.name)
    private EditText nameField;

    @InjectView(R.id.create)
    private Button createButton;

    @Inject
    private RestInterface rest;

    private FormValidationManager validationManager;

    private User authUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_user, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authUser = ((App) getActivity().getApplication()).getAuthUser(getActivity());
        if(authUser == null) {
            ViewHelper.unauthorized(getActivity());
        }

        //Workaround for monospace passwords and need to support ICS
        passwordField.setTypeface(Typeface.DEFAULT);
        confirmField.setTypeface(Typeface.DEFAULT);

        validationManager = new FormValidationManager();
        validationManager.addField(nameField, ValidationRule.requiredValidationRule(getActivity(), R.string.missing_name));
        validationManager.addField(emailField, ValidationRule.newEmailValidationRule(getActivity()));
        validationManager.addField(passwordField, ValidationRule.newPasswordValidationRule(getActivity()));
        validationManager.addField(confirmField, ValidationRule.newConfirmPasswordValidationRule(getActivity(), passwordField), passwordField);

        createButton.setOnClickListener(new RegisterButtonListener());
    }

    private class RegisterButtonListener extends FormSubmitClickListener {

        public RegisterButtonListener() {
            super(getActivity(), validationManager, R.string.creating_user);
        }

        @Override
        protected void submit() {
            rest.createUser(new UserForm(nameField.getText().toString(), emailField.getText().toString(), passwordField.getText().toString(), 0, authUser.getId(), null), new RetrofitDialogCallback<User>(getActivity(), progressDialog) {
                @Override
                public void onSuccess(User user, Response response) {
                    AlertDialog successDialog = DialogHelper.createDismissiveDialog(getActivity(), R.string.child_registration_success_title, R.string.child_registration_success_description);
                    successDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ViewHelper.injectFragment(new ManageUsersFragment(), getActivity().getSupportFragmentManager(), R.id.frame_container);
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

}
