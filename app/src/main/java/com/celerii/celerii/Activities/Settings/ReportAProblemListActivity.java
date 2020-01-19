package com.celerii.celerii.Activities.Settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;

public class ReportAProblemListActivity extends AppCompatActivity {

    Toolbar toolbar;
    RadioGroup reportingGroup;
    RadioButton loginSignUp, classFeed, assignmentFeed, notification, messaging, timetable, studentPerformanceHistory,
            studentPerformanceCurrent, studentPerformancePrediction, teacherPerformance, profile, attendance, event, newsletter,
            payment, other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_aproblem_list);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report a Problem");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_a_problem_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_next){
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

        return super.onOptionsItemSelected(item);
    }
}
