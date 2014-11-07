package com.blue_stingray.healthy_life_app.net.form;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import com.blue_stingray.healthy_life_app.misc.FormValidationManager;

/**
 * Click listener for forms that rely on a network action. Will validate the form then show a progress message and call
 * the submit method.
 */
public abstract class FormSubmitClickListener implements View.OnClickListener {

    private Activity activity;
    private FormValidationManager validationManager;
    protected ProgressDialog progressDialog;


    public FormSubmitClickListener(Activity activity, FormValidationManager validationManager, int progressMessageId) {
        this.activity = activity;
        this.validationManager = validationManager;

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(progressMessageId));
        progressDialog.setCancelable(false);
    }


    @Override
    public void onClick(View view) {
        View currentFocus = activity.getCurrentFocus();
        if(currentFocus != null) {
            currentFocus.clearFocus();
        }
        if(!validationManager.isFormValid()) {
            if(currentFocus != null) {
                currentFocus.requestFocus();
            }
            return;
        }

        progressDialog.show();
        submit();
    }

    protected abstract void submit();
}
