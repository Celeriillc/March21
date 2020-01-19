package com.celerii.celerii.Activities.StudentPerformance.History;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.HistoryPerformanceAdapter;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.HistoryPerformanceBody;
import com.celerii.celerii.models.HistoryPerformanceHeader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class HistoryPerformanceActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    private ArrayList<HistoryPerformanceBody> historyPerformanceBodyList;
    private ArrayList<String> xLabel;
    private ArrayList<Double> yLabel;
    String[] x;
    Double[] y;
    HistoryPerformanceHeader header;
    public RecyclerView recyclerView;
    public HistoryPerformanceAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Toolbar mtoolbar;

    String term, year;
    String activeStudent = "";
    String activeStudentID = "";
    String activeSubject = "";
    double previousScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_performance);

        Bundle dataBundle = getIntent().getExtras();
        activeStudent = dataBundle.getString("Active Student");
        activeSubject = dataBundle.getString("Subject");
        activeStudentID = activeStudent.split(" ")[0];

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        mtoolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(activeSubject);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

//        String[] x = new String[] {"one", "two", "three", "four", "five", "six" };
//        Double[] y = new Double[] {40.0, 50.0, 80.0, 60.0, 50.0, 100.0 };
//        HistoryPerformanceHeader header = new HistoryPerformanceHeader("English Language", "78", "89", y, x);

        header = new HistoryPerformanceHeader();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        historyPerformanceBodyList = new ArrayList<>();
        xLabel = new ArrayList<>();
        yLabel = new ArrayList<>();
        historyPerformanceBodyList.add(new HistoryPerformanceBody());
        loadDetailsFromFirebase();
        mAdapter = new HistoryPerformanceAdapter(historyPerformanceBodyList, header, this);
        recyclerView.setAdapter(mAdapter);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadDetailsFromFirebase();
                    }
                }
        );
    }

    int iterator = 0;
    private void loadDetailsFromFirebase() {
        term = Term.getTermShort();
        year = Date.getYear();
        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(activeStudentID);
        mDatabaseReference.orderByChild("subject").equalTo(activeSubject).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int childrenCount = (int) dataSnapshot.getChildrenCount();
                iterator = 0;
                if (dataSnapshot.exists()){
                    xLabel.clear();
                    yLabel.clear();
                    historyPerformanceBodyList.clear();
                    previousScore = 0.0;
                    double summer = 0;
                    double counter = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                        if (academicRecordStudent.getTerm().equals(term) && academicRecordStudent.getAcademicYear().equals(year)) {
                            continue;
                        }

                        String classID = academicRecordStudent.getClassID();

                        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String className;
                                if (dataSnapshot.exists()){
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    className = classInstance.getClassName();
                                } else {
                                    className = "Deleted";
                                }

                                HistoryPerformanceBody body;
                                String scoreNorm = TypeConverterClass.convStringToIntString(academicRecordStudent.getScore()); // + "/" + TypeConverterClass.convStringToIntString(academicRecordStudent.getPercentageOfTotal());
                                if (Double.valueOf(academicRecordStudent.getScore()) > previousScore) {
                                    body = new HistoryPerformanceBody(academicRecordStudent.getClassID(), className, academicRecordStudent.getTerm(),
                                            academicRecordStudent.getAcademicYear(), academicRecordStudent.getScore(), scoreNorm, activeStudent, activeSubject, "true");
                                } else if (Double.valueOf(academicRecordStudent.getScore()) > previousScore){
                                    body = new HistoryPerformanceBody(academicRecordStudent.getClassID(),className, academicRecordStudent.getTerm(),
                                            academicRecordStudent.getAcademicYear(), academicRecordStudent.getScore(), scoreNorm, activeStudent, activeSubject, "false");
                                } else {
                                    body = new HistoryPerformanceBody(academicRecordStudent.getClassID(),className, academicRecordStudent.getTerm(),
                                            academicRecordStudent.getAcademicYear(), academicRecordStudent.getScore(), scoreNorm, activeStudent, activeSubject, "equal");
                                }

                                historyPerformanceBodyList.add(body);

                                iterator = 0;
                                if (childrenCount == historyPerformanceBodyList.size()){
                                    if (historyPerformanceBodyList.size() > 1) {
                                        for (int i = 0; i < historyPerformanceBodyList.size(); i++) {
                                            body = historyPerformanceBodyList.get(i);
                                            if (body.getTerm().equals("1")) {
                                                body.setTerm("10");
                                            }
                                        }
                                        Collections.sort(historyPerformanceBodyList, new Comparator<HistoryPerformanceBody>() {
                                            @Override
                                            public int compare(HistoryPerformanceBody o1, HistoryPerformanceBody o2) {
                                                int value1 = o1.getYear().compareTo(o2.getYear());
                                                if (value1 == 0) {
                                                    return o1.getTerm().compareTo(o2.getTerm());
                                                } else {
                                                    return value1;
                                                }
                                            }
                                        });

                                        for (int i = 0; i < historyPerformanceBodyList.size(); i++) {
                                            body = historyPerformanceBodyList.get(i);
                                            if (body.getTerm().equals("10")) {
                                                body.setTerm("1");
                                            }
                                        }
                                    }

                                    Collections.reverse(historyPerformanceBodyList);
                                    for (int i = 0; i < historyPerformanceBodyList.size(); i++) {
                                        body = historyPerformanceBodyList.get(i);

                                        xLabel.add(0, body.getYear() + "_" + body.getTerm());
                                        yLabel.add(0, Double.valueOf(body.getScore()));

                                        if (i == historyPerformanceBodyList.size() - 1){
                                            body.setIsIncrease("equal");
                                        } else {
                                            if (TypeConverterClass.convStringToDouble(body.getScore()) > TypeConverterClass.convStringToDouble(historyPerformanceBodyList.get(i + 1).getScore())){
                                                body.setIsIncrease("true");
                                            } else if (TypeConverterClass.convStringToDouble(body.getScore()) < TypeConverterClass.convStringToDouble(historyPerformanceBodyList.get(i + 1).getScore())) {
                                                body.setIsIncrease("false");
                                            } else {
                                                body.setIsIncrease("equal");
                                            }
                                        }

                                        String class_subject_year_term = body.getClassID() + "_" + body.getSubject() + "_" + body.getYear() + "_" + body.getTerm();
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(auth.getCurrentUser().getUid()).child(activeStudentID).child("subjects").child(activeSubject).child("Class_Subject_AcademicYear_Term").child(class_subject_year_term).child("status");
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    boolean status = dataSnapshot.getValue(boolean.class);
                                                    if (status) {
                                                        historyPerformanceBodyList.get(iterator).setNew(true);
                                                    } else {
                                                        historyPerformanceBodyList.get(iterator).setNew(false);
                                                    }
                                                } else {
                                                    historyPerformanceBodyList.get(iterator).setNew(false);
                                                }

                                                iterator++;
                                                if (iterator == historyPerformanceBodyList.size()) {
                                                    updateBadges();
                                                    historyPerformanceBodyList.add(0, new HistoryPerformanceBody());
                                                    x = xLabel.toArray(new String[xLabel.size()]);
                                                    y = yLabel.toArray(new Double[yLabel.size()]);
                                                    header.setxList(x);
                                                    header.setyList(y);
                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
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

                        previousScore = Double.valueOf(academicRecordStudent.getScore());
                        summer = summer + (TypeConverterClass.convStringToDouble(academicRecordStudent.getScore()) / TypeConverterClass.convStringToDouble(academicRecordStudent.getMaxObtainable())) * 100;
                        counter++;
                    }
                    double score = (summer / counter);
                    header.setSubjectHead(activeSubject);
                    header.setAverageScore(String.valueOf(score));
                    mAdapter.notifyDataSetChanged();
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

    public void updateBadges(){
        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
        updateBadgesMap.put("AcademicRecordParentNotification/" + auth.getCurrentUser().getUid() + "/" + activeStudentID + "/subjects/" + activeSubject + "/status", false);
        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(updateBadgesMap);
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
