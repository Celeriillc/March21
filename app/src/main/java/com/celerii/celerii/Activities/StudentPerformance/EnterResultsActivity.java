package com.celerii.celerii.Activities.StudentPerformance;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.EnterResultAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.LogoutProtocol;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.helperClasses.TeacherEnterResultsSharedPreferences;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecord;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.EnterResultHeader;
import com.celerii.celerii.models.EnterResultRow;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class EnterResultsActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    TeacherEnterResultsSharedPreferences teacherEnterResultsSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    private ArrayList<EnterResultRow> enterResultRowList;
    private ArrayList<String> parentList;
    private HashMap<String, ArrayList<String>> studentParentList = new HashMap<String, ArrayList<String>>();
    private EnterResultHeader enterResultHeader;
    public RecyclerView recyclerView;
    public EnterResultAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    String childName;
    String childImageURL;
    String activeClass = "";
    String myName, myID, className, activeSchoolID, subject, testType, maximumScore, percentageOfTotal, term, date, dateForAdapter, sortableDate;
    String classID, teacherID, year, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear, class_subject_AcademicYear_Term, class_subject_Term_AcademicYear;
    Double newScore, score, classAverage = 0.0;
    Double previousPercentageOfTotal = 0.0;
    String pushID, uniquePushID;
    boolean connected = true;

    Map<String, Object> newResultEntry;
    Map<String, String> existingStudentScores;

    String featureUseKey = "";
    String featureName = "Enter Class Results";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(this);
        teacherEnterResultsSharedPreferences = new TeacherEnterResultsSharedPreferences(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });

        activeClass = sharedPreferencesManager.getActiveClass();

        if (activeClass == null) {
            Gson gson = new Gson();
            ArrayList<Class> myClasses = new ArrayList<>();
            String myClassesJSON = sharedPreferencesManager.getMyClasses();
            Type type = new TypeToken<ArrayList<Class>>() {}.getType();
            myClasses = gson.fromJson(myClassesJSON, type);

            if (myClasses != null) {
                if (myClasses.size() > 0) {
                    gson = new Gson();
                    activeClass = gson.toJson(myClasses.get(0));
                    sharedPreferencesManager.setActiveClass(activeClass);
                } else {
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle("New Class Result");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your classes' account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your school to access your classes or get started by clicking the " + "<b>" + "Find my school" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my school");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                    return;
                }
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("New Class Result");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(Html.fromHtml("You're not connected to any of your classes' account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your school to access your classes or get started by clicking the " + "<b>" + "Find my school" + "</b>" + " button below"));
                errorLayoutButton.setText("Find my school");
                errorLayoutButton.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            Boolean activeClassExist = false;
            Gson gson = new Gson();
            Type type = new TypeToken<Class>() {}.getType();
            Class activeClassModel = gson.fromJson(activeClass, type);

            String myClassesJSON = sharedPreferencesManager.getMyClasses();
            type = new TypeToken<ArrayList<Class>>() {}.getType();
            ArrayList<Class> myClasses = gson.fromJson(myClassesJSON, type);

            for (Class classInstance: myClasses) {
                if (activeClassModel.getID().equals(classInstance.getID())) {
                    activeClassExist = true;
                    activeClassModel = classInstance;
                    activeClass = gson.toJson(activeClassModel);
                    sharedPreferencesManager.setActiveClass(activeClass);
                    break;
                }
            }

            if (!activeClassExist) {
                if (myClasses.size() > 0) {
                    gson = new Gson();
                    activeClass = gson.toJson(myClasses.get(0));
                    sharedPreferencesManager.setActiveClass(activeClass);
                } else {
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle("New Class Result");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your classes' account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your school to access your classes or get started by clicking the " + "<b>" + "Find my school" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my school");
                    errorLayoutButton.setVisibility(View.VISIBLE);return;
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Class>() {}.getType();
        Class activeClassModel = gson.fromJson(activeClass, type);
        activeClass = activeClassModel.getID();
        className = activeClassModel.getClassName();

        myName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        myID = sharedPreferencesManager.getMyUserID();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New " + className + " Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        enterResultHeader = new EnterResultHeader();
        enterResultRowList = new ArrayList<>();
        parentList = new ArrayList<>();
        studentParentList = new HashMap<String, ArrayList<String>>();
        mAdapter = new EnterResultAdapter(enterResultRowList, enterResultHeader, this, this);
        loadNewHeaderFromFirebase();
        loadNewDetailsFromFirebase();
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadNewHeaderFromFirebase();
                        loadNewDetailsFromFirebase();
                    }
                }
        );

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Date Information"));
    }

    private void loadNewHeaderFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        subject = teacherEnterResultsSharedPreferences.getSubject();
        date = Date.getDate();
        sortableDate = Date.convertToSortableDate(date);
        testType = "Continuous Assessment";
        maximumScore = "100";
        percentageOfTotal = "30";
        year = Date.getYear();
        term = Term.getTermShort();
        academicYear_Term = year + "_" + term;
        term_AcademicYear = term + "_" + year;
        subject_AcademicYear_Term = subject + "_" + year + "_" + term;
        subject_Term_AcademicYear = subject + "_" + term + "_" + year;
        class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
        class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;

        enterResultHeader.setSubject(subject);
        enterResultHeader.setDate(date);
        enterResultHeader.setSortableDate(sortableDate);
        enterResultHeader.setTestType(testType);
        enterResultHeader.setMaxScore(maximumScore);
        enterResultHeader.setPercentageOfTotal(percentageOfTotal);
        enterResultHeader.setYear(year);
        enterResultHeader.setMonth(Date.getMonth());
        enterResultHeader.setDay(Date.getDay());
        enterResultHeader.setTerm(term);
        enterResultHeader.setClassID(activeClass);
        enterResultHeader.setClassName(className);
        enterResultHeader.setTeacherID(myID);
        enterResultHeader.setTeacher(myName);
        enterResultHeader.setPreviousPercentageOfTotal(previousPercentageOfTotal);
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordClass").child(activeClass).child(subject_AcademicYear_Term);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    previousPercentageOfTotal = 0.0;
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        AcademicRecord academicRecord = postSnapshot.getValue(AcademicRecord.class);
                        previousPercentageOfTotal += Double.valueOf(academicRecord.getPercentageOfTotal());
                    }
                } else {
                    previousPercentageOfTotal = 0.0;
                }

                if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                    percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
                }

                enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                enterResultHeader.setPreviousPercentageOfTotal(previousPercentageOfTotal);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("Class School/" + activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        activeSchoolID = postSnapshot.getKey();
                        enterResultHeader.setSchoolID(activeSchoolID);
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadHeaderFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        subject = teacherEnterResultsSharedPreferences.getSubject();
        date = Date.getDate();
        sortableDate = Date.convertToSortableDate(date);
        testType = "Continuous Assessment";
        maximumScore = "100";
        percentageOfTotal = "30";
        year = Date.getYear();
        term = Term.getTermShort();
        academicYear_Term = year + "_" + term;
        term_AcademicYear = term + "_" + year;
        subject_AcademicYear_Term = subject + "_" + year + "_" + term;
        subject_Term_AcademicYear = subject + "_" + term + "_" + year;
        class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
        class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;

        enterResultHeader.setSubject(subject);
        enterResultHeader.setDate(date);
        enterResultHeader.setSortableDate(sortableDate);
        enterResultHeader.setTestType(testType);
        enterResultHeader.setMaxScore(maximumScore);
        enterResultHeader.setPercentageOfTotal(percentageOfTotal);
        enterResultHeader.setYear(year);
        enterResultHeader.setMonth(Date.getMonth());
        enterResultHeader.setDay(Date.getDay());
        enterResultHeader.setTerm(term);
        enterResultHeader.setClassID(activeClass);
        enterResultHeader.setClassName(className);
        enterResultHeader.setTeacherID(myID);
        enterResultHeader.setTeacher(myName);
        enterResultHeader.setPreviousPercentageOfTotal(previousPercentageOfTotal);
        mAdapter.notifyDataSetChanged();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordClass").child(activeClass).child(class_subject_AcademicYear_Term).child("percentageOfTotal");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    previousPercentageOfTotal = Double.valueOf(dataSnapshot.getValue(String.class));
                } else {
                    previousPercentageOfTotal = 0.0;
                }

                if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                    percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
                }

                enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                enterResultHeader.setPreviousPercentageOfTotal(previousPercentageOfTotal);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("Class School/" + activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        activeSchoolID = postSnapshot.getKey();
                        enterResultHeader.setSchoolID(activeSchoolID);
                        mAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadNewDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        Gson gson = new Gson();
        ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList = new ArrayList<>();
        String myClassesStudentsParentsJSON = sharedPreferencesManager.getClassesStudentParent();
        Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
        classesStudentsAndParentsModelList = gson.fromJson(myClassesStudentsParentsJSON, type);

        if (classesStudentsAndParentsModelList == null) {

        } else {
            studentParentList.clear();
            for (ClassesStudentsAndParentsModel classesStudentsAndParentsModel: classesStudentsAndParentsModelList) {
                String studentID = classesStudentsAndParentsModel.getStudentID();
                String parentID = classesStudentsAndParentsModel.getParentID();

                if (!parentID.isEmpty()) {
                    try {
                        if (!studentParentList.get(studentID).contains(parentID)) {
                            studentParentList.get(studentID).add(parentID);
                        }
                    } catch (Exception e) {
                        studentParentList.put(studentID, new ArrayList<String>());
                        studentParentList.get(studentID).add(parentID);
                    }
                }
            }
        }

        gson = new Gson();
        HashMap<String, HashMap<String, Student>> classStudentsForTeacherMap = new HashMap<String, HashMap<String, Student>>();
        String classStudentsForTeacherJSON = sharedPreferencesManager.getClassStudentForTeacher();
        type = new TypeToken<HashMap<String, HashMap<String, Student>>>() {}.getType();
        classStudentsForTeacherMap = gson.fromJson(classStudentsForTeacherJSON, type);

        if (classStudentsForTeacherMap == null || classStudentsForTeacherMap.size() == 0) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText(Html.fromHtml(className + " doesn't contain any students. You can change the active class to another with students in the " + "<b>" + "More" + "</b>" + " area"));
        } else {
            enterResultRowList.clear();
            mAdapter.notifyDataSetChanged();
            HashMap<String, Student> classMap = classStudentsForTeacherMap.get(activeClass);

            if (classMap != null) {
                for (Map.Entry<String, Student> entry : classMap.entrySet()) {
                    String studentID = entry.getKey();
                    Student studentModel = entry.getValue();
                    String name = studentModel.getFirstName() + " " + studentModel.getLastName();
                    EnterResultRow enterResultRow = new EnterResultRow(name, studentModel.getImageURL(), "0");
                    enterResultRow.setStudentID(studentID);
                    enterResultRowList.add(enterResultRow);
                }
            }

            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            if (enterResultRowList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(Html.fromHtml(className + " doesn't contain any students. You can change the active class to another with students in the " + "<b>" + "More" + "</b>" + " area"));
            } else {
                enterResultRowList.add(0, new EnterResultRow());
                enterResultRowList.add(new EnterResultRow());
                recyclerView.setItemViewCacheSize(enterResultRowList.size());
                recyclerView.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Class Students/" + activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String studentKey = postSnapshot.getKey();

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Student Parent/" + studentKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        String parentKey = postSnapshot.getKey();
                                        try {
                                            studentParentList.get(studentKey).add(parentKey);
                                        } catch (Exception e) {
                                            studentParentList.put(studentKey, new ArrayList<String>());
                                            studentParentList.get(studentKey).add(parentKey);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference("Class Students/" + activeClass);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            enterResultRowList.clear();
                            mAdapter.notifyDataSetChanged();
                            final int childrenCount = (int) dataSnapshot.getChildrenCount();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String childKey = postSnapshot.getKey();

                                DatabaseReference childDatabaseReference = mFirebaseDatabase.getReference("Student/" + childKey);
                                childDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Student child = dataSnapshot.getValue(Student.class);
                                        childName = child.getFirstName() + " " + child.getLastName();
                                        childImageURL = child.getImageURL();

                                        EnterResultRow enterResultRow = new EnterResultRow(childName, childImageURL, "0");
                                        enterResultRow.setStudentID(dataSnapshot.getKey());
                                        enterResultRowList.add(enterResultRow);

                                        if (childrenCount == enterResultRowList.size()){
                                            enterResultRowList.add(0, new EnterResultRow());
                                            enterResultRowList.add(new EnterResultRow());
                                            recyclerView.setVisibility(View.VISIBLE);
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            mAdapter.notifyDataSetChanged();
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
                            errorLayoutText.setText("This class doesn't contain any students");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                subject = data.getStringExtra("Selected Subject");
                subject_AcademicYear_Term = subject + "_" + year + "_" + term;
                subject_Term_AcademicYear = subject + "_" + term + "_" + year;
                class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
                class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;
                updateNewPercentageOfTotal(subject_AcademicYear_Term);
                enterResultHeader.setSubject(subject);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                testType = data.getStringExtra("Selected Test Type");
                enterResultHeader.setTestType(testType);

                if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                    percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
                    enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                }

                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 3) {
            if(resultCode == RESULT_OK) {
                maximumScore = data.getStringExtra("Max Obtainable");
                enterResultHeader.setMaxScore(maximumScore);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 4) {
            if(resultCode == RESULT_OK) {
                percentageOfTotal = data.getStringExtra("PercentageOfTotal");
                enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == 5) {
            if(resultCode == RESULT_OK) {
                term = data.getStringExtra("Selected Term");
                academicYear_Term = year + "_" + term;
                term_AcademicYear = term + "_" + year;
                subject_AcademicYear_Term = subject + "_" + year + "_" + term;
                subject_Term_AcademicYear = subject + "_" + term + "_" + year;
                class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
                class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;
                updateNewPercentageOfTotal(class_subject_AcademicYear_Term);
                enterResultHeader.setTerm(term);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateNewPercentageOfTotal(String subjectKey) {
        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(EnterResultsActivity.this);
        progressDialog.show();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordClass").child(activeClass).child(subjectKey);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        AcademicRecord academicRecord = postSnapshot.getValue(AcademicRecord.class);
                        previousPercentageOfTotal += Double.valueOf(academicRecord.getPercentageOfTotal());
                    }
                } else {
                    previousPercentageOfTotal = 0.0;
                    percentageOfTotal = "30";
                }

                if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                    percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
                }

                enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                enterResultHeader.setPreviousPercentageOfTotal(previousPercentageOfTotal);
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updatePercentageOfTotal(String subjectKey){
        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(EnterResultsActivity.this);
        progressDialog.show();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordClass").child(activeClass).child(subjectKey).child("percentageOfTotal");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    previousPercentageOfTotal = Double.valueOf(dataSnapshot.getValue(String.class));
                } else {
                    previousPercentageOfTotal = 0.0;
                    percentageOfTotal = "30";
                }

                if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                    percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
                }

                enterResultHeader.setPercentageOfTotal(percentageOfTotal);
                enterResultHeader.setPreviousPercentageOfTotal(previousPercentageOfTotal);
                mAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void confirmSaveToCloud() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_binary_selection_dialog_with_cancel);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button save = (Button) dialog.findViewById(R.id.optionone);
        Button cancel = (Button) dialog.findViewById(R.id.optiontwo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        message.setText(Html.fromHtml("Please confirm the "  + "<b>" + enterResultHeader.getSubject() + "</b>" + ", " + "<b>" + Term.Term(enterResultHeader.getTerm()) + "</b>" + " academic information " +
                "you're about to save for " + "<b>" + className + "</b>" + ". Click the " + "<b>" + "Save" + "</b>" + " button " +
                "if you have confirmed the information."));

        save.setText("Save");
        cancel.setText("Cancel");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveNewToCloud();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void saveNewToCloud() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            showDialogWithMessage("Internet is down, check your connection and try again", false);
            return;
        }

        if (!enterResultHeader.getMaxScore().matches("^-?\\d+$")) {
            showDialogWithMessage("The maximum obtainable score for this test has to be a whole number", false);
            return;
        }

        int maxObt = Integer.parseInt(enterResultHeader.getMaxScore());
        if (maxObt <= 0) {
            showDialogWithMessage("The maximum obtainable score for this test has to be a whole number greater than zero (0)", false);
            return;
        }

        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(EnterResultsActivity.this);
        progressDialog.show();

//        for (int i = 0; i < recyclerView.getChildCount(); i++) {
//            try {
//                EnterResultAdapter.MyViewHolder holder = (EnterResultAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
//                assert holder != null;
//                enterResultRowList.get(i).setScore(holder.score.getText().toString());
//            } catch (Exception e) {
//                continue;
//            }
//        }

//        date = Date.getDate();
//        sortableDate = Date.convertToSortableDate(date);
        academicYear_Term = year + "_" + term;
        term_AcademicYear = term + "_" + year;
        subject_AcademicYear_Term = subject + "_" + year + "_" + term;
        subject_Term_AcademicYear = subject + "_" + term + "_" + year;
        class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
        class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;

        String device = "Android";

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordClass").child(activeClass).child(subject_AcademicYear_Term).push();
        pushID = mDatabaseReference.getKey();
        newResultEntry = new HashMap<>();

        double recordClassAve = 0.0;
        double recordTotalScore = 0.0;
        int recordCounter = 0;

        //Get Class Average
        //TODO: Check for null scores
        for (int i = 0; i < enterResultRowList.size(); i++) {
            if (enterResultRowList.get(i).getStudentID() != null) {
                int scoreInteger = Integer.parseInt(enterResultRowList.get(i).getScore());
                int maximumScoreInteger = Integer.parseInt(maximumScore);
                if (enterResultRowList.get(i).getScore().equals("")) {
                    showDialogWithMessage("A student score can not be empty. Please verify your entry and try again", false);
                    progressDialog.dismiss();
                    return;
                } else if (scoreInteger > maximumScoreInteger) {
                    showDialogWithMessage(enterResultRowList.get(i).getName() +  "'s score is more than the maximum obtainable for this test. Please verify your entry and try again", false);
                    progressDialog.dismiss();
                    return;
                } else {
                    recordTotalScore += Double.valueOf(enterResultRowList.get(i).getScore());
                    recordCounter++;
                }
            }
        }
        recordClassAve = recordTotalScore / (recordCounter - 2);

        final AcademicRecord academicRecord = new AcademicRecord(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, device, term, year, subject,
                date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
                class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType, maximumScore, percentageOfTotal);
        academicRecord.setClassAverage(String.valueOf(recordClassAve));

        newResultEntry.put("AcademicRecordClass/" + activeClass + "/" + subject_AcademicYear_Term + "/" + pushID, academicRecord);
        newResultEntry.put("AcademicRecordTeacher/" +  auth.getCurrentUser().getUid() + "/" + subject_AcademicYear_Term + "/" + pushID, academicRecord);
        newResultEntry.put("AcademicRecordClass-Subject/" + activeClass + "/" + subject, true);
        newResultEntry.put("AcademicRecordTeacher-Subject/" +  auth.getCurrentUser().getUid() + "/" + subject, true);

        for (int i = 0; i < enterResultRowList.size(); i++) {
            if (!enterResultRowList.get(i).getStudentID().equals("")) {
                newResultEntry.put("AcademicRecordClass-Student/" + activeClass + "/" + subject_AcademicYear_Term + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
                newResultEntry.put("AcademicRecordTeacher-Student/" + auth.getCurrentUser().getUid() + "/" + subject_AcademicYear_Term + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());

                AcademicRecordStudent academicRecordStudent = new AcademicRecordStudent(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, enterResultRowList.get(i).getStudentID(), device, term, year, subject,
                        date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
                        class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType,
                        maximumScore, percentageOfTotal, enterResultRowList.get(i).getScore(), String.valueOf(recordClassAve));

                newResultEntry.put("AcademicRecordStudent/" + enterResultRowList.get(i).getStudentID() + "/" + subject_AcademicYear_Term + "/" + pushID, academicRecordStudent);
                newResultEntry.put("AcademicRecordStudent-Subject/" + enterResultRowList.get(i).getStudentID() + "/" + subject, true);
                String studentID = enterResultRowList.get(i).getStudentID();
                ArrayList<String> parentIDList = studentParentList.get(studentID);
                if (parentIDList != null) {
                    for (int j = 0; j < parentIDList.size(); j++) {
                        String parentID = parentIDList.get(j);

                        if (!parentID.isEmpty()) {
                            String currentDate = Date.getDate();
                            String currentSortableDate = Date.convertToSortableDate(currentDate);
                            NotificationModel notificationModel = new NotificationModel(auth.getCurrentUser().getUid(), parentID, "Parent", sharedPreferencesManager.getActiveAccount(), currentDate, currentSortableDate, pushID, "NewResultPost", enterResultRowList.get(i).getImageURL(), enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getName(), false);
                            newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/status", true);
                            newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/" + subject_AcademicYear_Term + "/status", true);
                            newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/" + subject_AcademicYear_Term + "/" + pushID + "/status", true);
                            newResultEntry.put("AcademicRecordParentRecipients/" + pushID + "/" + parentID, true);
                            newResultEntry.put("NotificationParent/" + parentID + "/" + pushID, notificationModel);
                            newResultEntry.put("Notification Badges/Parents/" + parentID + "/Notifications/status", true);
                            newResultEntry.put("Notification Badges/Parents/" + parentID + "/More/status", true);
                        }
                    }
                }
            }
        }

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(newResultEntry, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    showDialogWithMessage("Results have been posted", true);
                } else {
                    progressDialog.dismiss();
                    String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                    ShowDialogWithMessage.showDialogWithMessage(context, message);
                }
            }
        });
    }

