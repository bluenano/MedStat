package com.seanschlaefli.medstat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class FormatDateTime {

    private FormatDateTime() {}


    public static String getFormattedTimeString(DateTime date) {
        String hour = getHourString(date);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("mm");
        String minute = date.toString(formatter);
        String amOrPm = getAmOrPm(date);
        return hour + ":" + minute + " " + amOrPm;
    }


    public static String getFormattedDateString(DateTime date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM d");
        return date.toString(formatter);
    }


    public static String getHourString(DateTime date) {
        int hourOfDay = Integer.parseInt(date.hourOfDay().getAsString());
        if (hourOfDay > 12) {
            return Integer.toString(hourOfDay-12);
        } else if (hourOfDay == 0) {
            return Integer.toString(12);
        }
        return Integer.toString(hourOfDay);
    }

    public static String getAmOrPm(DateTime date) {
        int hour = Integer.parseInt(date.hourOfDay().getAsString());
        if (hour < 12) {
            return " AM ";
        } else {
            return " PM ";
        }
    }
}
