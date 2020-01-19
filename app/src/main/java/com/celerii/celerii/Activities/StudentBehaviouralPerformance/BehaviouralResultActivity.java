package com.celerii.celerii.Activities.StudentBehaviouralPerformance;

import android.content.Intent;
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
import com.celerii.celerii.adapters.BehaviouralResultAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.BehaviouralRecordModel;
import com.celerii.celerii.models.BehaviouralResultRowModel;
import com.celerii.celerii.models.BehaviouralResultsHeaderModel;
import com.celerii.celerii.models.Class;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class BehaviouralResultActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    Bundle bundle;

    Toolbar toolbar;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<BehaviouralResultRowModel> behaviouralResultRowModelList;
    private BehaviouralResultsHeaderModel behaviouralResultsHeaderModel;
    public RecyclerView recyclerView;
    public BehaviouralResultAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeStudentID = "", year, term;
    String activeStudent;
    String activeStudentName;
    int totalPointsEarned, totalPointsFined, pointsEarnedThisTerm, pointsFinedThisTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavioural_result);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        bundle = getIntent().getExtras();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        activeStudentID = bundle.getString("childID");
        activeStudentName = bundle.getString("childName");
        if (activeStudentID == null){
            Set<String> childrenSet = sharedPreferencesManager.getMyChildren();
            ArrayList<String> children = new ArrayList<>();
            if (sharedPreferencesManager.getActiveAccount().equals("Parent") && childrenSet != null) {
                children = new ArrayList<>(childrenSet);
                activeStudent = children.get(0);
                sharedPreferencesManager.setActiveKid(activeStudent);
                activeStudentID = activeStudent.split(" ")[0];
                activeStudentName = activeStudent.split(" ")[1];
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    errorLayoutText.setText("You're not connected to any child account yet. Use the search button to search for your child and request connection from their school.");
                } else {
                    errorLayoutText.setText("It seems like you do not have the permission to view this child's academic record");
                }
                return;
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(activeStudentName + "'s Behavioural Performance");
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        behaviouralResultsHeaderModel = new BehaviouralResultsHeaderModel();
        behaviouralResultRowModelList = new ArrayList<>();
        loadDetailsFromFirebase();
        mAdapter = new BehaviouralResultAdapter(behaviouralResultRowModelList, behaviouralResultsHeaderModel, this);
        recyclerView.setAdapter(mAdapter);

        year= Date.getYear();
        term = Term.getTermShort();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadDetailsFromFirebase();
                    }
                }
        );

    }

    int counter;
    void loadDetailsFromFirebase(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        behaviouralResultRowModelList.clear();
        totalPointsEarned = 0;
        totalPointsFined = 0;
        pointsEarnedThisTerm = 0;
        pointsFinedThisTerm = 0;

        mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(activeStudentID).child("Reward");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totalPointsEarned = (int) dataSnapshot.getChildrenCount();
                }

                mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(activeStudentID).child("Punishment");
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            totalPointsFined = (int) dataSnapshot.getChildrenCount();
                        }

                        final String year_term = year + "_" + term;

                        mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(activeStudentID).child("Reward");
                        mDatabaseReference.orderByChild("academicYear_Term").equalTo(year_term).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    pointsEarnedThisTerm = (int) dataSnapshot.getChildrenCount();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        BehaviouralRecordModel behaviouralRecordModel = postSnapshot.getValue(BehaviouralRecordModel.class);
                                        behaviouralResultRowModelList.add(new BehaviouralResultRowModel("+1", behaviouralRecordModel.getRewardDescription(), "", behaviouralRecordModel.getClassID(), behaviouralRecordModel.getSortableDate()));
                                    }
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(activeStudentID).child("Punishment");
                                mDatabaseReference.orderByChild("academicYear_Term").equalTo(year_term).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            pointsFinedThisTerm = (int) dataSnapshot.getChildrenCount();
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                BehaviouralRecordModel behaviouralRecordModel = postSnapshot.getValue(BehaviouralRecordModel.class);
                                                behaviouralResultRowModelList.add(new BehaviouralResultRowModel("-1", behaviouralRecordModel.getRewardDescription(), "", behaviouralRecordModel.getClassID(), behaviouralRecordModel.getSortableDate()));
                                            }
                                        }

                                        behaviouralResultsHeaderModel.setTerm(term);
                                        behaviouralResultsHeaderModel.setYear(year);
                                        behaviouralResultsHeaderModel.setTotalPointsEarned(String.valueOf(totalPointsEarned));
                                        behaviouralResultsHeaderModel.setTotalPointsFined(String.valueOf(totalPointsFined));
                                        behaviouralResultsHeaderModel.setPointsEarnedThisTerm(String.valueOf(pointsEarnedThisTerm));
                                        behaviouralResultsHeaderModel.setPointsFinedThisTerm(String.valueOf(pointsFinedThisTerm));

                                        if (behaviouralResultRowModelList.size() >= 1) {
                                            counter = 0;
                                            for (final BehaviouralResultRowModel behaviouralResultRowModel : behaviouralResultRowModelList) {
                                                mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(behaviouralResultRowModel.getClassID());
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        counter++;
                                                        if (dataSnapshot.exists()) {
                                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                                            behaviouralResultRowModel.setClassName(classInstance.getClassName());
                                                        }

                                                        if (counter == behaviouralResultRowModelList.size()) {
                                                            Collections.sort(behaviouralResultRowModelList, new Comparator<BehaviouralResultRowModel>() {
                                                                @Override
                                                                public int compare(BehaviouralResultRowModel o1, BehaviouralResultRowModel o2) {
                                                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                                }
                                                            });

                                                            behaviouralResultRowModelList.add(0, new BehaviouralResultRowModel());
                                                            mAdapter.notifyDataSetChanged();
                                                            recyclerView.setVisibility(View.VISIBLE);
                                                            progressLayout.setVisibility(View.GONE);
                                                            errorLayout.setVisibility(View.GONE);
                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                        } else {
                                            behaviouralResultRowModelList.add(new BehaviouralResultRowModel());
                                            mAdapter.notifyDataSetChanged();
                                            recyclerView.setVisibility(View.VISIBLE);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            mySwipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if(resultCode == RESULT_OK) {
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                term = data.getStringExtra("Selected Term");
                loadDetailsFromFirebase();
            }
        }

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                year = data.getStringExtra("Selected Year");
                loadDetailsFromFirebase();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
