package com.celerii.celerii.Activities.StudentBehaviouralPerformance;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.BehaviouralResultAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.BehaviouralRecordModel;
import com.celerii.celerii.models.BehaviouralResultRowModel;
import com.celerii.celerii.models.BehaviouralResultsHeaderModel;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BehaviouralResultActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    Bundle bundle;

    Toolbar toolbar;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    private ArrayList<BehaviouralResultRowModel> behaviouralResultRowModelList;
    private BehaviouralResultsHeaderModel behaviouralResultsHeaderModel;
    public RecyclerView recyclerView;
    public BehaviouralResultAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeStudentID = "", year, term;
    String activeStudent;
    String activeStudentName;
    String parentActivity;
    int totalPointsEarned, totalPointsFined, pointsEarnedThisTerm, pointsFinedThisTerm;
    int isNewCounter = 0;

    Handler internetConnectionHandler = new Handler();
    Runnable internetConnectionRunnable;

    String featureUseKey = "";
    String featureName = "Behavioural Records";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavioural_result);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        parentActivity = bundle.getString("parentActivity");
        if (parentActivity != null) {
            if (!parentActivity.isEmpty()) {
                sharedPreferencesManager.setActiveAccount(parentActivity);
                mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
                mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue(parentActivity);
            }
        }

        activeStudent = bundle.getString("ChildID");
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ParentSearchActivity.class));
            }
        });

        if (activeStudent == null) {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Gson gson = new Gson();
                ArrayList<Student> myChildren = new ArrayList<>();
                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                Type type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                myChildren = gson.fromJson(myChildrenJSON, type);

                if (myChildren != null) {
                    if (myChildren.size() > 0) {
                        gson = new Gson();
                        activeStudent = gson.toJson(myChildren.get(0));
                        sharedPreferencesManager.setActiveKid(activeStudent);
                    } else {
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setTitle("Behavioural Performance");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setDisplayShowTitleEnabled(true);
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                        return;
                    }
                } else {
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle("Behavioural Performance");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                    return;
                }
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Behavioural Performance");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText("We couldn't find this student's account");
                return;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {
                }.getType();
                Student activeKidModel = gson.fromJson(activeStudent, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student : myChildren) {
                    if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                        activeKidExist = true;
                        activeKidModel = student;
                        activeStudent = gson.toJson(activeKidModel);
                        sharedPreferencesManager.setActiveKid(activeStudent);
                        break;
                    }
                }

                if (!activeKidExist) {
                    if (myChildren.size() > 0) {
                        if (myChildren.size() > 1) {
                            gson = new Gson();
                            activeStudent = gson.toJson(myChildren.get(0));
                            sharedPreferencesManager.setActiveKid(activeStudent);
                        }
                    } else {
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setTitle("Behavioural Performance");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setDisplayShowTitleEnabled(true);
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeStudentModel = gson.fromJson(activeStudent, type);

        activeStudentID = activeStudentModel.getStudentID();
        activeStudentName = activeStudentModel.getFirstName() + " " + activeStudentModel.getLastName();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(activeStudentName.trim() + "'s Behavioural Performance");
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        behaviouralResultsHeaderModel = new BehaviouralResultsHeaderModel();
        behaviouralResultRowModelList = new ArrayList<>();
        mAdapter = new BehaviouralResultAdapter(behaviouralResultRowModelList, behaviouralResultsHeaderModel, this);
        recyclerView.setAdapter(mAdapter);
        loadDetailsFromFirebase();

        year = Date.getYear();
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
//        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
//            mySwipeRefreshLayout.setRefreshing(false);
//            recyclerView.setVisibility(View.GONE);
//            progressLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
//            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
//            return;
//        }
        internetConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
        internetConnectionHandler.postDelayed(internetConnectionRunnable, 7000);

        updateBadges();
        behaviouralResultRowModelList.clear();
        mAdapter.notifyDataSetChanged();
        totalPointsEarned = 0;
        totalPointsFined = 0;
        pointsEarnedThisTerm = 0;
        pointsFinedThisTerm = 0;
        isNewCounter = 0;

        mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(activeStudentID).child("Reward");
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    totalPointsEarned = (int) dataSnapshot.getChildrenCount();
                }

                mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(activeStudentID).child("Punishment");
                mDatabaseReference.keepSynced(true);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            totalPointsFined = (int) dataSnapshot.getChildrenCount();
                        }

                        final String year_term = year + "_" + term;

                        mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(activeStudentID).child("Reward");
                        mDatabaseReference.keepSynced(true);
                        mDatabaseReference.orderByChild("academicYear_Term").equalTo(year_term).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    pointsEarnedThisTerm = (int) dataSnapshot.getChildrenCount();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        BehaviouralRecordModel behaviouralRecordModel = postSnapshot.getValue(BehaviouralRecordModel.class);
                                        behaviouralResultRowModelList.add(new BehaviouralResultRowModel(postSnapshot.getKey(), "+1", behaviouralRecordModel.getRewardDescription(), "", behaviouralRecordModel.getClassID(), behaviouralRecordModel.getSortableDate()));
                                    }
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(activeStudentID).child("Punishment");
                                mDatabaseReference.keepSynced(true);
                                mDatabaseReference.orderByChild("academicYear_Term").equalTo(year_term).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            pointsFinedThisTerm = (int) dataSnapshot.getChildrenCount();
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                BehaviouralRecordModel behaviouralRecordModel = postSnapshot.getValue(BehaviouralRecordModel.class);
                                                behaviouralResultRowModelList.add(new BehaviouralResultRowModel(postSnapshot.getKey(), "-1", behaviouralRecordModel.getRewardDescription(), "", behaviouralRecordModel.getClassID(), behaviouralRecordModel.getSortableDate()));
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
                                                mDatabaseReference.keepSynced(true);
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        counter++;
                                                        if (dataSnapshot.exists()) {
                                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                                            behaviouralResultRowModel.setClassName(classInstance.getClassName());
                                                        }

                                                        if (counter == behaviouralResultRowModelList.size()) {
                                                            for (final BehaviouralResultRowModel behaviouralResultRowModel: behaviouralResultRowModelList) {
                                                                String key = behaviouralResultRowModel.getKey();
                                                                mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordParentNotification").child(mFirebaseUser.getUid()).child(activeStudentID).child(key).child("status");
                                                                mDatabaseReference.keepSynced(true);
                                                                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()) {
                                                                            boolean isNew = dataSnapshot.getValue(boolean.class);
                                                                            if (isNew) {
                                                                                behaviouralResultRowModel.setNew(true);
                                                                            } else {
                                                                                behaviouralResultRowModel.setNew(false);
                                                                            }
                                                                        } else {
                                                                            behaviouralResultRowModel.setNew(false);
                                                                        }

                                                                        isNewCounter++;

                                                                        if (isNewCounter == behaviouralResultRowModelList.size()) {
                                                                            Collections.sort(behaviouralResultRowModelList, new Comparator<BehaviouralResultRowModel>() {
                                                                                @Override
                                                                                public int compare(BehaviouralResultRowModel o1, BehaviouralResultRowModel o2) {
                                                                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                                                }
                                                                            });

                                                                            Collections.reverse(behaviouralResultRowModelList);
                                                                            if (!behaviouralResultRowModelList.get(0).getClassID().equals("")) {
                                                                                behaviouralResultRowModelList.add(0, new BehaviouralResultRowModel());
                                                                            }

                                                                            behaviouralResultsHeaderModel.setErrorMessage("");
                                                                            mAdapter.notifyDataSetChanged();
                                                                            recyclerView.setVisibility(View.VISIBLE);
                                                                            progressLayout.setVisibility(View.GONE);
                                                                            errorLayout.setVisibility(View.GONE);
                                                                            internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                        } else {
                                            behaviouralResultRowModelList.add(new BehaviouralResultRowModel());
                                            behaviouralResultsHeaderModel.setErrorMessage("");
                                            mAdapter.notifyDataSetChanged();
                                            recyclerView.setVisibility(View.VISIBLE);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public void updateBadges(){
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
                updateBadgesMap.put("BehaviouralRecord/BehaviouralRecordParentNotification/" + mFirebaseUser.getUid() + "/" + activeStudentID + "/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + activeStudentID + "/More/status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
                updateBadgesMap.put("BehaviouralRecord/BehaviouralRecordParentNotification/" + mFirebaseUser.getUid() + "/" + activeStudentID + "/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
                updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + activeStudentID + "/More/status", false);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateBadgesMap);
            }
        }
    }

    public void updateBadgesForAllCurrent(){
        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
        if (behaviouralResultRowModelList != null) {
            for (int i = 0; i < behaviouralResultRowModelList.size(); i++) {
                BehaviouralResultRowModel behaviouralResultRowModel = behaviouralResultRowModelList.get(i);
                String key = behaviouralResultRowModel.getKey();
                updateBadgesMap.put("BehaviouralRecord/BehaviouralRecordParentNotification/" + mFirebaseUser.getUid() + "/" + activeStudentID + "/" + key + "/status", false);
            }
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
        updateBadgesForAllCurrent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
                    Intent i = new Intent(this, ParentMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "2");
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (parentActivity.equals("Teacher")) {
                    Intent i = new Intent(this, TeacherMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "3");
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                Intent i = new Intent(this, ParentMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "2");
                i.putExtras(bundle);
                startActivity(i);
            } else if (parentActivity.equals("Teacher")) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "3");
                i.putExtras(bundle);
                startActivity(i);
            }
        }
    }
}
