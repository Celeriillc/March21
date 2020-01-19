package com.celerii.celerii.helperClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DELL on 12/18/2018.
 */

public class TeacherEnterResultsSharedPreferences {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    static final String subject = "General";

    public TeacherEnterResultsSharedPreferences(Context context) {
        this.context = context;
        prefs = this.context.getSharedPreferences("AltariiTeacherEnterResultsPreferences", 0);
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
