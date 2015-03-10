package com.blue_stingray.healthy_life_app.model;

import android.util.Log;

import java.io.Serializable;

public class User implements Serializable {

    public String id;
    public String mentor_id;
    public String name;
    public String email;
    public String created_at;
    public String updated_at;
    public String deleted_at;
    public String is_admin;
    public String age;
    public String score;
    public String percentage;
    public String can_edit;
    public boolean is_public;
    public int rank;

    public User(String name) {
        this.name = name;
    }

    public User(String id,
                String mentor_id,
                String name,
                String email,
                String created_at,
                String updated_at,
                String deleted_at,
                String is_admin,
                String age,
                String score,
                String percentage,
                String can_edit,
                int rank,
                boolean is_public
    ) {
        this.id = id;
        this.mentor_id = mentor_id;
        this.name = name;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.is_admin = is_admin;
        this.age = age;
        this.score = score;
        this.percentage = percentage;
        this.can_edit = can_edit;
        this.rank = rank;
        this.is_public = is_public;
    }

    public int getId() {
        if (id == null) {
            return -1;
        }
        return Integer.valueOf(id);
    }

    public String getName() {
        if (name == null) {
            return "UNAVAILABLE";
        }
        return name;
    }

    public int getPercentile() {
        if (percentage == null) {
            return 0;
        }
        return (int) Double.parseDouble(percentage);
    }

    public String getPercentileFormatted() {
        if (percentage == null) {
            return "0%";
        }
        return percentage + "%";
    }

    public int getRank() {
        if (rank == -1) {
            return -1;
        }
        return rank;
    }

    public int getScore() {
        if (score == null)
            return 0;
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

    public boolean canEdit() {
        return can_edit != null && can_edit.equals("1");
    }

}
