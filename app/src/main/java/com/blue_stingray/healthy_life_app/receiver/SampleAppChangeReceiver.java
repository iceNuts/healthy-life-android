package com.blue_stingray.healthy_life_app.receiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.blue_stingray.healthy_life_app.R;

/**
 * Created by nick on 9/20/14.
 */
public class SampleAppChangeReceiver extends SelfAttachingReceiver {

    private PackageManager packageManager;
    private Handler handler = new Handler(Looper.getMainLooper());

    public SampleAppChangeReceiver(Context context) {
        super(context, new IntentFilter(context.getString(R.string.app_change)));
        packageManager = context.getApplicationContext().getPackageManager();
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(getClass().getSimpleName(), "Received App change Intent");
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    ComponentName component = intent.getParcelableExtra(context.getString(R.string.component_name));
                    CharSequence label = packageManager.getApplicationLabel(packageManager.getApplicationInfo(component.getPackageName(), 0));
                    Toast.makeText(context, label, Toast.LENGTH_SHORT).show();

                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(context, "Unknown", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
