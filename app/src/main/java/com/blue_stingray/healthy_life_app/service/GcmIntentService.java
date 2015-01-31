package com.blue_stingray.healthy_life_app.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.receiver.GcmBroadcastReceiver;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.util.Time;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;

public class GcmIntentService extends IntentService {

    private DataHelper dataHelper;

    public GcmIntentService() {
        super("GcmIntentService");
        dataHelper = DataHelper.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d("GCM", extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d("GCM", extras.toString());
            }
            // Right place
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.d("GCM", String.valueOf(extras.get("message")));
                try {
                    JSONObject message = new JSONObject(String.valueOf(extras.get("message")));
                    if (message.has("verdict")) {
                        requestLifeline(message);
                    }
                    else if (message.has("goal")) {
                        syncGoal(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void requestLifeline(JSONObject message) {
        String subject = "";
        // someone accept you request
        try {
            if (message.getString("verdict").equals("approve")) {
                dataHelper.extendLifeline(message.getString("package_name"));
                subject = message.getString("user")+" approved your lifeline request.";
            }
            // someone deny your request
            else if (message.getString("verdict").equals("deny")) {
                            /*not much to do*/
                subject = message.getString("user")+" denied your lifeline request.";
            }
            // somebody ask you to approve a lifeline request
            else {
                subject = message.getString("user")+" send you a lifeline request.";
            }
            fireNotification(subject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncGoal(JSONObject message) {
        try {
            String goalDay = new JSONObject(message.getString("goal")).getString("day");
            String goalHour = new JSONObject(message.getString("goal")).getString("hours");
            String packageName = message.getString("package_name");

            HashMap<Integer, Integer> newGoalMap = new HashMap<>();
            newGoalMap.put(Time.dayTranslate(goalDay), Integer.valueOf(goalHour));

            dataHelper.createNewGoal(packageName, newGoalMap);
            final PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(packageName, 0);
                fireNotification("A new goal has been set for "+pm.getApplicationLabel(ai)+" on website.");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }catch (JSONException e) {
        }
    }

    private void fireNotification(String subject) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(subject);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 10002;
        mNotificationManager.notify(mId, mBuilder.build());
    }

}
