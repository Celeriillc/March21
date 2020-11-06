package com.celerii.celerii.Activities.Settings;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ReportAProblemListActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    RadioGroup reportingGroup;
    RadioButton loginSignUp, classFeed, assignmentFeed, notification, messaging, timetable, studentPerformanceHistory,
            studentPerformanceCurrent, studentPerformancePrediction, teacherPerformance, profile, attendance, event, newsletter,
            payment, other;
    Button next;

    String featureUseKey = "";
    String featureName = "Report a Problem List";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_aproblem_list);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report a Problem");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        reportingGroup = (RadioGroup) findViewById(R.id.reportinggroup);
        loginSignUp = (RadioButton) findViewById(R.id.loginsignup);
        classFeed = (RadioButton) findViewById(R.id.classfeed);
        assignmentFeed = (RadioButton) findViewById(R.id.assignmentfeed);
        notification = (RadioButton) findViewById(R.id.notifications);
        messaging = (RadioButton) findViewById(R.id.messaging);
        timetable = (RadioButton) findViewById(R.id.timetable);
        studentPerformanceHistory = (RadioButton) findViewById(R.id.studentperformancehistory);
        studentPerformanceCurrent = (RadioButton) findViewById(R.id.studentperformancecurrent);
        studentPerformancePrediction = (RadioButton) findViewById(R.id.studentperformanceprediction);
        teacherPerformance = (RadioButton) findViewById(R.id.teacherperformance);
        profile = (RadioButton) findViewById(R.id.profile);
        attendance = (RadioButton) findViewById(R.id.attendance);
        event = (RadioButton) findViewById(R.id.event);
        newsletter = (RadioButton) findViewById(R.id.newsletter);
        payment = (RadioButton) findViewById(R.id.payment);
        other = (RadioButton) findViewById(R.id.other);
        next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(ReportAProblemListActivity.this, ReportAProblemActivity.class);
                if (loginSignUp.isChecked()){ bundle.putString("problemTitle", loginSignUp.getText().toString()); }
                else if (classFeed.isChecked()){ bundle.putString("problemTitle", classFeed.getText().toString()); }
                else if (assignmentFeed.isChecked()){ bundle.putString("problemTitle", assignmentFeed.getText().toString()); }
                else if (notification.isChecked()){ bundle.putString("problemTitle", notification.getText().toString()); }
                else if (messaging.isChecked()){ bundle.putString("problemTitle", messaging.getText().toString()); }
                else if (timetable.isChecked()){ bundle.putString("problemTitle", timetable.getText().toString()); }
                else if (studentPerformanceHistory.isChecked()){ bundle.putString("problemTitle", studentPerformanceHistory.getText().toString()); }
                else if (studentPerformanceCurrent.isChecked()){ bundle.putString("problemTitle", studentPerformanceCurrent.getText().toString()); }
                else if (studentPerformancePrediction.isChecked()){ bundle.putString("problemTitle", studentPerformancePrediction.getText().toString()); }
                else if (teacherPerformance.isChecked()){ bundle.putString("problemTitle", teacherPerformance.getText().toString()); }
                else if (profile.isChecked()){ bundle.putString("problemTitle", profile.getText().toString()); }
                else if (attendance.isChecked()){ bundle.putString("problemTitle", attendance.getText().toString()); }
                else if (event.isChecked()){ bundle.putString("problemTitle", event.getText().toString()); }
                else if (newsletter.isChecked()){ bundle.putString("problemTitle", newsletter.getText().toString()); }
                else if (payment.isChecked()){ bundle.putString("problemTitle", payment.getText().toString()); }
                else if (other.isChecked()){ bundle.putString("problemTitle", other.getText().toString()); }
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
