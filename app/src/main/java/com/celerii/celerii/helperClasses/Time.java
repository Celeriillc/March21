package com.celerii.celerii.helperClasses;

/**
 * Created by DELL on 10/15/2017.
 */

public class Time {
    public static String TimeFormatHHMMSS(String Date) {
        String[] timeArray = Date.split(" ")[1].split(":");
        String AMPM, hour, minute, sec;
        if (Integer.valueOf(timeArray[0]) >= 12){
            AMPM = "PM";
            hour = String.valueOf(Integer.valueOf(timeArray[0]) - 12);
        } else {
            AMPM = "AM";
            hour = String.valueOf(Integer.valueOf(timeArray[0]));
        }
        minute = timeArray[1];
        sec = timeArray[2];
        return hour + ":" + minute + ":" + sec + " " + AMPM;
    }

    public static String TimeFormatHHMM(String Date) {
        String[] timeArray = Date.split(" ")[1].split(":");
        String AMPM, hour, minute;
        if (Integer.valueOf(timeArray[0]) >= 12){
            AMPM = "PM";
            hour = String.valueOf(Integer.valueOf(timeArray[0]) - 12);
        } else {
            AMPM = "AM";
            hour = String.valueOf(Integer.valueOf(timeArray[0]));
        }
        minute = timeArray[1];
        return hour + ":" + minute + " " + AMPM;
    }

    public static String TimeFormatHH(String Date) {
        String[] timeArray = Date.split(" ")[1].split(":");
        String AMPM, hour;
        if (Integer.valueOf(timeArray[0]) >= 12){
            AMPM = "PM";
            hour = String.valueOf(Integer.valueOf(timeArray[0]) - 12);
        } else {
            AMPM = "AM";
            hour = String.valueOf(Integer.valueOf(timeArray[0]));
        }
        return hour + " " + AMPM;
    }
}
