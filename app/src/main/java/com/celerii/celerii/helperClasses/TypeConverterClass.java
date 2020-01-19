package com.celerii.celerii.helperClasses;

/**
 * Created by DELL on 1/24/2019.
 */

public class TypeConverterClass {
    public static String convStringToIntString(String string){
        if (string == null) return "";
        if (string.equals("")) return "";
        double douVal = Double.valueOf(string);
        return String.valueOf((int) douVal);
    }

    public static int convStringToInt(String string){
        if (string == null) return 0;
        if (string.equals("")) return 0;
        double douVal = Double.valueOf(string);
        return (int) douVal;
    }

    public static Double convStringToDouble(String string){
        if (string == null) return 0.0;
        if (string.equals("")) return 0.0;
        return Double.valueOf(string);
    }
}
