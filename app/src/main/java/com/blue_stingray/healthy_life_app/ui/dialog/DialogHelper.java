package com.blue_stingray.healthy_life_app.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.AdapterView;

import com.blue_stingray.healthy_life_app.R;

/**
 * Contains generic dialogs.
 */
public class DialogHelper {

    /**
     * Create a dialog for an error with the network connection.
     * @param context Context
     * @return AlertDialog
     */
    public static AlertDialog createNetworkErrorDialog(Context context) {
        return createDismissiveDialog(context, R.string.network_error_title, R.string.network_error_description);
    }

    /**
     * Create a dialog for a problem deserializing the server response.
     * @param context Context
     * @return AlertDialog
     */
    public static AlertDialog createDeserializationErrorDialog(Context context) {
        return createDismissiveDialog(context, R.string.deserialization_error_title, R.string.deserialization_error_description);
    }

    /**
     * Create a dialog for the server giving a 500 error.
     * @param context Context
     * @return AlertDialog
     */
    public static AlertDialog createServerErrorDialog(Context context) {
        return createDismissiveDialog(context, R.string.server_error_title, R.string.server_error_description);
    }

    /**
     * Create a dialog the can be dismissed.
     * @param context Context
     * @param titleId int - String resource id of the title
     * @param messageId int - String resource id of the message
     * @return AlertDialog
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

    public static AlertDialog createSingleSelectionDialog(Context context, String title, int options, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(options, listener);
        return builder.create();
    }

    public static AlertDialog createYesNoDialog(Context context, String title, String acceptMessage, String denyMessage, DialogInterface.OnClickListener accept, DialogInterface.OnClickListener deny) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setPositiveButton(acceptMessage, accept);
        builder.setNegativeButton(denyMessage, deny);
        return builder.create();
    }

}
