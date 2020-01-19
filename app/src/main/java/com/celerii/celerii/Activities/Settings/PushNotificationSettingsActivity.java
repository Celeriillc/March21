package com.celerii.celerii.Activities.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.PushNotificationSettings;

public class PushNotificationSettingsActivity extends AppCompatActivity {

    PushNotificationSettings pushNotificationSettings;
    private Toolbar toolbar;
    RadioGroup classPost, assignments, messages, attendance, timetable, performanceResults, behaviouralResults, events, newsletters;
    RadioButton classPostOn, assignmentsOn, messagesOn, attendanceOn, timetableOn, performanceResultsOn, behaviouralResultsOn, eventsOn, newslettersOn;
    RadioButton classPostOff, assignmentsOff, messagesOff, attendanceOff, timetableOff, performanceResultsOff, behaviouralResultsOff, eventsOff, newslettersOff;
    boolean classPostStat, assignmentsStat, messagesStat, attendanceStat, timetableStat, performanceResultsStat, behaviouralResultsStat, eventsStat, newslettersStat;

    @Override
    protected void onStop() {
        pushNotificationSettings = new PushNotificationSettings(this);

        if (classPostOn.isChecked()){
            pushNotificationSettings.setClassPost(true);
        } else {
            pushNotificationSettings.setClassPost(false);
        }

        if (assignmentsOn.isChecked()){
            pushNotificationSettings.setAssignment(true);
        } else {
            pushNotificationSettings.setAssignment(false);
        }

        if (messagesOn.isChecked()){
            pushNotificationSettings.setMessages(true);
        } else {
            pushNotificationSettings.setMessages(false);
        }

        if (attendanceOn.isChecked()){
            pushNotificationSettings.setAttendance(true);
        } else {
            pushNotificationSettings.setAttendance(false);
        }

        if (timetableOn.isChecked()){
            pushNotificationSettings.setTimetable(true);
        } else {
            pushNotificationSettings.setTimetable(false);
        }

        if (performanceResultsOn.isChecked()){
            pushNotificationSettings.setPerformanceResults(true);
        } else {
            pushNotificationSettings.setPerformanceResults(false);
        }

        if (behaviouralResultsOn.isChecked()){
            pushNotificationSettings.setBehavioralResults(true);
        } else {
            pushNotificationSettings.setBehavioralResults(false);
        }

        if (eventsOn.isChecked()){
            pushNotificationSettings.setNewEvent(true);
        } else {
            pushNotificationSettings.setNewEvent(false);
        }

        if (newslettersOn.isChecked()){
            pushNotificationSettings.setNewNewsletter(true);
        } else {
            pushNotificationSettings.setNewNewsletter(false);
        }

        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification_settings);

        pushNotificationSettings = new PushNotificationSettings(this);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Notification Settings");

        classPost = (RadioGroup) findViewById(R.id.classposts);
        assignments = (RadioGroup) findViewById(R.id.assignments);
        messages = (RadioGroup) findViewById(R.id.messages);
        attendance = (RadioGroup) findViewById(R.id.attendance);
        timetable = (RadioGroup) findViewById(R.id.timetable);
        performanceResults = (RadioGroup) findViewById(R.id.performanceresults);
        behaviouralResults = (RadioGroup) findViewById(R.id.behavioralresults);
        events = (RadioGroup) findViewById(R.id.events);
        newsletters = (RadioGroup) findViewById(R.id.newsletter);

        classPostOn = (RadioButton) findViewById(R.id.classpostson);
        assignmentsOn = (RadioButton) findViewById(R.id.assignmentson);
        messagesOn = (RadioButton) findViewById(R.id.messageson);
        attendanceOn = (RadioButton) findViewById(R.id.attendanceon);
        timetableOn = (RadioButton) findViewById(R.id.timetableon);
        performanceResultsOn = (RadioButton) findViewById(R.id.performanceresultson);
        behaviouralResultsOn = (RadioButton) findViewById(R.id.behavioralresultson);
        eventsOn = (RadioButton) findViewById(R.id.eventson);
        newslettersOn = (RadioButton) findViewById(R.id.newsletteron);

        classPostOff = (RadioButton) findViewById(R.id.classpostsoff);
        assignmentsOff = (RadioButton) findViewById(R.id.assignmentsoff);
        messagesOff = (RadioButton) findViewById(R.id.messagesoff);
        attendanceOff = (RadioButton) findViewById(R.id.attendanceoff);
        timetableOff = (RadioButton) findViewById(R.id.timetableoff);
        performanceResultsOff = (RadioButton) findViewById(R.id.performanceresultsoff);
        behaviouralResultsOff = (RadioButton) findViewById(R.id.behavioralresultsoff);
        eventsOff = (RadioButton) findViewById(R.id.eventsoff);
        newslettersOff = (RadioButton) findViewById(R.id.newsletteroff);

        classPostStat = pushNotificationSettings.getClassPost();
        assignmentsStat = pushNotificationSettings.getAssignment();
        messagesStat = pushNotificationSettings.getMessages();
        attendanceStat = pushNotificationSettings.getAttendance();
        timetableStat = pushNotificationSettings.getTimetable();
        performanceResultsStat = pushNotificationSettings.getPerformanceResults();
        behaviouralResultsStat = pushNotificationSettings.getBehavioralResults();
        eventsStat = pushNotificationSettings.getNewEvent();
        newslettersStat = pushNotificationSettings.getNewNewsletter();

        if (classPostStat){
            classPostOn.setChecked(true);
        } else {
            classPostOff.setChecked(true);
        }

        if (assignmentsStat){
            assignmentsOn.setChecked(true);
        } else {
            assignmentsOff.setChecked(true);
        }

        if (messagesStat){
            messagesOn.setChecked(true);
        } else {
            messagesOff.setChecked(true);
        }

        if (attendanceStat){
            attendanceOn.setChecked(true);
        } else {
            attendanceOff.setChecked(true);
        }

        if (timetableStat){
            timetableOn.setChecked(true);
        } else {
            timetableOff.setChecked(true);
        }

        if (performanceResultsStat){
            performanceResultsOn.setChecked(true);
        } else {
            performanceResultsOff.setChecked(true);
        }

        if (behaviouralResultsStat){
            behaviouralResultsOn.setChecked(true);
        } else {
            behaviouralResultsOff.setChecked(true);
        }

        if (eventsStat){
            eventsOn.setChecked(true);
        } else {
            eventsOff.setChecked(true);
        }

        if (newslettersStat){
            newslettersOn.setChecked(true);
        } else {
            newslettersOff.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
