package com.celerii.celerii.Activities.Events;

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
import com.celerii.celerii.adapters.EventRowAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
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

public class EventsRowActivity extends AppCompatActivity {

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
    String string = "Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor  Lorem ipsum dolor sit amet, nsectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor";
    String eventAccountType, accountType;
    SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_row);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        accountType = sharedPreferencesManager.getActiveAccount();
        if (accountType.equals("Teacher")){
            eventAccountType = "Teacher Events";
        } else if (accountType.equals("Parent")) {
            eventAccountType = "Parent Events";
        }

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

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
        loadEventsFromFirebase();
        mAdapter = new EventRowAdapter(eventsRowList, this);
        recyclerView.setAdapter(mAdapter);

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

        mDatabaseReference = mFirebaseDatabase.getReference().child(eventAccountType).child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    eventsRowList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final String eventKey = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Event").child(eventKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    final EventsRow eventRow = dataSnapshot.getValue(EventsRow.class);
                                    eventRow.setKey(dataSnapshot.getKey());

                                    String schoolID = eventRow.getSchool();
                                    mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                School schoolInstance = dataSnapshot.getValue(School.class);
                                                eventRow.setSchool(schoolInstance.getSchoolName());
                                                mAdapter.notifyDataSetChanged();
                                            } else {
                                                eventRow.setSchool("This school account has been deleted or doesn't exist");
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    eventsRowList.add(eventRow);
                                    mAdapter.notifyDataSetChanged();
                                }
                                mySwipeRefreshLayout.setRefreshing(false);
                                progressLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
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
                    if (accountType.equals("Parent")) {
                        errorLayoutText.setText("You don't have any scheduled events from your kid's school yet");
                    } else {
                        errorLayoutText.setText("You don't have any scheduled events from your school yet");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void yeah(){
        EventsRow eventsRow = new EventsRow("Open Day", "Thursday, Mar 23, 2019", string, "Lorem Ipsum High");
        eventsRowList.add(eventsRow);

        eventsRow = new EventsRow("Open Day", "Thursday, Mar 23, 2019", string, "Lorem Ipsum High");
        eventsRowList.add(eventsRow);

        eventsRow = new EventsRow("Open Day", "Thursday, Mar 23, 2019", string, "Lorem Ipsum High");
        eventsRowList.add(eventsRow);
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
