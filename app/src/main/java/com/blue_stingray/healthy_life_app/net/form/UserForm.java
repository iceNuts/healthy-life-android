package com.blue_stingray.healthy_life_app.net.form;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Form for creating a new user
 */
public class UserForm {

    private String name;
    private String email;
    private String password;
    private Integer mentor_id;
    private Integer is_admin;
    private Integer can_edit;
    private Boolean is_public;

    public UserForm(boolean isPublic) {

        this(null, null, null, null, null, isPublic);
    }

    public UserForm(String name, String email) {

        this(name, email, null, 1, null, null);
    }

    public UserForm(String name, String email, Integer is_admin) {

        this(name, email, null, is_admin, null, null);
    }

    public UserForm(String name, String email, String password, Integer is_admin) {

        this(name, email, password, is_admin, null, null);
    }

    public UserForm(String name, String email, String password) {
        this(name, email, password, 1, null, null);
    }

    public UserForm(Integer can_edit) {
        this.can_edit = can_edit;
    }


    public UserForm(String name, String email, String password, Integer is_admin, Integer mentor_id, Boolean is_public) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.is_admin = is_admin;
        this.mentor_id = mentor_id;
        this.is_public = is_public;
    }
}
