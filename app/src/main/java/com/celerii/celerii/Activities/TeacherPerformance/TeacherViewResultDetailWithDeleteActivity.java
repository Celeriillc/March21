package com.celerii.celerii.Activities.TeacherPerformance;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TeacherViewResultDetailWithDeleteAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.AcademicRecordTeacher;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.KidScoreForTeachersModel;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TeacherViewResultDetailWithDeleteActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    private ArrayList<KidScoreForTeachersModel> kidScoreForTeachersModelList;
    AcademicRecordTeacher academicRecordTeacher;
    public RecyclerView recyclerView;
    public TeacherViewResultDetailWithDeleteAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String subject_year_term, recordID;

    Handler internetConnectionHandler = new Handler();
    Runnable internetConnectionRunnable;

    String featureUseKey = "";
    String featureName = "Teacher Performance Analysis Detail with Delete";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_result_detail_with_delete);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(this);

        Bundle bundle = getIntent().getExtras();
        subject_year_term = bundle.getString("subject_year_term");
        recordID = bundle.getString("RecordID");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Detail with Delete");
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

        academicRecordTeacher = new AcademicRecordTeacher();
        kidScoreForTeachersModelList = new ArrayList<>();
        mAdapter = new TeacherViewResultDetailWithDeleteAdapter(kidScoreForTeachersModelList, academicRecordTeacher, recordID, this);
        loadNewHeader();
