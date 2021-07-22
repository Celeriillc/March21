package com.celerii.celerii.Activities.EClassroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.EClassroomParticipantsAdapter;
import com.celerii.celerii.adapters.ELibraryStudentPerformanceHomeAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Month;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.EClassroomParticipantsModel;
import com.celerii.celerii.models.ELibraryAssignmentStudentPerformanceModel;
import com.celerii.celerii.models.ELibraryStudentPerformanceHomeModel;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class EClassroomScheduledClassParticipantsActivity extends AppCompatActivity {

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
    private ArrayList<EClassroomParticipantsModel> eClassroomParticipantsModelList;
    public RecyclerView recyclerView;
    public EClassroomParticipantsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    Bundle bundle;
    String scheduledClassID = "";
    String scheduledClassState = "";
    String scheduledClassScheduledDate = "";

    String featureUseKey = "";
    String featureName = "E Classroom Participants";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eclassroom_scheduled_class_participants);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        bundle = getIntent().getExtras();
        scheduledClassID = bundle.getString("Scheduled Class ID");
        scheduledClassState = bundle.getString("Scheduled Class State");
        scheduledClassScheduledDate = bundle.getString("Scheduled Class Scheduled Date");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Class Participants");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);

        eClassroomParticipantsModelList = new ArrayList<>();
        mAdapter = new EClassroomParticipantsAdapter(eClassroomParticipantsModelList, this);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        loadFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
              @Override
              public void onRefresh() {
                  loadFromFirebase();
              }
          }
        );
    }

    int count;
    EClassroomParticipantsModel eClassroomParticipantsModelTeacher;

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        eClassroomParticipantsModelList.clear();
        mAdapter.notifyDataSetChanged();
        count = 0;

        if (scheduledClassState.equals("Scheduled")) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            String messageString = "Participants for this class will be available after the class is closed by its creator.";
            errorLayoutText.setText(messageString);
        } else {
            mDatabaseReference = mFirebaseDatabase.getReference().child("E Classroom Scheduled Class Participants").child(scheduledClassID);
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Boolean wasPresent = postSnapshot.getValue(Boolean.class);
                            EClassroomParticipantsModel eClassroomParticipantsModel = new EClassroomParticipantsModel(postSnapshot.getKey(), wasPresent);

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(postSnapshot.getKey());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        count++;
                                        Student student = dataSnapshot.getValue(Student.class);
                                        String studentName = student.getFirstName() + " " +  student.getLastName();
                                        eClassroomParticipantsModel.setName(studentName);
                                        eClassroomParticipantsModel.setProfilePictureURL(student.getImageURL());

                                        eClassroomParticipantsModelList.add(eClassroomParticipantsModel);

                                        if (count == childrenCount) {
                                            Collections.sort(eClassroomParticipantsModelList, new Comparator<EClassroomParticipantsModel>() {
                                                @Override
                                                public int compare(EClassroomParticipantsModel o1, EClassroomParticipantsModel o2) {
                                                    return o1.getName().compareTo(o2.getName());
                                                }
                                            });

                                            if (!eClassroomParticipantsModelTeacher.getId().equals("")) {
                                                eClassroomParticipantsModelList.add(0, eClassroomParticipantsModelTeacher);
                                            }

                                            mAdapter.notifyDataSetChanged();
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            recyclerView.setVisibility(View.VISIBLE);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                        }
                                    } else {
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(postSnapshot.getKey());
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                count++;
                                                if (dataSnapshot.exists()) {
                                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                    String teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                                                    eClassroomParticipantsModel.setName(teacherName);
                                                    eClassroomParticipantsModel.setProfilePictureURL(teacher.getProfilePicURL());
                                                } else {
                                                    eClassroomParticipantsModel.setName("Deleted User");
                                                    eClassroomParticipantsModel.setProfilePictureURL("");
                                                }

                                                eClassroomParticipantsModelTeacher = eClassroomParticipantsModel;

                                                if (count == childrenCount) {
                                                    Collections.sort(eClassroomParticipantsModelList, new Comparator<EClassroomParticipantsModel>() {
                                                        @Override
                                                        public int compare(EClassroomParticipantsModel o1, EClassroomParticipantsModel o2) {
                                                            return o1.getName().compareTo(o2.getName());
                                                        }
                                                    });

                                                    if (!eClassroomParticipantsModelTeacher.getId().equals("")) {
                                                        eClassroomParticipantsModelList.add(0, eClassroomParticipantsModelTeacher);
                                                    }

                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    } else {
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText("This e-classroom has no participants.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
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

    @Override
    protected void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }
}