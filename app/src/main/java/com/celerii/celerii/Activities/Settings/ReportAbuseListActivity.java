package com.celerii.celerii.Activities.Settings;

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
import com.celerii.celerii.adapters.ReportAbuseAdapter;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.ReportUserModel;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReportAbuseListActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;
    public RecyclerView recyclerView;

    Toolbar toolbar;
    private ArrayList<ReportUserModel> reportUserModelList;
    public ReportAbuseAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeAccount = "";
    String activeAccountID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_abuse_list);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        activeAccount = sharedPreferencesManager.getActiveAccount();
        activeAccountID = sharedPreferencesManager.getMyUserID();

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report Abuse");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        reportUserModelList = new ArrayList<>();
        mAdapter = new ReportAbuseAdapter(reportUserModelList, this);
        if (activeAccount.equals("Teacher")){
            loadFromFirebaseTeacher();
        } else if (activeAccount.equals("Parent")){
            loadFromFirebaseParent();
        }
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
                        if (activeAccount.equals("Teacher")){
                            loadFromFirebaseTeacher();
                        } else if (activeAccount.equals("Parent")){
                            loadFromFirebaseParent();
                        }
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    ArrayList<String> studentsList = new ArrayList<>();
    ArrayList<String> parentsList = new ArrayList<>();
    int studentCount = 0;
    private void loadFromFirebaseTeacher() {
        reportUserModelList.clear();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Student").child(activeAccountID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        studentsList.add(postSnapshot.getKey());
                    }

                    for (String student : studentsList) {
                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Parent").child(student);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                studentCount++;
                                if (dataSnapshot.exists()) {
                                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final String parentKey = postSnapshot.getKey();
                                        parentsList.add(parentKey);
                                    }
                                }

                                if (studentCount == studentsList.size()) {
                                    if (parentsList.size() > 0) {

                                        for (final String parentID : parentsList) {
                                            mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(parentID);
                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        if (dataSnapshot.exists()){
                                                            Parent parent = dataSnapshot.getValue(Parent.class);
                                                            ReportUserModel reportUser = new ReportUserModel(parent.getFirstName() + " " + parent.getLastName(), parent.getProfilePicURL(), parentID);
                                                            reportUserModelList.add(reportUser);
                                                        }

                                                        if (reportUserModelList.size() == parentsList.size()){
                                                            reportUserModelList.add(0, new ReportUserModel());
                                                            mAdapter.notifyDataSetChanged();
                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                            progressLayout.setVisibility(View.GONE);
                                                            recyclerView.setVisibility(View.VISIBLE);
                                                            errorLayout.setVisibility(View.GONE);
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

//        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Parent").child(activeAccountID);
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    reportUserModelList.clear();
//                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
//                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        final String parentKey = postSnapshot.getKey();
//
//                        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(parentKey);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()){
//                                    Parent parent = dataSnapshot.getValue(Parent.class);
//                                    ReportUserModel reportUser = new ReportUserModel(parent.getFirstName() + " " + parent.getLastName(), parent.getProfilePicURL(), parentKey);
//                                    reportUserModelList.add(reportUser);
//                                }
//
//                                if (childrenCount == reportUserModelList.size()){
//                                    reportUserModelList.add(0, new ReportUserModel());
//                                    mAdapter.notifyDataSetChanged();
//                                    mySwipeRefreshLayout.setRefreshing(false);
//                                    progressLayout.setVisibility(View.GONE);
//                                    recyclerView.setVisibility(View.VISIBLE);
//                                    errorLayout.setVisibility(View.GONE);
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
//                    mySwipeRefreshLayout.setRefreshing(false);
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

    ArrayList<String> teachersList = new ArrayList<>();
    private void loadFromFirebaseParent() {
        reportUserModelList.clear();
        studentsList = new ArrayList<>();
        studentCount = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(activeAccountID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        studentsList.add(postSnapshot.getKey());
                    }

                    for (String student : studentsList) {
                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Teacher").child(student);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                studentCount++;
                                if (dataSnapshot.exists()) {
                                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final String teacherKey = postSnapshot.getKey();
                                        teachersList.add(teacherKey);

                                    }
                                }

                                if (studentCount == studentsList.size()) {
                                    if (teachersList.size() > 0) {

                                        for (final String teacherID : teachersList) {
                                            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherID);
                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                        ReportUserModel reportUser = new ReportUserModel(teacher.getFirstName() + " " + teacher.getLastName(), teacher.getProfilePicURL(), teacherID);
                                                        reportUserModelList.add(reportUser);
                                                    }

                                                    if (reportUserModelList.size() == studentsList.size()) {
                                                        reportUserModelList.add(0, new ReportUserModel());
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
                                    } else {
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        recyclerView.setVisibility(View.GONE);
                                        progressLayout.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.VISIBLE);
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

//        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent Teacher").child(activeAccountID);
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    reportUserModelList.clear();
//                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
//                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        final String teacherKey = postSnapshot.getKey();
//
//                        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherKey);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()){
//                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
//                                    ReportUserModel reportUser = new ReportUserModel(teacher.getFirstName() + " " + teacher.getLastName(), teacher.getProfilePicURL(), teacherKey);
//                                    reportUserModelList.add(reportUser);
//                                }
//
//                                if (childrenCount == reportUserModelList.size()) {
//                                    reportUserModelList.add(0, new ReportUserModel());
//                                    mAdapter.notifyDataSetChanged();
//                                    mySwipeRefreshLayout.setRefreshing(false);
//                                    progressLayout.setVisibility(View.GONE);
//                                    recyclerView.setVisibility(View.VISIBLE);
//                                    errorLayout.setVisibility(View.GONE);
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
//                    mySwipeRefreshLayout.setRefreshing(false);
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
}