//    public void saveToCloud() {
//        if (CheckNetworkConnectivity.isNetworkAvailable(this)) {
//            final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(EnterResultsActivity.this);
//            progressDialog.show();
//
//            academicYear_Term = year + "_" + term;
//            term_AcademicYear = term + "_" + year;
//            subject_AcademicYear_Term = subject + "_" + year + "_" + term;
//            subject_Term_AcademicYear = subject + "_" + term + "_" + year;
//            class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
//            class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;
//
//            String device = "Android";
//
//            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecord").child("AcademicRecord").push();
//            uniquePushID = mDatabaseReference.getKey();
//
//            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordClass").child(activeClass).child(class_subject_AcademicYear_Term);
//            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    pushID = class_subject_AcademicYear_Term;
//                    mDatabaseReference = mFirebaseDatabase.getReference();
//                    newResultEntry = new HashMap<String, Object>();
//
//                    if ((previousPercentageOfTotal + Double.valueOf(percentageOfTotal)) > 100.0) {
//                        CustomToast.whiteBackgroundBottomToast(EnterResultsActivity.this, "The percentage of total is more than 100");
//                        return;
//                    }
//
//                    if ((previousPercentageOfTotal) >= 100.0) {
//                        CustomToast.whiteBackgroundBottomToast(EnterResultsActivity.this, "The percentage of total is more than 100");
//                        return;
//                    }
//
//                    final AcademicRecord academicRecord = new AcademicRecord(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, device, term, year, subject,
//                            date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
//                            class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType, maximumScore, percentageOfTotal);
//
//                    if (dataSnapshot.exists()) {
//                        existingStudentScores = new Hashtable<String, String>();
//                        DatabaseReference childDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordClass-Student").child(activeClass).child(pushID).child("Students");
//                        childDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                    String key = postSnapshot.getKey();
//                                    String score = postSnapshot.getValue(String.class);
//                                    existingStudentScores.put(key, score);
//                                }
//
//                                double classAverageNonTotal = 0;
//                                double TotalNonTotal = 0;
//                                int counterNonTotal = 0;
//                                for (int i = 0; i < enterResultRowList.size(); i++) {
//                                    if (enterResultRowList.get(i).getStudentID() != null) {
//                                        TotalNonTotal += Double.valueOf(enterResultRowList.get(i).getScore());
//                                        counterNonTotal++;
//                                    }
//                                }
//                                classAverageNonTotal = TotalNonTotal / counterNonTotal;
//
//                                double recordClassAve = 0.0;
//                                int counter = 0;
//                                for (int i = 0; i < enterResultRowList.size(); i++) {
//                                    if (enterResultRowList.get(i).getStudentID() != null) {
//                                        recordClassAve += Double.valueOf(enterResultRowList.get(i).getScore());
//                                        newScore = (Double.valueOf(enterResultRowList.get(i).getScore()) / Double.valueOf(maximumScore)) * Double.valueOf(percentageOfTotal);
//                                        final String studentID = enterResultRowList.get(i).getStudentID();
//                                        if (existingStudentScores.containsKey(studentID)) {
//                                            String oldScore = existingStudentScores.get(studentID);
//                                            Double oldScoreDouble = Double.valueOf(oldScore);
//                                            score = (oldScoreDouble + newScore);
//                                        } else {
//                                            score = newScore;
//                                        }
//                                        classAverage = classAverage + Double.valueOf(score);
//                                        counter++;
//
//                                        newResultEntry.put("AcademicRecordTotal/AcademicRecord-Student/" + activeSchoolID + "/" + pushID + "/Students/" + studentID, String.valueOf(score));
//                                        newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher-Student/" + auth.getCurrentUser().getUid() + "/" + pushID + "/Students/" + studentID, String.valueOf(score));
//                                        newResultEntry.put("AcademicRecordTotal/AcademicRecordClass-Student/" + activeClass + "/" + pushID + "/Students/" + studentID, String.valueOf(score));
//                                        AcademicRecordStudent academicRecordStudent = new AcademicRecordStudent(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, studentID, device, term, year, subject,
//                                                date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
//                                                class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType, maximumScore,
//                                                String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)), String.valueOf(score), String.valueOf(classAverage));
//
//                                        newResultEntry.put("AcademicRecordTotal/AcademicRecordStudent/" + studentID + "/" + pushID, academicRecordStudent);
//
//                                        newResultEntry.put("AcademicRecord/AcademicRecord-Student/" + activeSchoolID + "/" + uniquePushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
//                                        newResultEntry.put("AcademicRecord/AcademicRecordTeacher-Student/" + auth.getCurrentUser().getUid() + "/" + uniquePushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
//                                        newResultEntry.put("AcademicRecord/AcademicRecordClass-Student/" + activeClass + "/" + uniquePushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
//                                        academicRecordStudent = new AcademicRecordStudent(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, enterResultRowList.get(i).getStudentID(), device, term, year, subject,
//                                                date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
//                                                class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType, maximumScore, percentageOfTotal, enterResultRowList.get(i).getScore(), String.valueOf(classAverageNonTotal));
//
//                                        newResultEntry.put("AcademicRecord/AcademicRecordStudent/" + enterResultRowList.get(i).getStudentID() + "/" + uniquePushID, academicRecordStudent);
//
//                                        ArrayList<String> parentIDList = studentParentList.get(studentID);
//                                        if (parentIDList != null) {
//                                            for (int j = 0; j < parentIDList.size(); j++) {
//                                                String parentID = parentIDList.get(j);
//                                                newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/status", true);
//                                                newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/status", true);
//                                                newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_AcademicYear_Term + "/status", true);
//                                                newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_AcademicYear_Term + "/SingleRecords/" + pushID + "/status", true);
//                                                DatabaseReference updateLikeRef = mFirebaseDatabase.getReference("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/count");
//                                                updateLikeRef.runTransaction(new Transaction.Handler() {
//                                                    @Override
//                                                    public Transaction.Result doTransaction(MutableData mutableData) {
//                                                        Integer currentValue = mutableData.getValue(Integer.class);
//                                                        if (currentValue == null) {
//                                                            mutableData.setValue(1);
//                                                        } else {
//                                                            mutableData.setValue(currentValue + 1);
//                                                        }
//
//                                                        return Transaction.success(mutableData);
//
//                                                    }
//
//                                                    @Override
//                                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    }
//                                }
//
//                                recordClassAve = recordClassAve / counter;
//                                academicRecord.setClassAverage(String.valueOf(recordClassAve));
//                                newResultEntry.put("AcademicRecord/AcademicRecord/" + activeSchoolID + "/" + uniquePushID, academicRecord);
//                                newResultEntry.put("AcademicRecord/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + uniquePushID, academicRecord);
//                                newResultEntry.put("AcademicRecord/AcademicRecordClass/" + activeClass + "/" + uniquePushID, academicRecord);
//
//                                classAverage = classAverage / counter;
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID + "/classAverage", String.valueOf(classAverage));
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID + "/maxObtainable", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID + "/percentageOfTotal", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecord/" + activeSchoolID + "/" + pushID + "/classAverage", String.valueOf(classAverage));
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecord/" + activeSchoolID + "/" + pushID + "/maxObtainable", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecord/" + activeSchoolID + "/" + pushID + "/percentageOfTotal", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordClass/" + activeClass + "/" + pushID + "/classAverage", String.valueOf(classAverage));
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordClass/" + activeClass + "/" + pushID + "/maxObtainable", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordClass/" + activeClass + "/" + pushID + "/percentageOfTotal", String.valueOf(previousPercentageOfTotal + Double.valueOf(percentageOfTotal)));
//
//                                for (int i = 0; i < parentList.size(); i++){
//
//                                }
//
//                                mDatabaseReference.updateChildren(newResultEntry, new DatabaseReference.CompletionListener() {
//                                    @Override
//                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                        progressDialog.dismiss();
//                                        showDialogWithMessage("Results have been posted", true);
//                                    }
//
//
//                                });
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    } else {
//
//                        pushID = uniquePushID;
//                        double recordClassAve = 0.0;
//                        double recordTotalScore = 0.0;
//                        int recordCounter = 0;
//                        academicRecord.setMaxObtainable(maximumScore);
//
//                        //Get Class Average
//                        for (int i = 0; i < enterResultRowList.size(); i++) {
//                            if (enterResultRowList.get(i).getStudentID() != null) {
//                                recordTotalScore += Double.valueOf(enterResultRowList.get(i).getScore());
//                                recordCounter++;
//                            }
//                        }
//                        recordClassAve = recordTotalScore / recordCounter;
//
//                        for (int i = 0; i < enterResultRowList.size(); i++) {
//                            if (enterResultRowList.get(i).getStudentID() != null) {
//                                newResultEntry.put("AcademicRecord/AcademicRecord-Student/" + activeSchoolID + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
//                                newResultEntry.put("AcademicRecord/AcademicRecordTeacher-Student/" + auth.getCurrentUser().getUid() + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
//                                newResultEntry.put("AcademicRecord/AcademicRecordClass-Student/" + activeClass + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), enterResultRowList.get(i).getScore());
//                                AcademicRecordStudent academicRecordStudent = new AcademicRecordStudent(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, enterResultRowList.get(i).getStudentID(), device, term, year, subject,
//                                        date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
//                                        class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType,
//                                        maximumScore, percentageOfTotal, enterResultRowList.get(i).getScore(), String.valueOf(recordClassAve));
//
//                                newResultEntry.put("AcademicRecord/AcademicRecordStudent/" + enterResultRowList.get(i).getStudentID() + "/" + pushID, academicRecordStudent);
//
//                                String studentID = enterResultRowList.get(i).getStudentID();
//                                ArrayList<String> parentIDList = studentParentList.get(studentID);
//                                if (parentIDList != null) {
//                                    for (int j = 0; j < parentIDList.size(); j++) {
//                                        String parentID = parentIDList.get(j);
//                                        newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/status", true);
//                                        newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/status", true);
//                                        newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_AcademicYear_Term + "/status", true);
//                                        newResultEntry.put("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/subjects/" + subject + "/Class_Subject_AcademicYear_Term/" + class_subject_AcademicYear_Term + "/SingleRecords/" + pushID + "/status", true);
//                                        DatabaseReference updateLikeRef = mFirebaseDatabase.getReference("AcademicRecordParentNotification/" + parentID + "/" + studentID + "/count");
//                                        updateLikeRef.runTransaction(new Transaction.Handler() {
//                                            @Override
//                                            public Transaction.Result doTransaction(MutableData mutableData) {
//                                                Integer currentValue = mutableData.getValue(Integer.class);
//                                                if (currentValue == null) {
//                                                    mutableData.setValue(1);
//                                                } else {
//                                                    mutableData.setValue(currentValue + 1);
//                                                }
//
//                                                return Transaction.success(mutableData);
//
//                                            }
//
//                                            @Override
//                                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        }
////                            recordClassAve = recordTotalScore / recordCounter;
//                        academicRecord.setClassAverage(String.valueOf(recordClassAve));
//                        newResultEntry.put("AcademicRecord/AcademicRecord/" + activeSchoolID + "/" + pushID, academicRecord);
//                        newResultEntry.put("AcademicRecord/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID, academicRecord);
//                        newResultEntry.put("AcademicRecord/AcademicRecordClass/" + activeClass + "/" + pushID, academicRecord);
//
//                        pushID = class_subject_AcademicYear_Term;
//                        int counter = 0;
//
//                        String totalScore = "0.0";
//                        for (int i = 0; i < enterResultRowList.size(); i++) {
//                            if (enterResultRowList.get(i).getStudentID() != null) {
//                                totalScore = String.valueOf((Double.valueOf(enterResultRowList.get(i).getScore()) / Double.valueOf(maximumScore)) * Double.valueOf(percentageOfTotal));
//                                classAverage = classAverage + Double.valueOf(totalScore);
//                                counter++;
//                            }
//                        }
//                        classAverage = classAverage / counter;
//
//                        for (int i = 0; i < enterResultRowList.size(); i++) {
//                            if (enterResultRowList.get(i).getStudentID() != null) {
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecord-Student/" + activeSchoolID + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), totalScore);
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher-Student/" + auth.getCurrentUser().getUid() + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), totalScore);
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordClass-Student/" + activeClass + "/" + pushID + "/Students/" + enterResultRowList.get(i).getStudentID(), totalScore);
//
//                                AcademicRecordStudent academicRecordStudent = new AcademicRecordStudent(activeClass, auth.getCurrentUser().getUid(), activeSchoolID, enterResultRowList.get(i).getStudentID(), device, term, year, subject,
//                                        date, sortableDate, academicYear_Term, term_AcademicYear, subject_AcademicYear_Term, subject_Term_AcademicYear,
//                                        class_subject_AcademicYear_Term, class_subject_Term_AcademicYear, testType,
//                                        percentageOfTotal, percentageOfTotal, totalScore, String.valueOf(classAverage));
//
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordStudent/" + enterResultRowList.get(i).getStudentID() + "/" + pushID, academicRecordStudent);
//                                newResultEntry.put("AcademicRecordTotal/AcademicRecordStudent-Subject/" + enterResultRowList.get(i).getStudentID() + "/" + subject, true);
//                                //Todo: Confirm that AcademicRecordStudent-Subject writes
//                            }
//                        }
//                        academicRecord.setClassAverage(String.valueOf(classAverage)); //TODO: Change to academicRecordTotal
//                        academicRecord.setMaxObtainable(percentageOfTotal);
//                        newResultEntry.put("AcademicRecordTotal/AcademicRecord/" + activeSchoolID + "/" + pushID, academicRecord);
//                        newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher/" + auth.getCurrentUser().getUid() + "/" + pushID, academicRecord);
//                        newResultEntry.put("AcademicRecordTotal/AcademicRecordClass/" + activeClass + "/" + pushID, academicRecord);
//                        newResultEntry.put("AcademicRecordTotal/AcademicRecordTeacher-Subject/" + auth.getCurrentUser().getUid() + "/" + subject, true);
//
//                        mDatabaseReference.updateChildren(newResultEntry, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                progressDialog.dismiss();
//                                showDialogWithMessage("Results have been posted", true);
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        } else {
//            showDialogWithMessage("Internet is down, check your connection and try again", false);
//        }
//    }

    void showDialogWithMessage (String messageString, final boolean finish) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (finish) {
                    finish();
                }
            }
        });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            year = intent.getStringExtra("Year");
            String month = String.valueOf(intent.getStringExtra("Month"));
            String day = String.valueOf(intent.getStringExtra("Day"));
            term = Term.getTermShort(month);
            date = year + "/" + month + "/" + day + " 12:00:00:00";

            academicYear_Term = year + "_" + term;
            term_AcademicYear = term + "_" + year;
            subject_AcademicYear_Term = subject + "_" + year + "_" + term;
            subject_Term_AcademicYear = subject + "_" + term + "_" + year;
            class_subject_AcademicYear_Term = activeClass + "_" + subject + "_" + year + "_" + term;
            class_subject_Term_AcademicYear = activeClass + "_" + subject + "_" + term + "_" + year;
            updateNewPercentageOfTotal(subject_AcademicYear_Term);
            enterResultHeader.setYear(year);
            enterResultHeader.setMonth(month);
            enterResultHeader.setDay(day);
            enterResultHeader.setDate(date);
            enterResultHeader.setTerm(term);
            enterResultHeader.setSortableDate(Date.convertToSortableDate(date));
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }
}
