package com.celerii.celerii.Activities.StudentPerformance.Current;


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
import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.PerformanceCurrentAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.PerformanceCurrentHeader;
import com.celerii.celerii.models.PerformanceCurrentModel;
import com.celerii.celerii.models.School;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentFragment extends Fragment {

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

    private ArrayList<PerformanceCurrentModel> performanceCurrentModelList;
    private PerformanceCurrentHeader performanceCurrentHeader;
    public RecyclerView recyclerView;
    public PerformanceCurrentAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeStudentID = "", year, term, year_term;
    String activeStudent = "";
    String activeStudentName;
    String parentActivity = "";

    String featureUseKey = "";
    String featureName = "Current Academic Results";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public CurrentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current, container, false);

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
        parentActivity = activity.getParentActivity();

        if (activeStudent == null) {
            Gson gson = new Gson();
            ArrayList<Student> myChildren = new ArrayList<>();
            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            Type type = new TypeToken<ArrayList<Student>>() {}.getType();
            myChildren = gson.fromJson(myChildrenJSON, type);

            if (myChildren != null) {
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
                    errorLayoutText.setText("You do not have the permission to view this student's academic record");
                }

                return view;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {}.getType();
                Student activeKidModel = gson.fromJson(activeStudent, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {}.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student: myChildren) {
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
                            errorLayoutText.setText("You do not have the permission to view this student's academic record");
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

        year= Date.getYear();
        term = Term.getTermShort();

        performanceCurrentHeader = new PerformanceCurrentHeader();
        performanceCurrentModelList = new ArrayList<>();
//        subjectList = new ArrayList<>();
        loadNewDetailsFromFirebase();
        mAdapter = new PerformanceCurrentAdapter(performanceCurrentModelList, performanceCurrentHeader, getActivity(), getContext(), activeStudentID, parentActivity);
        recyclerView.setAdapter(mAdapter);

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

    int subjectCounter = 0;
    int counter = 0;
    int isNewCounter = 0;
    double studentScoreTotal = 0.0;
    double classScoreTotal = 0.0;
    double maxScoreTotal = 0.0;
    String classID = "null", schoolID = "null";
    String className = "No result", schoolName = "No result";

    private void loadNewDetailsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        subjectCounter = 0;
        counter = 0;
        isNewCounter = 0;
        studentScoreTotal = 0.0;
        classScoreTotal = 0.0;
        maxScoreTotal = 0.0;
        classID = "null__";
        schoolID = "null__";
        className = "No result";
        schoolName = "No result";

        year_term = year + "_" + term;
        performanceCurrentModelList.clear();
        updateBadges();
        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(activeStudentID);
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
                            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(activeStudentID).child(subject_year_term);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        double termAverage = 0.0;
                                        double caSum = 0.0;
                                        double examSum = 0.0;
                                        double caMax = 0.0;
                                        double examMax = 0.0;
                                        double caAverage = 0.0;
                                        double examAverage = 0.0;
                                        double classAverage = 0.0;
                                        double maxScore = 0.0;
                                        String subject = "";
                                        String latestDate = "0000/00/00 00:00:00:000";

                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                            double testAverage = Double.parseDouble(academicRecordStudent.getScore());
                                            double termClassAverage = Double.parseDouble(academicRecordStudent.getClassAverage());
                                            double maxObtainable = Double.parseDouble(academicRecordStudent.getMaxObtainable());
                                            double percentageOfTotal = Double.parseDouble(academicRecordStudent.getPercentageOfTotal());
                                            String testType = academicRecordStudent.getTestType();
                                            subject = academicRecordStudent.getSubject();
                                            classID = academicRecordStudent.getClassID();
                                            className = academicRecordStudent.getClassName();
                                            schoolID = academicRecordStudent.getSchoolID();

                                            if (Date.compareDates(academicRecordStudent.getDate(), latestDate)) {
                                                latestDate = academicRecordStudent.getDate();
                                            }

                                            double normalizedTestAverage = (testAverage / maxObtainable) * percentageOfTotal;
                                            double normalizedTestClassAverage = (termClassAverage / maxObtainable) * percentageOfTotal;
                                            double normalizedMaxObtainable = (maxObtainable / maxObtainable) * percentageOfTotal;

                                            if (testType.equals("Examination")) {
                                                examSum += normalizedTestAverage;
                                                examMax += normalizedMaxObtainable;
                                            } else {
                                                caSum += normalizedTestAverage;
                                                caMax += normalizedMaxObtainable;
                                            }

                                            termAverage += normalizedTestAverage;
                                            classAverage += normalizedTestClassAverage;
                                            maxScore += normalizedMaxObtainable;
                                        }

                                        examAverage = (examSum / examMax) * 100;
                                        caAverage = (caSum / caMax) * 100;
                                        termAverage = (termAverage / maxScore) * 100;

                                        PerformanceCurrentModel performanceCurrentModel = new PerformanceCurrentModel(subject, latestDate, (int) caAverage, (int) examAverage, (int) termAverage);
                                        performanceCurrentModelList.add(performanceCurrentModel);
                                        studentScoreTotal += termAverage;
                                        classScoreTotal += classAverage;
                                        maxScoreTotal += maxScore;
                                        subjectCounter++;
                                    }
                                    counter++;

                                    if (counter == childrenCount) {
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    School schoolInstance = dataSnapshot.getValue(School.class);
                                                    schoolName = schoolInstance.getSchoolName();
                                                }

                                                mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(classID);
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                                            className = classInstance.getClassName();
                                                        }

                                                        for (final PerformanceCurrentModel performanceCurrentModel: performanceCurrentModelList) {
                                                            String subject_year_term = performanceCurrentModel.getSubject() + "_" + year_term;
                                                            mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(mFirebaseUser.getUid()).child(activeStudentID).child(subject_year_term).child("status");
                                                            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        boolean isNew = dataSnapshot.getValue(boolean.class);
                                                                        if (isNew) {
                                                                            performanceCurrentModel.setNew(true);
                                                                        } else {
                                                                            performanceCurrentModel.setNew(false);
                                                                        }
                                                                    } else {
                                                                        performanceCurrentModel.setNew(false);
                                                                    }

                                                                    isNewCounter++;

                                                                    if (isNewCounter == performanceCurrentModelList.size()) {
                                                                        if (performanceCurrentModelList.size() > 1) {
                                                                            Collections.sort(performanceCurrentModelList, new Comparator<PerformanceCurrentModel>() {
                                                                                @Override
                                                                                public int compare(PerformanceCurrentModel o1, PerformanceCurrentModel o2) {
                                                                                    return o1.getSubject().compareTo(o2.getSubject());
                                                                                }
                                                                            });
                                                                        }

                                                                        int studentScoreAverage = (int) (studentScoreTotal / subjectCounter);
                                                                        int classScoreAverage = (int) (classScoreTotal / subjectCounter);
                                                                        int maxScoreAverage = (int) (maxScoreTotal / subjectCounter);

                                                                        performanceCurrentHeader.setTermAverage(String.valueOf(studentScoreAverage));
                                                                        performanceCurrentHeader.setClassAverage(String.valueOf(classScoreAverage));
                                                                        performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(maxScoreAverage));
                                                                        performanceCurrentHeader.setTerm(term);
                                                                        performanceCurrentHeader.setYear(year);
                                                                        performanceCurrentHeader.setClassName(className);
                                                                        performanceCurrentHeader.setSchool(schoolName);
                                                                        performanceCurrentHeader.setStudent(activeStudentID);
                                                                        if (!performanceCurrentModelList.get(0).getSubject().equals("")) {
                                                                            performanceCurrentModelList.add(0, new PerformanceCurrentModel());
                                                                        }
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
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            counter++;

                            if (counter == childrenCount) {
                                mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            School schoolInstance = dataSnapshot.getValue(School.class);
                                            schoolName = schoolInstance.getSchoolName();
                                        }

                                        if (performanceCurrentModelList.size() > 0) {
                                            for (final PerformanceCurrentModel performanceCurrentModel: performanceCurrentModelList) {
                                                String subject_year_term = performanceCurrentModel.getSubject() + "_" + year_term;
                                                mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(mFirebaseUser.getUid()).child(activeStudentID).child(subject_year_term).child("status");
                                                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            boolean isNew = dataSnapshot.getValue(boolean.class);
                                                            if (isNew) {
                                                                performanceCurrentModel.setNew(true);
                                                            } else {
                                                                performanceCurrentModel.setNew(false);
                                                            }
                                                        } else {
                                                            performanceCurrentModel.setNew(false);
                                                        }

                                                        isNewCounter++;

                                                        if (isNewCounter == performanceCurrentModelList.size()) {
                                                            Collections.sort(performanceCurrentModelList, new Comparator<PerformanceCurrentModel>() {
                                                                @Override
                                                                public int compare(PerformanceCurrentModel o1, PerformanceCurrentModel o2) {
                                                                    return o1.getSubject().compareTo(o2.getSubject());
                                                                }
                                                            });

                                                            int studentScoreAverage = 0;
                                                            int classScoreAverage = 0;
                                                            int maxScoreAverage = 0;

                                                            studentScoreAverage = (int) (studentScoreTotal / subjectCounter);
                                                            classScoreAverage = (int) (classScoreTotal / subjectCounter);
                                                            maxScoreAverage = (int) (maxScoreTotal / subjectCounter);

                                                            performanceCurrentHeader.setTermAverage(String.valueOf(studentScoreAverage));
                                                            performanceCurrentHeader.setClassAverage(String.valueOf(classScoreAverage));
                                                            performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(maxScoreAverage));
                                                            performanceCurrentHeader.setTerm(term);
                                                            performanceCurrentHeader.setYear(year);
                                                            performanceCurrentHeader.setClassName(className);
                                                            performanceCurrentHeader.setSchool(schoolName);
                                                            performanceCurrentHeader.setStudent(activeStudentID);
                                                            if (!performanceCurrentModelList.get(0).getSubject().equals("")) {
                                                                performanceCurrentModelList.add(0, new PerformanceCurrentModel());
                                                            }
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
                                        } else {
                                            performanceCurrentHeader.setTermAverage(String.valueOf(0));
                                            performanceCurrentHeader.setClassAverage(String.valueOf(0));
                                            performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(0));
                                            performanceCurrentHeader.setTerm(term);
                                            performanceCurrentHeader.setYear(year);
                                            performanceCurrentHeader.setClassName("No result");
                                            performanceCurrentHeader.setSchool("No result");
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
                        }
                    }
                } else {
                    performanceCurrentHeader.setTermAverage(String.valueOf(0));
                    performanceCurrentHeader.setClassAverage(String.valueOf(0));
                    performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(0));
                    performanceCurrentHeader.setTerm(term);
                    performanceCurrentHeader.setYear(year);
                    performanceCurrentHeader.setClassName("No result");
                    performanceCurrentHeader.setSchool("No result");
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

