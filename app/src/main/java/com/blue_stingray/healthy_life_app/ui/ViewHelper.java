package com.blue_stingray.healthy_life_app.ui;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Provides helper methods for user interface functionality and interaction.
 */
public class ViewHelper {

    public static void injectFragment(Fragment fragment, FragmentManager manager, int container) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void injectFragment(Fragment fragment, int container) {
        injectFragment(fragment, fragment.getFragmentManager(), container);
    }
}
