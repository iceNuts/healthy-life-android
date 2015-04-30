package com.blue_stingray.healthy_life_app.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.receiver.GcmBroadcastReceiver;
import com.blue_stingray.healthy_life_app.receiver.LocalBroadcastManagerProvider;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.blue_stingray.healthy_life_app.storage.db.SharedPreferencesHelper;
import com.blue_stingray.healthy_life_app.ui.activity.MainActivity;
import com.blue_stingray.healthy_life_app.util.Time;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.inject.Inject;

import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;

public class GcmIntentService extends IntentService {

    private DataHelper dataHelper;

    private LocalBroadcastManager localBroadcastManager;

    private SharedPreferencesHelper prefs;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        dataHelper = DataHelper.getInstance(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                Log.d("GCM", extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//                Log.d("GCM", extras.toString());
            }
            // Right place
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//                Log.d("GCM", String.valueOf(extras.get("message")));
                try {
                    JSONObject message = new JSONObject(String.valueOf(extras.get("message")));
                    if (message.has("verdict")) {
                        requestLifeline(message);
                    }
                    else if (message.has("goal")) {
                        syncGoal(message);
                    }
                    else if (message.has("mentorRequest")) {
                        notifyMentorRequest(message);
                    }
                    else if (message.has("remove_goal")) {
                        removeUserGoal(message);
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
                prefs = new SharedPreferencesHelper(getApplicationContext());
                prefs.setNewLifelineRequest(true);
            }
            fireLifelineRequestClickableNotification(subject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncGoal(JSONObject message) {
        try {
            String goalDay = new JSONObject(message.getString("goal")).getString("day");
            String goalHour = new JSONObject(message.getString("goal")).getString("hours");
            String packageName = message.getString("package_name");

            HashMap<Integer, Double> newGoalMap = new HashMap<>();
            newGoalMap.put(Time.dayTranslate(goalDay), Double.valueOf(goalHour));

            dataHelper.createNewGoal(packageName, newGoalMap, true);
            final PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(packageName, 0);
                fireNotification("A new goal has been set for "+pm.getApplicationLabel(ai));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
        }
    }

    private void notifyMentorRequest(JSONObject message) {
        try {
            prefs = new SharedPreferencesHelper(getApplicationContext());
            String subject = message.getString("description");
            fireMentorRequestClickableNotification(subject);
            Intent broadcast = new Intent("mem_notification");
            prefs.setMentorNotificationStatus(true);
            localBroadcastManager.sendBroadcast(broadcast);
        } catch (JSONException e) {
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

    private void fireMentorRequestClickableNotification(String subject) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSubText("Click to view")
                        .setContentText(subject);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 10005;
        Intent MentorRequestIntent = new Intent(this, MainActivity.class);
        MentorRequestIntent.setAction("OPEN_MENTOR_REQUEST");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                MentorRequestIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private void fireLifelineRequestClickableNotification(String subject) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSubText("Click to view")
                        .setContentText(subject);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int mId = 10004;
        Intent lifelineRequestIntent = new Intent(this, MainActivity.class);
        lifelineRequestIntent.setAction("OPEN_LIFELINE_REQUEST");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                lifelineRequestIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private void removeUserGoal(JSONObject message) {
        prefs = new SharedPreferencesHelper(getApplicationContext());
        try {
            // avoid deleting user self goal
            if (message.getString("user_id").equals(String.valueOf(prefs.getCurrentUser().getId())))
                return;
            String subject = message.getString("description");
            fireNotification(subject);
            dataHelper.removeGoal(message.getString("user_id"), message.getString("package_name"));
        } catch (JSONException e) {
        }
    }
}
