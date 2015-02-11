package com.blue_stingray.healthy_life_app.model;

/**
 * Created by BillZeng on 2/10/15.
 */
public class MentorRequest {

    public String id;
    public String user_id;
    public String child_id;
    public String requested_at;
    public String accepted_at;
    public String ignored_at;
    public String child_name;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
    }

    public String getRequested_at() {
        return requested_at;
    }

    public void setRequested_at(String requested_at) {
        this.requested_at = requested_at;
    }

    public String getAccepted_at() {
        return accepted_at;
    }

    public void setAccepted_at(String accepted_at) {
        this.accepted_at = accepted_at;
    }

    public String getIgnored_at() {
        return ignored_at;
    }

    public void setIgnored_at(String ignored_at) {
        this.ignored_at = ignored_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
