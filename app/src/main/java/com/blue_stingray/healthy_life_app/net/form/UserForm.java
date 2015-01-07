package com.blue_stingray.healthy_life_app.net.form;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Form for creating a new user
 */
public class UserForm {

    private final String name;
    private final String email;
    private final String password;
    private final Integer mentor_id;
    private final Integer is_admin;

    public UserForm(String name, String email) {
        this(name, email, null, 1, null);
    }

    public UserForm(String name, String email, String password) {
        this(name, email, password, 1, null);
    }

    public UserForm(String name, String email, String password, Integer is_admin, Integer mentor_id) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.is_admin = is_admin;
        this.mentor_id = mentor_id;
    }
}
