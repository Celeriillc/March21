package com.celerii.celerii.Activities.StudentPerformance.History;

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
import com.celerii.celerii.adapters.AcademicRecordDetailAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class AcademicRecordDetailActivity extends AppCompatActivity {

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
    private ArrayList<AcademicRecordStudent> academicRecordStudentList;
    public RecyclerView recyclerView;
    public AcademicRecordDetailAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String student, studentID, subject, term, year, subject_year_term;

    Handler internetConnectionHandler = new Handler();
    Runnable internetConnectionRunnable;

    String featureUseKey = "";
    String featureName = "Historical Academic Results Detail";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_record_detail);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        Bundle bundle = getIntent().getExtras();
        student = bundle.getString("Active Student");
        subject = bundle.getString("Subject");
        term = bundle.getString("Term");
        year = bundle.getString("Year");

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeStudentModel = gson.fromJson(student, type);

        studentID = activeStudentModel.getStudentID();
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
        mAdapter = new AcademicRecordDetailAdapter(academicRecordStudentList, this);
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

        academicRecordStudentList.clear();
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordStudent").child(studentID).child(subject_year_term);
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                        academicRecordStudent.setRecordKey(postSnapshot.getKey());

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(academicRecordStudent.getClassID());
                        mDatabaseReference.keepSynced(true);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    academicRecordStudent.setClassName(classInstance.getClassName());
                                    academicRecordStudentList.add(academicRecordStudent);
                                }

                                if (childrenCount == academicRecordStudentList.size()){
                                    mAdapter.notifyDataSetChanged();
                                    internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
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
                } else {
                    internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
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

        academicRecordStudentList.clear();
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecord/AcademicRecordStudent").child(studentID);
        mDatabaseReference.orderByChild("subject_AcademicYear_Term").equalTo(subject_year_term).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();

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
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(auth.getCurrentUser().getUid()).child(studentID).child("subjects").child(subject).child("Class_Subject_AcademicYear_Term").child(class_subject_year_term).child("SingleRecords").child(recordKey).child("status");
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
//                                                    updateBadges();
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

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    public void updateBadges(){
//        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
//        for (int i = 0; i < academicRecordStudentList.size(); i++) {
//            AcademicRecordStudent academicRecordStudent = academicRecordStudentList.get(i);
//            String class_subject_year_term = academicRecordStudent.getClassID() + "_" + subject_year_term;
//            updateBadgesMap.put("AcademicRecordParentNotification/" + auth.getCurrentUser().getUid() + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_year_term + "/status", false);
//            break;
//        }
//        mDatabaseReference = mFirebaseDatabase.getReference();
//        mDatabaseReference.updateChildren(updateBadgesMap);
//    }

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

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
            for (int i = 0; i < academicRecordStudentList.size(); i++) {
                AcademicRecordStudent academicRecordStudent = academicRecordStudentList.get(i);
                String recordKey = academicRecordStudent.getRecordKey();
                String class_subject_year_term = academicRecordStudent.getClassID() + "_" + subject_year_term;

                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {
                }.getType();
                Student activeStudentModel = gson.fromJson(sharedPreferencesManager.getActiveKid(), type);
                String activeKidID = activeStudentModel.getStudentID();

                if (academicRecordStudent.getRecordKey() != null && academicRecordStudent.isNew()) {
                    updateBadgesMap.put("AcademicRecordParentNotification/" + auth.getCurrentUser().getUid() + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_year_term + "/SingleRecords/" + recordKey + "/status", false);
                    DatabaseReference updateLikeRef = mFirebaseDatabase.getReference("AcademicRecordParentNotification/" + auth.getCurrentUser().getUid() + "/" + activeKidID + "/count");
                    updateLikeRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Integer currentValue = mutableData.getValue(Integer.class);
                            if (currentValue == null) {
                                mutableData.setValue(1);
                            } else {
                                mutableData.setValue(currentValue - 1);
                            }

                            return Transaction.success(mutableData);

                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    });
                }

                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        }

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
