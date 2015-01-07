package com.blue_stingray.healthy_life_app.model;

import android.util.Log;

import com.blue_stingray.healthy_life_app.util.Time;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsageReport {

    private HashMap<String, String> score_history;

    public HashMap<String, String> getScoreHistory() {
        return score_history;
    }

    public Integer[] getScoreHistoryByMonth() {
        HashMap<Integer, ArrayList<Integer>> occurences = new HashMap<>();
        Integer[] months = new Integer[12];

        // count score occurrences
        for(Map.Entry<String, String> scoreEntry : score_history.entrySet()) {
            Timestamp timestamp = Time.parseSqlDate(scoreEntry.getKey(), false);
            int month = timestamp.getMonth();
            int score = Integer.parseInt(scoreEntry.getValue());

            if(occurences.get(month) == null) {
                occurences.put(month, new ArrayList<Integer>());
            }
            occurences.get(month).add(score);
        }

        // average scores
        for(int i = 0; i < 12; i++) {
            ArrayList<Integer> scores = occurences.get(i);
            months[i] = 0;

            if(scores != null) {
                int count = 0;
                for(Integer score : scores) {
                    count += score;
                }
                months[i] = count / scores.size();
            }
        }

        return months;
    }



}
