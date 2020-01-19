package com.celerii.celerii.Activities.StudentPerformance.History;

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
import com.celerii.celerii.adapters.StudentAcademicHistoryAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentAcademicHistoryRowModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;

public class StudentAcademicHistoryActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    private ArrayList<StudentAcademicHistoryRowModel> studentAcademicHistoryRowModelList;
    public RecyclerView recyclerView;
    public StudentAcademicHistoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String activeClass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_academic_history);

        sharedPreferencesManager = new SharedPreferencesManager(this);

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

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(sharedPreferencesManager.getActiveClass().split(" ")[1]); //TODO: Use class name, make dynamic
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        errorLayout.setVisibility(View.GONE);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        studentAcademicHistoryRowModelList = new ArrayList<>();
        loadDetailsFromFirebase();
        mAdapter = new StudentAcademicHistoryAdapter(studentAcademicHistoryRowModelList, this);
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

        mDatabaseReference = mFirebaseDatabase.getReference("Class Students/" + activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    studentAcademicHistoryRowModelList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final String childKey = postSnapshot.getKey();
                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Student child = dataSnapshot.getValue(Student.class);
                                final StudentAcademicHistoryRowModel studentAcademicHistoryRowModel = new StudentAcademicHistoryRowModel();
                                studentAcademicHistoryRowModel.setImageURL(child.getImageURL());
                                studentAcademicHistoryRowModel.setName(child.getFirstName() + " " + child.getLastName());
                                studentAcademicHistoryRowModel.setStudentID(childKey);

                                final String year = Date.getYear();

                                mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(childKey);
                                mDatabaseReference.orderByChild("academicYear").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        double score = 0;
                                        if (dataSnapshot.exists()) {
                                            double summer = 0;
                                            double counter = 0;
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                                summer = summer + Double.valueOf(academicRecordStudent.getScore());
                                                counter++;
                                            }
                                            score = (summer / counter);
                                        }
                                        studentAcademicHistoryRowModel.setAverage(String.valueOf(score));
                                        studentAcademicHistoryRowModelList.add(studentAcademicHistoryRowModel);
                                        mAdapter.notifyDataSetChanged();

                                        if (childrenCount == studentAcademicHistoryRowModelList.size()) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
