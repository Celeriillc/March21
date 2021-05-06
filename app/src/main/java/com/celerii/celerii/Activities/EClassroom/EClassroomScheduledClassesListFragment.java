package com.celerii.celerii.Activities.EClassroom;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.EClassroomScheduledClassesListAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.EClassroomConcludedClassesListAdapter;
import com.celerii.celerii.models.EClassroomScheduledClassesListModel;
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

public class EClassroomScheduledClassesListFragment extends Fragment {

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
    private ArrayList<EClassroomScheduledClassesListModel> eClassroomScheduledClassesListModelList;
    public EClassroomScheduledClassesListAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String studentID;

    String featureUseKey = "";
    String featureName = "E Classroom Scheduled Classes List";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public EClassroomScheduledClassesListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_e_classroom_scheduled_classes_list, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(context);

        EClassroomHomeActivity activity = (EClassroomHomeActivity) getActivity();
        studentID = activity.getData();

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

        eClassroomScheduledClassesListModelList = new ArrayList<>();
        mAdapter = new EClassroomScheduledClassesListAdapter(eClassroomScheduledClassesListModelList, context);
        recyclerView.setAdapter(mAdapter);
        loadFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );

        return view;
    }

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        if (studentID.trim().equals("")) {
            mDatabaseReference = mFirebaseDatabase.getReference().child("Scheduled Class").child("Teacher").child(mFirebaseUser.getUid());
        } else {
            mDatabaseReference = mFirebaseDatabase.getReference().child("Scheduled Class").child("Student").child(studentID);
        }

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        EClassroomScheduledClassesListModel eClassroomScheduledClassesListModel = postSnapshot.getValue(EClassroomScheduledClassesListModel.class);

                        String todayDate = Date.getDate();
                        if (Date.compareDates(eClassroomScheduledClassesListModel.getDateScheduled(), todayDate)) {
                            eClassroomScheduledClassesListModelList.add(eClassroomScheduledClassesListModel);
                        }
                    }

                    if (eClassroomScheduledClassesListModelList.size() > 0) {
                        if (eClassroomScheduledClassesListModelList.size() > 1) {
                            Collections.sort(eClassroomScheduledClassesListModelList, new Comparator<EClassroomScheduledClassesListModel>() {
                                @Override
                                public int compare(EClassroomScheduledClassesListModel o1, EClassroomScheduledClassesListModel o2) {
                                    return o1.getSortableDateScheduled().compareTo(o2.getSortableDateScheduled());
                                }
                            });
                        }

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
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                            errorLayoutText.setText("You do not have any scheduled classes at the moment");
                        } else {
                            errorLayoutText.setText("You do not have any scheduled classes at the moment. Click the " + "<b>" + "Add Class" + "</b>" + " floating button on the bottom left to schedule a class");
                        }
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                        errorLayoutText.setText("You do not have any scheduled classes at the moment");
                    } else {
                        errorLayoutText.setText("You do not have any scheduled classes at the moment. Click the " + "<b>" + "Add Class" + "</b>" + " floating button on the bottom left to schedule a class");
                    }
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
}