package com.celerii.celerii.Activities.EditTermAndYearInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SelectSubjectAdapter;
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

import java.util.ArrayList;

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
    LinearLayout errorLayout, progressLayout;

    private ArrayList<SelectSubjectModel> selectSubjectModelList;
    public RecyclerView recyclerView;
    public SelectSubjectAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    Toolbar toolbar;
    String activeClass;
    String selectedSubject, activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results_edit_subjects);

        teacherTakeAttendanceSharedPreferences = new TeacherTakeAttendanceSharedPreferences(this);
        teacherEnterResultsSharedPreferences = new TeacherEnterResultsSharedPreferences(this);
        parentCheckAttendanceSharedPreferences = new ParentCheckAttendanceSharedPreferences(this);
        parentCheckAttendanceSharedPreferences.deleteSubject();

        Bundle bundle = getIntent().getExtras();
        activeClass = bundle.getString("Active Class");
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

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        selectSubjectModelList = new ArrayList<>();
        mAdapter = new SelectSubjectAdapter(selectSubjectModelList, selectedSubject, this);
        loadFromFirebase();
        recyclerView.setAdapter(mAdapter);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

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
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        String subject = postSnapshot.getKey();

                                        SelectSubjectModel selectSubjectModel = new SelectSubjectModel(subject);
                                        selectSubjectModelList.add(selectSubjectModel);
                                        mAdapter.notifyDataSetChanged();

                                        if (childrenCount == selectSubjectModelList.size()) {
                                            if (!selectSubjectModelList.contains(new SelectSubjectModel("General"))) selectSubjectModelList.add(0, new SelectSubjectModel("General"));
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                            errorLayout.setVisibility(View.GONE);
                                        }
                                    }
                                } else {
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    recyclerView.setVisibility(View.GONE);
                                    progressLayout.setVisibility(View.GONE);
                                    errorLayout.setVisibility(View.VISIBLE);
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
