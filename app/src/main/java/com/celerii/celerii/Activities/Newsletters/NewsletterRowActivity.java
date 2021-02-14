package com.celerii.celerii.Activities.Newsletters;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.NewsletterRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.NewsletterRow;
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

public class NewsletterRowActivity extends AppCompatActivity {
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
    private ArrayList<NewsletterRow> newsletterRowList;
    public RecyclerView recyclerView;
    public NewsletterRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String newsletterAccountType, accountType;
    int childrenCounter = 0;

    String featureUseKey = "";
    String featureName = "Newsletter Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsletter_row);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        accountType = sharedPreferencesManager.getActiveAccount();
        if (accountType.equals("Teacher")){
            newsletterAccountType = "Teacher Newsletters";
        } else if (accountType.equals("Parent")) {
            newsletterAccountType = "Parent Newsletters";
        }

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Newsletter");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        newsletterRowList = new ArrayList<>();
        loadNewslettersFromFirebase();
        mAdapter = new NewsletterRowAdapter(newsletterRowList, this);
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadNewslettersFromFirebase();
                    }
                }
        );
    }

    private void loadNewslettersFromFirebase() {
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
        mDatabaseReference = mFirebaseDatabase.getReference().child(newsletterAccountType).child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    newsletterRowList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        final String newsletterKey = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Newsletter").child(newsletterKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final NewsletterRow newsletterRow = dataSnapshot.getValue(NewsletterRow.class);
                                    newsletterRow.setNewsletterKey(dataSnapshot.getKey());

                                    String schoolID = newsletterRow.getSchoolID();
                                    mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            childrenCounter++;
                                            if (dataSnapshot.exists()){
                                                School schoolInstance = dataSnapshot.getValue(School.class);
                                                newsletterRow.setSchoolID(schoolInstance.getSchoolName());
                                            } else {
                                                newsletterRow.setSchoolID("This school account has been deleted or doesn't exist");
                                            }
//                                            mAdapter.notifyDataSetChanged();
                                            newsletterRowList.add(newsletterRow);

                                            if (childrenCount == childrenCounter) {
                                                if (newsletterRowList.size() > 0) {
                                                    if (newsletterRowList.size() > 1) {
                                                        Collections.sort(newsletterRowList, new Comparator<NewsletterRow>() {
                                                            @Override
                                                            public int compare(NewsletterRow o1, NewsletterRow o2) {
                                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                            }
                                                        });
                                                    }

                                                    Collections.reverse(newsletterRowList);
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
                                                        errorLayoutText.setText("You don't have any published newsletters from your kid's school yet");
                                                    } else {
                                                        errorLayoutText.setText("You don't have any published newsletters from your school yet");
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                } else {
                                    childrenCounter++;
                                    if (childrenCount == childrenCounter) {
                                        if (newsletterRowList.size() > 1) {
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
                                                errorLayoutText.setText("You don't have any published newsletters from your kid's school yet");
                                            } else {
                                                errorLayoutText.setText("You don't have any published newsletters from your school yet");
                                            }
                                        }
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
                    if (accountType.equals("Parent")) {
                        errorLayoutText.setText("You don't have any published newsletters from your kid's school yet");
                    } else {
                        errorLayoutText.setText("You don't have any published newsletters from your school yet");
                    }
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
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
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

    public void updateBadges(){
        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Newsletter/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
        } else {
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/Newsletter/status", false);
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/Notifications/status", false);
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/More/status", false);
        }

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(updateBadgesMap);
    }

    @Override
    protected void onStop() {
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
