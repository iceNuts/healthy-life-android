package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.net.form.UserForm;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.net.form.validation.ValidationRule;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.ui.ViewHelper;
import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;
import com.google.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;


public class EditUserFragment extends RoboFragment {

    @InjectView(R.id.email)
    private EditText emailField;

    @InjectView(R.id.name)
    private EditText nameField;

    @InjectView(R.id.edit)
    private Button editButton;

    @Inject
    private RestInterface rest;

    private FormValidationManager validationManager;

    private DataHelper dataHelper;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = (User) getArguments().getSerializable("user");
        return inflater.inflate(R.layout.fragment_edit_user, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_edit_user);

        // prepopulate
        emailField.setText(user.getEmail());
        nameField.setText(user.getName());

        validationManager = new FormValidationManager();
        validationManager.addField(nameField, ValidationRule.requiredValidationRule(getActivity(), R.string.missing_name));
        validationManager.addField(emailField, ValidationRule.newEmailValidationRule(getActivity()));

        editButton.setOnClickListener(new EditButtonListener());
    }

    private class EditButtonListener extends FormSubmitClickListener {

        public EditButtonListener() {
            super(getActivity(), validationManager, R.string.editing_user);
        }

        @Override
        protected void submit() {
            rest.updateUser(user.getId(), new UserForm(nameField.getText().toString(), emailField.getText().toString()), new RetrofitDialogCallback<User>(getActivity(), progressDialog) {
                @Override
                public void onSuccess(User user, Response response) {
                    AlertDialog successDialog = DialogHelper.createDismissiveDialog(getActivity(), R.string.child_edit_success_title, R.string.child_edit_success_description);
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
