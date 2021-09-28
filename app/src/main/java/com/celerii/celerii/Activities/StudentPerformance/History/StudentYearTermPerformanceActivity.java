package com.celerii.celerii.Activities.StudentPerformance.History;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.StudentYearTermPerformanceAdapter;
import com.celerii.celerii.adapters.TeacherPerformanceRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.AcademicRecordTeacher;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentYearTermPerformanceHeader;
import com.celerii.celerii.models.StudentYearTermPerformanceModel;
import com.celerii.celerii.models.TeacherPerformanceHeader;
import com.celerii.celerii.models.TeacherPerformanceRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentYearTermPerformanceActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    private ArrayList<StudentYearTermPerformanceModel> studentYearTermPerformanceModelList;
    public RecyclerView recyclerView;
    public StudentYearTermPerformanceAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    public static String studentID = "";
    public static String studentName = "";
    public static HashMap<String, ArrayList<AcademicRecordStudent>> subjectMap = new HashMap<>();
    String year_term = "";
    String subject_year_term = "";

    StudentYearTermPerformanceHeader studentYearTermPerformanceHeader;

    String featureUseKey = "";
    String featureName = "Student Year Term Performance Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_year_term_performance);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Performance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        studentYearTermPerformanceHeader = new StudentYearTermPerformanceHeader(studentID, subjectMap);
        studentYearTermPerformanceModelList = new ArrayList<>();
        mAdapter = new StudentYearTermPerformanceAdapter(studentYearTermPerformanceModelList, studentYearTermPerformanceHeader, this);
        recyclerView.setAdapter(mAdapter);
        loadDetailsFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadDetailsFromFirebase();
                    }
                }
        );
    }

    private void loadDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(studentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String studentName = "";
                if (dataSnapshot.exists()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    studentName = student.getFirstName() + " " + student.getLastName();
                } else {
                    studentName = "Deleted Student";
                }
                toolbar.setTitle(studentName);

                for (Map.Entry<String, ArrayList<AcademicRecordStudent>> subjectEntry : subjectMap.entrySet()) {
                    String subject = subjectEntry.getKey();
                    ArrayList<AcademicRecordStudent> academicRecords = subjectEntry.getValue();
                    double subjectTermAverage = 0.0;
                    double subjectMaxScore = 0.0;
                    for (AcademicRecordStudent academicRecordStudent : academicRecords) {
                        double testClassAverage = Double.parseDouble(academicRecordStudent.getScore());
                        double maxObtainable = Double.parseDouble(academicRecordStudent.getMaxObtainable());
                        double percentageOfTotal = Double.parseDouble(academicRecordStudent.getPercentageOfTotal());
                        year_term = academicRecordStudent.getAcademicYear() + "_" + academicRecordStudent.getTerm();
                        subject_year_term = subject + "_" + academicRecordStudent.getAcademicYear() + "_" + academicRecordStudent.getTerm();

                        double normalizedTestClassAverage = (testClassAverage / maxObtainable) * percentageOfTotal;
                        double normalizedMaxObtainable = (maxObtainable / maxObtainable) * percentageOfTotal;
                        subjectTermAverage += normalizedTestClassAverage;
                        subjectMaxScore += normalizedMaxObtainable;
                    }
                    subjectTermAverage = (subjectTermAverage / subjectMaxScore) * 100;
                    StudentYearTermPerformanceModel studentYearTermPerformanceModel = new StudentYearTermPerformanceModel(String.valueOf(subjectTermAverage), subject, subject_year_term);
                    studentYearTermPerformanceModelList.add(studentYearTermPerformanceModel);
                }

                mAdapter.notifyDataSetChanged();
                mySwipeRefreshLayout.setRefreshing(false);
                progressLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}