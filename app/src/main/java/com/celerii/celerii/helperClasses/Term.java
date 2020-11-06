package com.celerii.celerii.helperClasses;

import java.util.Calendar;

/**
 * Created by DELL on 10/15/2017.
 */

public class Term {

    public static String Term(String Term){
        if (Term==null){ return ""; }
        switch (Term){
            case "1":
                return "First Term";
            case "2":
                return "Second Term";
            case "3":
                return "Third Term";
            case "Not Available":
                return "Not Available";
        }
        return "Other Term";
    }

    public static String TermShort(String Term){
        if (Term==null){ return ""; }
        switch (Term){
            case "1":
                return "1st Term";
            case "2":
                return "2nd Term";
            case "3":
                return "3rd Term";
            case "Not Available":
                return "NA";
        }
        return "Other Term";
    }

    public static String getTermShort(){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        String term = "0";

        if (month >= 9 && month <= 12){
            term = "1";
        } else if (month >= 1 && month <= 4) {
            term = "2";
        } else if (month > 4 && month <= 8) {
            term = "3";
        }

        return term;
    }

    public static String getTermLong(){
        String term = Term(getTermShort());
        return term;
    }

    public static String getTermShort(String month){
        int intMonth = Integer.valueOf(month);
        String term = "0";

        if (intMonth >= 9 && intMonth <= 12){
            term = "1";
        } else if (intMonth >= 1 && intMonth <= 4){
            term = "2";
        } else if (intMonth > 4 && intMonth <= 7){
            term = "3";
        }

        return term;
    }

    public static String getTermLong(String month) {
        String term = Term(getTermShort(month));
        return term;
    }
}
