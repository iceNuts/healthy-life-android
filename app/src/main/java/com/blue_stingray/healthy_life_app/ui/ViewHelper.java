package com.blue_stingray.healthy_life_app.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.blue_stingray.healthy_life_app.ui.activity.LoginActivity;

/**
 * Provides helper methods for user interface functionality and interaction.
 */
public class ViewHelper {

    public static void injectFragment(Fragment fragment, FragmentManager manager, int container) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(container, fragment);
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    public static void injectFragment(Fragment fragment, int container) {
        injectFragment(fragment, fragment.getFragmentManager(), container);
    }

    public static void unauthorized(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Unauthorized");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
