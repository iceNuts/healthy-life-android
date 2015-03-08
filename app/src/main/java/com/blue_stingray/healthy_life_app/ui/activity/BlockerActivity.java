package com.blue_stingray.healthy_life_app.ui.activity;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;

import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.model.Lifeline;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.RestInterface;
import com.blue_stingray.healthy_life_app.net.RetrofitDialogCallback;
import com.blue_stingray.healthy_life_app.net.form.LifelineForm;
import com.blue_stingray.healthy_life_app.storage.db.DataHelper;
import com.google.inject.Inject;

import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BlockerActivity extends BaseActivity{

    private ActivityManager activityManager;
    private Intent launcherIntent;

    @Inject private RestInterface rest;
    ProgressDialog progressDialog;

    private DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataHelper = DataHelper.getInstance(this);

        // hide action bar
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        // set transparent background
        setContentView(R.layout.activity_blocker);

        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);

        launcherIntent = new Intent();
        launcherIntent.setAction(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);

        final ProgressDialog loading = ProgressDialog.show(this, "", "Loading...");
        rest.getMyUser(
            new RetrofitDialogCallback<User>(this, loading) {
                @Override
                public void onSuccess(User user, Response response) {
                    // it is admin
                    if (1 == user.getIsAdmin()) {
                        // has mentor?
                        try {
                            Integer mentorId = user.getMentorId();
                            // there is a mentor
                            showBlockerDialog();
                        }
                        catch (Exception e) {
                            // was unlock by itself
//                            showUnlockDialog();
                            showBlockerDialog();
                        }
                    }
                    else {
                        showBlockerDialog();
                    }
                }

                @Override
                public void onFailure(RetrofitError error) {
                    showBlockerDialog();
                }
            }
        );
    }

    private void showBlockerDialog() {
        final String packageName = getIntent().getStringExtra("packageName");
        final String alertInfo = getIntent().getStringExtra("AlertInfo");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (packageName != null) {
            builder.setMessage(R.string.app_usage_limit_reached).setTitle(R.string.app_name);
            builder.setPositiveButton(R.string.app_alert_close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BlockerActivity.this.startActivity(launcherIntent);
                    activityManager.killBackgroundProcesses(packageName);
                    finish();
                }
            });

            builder.setNegativeButton(R.string.app_alert_request, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    activityManager.killBackgroundProcesses(packageName);
                    rest.createLifeline(
                            new LifelineForm(
                                    packageName,
                                    String.valueOf(new Date().getTime() / 1000)
                            ),
                            new RetrofitDialogCallback<Lifeline>(
                                    getApplicationContext(),
                                    null
                            ) {
                                @Override
                                public void onSuccess(Lifeline lifeline, Response response) {
                                    BlockerActivity.this.startActivity(launcherIntent);
                                    finish();
                                }

                                @Override
                                public void onFailure(RetrofitError retrofitError) {
                                    BlockerActivity.this.startActivity(launcherIntent);
                                    finish();
                                }
                            }
                    );
                }
            });
            builder.create();
            builder.show();
        }
        else {
            builder.setMessage(alertInfo).setTitle(R.string.app_name);
            builder.setPositiveButton(R.string.app_alert_close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BlockerActivity.this.startActivity(launcherIntent);
                    activityManager.killBackgroundProcesses(packageName);
                    finish();
                }
            });
            builder.create();
            builder.show();
        }
    }

    private void showUnlockDialog() {
        final String packageName = getIntent().getStringExtra("packageName");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have used up time for this app, click unlock to continue use. ").setTitle(R.string.app_name);
        builder.setPositiveButton(R.string.app_alert_unlock, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dataHelper.extendLifeline(packageName);
                finish();
            }
        });
        builder.create();
        builder.show();
    }
}
