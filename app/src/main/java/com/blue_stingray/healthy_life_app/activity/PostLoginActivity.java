package com.blue_stingray.healthy_life_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.blue_stingray.healthy_life_app.R;
import com.blue_stingray.healthy_life_app.db.SharedPreferencesHelper;
import roboguice.inject.InjectView;

/**
 * Activity triggered after login. Responsible for sync, enabling device admin, etc
 */
public class PostLoginActivity extends BaseActivity {

    @InjectView(R.id.enable_device_admin)
    private Button adminRequestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);

        adminRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptForAdmin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDeviceAdmin()) {
            prefs.setState(SharedPreferencesHelper.State.READY);
            prefs.setLocked(true);
            startActivity(new Intent(this, StartActivity.class));
            finish();
        }
    }

}
