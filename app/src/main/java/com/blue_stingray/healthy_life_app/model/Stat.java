package com.blue_stingray.healthy_life_app.model;

import com.blue_stingray.healthy_life_app.util.Time;
import java.sql.Timestamp;

public class Stat {

    private String id;
    private String elapsed;
    private String start;
    private String stop;
    private String created_at;
    private String updated_at;
    private String deleted_at;

    public int getElapsed() {
        return Integer.parseInt(elapsed);
    }

    public Timestamp getStart() {
        return Time.parseSqlDate(start);
    }

    public Timestamp getStop() {
        return Time.parseSqlDate(stop);
    }

    public Timestamp getCreatedAt() {
        return Time.parseSqlDate(created_at);
    }

    public Timestamp getUpdatedAt() {
        return Time.parseSqlDate(updated_at);
    }

    public Timestamp getDeletedAt() {
        return Time.parseSqlDate(deleted_at);
    }

    public int getId() {
        return Integer.valueOf(id);
    }

}
