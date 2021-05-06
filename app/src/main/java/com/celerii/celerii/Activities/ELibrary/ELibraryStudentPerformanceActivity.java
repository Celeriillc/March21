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
import com.celerii.celerii.adapters.ELibraryStudentPerformanceHomeAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ELibraryAssignmentDetailHeaderModel;
import com.celerii.celerii.models.ELibraryAssignmentStudentPerformanceModel;
import com.celerii.celerii.models.ELibraryStudentPerformanceHomeModel;
import com.celerii.celerii.models.QuestionModel;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ELibraryStudentPerformanceActivity extends AppCompatActivity {

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
    private ArrayList<ELibraryStudentPerformanceHomeModel> eLibraryStudentPerformanceHomeModelList;
    public RecyclerView recyclerView;
    public ELibraryStudentPerformanceHomeAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    Bundle bundle;
    String assignmentID = "";

    String featureUseKey = "";
    String featureName = "E Library Student Performance Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_library_student_performance);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        bundle = getIntent().getExtras();
        assignmentID = bundle.getString("assignmentID");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Performance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);

        eLibraryStudentPerformanceHomeModelList = new ArrayList<>();
        mAdapter = new ELibraryStudentPerformanceHomeAdapter(eLibraryStudentPerformanceHomeModelList, assignmentID, this);
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
    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        eLibraryStudentPerformanceHomeModelList.clear();
        mAdapter.notifyDataSetChanged();
        count = 0;

        mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Assignment Student Performance").child(assignmentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        ELibraryAssignmentStudentPerformanceModel eLibraryAssignmentStudentPerformanceModel = postSnapshot.getValue(ELibraryAssignmentStudentPerformanceModel.class);
                        eLibraryAssignmentStudentPerformanceModel.setStudentID(postSnapshot.getKey());

                        double score = (Double.parseDouble(eLibraryAssignmentStudentPerformanceModel.getCorrectAnswers()) / Double.parseDouble(eLibraryAssignmentStudentPerformanceModel.getTotalQuestions())) * 100;
                        final ELibraryStudentPerformanceHomeModel eLibraryStudentPerformanceHomeModel = new ELibraryStudentPerformanceHomeModel(postSnapshot.getKey(), String.valueOf(score));
                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(eLibraryStudentPerformanceHomeModel.getStudentID());
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                count++;
                                if (dataSnapshot.exists()) {
                                    Student student = dataSnapshot.getValue(Student.class);
                                    String studentName = student.getFirstName() + " " +  student.getLastName();
                                    eLibraryStudentPerformanceHomeModel.setStudentName(studentName);
                                    eLibraryStudentPerformanceHomeModel.setStudentProfilePictureURL(student.getImageURL());
                                } else {
                                    eLibraryStudentPerformanceHomeModel.setStudentName("Deleted Student");
                                    eLibraryStudentPerformanceHomeModel.setStudentProfilePictureURL("");
                                }
                                eLibraryStudentPerformanceHomeModelList.add(eLibraryStudentPerformanceHomeModel);

                                if (count == childrenCount) {
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
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("No student has attempted this assignment yet.");
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