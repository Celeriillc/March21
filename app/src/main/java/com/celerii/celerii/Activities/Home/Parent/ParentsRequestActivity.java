package com.celerii.celerii.Activities.Home.Parent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ClassNotificationAdapter;
import com.celerii.celerii.adapters.ParentRequestAdapter;
import com.celerii.celerii.adapters.TutorialsAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.ParentSchoolConnectionRequest;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Teacher;
import com.celerii.celerii.models.TeacherSchoolConnectionRequest;
import com.celerii.celerii.models.TutorialModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class ParentsRequestActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    Context context;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    String parentActivity;

    private ArrayList<ParentSchoolConnectionRequest> parentSchoolConnectionRequestList;
    private ArrayList<String> parentSchoolConnectionRequestListKeys;
    public RecyclerView recyclerView;
    public ParentRequestAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Toolbar toolbar;

    String featureUseKey = "";
    String featureName = "Parent Request";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents_request);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        context = this;

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            parentActivity = b.getString("parentActivity");
            if (parentActivity != null) {
                if (!parentActivity.isEmpty()) {
                    sharedPreferencesManager.setActiveAccount(parentActivity);
                    mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
                    mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue(parentActivity);
                }
            }
        }

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Requests");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        parentSchoolConnectionRequestList = new ArrayList<>();
        parentSchoolConnectionRequestListKeys = new ArrayList<>();
