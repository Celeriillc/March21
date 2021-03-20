package com.celerii.celerii.Activities.StudentPerformance.History;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.PerformanceHistoryAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.PerformanceHistoryModel;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;


public class HistoryFragment extends Fragment {

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

    private ArrayList<PerformanceHistoryModel> performanceHistoryModelList;
    private ArrayList<String> subjectList, subjectKey;
    public RecyclerView recyclerView;
    public PerformanceHistoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeStudentID = "";
    String activeStudent = "";
    String activeStudentName = "";

    String featureUseKey = "";
    String featureName = "Historical Academic Results";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

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

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ParentSearchActivity.class));
            }
        });

        StudentPerformanceForParentsActivity activity = (StudentPerformanceForParentsActivity) getActivity();
        activeStudent = activity.getData();

        if (activeStudent == null) {
            Gson gson = new Gson();
            ArrayList<Student> myChildren = new ArrayList<>();
            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            Type type = new TypeToken<ArrayList<Student>>() {}.getType();
            myChildren = gson.fromJson(myChildrenJSON, type);

            if (myChildren != null) {
                if (myChildren.size() > 0) {
                    gson = new Gson();
                    activeStudent = gson.toJson(myChildren.get(0));
                    sharedPreferencesManager.setActiveKid(activeStudent);
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                    } else {
                        errorLayoutText.setText("It seems like you do not have the permission to view this child's academic record");
                    }

                    return view;
                }
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                } else {
                    errorLayoutText.setText("It seems like you do not have the permission to view this child's academic record");
                }

                return view;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {
                }.getType();
                Student activeKidModel = gson.fromJson(activeStudent, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student : myChildren) {
                    if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                        activeKidExist = true;
                        activeKidModel = student;
                        activeStudent = gson.toJson(activeKidModel);
                        sharedPreferencesManager.setActiveKid(activeStudent);
                        break;
                    }
                }

                if (!activeKidExist) {
                    if (myChildren.size() > 0) {
                        if (myChildren.size() > 1) {
                            gson = new Gson();
                            activeStudent = gson.toJson(myChildren.get(0));
                            sharedPreferencesManager.setActiveKid(activeStudent);
                        }
                    } else {
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                            errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                            errorLayoutButton.setText("Find my child");
                            errorLayoutButton.setVisibility(View.VISIBLE);
                        } else {
                            errorLayoutText.setText("It seems like you do not have the permission to view this child's academic record");
                        }

                        return view;
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeStudentModel = gson.fromJson(activeStudent, type);
        activeStudentID = activeStudentModel.getStudentID();
        activeStudentName = activeStudentModel.getFirstName() + " " + activeStudentModel.getLastName();

        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        performanceHistoryModelList = new ArrayList<>();
        subjectList = new ArrayList<>();
        mAdapter = new PerformanceHistoryAdapter(performanceHistoryModelList, context, activeStudent);
        recyclerView.setAdapter(mAdapter);
        loadNewDetailsFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadNewDetailsFromFirebase();
                    }
                }
        );

        return view;
    }

    int counter = 0;
    HashMap<String, Double> subjectTotal = new HashMap<>();
    HashMap<String, Integer> subjectCount = new HashMap<>();
    private void loadNewDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        counter = 0;
        subjectTotal = new HashMap<>();
        subjectCount = new HashMap<>();
        performanceHistoryModelList.clear();
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(activeStudentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String subject_year_term = postSnapshot.getKey();
                        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(activeStudentID).child(subject_year_term);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                counter++;
                                if (dataSnapshot.exists()) {
                                    double termAverage = 0.0;
                                    String subject = "";
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                        subject = academicRecordStudent.getSubject();
                                        double testClassAverage = Double.valueOf(academicRecordStudent.getScore());
                                        double maxObtainable = Double.valueOf(academicRecordStudent.getMaxObtainable());
                                        double percentageOfTotal = Double.valueOf(academicRecordStudent.getPercentageOfTotal());

                                        double normalizedTestClassAverage = (testClassAverage / maxObtainable) * percentageOfTotal;
                                        termAverage += normalizedTestClassAverage;
                                    }

                                    if (!subjectTotal.containsKey(subject)) {
                                        subjectTotal.put(subject, termAverage);
                                    } else {
                                        subjectTotal.put(subject, subjectTotal.get(subject) + termAverage);
                                    }

                                    if (!subjectCount.containsKey(subject)) {
                                        subjectCount.put(subject, 1);
                                    } else {
                                        subjectCount.put(subject, subjectCount.get(subject) + 1);
                                    }
                                }

                                if (counter == childrenCount) {
                                    for (String key: subjectTotal.keySet()) {
                                        double score = (int)((subjectTotal.get(key) / subjectCount.get(key)));
                                        PerformanceHistoryModel model = new PerformanceHistoryModel(key, ((int) score));
                                        performanceHistoryModelList.add(model);
                                    }

                                    updateBadges();
                                    mAdapter.notifyDataSetChanged();
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    progressLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    errorLayout.setVisibility(View.GONE);
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
                    errorLayoutText.setText(activeStudentName + " doesn't have any academic history at the moment");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    int iterator = 0; int j; int subIterator = 0;
    private void loadDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
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
                    mAdapter.notifyDataSetChanged();

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
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(sharedPreferencesManager.getMyUserID()).child(activeStudentID).child("subjects").child(performanceHistoryModelList.get(j).getSubject()).child("status");
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
