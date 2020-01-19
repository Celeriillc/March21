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
import com.celerii.celerii.adapters.TeacherPerformanceRowAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecordTeacher;
import com.celerii.celerii.models.TeacherPerformanceHeader;
import com.celerii.celerii.models.TeacherPerformanceRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherPerformanceRowActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    private ArrayList<TeacherPerformanceRow> teacherPerformanceRowList;
    private ArrayList<AcademicRecordTeacher> currentAcademicRecord;
    private ArrayList<String> subjectList, subjectKey;
    private TeacherPerformanceHeader teacherPerformanceHeader;
    public RecyclerView recyclerView;
    public TeacherPerformanceRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    TeacherPerformanceRow teacherPerformanceRow;

    String currentScore = "NA", previousScore = "NA", projectedScore;
    String myID;
    int teacherPerformanceRowListIterator, subIterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_performance_row);

        sharedPreferencesManager = new SharedPreferencesManager(this);

//        myID = "31sQQgT5hYPE0YkpaAyJj5MtA0b2";
        myID = sharedPreferencesManager.getMyUserID();

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Performance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        teacherPerformanceHeader = new TeacherPerformanceHeader();

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        teacherPerformanceRowList = new ArrayList<>();
        currentAcademicRecord = new ArrayList<>();
//        teacherPerformanceRowList.add(new TeacherPerformanceRow());
        subjectList = new ArrayList<>();
        subjectKey = new ArrayList<>();
        loadDetailsFromFirebase();
        mAdapter = new TeacherPerformanceRowAdapter(teacherPerformanceRowList, teacherPerformanceHeader, this);
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

    private void loadDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordTeacher-Subject").child(myID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    teacherPerformanceRowList.clear();
                    currentAcademicRecord.clear();
                    subjectList.clear();

                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        subjectList.add(postSnapshot.getKey());
                    }

                    subIterator = 0;
                    for (int i = 0; i < subjectList.size(); i++) {
                        final String subject = subjectList.get(i);

                        String term = Term.getTermShort();
                        String year = Date.getYear();
                        final String subject_year_term = subject + "_" + year + "_" + term;

                        teacherPerformanceRow = new TeacherPerformanceRow(previousScore, currentScore, "NA", subject, subject_year_term);
                        teacherPerformanceRowList.add(teacherPerformanceRow);

                        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordTeacher").child(myID);
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int latestDate = 0;
                                currentScore = "NA";
                                previousScore = "NA";
                                if (dataSnapshot.exists()){
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        AcademicRecordTeacher academicRecordTeacher = postSnapshot.getValue(AcademicRecordTeacher.class);
                                        if (academicRecordTeacher.getSubject_AcademicYear_Term().equals(teacherPerformanceRowList.get(subIterator).getSubject_year_term())){
                                            int sortableDate = TypeConverterClass.convStringToInt(academicRecordTeacher.getSortableDate());
                                            if (sortableDate > latestDate){
                                                latestDate = sortableDate;
                                                currentScore = TypeConverterClass.convStringToIntString(academicRecordTeacher.getClassAverage());
                                                previousScore = "NA";
                                            }
                                        }
                                    }
                                }

                                teacherPerformanceRowList.get(subIterator).setCurrentScore(currentScore);
                                subIterator++;

                                if (subIterator == subjectList.size()){
                                    subIterator = 0;

                                    for (int j = 0; j < teacherPerformanceRowList.size(); j++) {
                                        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordTeacher").child(myID);
                                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                previousScore = "NA";
                                                int latestDate = 0;

                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        AcademicRecordTeacher academicRecordTeacher = postSnapshot.getValue(AcademicRecordTeacher.class);
                                                        if (academicRecordTeacher.getSubject().equals(teacherPerformanceRowList.get(subIterator).getSubject()) && !academicRecordTeacher.getSubject_AcademicYear_Term().equals(teacherPerformanceRowList.get(subIterator).getSubject_year_term())) {
                                                            int sortableDate = TypeConverterClass.convStringToInt(academicRecordTeacher.getSortableDate());
                                                            if (sortableDate > latestDate) {
                                                                latestDate = sortableDate;
                                                                previousScore = TypeConverterClass.convStringToIntString(academicRecordTeacher.getClassAverage());
                                                                teacherPerformanceRowList.get(subIterator).setPreviousScore(previousScore);
                                                            }
                                                        }
                                                    }
                                                }

                                                if (teacherPerformanceRowList.get(subIterator).getCurrentScore().equals("NA") && teacherPerformanceRowList.get(subIterator).getPreviousScore().equals("NA")) {
                                                    teacherPerformanceRowList.remove(teacherPerformanceRowList.get(subIterator));
                                                }
                                                subIterator++;
                                                if (subjectList.size() == subIterator) {
                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    subIterator = 0;
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
                    errorLayoutText.setText("You don't have any academic history yet. To get started, post academic results for any class");
                }
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
}
