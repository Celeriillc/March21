package com.celerii.celerii.Activities.StudentBehaviouralPerformance;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.StudentRewardFragmentAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.TeacherRewardModel;
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
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentPunishmentFragment extends Fragment {
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<TeacherRewardModel> teacherRewardModelList;
    public RecyclerView recyclerView;
    public StudentRewardFragmentAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String studentID;
    String studentName;
    String studentPicURL;

    String featureUseKey = "";
    String featureName = "Punishments";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public StudentPunishmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_punishment, container, false);

        sharedPreferencesManager = new SharedPreferencesManager(getContext());

        StudentRewardHome activity = (StudentRewardHome) getActivity();
        studentID = activity.getStudentID();
        studentName = activity.getStudentName();
        studentPicURL = activity.getStudentPicURL();

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        teacherRewardModelList = new ArrayList<>();
        mAdapter = new StudentRewardFragmentAdapter(teacherRewardModelList, "Punishment", studentID, studentName, studentPicURL, getContext());
        loadRewardsFromFirebase();
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadRewardsFromFirebase();
                    }
                }
        );

        return view;
    }

    void loadRewardsFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("TeacherBehaviouralRewardsCustom").child(sharedPreferencesManager.getMyUserID()).child("Punishments");
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherRewardModelList.clear();
                mAdapter.notifyDataSetChanged();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String reward = postSnapshot.getValue(String.class);
                        teacherRewardModelList.add(new TeacherRewardModel(reward, "-", false));
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference().child("TeacherBehaviouralRewardsDefault").child("Punishments");
                mDatabaseReference.keepSynced(true);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String reward = postSnapshot.getValue(String.class);
                                teacherRewardModelList.add(new TeacherRewardModel(reward, "-", false));
                            }
                        }

                        if (teacherRewardModelList.size() > 1) {
                            Collections.sort(teacherRewardModelList, new Comparator<TeacherRewardModel>() {
                                @Override
                                public int compare(TeacherRewardModel o1, TeacherRewardModel o2) {
                                    return o1.getReward().compareTo(o2.getReward());
                                }
                            });
                        }

                        if (teacherRewardModelList.size() > 0) {
                            teacherRewardModelList.remove(new TeacherRewardModel("Footer", " ", false));
                            teacherRewardModelList.add(new TeacherRewardModel("Footer", " ", false));
                            mySwipeRefreshLayout.setRefreshing(false);
                            progressLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
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
