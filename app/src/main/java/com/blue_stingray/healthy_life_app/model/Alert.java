package com.blue_stingray.healthy_life_app.model;

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
    private Object targetObj;
    public String subject;
    private String action;
    private String target;
    private String message;

    public Alert(String subject) {
        this.subject = subject;
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

    public String getSubject() {
        return subject;
    }

    public String getTarget() {
        return target;
    }

    public String getAction() {
        return action;
    }

    public Timestamp getCreatedAt() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new Timestamp(formatter.parse(created_at).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Timestamp getUpdatedAt() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new Timestamp(formatter.parse(updated_at).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUser() {
        return user;
    }

    public Object getTargetObj() {
        return targetObj;
    }

    public String build() {
        return message;
    }

}