//        loadFromFirebase();
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadNewHeader();
                    }
                }
        );
    }

    private void loadNewHeader() {
//        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
//            mySwipeRefreshLayout.setRefreshing(false);
//            recyclerView.setVisibility(View.GONE);
//            progressLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
//            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
//            return;
//        }
        internetConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(getString(R.string.no_internet_message_for_offline_download));
                }
            }
        };
        internetConnectionHandler.postDelayed(internetConnectionRunnable, 7000);

        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordTeacher").child(mFirebaseUser.getUid()).child(subject_year_term).child(recordID);
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    AcademicRecordTeacher newAcademicRecordTeacher = dataSnapshot.getValue(AcademicRecordTeacher.class);
                    academicRecordTeacher.setSubject(newAcademicRecordTeacher.getSubject());
                    academicRecordTeacher.setTestType(newAcademicRecordTeacher.getTestType());
                    academicRecordTeacher.setDate(newAcademicRecordTeacher.getDate());
                    academicRecordTeacher.setAcademicYear(newAcademicRecordTeacher.getAcademicYear());
                    academicRecordTeacher.setTerm(newAcademicRecordTeacher.getTerm());
                    academicRecordTeacher.setClassAverage(newAcademicRecordTeacher.getClassAverage());
                    academicRecordTeacher.setMaxObtainable(newAcademicRecordTeacher.getMaxObtainable());
                    academicRecordTeacher.setPercentageOfTotal(newAcademicRecordTeacher.getPercentageOfTotal());
                    academicRecordTeacher.setRecordKey(recordID);
                    academicRecordTeacher.setSchoolID(newAcademicRecordTeacher.getSchoolID());
                    academicRecordTeacher.setTeacherID(newAcademicRecordTeacher.getTeacherID());
                    academicRecordTeacher.setClassID(newAcademicRecordTeacher.getClassID());
                    academicRecordTeacher.setSubject_AcademicYear_Term(newAcademicRecordTeacher.getSubject_AcademicYear_Term());
                    academicRecordTeacher.setSubject_Term_AcademicYear(newAcademicRecordTeacher.getSubject_Term_AcademicYear());
                    mAdapter.notifyDataSetChanged();
                    final String teacherID = newAcademicRecordTeacher.getTeacherID();
                    final String classID = newAcademicRecordTeacher.getClassID();

                    mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherID);
                    mDatabaseReference.keepSynced(true);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                String teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                                academicRecordTeacher.setTeacherName(teacherName);
                                mAdapter.notifyDataSetChanged();
                            }

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(classID);
                            mDatabaseReference.keepSynced(true);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        Class classInstance = dataSnapshot.getValue(Class.class);
                                        academicRecordTeacher.setClassName(classInstance.getClassName());
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    loadNewFromFirebase();
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadHeader(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecord").child("AcademicRecordTeacher").child(mFirebaseUser.getUid()).child(recordID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    AcademicRecordTeacher newAcademicRecordTeacher = dataSnapshot.getValue(AcademicRecordTeacher.class);
                    academicRecordTeacher.setSubject(newAcademicRecordTeacher.getSubject());
                    academicRecordTeacher.setTestType(newAcademicRecordTeacher.getTestType());
                    academicRecordTeacher.setDate(newAcademicRecordTeacher.getDate());
                    academicRecordTeacher.setAcademicYear(newAcademicRecordTeacher.getAcademicYear());
                    academicRecordTeacher.setTerm(newAcademicRecordTeacher.getTerm());
                    academicRecordTeacher.setClassAverage(newAcademicRecordTeacher.getClassAverage());
                    academicRecordTeacher.setMaxObtainable(newAcademicRecordTeacher.getMaxObtainable());
                    academicRecordTeacher.setPercentageOfTotal(newAcademicRecordTeacher.getPercentageOfTotal());
                    academicRecordTeacher.setRecordKey(recordID);
                    academicRecordTeacher.setSchoolID(newAcademicRecordTeacher.getSchoolID());
                    academicRecordTeacher.setTeacherID(newAcademicRecordTeacher.getTeacherID());
                    academicRecordTeacher.setClassID(newAcademicRecordTeacher.getClassID());
                    mAdapter.notifyDataSetChanged();
                    final String teacherID = newAcademicRecordTeacher.getTeacherID();
                    final String classID = newAcademicRecordTeacher.getClassID();

                    mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherID);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                String teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                                academicRecordTeacher.setTeacherName(teacherName);
                                mAdapter.notifyDataSetChanged();
                            }

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(classID);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        Class classInstance = dataSnapshot.getValue(Class.class);
                                        academicRecordTeacher.setClassName(classInstance.getClassName());
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    loadNewFromFirebase();
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadNewFromFirebase() {
        kidScoreForTeachersModelList.clear();
        mAdapter.notifyDataSetChanged();
        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordTeacher-Student").child(mFirebaseUser.getUid()).child(subject_year_term).child(recordID).child("Students");
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final KidScoreForTeachersModel kidScoreForTeachersModel = new KidScoreForTeachersModel();
                        kidScoreForTeachersModel.setKidScore(postSnapshot.getValue(String.class));
                        kidScoreForTeachersModel.setKidID(postSnapshot.getKey());
                        String childID = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(childID);
                        mDatabaseReference.keepSynced(true);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Student student = dataSnapshot.getValue(Student.class);
                                    kidScoreForTeachersModel.setKidName(student.getFirstName() + " " + student.getLastName());
                                    kidScoreForTeachersModel.setKidProfilePicture(student.getImageURL());
                                }
                                kidScoreForTeachersModelList.add(kidScoreForTeachersModel);

                                if (childrenCount == kidScoreForTeachersModelList.size()) {
                                    kidScoreForTeachersModelList.add(0, new KidScoreForTeachersModel());
                                    mAdapter.notifyDataSetChanged();
                                    recyclerView.setVisibility(View.VISIBLE);
                                    internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    progressLayout.setVisibility(View.GONE);
                                    errorLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecord").child("AcademicRecordTeacher-Student").child(mFirebaseUser.getUid()).child(recordID).child("Students");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    kidScoreForTeachersModelList.clear();
                    mAdapter.notifyDataSetChanged();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final KidScoreForTeachersModel kidScoreForTeachersModel = new KidScoreForTeachersModel();
                        kidScoreForTeachersModel.setKidScore(postSnapshot.getValue(String.class));
                        kidScoreForTeachersModel.setKidID(postSnapshot.getKey());
                        String childID = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(childID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Student student = dataSnapshot.getValue(Student.class);
                                    kidScoreForTeachersModel.setKidName(student.getFirstName() + " " + student.getLastName());
                                    kidScoreForTeachersModel.setKidProfilePicture(student.getImageURL());
                                }
                                kidScoreForTeachersModelList.add(kidScoreForTeachersModel);

                                if (childrenCount == kidScoreForTeachersModelList.size()) {
                                    kidScoreForTeachersModelList.add(0, new KidScoreForTeachersModel());
                                    mAdapter.notifyDataSetChanged();
                                    recyclerView.setVisibility(View.VISIBLE);
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    progressLayout.setVisibility(View.GONE);
                                    errorLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
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
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
