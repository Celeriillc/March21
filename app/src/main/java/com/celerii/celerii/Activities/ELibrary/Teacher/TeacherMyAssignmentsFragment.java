package com.celerii.celerii.Activities.ELibrary.Teacher;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ELibraryMyAssignmentAdapter;
import com.celerii.celerii.adapters.PerformanceHistoryAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.ELibraryAssignmentStudentPerformanceModel;
import com.celerii.celerii.models.ELibraryMaterialsModel;
import com.celerii.celerii.models.ELibraryMyAssignmentModel;
import com.celerii.celerii.models.PerformanceHistoryModel;
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

public class TeacherMyAssignmentsFragment extends Fragment {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    public RecyclerView recyclerView;
    private ArrayList<ELibraryMyAssignmentModel> eLibraryMyAssignmentModelList;
    public ELibraryMyAssignmentAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String featureUseKey = "";
    String featureName = "Teacher My Assignment";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public TeacherMyAssignmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_my_assignments, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        eLibraryMyAssignmentModelList = new ArrayList<>();
        mAdapter = new ELibraryMyAssignmentAdapter(eLibraryMyAssignmentModelList, "TeacherMyAssignment", context);
        recyclerView.setAdapter(mAdapter);
//        loadAssignmentsFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadAssignmentsFromFirebase();
                    }
                }
        );

        return view;
    }

    double totalPerformance;
    int count = 0;
    private void loadAssignmentsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        count = 0;

        mDatabaseReference = mFirebaseDatabase.getReference("E Library Assignment").child("Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eLibraryMyAssignmentModelList.clear();
                mAdapter.notifyDataSetChanged();
                if (dataSnapshot.exists()) {
                    final int numberOfAssignments = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        final ELibraryMyAssignmentModel eLibraryMyAssignmentModel = postSnapshot.getValue(ELibraryMyAssignmentModel.class);

                        totalPerformance = 0;

                        mDatabaseReference = mFirebaseDatabase.getReference("E Library Assignment Student Performance").child(eLibraryMyAssignmentModel.getAssignmentID());
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                count++;
                                if (dataSnapshot.exists()) {
                                    float childrenCount = dataSnapshot.getChildrenCount();
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        ELibraryAssignmentStudentPerformanceModel eLibraryAssignmentStudentPerformanceModel = postSnapshot.getValue(ELibraryAssignmentStudentPerformanceModel.class);
                                        eLibraryAssignmentStudentPerformanceModel.setStudentID(postSnapshot.getKey());

                                        double score = (Double.parseDouble(eLibraryAssignmentStudentPerformanceModel.getCorrectAnswers()) / Double.parseDouble(eLibraryAssignmentStudentPerformanceModel.getTotalQuestions())) * 100;
                                        totalPerformance += score;
                                    }

                                    eLibraryMyAssignmentModel.setPerformance(String.valueOf(totalPerformance / childrenCount));
                                } else {
                                    eLibraryMyAssignmentModel.setPerformance("0");
                                }
                                eLibraryMyAssignmentModelList.add(eLibraryMyAssignmentModel);

                                if (count == numberOfAssignments){
                                    if (eLibraryMyAssignmentModelList.size() > 1) {
                                        Collections.sort(eLibraryMyAssignmentModelList, new Comparator<ELibraryMyAssignmentModel>() {
                                            @Override
                                            public int compare(ELibraryMyAssignmentModel o1, ELibraryMyAssignmentModel o2) {
                                                return o1.getSortableDateGiven().compareTo(o2.getSortableDateGiven());
                                            }
                                        });
                                    }

                                    Collections.reverse(eLibraryMyAssignmentModelList);
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
                    errorLayoutText.setText(Html.fromHtml("You haven't created any assignments for your students yet. Open a book, video or audio book and click on the " +  "<b>" + "Create Assignment" + "</b>" + " button to create an assignment"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    public void onResume() {
        UpdateDataFromFirebase.populateEssentials(context);
        loadAssignmentsFromFirebase();
        super.onResume();
    }
}