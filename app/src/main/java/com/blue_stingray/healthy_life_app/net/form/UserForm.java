package com.blue_stingray.healthy_life_app.net.form;

/**
 * Form for creating a new user
 */
public class UserForm {

    private String emailAddress;
    private String password;

    public UserForm(CharSequence emailAddress, CharSequence password) {
        this.emailAddress = emailAddress.toString();
        this.password = password.toString();
    }
}
