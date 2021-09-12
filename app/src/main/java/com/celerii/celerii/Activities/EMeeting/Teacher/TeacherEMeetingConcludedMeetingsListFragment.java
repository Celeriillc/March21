package com.celerii.celerii.Activities.EMeeting.Teacher;

import android.content.Context;
import android.content.Intent;
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
import com.celerii.celerii.adapters.TeacherEMeetingConcludedMeetingsListAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.EMeetingScheduledMeetingsListModel;
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

public class TeacherEMeetingConcludedMeetingsListFragment extends Fragment {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    public RecyclerView recyclerView;
    private ArrayList<EMeetingScheduledMeetingsListModel> eMeetingScheduledMeetingsListModelList;
    public TeacherEMeetingConcludedMeetingsListAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String featureUseKey = "";
    String featureName = "Teacher E Meeting Concluded Meetings List";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public TeacherEMeetingConcludedMeetingsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_e_meeting_concluded_meetings_list, container, false);

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
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        eMeetingScheduledMeetingsListModelList = new ArrayList<>();
        mAdapter = new TeacherEMeetingConcludedMeetingsListAdapter(eMeetingScheduledMeetingsListModelList, context);
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

        mDatabaseReference = mFirebaseDatabase.getReference().child("E Meeting Scheduled Meeting").child("Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eMeetingScheduledMeetingsListModelList.clear();
                mAdapter.notifyDataSetChanged();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        EMeetingScheduledMeetingsListModel eMeetingScheduledMeetingsListModel = postSnapshot.getValue(EMeetingScheduledMeetingsListModel.class);

                        if (eMeetingScheduledMeetingsListModel.getOpen() == null) {
                            eMeetingScheduledMeetingsListModel.setOpen(true);
                        }

                        if (!eMeetingScheduledMeetingsListModel.getOpen()) {
                            eMeetingScheduledMeetingsListModelList.add(eMeetingScheduledMeetingsListModel);
                        }
                    }

                    if (eMeetingScheduledMeetingsListModelList.size() > 0) {
                        if (eMeetingScheduledMeetingsListModelList.size() > 1) {
                            Collections.sort(eMeetingScheduledMeetingsListModelList, new Comparator<EMeetingScheduledMeetingsListModel>() {
                                @Override
                                public int compare(EMeetingScheduledMeetingsListModel o1, EMeetingScheduledMeetingsListModel o2) {
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
                        errorLayoutText.setText(Html.fromHtml("You do not have any concluded meetings at the moment. Concluded Meetings from your school will appear here."));
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You do not have any concluded meetings at the moment. Concluded Meetings from your school will appear here."));
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
        super.onResume();
    }
}