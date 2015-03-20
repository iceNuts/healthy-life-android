package com.blue_stingray.healthy_life_app.net.form;

/**
 * Created by BillZeng on 3/19/15.
 */
public class DeleteGoalForm {

    private final String package_name;
    private final String device_id;

    public DeleteGoalForm(String device_id, String package_name) {
        this.package_name = package_name;
        this.device_id = device_id;
    }
}
