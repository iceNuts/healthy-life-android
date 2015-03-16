package com.blue_stingray.healthy_life_app.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.FormSubmitClickListener;
import com.blue_stingray.healthy_life_app.net.form.UserForm;
import com.blue_stingray.healthy_life_app.net.form.validation.FormValidationManager;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.google.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;

/**
 * Created by BillZeng on 3/8/15.
 */
public class UpdateUserFragment extends RoboFragment {

    private EditText userNameView;
    private EditText userEmailView;
    private Button updateButton;
    private User user;

    @Inject
    private SharedPreferencesHelper prefs;

    @Inject
    private RestInterface rest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_update_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = prefs.getCurrentUser();
        userNameView  = (EditText)getActivity().findViewById(R.id.updated_user_name);
        userEmailView = (EditText)getActivity().findViewById(R.id.updated_user_email);
        updateButton = (Button)getActivity().findViewById(R.id.update_user);

        userNameView.setText(user.name);
        userEmailView.setText(user.email);

        updateButton.setOnClickListener(new UpdateUserButtonListener());
    }

    private class UpdateUserButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            InputMethodManager imm =
                    (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(userEmailView.getWindowToken(), 0);
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Loading...");
            rest.updateUser(
                    Integer.valueOf(user.id),
                    new UserForm(
                            userNameView.getText().toString(),
                            userEmailView.getText().toString(),
                            Integer.valueOf(user.is_admin)
                    ),
                    new RetrofitDialogCallback<User>(
                           getActivity(),
                           null
                    ) {

                        @Override
                        public void onSuccess(User user, Response response) {
                            rest.getMyUser(
                                    new RetrofitDialogCallback<User>(
                                            getActivity(),
                                            loading
                                    ) {
                                        @Override
                                        public void onSuccess(User user, Response response) {
                                            prefs.setCurrentUser(user);
                                            Toast.makeText(getActivity(), "Update Successfully", Toast.LENGTH_LONG);
                                            getFragmentManager().popBackStack();
                                        }

                                        @Override
                                        public void onFailure(RetrofitError retrofitError) {
                                            Toast.makeText(getActivity(), "Update Failed", Toast.LENGTH_LONG);
                                        }
                                    }
                            );
                        }

                        @Override
                        public void onFailure(RetrofitError retrofitError) {
                            loading.cancel();
                            Toast.makeText(getActivity(), "Update Failed", Toast.LENGTH_LONG);
                        }
                    });
            }
    }
}













