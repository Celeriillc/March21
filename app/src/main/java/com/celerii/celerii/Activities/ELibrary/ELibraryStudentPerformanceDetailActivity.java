package com.celerii.celerii.Activities.ELibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ELibraryAssignmentDetailAdapter;
import com.celerii.celerii.adapters.ELibraryStudentPerformanceDetailAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ELibraryAssignmentDetailHeaderModel;
import com.celerii.celerii.models.ELibraryMyAssignmentModel;
import com.celerii.celerii.models.ELibraryStudentPerformanceDetailHeaderModel;
import com.celerii.celerii.models.QuestionModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ELibraryStudentPerformanceDetailActivity extends AppCompatActivity {

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
    private ArrayList<QuestionModel> questionModelList;
    ELibraryStudentPerformanceDetailHeaderModel eLibraryStudentPerformanceDetailHeaderModel;
    public RecyclerView recyclerView;
    public ELibraryStudentPerformanceDetailAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    Bundle bundle;
    String assignmentID = "";
    String studentID, studentName, title, classID, className, date, sortableDate, score;

    String featureUseKey = "";
    String featureName = "E Library Student Performance Detail";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_library_student_performance_detail);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        bundle = getIntent().getExtras();
        assignmentID = bundle.getString("assignmentID");
        studentID = bundle.getString("studentID");
        studentName = bundle.getString("studentName");
        score = bundle.getString("score");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(studentName + "'s Score");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);

        questionModelList = new ArrayList<>();
        eLibraryStudentPerformanceDetailHeaderModel = new ELibraryStudentPerformanceDetailHeaderModel(studentID, studentName, score);
        mAdapter = new ELibraryStudentPerformanceDetailAdapter(questionModelList, eLibraryStudentPerformanceDetailHeaderModel, this,this);
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

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        questionModelList.clear();
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Assignment").child("Student").child(studentID).child(assignmentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ELibraryMyAssignmentModel eLibraryMyAssignmentModel = dataSnapshot.getValue(ELibraryMyAssignmentModel.class);
                    eLibraryStudentPerformanceDetailHeaderModel.setTitle(eLibraryMyAssignmentModel.getMaterialTitle());
                    eLibraryStudentPerformanceDetailHeaderModel.setClassID(eLibraryMyAssignmentModel.getClassID());
                    eLibraryStudentPerformanceDetailHeaderModel.setClassName(eLibraryMyAssignmentModel.getClassName());
                    eLibraryStudentPerformanceDetailHeaderModel.setDate(eLibraryMyAssignmentModel.getDateGiven());
                    eLibraryStudentPerformanceDetailHeaderModel.setSortableDate(eLibraryMyAssignmentModel.getSortableDateGiven());
                    mAdapter.notifyDataSetChanged();

                    mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Assignment Questions").child("Student").child(studentID).child(assignmentID);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    QuestionModel questionModel = postSnapshot.getValue(QuestionModel.class);
                                    questionModelList.add(questionModel);
                                }

                                questionModelList.add(0, new QuestionModel());
                                mAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                                recyclerView.setVisibility(View.VISIBLE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.GONE);
                            } else {
                                mySwipeRefreshLayout.setRefreshing(false);
                                recyclerView.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                                errorLayoutText.setText("This assignment doesn't have any questions or has been deleted.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("This assignment doesn't have any questions or has been deleted.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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