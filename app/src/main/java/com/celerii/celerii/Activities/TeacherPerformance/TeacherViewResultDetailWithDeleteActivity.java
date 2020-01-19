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
import com.celerii.celerii.adapters.TeacherViewResultDetailWithDeleteAdapter;
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

public class TeacherViewResultDetailWithDeleteActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    Toolbar toolbar;
    private ArrayList<KidScoreForTeachersModel> kidScoreForTeachersModelList;
    AcademicRecordTeacher academicRecordTeacher;
    public RecyclerView recyclerView;
    public TeacherViewResultDetailWithDeleteAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    boolean connected;
    String recordID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_result_detail_with_delete);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        Bundle bundle = getIntent().getExtras();
        recordID = bundle.getString("RecordID");

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

        academicRecordTeacher = new AcademicRecordTeacher();
        kidScoreForTeachersModelList = new ArrayList<>();
        mAdapter = new TeacherViewResultDetailWithDeleteAdapter(kidScoreForTeachersModelList, academicRecordTeacher, recordID, this);
        loadHeader();
//        loadFromFirebase();
        recyclerView.setAdapter(mAdapter);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
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
                        loadHeader();
                    }
                }
        );
    }

    private void loadHeader(){
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

                                    loadFromFirebase();
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

    private void loadFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecord").child("AcademicRecordTeacher-Student").child(mFirebaseUser.getUid()).child(recordID).child("Students");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    kidScoreForTeachersModelList.clear();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
