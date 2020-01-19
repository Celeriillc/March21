package com.celerii.celerii.Activities.StudentAttendance;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ParentAttendanceRowAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Month;
import com.celerii.celerii.helperClasses.ParentCheckAttendanceSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ParentAttendanceHeader;
import com.celerii.celerii.models.ParentAttendanceRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class ParentAttendanceActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    ParentCheckAttendanceSharedPreferences parentCheckAttendanceSharedPreferences;

    Toolbar toolbar;
    private ArrayList<ParentAttendanceRow> parentAttendanceRowList;
    private ParentAttendanceHeader parentAttendanceHeader;
    public RecyclerView recyclerView;
    public ParentAttendanceRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String mMonth;
    int thisMonth;
    int thisYear;
    String subject, term, month, year, month_year, term_year, subject_term_year;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    String childID, classID;
    String childsFirstName;
    String searchTerm;
    String className, school, teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_attendance);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        parentCheckAttendanceSharedPreferences = new ParentCheckAttendanceSharedPreferences(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        String activeKid = b.getString("Child ID");
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        if (activeKid == null) {
            Set<String> childrenSet = sharedPreferencesManager.getMyChildren();
            ArrayList<String> children = new ArrayList<>();
            if (childrenSet != null) {
                children = new ArrayList<>(childrenSet);
                activeKid = children.get(0);
                sharedPreferencesManager.setActiveKid(activeKid);
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText("You're not connected to any child account yet. Use the search button to search for your child and request connection from their school.");
                return;
            }
        }

//        String[] activeKid = b.getString("Child ID").split(" ");
        childID = activeKid.split(" ")[0];
        childsFirstName = activeKid.split(" ")[1];

        Calendar calendar = Calendar.getInstance();
        thisYear = calendar.get(Calendar.YEAR);
        thisMonth = calendar.get(Calendar.MONTH);
        mMonth = Month.Month(thisMonth);
        subject = "General";

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(childsFirstName + "'s Attendance");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        parentAttendanceHeader = new ParentAttendanceHeader("July", "2018", subject);
        parentAttendanceRowList = new ArrayList<>();
        loadHeader();
        loadFromFirebase();
        mAdapter = new ParentAttendanceRowAdapter(parentAttendanceRowList, parentAttendanceHeader, this, this);
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );
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

        subject = parentCheckAttendanceSharedPreferences.getSubject();
        year = Date.getYear();
        month = Date.getMonth();
        term = Term.getTermShort();
        month_year = month + "_" + year;
        term_year = term + "_" + year;
        subject_term_year = subject + "_" + term + "_" + year;

        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Class").child(childID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        classID = postSnapshot.getKey();
                        parentAttendanceHeader.setClassID(classID);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        parentAttendanceHeader.setStudentID(childID);
        parentAttendanceHeader.setSubject(subject);
        parentAttendanceHeader.setYear(year);
        parentAttendanceHeader.setTerm(term);
    }

    private void loadFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("AttendenceStudent").child(childID);
        mDatabaseReference.orderByChild("subject_term_year").equalTo(subject_term_year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    parentAttendanceRowList.clear();
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final ParentAttendanceRow parentAttendanceRow = postSnapshot.getValue(ParentAttendanceRow.class);
                        parentAttendanceRow.setKey(postSnapshot.getKey());
                        parentAttendanceRow.setStudentID(childID);
                        parentAttendanceHeader.setClassID(parentAttendanceRow.getClassID());

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(parentAttendanceRow.getClassID());
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    parentAttendanceRow.setClassName(classInstance.getClassName());
                                    parentAttendanceRowList.add(parentAttendanceRow);
                                }

                                if (childrenCount == parentAttendanceRowList.size()){
                                    parentAttendanceRowList.add(0, new ParentAttendanceRow());
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    mAdapter.notifyDataSetChanged();
                                    errorLayout.setVisibility(View.GONE);
                                    progressLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                } else {
                    parentAttendanceRowList.clear();
                    parentAttendanceRowList.add(0, new ParentAttendanceRow());
                    mySwipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mDatabaseReference = mFirebaseDatabase.getReference().child("AttendenceStudent").child(childID);
//        mDatabaseReference.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    parentAttendanceRowList.clear();
//                    parentAttendanceRowList.add(new ParentAttendanceRow());
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        TeacherAttendanceRow latestAttendance = postSnapshot.getValue(TeacherAttendanceRow.class);
//                        parentAttendanceHeader.setStudentID(childID);
//                        parentAttendanceHeader.setMonth(Month.Month(Integer.parseInt(latestAttendance.getMonth()) - 1));
//                        parentAttendanceHeader.setYear(latestAttendance.getYear());
//                        parentAttendanceHeader.setSubject(latestAttendance.getSubject());
//                        mAdapter.notifyDataSetChanged();
//
//                        mDatabaseReference = mFirebaseDatabase.getReference().child("AttendenceStudent").child(childID);
//                        searchTerm = latestAttendance.getMonth() + "_" + latestAttendance.getYear();
//                        mDatabaseReference.orderByChild("month_year").equalTo(searchTerm).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                        final ParentAttendanceRow parentAttendanceRow = postSnapshot.getValue(ParentAttendanceRow.class);
//                                        parentAttendanceRow.setKey(postSnapshot.getKey());
//                                        parentAttendanceRow.setStudentID(childID);
//                                        parentAttendanceRowList.add(parentAttendanceRow);
//                                        mAdapter.notifyDataSetChanged();
//
//                                        errorLayout.setVisibility(View.GONE);
//                                        progressLayout.setVisibility(View.GONE);
//                                        recyclerView.setVisibility(View.VISIBLE);
//                                    }
//                                } else {
//                                    recyclerView.setVisibility(View.GONE);
//                                    progressLayout.setVisibility(View.GONE);
//                                    errorLayout.setVisibility(View.VISIBLE);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                } else {
//                    recyclerView.setVisibility(View.GONE);
//                    progressLayout.setVisibility(View.GONE);
//                    errorLayout.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.parent_attendance_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        } else if (id == R.id.pickdate){

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                subject = data.getStringExtra("Selected Subject");
                subject_term_year = subject + "_" + term + "_" + year;
                parentAttendanceHeader.setSubject(subject);
                mAdapter.notifyDataSetChanged();
                loadFromFirebase();
            }
        }
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                term = data.getStringExtra("Selected Term");
                term_year = term + "_" + year;
                subject_term_year = subject + "_" + term + "_" + year;
                parentAttendanceHeader.setTerm(term);
                mAdapter.notifyDataSetChanged();
                loadFromFirebase();
            }
        }
        if (requestCode == 3) {
            if(resultCode == RESULT_OK) {
                year = data.getStringExtra("Selected Year");
                term_year = term + "_" + year;
                subject_term_year = subject + "_" + term + "_" + year;
                parentAttendanceHeader.setYear(year);
                mAdapter.notifyDataSetChanged();
                loadFromFirebase();
            }
        }
    }
}
