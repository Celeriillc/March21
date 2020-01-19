package com.celerii.celerii.helperClasses;

import java.util.Calendar;

/**
 * Created by DELL on 10/15/2017.
 */

public class Date {
    public static String getDate(){
        Calendar calendar = Calendar.getInstance();
        String date = String.valueOf(calendar.get(Calendar.YEAR)) + "/" + Month.MonthBase1(calendar.get(Calendar.MONTH)) + "/" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar.get(Calendar.MINUTE)) + ":" + String.valueOf(calendar.get(Calendar.SECOND)) + ":" + String.valueOf(calendar.get(Calendar.MILLISECOND));
        return date;
    }

    public static String DateFormatMMDDYYYY(String Date) {
        if (Date == null) {return "";}
        if (Date.equals("")) {return "";}
        String[] dateArray = Date.split(" ")[0].split("/");
        return Month.Month(Integer.valueOf(dateArray[1]) - 1) + " " + dateArray[2] + ", " + dateArray[0];
    }

    public static String getMonthString(String Date) {
        if (Date == null) {return "";}
        if (Date.equals("")) {return "";}
        String[] dateArray = Date.split(" ")[0].split("/");
        return Month.Month(Integer.valueOf(dateArray[1]) - 1);
    }

    public static String getYear(String Date) {
        if (Date == null) {return "";}
        if (Date.equals("")) {return "";}
        String[] dateArray = Date.split(" ")[0].split("/");
        return dateArray[0];
    }

    public static String getYear(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    public static String getMonth(String Date) {
        if (Date == null) {return "";}
        if (Date.equals("")) {return "";}
        String[] dateArray = Date.split(" ")[0].split("/");
        return dateArray[1];
    }

    public static String getMonth(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.MONTH) + 1);
    }

    public static String getDay(String Date) {
        if (Date == null) {return "";}
        if (Date.equals("")) {return "";}
        String[] dateArray = Date.split(" ")[0].split("/");
        return dateArray[2];
    }

    public static String getDay(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
    }

    public static String getHour(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
    }

    public static String getMinute(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.MINUTE));
    }

    public static String getSecond(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.SECOND));
    }

    public static String getMillisecond(){
        Calendar calendar = Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.MILLISECOND));
    }

    private static String makeTwoDigits(int val){
        if (val >= 0 && val < 10){
            return "0" + String.valueOf(val);
        }
        return String.valueOf(val);
    }

    public static String convertToSortableDate(String Date) {
        if (Date == null) {return "";}
        if (Date.equals("")) {return "";}
        String[] calendarDate = Date.split(" ")[0].split("/");
        String[] time = Date.split(" ")[1].split(":");

        String year = calendarDate[0];
        String month = makeTwoDigits(Integer.parseInt(calendarDate[1]));
        String day = makeTwoDigits(Integer.parseInt(calendarDate[2]));
        String hour = makeTwoDigits(Integer.parseInt(time[0]));
        String min = makeTwoDigits(Integer.parseInt(time[1]));
        String sec = makeTwoDigits(Integer.parseInt(time[2]));
        String milliSec = time[3];

        return year + month + day +
                hour + min + sec + milliSec;
    }

    public static String convertToSortableDateShort(String Date) {
        if (Date == null) {return "";}
        if (Date.equals("")) {return "";}
        String[] calendarDate = Date.split("/");

        String year = calendarDate[0];
        String month = makeTwoDigits(Integer.parseInt(calendarDate[1]));
        String day = makeTwoDigits(Integer.parseInt(calendarDate[2]));

        return year + month + day;
    }

    public static boolean compareDates(String dateA, String dateB){
        boolean dateAGreater = false;
        String[] calenderDateA = dateA.split(" ")[0].split("/");
        String[] calenderDateB = dateB.split(" ")[0].split("/");
        String[] timeA = dateA.split(" ")[1].split(":");
        String[] timeB = dateB.split(" ")[1].split(":");

        if (Integer.valueOf(calenderDateA[0]) > Integer.valueOf(calenderDateB[0])){
            dateAGreater = true;
        } else if (Integer.valueOf(calenderDateA[1]) > Integer.valueOf(calenderDateB[1])){
            dateAGreater = true;
        } else if (Integer.valueOf(calenderDateA[2]) > Integer.valueOf(calenderDateB[2])){
            dateAGreater = true;
        } else if (Integer.valueOf(timeA[0]) > Integer.valueOf(timeB[0])){
            dateAGreater = true;
        } else if (Integer.valueOf(timeA[1]) > Integer.valueOf(timeB[1])){
            dateAGreater = true;
        } else if (Integer.valueOf(timeA[2]) > Integer.valueOf(timeB[2])){
            dateAGreater = true;
        } else if (Integer.valueOf(timeA[3]) > Integer.valueOf(timeB[3])){
            dateAGreater = true;
        }

        return dateAGreater;
    }

    public static String getRelativeTimeSpan(String date){
        if (date == null) {return "";}
        if (date.equals("")) {return "";}
        Integer postYear, postMonth, postDay, postHour, postMin, postSec;
        String postDate = date.split(" ")[0];
        String postTime = date.split(" ")[1];
        postYear = Integer.valueOf(postDate.split("/")[0]);
        postMonth = Integer.valueOf(postDate.split("/")[1]);
        postDay = Integer.valueOf(postDate.split("/")[2]);
        postHour = Integer.valueOf(postTime.split(":")[0]);
        postMin = Integer.valueOf(postTime.split(":")[1]);
        postSec = Integer.valueOf(postTime.split(":")[2]);
        Calendar calendar = Calendar.getInstance();
        Integer year, month, day, hour, min, sec;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        sec = calendar.get(Calendar.SECOND);
        int diff = 0;

        Integer yearDiff, monthDiff, dayDiff, hourDiff, minuteDiff, secDiff;
        yearDiff = (year - postYear) * 31104000;
        monthDiff = (month - postMonth) * 2592000;
        dayDiff = (day -postDay) * 86400;
        hourDiff = (hour - postHour) * 3600;
        minuteDiff = (min - postMin) * 60;
        secDiff = (sec - postSec);

        Integer totalTime = yearDiff + monthDiff + dayDiff + hourDiff + minuteDiff + secDiff;

        if (totalTime < 60){
            diff = totalTime;
            if (diff == 1){
                return String.valueOf(diff) + " second ago";
            } else {
                return String.valueOf(diff) + " seconds ago";
            }
        } else if (totalTime >= 60 && totalTime < 3600){
            diff = Math.round(totalTime / 60);
            if (diff == 1){
                return String.valueOf(diff) + " minute ago";
            } else {
                return String.valueOf(diff) + " minutes ago";
            }
        } else if (totalTime >= 3600 && totalTime < 86400){
            diff = Math.round(totalTime / 3600);
            if (diff == 1){
                return String.valueOf(diff) + " hour ago";
            } else {
                return String.valueOf(diff) + " hours ago";
            }
        } else if (totalTime >= 86400 && totalTime < 2592000){
            diff = Math.round(totalTime / 86400);
            if (diff == 1){
                return String.valueOf(diff) + " day ago";
            } else {
                return String.valueOf(diff) + " days ago";
            }
        } else if (totalTime >= 2592000 && totalTime < 31104000){
            diff = Math.round(totalTime / 2592000);
            if (diff == 1){
                return String.valueOf(diff) + " month ago";
            } else {
                return String.valueOf(diff) + " months ago";
            }
        } else if (totalTime >= 31104000){
            diff = Math.round(totalTime / 31104000);
            if (diff == 1){
                return String.valueOf(diff) + " year ago";
            } else {
                return String.valueOf(diff) + " years ago";
            }
        }

        return DateFormatMMDDYYYY(date);
    }

    public static String getRelativeTimeSpanShort(String date){
        if (date == null) {return "";}
        if (date.equals("")) {return "";}
        Integer postYear, postMonth, postDay, postHour, postMin, postSec;
        String postDate = date.split(" ")[0];
        String postTime = date.split(" ")[1];
        postYear = Integer.valueOf(postDate.split("/")[0]);
        postMonth = Integer.valueOf(postDate.split("/")[1]);
        postDay = Integer.valueOf(postDate.split("/")[2]);
        postHour = Integer.valueOf(postTime.split(":")[0]);
        postMin = Integer.valueOf(postTime.split(":")[1]);
        postSec = Integer.valueOf(postTime.split(":")[2]);
        Calendar calendar = Calendar.getInstance();
        Integer year, month, day, hour, min, sec;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        sec = calendar.get(Calendar.SECOND);
        int diff = 0;

        Integer yearDiff, monthDiff, dayDiff, hourDiff, minuteDiff, secDiff;
        yearDiff = (year - postYear) * 31104000;
        monthDiff = (month - postMonth) * 2592000;
        dayDiff = (day -postDay) * 86400;
        hourDiff = (hour - postHour) * 3600;
        minuteDiff = (min - postMin) * 60;
        secDiff = (sec - postSec);

        Integer totalTime = yearDiff + monthDiff + dayDiff + hourDiff + minuteDiff + secDiff;

        if (totalTime < 60){
            diff = totalTime;
            return String.valueOf(diff) + "s";
        } else if (totalTime >= 60 && totalTime < 3600){
            diff = Math.round(totalTime / 60);
            return String.valueOf(diff) + "m";
        } else if (totalTime >= 3600 && totalTime < 86400){
            diff = Math.round(totalTime / 3600);
            return String.valueOf(diff) + "h";
        } else if (totalTime >= 86400 && totalTime < 2592000){
            diff = Math.round(totalTime / 86400);
            return String.valueOf(diff) + "d";
        } else if (totalTime >= 2592000 && totalTime < 31104000){
            diff = Math.round(totalTime / 2592000);
            return String.valueOf(diff) + "m";
        } else if (totalTime >= 31104000){
            diff = Math.round(totalTime / 31104000);
            return String.valueOf(diff) + "y";
        }

        return DateFormatMMDDYYYY(date);
    }

    public static String getFormalDocumentDate(String date) {
        if (date == null) {return "";}
        if (date.equals("")) {return "";}
        Integer postYear, postMonth, postDay;
        String postDate = date.split(" ")[0];
        postYear = Integer.valueOf(postDate.split("/")[0]);
        postMonth = Integer.valueOf(postDate.split("/")[1]);
        postDay = Integer.valueOf(postDate.split("/")[2]);

        String returnableDaySuffix = "";

        if (postDay % 10 == 1) {
            returnableDaySuffix = "st";
        } else if (postDay % 10 == 2) {
            returnableDaySuffix = "nd";
        } else if (postDay % 10 == 3) {
            returnableDaySuffix = "rd";
        } else {
            returnableDaySuffix = "th";
        }

        return postDay + returnableDaySuffix + " " + Month.Month(postMonth - 1) + ", " + postYear;
    }
}
