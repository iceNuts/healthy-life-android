package com.blue_stingray.healthy_life_app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.blue_stingray.healthy_life_app.R;

public class SocialConnectActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_connect);
    }

    public void showLogin(View v) {
        startActivity(new Intent(SocialConnectActivity.this, LoginActivity.class));
    }

}
