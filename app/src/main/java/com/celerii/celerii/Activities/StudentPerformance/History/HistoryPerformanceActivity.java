package com.celerii.celerii.Activities.StudentPerformance.History;

import android.content.Context;
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
import com.celerii.celerii.adapters.HistoryPerformanceAdapter;
import com.celerii.celerii.adapters.TeacherPerformanceRowMain;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecord;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.HistoryPerformanceBody;
import com.celerii.celerii.models.HistoryPerformanceHeader;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.SubscriptionModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class HistoryPerformanceActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<HistoryPerformanceBody> historyPerformanceBodyList;
    private ArrayList<AcademicRecordStudent> academicRecordStudentList;
    private ArrayList<String> classSubjectYearTermList;
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

    String featureUseKey = "";
    String featureName = "Historical Academic Result Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_performance);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        Bundle dataBundle = getIntent().getExtras();
        activeStudent = dataBundle.getString("Active Student");
        activeSubject = dataBundle.getString("Subject");

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeStudentModel = gson.fromJson(activeStudent, type);

        activeStudentID = activeStudentModel.getStudentID();

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

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

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        historyPerformanceBodyList = new ArrayList<>();
        academicRecordStudentList = new ArrayList<>();
        classSubjectYearTermList = new ArrayList<>();
        xLabel = new ArrayList<>();
        yLabel = new ArrayList<>();
        mAdapter = new HistoryPerformanceAdapter(historyPerformanceBodyList, header, this);
        recyclerView.setAdapter(mAdapter);
        loadNewDetailsFromFirebase();

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
    double termAverage = 0.0;
    AcademicRecordStudent academicRecord;
    HistoryPerformanceBody historyPerformanceBody;

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
        xLabel = new ArrayList<>();
        yLabel = new ArrayList<>();
        historyPerformanceBodyList.clear();
        academicRecordStudentList.clear();
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(activeStudentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String subject_year_term = postSnapshot.getKey();
                        String subjectKey = subject_year_term.split("_")[0];

                        if (subjectKey.equals(activeSubject)) {
                            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(activeStudentID).child(subject_year_term);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    counter++;
                                    if (dataSnapshot.exists()) {
                                        termAverage = 0.0;

                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            academicRecord = postSnapshot.getValue(AcademicRecordStudent.class);
                                            academicRecordStudentList.add(academicRecord);
                                            if (!classSubjectYearTermList.contains(academicRecord.getClass_subject_AcademicYear_Term())) {
                                                classSubjectYearTermList.add(academicRecord.getClass_subject_AcademicYear_Term());
                                            }
                                        }

//                                        xLabel.add(academicYear_Term);
//                                        yLabel.add(termAverage);
//                                        overallTotal += termAverage;
//                                        overallCount += 1;

                                        if (counter == childrenCount) {
                                            for (String classSubjectYearTerm: classSubjectYearTermList) {
                                                termAverage = 0.0;
                                                String academicYear_Term = "";
                                                String latestDate = "0000/00/00 00:00:00:000";
                                                for (AcademicRecordStudent academicRecordStudent: academicRecordStudentList) {
                                                    if (academicRecordStudent.getClass_subject_AcademicYear_Term().equals(classSubjectYearTerm)) {
                                                        double testAverage = Double.valueOf(academicRecordStudent.getScore());
                                                        double maxObtainable = Double.valueOf(academicRecordStudent.getMaxObtainable());
                                                        double percentageOfTotal = Double.valueOf(academicRecordStudent.getPercentageOfTotal());
                                                        academicYear_Term = academicRecordStudent.getAcademicYear_Term();

                                                        if (Date.compareDates(academicRecordStudent.getDate(), latestDate)) {
                                                            latestDate = academicRecordStudent.getDate();
                                                        }

                                                        double normalizedTestClassAverage = (testAverage / maxObtainable) * percentageOfTotal;
                                                        termAverage += normalizedTestClassAverage;
                                                        academicRecord = academicRecordStudent;
                                                    }
                                                }

//                                                xLabel.add(academicYear_Term);
//                                                yLabel.add(termAverage);
                                                overallTotal += termAverage;
                                                overallCount += 1;
                                                historyPerformanceBody = new HistoryPerformanceBody(academicRecord.getClassID(), "", academicRecord.getTerm(), academicRecord.getAcademicYear(), String.valueOf((int)termAverage), String.valueOf((int)termAverage), latestDate, activeStudent, activeSubject, "neutral");
                                                historyPerformanceBodyList.add(historyPerformanceBody);
//                                                teacherPerformanceRowMain = new TeacherPerformanceRowMain("", academicRecord.getClassID(), academicRecord.getTeacherID(), academicRecord.getSubject(), academicRecord.getTerm(), academicRecord.getAcademicYear(),
//                                                        String.valueOf(termClassAverage), "neutral");
//                                                teacherPerformanceRowMainList.add(teacherPerformanceRowMain);
                                            }

                                            innerCounter = 0;
                                            for (final HistoryPerformanceBody historyPerformanceBody: historyPerformanceBodyList) {
                                                mDatabaseReference = mFirebaseDatabase.getReference("Class").child(historyPerformanceBody.getClassID());
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        innerCounter++;
                                                        if (dataSnapshot.exists()) {
                                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                                            historyPerformanceBody.setClassName(classInstance.getClassName());
                                                        }

                                                        if (innerCounter == historyPerformanceBodyList.size()) {
                                                            if (historyPerformanceBodyList.size() > 1) {
                                                                Collections.sort(historyPerformanceBodyList, new Comparator<HistoryPerformanceBody>() {
                                                                    @Override
                                                                    public int compare(HistoryPerformanceBody o1, HistoryPerformanceBody o2) {
                                                                        return o1.getYear_term().compareTo(o2.getYear_term());
                                                                    }
                                                                });

                                                                Boolean isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
                                                                Gson gson = new Gson();
                                                                String subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationTeachers();
                                                                Type type = new TypeToken<HashMap<String, SubscriptionModel>>() {}.getType();
                                                                HashMap<String, SubscriptionModel> subscriptionModelMap = gson.fromJson(subscriptionModelJSON, type);
                                                                SubscriptionModel subscriptionModel = new SubscriptionModel();
                                                                if (subscriptionModelMap != null) {
                                                                    subscriptionModel = subscriptionModelMap.get(activeStudentID);
                                                                    if (subscriptionModel == null) {
                                                                        subscriptionModel = new SubscriptionModel();
                                                                    }
                                                                }
                                                                if (subscriptionModel.getStudentAccount().equals("")) {
                                                                    gson = new Gson();
                                                                    subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationParents();
                                                                    type = new TypeToken<HashMap<String, ArrayList<SubscriptionModel>>>() {}.getType();
                                                                    HashMap<String, ArrayList<SubscriptionModel>> subscriptionModelMapParent = gson.fromJson(subscriptionModelJSON, type);
                                                                    subscriptionModel = new SubscriptionModel();
                                                                    if (subscriptionModelMapParent != null) {
                                                                        ArrayList<SubscriptionModel> subscriptionModelList = subscriptionModelMapParent.get(activeStudentID);
                                                                        String latestSubscriptionDate = "0000/00/00 00:00:00:000";
                                                                        for (SubscriptionModel subscriptionModel1: subscriptionModelList) {
                                                                            if (Date.compareDates(subscriptionModel1.getExpiryDate(), latestSubscriptionDate)) {
                                                                                subscriptionModel = subscriptionModel1;
                                                                                latestSubscriptionDate = subscriptionModel1.getExpiryDate();
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                for (int i = 0; i < historyPerformanceBodyList.size(); i++) {
                                                                    if (i == 0) {
                                                                        historyPerformanceBodyList.get(i).setIsIncrease("neutral");
                                                                    } else {
                                                                        double currentScore = Double.valueOf(historyPerformanceBodyList.get(i).getScore());
                                                                        double previousScore = Double.valueOf(historyPerformanceBodyList.get(i - 1).getScore());
                                                                        if (currentScore > previousScore) {
                                                                            historyPerformanceBodyList.get(i).setIsIncrease("true");
                                                                        } else if (currentScore < previousScore) {
                                                                            historyPerformanceBodyList.get(i).setIsIncrease("false");
                                                                        } else {
                                                                            historyPerformanceBodyList.get(i).setIsIncrease("neutral");
                                                                        }
                                                                    }

                                                                    xLabel.add(historyPerformanceBodyList.get(i).getYear() + "_" + historyPerformanceBodyList.get(i).getTerm());
                                                                    yLabel.add(Double.parseDouble(historyPerformanceBodyList.get(i).getScore()));

                                                                    boolean isExpired = Date.compareDates(historyPerformanceBodyList.get(i).getDate(), subscriptionModel.getExpiryDate());
                                                                    if (!isOpenToAll) {
                                                                        if (isExpired) {
                                                                            yLabel.set(i, 0.0);
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            Collections.reverse(historyPerformanceBodyList);
                                                            double score = overallTotal / overallCount;
                                                            x = xLabel.toArray(new String[xLabel.size()]);
                                                            y = yLabel.toArray(new Double[yLabel.size()]);
                                                            header.setAverageScore(String.valueOf((int) score));
                                                            header.setSubjectHead(activeSubject);
                                                            header.setxList(x);
                                                            header.setyList(y);
                                                            historyPerformanceBodyList.add(0, new HistoryPerformanceBody());
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
                                if (historyPerformanceBodyList.size() > 1) {
                                    Collections.sort(historyPerformanceBodyList, new Comparator<HistoryPerformanceBody>() {
                                        @Override
                                        public int compare(HistoryPerformanceBody o1, HistoryPerformanceBody o2) {
                                            return o1.getYear_term().compareTo(o2.getYear_term());
                                        }
                                    });

                                    for (int i = 0; i < historyPerformanceBodyList.size(); i++) {
                                        if (i == 0) {
                                            historyPerformanceBodyList.get(i).setIsIncrease("neutral");
                                        } else {
                                            double currentScore = Double.valueOf(historyPerformanceBodyList.get(i).getScore());
                                            double previousScore = Double.valueOf(historyPerformanceBodyList.get(i - 1).getScore());
                                            if (currentScore > previousScore) {
                                                historyPerformanceBodyList.get(i).setIsIncrease("true");
                                            } else if (currentScore < previousScore) {
                                                historyPerformanceBodyList.get(i).setIsIncrease("false");
                                            } else {
                                                historyPerformanceBodyList.get(i).setIsIncrease("neutral");
                                            }
                                        }
                                    }
                                }

                                Collections.reverse(historyPerformanceBodyList);
                                double score = overallTotal / overallCount;
                                x = xLabel.toArray(new String[xLabel.size()]);
                                y = xLabel.toArray(new Double[xLabel.size()]);
                                header.setAverageScore(String.valueOf((int)score));
                                header.setSubjectHead(activeSubject);
                                header.setxList(x);
                                header.setyList(y);
                                historyPerformanceBodyList.add(0, new HistoryPerformanceBody());
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

//
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
//        xLabel = new ArrayList<>();
//        yLabel = new ArrayList<>();
//        historyPerformanceBodyList.clear();
//
//        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(activeStudentID);
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
//                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                        String subject_year_term = postSnapshot.getKey();
//                        String subjectKey = subject_year_term.split("_")[0];
//
//                        if (subjectKey.equals(activeSubject)) {
//                            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(activeStudentID).child(subject_year_term);
//                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    counter++;
//                                    if (dataSnapshot.exists()) {
//                                        termAverage = 0.0;
//                                        String academicYear_Term = "";
//
//                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                            academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
//                                            double testAverage = Double.valueOf(academicRecordStudent.getScore());
//                                            double maxObtainable = Double.valueOf(academicRecordStudent.getMaxObtainable());
//                                            double percentageOfTotal = Double.valueOf(academicRecordStudent.getPercentageOfTotal());
//                                            academicYear_Term = academicRecordStudent.getAcademicYear_Term();
//
//                                            double normalizedTestClassAverage = (testAverage / maxObtainable) * percentageOfTotal;
//                                            termAverage += normalizedTestClassAverage;
//                                        }
//
//                                        xLabel.add(academicYear_Term);
//                                        yLabel.add(termAverage);
//                                        overallTotal += termAverage;
//                                        overallCount += 1;
//
//                                        historyPerformanceBody = new HistoryPerformanceBody(academicRecordStudent.getClassID(), "", academicRecordStudent.getTerm(),
//                                                academicRecordStudent.getAcademicYear(), String.valueOf((int)termAverage), String.valueOf((int)termAverage), activeStudent, activeSubject, "equal");
//                                        historyPerformanceBodyList.add(historyPerformanceBody);
//
//                                        if (counter == childrenCount) {
//                                            innerCounter = 0;
//                                            for (final HistoryPerformanceBody historyPerformanceBody: historyPerformanceBodyList) {
//                                                mDatabaseReference = mFirebaseDatabase.getReference("Class").child(historyPerformanceBody.getClassID());
//                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                                        innerCounter++;
//                                                        if (dataSnapshot.exists()) {
//                                                            Class classInstance = dataSnapshot.getValue(Class.class);
//                                                            historyPerformanceBody.setClassName(classInstance.getClassName());
//                                                        }
//
//                                                        if (innerCounter == historyPerformanceBodyList.size()) {
//                                                            if (historyPerformanceBodyList.size() > 1) {
//                                                                Collections.sort(historyPerformanceBodyList, new Comparator<HistoryPerformanceBody>() {
//                                                                    @Override
//                                                                    public int compare(HistoryPerformanceBody o1, HistoryPerformanceBody o2) {
//                                                                        return o1.getYear_term().compareTo(o2.getYear_term());
//                                                                    }
//                                                                });
//
//                                                                for (int i = 0; i < historyPerformanceBodyList.size(); i++) {
//                                                                    if (i == 0) {
//                                                                        historyPerformanceBodyList.get(i).setIsIncrease("neutral");
//                                                                    } else {
//                                                                        double currentScore = Double.valueOf(historyPerformanceBodyList.get(i).getScore());
//                                                                        double previousScore = Double.valueOf(historyPerformanceBodyList.get(i - 1).getScore());
//                                                                        if (currentScore > previousScore) {
//                                                                            historyPerformanceBodyList.get(i).setIsIncrease("true");
//                                                                        } else if (currentScore < previousScore) {
//                                                                            historyPerformanceBodyList.get(i).setIsIncrease("false");
//                                                                        } else {
//                                                                            historyPerformanceBodyList.get(i).setIsIncrease("neutral");
//                                                                        }
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            Collections.reverse(historyPerformanceBodyList);
//                                                            double score = overallTotal / overallCount;
//                                                            x = xLabel.toArray(new String[xLabel.size()]);
//                                                            y = yLabel.toArray(new Double[yLabel.size()]);
//                                                            header.setAverageScore(String.valueOf((int) score));
//                                                            header.setSubjectHead(activeSubject);
//                                                            header.setxList(x);
//                                                            header.setyList(y);
//                                                            historyPerformanceBodyList.add(0, new HistoryPerformanceBody());
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
//                                if (historyPerformanceBodyList.size() > 1) {
//                                    Collections.sort(historyPerformanceBodyList, new Comparator<HistoryPerformanceBody>() {
//                                        @Override
//                                        public int compare(HistoryPerformanceBody o1, HistoryPerformanceBody o2) {
//                                            return o1.getYear_term().compareTo(o2.getYear_term());
//                                        }
//                                    });
//
//                                    for (int i = 0; i < historyPerformanceBodyList.size(); i++) {
//                                        if (i == 0) {
//                                            historyPerformanceBodyList.get(i).setIsIncrease("neutral");
//                                        } else {
//                                            double currentScore = Double.valueOf(historyPerformanceBodyList.get(i).getScore());
//                                            double previousScore = Double.valueOf(historyPerformanceBodyList.get(i - 1).getScore());
//                                            if (currentScore > previousScore) {
//                                                historyPerformanceBodyList.get(i).setIsIncrease("true");
//                                            } else if (currentScore < previousScore) {
//                                                historyPerformanceBodyList.get(i).setIsIncrease("false");
//                                            } else {
//                                                historyPerformanceBodyList.get(i).setIsIncrease("neutral");
//                                            }
//                                        }
//                                    }
//                                }
//
//                                Collections.reverse(historyPerformanceBodyList);
//                                double score = overallTotal / overallCount;
//                                x = xLabel.toArray(new String[xLabel.size()]);
//                                y = xLabel.toArray(new Double[xLabel.size()]);
//                                header.setAverageScore(String.valueOf((int)score));
//                                header.setSubjectHead(activeSubject);
//                                header.setxList(x);
//                                header.setyList(y);
//                                historyPerformanceBodyList.add(0, new HistoryPerformanceBody());
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

    int iterator = 0;
    private void loadDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

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
                    mAdapter.notifyDataSetChanged();
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
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
