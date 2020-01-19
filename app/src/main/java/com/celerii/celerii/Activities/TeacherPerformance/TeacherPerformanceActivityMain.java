package com.celerii.celerii.Activities.TeacherPerformance;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TeacherPerformanceMainAdapter;
import com.celerii.celerii.adapters.TeacherPerformanceRowMain;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecordTeacher;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.TeacherPerformanceHeaderMain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TeacherPerformanceActivityMain extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    private ArrayList<TeacherPerformanceRowMain> teacherPerformanceRowMainList;
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

//        String[] x = new String[] {"one", "two", "three", "four", "five", "six" };
//        Double[] y = new Double[] {40.0, 50.0, 80.0, 60.0, 50.0, 100.0 };

        teacherPerformanceHeaderMain = new TeacherPerformanceHeaderMain(y, x);

//        teacherPerformanceHeaderMain = new TeacherPerformanceHeaderMain("89", "94", "93", "Toulouse", "Mar 32, 2023", "12:23:45 PM", "Second Term",
//                                        "2023", "Lille", "Jul 12, 2023", "12:23:45 PM", "Third Term", "2023", "N/A", "N/A", "N/A", "N/A", "N/A", y, x);

        teacherPerformanceRowMainList = new ArrayList<>();
        loadDetailsFromFirebase();
        mAdapter = new TeacherPerformanceMainAdapter(teacherPerformanceRowMainList, teacherPerformanceHeaderMain, this);
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadDetailsFromFirebase();
                    }
                }
        );
    }

    int total = 0;
    int counter = 0;
    int average = 0;
    private void loadDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordTeacher").child(myID);
        mDatabaseReference.orderByChild("subject").equalTo(subject).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    teacherPerformanceRowMainList.clear();
                    xList.clear();
                    yList.clear();
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    immediatePreviousScore = 0.0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        final AcademicRecordTeacher academicRecord = postSnapshot.getValue(AcademicRecordTeacher.class);
                        final String classID = academicRecord.getClassID();

                        total += Double.valueOf(academicRecord.getClassAverage());
                        counter += 1;

                        mDatabaseReference = mFirebaseDatabase.getReference("Class/" + classID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    Double currentScore = Double.valueOf(academicRecord.getClassAverage());
                                    boolean flag;
                                    if (currentScore >= immediatePreviousScore) {
                                        flag = true;
                                    } else {
                                        flag = false;
                                    }
                                    teacherPerformanceRowMain = new TeacherPerformanceRowMain(classInstance.getClassName(), classID, academicRecord.getTeacherID(), academicRecord.getSubject(), academicRecord.getTerm(), academicRecord.getAcademicYear(),
                                            academicRecord.getClassAverage(), flag);
                                    immediatePreviousScore = currentScore;

                                    teacherPerformanceRowMainList.add(teacherPerformanceRowMain);
                                    xList.add(academicRecord.getAcademicYear_Term());
                                    yList.add(Double.valueOf(academicRecord.getClassAverage()));
                                }

                                if (childrenCount == teacherPerformanceRowMainList.size()){

                                    if (teacherPerformanceRowMainList.size() > 1) {
                                        for (int i = 0; i < teacherPerformanceRowMainList.size(); i++) {
                                            teacherPerformanceRowMain = teacherPerformanceRowMainList.get(i);
                                            if (teacherPerformanceRowMain.getTerm().equals("1")) {
                                                teacherPerformanceRowMain.setTerm("10");
                                            }
                                        }
                                        Collections.sort(teacherPerformanceRowMainList, new Comparator<TeacherPerformanceRowMain>() {
                                            @Override
                                            public int compare(TeacherPerformanceRowMain o1, TeacherPerformanceRowMain o2) {
                                                int value1 = o1.getYear().compareTo(o2.getYear());
                                                if (value1 == 0) {
                                                    return o1.getTerm().compareTo(o2.getTerm());
                                                } else {
                                                    return value1;
                                                }
                                            }
                                        });

                                        for (int i = 0; i < teacherPerformanceRowMainList.size(); i++) {
                                            teacherPerformanceRowMain = teacherPerformanceRowMainList.get(i);
                                            if (teacherPerformanceRowMain.getTerm().equals("10")) {
                                                teacherPerformanceRowMain.setTerm("1");
                                            }
                                        }
                                    }

                                    Collections.reverse(teacherPerformanceRowMainList);

                                    average = total / counter;
                                    x = xList.toArray(new String[xList.size()]);
                                    y = yList.toArray(new Double[yList.size()]);
                                    teacherPerformanceHeaderMain.setCurrentScore(String.valueOf(average));
                                    teacherPerformanceHeaderMain.setxList(x);
                                    teacherPerformanceHeaderMain.setyList(y);
                                    teacherPerformanceRowMainList.add(0, new TeacherPerformanceRowMain());
                                    mAdapter.notifyDataSetChanged();
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    progressLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    errorLayout.setVisibility(View.GONE);
//                                    loadHeader();
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

    void loadHeader(){
        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordTeacher").child(myID);
        mDatabaseReference.orderByChild("subject_AcademicYear_Term").equalTo(subject_year_term).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int latestDate = 0;
                loadUpCurrentEmpty();
                loadUpPreviousEmpty();
                loadUpProjectedEmpty();
                if (dataSnapshot.exists()){








                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        AcademicRecordTeacher academicRecordTeacher = postSnapshot.getValue(AcademicRecordTeacher.class);
                        int sortableDate = TypeConverterClass.convStringToInt(academicRecordTeacher.getSortableDate());
                        if (sortableDate > latestDate) {
                            currentClassID = academicRecordTeacher.getClassID();
                            currentScore = TypeConverterClass.convStringToIntString(academicRecordTeacher.getClassAverage());
                            currentTerm = academicRecordTeacher.getTerm();
                            currentYear = academicRecordTeacher.getAcademicYear();
                            currentMaxObtainable = TypeConverterClass.convStringToIntString(academicRecordTeacher.getMaxObtainable()) + "%";
                        }
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordTeacher").child(myID);
                mDatabaseReference.orderByChild("subject").equalTo(subject).limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String key = postSnapshot.getKey();
                                if (!subject_year_term.equals(key)) {
                                    AcademicRecordTeacher academicRecordTeacherPrev = postSnapshot.getValue(AcademicRecordTeacher.class);
                                    previousClassID = academicRecordTeacherPrev.getClassID();
                                    previousScore = TypeConverterClass.convStringToIntString(academicRecordTeacherPrev.getClassAverage());
                                    previousTerm = academicRecordTeacherPrev.getTerm();
                                    previousYear = academicRecordTeacherPrev.getAcademicYear();
                                    previousMaxObtainable = TypeConverterClass.convStringToIntString(academicRecordTeacherPrev.getMaxObtainable()) + "%";
                                }
                            }
                        }

                        mDatabaseReference = mFirebaseDatabase.getReference("Class/" + currentClassID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    currentClassName = classInstance.getClassName();
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference("Class/" + previousClassID);
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                            previousClassName = classInstance.getClassName();
                                        }

                                        loadUpToView();
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        progressLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        errorLayout.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
























//
//        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordTeacher").child(myID).child(subject_year_term);
//        mDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    final AcademicRecordTeacher academicRecordTeacher = dataSnapshot.getValue(AcademicRecordTeacher.class);
//
//                    currentClassID = academicRecordTeacher.getClassID();
//                    currentScore = String.valueOf(Double.valueOf(academicRecordTeacher.getClassAverage()).intValue());
//                    currentTerm = academicRecordTeacher.getTerm();
//                    currentYear = academicRecordTeacher.getAcademicYear();
//                    currentMaxObtainable = String.valueOf((Double.valueOf(academicRecordTeacher.getMaxObtainable())).intValue()) + "%";
//                    previousClassName = "Not Available";
//                    previousScore = "NA";
//                    previousTerm = "Not Available";
//                    previousYear = "Not Available";
//                    previousMaxObtainable = "Not Available";
//                    projectedScore = "NA";
//                    projectedTerm = "Not Available";
//                    projectedYear = "Not Available";
//                    projectedClassName = "Not Available";
//                    projectedMaxObtainable = "Not Available";
//                } else {
//                    currentClassName = "Not Available";
//                    currentScore = "NA";
//                    currentTerm = "Not Available";
//                    currentYear = "Not Available";
//                    currentMaxObtainable = "Not Available";
//                    previousClassName = "Not Available";
//                    previousScore = "NA";
//                    previousTerm = "Not Available";
//                    previousYear = "Not Available";
//                    previousMaxObtainable = "Not Available";
//                    projectedScore = "NA";
//                    projectedTerm = "Not Available";
//                    projectedYear = "Not Available";
//                    projectedClassName = "Not Available";
//                    projectedMaxObtainable = "Not Available";
//                }
//
//                mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordTeacher").child(myID);
//                mDatabaseReference.orderByChild("subject").equalTo(subject).limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                                String key = postSnapshot.getKey();
//                                if (!subject_year_term.equals(key)){
//                                    AcademicRecordTeacher academicRecordTeacherPrev = postSnapshot.getValue(AcademicRecordTeacher.class);
//                                    previousClassID = academicRecordTeacherPrev.getClassID();
//                                    previousScore = String.valueOf(Double.valueOf(academicRecordTeacherPrev.getClassAverage()).intValue());
//                                    previousTerm = academicRecordTeacherPrev.getTerm();
//                                    previousYear = academicRecordTeacherPrev.getAcademicYear();
//                                    previousMaxObtainable = String.valueOf((Double.valueOf(academicRecordTeacherPrev.getMaxObtainable())).intValue()) + "%";
//                                }
//                                break;
//                            }
//                        }
//
//                        mDatabaseReference = mFirebaseDatabase.getReference("Class/" + currentClassID);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()){
//                                    Class classInstance = dataSnapshot.getValue(Class.class);
//                                    currentClassName = classInstance.getClassName();
//                                }
//
//                                mDatabaseReference = mFirebaseDatabase.getReference("Class/" + previousClassID);
//                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot.exists()){
//                                            Class classInstance = dataSnapshot.getValue(Class.class);
//                                            previousClassName = classInstance.getClassName();
//                                        }
//
//                                        teacherPerformanceHeaderMain.setCurrentClass(currentClassName);
//                                        teacherPerformanceHeaderMain.setCurrentScore(currentScore);
//                                        teacherPerformanceHeaderMain.setCurrentTerm(currentTerm);
//                                        teacherPerformanceHeaderMain.setCurrentYear(currentYear);
//                                        teacherPerformanceHeaderMain.setCurrentMaxObtainable(currentMaxObtainable);
//                                        teacherPerformanceHeaderMain.setPreviousClass(previousClassName);
//                                        teacherPerformanceHeaderMain.setPreviousScore(previousScore);
//                                        teacherPerformanceHeaderMain.setPreviousTerm(previousTerm);
//                                        teacherPerformanceHeaderMain.setPreviousYear(previousYear);
//                                        teacherPerformanceHeaderMain.setPreviousMaxObtainable(previousMaxObtainable);
//                                        teacherPerformanceHeaderMain.setProjectedClass(projectedClassName);
//                                        teacherPerformanceHeaderMain.setProjectedScore(projectedScore);
//                                        teacherPerformanceHeaderMain.setProjectedTerm(projectedTerm);
//                                        teacherPerformanceHeaderMain.setProjectedYear(projectedYear);
//                                        teacherPerformanceHeaderMain.setProjectedMaxObtainable(projectedMaxObtainable);
//                                        mAdapter.notifyDataSetChanged();
//
//                                        mySwipeRefreshLayout.setRefreshing(false);
//                                        progressLayout.setVisibility(View.GONE);
//                                        recyclerView.setVisibility(View.VISIBLE);
//                                        errorLayout.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//
//        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordTeacher").child(myID);
//        mDatabaseReference.orderByChild("subject").equalTo(subject).limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    final int childrenCountHeader = (int) dataSnapshot.getChildrenCount();
//                    Integer counter = 0;
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        final AcademicRecordTeacher academicRecordTeacher = postSnapshot.getValue(AcademicRecordTeacher.class);
//                        String date = academicRecordTeacher.getDate().split(" ")[0];
//                        String[] dateArray = date.split("/");
//                        time = academicRecordTeacher.getDate().split(" ")[1];
//                        String[] timeArray = time.split(":");
//                        final String year = dateArray[0];
//                        final String month = Month.Month(Integer.valueOf(dateArray[1]) - 1);
//                        final String day = dateArray[2];
//                        String hour = timeArray[0];
//                        String minute = timeArray[1];
//                        String sec = timeArray[2];
//                        if (Integer.valueOf(hour) > 12){
//                            hour = String.valueOf(Integer.valueOf(hour) - 12);
//                            time = hour + ":" + minute + ":" + sec + " PM";
//                        } else{
//                            time = hour + ":" + minute + ":" + sec + " AM";
//                        }
//
//                        if (counter == 0){
//                            String classID = academicRecordTeacher.getClassID();
//                            mDatabaseReference = mFirebaseDatabase.getReference("Class/" + classID);
//                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//                                        Class classInstance = dataSnapshot.getValue(Class.class);
//                                        currentClassName = classInstance.getClassName();
//                                        currentScore = String.valueOf(Double.valueOf(academicRecordTeacher.getClassAverage()).intValue());
//                                        currentTerm = academicRecordTeacher.getTerm();
//                                        currentYear = academicRecordTeacher.getAcademicYear();
//                                        currentDate = month + " " + day + ", " + year;
//                                        currentTime = time;
//                                        previousScore = "NA";
//                                        previousTerm = "Not Available";
//                                        previousYear = "Not Available";
//                                        previousDate = "Not Available";
//                                        previousClassName = "Not Available";
//                                        previousTime = "Not Available";
//                                        projectedScore = "NA";
//                                        projectedTerm = "Not Available";
//                                        projectedYear = "Not Available";
//                                        projectedDate = "Not Available";
//                                        projectedClassName = "Not Available";
//                                        projectedTime = "Not Available";
//
//                                        teacherPerformanceHeaderMain.setCurrentClass(currentClassName);
//                                        teacherPerformanceHeaderMain.setCurrentScore(currentScore);
//                                        teacherPerformanceHeaderMain.setCurrentTerm(currentTerm);
//                                        teacherPerformanceHeaderMain.setCurrentYear(currentYear);
//                                        teacherPerformanceHeaderMain.setCurrentTime(currentTime);
//                                        teacherPerformanceHeaderMain.setCurrentDate(currentDate);
//                                        teacherPerformanceHeaderMain.setPreviousClass(previousClassName);
//                                        teacherPerformanceHeaderMain.setPreviousScore(previousScore);
//                                        teacherPerformanceHeaderMain.setPreviousTerm(previousTerm);
//                                        teacherPerformanceHeaderMain.setPreviousYear(previousYear);
//                                        teacherPerformanceHeaderMain.setPreviousTime(previousTime);
//                                        teacherPerformanceHeaderMain.setPreviousDate(previousDate);
//                                        teacherPerformanceHeaderMain.setProjectedClass(projectedClassName);
//                                        teacherPerformanceHeaderMain.setProjectedScore(projectedScore);
//                                        teacherPerformanceHeaderMain.setProjectedTerm(projectedTerm);
//                                        teacherPerformanceHeaderMain.setProjectedYear(projectedYear);
//                                        teacherPerformanceHeaderMain.setProjectedTime(projectedTime);
//                                        teacherPerformanceHeaderMain.setProjectedDate(projectedDate);
//                                        mAdapter.notifyDataSetChanged();
//
//                                        if (childrenCountHeader == 1){
//                                            mySwipeRefreshLayout.setRefreshing(false);
//                                            progressLayout.setVisibility(View.GONE);
//                                            recyclerView.setVisibility(View.VISIBLE);
//                                            errorLayout.setVisibility(View.GONE);
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                        } else {
//                            String classID = academicRecordTeacher.getClassID();
//                            mDatabaseReference = mFirebaseDatabase.getReference("Class/" + classID);
//                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//                                        Class classInstance = dataSnapshot.getValue(Class.class);
//                                        previousScore = currentScore;
//                                        previousTerm = currentTerm;
//                                        previousYear = currentYear;
//                                        previousDate = currentDate;
//                                        previousClassName = currentTime;
//                                        previousTime = currentClassName;
//                                        currentClassName = classInstance.getClassName();
//                                        currentScore = String.valueOf(Double.valueOf(academicRecordTeacher.getClassAverage()).intValue());
//                                        currentTerm = academicRecordTeacher.getTerm();
//                                        currentYear = academicRecordTeacher.getAcademicYear();
//                                        currentDate = month + " " + day + ", " + year;
//                                        currentTime = time;
//                                        projectedScore = "NA";
//                                        projectedTerm = "Not Available";
//                                        projectedYear = "Not Available";
//                                        projectedDate = "Not Available";
//                                        projectedClassName = "Not Available";
//                                        projectedTime = "Not Available";
//
//                                        teacherPerformanceHeaderMain.setCurrentClass(currentClassName);
//                                        teacherPerformanceHeaderMain.setCurrentScore(currentScore);
//                                        teacherPerformanceHeaderMain.setCurrentTerm(currentTerm);
//                                        teacherPerformanceHeaderMain.setCurrentYear(currentYear);
//                                        teacherPerformanceHeaderMain.setCurrentTime(currentTime);
//                                        teacherPerformanceHeaderMain.setCurrentDate(currentDate);
//                                        teacherPerformanceHeaderMain.setPreviousClass(previousClassName);
//                                        teacherPerformanceHeaderMain.setPreviousScore(previousScore);
//                                        teacherPerformanceHeaderMain.setPreviousTerm(previousTerm);
//                                        teacherPerformanceHeaderMain.setPreviousYear(previousYear);
//                                        teacherPerformanceHeaderMain.setPreviousTime(previousTime);
//                                        teacherPerformanceHeaderMain.setPreviousDate(previousDate);
//                                        teacherPerformanceHeaderMain.setProjectedClass(projectedClassName);
//                                        teacherPerformanceHeaderMain.setProjectedScore(projectedScore);
//                                        teacherPerformanceHeaderMain.setProjectedTerm(projectedTerm);
//                                        teacherPerformanceHeaderMain.setProjectedYear(projectedYear);
//                                        teacherPerformanceHeaderMain.setProjectedTime(projectedTime);
//                                        teacherPerformanceHeaderMain.setProjectedDate(projectedDate);
//                                        mAdapter.notifyDataSetChanged();
//
//                                        if (childrenCountHeader == 2){
//                                            mySwipeRefreshLayout.setRefreshing(false);
//                                            progressLayout.setVisibility(View.GONE);
//                                            recyclerView.setVisibility(View.VISIBLE);
//                                            errorLayout.setVisibility(View.GONE);
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                        counter++;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadUpCurrentEmpty(){
        currentClassName = "Not Available";
        currentScore = "NA";
        currentTerm = "Not Available";
        currentYear = "Not Available";
        currentMaxObtainable = "Not Available";
    }

    private void loadUpPreviousEmpty(){
        previousClassName = "Not Available";
        previousScore = "NA";
        previousTerm = "Not Available";
        previousYear = "Not Available";
        previousMaxObtainable = "Not Available";
    }

    private void loadUpProjectedEmpty(){
        projectedScore = "NA";
        projectedTerm = "Not Available";
        projectedYear = "Not Available";
        projectedClassName = "Not Available";
        projectedMaxObtainable = "Not Available";
    }

    private void loadUpToView(){
        teacherPerformanceHeaderMain.setCurrentClass(currentClassName);
        teacherPerformanceHeaderMain.setCurrentScore(currentScore);
        teacherPerformanceHeaderMain.setCurrentTerm(currentTerm);
        teacherPerformanceHeaderMain.setCurrentYear(currentYear);
        teacherPerformanceHeaderMain.setCurrentMaxObtainable(currentMaxObtainable);
        teacherPerformanceHeaderMain.setPreviousClass(previousClassName);
        teacherPerformanceHeaderMain.setPreviousScore(previousScore);
        teacherPerformanceHeaderMain.setPreviousTerm(previousTerm);
        teacherPerformanceHeaderMain.setPreviousYear(previousYear);
        teacherPerformanceHeaderMain.setPreviousMaxObtainable(previousMaxObtainable);
        teacherPerformanceHeaderMain.setProjectedClass(projectedClassName);
        teacherPerformanceHeaderMain.setProjectedScore(projectedScore);
        teacherPerformanceHeaderMain.setProjectedTerm(projectedTerm);
        teacherPerformanceHeaderMain.setProjectedYear(projectedYear);
        teacherPerformanceHeaderMain.setProjectedMaxObtainable(projectedMaxObtainable);
        mAdapter.notifyDataSetChanged();
    }
}
