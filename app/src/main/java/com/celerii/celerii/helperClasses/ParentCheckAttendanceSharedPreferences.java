package com.celerii.celerii.helperClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DELL on 1/6/2019.
 */

public class ParentCheckAttendanceSharedPreferences {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    static final String subject = "General";

    public ParentCheckAttendanceSharedPreferences(Context context) {
        this.context = context;
        prefs = this.context.getSharedPreferences("ParentCheckAttendancePreferences", 0);
        editor = prefs.edit();
    }

    public String getSubject() {
        return prefs.getString(subject, "General");
    }

    public void setSubject(String subject) {
        editor.putString(this.subject, subject);
        editor.commit();
    }

    public void deleteSubject() {
        editor.remove(subject);
        editor.commit();
    }
}
