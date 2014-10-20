package com.blue_stingray.healthy_life_app.net.form;

/**
 * Form to create a session
 */
public class SessionForm {
    private String email;
    private String password;

    public SessionForm(CharSequence email, CharSequence password) {
        this.email = email.toString();
        this.password = password.toString();
    }
}
