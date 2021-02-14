package com.celerii.celerii.Activities.StudentPerformance.Current;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.PerformanceCurrentDetailAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PerformanceCurrentDetailActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    private ArrayList<AcademicRecordStudent> academicRecordStudentList;
    public RecyclerView recyclerView;
    public PerformanceCurrentDetailAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String student, studentID, subject, term, year, subject_year_term;
    String parentActivity;
    int isNewCounter = 0;

    String featureUseKey = "";
    String featureName = "Current Academic Results Detail";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_current_detail);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        Bundle bundle = getIntent().getExtras();
        studentID = bundle.getString("Active Student");
        subject = bundle.getString("Subject");
        term = bundle.getString("Term");
        year = bundle.getString("Year");
        parentActivity = bundle.getString("parentActivity");
        subject_year_term = subject + "_" + year + "_" + term;

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail");
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

        academicRecordStudentList = new ArrayList<>();
        mAdapter = new PerformanceCurrentDetailAdapter(academicRecordStudentList, this);
        loadNewFromFirebase();
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadNewFromFirebase();
                    }
                }
        );
    }

    void loadNewFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        isNewCounter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordStudent").child(studentID).child(subject_year_term);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    academicRecordStudentList.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                        academicRecordStudent.setRecordKey(postSnapshot.getKey());

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(academicRecordStudent.getClassID());
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    academicRecordStudent.setClassName(classInstance.getClassName());
                                    academicRecordStudentList.add(academicRecordStudent);
                                }

                                if (childrenCount == academicRecordStudentList.size()){
                                    for (final AcademicRecordStudent academicRecordStudent: academicRecordStudentList) {
                                        String subject_year_term = academicRecordStudent.getSubject_AcademicYear_Term();
                                        String key = academicRecordStudent.getRecordKey();

                                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(mFirebaseUser.getUid()).child(studentID).child(subject_year_term).child(key).child("status");
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    boolean isNew = dataSnapshot.getValue(boolean.class);
                                                    if (isNew) {
                                                        academicRecordStudent.setNew(true);
                                                    } else {
                                                        academicRecordStudent.setNew(false);
                                                    }
                                                } else {
                                                    academicRecordStudent.setNew(false);
                                                }

                                                isNewCounter++;

                                                if (isNewCounter == academicRecordStudentList.size()) {
                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    int iterator = 0;
    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecord/AcademicRecordStudent").child(studentID);
        mDatabaseReference.orderByChild("subject_AcademicYear_Term").equalTo(subject_year_term).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    academicRecordStudentList.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                        academicRecordStudent.setRecordKey(postSnapshot.getKey());

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(academicRecordStudent.getClassID());
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    academicRecordStudent.setClassName(classInstance.getClassName());
                                    academicRecordStudentList.add(academicRecordStudent);
                                    mAdapter.notifyDataSetChanged();
                                }

                                if (childrenCount == academicRecordStudentList.size()){
                                    for (int i = 0; i < academicRecordStudentList.size(); i++) {
                                        AcademicRecordStudent academicRecordStudent = academicRecordStudentList.get(i);
                                        String recordKey = academicRecordStudent.getRecordKey();
                                        String class_subject_year_term = academicRecordStudent.getClassID() + "_" + subject_year_term;
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(mFirebaseUser.getUid()).child(studentID).child("subjects").child(subject).child("Class_Subject_AcademicYear_Term").child(class_subject_year_term).child("SingleRecords").child(recordKey).child("status");
                                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    boolean status = dataSnapshot.getValue(boolean.class);
                                                    if (status) {
                                                        academicRecordStudentList.get(iterator).setNew(true);
                                                    } else {
                                                        academicRecordStudentList.get(iterator).setNew(false);
                                                    }
                                                } else {
                                                    academicRecordStudentList.get(iterator).setNew(false);
                                                }

                                                iterator++;
                                                if (iterator == academicRecordStudentList.size()) {
                                                    updateBadges();
                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    iterator = 0;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("You don't have any " + subject + " results for the " + Term.Term(term) + " in " + year + ".");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        updateBadges();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateBadges() {
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
                String subject_year_term = "";
                if (academicRecordStudentList != null) {
                    for (int i = 0; i < academicRecordStudentList.size(); i++) {
                        AcademicRecordStudent academicRecordStudent = academicRecordStudentList.get(i);
                        subject_year_term = academicRecordStudent.getSubject_AcademicYear_Term();
                        String key = academicRecordStudent.getRecordKey();
                        updateBadgesMap.put("AcademicRecordParentNotification/" + mFirebaseUser.getUid() + "/" + studentID + "/" + subject_year_term + "/" + key + "/status", false);
                    }
                }
                updateBadgesMap.put("AcademicRecordParentNotification/" + mFirebaseUser.getUid() + "/" + studentID + "/" + subject_year_term + "/status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
                String subject_year_term = "";
                if (academicRecordStudentList != null) {
                    for (int i = 0; i < academicRecordStudentList.size(); i++) {
                        AcademicRecordStudent academicRecordStudent = academicRecordStudentList.get(i);
                        subject_year_term = academicRecordStudent.getSubject_AcademicYear_Term();
                        String key = academicRecordStudent.getRecordKey();
                        updateBadgesMap.put("AcademicRecordParentNotification/" + mFirebaseUser.getUid() + "/" + studentID + "/" + subject_year_term + "/" + key + "/status", false);
                    }
                }
                updateBadgesMap.put("AcademicRecordParentNotification/" + mFirebaseUser.getUid() + "/" + studentID + "/" + subject_year_term + "/status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        }

//        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
//
//        }
    }
}
