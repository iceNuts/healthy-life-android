package com.blue_stingray.healthy_life_app.model;

import android.util.Log;

public class User {

    private String id;
    private String mentor_id;
    private String name;
    private String email;
    private String created_at;
    private String updated_at;
    private String deleted_at;
    private String is_admin;
    private String age;
    private String score;
    private int percentage;
    private int rank;

    public User(String name) {
        this.name = name;
    }

    public int getId() {
        return Integer.valueOf(id);
    }

    public String getName() {
        return name;
    }

    public int getPercentile() {
        return percentage;
    }

    public String getPercentileFormatted() {
        return percentage + "%";
    }

    public int getRank() {
        return rank;
    }

    public int getScore() {
        Double score = Double.parseDouble(this.score);
        return Math.max(0, score.intValue());
    }

    public boolean isAdmin() {
        Log.i("healthy", "is admin : " + is_admin);
        return !(is_admin == null || is_admin.equals("0"));
    }

}
