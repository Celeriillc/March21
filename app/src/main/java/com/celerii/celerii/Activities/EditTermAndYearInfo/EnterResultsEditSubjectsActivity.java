package com.celerii.celerii.Activities.EditTermAndYearInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SelectSubjectAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.ParentCheckAttendanceSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TeacherEnterResultsSharedPreferences;
import com.celerii.celerii.helperClasses.TeacherTakeAttendanceSharedPreferences;
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

public class EnterResultsEditSubjectsActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    TeacherTakeAttendanceSharedPreferences teacherTakeAttendanceSharedPreferences;
    TeacherEnterResultsSharedPreferences teacherEnterResultsSharedPreferences;
    ParentCheckAttendanceSharedPreferences parentCheckAttendanceSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<SelectSubjectModel> selectSubjectModelList;
    ArrayList<String> subjectList = new ArrayList<>();
    public RecyclerView recyclerView;
    public SelectSubjectAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    Toolbar toolbar;
    String activeClass;
    String selectedSubject, activity;

    String featureUseKey = "";
    String featureName = "Edit Subject";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results_edit_subjects);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        teacherTakeAttendanceSharedPreferences = new TeacherTakeAttendanceSharedPreferences(this);
        teacherEnterResultsSharedPreferences = new TeacherEnterResultsSharedPreferences(this);
        parentCheckAttendanceSharedPreferences = new ParentCheckAttendanceSharedPreferences(this);
        parentCheckAttendanceSharedPreferences.deleteSubject();

        Bundle bundle = getIntent().getExtras();
//        activeClass = bundle.getString("Active Class");
        activity = bundle.getString("Activity");
        selectedSubject = bundle.getString("Subject");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Subject");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        selectSubjectModelList = new ArrayList<>();
        mAdapter = new SelectSubjectAdapter(selectSubjectModelList, selectedSubject, this);
        loadFromSharedPreferences();
        recyclerView.setAdapter(mAdapter);

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
        subjectList = new ArrayList<>();
        String subjectJSON = sharedPreferencesManager.getSubjects();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        subjectList = gson.fromJson(subjectJSON, type);

        if (subjectList == null) {
            subjectList = new ArrayList<>();
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("There are no subjects for you to access. If you're not connected to a school, use the search feature to search for a school and send a request.");
        } else {
            for (int i = 0; i < subjectList.size(); i++) {
                SelectSubjectModel selectSubjectModel = new SelectSubjectModel(subjectList.get(i));
                selectSubjectModelList.add(selectSubjectModel);
//                if (!selectSubjectModelList.contains(new SelectSubjectModel("General"))) selectSubjectModelList.add(0, new SelectSubjectModel("General"));
            }

            selectSubjectModelList.add(0, new SelectSubjectModel(""));
            mAdapter.notifyDataSetChanged();
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
    }

    private void loadFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Class School").child(activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String schoolID = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("School Subjects").child(schoolID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final int childrenCount = (int) dataSnapshot.getChildrenCount();
                                if (dataSnapshot.exists()) {
                                    selectSubjectModelList.clear();
                                    mAdapter.notifyDataSetChanged();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        String subject = postSnapshot.getKey();

                                        SelectSubjectModel selectSubjectModel = new SelectSubjectModel(subject);
                                        selectSubjectModelList.add(selectSubjectModel);

                                        if (childrenCount == selectSubjectModelList.size()) {
                                            if (!selectSubjectModelList.contains(new SelectSubjectModel("General"))) selectSubjectModelList.add(0, new SelectSubjectModel("General"));
                                            subjectList.clear();
                                            mAdapter.notifyDataSetChanged();
                                            for (int i = 0; i < selectSubjectModelList.size(); i++) {
                                                subjectList.add(selectSubjectModelList.get(i).getSubject());
                                            }

                                            mAdapter.notifyDataSetChanged();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(subjectList);
                                            sharedPreferencesManager.setSubjects(json);
                                            mySwipeRefreshLayout.setRefreshing(false);
                                        }
                                    }
                                } else {
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    recyclerView.setVisibility(View.GONE);
                                    progressLayout.setVisibility(View.GONE);
                                    errorLayout.setVisibility(View.VISIBLE);
                                    errorLayoutText.setText("Your school hasn't registered any subjects yet use the search feature to search for a school and send a request.");
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
                    errorLayoutText.setText("There are no subjects for you to access. If you're not connected to a school, use the search feature to search for a school and send a request.");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            selectedSubject = intent.getStringExtra("SelectedSubject");
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
//            Intent intent = new Intent();
//
//            if (selectedSubject == null) {return false;}
//
//            if (activity.equals("WriteAttendance")) {
//                teacherTakeAttendanceSharedPreferences.setSubject(selectedSubject);
//            } else if (activity.equals("WriteResult")) {
//                teacherEnterResultsSharedPreferences.setSubject(selectedSubject);
//            } else if (activity.equals("ParentReadAttendance")) {
//                parentCheckAttendanceSharedPreferences.setSubject(selectedSubject);
//            }
//
//            intent.putExtra("Selected Subject", selectedSubject);
//            setResult(RESULT_OK, intent);
            finish();
        }
        else if (id == R.id.action_send){
            Intent intent = new Intent();

            if (selectedSubject == null) {return false;}

            if (activity.equals("WriteAttendance")) {
                teacherTakeAttendanceSharedPreferences.setSubject(selectedSubject);
            } else if (activity.equals("WriteResult")) {
                teacherEnterResultsSharedPreferences.setSubject(selectedSubject);
            } else if (activity.equals("ParentReadAttendance")) {
                parentCheckAttendanceSharedPreferences.setSubject(selectedSubject);
            }

            intent.putExtra("Selected Subject", selectedSubject);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
