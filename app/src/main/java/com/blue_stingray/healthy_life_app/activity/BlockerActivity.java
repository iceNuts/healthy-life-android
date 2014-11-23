package com.blue_stingray.healthy_life_app.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.blue_stingray.healthy_life_app.R;

/**
 * Created by BillZeng on 11/23/14.
 */
public class BlockerActivity extends BaseActivity{

    private ActivityManager activityManager;
    private Intent launcherIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcherIntent = new Intent();
        launcherIntent.setAction(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        builder.setMessage(R.string.app_usage_limit_reached).setTitle(R.string.app_name);
        builder.setPositiveButton(R.string.app_alert_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BlockerActivity.this.startActivity(launcherIntent);
                activityManager.killBackgroundProcesses("com.android.calendar");
                finish();
            }
        });
        builder.setNegativeButton(R.string.app_alert_request, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BlockerActivity.this.startActivity(launcherIntent);
                activityManager.killBackgroundProcesses("com.android.calendar");
                finish();
            }
        });
        builder.create();
        builder.show();
    }
}