//    int counter, j, subIterator = 0;
//    double totalMax = 0, totalMyAverage = 0, totalClassAverage = 0;
//    double averageMax = 0, averageMyAverage = 0, averageClassAverage = 0;
//    int counterMax = 0, counterMyAverage = 0, counterClassAverage = 0;
//    String schoolID, classID;
//    String school = "No result", className = "No result";
//    private void loadDetailsFromFirebase() {
//
//        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
//            mySwipeRefreshLayout.setRefreshing(false);
//            recyclerView.setVisibility(View.GONE);
//            progressLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
//            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
//            return;
//        }
//
//        subjectList.clear();
//        performanceCurrentModelList.clear();
//        counter = 0;
//        totalMax = 0; totalMyAverage = 0; totalClassAverage = 0;
//        averageMax = 0; averageMyAverage = 0; averageClassAverage = 0;
//        counterMax = 0; counterMyAverage = 0; counterClassAverage = 0;
//        school = "No result"; className = "No result";
//        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal").child("AcademicRecordStudent-Subject").child(activeStudentID);
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    performanceCurrentModelList.clear();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        subjectList.add(postSnapshot.getKey());
//                    }
//
//                    for (int i = 0; i < subjectList.size(); i++) {
//                        final String subject = subjectList.get(i);
//                        final String subject_year_term = subject + "_" + year + "_" + term;
//                        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(activeStudentID);
//                        mDatabaseReference.orderByChild("subject_AcademicYear_Term").equalTo(subject_year_term).addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                counter++;
//                                if (dataSnapshot.exists()) {
//                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                        AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
//                                        schoolID = academicRecordStudent.getSchoolID();
//                                        classID = academicRecordStudent.getClassID();
//                                        totalMax += Double.valueOf(academicRecordStudent.getMaxObtainable());
//                                        totalMyAverage += Double.valueOf(academicRecordStudent.getScore());
//                                        totalClassAverage += Double.valueOf(academicRecordStudent.getClassAverage());
//                                        counterMax++; counterMyAverage++; counterClassAverage++;
//                                        PerformanceCurrentModel performanceCurrentModel = new PerformanceCurrentModel(subject, TypeConverterClass.convStringToInt(academicRecordStudent.getScore()));
//                                        performanceCurrentModelList.add(performanceCurrentModel);
//                                    }
//                                }
//
//                                if (counter == subjectList.size()) {
//                                    if (performanceCurrentModelList.size() > 0) {
//                                        averageMax = (totalMax / counterMax) * 1;
//                                        averageMyAverage = totalMyAverage / counterMyAverage;
//                                        averageClassAverage = totalClassAverage / counterClassAverage;
//                                        performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(averageMax));
//                                        performanceCurrentHeader.setTermAverage(String.valueOf(averageMyAverage));
//                                        performanceCurrentHeader.setClassAverage(String.valueOf(averageClassAverage));
//
//
//
//                                        mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
//                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot.exists()) {
//                                                    School schoolInstance = dataSnapshot.getValue(School.class);
//                                                    school = schoolInstance.getSchoolName();
//                                                }
//
//                                                mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(classID);
//                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                                        if (dataSnapshot.exists()) {
//                                                            Class classInstance = dataSnapshot.getValue(Class.class);
//                                                            className = classInstance.getClassName();
//                                                        }
//
//                                                        if (performanceCurrentModelList.size() > 1) {
//                                                            Collections.sort(performanceCurrentModelList, new Comparator<PerformanceCurrentModel>() {
//                                                                @Override
//                                                                public int compare(PerformanceCurrentModel o1, PerformanceCurrentModel o2) {
//                                                                    return o1.getSubject().compareTo(o2.getSubject());
//                                                                }
//                                                            });
//                                                        }
//
//                                                        for (j = 0; j < performanceCurrentModelList.size(); j++){
//                                                            mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(sharedPreferencesManager.getMyUserID()).child(activeStudentID).child("subjects").child(performanceCurrentModelList.get(j).getSubject()).child("status");
//                                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                @Override
//                                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                    if (dataSnapshot.exists()){
//                                                                        boolean status = dataSnapshot.getValue(boolean.class);
//                                                                        if (status) {
//                                                                            performanceCurrentModelList.get(subIterator).setNew(true);
//                                                                        } else {
//                                                                            performanceCurrentModelList.get(subIterator).setNew(false);
//                                                                        }
//                                                                    } else {
//                                                                        performanceCurrentModelList.get(subIterator).setNew(false);
//                                                                    }
//
//                                                                    subIterator++;
//                                                                    if (subIterator == performanceCurrentModelList.size()){
//                                                                        updateBadges();
//                                                                        performanceCurrentHeader.setTerm(term);
//                                                                        performanceCurrentHeader.setYear(year);
//                                                                        performanceCurrentHeader.setClassName(className);
//                                                                        performanceCurrentHeader.setSchool(school);
//                                                                        performanceCurrentHeader.setStudent(activeStudentID);
//                                                                        performanceCurrentModelList.add(0, new PerformanceCurrentModel());
//                                                                        mAdapter.notifyDataSetChanged();
//                                                                        recyclerView.setVisibility(View.VISIBLE);
//                                                                        errorLayout.setVisibility(View.GONE);
//                                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                                        progressLayout.setVisibility(View.GONE);
//                                                                        subIterator = 0;
//                                                                    }
//                                                                }
//
//                                                                @Override
//                                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(DatabaseError databaseError) {
//
//                                                    }
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                        mAdapter.notifyDataSetChanged();
//                                    } else {
//                                        performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(averageMax));
//                                        performanceCurrentHeader.setTermAverage(String.valueOf(averageMyAverage));
//                                        performanceCurrentHeader.setClassAverage(String.valueOf(averageClassAverage));
//                                        performanceCurrentHeader.setTerm(term);
//                                        performanceCurrentHeader.setYear(year);
//                                        performanceCurrentHeader.setClassName(className);
//                                        performanceCurrentHeader.setSchool(school);
//                                        performanceCurrentHeader.setStudent(activeStudentID);
//                                        performanceCurrentModelList.add(0, new PerformanceCurrentModel());
//                                        mAdapter.notifyDataSetChanged();
//                                        recyclerView.setVisibility(View.VISIBLE);
//                                        errorLayout.setVisibility(View.GONE);
//                                        mySwipeRefreshLayout.setRefreshing(false);
//                                        progressLayout.setVisibility(View.GONE);
//                                    }
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
//                    performanceCurrentHeader.setMaxPossibleAverage(String.valueOf(averageMax));
//                    performanceCurrentHeader.setTermAverage(String.valueOf(averageMyAverage));
//                    performanceCurrentHeader.setClassAverage(String.valueOf(averageClassAverage));
//                    performanceCurrentHeader.setTerm(term);
//                    performanceCurrentHeader.setYear(year);
//                    performanceCurrentHeader.setClassName(className);
//                    performanceCurrentHeader.setSchool(school);
//                    performanceCurrentHeader.setStudent(activeStudentID);
//                    performanceCurrentModelList.add(0, new PerformanceCurrentModel());
//                    mAdapter.notifyDataSetChanged();
//                    recyclerView.setVisibility(View.VISIBLE);
//                    errorLayout.setVisibility(View.GONE);
//                    mySwipeRefreshLayout.setRefreshing(false);
//                    progressLayout.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void updateBadges() {
        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
            updateBadgesMap.put("AcademicRecordParentNotification/" + mFirebaseUser.getUid() + "/" + activeStudentID + "/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + activeStudentID + "/More/Status", false);
            mDatabaseReference = mFirebaseDatabase.getReference();
            mDatabaseReference.updateChildren(updateBadgesMap);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if(resultCode == getActivity().RESULT_OK) {
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                term = data.getStringExtra("Selected Term");
                loadNewDetailsFromFirebase();
            }
        }

        if (requestCode == 1) {
            if(resultCode == getActivity().RESULT_OK) {
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                year = data.getStringExtra("Selected Year");
                loadNewDetailsFromFirebase();
            }
        }
    }
}
