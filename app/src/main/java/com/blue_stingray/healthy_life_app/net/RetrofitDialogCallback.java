package com.blue_stingray.healthy_life_app.net;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.blue_stingray.healthy_life_app.ui.dialog.DialogHelper;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Retrofit Callback for handling network events with loading dialogs. Will dismiss the progress dialog and show an
 * appropriate one for common errors.
 */
public abstract class RetrofitDialogCallback<T> implements Callback<T> {
    private Context context;
    private ProgressDialog dialog;

    public RetrofitDialogCallback(Context context, ProgressDialog dialog) {
        this.context = context;
        this.dialog = dialog;
        this.dialog.setCancelable(true);
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onFailure(RetrofitError.networkError("", new IOException()));
            }
        });
    }

    @Override
    public void success(T t, Response response) {
        if(dialog != null)
            dialog.hide();
        onSuccess(t, response);
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        if(dialog == null) {
            onFailure(retrofitError);
            return;
        }
        dialog.hide();
        switch (retrofitError.getKind()) {
            case NETWORK:
                DialogHelper.createNetworkErrorDialog(context).show();
                break;
             case CONVERSION:
                 DialogHelper.createDeserializationErrorDialog(context).show();
                 break;
             case HTTP:
                 if (retrofitError.getResponse().getStatus() == 500) {
                     DialogHelper.createServerErrorDialog(context).show();
                 } else {
                     onFailure(retrofitError);
                 }
        }
    }

    public abstract void onSuccess(T t, Response response);
    public abstract void onFailure(RetrofitError retrofitError);

}
