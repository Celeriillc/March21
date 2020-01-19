package com.celerii.celerii.helperClasses;

import android.graphics.Color;

import com.celerii.celerii.R;
import com.amulyakhare.textdrawable.TextDrawable;

/**
 * Created by DELL on 10/21/2017.
 */

public class CreateDrawable {
    public static TextDrawable attendanceDrawable(String attendance){
        if (attendance.equals("Present")){
            String attendanceLetter = "P";
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(attendanceLetter, Color.argb(255, 0, 200, 0));
            return textDrawable;
        } else if (attendance.equals("Absent")){
            String attendanceLetter = "A";
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(attendanceLetter, Color.argb(255, 255, 0, 0));
            return textDrawable;
        } else if (attendance.equals("Came In Late")){
            String attendanceLetter = "L";
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(attendanceLetter, Color.argb(255, 0, 0, 255));
            return textDrawable;
        } else {
            String attendanceLetter = "-";
            TextDrawable textDrawable = TextDrawable.builder()
                    .buildRound(attendanceLetter, Color.GRAY);
            return textDrawable;
        }
    }

    public static TextDrawable subjectNameDrawable(String subject){
        String firstLetter = subject.substring(0, 1);
        return TextDrawable.builder()
                .buildRound(firstLetter, Color.GRAY);
    }

    public static int attendanceMarkerDrawable(String attendance){
        if (attendance.equals("Present")){
            return R.drawable.ic_attendance_present_24dp;
        } else if (attendance.equals("Absent")){
            return R.drawable.ic_attendance_absent_24dp;
        } else if (attendance.equals("Came In Late")){
            return R.drawable.ic_attendance_late_24dp;
        } else {
            return R.drawable.ic_attendance_late_24dp;
        }
    }
}
