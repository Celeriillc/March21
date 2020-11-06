package com.celerii.celerii.Activities.TeacherPerformance;

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
import com.celerii.celerii.adapters.TeacherPerformanceMainAdapter;
import com.celerii.celerii.adapters.TeacherPerformanceRowMain;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecord;
import com.celerii.celerii.models.AcademicRecordTeacher;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.TeacherPerformanceHeaderMain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class TeacherPerformanceActivityMain extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    private ArrayList<TeacherPerformanceRowMain> teacherPerformanceRowMainList;
    private ArrayList<AcademicRecord> academicRecordTeacherList;
    private ArrayList<String> classSubjectYearTermList;
    private TeacherPerformanceHeaderMain teacherPerformanceHeaderMain;
    public RecyclerView recyclerView;
    public TeacherPerformanceMainAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeClass = "fuk";
    String subject = "", year, term, subject_year_term;
    String currentScore, currentClassName, currentClassID, currentMaxObtainable, currentTerm, currentYear;
    String previousScore, previousClassName, previousClassID, previousMaxObtainable, previousTerm, previousYear;
    String projectedScore, projectedClassName, projectedClassID, projectedMaxObtainable, projectedTerm, projectedYear;
    String[] x = new String[0];
    Double[] y = new Double[0];
    ArrayList<String> xList = new ArrayList<>();
    ArrayList<Double> yList = new ArrayList<>();
    TeacherPerformanceRowMain teacherPerformanceRowMain;
    Double immediatePreviousScore;
    String myID;

    String featureUseKey = "";
    String featureName = "Teacher Performance Analysis Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_performance_main);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        myID = sharedPreferencesManager.getMyUserID();

        Bundle bundle = getIntent().getExtras();
        subject = bundle.getString("Subject");
        year = Date.getYear();
        term = Term.getTermShort();
        subject_year_term = subject + "_" + year + "_" + term;

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(subject);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        teacherPerformanceHeaderMain = new TeacherPerformanceHeaderMain(y, x);

        teacherPerformanceRowMainList = new ArrayList<>();
        academicRecordTeacherList = new ArrayList<>();
        classSubjectYearTermList = new ArrayList<>();
        loadNewDetailsFromFirebase();
        mAdapter = new TeacherPerformanceMainAdapter(teacherPerformanceRowMainList, teacherPerformanceHeaderMain, this);
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadNewDetailsFromFirebase();
                    }
                }
        );
    }

    int counter = 0, innerCounter = 0;
    Double overallTotal = 0.0;
    int overallCount = 0;
    double termClassAverage = 0.0;
    AcademicRecord academicRecord;

    private void loadNewDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        counter = 0;
        overallTotal = 0.0;
        overallCount = 0;
        xList = new ArrayList<>();
        yList = new ArrayList<>();
        teacherPerformanceRowMainList.clear();
        academicRecordTeacherList.clear();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTeacher").child(myID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String subject_year_term = postSnapshot.getKey();
                        String subjectKey = subject_year_term.split("_")[0];

                        if (subjectKey.equals(subject)) {
                            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTeacher").child(myID).child(subject_year_term);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    counter++;
                                    if (dataSnapshot.exists()) {
                                        termClassAverage = 0.0;
//                                        String academicYear_Term = "";

                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            academicRecord = postSnapshot.getValue(AcademicRecord.class);
                                            academicRecordTeacherList.add(academicRecord);
                                            if (!classSubjectYearTermList.contains(academicRecord.getClass_subject_AcademicYear_Term())) {
                                                classSubjectYearTermList.add(academicRecord.getClass_subject_AcademicYear_Term());
                                            }
                                        }

                                        if (counter == childrenCount) {
                                            for (String classSubjectYearTerm: classSubjectYearTermList) {
                                                termClassAverage = 0.0;
                                                String academicYear_Term = "";
                                                for (AcademicRecord academicRecordTeacher: academicRecordTeacherList) {
                                                    if (academicRecordTeacher.getClass_subject_AcademicYear_Term().equals(classSubjectYearTerm)) {
                                                        double testClassAverage = Double.valueOf(academicRecordTeacher.getClassAverage());
                                                        double maxObtainable = Double.valueOf(academicRecordTeacher.getMaxObtainable());
                                                        double percentageOfTotal = Double.valueOf(academicRecordTeacher.getPercentageOfTotal());
                                                        academicYear_Term = academicRecordTeacher.getAcademicYear_Term();

                                                        double normalizedTestClassAverage = (testClassAverage / maxObtainable) * percentageOfTotal;
                                                        termClassAverage += normalizedTestClassAverage;
                                                        academicRecord = academicRecordTeacher;
                                                    }
                                                }

//                                                xList.add(academicYear_Term);
//                                                yList.add(termClassAverage);
                                                overallTotal += termClassAverage;
                                                overallCount += 1;
                                                teacherPerformanceRowMain = new TeacherPerformanceRowMain("", academicRecord.getClassID(), academicRecord.getTeacherID(), academicRecord.getSubject(), academicRecord.getTerm(), academicRecord.getAcademicYear(),
                                                        String.valueOf(termClassAverage), "neutral");
                                                teacherPerformanceRowMainList.add(teacherPerformanceRowMain);
                                            }

                                            innerCounter = 0;
                                            for (final TeacherPerformanceRowMain teacherPerformanceRowMain: teacherPerformanceRowMainList) {
                                                mDatabaseReference = mFirebaseDatabase.getReference("Class").child(teacherPerformanceRowMain.getClassID());
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        innerCounter++;
                                                        if (dataSnapshot.exists()) {
                                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                                            teacherPerformanceRowMain.setClassName(classInstance.getClassName());
                                                        }

                                                        if (innerCounter == teacherPerformanceRowMainList.size()) {
                                                            if (teacherPerformanceRowMainList.size() > 1) {
                                                                Collections.sort(teacherPerformanceRowMainList, new Comparator<TeacherPerformanceRowMain>() {
                                                                    @Override
                                                                    public int compare(TeacherPerformanceRowMain o1, TeacherPerformanceRowMain o2) {
                                                                        return o1.getYear_term().compareTo(o2.getYear_term());
                                                                    }
                                                                });

                                                                for (int i = 0; i < teacherPerformanceRowMainList.size(); i++) {
                                                                    if (i == 0) {
                                                                        teacherPerformanceRowMainList.get(i).setIncrease("neutral");
                                                                    } else {
                                                                        double currentScore = Double.valueOf(teacherPerformanceRowMainList.get(i).getScore());
                                                                        double previousScore = Double.valueOf(teacherPerformanceRowMainList.get(i - 1).getScore());
                                                                        if (currentScore > previousScore) {
                                                                            teacherPerformanceRowMainList.get(i).setIncrease("true");
                                                                        } else if (currentScore < previousScore) {
                                                                            teacherPerformanceRowMainList.get(i).setIncrease("false");
                                                                        } else {
                                                                            teacherPerformanceRowMainList.get(i).setIncrease("neutral");
                                                                        }
                                                                    }

                                                                    xList.add(teacherPerformanceRowMainList.get(i).getYear() + "_" + teacherPerformanceRowMainList.get(i).getTerm());
                                                                    yList.add(Double.parseDouble(teacherPerformanceRowMainList.get(i).getScore()));
                                                                }
                                                            }

                                                            Collections.reverse(teacherPerformanceRowMainList);
                                                            double score = overallTotal / overallCount;
                                                            x = xList.toArray(new String[xList.size()]);
                                                            y = yList.toArray(new Double[yList.size()]);
                                                            teacherPerformanceHeaderMain.setCurrentScore(String.valueOf((int) score));
                                                            teacherPerformanceHeaderMain.setxList(x);
                                                            teacherPerformanceHeaderMain.setyList(y);
                                                            teacherPerformanceRowMainList.add(0, new TeacherPerformanceRowMain());
                                                            mAdapter.notifyDataSetChanged();
                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                            progressLayout.setVisibility(View.GONE);
                                                            recyclerView.setVisibility(View.VISIBLE);
                                                            errorLayout.setVisibility(View.GONE);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            counter++;

                            if (counter == childrenCount) {
                                if (teacherPerformanceRowMainList.size() > 1) {
                                    Collections.sort(teacherPerformanceRowMainList, new Comparator<TeacherPerformanceRowMain>() {
                                        @Override
                                        public int compare(TeacherPerformanceRowMain o1, TeacherPerformanceRowMain o2) {
                                            return o1.getYear_term().compareTo(o2.getYear_term());
                                        }
                                    });

                                    for (int i = 0; i < teacherPerformanceRowMainList.size(); i++) {
                                        if (i == 0) {
                                            teacherPerformanceRowMainList.get(i).setIncrease("neutral");
                                        } else {
                                            double currentScore = Double.valueOf(teacherPerformanceRowMainList.get(i).getScore());
                                            double previousScore = Double.valueOf(teacherPerformanceRowMainList.get(i - 1).getScore());
                                            if (currentScore > previousScore) {
                                                teacherPerformanceRowMainList.get(i).setIncrease("true");
                                            } else if (currentScore < previousScore) {
                                                teacherPerformanceRowMainList.get(i).setIncrease("false");
                                            } else {
                                                teacherPerformanceRowMainList.get(i).setIncrease("neutral");
                                            }
                                        }
                                    }
                                }

                                Collections.reverse(teacherPerformanceRowMainList);
                                double score = overallTotal / overallCount;
                                x = xList.toArray(new String[xList.size()]);
                                y = yList.toArray(new Double[yList.size()]);
                                teacherPerformanceHeaderMain.setCurrentScore(String.valueOf((int)score));
                                teacherPerformanceHeaderMain.setxList(x);
                                teacherPerformanceHeaderMain.setyList(y);
                                teacherPerformanceRowMainList.add(0, new TeacherPerformanceRowMain());
                                mAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                                progressLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                errorLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("You don't have any academic history yet. To get started, post academic results for any class");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void loadNewDetailsFromFirebase() {
//        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
//            mySwipeRefreshLayout.setRefreshing(false);
//            recyclerView.setVisibility(View.GONE);
//            progressLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
//            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
//            return;
//        }
//
//        counter = 0;
//        overallTotal = 0.0;
//        overallCount = 0;
//        xList = new ArrayList<>();
//        yList = new ArrayList<>();
//        teacherPerformanceRowMainList.clear();
//
//        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTeacher").child(myID);
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
//                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                        String subject_year_term = postSnapshot.getKey();
//                        String subjectKey = subject_year_term.split("_")[0];
//
//                        if (subjectKey.equals(subject)) {
//                            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTeacher").child(myID).child(subject_year_term);
//                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    counter++;
//                                    if (dataSnapshot.exists()) {
//                                        termClassAverage = 0.0;
//                                        String academicYear_Term = "";
//
//                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                            academicRecord = postSnapshot.getValue(AcademicRecord.class);
//                                            double testClassAverage = Double.valueOf(academicRecord.getClassAverage());
//                                            double maxObtainable = Double.valueOf(academicRecord.getMaxObtainable());
//                                            double percentageOfTotal = Double.valueOf(academicRecord.getPercentageOfTotal());
//                                            academicYear_Term = academicRecord.getAcademicYear_Term();
//
//                                            double normalizedTestClassAverage = (testClassAverage / maxObtainable) * percentageOfTotal;
//                                            termClassAverage += normalizedTestClassAverage;
//                                        }
//
//                                        xList.add(academicYear_Term);
//                                        yList.add(termClassAverage);
//                                        overallTotal += termClassAverage;
//                                        overallCount += 1;
//
//                                        teacherPerformanceRowMain = new TeacherPerformanceRowMain("", academicRecord.getClassID(), academicRecord.getTeacherID(), academicRecord.getSubject(), academicRecord.getTerm(), academicRecord.getAcademicYear(),
//                                                String.valueOf(termClassAverage), "neutral");
//                                        teacherPerformanceRowMainList.add(teacherPerformanceRowMain);
//
//                                        if (counter == childrenCount) {
//                                            innerCounter = 0;
//                                            for (final TeacherPerformanceRowMain teacherPerformanceRowMain: teacherPerformanceRowMainList) {
//                                                mDatabaseReference = mFirebaseDatabase.getReference("Class").child(teacherPerformanceRowMain.getClassID());
//                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                                        innerCounter++;
//                                                        if (dataSnapshot.exists()) {
//                                                            Class classInstance = dataSnapshot.getValue(Class.class);
//                                                            teacherPerformanceRowMain.setClassName(classInstance.getClassName());
//                                                        }
//
//                                                        if (innerCounter == teacherPerformanceRowMainList.size()) {
//                                                            if (teacherPerformanceRowMainList.size() > 1) {
//                                                                Collections.sort(teacherPerformanceRowMainList, new Comparator<TeacherPerformanceRowMain>() {
//                                                                    @Override
//                                                                    public int compare(TeacherPerformanceRowMain o1, TeacherPerformanceRowMain o2) {
//                                                                      return o1.getYear_term().compareTo(o2.getYear_term());
//                                                                    }
//                                                                });
//
//                                                                for (int i = 0; i < teacherPerformanceRowMainList.size(); i++) {
//                                                                    if (i == 0) {
//                                                                        teacherPerformanceRowMainList.get(i).setIncrease("neutral");
//                                                                    } else {
//                                                                        double currentScore = Double.valueOf(teacherPerformanceRowMainList.get(i).getScore());
//                                                                        double previousScore = Double.valueOf(teacherPerformanceRowMainList.get(i - 1).getScore());
//                                                                        if (currentScore > previousScore) {
//                                                                            teacherPerformanceRowMainList.get(i).setIncrease("true");
//                                                                        } else if (currentScore < previousScore) {
//                                                                            teacherPerformanceRowMainList.get(i).setIncrease("false");
//                                                                        } else {
//                                                                            teacherPerformanceRowMainList.get(i).setIncrease("neutral");
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            Collections.reverse(teacherPerformanceRowMainList);
//                                                            double score = overallTotal / overallCount;
//                                                            x = xList.toArray(new String[xList.size()]);
//                                                            y = yList.toArray(new Double[yList.size()]);
//                                                            teacherPerformanceHeaderMain.setCurrentScore(String.valueOf((int) score));
//                                                            teacherPerformanceHeaderMain.setxList(x);
//                                                            teacherPerformanceHeaderMain.setyList(y);
//                                                            teacherPerformanceRowMainList.add(0, new TeacherPerformanceRowMain());
//                                                            mAdapter.notifyDataSetChanged();
//                                                            mySwipeRefreshLayout.setRefreshing(false);
//                                                            progressLayout.setVisibility(View.GONE);
//                                                            recyclerView.setVisibility(View.VISIBLE);
//                                                            errorLayout.setVisibility(View.GONE);
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(DatabaseError databaseError) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        } else {
//                            counter++;
//
//                            if (counter == childrenCount) {
//                                if (teacherPerformanceRowMainList.size() > 1) {
//                                    Collections.sort(teacherPerformanceRowMainList, new Comparator<TeacherPerformanceRowMain>() {
//                                        @Override
//                                        public int compare(TeacherPerformanceRowMain o1, TeacherPerformanceRowMain o2) {
//                                            return o1.getYear_term().compareTo(o2.getYear_term());
//                                        }
//                                    });
//
//                                    for (int i = 0; i < teacherPerformanceRowMainList.size(); i++) {
//                                        if (i == 0) {
//                                            teacherPerformanceRowMainList.get(i).setIncrease("neutral");
//                                        } else {
//                                            double currentScore = Double.valueOf(teacherPerformanceRowMainList.get(i).getScore());
//                                            double previousScore = Double.valueOf(teacherPerformanceRowMainList.get(i - 1).getScore());
//                                            if (currentScore > previousScore) {
//                                                teacherPerformanceRowMainList.get(i).setIncrease("true");
//                                            } else if (currentScore < previousScore) {
//                                                teacherPerformanceRowMainList.get(i).setIncrease("false");
//                                            } else {
//                                                teacherPerformanceRowMainList.get(i).setIncrease("neutral");
//                                            }
//                                        }
//                                    }
//                                }
//
//                                Collections.reverse(teacherPerformanceRowMainList);
//                                double score = overallTotal / overallCount;
//                                x = xList.toArray(new String[xList.size()]);
//                                y = yList.toArray(new Double[yList.size()]);
//                                teacherPerformanceHeaderMain.setCurrentScore(String.valueOf((int)score));
//                                teacherPerformanceHeaderMain.setxList(x);
//                                teacherPerformanceHeaderMain.setyList(y);
//                                teacherPerformanceRowMainList.add(0, new TeacherPerformanceRowMain());
//                                mAdapter.notifyDataSetChanged();
//                                mySwipeRefreshLayout.setRefreshing(false);
//                                progressLayout.setVisibility(View.GONE);
//                                recyclerView.setVisibility(View.VISIBLE);
//                                errorLayout.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//                } else {
//                    mySwipeRefreshLayout.setRefreshing(false);
//                    recyclerView.setVisibility(View.GONE);
//                    progressLayout.setVisibility(View.GONE);
//                    errorLayout.setVisibility(View.VISIBLE);
//                    errorLayoutText.setText("You don't have any academic history yet. To get started, post academic results for any class");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
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
}
