package com.blue_stingray.healthy_life_app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.blue_stingray.healthy_life_app.ui.fragment.SocialConnectFragment;

public class SocialConnectActivity extends BaseActivity {

    private SocialConnectFragment socialConnectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            socialConnectFragment = new SocialConnectFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, socialConnectFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            socialConnectFragment = (SocialConnectFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }

    public void showLogin(View v) {
        startActivity(new Intent(SocialConnectActivity.this, LoginActivity.class));
    }

}
