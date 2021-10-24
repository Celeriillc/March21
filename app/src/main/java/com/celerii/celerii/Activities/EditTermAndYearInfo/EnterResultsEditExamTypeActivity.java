package com.celerii.celerii.Activities.EditTermAndYearInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SelectExamTypeAdapter;
import com.celerii.celerii.adapters.SelectSubjectAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.SelectExamTypeModel;
import com.celerii.celerii.models.SelectSubjectModel;
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
import java.util.HashMap;

public class EnterResultsEditExamTypeActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar toolbar;
//    RadioButton ca, exam, other;
//    RadioGroup testtypeGroup;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<SelectExamTypeModel> selectExamTypeModelList;
    ArrayList<String> examTypeList = new ArrayList<>();
    public RecyclerView recyclerView;
    public SelectExamTypeAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String selectedTestType;
    String testType;
    String myUserID;

    String featureUseKey = "";
    String featureName = "Edit Exam Type";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results_edit_exam_type);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        myUserID = mFirebaseUser.getUid();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        Bundle b = getIntent().getExtras();
        testType = b.getString("Test Type");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Test Type");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        ca = (RadioButton) findViewById(R.id.ca);
//        exam = (RadioButton) findViewById(R.id.exam);
//        other = (RadioButton) findViewById(R.id.other);
//        testtypeGroup = (RadioGroup) findViewById(R.id.testtypegroup);
//
//        if (testType.equals("Continuous Assessment")){
//            ca.setChecked(true);
//            selectedTestType = "Continuous Assessment";
//        } else if (testType.equals("Examination")){
//            exam.setChecked(true);
//            selectedTestType = "Examination";
//        } else {
//            other.setChecked(true);
//            selectedTestType = "Other";
//        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        selectExamTypeModelList = new ArrayList<>();
        mAdapter = new SelectExamTypeAdapter(selectExamTypeModelList, selectedTestType, this);
        recyclerView.setAdapter(mAdapter);

        loadFromSharedPreferences();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Selected Subject"));
    }

    private void loadFromSharedPreferences() {
        Gson gson = new Gson();
        String subjectJSON = sharedPreferencesManager.getExamType();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        examTypeList = gson.fromJson(subjectJSON, type);

        if (examTypeList == null) {
            examTypeList = new ArrayList<>();
            selectExamTypeModelList.add(new SelectExamTypeModel(""));
            selectExamTypeModelList.add(new SelectExamTypeModel("Continuous Assessment"));
            selectExamTypeModelList.add(new SelectExamTypeModel("Examination"));
            selectExamTypeModelList.add(new SelectExamTypeModel("Other"));
        } else {
            selectExamTypeModelList.clear();

            if (!examTypeList.contains("Other")) {
                examTypeList.add(0, "Other");
            }

            if (!examTypeList.contains("Examination")) {
                examTypeList.add(0, "Examination");
            }

            if (!examTypeList.contains("Continuous Assessment")) {
                examTypeList.add(0, "Continuous Assessment");
            }

            selectExamTypeModelList.add(0, new SelectExamTypeModel(""));
        }

        for (int i = 0; i < examTypeList.size(); i++) {
            SelectExamTypeModel selectExamTypeModel = new SelectExamTypeModel(examTypeList.get(i));
            selectExamTypeModelList.add(selectExamTypeModel);
        }

        mAdapter.notifyDataSetChanged();
        mySwipeRefreshLayout.setRefreshing(false);
        progressLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }

    int counter = 0;
    private void loadFromFirebase() {
        counter = 0;
        examTypeList.clear();
        selectExamTypeModelList.clear();
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference("Teacher School").child(myUserID);
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String schoolKey = postSnapshot.getKey();

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("School Exam Type").child(schoolKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final String examTypeKey = postSnapshot.getKey();

                                        if (!examTypeList.contains(examTypeKey)) {
                                            examTypeList.add(examTypeKey);
                                        }
                                    }
                                }
                                counter++;

                                if (childrenCount == counter) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(examTypeList);
                                    sharedPreferencesManager.setExamType(json);

                                    if (!examTypeList.contains("Other")) {
                                        examTypeList.add(0, "Other");
                                    }

                                    if (!examTypeList.contains("Examination")) {
                                        examTypeList.add(0, "Examination");
                                    }

                                    if (!examTypeList.contains("Continuous Assessment")) {
                                        examTypeList.add(0, "Continuous Assessment");
                                    }

                                    selectExamTypeModelList.add(0, new SelectExamTypeModel(""));

                                    for (int i = 0; i < examTypeList.size(); i++) {
                                        SelectExamTypeModel selectExamTypeModel = new SelectExamTypeModel(examTypeList.get(i));
                                        selectExamTypeModelList.add(selectExamTypeModel);
                                    }

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
                    errorLayoutText.setText("You're not connected to a school, use the search feature to search for a school and send a request.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            selectedTestType = intent.getStringExtra("SelectedExamType");
        }
    };

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_send){
            //TODO: Validate against null values
            Intent intent = new Intent();

            if (selectedTestType == null) {return false;}

            intent.putExtra("Selected Test Type", selectedTestType);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
