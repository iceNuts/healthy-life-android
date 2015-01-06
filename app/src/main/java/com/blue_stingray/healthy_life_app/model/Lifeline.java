package com.blue_stingray.healthy_life_app.model;

public class Lifeline {

    public String id;
    public String user_name;
    public String app_name;
    public String user_id;
    public String app_id;
    public String requested_at;
    public String accepted_at;
    public String ignored_at;

    public int getId() {
        return Integer.valueOf(id);
    }

}
