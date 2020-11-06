package com.celerii.celerii.Activities.Home.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TeacherRequestAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.TeacherSchoolConnectionRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class TeacherRequestActivity extends AppCompatActivity {
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

    private ArrayList<TeacherSchoolConnectionRequest> teacherSchoolConnectionRequestList;
    public RecyclerView recyclerView;
    public TeacherRequestAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Toolbar toolbar;

    String featureUseKey = "";
    String featureName = "Teacher Request";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_request);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        context = this;

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            parentActivity = b.getString("parentActivity");
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

        teacherSchoolConnectionRequestList = new ArrayList<>();
        mAdapter = new TeacherRequestAdapter(teacherSchoolConnectionRequestList, context);
        recyclerView.setAdapter(mAdapter);
        loadSchoolToTeacherRequestFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadSchoolToTeacherRequestFromFirebase();
                    }
                }
        );

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });
    }

    int counterSchools = 0;
    int loopControl = 0;
    int loopControlTwo = 0;
    private void loadSchoolToTeacherRequestFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        updateBadges();
        counterSchools = 0;
        loopControl = 0;
        loopControlTwo = 0;
        mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    teacherSchoolConnectionRequestList.clear();
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String schoolKey = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(schoolKey);
                        mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                loopControl++;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        TeacherSchoolConnectionRequest teacherSchoolConnectionRequest = postSnapshot.getValue(TeacherSchoolConnectionRequest.class);
                                        teacherSchoolConnectionRequest.setSender(schoolKey);
                                        teacherSchoolConnectionRequest.setSorttableTimeSent(teacherSchoolConnectionRequest.getTimeSent());
                                        teacherSchoolConnectionRequestList.add(teacherSchoolConnectionRequest);
                                    }
                                }

                                if (loopControl == childrenCount) {
                                    loadTeacherToSchoolRequestsFromFirebase();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    loadTeacherToSchoolRequestsFromFirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void loadTeacherToSchoolRequestsFromFirebase () {
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String schoolKey = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid()).child(schoolKey);
                        mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                loopControlTwo++;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        TeacherSchoolConnectionRequest teacherSchoolConnectionRequest = postSnapshot.getValue(TeacherSchoolConnectionRequest.class);
                                        teacherSchoolConnectionRequest.setSender(mFirebaseUser.getUid());
                                        teacherSchoolConnectionRequest.setSorttableTimeSent(teacherSchoolConnectionRequest.getTimeSent());
                                        teacherSchoolConnectionRequestList.add(teacherSchoolConnectionRequest);
                                    }
                                }

                                if (loopControlTwo == childrenCount) {
                                    loadSchoolInformationFromFirebase();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    loadSchoolInformationFromFirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void loadSchoolInformationFromFirebase() {
        if (teacherSchoolConnectionRequestList.size() > 0) {
            counterSchools = 0;
            for (final TeacherSchoolConnectionRequest teacherSchoolConnectionRequest : teacherSchoolConnectionRequestList) {
                mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(teacherSchoolConnectionRequest.getSchool());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        counterSchools++;
                        if (dataSnapshot.exists()) {
                            School school = dataSnapshot.getValue(School.class);
                            teacherSchoolConnectionRequest.setSchoolName(school.getSchoolName());
                            teacherSchoolConnectionRequest.setSchoolProfilePictureURL(school.getProfilePhotoUrl());
                        }

                        if (counterSchools == teacherSchoolConnectionRequestList.size()) {
                            if (teacherSchoolConnectionRequestList.size() > 1) {
                                Collections.sort(teacherSchoolConnectionRequestList, new Comparator<TeacherSchoolConnectionRequest>() {
                                    @Override
                                    public int compare(TeacherSchoolConnectionRequest o1, TeacherSchoolConnectionRequest o2) {
                                        return o1.getSorttableTimeSent().compareTo(o2.getSorttableTimeSent());
                                    }
                                });
                            }

                            Collections.reverse(teacherSchoolConnectionRequestList);

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
        } else {
            mAdapter.notifyDataSetChanged();
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            mySwipeRefreshLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText(Html.fromHtml("You don't have any pending requests at this time, click the " + "<b>" + "Find my school" + "</b>" + " button below to send a connection request"));
            errorLayoutButton.setText("Find my school");
            errorLayoutButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (parentActivity != null) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
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
            Intent i = new Intent(this, TeacherMainActivityTwo.class);
            startActivity(i);
        }
    }

    public void updateBadges(){
        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
        updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/Notifications/status", false);
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
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;

        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();
        String mFirebaseUserID = mFirebaseUser.getUid();

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        DatabaseReference featureUseUpdateRef = FirebaseDatabase.getInstance().getReference();
        featureUseUpdateRef.updateChildren(featureUseUpdateMap);
    }
}