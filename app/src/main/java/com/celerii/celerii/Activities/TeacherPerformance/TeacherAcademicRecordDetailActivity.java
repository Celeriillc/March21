package com.celerii.celerii.Activities.TeacherPerformance;

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
import com.celerii.celerii.adapters.TeacherAcademicRecordDetailAdapter;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.AcademicRecordTeacher;
import com.celerii.celerii.models.Class;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherAcademicRecordDetailActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    Toolbar toolbar;
    private ArrayList<AcademicRecordTeacher> academicRecordTeacherList;
    public RecyclerView recyclerView;
    public TeacherAcademicRecordDetailAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String teacherID, subject, term, year, subject_year_term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_academic_record_detail);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        Bundle bundle = getIntent().getExtras();
        subject = bundle.getString("Subject");
        term = bundle.getString("Term");
        year = bundle.getString("Year");
        teacherID = bundle.getString("Active Teacher");
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

        academicRecordTeacherList = new ArrayList<>();
        mAdapter = new TeacherAcademicRecordDetailAdapter(academicRecordTeacherList, this);
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

    private void loadFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecord/AcademicRecordTeacher").child(teacherID);
        mDatabaseReference.orderByChild("subject_AcademicYear_Term").equalTo(subject_year_term).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    academicRecordTeacherList.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final AcademicRecordTeacher academicRecordTeacher = postSnapshot.getValue(AcademicRecordTeacher.class);
                        academicRecordTeacher.setRecordKey(postSnapshot.getKey());


                        mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(academicRecordTeacher.getClassID());
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    academicRecordTeacher.setClassName(classInstance.getClassName());
                                    academicRecordTeacherList.add(academicRecordTeacher);
                                }

                                if (childrenCount == academicRecordTeacherList.size()){
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
