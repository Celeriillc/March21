package com.celerii.celerii.Activities.Settings;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.PushNotificationSettings;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PushNotificationSettingsActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    PushNotificationSettings pushNotificationSettings;
    private Toolbar toolbar;
    RadioGroup classPost, assignments, messages, attendance, timetable, performanceResults, behaviouralResults, events, newsletters;
    RadioButton classPostOn, assignmentsOn, messagesOn, attendanceOn, timetableOn, performanceResultsOn, behaviouralResultsOn, eventsOn, newslettersOn;
    RadioButton classPostOff, assignmentsOff, messagesOff, attendanceOff, timetableOff, performanceResultsOff, behaviouralResultsOff, eventsOff, newslettersOff;
    boolean classPostStat, assignmentsStat, messagesStat, attendanceStat, timetableStat, performanceResultsStat, behaviouralResultsStat, eventsStat, newslettersStat;

    String featureUseKey = "";
    String featureName = "Push Notification Setting";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification_settings);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        pushNotificationSettings = new PushNotificationSettings(context);

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
    protected void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();

        pushNotificationSettings = new PushNotificationSettings(context);

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

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;

        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();
        String mFirebaseUserID = mFirebaseUser.getUid();

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        DatabaseReference featureUseUpdateRef = FirebaseDatabase.getInstance().getReference();
        featureUseUpdateRef.updateChildren(featureUseUpdateMap);
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
