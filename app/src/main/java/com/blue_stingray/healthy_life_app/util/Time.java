package com.blue_stingray.healthy_life_app.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Timestamp parseSqlDate(String date, boolean withTime) {
        try {
            String formatString = "yyyy-MM-dd";
            if(withTime) {
                formatString += " HH:mm:ss";
            }

            SimpleDateFormat formatter = new SimpleDateFormat(formatString);
            return new Timestamp(formatter.parse(date).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Timestamp parseSqlDate(String date) {
        return parseSqlDate(date, true);
    }

    public static String getPrettyTime(Timestamp timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("M/d/y hh:mm a");
        return formatter.format(timestamp);
    }


}
