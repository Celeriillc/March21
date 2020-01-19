package com.celerii.celerii.helperClasses;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DELL on 5/9/2018.
 */

public class PushNotificationSettings {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    static final String class_Post = "classPost";
    static final String assignment = "assignment";
    static final String messages = "messages";
    static final String attendance = "attendance";
    static final String timetable = "timetable";
    static final String performance_Results = "performanceResults";
    static final String behavioral_Results = "behavioralResults";
    static final String new_Event = "newEvent";
    static final String new_Newsletter = "newNewsletter";

    public PushNotificationSettings(Context context) {
        this.context = context;
        prefs = this.context.getSharedPreferences("PushNotifications", 0);
        editor = prefs.edit();
    }

    //region ClassPost
    public boolean getClassPost() {
        return prefs.getBoolean(class_Post, true);
    }

    public void setClassPost(boolean classPost) {
        editor.putBoolean(class_Post, classPost);
        editor.commit();
    }

    public void deleteClassPost() {
        editor.remove(class_Post);
        editor.commit();
    }
    //endregion

    //region Assignment
    public boolean getAssignment() {
        return prefs.getBoolean(assignment, true);
    }

    public void setAssignment(boolean assignment) {
        editor.putBoolean(this.assignment, assignment);
        editor.commit();
    }

    public void deleteAssignment() {
        editor.remove(assignment);
        editor.commit();
    }
    //endregion

    //region Messages
    public boolean getMessages() {
        return prefs.getBoolean(messages, true);
    }

    public void setMessages(boolean messages) {
        editor.putBoolean(this.messages, messages);
        editor.commit();
    }

    public void deleteMessages() {
        editor.remove(messages);
        editor.commit();
    }
    //endregion

    //region Attendance
    public boolean getAttendance() {
        return prefs.getBoolean(attendance, true);
    }

    public void setAttendance(boolean attendance) {
        editor.putBoolean(this.attendance, attendance);
        editor.commit();
    }

    public void deleteAttendance() {
        editor.remove(attendance);
        editor.commit();
    }
    //endregion

    //region Timetable
    public boolean getTimetable() {
        return prefs.getBoolean(timetable, true);
    }

    public void setTimetable(boolean timetable) {
        editor.putBoolean(this.timetable, timetable);
        editor.commit();
    }

    public void deleteTimetable() {
        editor.remove(timetable);
        editor.commit();
    }
    //endregion

    //region PerformanceResults
    public boolean getPerformanceResults() {
        return prefs.getBoolean(performance_Results, true);
    }

    public void setPerformanceResults(boolean performanceResults) {
        editor.putBoolean(performance_Results, performanceResults);
        editor.commit();
    }

    public void deletePerformanceResults() {
        editor.remove(performance_Results);
        editor.commit();
    }
    //endregion

    //region BehavioralResults
    public boolean getBehavioralResults() {
        return prefs.getBoolean(behavioral_Results, true);
    }

    public void setBehavioralResults(boolean behavioralResults) {
        editor.putBoolean(behavioral_Results, behavioralResults);
        editor.commit();
    }

    public void deleteBehavioralResults() {
        editor.remove(behavioral_Results);
        editor.commit();
    }
    //endregion

    //region NewEvent
    public boolean getNewEvent() {
        return prefs.getBoolean(new_Event, true);
    }

    public void setNewEvent(boolean newEvent) {
        editor.putBoolean(new_Event, newEvent);
        editor.commit();
    }

    public void deleteNewEvent() {
        editor.remove(new_Event);
        editor.commit();
    }
    //endregion

    //region NewNewsletter
    public boolean getNewNewsletter() {
        return prefs.getBoolean(new_Newsletter, true);
    }

    public void setNewNewsletter(boolean newNewsletter) {
        editor.putBoolean(new_Newsletter, newNewsletter);
        editor.commit();
    }

    public void deleteNewNewsletter() {
        editor.remove(new_Newsletter);
        editor.commit();
    }
    //endregion
}
