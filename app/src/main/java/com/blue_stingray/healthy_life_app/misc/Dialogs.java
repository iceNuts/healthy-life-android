package com.blue_stingray.healthy_life_app.misc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.blue_stingray.healthy_life_app.R;

/**
 * Some generic dialogs
 */
public class Dialogs {

    /**
     * Create a dialog for an error with the network connection
     */
    public static AlertDialog newNetworkErrorDialog(Context context) {
        return newDismissiveDialog(context, R.string.network_error_title, R.string.network_error_description);
    }

    /**
     * Create a dialog for a problem deserializing the server response
     */
    public static AlertDialog newDeserializationErrorDialog(Context context) {
        return newDismissiveDialog(context, R.string.deserialization_error_title, R.string.deserialization_error_description);
    }

    /**
     * Create a dialog for the server giving a 500 error
     */
    public static AlertDialog newServerErrorDialog(Context context) {
        return newDismissiveDialog(context, R.string.server_error_title, R.string.server_error_description);
    }

    /**
     * Create a dialog the can be dismissed
     */
    public static AlertDialog newDismissiveDialog(Context context, int titleId, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleId);
        builder.setMessage(messageId);
        builder.setNeutralButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.create();
    }
}
