package com.celerii.celerii.helperClasses;

/**
 * Created by DELL on 2/26/2018.
 */

public class Day {
    public static String Day(int val){
        switch (val) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
        }
        return "";
    }
}
