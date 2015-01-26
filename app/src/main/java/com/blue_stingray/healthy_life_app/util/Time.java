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

    public static int dayTranslate(String day) {
        day = day.toLowerCase();

        if(day.equals("mon")) {
            return Calendar.MONDAY;
        }
        else if (day.equals("tue") ) {
            return Calendar.TUESDAY;
        }
        else if (day.equals("wed")) {
            return Calendar.WEDNESDAY;
        }
        else if (day.equals("thu")) {
            return Calendar.THURSDAY;
        }
        else if (day.equals("fri")) {
            return Calendar.FRIDAY;
        }
        else if (day.equals("sat")) {
            return Calendar.SATURDAY;
        }
        else {
            return Calendar.SUNDAY;
        }
    }

    public static String dayTranslate(int day) {
        if(day == Calendar.MONDAY) {
            return "mon";
        }
        else if (day == Calendar.TUESDAY) {
            return "tue";
        }
        else if (day == Calendar.WEDNESDAY) {
            return "wed";
        }
        else if (day == Calendar.THURSDAY) {
            return "thu";
        }
        else if (day == Calendar.FRIDAY) {
            return "fri";
        }
        else if (day == Calendar.SATURDAY) {
            return "sat";
        }
        else {
            return "sun";
        }
    }

}
