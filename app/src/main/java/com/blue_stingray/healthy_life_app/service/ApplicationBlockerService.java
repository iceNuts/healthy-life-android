package com.blue_stingray.healthy_life_app.service;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.view.View;

import com.blue_stingray.healthy_life_app.activity.BlockerActivity;
import com.blue_stingray.healthy_life_app.misc.Intents;
import com.blue_stingray.healthy_life_app.receiver.SelfAttachingReceiver;

import java.util.Map;

import roboguice.service.RoboService;

/**
 * Created by BillZeng on 11/23/14.
 */
public class ApplicationBlockerService  extends RoboService{

    private ApplicationChangeReceiver appChangeReceiver = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (appChangeReceiver == null) {
            appChangeReceiver = new ApplicationChangeReceiver();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support binding");
    }

    private class ApplicationChangeReceiver extends SelfAttachingReceiver {

        public ApplicationChangeReceiver() {
            super(ApplicationBlockerService.this, new IntentFilter(Intents.Monitor.APP_CHANGE));
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ComponentName currentComponent = intent.getParcelableExtra(Intents.Monitor.Extra.COMPONENT_NAME);
            Log.d("kill", currentComponent.getPackageName());
            if (currentComponent.getPackageName().equals("com.android.calendar")) {
                Intent dialogIntent = new Intent(getBaseContext(), BlockerActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(dialogIntent);
            }
        }
    }
}
