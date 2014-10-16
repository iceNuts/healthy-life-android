package com.blue_stingray.healthy_life_app.activity;

import android.app.Activity;
import android.os.Bundle;
import com.blue_stingray.healthy_life_app.R;


public class AdminRemovalActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_removal);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
