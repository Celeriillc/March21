package com.celerii.celerii.Activities.Events;

import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.EventRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.EventsRow;
import com.celerii.celerii.models.School;
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

public class EventsRowActivity extends AppCompatActivity {
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
    private ArrayList<EventsRow> eventsRowList;
    public RecyclerView recyclerView;
    public EventRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String eventAccountType, accountType;
    String todaysDate = "";
    int childrenCounter = 0;

    String featureUseKey = "";
    String featureName = "Event Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_row);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        accountType = sharedPreferencesManager.getActiveAccount();
        if (accountType.equals("Teacher")){
            eventAccountType = "Teacher Events";
        } else if (accountType.equals("Parent")) {
            eventAccountType = "Parent Events";
        }

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Events");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        eventsRowList = new ArrayList<>();
        todaysDate = (Date.getDate());
        mAdapter = new EventRowAdapter(eventsRowList, this);
        recyclerView.setAdapter(mAdapter);
        loadEventsFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadEventsFromFirebase();
                    }
                }
        );
    }

    private void loadEventsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        updateBadges();
        childrenCounter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child(eventAccountType).child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventsRowList.clear();
                    mAdapter.notifyDataSetChanged();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        final String eventKey = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Event").child(eventKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final EventsRow eventRow = dataSnapshot.getValue(EventsRow.class);
                                    eventRow.setKey(dataSnapshot.getKey());

                                    String schoolID = eventRow.getSchoolID();
                                    mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            childrenCounter++;
                                            if (dataSnapshot.exists()){
                                                School schoolInstance = dataSnapshot.getValue(School.class);
                                                eventRow.setSchoolID(schoolInstance.getSchoolName());
//                                                mAdapter.notifyDataSetChanged();
                                            } else {
                                                eventRow.setSchoolID("This school account has been deleted or doesn't exist");
//                                                mAdapter.notifyDataSetChanged();
                                            }

                                            String eventDate = eventRow.getEventDate();
                                            eventRow.setEventSortableDate(Date.convertToSortableDate(eventRow.getEventDate()));
                                            if (Date.compareDates(eventDate, todaysDate)) {
                                                eventsRowList.add(eventRow);
                                            }

                                            if (childrenCount == childrenCounter) {
                                                if (eventsRowList.size() > 0) {
                                                    if (eventsRowList.size() > 1) {
                                                        Collections.sort(eventsRowList, new Comparator<EventsRow>() {
                                                            @Override
                                                            public int compare(EventsRow o1, EventsRow o2) {
                                                                return o1.getEventSortableDate().compareTo(o2.getEventSortableDate());
                                                            }
                                                        });
                                                    }

                                                    Collections.reverse(eventsRowList);
                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                } else {
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    recyclerView.setVisibility(View.GONE);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.VISIBLE);
                                                    if (accountType.equals("Parent")) {
                                                        errorLayoutText.setText("You don't have any scheduled events from your kid's school yet");
                                                    } else {
                                                        errorLayoutText.setText("You don't have any scheduled events from your school yet");
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            recyclerView.setVisibility(View.GONE);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.VISIBLE);
                                            errorLayoutText.setText(message);
                                            return;
                                        }
                                    });

                                } else {
                                    childrenCounter++;
                                    if (childrenCount == childrenCounter) {
                                        if (eventsRowList.size() > 1) {
                                            mAdapter.notifyDataSetChanged();
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                        } else {
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            recyclerView.setVisibility(View.GONE);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.VISIBLE);
                                            if (accountType.equals("Parent")) {
                                                errorLayoutText.setText("You don't have any scheduled events from your kid's school yet");
                                            } else {
                                                errorLayoutText.setText("You don't have any scheduled events from your school yet");
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                mySwipeRefreshLayout.setRefreshing(false);
                                recyclerView.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                                errorLayoutText.setText(message);
                                return;
                            }
                        });
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    if (accountType.equals("Parent")) {
                        errorLayoutText.setText("You don't have any scheduled events from your kid's school yet");
                    } else {
                        errorLayoutText.setText("You don't have any scheduled events from your school yet");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(message);
                return;
            }
        });
    }

    public void updateBadges(){
        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Events/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
        } else {
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/Events/status", false);
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/Notifications/status", false);
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/More/status", false);
        }

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(updateBadgesMap);
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

        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
