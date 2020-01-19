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
import com.celerii.celerii.adapters.AcademicRecordDetailAdapter;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AcademicRecordDetailActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    Toolbar toolbar;
    private ArrayList<AcademicRecordStudent> academicRecordStudentList;
    public RecyclerView recyclerView;
    public AcademicRecordDetailAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String student, studentID, subject, term, year, subject_year_term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_record_detail);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        Bundle bundle = getIntent().getExtras();
        student = bundle.getString("Active Student");
        subject = bundle.getString("Subject");
        term = bundle.getString("Term");
        year = bundle.getString("Year");
        studentID = student.split(" ")[0];
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

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        academicRecordStudentList = new ArrayList<>();
        mAdapter = new AcademicRecordDetailAdapter(academicRecordStudentList, this);
        loadFromFirebase();
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
                        loadFromFirebase();
                    }
                }
        );
    }

    int iterator = 0;
    private void loadFromFirebase() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {

        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
        for (int i = 0; i < academicRecordStudentList.size(); i++) {
            AcademicRecordStudent academicRecordStudent = academicRecordStudentList.get(i);
            String recordKey = academicRecordStudent.getRecordKey();
            String class_subject_year_term = academicRecordStudent.getClassID() + "_" + subject_year_term;

            if (academicRecordStudent.getRecordKey() != null && academicRecordStudent.isNew()) {
                updateBadgesMap.put("AcademicRecordParentNotification/" + auth.getCurrentUser().getUid() + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_year_term + "/SingleRecords/" + recordKey + "/status", false);
                DatabaseReference updateLikeRef = mFirebaseDatabase.getReference("AcademicRecordParentNotification/" + sharedPreferencesManager.getMyUserID() + "/" + sharedPreferencesManager.getActiveKid().split(" ")[0] + "/count");
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

        super.onStop();
    }
}
