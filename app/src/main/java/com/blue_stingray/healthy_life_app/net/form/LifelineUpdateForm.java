package com.blue_stingray.healthy_life_app.net.form;

/**
 * Created by BillZeng on 12/15/14.
 */
public class LifelineUpdateForm {

    private final String accepted_at;
    private final String ignored_at;

    public LifelineUpdateForm(String accepted_at, String ignored_at) {
        this.accepted_at = accepted_at;
        this.ignored_at = ignored_at;
    }

}
