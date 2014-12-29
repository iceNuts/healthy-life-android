package com.blue_stingray.healthy_life_app.model;

public class User {

    private String id;
    private String mentorId;
    private String name;
    private String email;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private String isAdmin;
    private String age;
    private String score;

    public User(String name) {
        this.name = name;
    }

    public int getTrackableApps() {
        return 10;
    }

    public int getActiveGoals() {
        return 9;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        Double score = Double.parseDouble(this.score);
        return Math.max(0, score.intValue());
    }

    public boolean isAdmin() {
        return isAdmin == null || isAdmin.equals("0");
    }

}
