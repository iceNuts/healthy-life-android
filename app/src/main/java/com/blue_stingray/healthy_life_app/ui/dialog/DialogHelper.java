package com.blue_stingray.healthy_life_app.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.blue_stingray.healthy_life_app.R;

/**
 * Some generic dialogs
 */
public class DialogHelper {

    /**
     * Create a dialog for an error with the network connection
     */
    public static AlertDialog createNetworkErrorDialog(Context context) {
        return createDismissiveDialog(context, R.string.network_error_title, R.string.network_error_description);
    }

    /**
     * Create a dialog for a problem deserializing the server response
     */
    public static AlertDialog createDeserializationErrorDialog(Context context) {
        return createDismissiveDialog(context, R.string.deserialization_error_title, R.string.deserialization_error_description);
    }

    /**
     * Create a dialog for the server giving a 500 error
     */
    public static AlertDialog createServerErrorDialog(Context context) {
        return createDismissiveDialog(context, R.string.server_error_title, R.string.server_error_description);
    }

    /**
     * Create a dialog the can be dismissed
     */
    public static AlertDialog createDismissiveDialog(Context context, int titleId, int messageId) {
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
