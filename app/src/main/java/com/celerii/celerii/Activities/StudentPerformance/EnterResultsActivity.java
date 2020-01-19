package com.celerii.celerii.Activities.StudentPerformance;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
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
import com.celerii.celerii.adapters.EnterResultAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TeacherEnterResultsSharedPreferences;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecord;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.EnterResultHeader;
import com.celerii.celerii.models.EnterResultRow;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class EnterResultsActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    TeacherEnterResultsSharedPreferences teacherEnterResultsSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    Toolbar toolbar;
    private ArrayList<EnterResultRow> enterResultRowList;
    private ArrayList<String> parentList;
    private HashMap<String, ArrayList<String>> studentParentList = new HashMap<String, ArrayList<String>>();
    private EnterResultHeader enterResultHeader;
    public RecyclerView recyclerView;
    public EnterResultAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    String childName;
    String childImageURL;
    String activeClass = "";
    String myName, myID, className, activeSchoolID, subject, testType, maximumScore, percentageOfTotal, term, date, dateForAdapter, sortableDate;
    String classID, teacherID, year, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear, class_subject_AcademicYear_Term, class_subject_Term_AcademicYear;
    Double newScore, score, classAverage = 0.0;
    Double previousPercentageOfTotal = 0.0;
    String pushID, uniquePushID;
    boolean connected = true;

    Map<String, Object> newResultEntry;
    Map<String, String> existingStudentScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        teacherEnterResultsSharedPreferences = new TeacherEnterResultsSharedPreferences(this);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        activeClass = sharedPreferencesManager.getActiveClass();
        if (activeClass == null) {
            Set<String> classSet = sharedPreferencesManager.getMyClasses();
            ArrayList<String> classes = new ArrayList<>();
            if (classSet != null) {
                classes = new ArrayList<>(classSet);
                activeClass = classes.get(0);
                sharedPreferencesManager.setActiveClass(activeClass);
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                return;
            }
        }

        activeClass = sharedPreferencesManager.getActiveClass().split(" ")[0];
        className = sharedPreferencesManager.getActiveClass().split(" ")[1];
        myName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        myID = sharedPreferencesManager.getMyUserID();

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New " + className + " Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        enterResultHeader = new EnterResultHeader();
        enterResultRowList = new ArrayList<>();
        parentList = new ArrayList<>();
        studentParentList = new HashMap<String, ArrayList<String>>();
        enterResultRowList.add(new EnterResultRow());
        mAdapter = new EnterResultAdapter(enterResultRowList, enterResultHeader, this, this);
        loadHeaderFromFirebase();
        loadDetailsFromFirebase();
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadHeaderFromFirebase();
                        loadDetailsFromFirebase();
                    }
                }
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Date Information"));
    }

    private void loadHeaderFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        subject = teacherEnterResultsSharedPreferences.getSubject();
        date = Date.getDate();
        sortableDate = Date.convertToSortableDate(date);
        testType = "Continous Assessment";
        maximumScore = "100";
        percentageOfTotal = "30";
        year = Date.getYear();
        term = Term.getTermShort();
        academicYear_Term = year + "_" + term;
        term_AcademicYear = term + "_" + year;
        subject_AcademicYear_Term = subject + "_" + year + "_" + term;
        subject_Term_AcademicYear = subject + "_" + term + "_" + year;
        class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
        class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;

        enterResultHeader.setSubject(subject);
        enterResultHeader.setDate(date);
        enterResultHeader.setSortableDate(sortableDate);
        enterResultHeader.setTestType(testType);
        enterResultHeader.setMaxScore(maximumScore);
        enterResultHeader.setPercentageOfTotal(percentageOfTotal);
        enterResultHeader.setYear(year);
        enterResultHeader.setMonth(Date.getMonth());
        enterResultHeader.setDay(Date.getDay());
        enterResultHeader.setTerm(term);
        enterResultHeader.setClassID(activeClass);
        enterResultHeader.setClassName(className);
        enterResultHeader.setTeacherID(myID);
        enterResultHeader.setTeacher(myName);
        enterResultHeader.setPreviousPercentageOfTotal(previousPercentageOfTotal);
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordClass").child(activeClass).child(class_subject_AcademicYear_Term).child("percentageOfTotal");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    previousPercentageOfTotal = Double.valueOf(dataSnapshot.getValue(String.class));
                } else {
                    previousPercentageOfTotal = 0.0;
                }

                if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                    percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
                }

                enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                enterResultHeader.setPreviousPercentageOfTotal(previousPercentageOfTotal);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("Class School/" + activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        activeSchoolID = postSnapshot.getKey();
                        enterResultHeader.setSchoolID(activeSchoolID);
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Class Students/" + activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String studentKey = postSnapshot.getKey();

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Student Parent/" + studentKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        String parentKey = postSnapshot.getKey();
                                        try {
                                            studentParentList.get(studentKey).add(parentKey);
                                        } catch (Exception e) {
                                            studentParentList.put(studentKey, new ArrayList<String>());
                                            studentParentList.get(studentKey).add(parentKey);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference("Class Students/" + activeClass);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            enterResultRowList.clear();
                            final int childrenCount = (int) dataSnapshot.getChildrenCount();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String childKey = postSnapshot.getKey();

                                DatabaseReference childDatabaseReference = mFirebaseDatabase.getReference("Student/" + childKey);
                                childDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Student child = dataSnapshot.getValue(Student.class);
                                        childName = child.getFirstName() + " " + child.getLastName();
                                        childImageURL = child.getImageURL();

                                        EnterResultRow enterResultRow = new EnterResultRow(childName, childImageURL, "");
                                        enterResultRow.setStudentID(dataSnapshot.getKey());
                                        enterResultRowList.add(enterResultRow);

                                        if (childrenCount == enterResultRowList.size()){
                                            enterResultRowList.add(0, new EnterResultRow());
                                            enterResultRowList.add(new EnterResultRow());
                                            recyclerView.setVisibility(View.VISIBLE);
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            mAdapter.notifyDataSetChanged();
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
                            errorLayoutText.setText("This class doesn't contain any students");
                        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                subject = data.getStringExtra("Selected Subject");
                subject_AcademicYear_Term = subject + "_" + year + "_" + term;
                subject_Term_AcademicYear = subject + "_" + term + "_" + year;
                class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
                class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;
                updatePercentageOfTotal(class_subject_AcademicYear_Term);
                enterResultHeader.setSubject(subject);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                testType = data.getStringExtra("Selected Test Type");
                enterResultHeader.setTestType(testType);

                if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                    percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
                    enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                }

                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 3) {
            if(resultCode == RESULT_OK) {
                maximumScore = data.getStringExtra("Max Obtainable");
                enterResultHeader.setMaxScore(maximumScore);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 4) {
            if(resultCode == RESULT_OK) {
                percentageOfTotal = data.getStringExtra("PercentageOfTotal");
                enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 5) {
            if(resultCode == RESULT_OK) {
                term = data.getStringExtra("Selected Term");
                academicYear_Term = year + "_" + term;
                term_AcademicYear = term + "_" + year;
                subject_AcademicYear_Term = subject + "_" + year + "_" + term;
                subject_Term_AcademicYear = subject + "_" + term + "_" + year;
                class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
                class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;
                updatePercentageOfTotal(class_subject_AcademicYear_Term);
                enterResultHeader.setTerm(term);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updatePercentageOfTotal(String subjectKey){
        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(EnterResultsActivity.this);
        progressDialog.show();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordClass").child(activeClass).child(subjectKey).child("percentageOfTotal");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    previousPercentageOfTotal = Double.valueOf(dataSnapshot.getValue(String.class));
                } else {
                    previousPercentageOfTotal = 0.0;
                    percentageOfTotal = "30";
                }

                if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                    percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
                }

                enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                enterResultHeader.setPreviousPercentageOfTotal(previousPercentageOfTotal);
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void saveToCloud() {
        if (CheckNetworkConnectivity.isNetworkAvailable(this)) {
            final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(EnterResultsActivity.this);
            progressDialog.show();

            academicYear_Term = year + "_" + term;
            term_AcademicYear = term + "_" + year;
            subject_AcademicYear_Term = subject + "_" + year + "_" + term;
            subject_Term_AcademicYear = subject + "_" + term + "_" + year;
            class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
            class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;

            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecord").child("AcademicRecord").push();
            uniquePushID = mDatabaseReference.getKey();

            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordClass").child(activeClass).child(class_subject_AcademicYear_Term);
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pushID = class_subject_AcademicYear_Term;
                    mDatabaseReference = mFirebaseDatabase.getReference();
                    newResultEntry = new HashMap<String, Object>();

                    if ((previousPercentageOfTotal + Double.valueOf(percentageOfTotal)) > 100.0) {
                        CustomToast.whiteBackgroundBottomToast(EnterResultsActivity.this, "The percentage of total is more than 100");
                        return;
                    }

                    if ((previousPercentageOfTotal) >= 100.0) {
                        CustomToast.whiteBackgroundBottomToast(EnterResultsActivity.this, "The percentage of total is more than 100");
                        return;
                    }

                    final AcademicRecord academicRecord = new AcademicRecord(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, term, year, subject,
                            date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
                            class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType, maximumScore, percentageOfTotal);

                    if (dataSnapshot.exists()) {
                        existingStudentScores = new Hashtable<String, String>();
                        DatabaseReference childDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordClass-Student").child(classID).child(pushID).child("Students");
                        childDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String key = postSnapshot.getKey();
                                    String score = postSnapshot.getValue(String.class);
                                    existingStudentScores.put(key, score);
                                }

                                double classAverageNonTotal = 0;
                                double TotalNonTotal = 0;
                                int counterNonTotal = 0;
                                for (int i = 0; i < enterResultRowList.size(); i++) {
                                    if (enterResultRowList.get(i).getStudentID() != null) {
                                        TotalNonTotal += Double.valueOf(enterResultRowList.get(i).getScore());
                                        counterNonTotal++;
                                    }
                                }
                                classAverageNonTotal = TotalNonTotal / counterNonTotal;

                                double recordClassAve = 0.0;
                                int counter = 0;
                                for (int i = 0; i < enterResultRowList.size(); i++) {
                                    if (enterResultRowList.get(i).getStudentID() != null) {
                                        recordClassAve += Double.valueOf(enterResultRowList.get(i).getScore());
                                        newScore = (Double.valueOf(enterResultRowList.get(i).getScore()) / Double.valueOf(maximumScore)) * Double.valueOf(percentageOfTotal);
                                        final String studentID = enterResultRowList.get(i).getStudentID();
                                        if (existingStudentScores.containsKey(studentID)) {
                                            String oldScore = existingStudentScores.get(studentID);
                                            Double oldScoreDouble = Double.valueOf(oldScore);
                                            score = (oldScoreDouble + newScore);
                                        } else {
                                            score = newScore;
                                        }
                                        classAverage = classAverage + Double.valueOf(score);
                                        counter++;

                                        newResultEntry.put("AcademicRecordTotal/AcademicRecord-Student/" + activeSchoolID + "/" + pushID + "/Students/" + studentID, String.valueOf(score));
                                        newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher-Student/" + auth.getCurrentUser().getUid() + "/" + pushID + "/Students/" + studentID, String.valueOf(score));
                                        newResultEntry.put("AcademicRecordTotal/AcademicRecordClass-Student/" + activeClass + "/" + pushID + "/Students/" + studentID, String.valueOf(score));
                                        AcademicRecordStudent academicRecordStudent = new AcademicRecordStudent(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, studentID, term, year, subject,
                                                date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
                                                class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType, maximumScore,
                                                String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)), String.valueOf(score), String.valueOf(classAverage));

                                        newResultEntry.put("AcademicRecordTotal/AcademicRecordStudent/" + studentID + "/" + pushID, academicRecordStudent);

                                        newResultEntry.put("AcademicRecord/AcademicRecord-Student/" + activeSchoolID + "/" + uniquePushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
                                        newResultEntry.put("AcademicRecord/AcademicRecordTeacher-Student/" + auth.getCurrentUser().getUid() + "/" + uniquePushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
                                        newResultEntry.put("AcademicRecord/AcademicRecordClass-Student/" + activeClass + "/" + uniquePushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
                                        academicRecordStudent = new AcademicRecordStudent(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, enterResultRowList.get(i).getStudentID(), term, year, subject,
                                                date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
                                                class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType, maximumScore, percentageOfTotal, enterResultRowList.get(i).getScore(), String.valueOf(classAverageNonTotal));

                                        newResultEntry.put("AcademicRecord/AcademicRecordStudent/" + enterResultRowList.get(i).getStudentID() + "/" + uniquePushID, academicRecordStudent);

                                        ArrayList<String> parentIDList = studentParentList.get(studentID);
                                        if (parentIDList != null) {
                                            for (int j = 0; j < parentIDList.size(); j++) {
                                                String parentID = parentIDList.get(j);
                                                newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/status", true);
                                                newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/status", true);
                                                newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_AcademicYear_Term + "/status", true);
                                                newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_AcademicYear_Term + "/SingleRecords/" + pushID + "/status", true);
                                                DatabaseReference updateLikeRef = mFirebaseDatabase.getReference("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/count");
                                                updateLikeRef.runTransaction(new Transaction.Handler() {
                                                    @Override
                                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                                        Integer currentValue = mutableData.getValue(Integer.class);
                                                        if (currentValue == null) {
                                                            mutableData.setValue(1);
                                                        } else {
                                                            mutableData.setValue(currentValue + 1);
                                                        }

                                                        return Transaction.success(mutableData);

                                                    }

                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                recordClassAve = recordClassAve / counter;
                                academicRecord.setClassAverage(String.valueOf(recordClassAve));
                                newResultEntry.put("AcademicRecord/AcademicRecord/" + activeSchoolID + "/" + uniquePushID, academicRecord);
                                newResultEntry.put("AcademicRecord/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + uniquePushID, academicRecord);
                                newResultEntry.put("AcademicRecord/AcademicRecordClass/" + activeClass + "/" + uniquePushID, academicRecord);

                                classAverage = classAverage / counter;
                                newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID + "/classAverage", String.valueOf(classAverage));
                                newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID + "/maxObtainable", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
                                newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID + "/percentageOfTotal", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
                                newResultEntry.put("AcademicRecordTotal/AcademicRecord/" + activeSchoolID + "/" + pushID + "/classAverage", String.valueOf(classAverage));
                                newResultEntry.put("AcademicRecordTotal/AcademicRecord/" + activeSchoolID + "/" + pushID + "/maxObtainable", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
                                newResultEntry.put("AcademicRecordTotal/AcademicRecord/" + activeSchoolID + "/" + pushID + "/percentageOfTotal", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
                                newResultEntry.put("AcademicRecordTotal/AcademicRecordClass/" + activeClass + "/" + pushID + "/classAverage", String.valueOf(classAverage));
                                newResultEntry.put("AcademicRecordTotal/AcademicRecordClass/" + activeClass + "/" + pushID + "/maxObtainable", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
                                newResultEntry.put("AcademicRecordTotal/AcademicRecordClass/" + activeClass + "/" + pushID + "/percentageOfTotal", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));

                                for (int i = 0; i < parentList.size(); i++){

                                }

                                mDatabaseReference.updateChildren(newResultEntry, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        progressDialog.dismiss();
                                        showDialogWithMessage("Results have been posted", true);
                                    }


                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {

                        pushID = uniquePushID;
                        double recordClassAve = 0.0;
                        double recordTotalScore = 0.0;
                        int recordCounter = 0;
                        academicRecord.setMaxObtainable(maximumScore);

                        //Get Class Average
                        for (int i = 0; i < enterResultRowList.size(); i++) {
                            if (enterResultRowList.get(i).getStudentID() != null) {
                                recordTotalScore += Double.valueOf(enterResultRowList.get(i).getScore());
                                recordCounter++;
                            }
                        }
                        recordClassAve = recordTotalScore / recordCounter;

                        for (int i = 0; i < enterResultRowList.size(); i++) {
                            if (enterResultRowList.get(i).getStudentID() != null) {
                                newResultEntry.put("AcademicRecord/AcademicRecord-Student/" + activeSchoolID + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
                                newResultEntry.put("AcademicRecord/AcademicRecordTeacher-Student/" + auth.getCurrentUser().getUid() + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
                                newResultEntry.put("AcademicRecord/AcademicRecordClass-Student/" + activeClass + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
                                AcademicRecordStudent academicRecordStudent = new AcademicRecordStudent(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, enterResultRowList.get(i).getStudentID(), term, year, subject,
                                        date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
                                        class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType,
                                        maximumScore, percentageOfTotal, enterResultRowList.get(i).getScore(), String.valueOf(recordClassAve));

                                newResultEntry.put("AcademicRecord/AcademicRecordStudent/" + enterResultRowList.get(i).getStudentID() + "/" + pushID, academicRecordStudent);

                                String studentID = enterResultRowList.get(i).getStudentID();
                                ArrayList<String> parentIDList = studentParentList.get(studentID);
                                if (parentIDList != null) {
                                    for (int j = 0; j < parentIDList.size(); j++) {
                                        String parentID = parentIDList.get(j);
                                        newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/status", true);
                                        newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/status", true);
                                        newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_AcademicYear_Term + "/status", true);
                                        newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_AcademicYear_Term + "/SingleRecords/" + pushID + "/status", true);
                                        DatabaseReference updateLikeRef = mFirebaseDatabase.getReference("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/count");
                                        updateLikeRef.runTransaction(new Transaction.Handler() {
                                            @Override
                                            public Transaction.Result doTransaction(MutableData mutableData) {
                                                Integer currentValue = mutableData.getValue(Integer.class);
                                                if (currentValue == null) {
                                                    mutableData.setValue(1);
                                                } else {
                                                    mutableData.setValue(currentValue + 1);
                                                }

                                                return Transaction.success(mutableData);

                                            }

                                            @Override
                                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                                            }
                                        });
                                    }
                                }
                            }
                        }
//                            recordClassAve = recordTotalScore / recordCounter;
                        academicRecord.setClassAverage(String.valueOf(recordClassAve));
                        newResultEntry.put("AcademicRecord/AcademicRecord/" + activeSchoolID + "/" + pushID, academicRecord);
                        newResultEntry.put("AcademicRecord/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID, academicRecord);
                        newResultEntry.put("AcademicRecord/AcademicRecordClass/" + activeClass + "/" + pushID, academicRecord);

                        pushID = class_subject_AcademicYear_Term;
                        int counter = 0;

                        String totalScore = "0.0";
                        for (int i = 0; i < enterResultRowList.size(); i++) {
                            if (enterResultRowList.get(i).getStudentID() != null) {
                                totalScore = String.valueOf((Double.valueOf(enterResultRowList.get(i).getScore()) / Double.valueOf(maximumScore)) * Double.valueOf(percentageOfTotal));
                                classAverage = classAverage + Double.valueOf(totalScore);
                                counter++;
                            }
                        }
                        classAverage = classAverage / counter;

                        for (int i = 0; i < enterResultRowList.size(); i++) {
                            if (enterResultRowList.get(i).getStudentID() != null) {
                                newResultEntry.put("AcademicRecordTotal/AcademicRecord-Student/" + activeSchoolID + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), totalScore);
                                newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher-Student/" + auth.getCurrentUser().getUid() + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), totalScore);
                                newResultEntry.put("AcademicRecordTotal/AcademicRecordClass-Student/" + activeClass + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), totalScore);

                                AcademicRecordStudent academicRecordStudent = new AcademicRecordStudent(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, enterResultRowList.get(i).getStudentID(), term, year, subject,
                                        date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
                                        class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType,
                                        percentageOfTotal, percentageOfTotal, totalScore, String.valueOf(classAverage));

                                newResultEntry.put("AcademicRecordTotal/AcademicRecordStudent/" + enterResultRowList.get(i).getStudentID() + "/" + pushID, academicRecordStudent);
                                newResultEntry.put("AcademicRecordTotal/AcademicRecordStudent-Subject/" + enterResultRowList.get(i).getStudentID() + "/" + subject, true);
                                //Todo: Confirm that AcademicRecordStudent-Subject writes
                            }
                        }
                        academicRecord.setClassAverage(String.valueOf(classAverage)); //TODO: Change to academicRecordTotal
                        academicRecord.setMaxObtainable(percentageOfTotal);
                        newResultEntry.put("AcademicRecordTotal/AcademicRecord/" + activeSchoolID + "/" + pushID, academicRecord);
                        newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID, academicRecord);
                        newResultEntry.put("AcademicRecordTotal/AcademicRecordClass/" + activeClass + "/" + pushID, academicRecord);
                        newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher-Subject/" + auth.getCurrentUser().getUid() + "/" + subject, true);

                        mDatabaseReference.updateChildren(newResultEntry, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                progressDialog.dismiss();
                                showDialogWithMessage("Results have been posted", true);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            showDialogWithMessage("Internet is down, check your connection and try again", false);
        }
    }

    void showDialogWithMessage (String messageString, final boolean finish) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        TextView OK = (TextView) dialog.findViewById(R.id.optionone);
        dialog.show();

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (finish) {
                    finish();
                }
            }
        });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            year = intent.getStringExtra("Year");
            String month = String.valueOf(intent.getStringExtra("Month"));
            String day = String.valueOf(intent.getStringExtra("Day"));
            term = Term.getTermShort(month);
            date = year + "/" + month + "/" + day + " 12:00:00:00";

            academicYear_Term = year + "_" + term;
            term_AcademicYear = term + "_" + year;
            subject_AcademicYear_Term = subject + "_" + year + "_" + term;
            subject_Term_AcademicYear = subject + "_" + term + "_" + year;
            class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
            class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;
            updatePercentageOfTotal(class_subject_AcademicYear_Term);
            enterResultHeader.setYear(year);
            enterResultHeader.setMonth(month);
            enterResultHeader.setDay(day);
            enterResultHeader.setDate(date);
            enterResultHeader.setTerm(term);
            enterResultHeader.setSortableDate(Date.convertToSortableDate(date));
            mAdapter.notifyDataSetChanged();
        }
    };
}
