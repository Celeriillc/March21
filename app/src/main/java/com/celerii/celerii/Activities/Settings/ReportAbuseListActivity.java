package com.celerii.celerii.Activities.Settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ReportAbuseAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.ReportUserModel;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.StudentsSchoolsClassesandTeachersModel;
import com.celerii.celerii.models.Teacher;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ReportAbuseListActivity extends AppCompatActivity {

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

    Toolbar toolbar;
    private ArrayList<ReportUserModel> reportUserModelList;
    public ReportAbuseAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList = new ArrayList<>();
    ArrayList<StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelList = new ArrayList<>();
    HashMap<String, Object> reportUserModelMap = new HashMap<>();
    int counter = 0;

    String activeAccount = "";
    String activeAccountID = "";

    String featureUseKey = "";
    String featureName = "Report Abuse List";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_abuse_list);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(this);
        activeAccount = sharedPreferencesManager.getActiveAccount();
        activeAccountID = sharedPreferencesManager.getMyUserID();

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report Abuse");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        reportUserModelList = new ArrayList<>();
        mAdapter = new ReportAbuseAdapter(reportUserModelList, activeAccount,this);
        if (activeAccount.equals("Teacher")){
            loadFromFirebaseTeacher();
        } else if (activeAccount.equals("Parent")){
            loadFromFirebaseParent();
        }
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (activeAccount.equals("Teacher")){
                            loadFromFirebaseTeacher();
                        } else if (activeAccount.equals("Parent")){
                            loadFromFirebaseParent();
                        }
                    }
                }
        );
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
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;

        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();
        String mFirebaseUserID = mFirebaseUser.getUid();

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        DatabaseReference featureUseUpdateRef = FirebaseDatabase.getInstance().getReference();
        featureUseUpdateRef.updateChildren(featureUseUpdateMap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    ArrayList<String> studentsList = new ArrayList<>();
    ArrayList<String> parentsList = new ArrayList<>();
    int schoolCounter = 0;
    private void loadFromFirebaseTeacher() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        Gson gson = new Gson();
        classesStudentsAndParentsModelList = new ArrayList<>();
        String classesStudentsAndParentsModelListJSON = sharedPreferencesManager.getClassesStudentParent();
        Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
        classesStudentsAndParentsModelList = gson.fromJson(classesStudentsAndParentsModelListJSON, type);

        if (classesStudentsAndParentsModelList == null) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("You do not have any Parents to report at this time");
        } else {
            counter = 0;
            reportUserModelList.clear();
            reportUserModelMap.clear();
            for (final ClassesStudentsAndParentsModel classesStudentsAndParentsModel : classesStudentsAndParentsModelList) {
                mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(classesStudentsAndParentsModel.getParentID());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        counter++;
                        if (dataSnapshot.exists()) {
                            Parent parent = dataSnapshot.getValue(Parent.class);
                            String parentID = dataSnapshot.getKey();
                            ReportUserModel reportUser = new ReportUserModel(parent.getFirstName() + " " + parent.getLastName(), parent.getProfilePicURL(), parentID);
                            if (!parent.getDeleted()) {
                                if (!reportUserModelMap.containsKey(parentID)) {
                                    reportUserModelList.add(reportUser);
                                    reportUserModelMap.put(parentID, reportUser);
                                }
                            }
                        }

                        if ((counter / 2) == classesStudentsAndParentsModelList.size()) {
                            if (reportUserModelList.size() > 0) {
                                Collections.sort(reportUserModelList, new Comparator<ReportUserModel>() {
                                    @Override
                                    public int compare(ReportUserModel o1, ReportUserModel o2) {
                                        return o1.getName().compareTo(o2.getName());
                                    }
                                });
                                reportUserModelList.add(0, new ReportUserModel());
                                mAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                                progressLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                errorLayout.setVisibility(View.GONE);
                            } else {
                                mySwipeRefreshLayout.setRefreshing(false);
                                recyclerView.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                                errorLayoutText.setText("You do not have any parents or schools to report at this time");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(classesStudentsAndParentsModel.getSchoolID());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        counter++;
                        if (dataSnapshot.exists()) {
                            School school = dataSnapshot.getValue(School.class);
                            String schoolID = dataSnapshot.getKey();
                            ReportUserModel reportUser = new ReportUserModel(school.getSchoolName(), school.getProfilePhotoUrl(), schoolID);
                            if (!school.getDeleted()) {
                                if (!reportUserModelMap.containsKey(schoolID)) {
                                    reportUserModelList.add(reportUser);
                                    reportUserModelMap.put(schoolID, reportUser);
                                }
                            }
                        }

                        if ((counter / 2) == classesStudentsAndParentsModelList.size()) {
                            if (reportUserModelList.size() > 0) {
                                Collections.sort(reportUserModelList, new Comparator<ReportUserModel>() {
                                    @Override
                                    public int compare(ReportUserModel o1, ReportUserModel o2) {
                                        return o1.getName().compareTo(o2.getName());
                                    }
                                });
                                reportUserModelList.add(0, new ReportUserModel());
                                mAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                                progressLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                errorLayout.setVisibility(View.GONE);
                            } else {
                                mySwipeRefreshLayout.setRefreshing(false);
                                recyclerView.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                                errorLayoutText.setText("You do not have any parents or schools to report at this time");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    ArrayList<String> teachersList = new ArrayList<>();
    private void loadFromFirebaseParent() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        schoolCounter = 0;
        Gson gson = new Gson();
        studentsSchoolsClassesandTeachersModelList = new ArrayList<>();
        String studentsSchoolsClassesandTeachersModelListJSON = sharedPreferencesManager.getStudentsSchoolsClassesTeachers();
        Type type = new TypeToken<ArrayList<StudentsSchoolsClassesandTeachersModel>>() {}.getType();
        studentsSchoolsClassesandTeachersModelList = gson.fromJson(studentsSchoolsClassesandTeachersModelListJSON, type);

        if (studentsSchoolsClassesandTeachersModelList == null) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("You do not have any Teachers to report at this time");
        } else {
            counter = 0;
            reportUserModelList.clear();
            reportUserModelMap.clear();
            for (final StudentsSchoolsClassesandTeachersModel studentsSchoolsClassesandTeachersModel : studentsSchoolsClassesandTeachersModelList) {
                mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(studentsSchoolsClassesandTeachersModel.getTeacherID());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        counter++;
                        if (dataSnapshot.exists()) {
                            Teacher teacher = dataSnapshot.getValue(Teacher.class);
                            String teacherID = dataSnapshot.getKey();
                            ReportUserModel reportUser = new ReportUserModel(teacher.getFirstName() + " " + teacher.getLastName(), teacher.getProfilePicURL(), teacherID);
                            if (!teacher.getDeleted()) {
                                if (!reportUserModelMap.containsKey(teacherID)) {
                                    reportUserModelList.add(reportUser);
                                    reportUserModelMap.put(teacherID, reportUser);
                                }
                            }
                        }

                        if ((counter / 2) == studentsSchoolsClassesandTeachersModelList.size()) {
                            if (reportUserModelList.size() > 0) {
                                Collections.sort(reportUserModelList, new Comparator<ReportUserModel>() {
                                    @Override
                                    public int compare(ReportUserModel o1, ReportUserModel o2) {
                                        return o1.getName().compareTo(o2.getName());
                                    }
                                });
                                reportUserModelList.add(0, new ReportUserModel());
                                mAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                                progressLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                errorLayout.setVisibility(View.GONE);
                            } else {
                                mySwipeRefreshLayout.setRefreshing(false);
                                recyclerView.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                                errorLayoutText.setText("You do not have any teacher or schools to report at this time");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(studentsSchoolsClassesandTeachersModel.getSchoolID());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        counter++;
                        if (dataSnapshot.exists()) {
                            School school = dataSnapshot.getValue(School.class);
                            String schoolID = dataSnapshot.getKey();
                            ReportUserModel reportUser = new ReportUserModel(school.getSchoolName(), school.getProfilePhotoUrl(), schoolID);
                            if (!school.getDeleted()) {
                                if (!reportUserModelMap.containsKey(schoolID)) {
                                    reportUserModelList.add(reportUser);
                                    reportUserModelMap.put(schoolID, reportUser);
                                }
                            }
                        }

                        if ((counter / 2) == studentsSchoolsClassesandTeachersModelList.size()) {
                            if (reportUserModelList.size() > 0) {
                                Collections.sort(reportUserModelList, new Comparator<ReportUserModel>() {
                                    @Override
                                    public int compare(ReportUserModel o1, ReportUserModel o2) {
                                        return o1.getName().compareTo(o2.getName());
                                    }
                                });
                                reportUserModelList.add(0, new ReportUserModel());
                                mAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                                progressLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                errorLayout.setVisibility(View.GONE);
                            } else {
                                mySwipeRefreshLayout.setRefreshing(false);
                                recyclerView.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                                errorLayoutText.setText("You do not have any teacher or schools to report at this time");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

//        reportUserModelList.clear();
//        studentsList = new ArrayList<>();
//        studentCount = 0;
//        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(activeAccountID);
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        studentsList.add(postSnapshot.getKey());
//                    }
//
//                    for (String student : studentsList) {
//                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Teacher").child(student);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                studentCount++;
//                                if (dataSnapshot.exists()) {
//                                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
//                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                        final String teacherKey = postSnapshot.getKey();
//                                        teachersList.add(teacherKey);
//
//                                    }
//                                }
//
//                                if (studentCount == studentsList.size()) {
//                                    if (teachersList.size() > 0) {
//
//                                        for (final String teacherID : teachersList) {
//                                            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherID);
//                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot.exists()) {
//                                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
//                                                        ReportUserModel reportUser = new ReportUserModel(teacher.getFirstName() + " " + teacher.getLastName(), teacher.getProfilePicURL(), teacherID);
//                                                        reportUserModelList.add(reportUser);
//                                                    }
//
//                                                    if (reportUserModelList.size() == studentsList.size()) {
//                                                        reportUserModelList.add(0, new ReportUserModel());
//                                                        mAdapter.notifyDataSetChanged();
//                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        }
//                                    } else {
//                                        mySwipeRefreshLayout.setRefreshing(false);
//                                        recyclerView.setVisibility(View.GONE);
//                                        progressLayout.setVisibility(View.GONE);
//                                        errorLayout.setVisibility(View.VISIBLE);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//                } else {
//                    mySwipeRefreshLayout.setRefreshing(false);
//                    recyclerView.setVisibility(View.GONE);
//                    progressLayout.setVisibility(View.GONE);
//                    errorLayout.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent Teacher").child(activeAccountID);
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    reportUserModelList.clear();
//                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
//                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        final String teacherKey = postSnapshot.getKey();
//
//                        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherKey);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()){
//                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
//                                    ReportUserModel reportUser = new ReportUserModel(teacher.getFirstName() + " " + teacher.getLastName(), teacher.getProfilePicURL(), teacherKey);
//                                    reportUserModelList.add(reportUser);
//                                }
//
//                                if (childrenCount == reportUserModelList.size()) {
//                                    reportUserModelList.add(0, new ReportUserModel());
//                                    mAdapter.notifyDataSetChanged();
//                                    mySwipeRefreshLayout.setRefreshing(false);
//                                    progressLayout.setVisibility(View.GONE);
//                                    recyclerView.setVisibility(View.VISIBLE);
//                                    errorLayout.setVisibility(View.GONE);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                } else {
//                    mySwipeRefreshLayout.setRefreshing(false);
//                    recyclerView.setVisibility(View.GONE);
//                    progressLayout.setVisibility(View.GONE);
//                    errorLayout.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
