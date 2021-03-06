package com.celerii.celerii.Activities.StudentPerformance.History;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.StudentAcademicHistoryAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentAcademicHistoryHeaderModel;
import com.celerii.celerii.models.StudentAcademicHistoryRowModel;
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
import java.util.Map;

public class StudentAcademicHistoryActivity extends AppCompatActivity {

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

    Toolbar toolbar;
    private ArrayList<StudentAcademicHistoryRowModel> studentAcademicHistoryRowModelList;
    StudentAcademicHistoryHeaderModel studentAcademicHistoryHeaderModel;
    public RecyclerView recyclerView;
    public StudentAcademicHistoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String activeClass = "", className = "";
    String year, term, year_term;

    String featureUseKey = "";
    String featureName = "Class Term Average";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_academic_history);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(this);

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
                    getSupportActionBar().setTitle("Class Academic Records"); //TODO: Use class name, make dynamic
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
                getSupportActionBar().setTitle("Class Academic Records"); //TODO: Use class name, make dynamic
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
                    getSupportActionBar().setTitle("Class Academic Records"); //TODO: Use class name, make dynamic
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
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Class>() {}.getType();
        Class activeClassModel = gson.fromJson(activeClass, type);
        activeClass = activeClassModel.getID();
        className = activeClassModel.getClassName();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(className + "'s Term Average"); //TODO: Use class name, make dynamic
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        errorLayout.setVisibility(View.GONE);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        year = Date.getYear();
        term = Term.getTermShort();
        studentAcademicHistoryHeaderModel = new StudentAcademicHistoryHeaderModel(term, year, className);

        studentAcademicHistoryRowModelList = new ArrayList<>();
        mAdapter = new StudentAcademicHistoryAdapter(studentAcademicHistoryRowModelList, studentAcademicHistoryHeaderModel, this,this);
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
    }

    HashMap<String, Double> studentScore = new HashMap<>();
    HashMap<String, Integer> studentCounter = new HashMap<>();
    int subjectCounter = 0;

    private void loadNewDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        year_term = year + "_" + term;

        Gson gson = new Gson();
        HashMap<String, HashMap<String, Student>> classStudentsForTeacherMap = new HashMap<String, HashMap<String, Student>>();
        String classStudentsForTeacherJSON = sharedPreferencesManager.getClassStudentForTeacher();
        Type type = new TypeToken<HashMap<String, HashMap<String, Student>>>() {}.getType();
        classStudentsForTeacherMap = gson.fromJson(classStudentsForTeacherJSON, type);

        if (classStudentsForTeacherMap == null || classStudentsForTeacherMap.size() == 0) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText(Html.fromHtml(className + " doesn't contain any students. You can change the active class to another with students in the " + "<b>" + "More" + "</b>" + " area"));
        } else {
            final HashMap<String, Student> classMap = classStudentsForTeacherMap.get(activeClass);

            if (classMap == null) {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(Html.fromHtml(className + " doesn't contain any students. You can change the active class to another with students in the " + "<b>" + "More" + "</b>" + " area"));
                return;
            }

            if (classMap.size() == 0) {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(Html.fromHtml(className + " doesn't contain any students. You can change the active class to another with students in the " + "<b>" + "More" + "</b>" + " area"));
                return;
            }

            studentScore = new HashMap<>();
            studentCounter = new HashMap<>();
            studentAcademicHistoryRowModelList.clear();
            mAdapter.notifyDataSetChanged();

            for (final Map.Entry<String, Student> entry : classMap.entrySet()) {
                final String studentID = entry.getKey();
                Student studentModel = entry.getValue();

                final StudentAcademicHistoryRowModel studentAcademicHistoryRowModel = new StudentAcademicHistoryRowModel();
                studentAcademicHistoryRowModel.setImageURL(studentModel.getImageURL());
                studentAcademicHistoryRowModel.setName(studentModel.getFirstName() + " " + studentModel.getLastName());
                studentAcademicHistoryRowModel.setStudentID(studentID);
                studentAcademicHistoryRowModelList.add(studentAcademicHistoryRowModel);
//                studentScore.put(studentID, 0.0);
//                studentCounter.put(studentID, 0);

                mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(studentID);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final int childrenCount = (int) dataSnapshot.getChildrenCount();
                            subjectCounter = 0;
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                                String subject_year_term = postSnapshot.getKey();
                                String yearTermKey = subject_year_term.split("_")[1] + "_" + subject_year_term.split("_")[2];

                                if (yearTermKey.equals(year_term)) {
                                    mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(entry.getKey()).child(subject_year_term);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Double termAverage = 0.0;
                                                Double maxScore = 0.0;
                                                String localStudentID = "";

                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                                    localStudentID = academicRecordStudent.getStudentID();
                                                    double testClassAverage = Double.valueOf(academicRecordStudent.getScore());
                                                    double maxObtainable = Double.valueOf(academicRecordStudent.getMaxObtainable());
                                                    double percentageOfTotal = Double.valueOf(academicRecordStudent.getPercentageOfTotal());

                                                    double normalizedTestClassAverage = (testClassAverage / maxObtainable) * percentageOfTotal;
                                                    double normalizedMaxObtainable = (maxObtainable / maxObtainable) * percentageOfTotal;
                                                    termAverage += normalizedTestClassAverage;
                                                    maxScore += normalizedMaxObtainable;
                                                }

                                                termAverage = (termAverage / maxScore) * 100;
                                                if (!studentScore.containsKey(localStudentID)) {
                                                    studentScore.put(localStudentID, termAverage);
                                                } else {
                                                    studentScore.put(localStudentID, studentScore.get(localStudentID) + termAverage);
                                                }

                                                if (!studentCounter.containsKey(localStudentID)) {
                                                    studentCounter.put(localStudentID, 1);
                                                } else {
                                                    studentCounter.put(localStudentID, studentCounter.get(localStudentID) + 1);
                                                }
                                            }
                                            subjectCounter++;

                                            if (subjectCounter == childrenCount) {
                                                subjectCounter = 0;
                                            }

                                            if (studentCounter.size() == classMap.size()) {
                                                for (StudentAcademicHistoryRowModel studentAcademicHistoryRowModel: studentAcademicHistoryRowModelList) {
                                                    String key = studentAcademicHistoryRowModel.getStudentID();
                                                    try {
                                                        double score = (int) ((studentScore.get(key) / studentCounter.get(key)));
                                                        studentAcademicHistoryRowModel.setAverage(String.valueOf(score));
                                                    } catch (Exception e) {
                                                        studentAcademicHistoryRowModel.setAverage(String.valueOf(0.0));
                                                    }
                                                }

                                                if (!studentAcademicHistoryRowModelList.get(0).getStudentID().trim().isEmpty()) {
                                                    studentAcademicHistoryRowModelList.add(0, new StudentAcademicHistoryRowModel());
                                                }
                                                mAdapter.notifyDataSetChanged();
                                                mySwipeRefreshLayout.setRefreshing(false);
                                                progressLayout.setVisibility(View.GONE);
                                                errorLayout.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    subjectCounter++;

                                    if (subjectCounter == childrenCount) {
                                        if (!studentScore.containsKey(studentID)) {
                                            studentScore.put(studentID, 0.0);
                                        }

                                        if (!studentCounter.containsKey(studentID)) {
                                            studentCounter.put(studentID, 1);
                                        }
                                    }

                                    if (studentCounter.size() == classMap.size()) {
                                        for (StudentAcademicHistoryRowModel studentAcademicHistoryRowModel: studentAcademicHistoryRowModelList) {
                                            String key = studentAcademicHistoryRowModel.getStudentID();
                                            try {
                                                double score = (int) ((studentScore.get(key) / studentCounter.get(key)));
                                                studentAcademicHistoryRowModel.setAverage(String.valueOf(score));
                                            } catch (Exception e) {
                                                studentAcademicHistoryRowModel.setAverage(String.valueOf(0.0));
                                            }
                                        }

                                        if (!studentAcademicHistoryRowModelList.get(0).getStudentID().trim().isEmpty()) {
                                            studentAcademicHistoryRowModelList.add(0, new StudentAcademicHistoryRowModel());
                                        }
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        progressLayout.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        } else {
                            studentScore.put(studentID, 0.0);
                            studentCounter.put(studentID, 1);

                            if (studentCounter.size() == classMap.size()) {
                                for (StudentAcademicHistoryRowModel studentAcademicHistoryRowModel: studentAcademicHistoryRowModelList) {
                                    String key = studentAcademicHistoryRowModel.getStudentID();
                                    try {
                                        double score = (int) ((studentScore.get(key) / studentCounter.get(key)));
                                        studentAcademicHistoryRowModel.setAverage(String.valueOf(score));
                                    } catch (Exception e) {
                                        studentAcademicHistoryRowModel.setAverage(String.valueOf(0.0));
                                    }
                                }

                                if (!studentAcademicHistoryRowModelList.get(0).getStudentID().trim().isEmpty()) {
                                    studentAcademicHistoryRowModelList.add(0, new StudentAcademicHistoryRowModel());
                                }
                                mAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

//    int studentCounter = 0;
//    private void loadNeDetailsFromFirebase() {
//        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
//            mySwipeRefreshLayout.setRefreshing(false);
//            recyclerView.setVisibility(View.GONE);
//            progressLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
//            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
//            return;
//        }
//
//        year = Date.getYear();
//        term = Term.getTermShort();
//        year_term = year + "_" + term;
//
//        Gson gson = new Gson();
//        HashMap<String, HashMap<String, Student>> classStudentsForTeacherMap = new HashMap<String, HashMap<String, Student>>();
//        String classStudentsForTeacherJSON = sharedPreferencesManager.getClassStudentForTeacher();
//        Type type = new TypeToken<HashMap<String, HashMap<String, Student>>>() {}.getType();
//        classStudentsForTeacherMap = gson.fromJson(classStudentsForTeacherJSON, type);
//
//        if (classStudentsForTeacherMap == null || classStudentsForTeacherMap.size() == 0) {
//            mySwipeRefreshLayout.setRefreshing(false);
//            recyclerView.setVisibility(View.GONE);
//            progressLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
//            errorLayoutText.setText("This class doesn't contain any students");
//        } else {
//            HashMap<String, Student> classMap = classStudentsForTeacherMap.get(activeClass);
//            final int studentCount = classMap.size();
//            for (Map.Entry<String, Student> entry : classMap.entrySet()) {
//                final String studentID = entry.getKey();
//                Student studentModel = entry.getValue();
//
//                final StudentAcademicHistoryRowModel studentAcademicHistoryRowModel = new StudentAcademicHistoryRowModel();
//                studentAcademicHistoryRowModel.setImageURL(studentModel.getImageURL());
//                studentAcademicHistoryRowModel.setName(studentModel.getFirstName() + " " + studentModel.getLastName());
//                studentAcademicHistoryRowModel.setStudentID(studentID);
//
//                mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent-Subject").child(studentID);
//                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            final int numberOfSubjects = (int) dataSnapshot.getChildrenCount();
//                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                                String term = Term.getTermShort();
//                                String year = Date.getYear();
//                                String subject_year_term = postSnapshot.getKey() + "_" + year + "_" + term;
//
//                                mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(studentID).child(subject_year_term);
//                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        studentCounter++;
//                                        studentScoreCount++;
//                                        if (dataSnapshot.exists()) {
//                                            double termScore = 0.0;
//                                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                                                AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
//                                                double testScore = Double.valueOf(academicRecordStudent.getClassAverage());
//                                                double maxObtainable = Double.valueOf(academicRecordStudent.getMaxObtainable());
//                                                double percentageOfTotal = Double.valueOf(academicRecordStudent.getPercentageOfTotal());
//                                                double normalizedTestClassAverage = (testScore / maxObtainable) * percentageOfTotal;
//                                                termScore += normalizedTestClassAverage;
//                                            }
//                                            studentTotalScore += termScore;
//
//                                            if (numberOfSubjects == studentScoreCount) {
//                                                studentAverageScore = studentTotalScore / studentScoreCount;
//                                                studentAcademicHistoryRowModel.setAverage(String.valueOf(studentAverageScore));
//                                                studentAcademicHistoryRowModelList.add(studentAcademicHistoryRowModel);
//
//                                                if (studentCount == studentCounter) {
//                                                    mySwipeRefreshLayout.setRefreshing(false);
//                                                    progressLayout.setVisibility(View.GONE);
//                                                    errorLayout.setVisibility(View.GONE);
//                                                    recyclerView.setVisibility(View.VISIBLE);
//                                                }
//                                            }
//                                        } else {
//                                            if (studentCount == studentCounter) {
//                                                mySwipeRefreshLayout.setRefreshing(false);
//                                                progressLayout.setVisibility(View.GONE);
//                                                errorLayout.setVisibility(View.GONE);
//                                                recyclerView.setVisibility(View.VISIBLE);
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        } else {
//                            studentCounter++;
//                            studentAcademicHistoryRowModel.setAverage(String.valueOf(0.0));
//                            studentAcademicHistoryRowModelList.add(studentAcademicHistoryRowModel);
//
//                            if (studentCount == studentCounter) {
//                                mySwipeRefreshLayout.setRefreshing(false);
//                                progressLayout.setVisibility(View.GONE);
//                                errorLayout.setVisibility(View.GONE);
//                                recyclerView.setVisibility(View.VISIBLE);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        }
//    }

    private void loadDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference("Class Students/" + activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    studentAcademicHistoryRowModelList.clear();
                    mAdapter.notifyDataSetChanged();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final String childKey = postSnapshot.getKey();
                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Student child = dataSnapshot.getValue(Student.class);
                                final StudentAcademicHistoryRowModel studentAcademicHistoryRowModel = new StudentAcademicHistoryRowModel();
                                studentAcademicHistoryRowModel.setImageURL(child.getImageURL());
                                studentAcademicHistoryRowModel.setName(child.getFirstName() + " " + child.getLastName());
                                studentAcademicHistoryRowModel.setStudentID(childKey);

                                final String year = Date.getYear();

                                mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(childKey);
                                mDatabaseReference.orderByChild("academicYear").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        double score = 0;
                                        if (dataSnapshot.exists()) {
                                            double summer = 0;
                                            double counter = 0;
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                                summer = summer + Double.valueOf(academicRecordStudent.getScore());
                                                counter++;
                                            }
                                            score = (summer / counter);
                                        }
                                        studentAcademicHistoryRowModel.setAverage(String.valueOf(score));
                                        studentAcademicHistoryRowModelList.add(studentAcademicHistoryRowModel);
                                        mAdapter.notifyDataSetChanged();

                                        if (childrenCount == studentAcademicHistoryRowModelList.size()) {
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);
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
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if(resultCode == RESULT_OK) {
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                term = data.getStringExtra("Selected Term");
                studentAcademicHistoryHeaderModel.setTerm(term);
                mAdapter.notifyDataSetChanged();
                loadNewDetailsFromFirebase();
            }
        }

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                year = data.getStringExtra("Selected Year");
                studentAcademicHistoryHeaderModel.setYear(year);
                mAdapter.notifyDataSetChanged();
                loadNewDetailsFromFirebase();
            }
        }
    }
}