//        loadFromSharedPreferences();
        mAdapter = new ParentRequestAdapter(parentSchoolConnectionRequestList, context);
        recyclerView.setAdapter(mAdapter);
        loadFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ParentSearchActivity.class));
            }
        });
    }

    int counterStudents = 0;
    int counterSender = 0;
    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        updateBadges();
        mDatabaseReference = mFirebaseDatabase.getReference("Student Connection Request Recipients").child(mFirebaseUser.getUid());
        mDatabaseReference.orderByChild("requestStatus").equalTo("Pending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                parentSchoolConnectionRequestList.clear();
                mAdapter.notifyDataSetChanged();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ParentSchoolConnectionRequest parentSchoolConnectionRequest = postSnapshot.getValue(ParentSchoolConnectionRequest.class);
                        parentSchoolConnectionRequest.setSorttableTimeSent(Date.convertToSortableDate(parentSchoolConnectionRequest.getTimeSent()));
                        if (!parentSchoolConnectionRequestListKeys.contains(parentSchoolConnectionRequest.getRequestKey())) {
                            parentSchoolConnectionRequestListKeys.add(parentSchoolConnectionRequest.getRequestKey());
                        } else {
                            for(Iterator<ParentSchoolConnectionRequest> iterator = parentSchoolConnectionRequestList.iterator(); iterator.hasNext(); ) {
                                if(iterator.next().getRequestKey().equals(parentSchoolConnectionRequest.getRequestKey()))
                                    iterator.remove();
                            }
                        }
                        parentSchoolConnectionRequestList.add(parentSchoolConnectionRequest);
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference("Student Connection Request Sender").child(mFirebaseUser.getUid());
                mDatabaseReference.orderByChild("requestStatus").equalTo("Pending").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(Iterator<ParentSchoolConnectionRequest> iterator = parentSchoolConnectionRequestList.iterator(); iterator.hasNext(); ) {
                            if(iterator.next().getRequestSenderID().equals(mFirebaseUser.getUid()))
                                iterator.remove();
                        }
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                ParentSchoolConnectionRequest parentSchoolConnectionRequest = postSnapshot.getValue(ParentSchoolConnectionRequest.class);
                                parentSchoolConnectionRequest.setSorttableTimeSent(Date.convertToSortableDate(parentSchoolConnectionRequest.getTimeSent()));
                                if (!parentSchoolConnectionRequestListKeys.contains(parentSchoolConnectionRequest.getRequestKey())) {
                                    parentSchoolConnectionRequestListKeys.add(parentSchoolConnectionRequest.getRequestKey());
                                } else {
                                    for(Iterator<ParentSchoolConnectionRequest> iterator = parentSchoolConnectionRequestList.iterator(); iterator.hasNext(); ) {
                                        if(iterator.next().getRequestKey().equals(parentSchoolConnectionRequest.getRequestKey()))
                                            iterator.remove();
                                    }
                                }
                                parentSchoolConnectionRequestList.add(parentSchoolConnectionRequest);
                            }
                        }

                        if (parentSchoolConnectionRequestList.size() > 0) {
                            counterStudents = 0;
                            for (final ParentSchoolConnectionRequest parentSchoolConnectionRequest : parentSchoolConnectionRequestList) {
                                mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(parentSchoolConnectionRequest.getStudentID());
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        counterStudents++;
                                        if (dataSnapshot.exists()) {
                                            Student student = dataSnapshot.getValue(Student.class);
                                            parentSchoolConnectionRequest.setStudentName(student.getFirstName() + " " + student.getLastName());
                                            parentSchoolConnectionRequest.setStudentProfilePictureURL(student.getImageURL());
                                        }

                                        if (counterStudents == parentSchoolConnectionRequestList.size()) {
                                            counterSender = 0;
                                            for (final ParentSchoolConnectionRequest parentSchoolConnectionRequest : parentSchoolConnectionRequestList) {
                                                if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                                    mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(parentSchoolConnectionRequest.getRequestSenderID());
                                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            counterSender++;
                                                            if (dataSnapshot.exists()) {
                                                                School school = dataSnapshot.getValue(School.class);
                                                                parentSchoolConnectionRequest.setRequestSenderName(school.getSchoolName());
                                                                parentSchoolConnectionRequest.setRequestSenderProfilePictureURL(school.getProfilePhotoUrl());
                                                            }

                                                            if (counterSender == parentSchoolConnectionRequestList.size()) {
                                                                if (parentSchoolConnectionRequestList.size() > 1) {
                                                                    Collections.sort(parentSchoolConnectionRequestList, new Comparator<ParentSchoolConnectionRequest>() {
                                                                        @Override
                                                                        public int compare(ParentSchoolConnectionRequest o1, ParentSchoolConnectionRequest o2) {
                                                                            return o1.getSorttableTimeSent().compareTo(o2.getSorttableTimeSent());
                                                                        }
                                                                    });
                                                                }

                                                                Collections.reverse(parentSchoolConnectionRequestList);

                                                                mAdapter.notifyDataSetChanged();
                                                                mySwipeRefreshLayout.setRefreshing(false);
                                                                progressLayout.setVisibility(View.GONE);
                                                                errorLayout.setVisibility(View.GONE);
                                                                recyclerView.setVisibility(View.VISIBLE);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                } else {
                                                    mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(parentSchoolConnectionRequest.getRequestSenderID());
                                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            counterSender++;
                                                            if (dataSnapshot.exists()) {
                                                                Parent parent = dataSnapshot.getValue(Parent.class);
                                                                parentSchoolConnectionRequest.setRequestSenderName(parent.getFirstName() + " " + parent.getLastName());
                                                                parentSchoolConnectionRequest.setRequestSenderProfilePictureURL(parent.getProfilePicURL());
                                                            }

                                                            if (counterSender == parentSchoolConnectionRequestList.size()) {
                                                                if (parentSchoolConnectionRequestList.size() > 1) {
                                                                    Collections.sort(parentSchoolConnectionRequestList, new Comparator<ParentSchoolConnectionRequest>() {
                                                                        @Override
                                                                        public int compare(ParentSchoolConnectionRequest o1, ParentSchoolConnectionRequest o2) {
                                                                            return o1.getSorttableTimeSent().compareTo(o2.getSorttableTimeSent());
                                                                        }
                                                                    });
                                                                }

                                                                Collections.reverse(parentSchoolConnectionRequestList);

                                                                mAdapter.notifyDataSetChanged();
                                                                mySwipeRefreshLayout.setRefreshing(false);
                                                                progressLayout.setVisibility(View.GONE);
                                                                errorLayout.setVisibility(View.GONE);
                                                                recyclerView.setVisibility(View.VISIBLE);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        } else {
                            mAdapter.notifyDataSetChanged();
                            mySwipeRefreshLayout.setRefreshing(false);
                            progressLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                            mySwipeRefreshLayout.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                            errorLayoutText.setText(Html.fromHtml("You don't have any pending requests at this time, click the " + "<b>" + "Find my child" + "</b>" + " button below to send a connection request"));
                            errorLayoutButton.setText("Find my child");
                            errorLayoutButton.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
            if (parentActivity != null) {
                Intent i = new Intent(this, ParentMainActivityTwo.class);
                startActivity(i);
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (parentActivity != null) {
            Intent i = new Intent(this, ParentMainActivityTwo.class);
            startActivity(i);
        }
    }

    public void updateBadges(){
        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
        updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(updateBadgesMap);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
        UpdateDataFromFirebase.populateEssentials(context);
    }

    @Override
    public void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }
}