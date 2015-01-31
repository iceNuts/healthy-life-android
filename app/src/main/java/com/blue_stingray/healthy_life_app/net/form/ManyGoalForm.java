package com.blue_stingray.healthy_life_app.net.form;

public class ManyGoalForm {

    private int device_id;
    private GoalForm[] goals;

    public ManyGoalForm(int device_id, GoalForm[] goals) {
        this.device_id = device_id;
        this.goals = goals;
    }

}
