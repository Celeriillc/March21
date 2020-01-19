package com.celerii.celerii.Activities.StudentPerformance.History;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.PerformanceHistoryAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.PerformanceHistoryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class HistoryFragment extends Fragment {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<PerformanceHistoryModel> performanceHistoryModelList;
    private ArrayList<String> subjectList, subjectKey;
    public RecyclerView recyclerView;
    public PerformanceHistoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeStudentID = "";
    String activeStudent = "";
    String activeStudentName = "";

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        sharedPreferencesManager = new SharedPreferencesManager(getContext());

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        StudentPerformanceForParentsActivity activity = (StudentPerformanceForParentsActivity) getActivity();
        activeStudent = activity.getData();
        if (activeStudent == null){
            Set<String> childrenSet = sharedPreferencesManager.getMyChildren();
            ArrayList<String> children = new ArrayList<>();
            if (sharedPreferencesManager.getActiveAccount().equals("Parent") && childrenSet != null) {
                children = new ArrayList<>(childrenSet);
                activeStudent = children.get(0);
                sharedPreferencesManager.setActiveKid(activeStudent);
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    errorLayoutText.setText("You're not connected to any child account yet. Use the search button to search for your child and request connection from their school.");
                } else {
                    errorLayoutText.setText("It seems like you do not have the permission to view this child's academic record");
                }
                return view;
            }
        }

        activeStudentID = activeStudent.split(" ")[0];
        activeStudentName = activeStudent.split(" ")[1];
        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        performanceHistoryModelList = new ArrayList<>();
        subjectList = new ArrayList<>();
        loadDetailsFromFirebase();
        mAdapter = new PerformanceHistoryAdapter(performanceHistoryModelList, getContext(), activeStudent);
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadDetailsFromFirebase();
                    }
                }
        );

        return view;
    }

    int iterator = 0; int j; int subIterator = 0;
    private void loadDetailsFromFirebase() {

        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordStudent-Subject").child(activeStudentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    performanceHistoryModelList.clear();
                    subjectList.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        subjectList.add(postSnapshot.getKey());
                    }

                    iterator = 0;
                    for (int i = 0; i < subjectList.size(); i++) {
                        final String subject = subjectList.get(i);

                        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(activeStudentID);
                        mDatabaseReference.orderByChild("subject").equalTo(subject).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                iterator++;
                                if (dataSnapshot.exists()){
                                    double summer = 0;
                                    double counter = 0;
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                        AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                        summer = summer + (TypeConverterClass.convStringToDouble(academicRecordStudent.getScore()) / TypeConverterClass.convStringToDouble(academicRecordStudent.getMaxObtainable())) * 100;
                                        counter++;
                                    }
                                    double score = (summer / counter);
                                    PerformanceHistoryModel model = new PerformanceHistoryModel(subject, ((int) score));
                                    performanceHistoryModelList.add(model);
                                    mAdapter.notifyDataSetChanged();
                                }

                                if (iterator == childrenCount){
                                    for (j = 0; j < performanceHistoryModelList.size(); j++){
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(sharedPreferencesManager.getMyUserID()).child(activeStudent).child("subjects").child(performanceHistoryModelList.get(j).getSubject()).child("status");
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    boolean status = dataSnapshot.getValue(boolean.class);
                                                    if (status) {
                                                        performanceHistoryModelList.get(subIterator).setNew(true);
                                                    } else {
                                                        performanceHistoryModelList.get(subIterator).setNew(false);
                                                    }
                                                } else {
                                                    performanceHistoryModelList.get(subIterator).setNew(false);
                                                }

                                                subIterator++;
                                                if (subIterator == performanceHistoryModelList.size()){
                                                    updateBadges();
                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    subIterator = 0;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(activeStudentName + " doesn't have any academic history at the moment");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateBadges(){
        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
        updateBadgesMap.put("AcademicRecordParentNotification/" + sharedPreferencesManager.getMyUserID() + "/" + activeStudentID + "/status", false);
        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(updateBadgesMap);
    }

//    @Override
//    public void onResume() {
//        loadDetailsFromFirebase();
//        super.onResume();
//    }
}
