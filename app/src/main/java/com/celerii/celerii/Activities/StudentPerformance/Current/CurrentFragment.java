package com.celerii.celerii.Activities.StudentPerformance.Current;


import android.content.Intent;
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
import com.celerii.celerii.adapters.PerformanceCurrentAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.PerformanceCurrentHeader;
import com.celerii.celerii.models.PerformanceCurrentModel;
import com.celerii.celerii.models.School;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentFragment extends Fragment {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<PerformanceCurrentModel> performanceCurrentModelList;
    private PerformanceCurrentHeader performanceCurrentHeader;
    private ArrayList<String> subjectList, subjectKey;
    public RecyclerView recyclerView;
    public PerformanceCurrentAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeStudentID = "", year, term;
    String activeStudent = "", classKey;
    String activeStudentName;
    Integer currentScoreHolder;
    boolean connected;

    public CurrentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current, container, false);

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

        performanceCurrentHeader = new PerformanceCurrentHeader();
        performanceCurrentModelList = new ArrayList<>();
        subjectList = new ArrayList<>();
        loadDetailsFromFirebase();
        mAdapter = new PerformanceCurrentAdapter(performanceCurrentModelList, performanceCurrentHeader, getActivity(), getContext(), activeStudent);
        recyclerView.setAdapter(mAdapter);

        year= Date.getYear();
        term = Term.getTermShort();

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

    int counter, j, subIterator = 0;
    double totalMax = 0, totalMyAverage = 0, totalClassAverage = 0;
    double averageMax = 0, averageMyAverage = 0, averageClassAverage = 0;
    int counterMax = 0, counterMyAverage = 0, counterClassAverage = 0;
    String schoolID, classID;
    String school = "No result", className = "No result";
    private void loadDetailsFromFirebase() {

        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        subjectList.clear();
        performanceCurrentModelList.clear();
        counter = 0;
        totalMax = 0; totalMyAverage = 0; totalClassAverage = 0;
        averageMax = 0; averageMyAverage = 0; averageClassAverage = 0;
        counterMax = 0; counterMyAverage = 0; counterClassAverage = 0;
        school = "No result"; className = "No result";
        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordStudent-Subject").child(activeStudentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    performanceCurrentModelList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        subjectList.add(postSnapshot.getKey());
                    }

                    for (int i = 0; i < subjectList.size(); i++) {
                        final String subject = subjectList.get(i);
                        final String subject_year_term = subject + "_" + year + "_" + term;
                        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(activeStudentID);
                        mDatabaseReference.orderByChild("subject_AcademicYear_Term").equalTo(subject_year_term).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                counter++;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                        schoolID = academicRecordStudent.getSchoolID();
                                        classID = academicRecordStudent.getClassID();
                                        totalMax += Double.valueOf(academicRecordStudent.getMaxObtainable());
                                        totalMyAverage += Double.valueOf(academicRecordStudent.getScore());
                                        totalClassAverage += Double.valueOf(academicRecordStudent.getClassAverage());
                                        counterMax++; counterMyAverage++; counterClassAverage++;
                                        PerformanceCurrentModel performanceCurrentModel = new PerformanceCurrentModel(subject, TypeConverterClass.convStringToInt(academicRecordStudent.getScore()));
                                        performanceCurrentModelList.add(performanceCurrentModel);
                                    }
                                }

                                if (counter == subjectList.size()) {
                                    if (performanceCurrentModelList.size() > 0) {
                                        averageMax = (totalMax / counterMax) * 1;
                                        averageMyAverage = totalMyAverage / counterMyAverage;
                                        averageClassAverage = totalClassAverage / counterClassAverage;
                                        performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(averageMax));
                                        performanceCurrentHeader.setTermAverage(String.valueOf(averageMyAverage));
                                        performanceCurrentHeader.setClassAverage(String.valueOf(averageClassAverage));



                                        mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    School schoolInstance = dataSnapshot.getValue(School.class);
                                                    school = schoolInstance.getSchoolName();
                                                }

                                                mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(classID);
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                                            className = classInstance.getClassName();
                                                        }

                                                        if (performanceCurrentModelList.size() > 1) {
                                                            Collections.sort(performanceCurrentModelList, new Comparator<PerformanceCurrentModel>() {
                                                                @Override
                                                                public int compare(PerformanceCurrentModel o1, PerformanceCurrentModel o2) {
                                                                    return o1.getSubject().compareTo(o2.getSubject());
                                                                }
                                                            });
                                                        }

                                                        for (j = 0; j < performanceCurrentModelList.size(); j++){
                                                            mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(sharedPreferencesManager.getMyUserID()).child(activeStudent).child("subjects").child(performanceCurrentModelList.get(j).getSubject()).child("status");
                                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()){
                                                                        boolean status = dataSnapshot.getValue(boolean.class);
                                                                        if (status) {
                                                                            performanceCurrentModelList.get(subIterator).setNew(true);
                                                                        } else {
                                                                            performanceCurrentModelList.get(subIterator).setNew(false);
                                                                        }
                                                                    } else {
                                                                        performanceCurrentModelList.get(subIterator).setNew(false);
                                                                    }

                                                                    subIterator++;
                                                                    if (subIterator == performanceCurrentModelList.size()){
                                                                        updateBadges();
                                                                        performanceCurrentHeader.setTerm(term);
                                                                        performanceCurrentHeader.setYear(year);
                                                                        performanceCurrentHeader.setClassName(className);
                                                                        performanceCurrentHeader.setSchool(school);
                                                                        performanceCurrentHeader.setStudent(activeStudentID);
                                                                        performanceCurrentModelList.add(0, new PerformanceCurrentModel());
                                                                        mAdapter.notifyDataSetChanged();
                                                                        recyclerView.setVisibility(View.VISIBLE);
                                                                        errorLayout.setVisibility(View.GONE);
                                                                        mySwipeRefreshLayout.setRefreshing(false);
                                                                        progressLayout.setVisibility(View.GONE);
                                                                        subIterator = 0;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
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
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(averageMax));
                                        performanceCurrentHeader.setTermAverage(String.valueOf(averageMyAverage));
                                        performanceCurrentHeader.setClassAverage(String.valueOf(averageClassAverage));
                                        performanceCurrentHeader.setTerm(term);
                                        performanceCurrentHeader.setYear(year);
                                        performanceCurrentHeader.setClassName(className);
                                        performanceCurrentHeader.setSchool(school);
                                        performanceCurrentHeader.setStudent(activeStudentID);
                                        performanceCurrentModelList.add(0, new PerformanceCurrentModel());
                                        mAdapter.notifyDataSetChanged();
                                        recyclerView.setVisibility(View.VISIBLE);
                                        errorLayout.setVisibility(View.GONE);
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        progressLayout.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(averageMax));
                    performanceCurrentHeader.setTermAverage(String.valueOf(averageMyAverage));
                    performanceCurrentHeader.setClassAverage(String.valueOf(averageClassAverage));
                    performanceCurrentHeader.setTerm(term);
                    performanceCurrentHeader.setYear(year);
                    performanceCurrentHeader.setClassName(className);
                    performanceCurrentHeader.setSchool(school);
                    performanceCurrentHeader.setStudent(activeStudentID);
                    performanceCurrentModelList.add(0, new PerformanceCurrentModel());
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if(resultCode == getActivity().RESULT_OK) {
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                term = data.getStringExtra("Selected Term");
                loadDetailsFromFirebase();
            }
        }

        if (requestCode == 1) {
            if(resultCode == getActivity().RESULT_OK) {
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                year = data.getStringExtra("Selected Year");
                loadDetailsFromFirebase();
            }
        }
    }
}
