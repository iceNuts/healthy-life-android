package com.blue_stingray.healthy_life_app.net.form;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Form for creating a new user
 */
public class UserForm {

    private final String email;
    private final String password;

    public UserForm(Context context, CharSequence email, CharSequence password) {
        this.email = email.toString();
        this.password = password.toString();
    }
}
