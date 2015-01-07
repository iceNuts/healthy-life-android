package com.blue_stingray.healthy_life_app.model;

import android.util.Log;

import java.io.Serializable;

public class User implements Serializable {

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
        return !(is_admin == null || is_admin.equals("0"));
    }

    public int getIsAdmin() {
        return Integer.valueOf(is_admin);
    }

    public int getMentorId() {
        return Integer.valueOf(mentor_id);
    }

    public String getEmail() {
        return email;
    }

}
