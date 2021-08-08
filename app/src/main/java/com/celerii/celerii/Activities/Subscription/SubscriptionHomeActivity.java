package com.celerii.celerii.Activities.Subscription;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SubscriptionAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.SubscriptionModel;
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

public class SubscriptionHomeActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    private ArrayList<SubscriptionModel> subscriptionModelList;
    private SubscriptionModel subscriptionModel;
    public RecyclerView recyclerView;
    public SubscriptionAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String childID;
    String childsFirstName;

    String featureUseKey = "";
    String featureName = "Subscription Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_home);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

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
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ParentSearchActivity.class));
            }
        });

        if (activeKid == null) {
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
                        activeKid = gson.toJson(myChildren.get(0));
                        sharedPreferencesManager.setActiveKid(activeKid);
                    } else {
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setTitle("Subscription Status");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setHomeButtonEnabled(true);
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
                    getSupportActionBar().setTitle("Subscription Status");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
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
                getSupportActionBar().setTitle("Subscription Status");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(Html.fromHtml("We couldn't find this student's account"));
                return;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {
                }.getType();
                Student activeKidModel = gson.fromJson(activeKid, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student : myChildren) {
                    if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                        activeKidExist = true;
                        activeKidModel = student;
                        activeKid = gson.toJson(activeKidModel);
                        sharedPreferencesManager.setActiveKid(activeKid);
                        break;
                    }
                }

                if (!activeKidExist) {
                    if (myChildren.size() > 0) {
                        if (myChildren.size() > 1) {
                            gson = new Gson();
                            activeKid = gson.toJson(myChildren.get(0));
                            sharedPreferencesManager.setActiveKid(activeKid);
                        }
                    } else {
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setTitle("Subscription Status");
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setHomeButtonEnabled(true);
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
        Student activeKidModel = gson.fromJson(activeKid, type);
        childID = activeKidModel.getStudentID();
        childsFirstName = activeKidModel.getFirstName();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(childsFirstName + "'s Subscription");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

//        recyclerView.setVisibility(View.GONE);
//        progressLayout.setVisibility(View.VISIBLE);

        subscriptionModel = new SubscriptionModel();
        subscriptionModelList = new ArrayList<>();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            loadSubscriptionInformationForParents();
        } else {
            loadSubscriptionInformationForTeachers();
        }

//        mAdapter = new SubscriptionAdapter(subscriptionModelList, subscriptionModel, this);
//        recyclerView.setAdapter(mAdapter);
        loadStudentSubscriptionInformationFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadStudentSubscriptionInformationFromFirebase();
                    }
                }
        );
    }

    static HashMap<String, ArrayList<SubscriptionModel>> subscriptionMap = new HashMap<>();
    private void loadSubscriptionInformationForParents() {
        Gson gson = new Gson();
        subscriptionMap = new HashMap<>();
        String subscriptionJSON = sharedPreferencesManager.getSubscriptionInformationParents();
        Type type = new TypeToken<HashMap<String, ArrayList<SubscriptionModel>>>() {}.getType();
        subscriptionMap = gson.fromJson(subscriptionJSON, type);

        ArrayList<SubscriptionModel> subscriptionModelList = new ArrayList<>();

        if (subscriptionMap == null) {
            subscriptionModelList = new ArrayList<>();
        } else {
            subscriptionModelList = subscriptionMap.get(childID);
        }

        if (subscriptionModelList == null || subscriptionModelList.size() == 0) {
            subscriptionModelList = new ArrayList<>();
            subscriptionModelList.add(new SubscriptionModel());
            mAdapter = new SubscriptionAdapter(subscriptionModelList, new SubscriptionModel(), childID, this);
            recyclerView.setAdapter(mAdapter);
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
        } else {
            if (subscriptionModelList.size() > 1) {
                Collections.sort(subscriptionModelList, new Comparator<SubscriptionModel>() {
                    @Override
                    public int compare(SubscriptionModel o1, SubscriptionModel o2) {
                        return o1.getSubscriptionDate().compareTo(o2.getSubscriptionDate());
                    }
                });
            }

            Collections.reverse(subscriptionModelList);
            subscriptionModelList.add(0, new SubscriptionModel());
            mAdapter = new SubscriptionAdapter(subscriptionModelList, subscriptionModelList.get(1), childID, this);
            recyclerView.setAdapter(mAdapter);
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadSubscriptionInformationForTeachers() {
        Gson gson = new Gson();
        String subscriptionJSON = sharedPreferencesManager.getSubscriptionInformationTeachers();
        Type type = new TypeToken<HashMap<String, SubscriptionModel>>() {}.getType();
        HashMap<String, SubscriptionModel> subscriptionModelMap = gson.fromJson(subscriptionJSON, type);

        ArrayList<SubscriptionModel> subscriptionModelList = new ArrayList<>();

        if (subscriptionModelMap == null) {
            subscriptionModelList = new ArrayList<>();
        } else {
            subscriptionModelList.add(subscriptionModelMap.get(childID));
        }

        if (subscriptionModelList == null || subscriptionModelList.size() == 0) {
            subscriptionModelList = new ArrayList<>();
            subscriptionModelList.add(new SubscriptionModel());
            mAdapter = new SubscriptionAdapter(subscriptionModelList, new SubscriptionModel(), childID, this);
            recyclerView.setAdapter(mAdapter);
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
        } else {
            if (subscriptionModelList.size() > 1) {
                Collections.sort(subscriptionModelList, new Comparator<SubscriptionModel>() {
                    @Override
                    public int compare(SubscriptionModel o1, SubscriptionModel o2) {
                        return o1.getSubscriptionDate().compareTo(o2.getSubscriptionDate());
                    }
                });
            }

            Collections.reverse(subscriptionModelList);
            subscriptionModelList.add(0, new SubscriptionModel());
            mAdapter = new SubscriptionAdapter(subscriptionModelList, subscriptionModelList.get(1), childID, this);
            recyclerView.setAdapter(mAdapter);
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadStudentSubscriptionInformationFromFirebase() {
        subscriptionModelList.clear();
        mAdapter.notifyDataSetChanged();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Subscription").child(childID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        SubscriptionModel subscriptionModel = postSnapshot.getValue(SubscriptionModel.class);
                        subscriptionModelList.add(subscriptionModel);
                    }

                    if (subscriptionModelList.size() > 1) {
                        Collections.sort(subscriptionModelList, new Comparator<SubscriptionModel>() {
                            @Override
                            public int compare(SubscriptionModel o1, SubscriptionModel o2) {
                                return o1.getSubscriptionDate().compareTo(o2.getSubscriptionDate());
                            }
                        });
                    }

                    Collections.reverse(subscriptionModelList);
                }

                subscriptionModelList.add(0, new SubscriptionModel());
                mySwipeRefreshLayout.setRefreshing(false);
//                mAdapter.notifyDataSetChanged();
                mAdapter = new SubscriptionAdapter(subscriptionModelList, subscriptionModelList.get(1), childID, context);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        UpdateDataFromFirebase.populateEssentials(this);
        super.onResume();
    }
}
