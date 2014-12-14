package com.blue_stingray.healthy_life_app.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.blue_stingray.healthy_life_app.receiver.GcmBroadcastReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.util.Log;

/**
 * Created by BillZeng on 12/14/14.
 */
public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {

            }
            // Right place
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.d("GCM", extras.toString());
            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
