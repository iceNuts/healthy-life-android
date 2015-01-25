package com.blue_stingray.healthy_life_app.model;

import com.blue_stingray.healthy_life_app.util.Time;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Alert {

    private String id;
    private String user_id;
    private String target_id;
    private String target_type;
    private String created_at;
    private String updated_at;
    private User user;
    private Bundle bundle;

    public Alert(String subject) {
        if(bundle == null) {
            bundle = new Bundle();
        }

        this.bundle.subject = subject;
    }

    public int getId() {
        if(id == null) {
            return -1;
        }

        return Integer.valueOf(id);
    }

    public int getUserId() {
        if(user_id == null) {
            return -1;
        }

        return Integer.valueOf(user_id);
    }

    public int getTargetId() {
        if(target_id == null) {
            return -1;
        }

        return Integer.valueOf(target_id);
    }

    public String getTargetType() {
        return target_type;
    }

    public String getSubject() {
        return bundle.subject;
    }

    public String getTarget() {
        return bundle.target;
    }

    public String getAction() {
        return bundle.action;
    }

    public Timestamp getCreatedAt() {
        return Time.parseSqlDate(created_at);
    }

    public Timestamp getUpdatedAt() {
        return Time.parseSqlDate(updated_at);
    }

    public User getUser() {
        return user;
    }

    private class Bundle {

        public String subject;
        public String action;
        public String target;

    }

}
