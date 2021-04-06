package com.celerii.celerii.Activities.Profiles;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EditYearActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecord;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.PerformanceCurrentModel;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Teacher;
import com.celerii.celerii.models.TeacherAttendanceHeader;
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

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ClassProfileActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    ScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    Toolbar toolbar;
    Button termButton, yearButton;
    ImageView profilePic;
    LinearLayout profilePictureClipper;
    TextView fullName, classTeacher, school, averageAttendance, averagePerformance, boys, girls, seats;

    Bundle bundle;
    String activeClass = "";
    int maleCount, femaleCount, studentCount;
    String year, term, year_term, term_year, subject_term_year;
    ArrayList<Double> normalizedAverageList;
    int attendanceCounter;
    double presentPercentageSummer;

    String featureUseKey = "";
    String featureName = "Class Profile";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_profile);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        superLayout = (ScrollView) findViewById(R.id.superlayout);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });

        bundle = getIntent().getExtras();
        activeClass = bundle.getString("ClassID");

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
                    getSupportActionBar().setTitle("View Attendance Records");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
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
                getSupportActionBar().setTitle("View Attendance Records");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                mySwipeRefreshLayout.setRefreshing(false);
                superLayout.setVisibility(View.GONE);
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
                    getSupportActionBar().setTitle("View Attendance Records");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
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
//        className = activeClassModel.getClassName();

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        termButton = (Button) findViewById(R.id.term);
        yearButton = (Button) findViewById(R.id.year);

        profilePic = (ImageView) findViewById(R.id.profilepic);
        profilePictureClipper = (LinearLayout) findViewById(R.id.profilepictureclipper);

        fullName = (TextView) findViewById(R.id.fullname);
        classTeacher = (TextView) findViewById(R.id.classteacher);
        school = (TextView) findViewById(R.id.school);
        averageAttendance = (TextView) findViewById(R.id.averageattendance);
        averagePerformance = (TextView) findViewById(R.id.averageperformance);
        seats = (TextView) findViewById(R.id.totalnumberofseats);
        boys = (TextView) findViewById(R.id.totalnumberofboys);
        girls = (TextView) findViewById(R.id.totalnumberofgirls);

        term = Term.getTermShort();
        year = Date.getYear();

        termButton.setText(Term.Term(term));
        yearButton.setText(year);

        loadFromFirebase();

        termButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EnterResultsEditTermActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Term", term);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });

        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditYearActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Year", year);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );
    }

    void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            superLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String classTeacherString = "";
                if (dataSnapshot.exists()) {
                    Class classInstance = dataSnapshot.getValue(Class.class);
                    String classNameString = classInstance.getClassName();
                    classTeacherString = classInstance.getClassTeacher();
                    String classPicURL = classInstance.getClassPicURL();
                    fullName.setText(classNameString);
                    classTeacher.setText("Class teacher not assigned yet");

                    Drawable textDrawable;
                    if (!classNameString.isEmpty()) {
                        String[] nameArray = classNameString.replaceAll("\\s+", " ").trim().split(" ");
                        if (nameArray.length == 1) {
                            textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], 150);
                        } else {
                            textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], nameArray[1], 150);
                        }
                        profilePic.setImageDrawable(textDrawable);
                    } else {
                        textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 150);
                    }

                    if (!classPicURL.isEmpty()) {
                        Glide.with(context)
                                .load(classPicURL)
                                .placeholder(textDrawable)
                                .error(textDrawable)
                                .centerCrop()
                                .bitmapTransform(new CropCircleTransformation(context))
                                .into(profilePic);
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("Sorry, this class does not exist.");
                    return;
                }

                mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(classTeacherString);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Teacher teacher = dataSnapshot.getValue(Teacher.class);
                            String teacherName = teacher.getFirstName() + " " +  teacher.getLastName();
                            if (!teacherName.trim().equals("")) {
                                classTeacher.setText(teacherName);
                            } else {
                                classTeacher.setText("Class teacher not assigned yet");
                            }

                        } else {
                            classTeacher.setText("Class teacher not assigned yet");
                        }

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Class School").child(activeClass);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                String schoolID = "";
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        schoolID = postSnapshot.getKey();
                                        break;
                                    }
                                } else {
                                    school.setText("School does not exist");
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            School schoolInstance = dataSnapshot.getValue(School.class);
                                            String schoolName = schoolInstance.getSchoolName();
                                            school.setText(schoolName);
                                        } else {
                                            school.setText("School does not exist");
                                        }

                                        mDatabaseReference = mFirebaseDatabase.getReference("Class Students").child(activeClass);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    maleCount = 0;
                                                    femaleCount = 0;
                                                    studentCount = 0;
                                                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        final String studentKey = postSnapshot.getKey();

                                                        DatabaseReference childReference = mFirebaseDatabase.getReference("Student/" + studentKey);
                                                        childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                Student student = dataSnapshot.getValue(Student.class);
                                                                String gender = student.getGender();
                                                                if (gender.equals("Male")) {
                                                                    maleCount++;
                                                                } else if (gender.equals("Female")) {
                                                                    femaleCount++;
                                                                }
                                                                studentCount++;

                                                                if (studentCount == childrenCount) {
                                                                    String boysString = maleCount + " Boy(s)";
                                                                    String girlsString = femaleCount + " Girl(s)";
                                                                    String seatsString = studentCount + " Seat(s)";
                                                                    boys.setText(boysString);
                                                                    girls.setText(girlsString);
                                                                    seats.setText(seatsString);

                                                                    year_term = year + "_" + term;
                                                                    term_year = term + "_" + year;
                                                                    subject_term_year = "General" + "_" + term_year;

                                                                    attendanceCounter = 0;
                                                                    presentPercentageSummer = 0.0;

                                                                    mDatabaseReference = mFirebaseDatabase.getReference().child("AttendanceClass").child(activeClass);
                                                                    mDatabaseReference.orderByChild("subject_term_year").equalTo(subject_term_year).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            if (dataSnapshot.exists()) {
                                                                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                                                    TeacherAttendanceHeader teacherAttendanceHeader = postSnapshot.getValue(TeacherAttendanceHeader.class);
                                                                                    double present = Double.parseDouble(teacherAttendanceHeader.getPresent());
                                                                                    double absent = Double.parseDouble(teacherAttendanceHeader.getAbsent());
                                                                                    double late = Double.parseDouble(teacherAttendanceHeader.getLate());
                                                                                    double presentPercentage = ((present + late) / (present + absent + late)) * 100;
                                                                                    presentPercentageSummer += presentPercentage;
                                                                                    attendanceCounter += 1;
                                                                                }

                                                                                double average = presentPercentageSummer / attendanceCounter;
                                                                                String averageString = (int) average + "%";
                                                                                averageAttendance.setText(averageString);
                                                                            } else {
                                                                                averageAttendance.setText("0%");
                                                                            }

                                                                            loadAcademicAverage();
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }
                                                } else {
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    superLayout.setVisibility(View.VISIBLE);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    int counter;
    private void loadAcademicAverage() {

        normalizedAverageList = new ArrayList<>();
        counter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordClass").child(activeClass);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String subject_year_term = postSnapshot.getKey();
                        String yearTermKey = subject_year_term.split("_")[1] + "_" + subject_year_term.split("_")[2];

                        if (yearTermKey.equals(year_term)) {
                            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordClass").child(activeClass).child(subject_year_term);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        double classAverage = 0.0;
                                        double maxScore = 0.0;
                                        double totalPercentage = 0.0;

                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            AcademicRecord academicRecord = postSnapshot.getValue(AcademicRecord.class);
                                            double termClassAverage = Double.valueOf(academicRecord.getClassAverage());
                                            double maxObtainable = Double.valueOf(academicRecord.getMaxObtainable());
                                            double percentageOfTotal = Double.valueOf(academicRecord.getPercentageOfTotal());

                                            double normalizedTestClassAverage = (termClassAverage / maxObtainable) * percentageOfTotal;
                                            double normalizedMaxObtainable = (maxObtainable / maxObtainable) * percentageOfTotal;
                                            classAverage += normalizedTestClassAverage;
                                            maxScore += normalizedMaxObtainable;
                                            totalPercentage += percentageOfTotal;
                                        }

                                        Double normalizedAverage = (classAverage / maxScore) * 100;
                                        normalizedAverageList.add(normalizedAverage);
                                    }
                                    counter++;

                                    if (counter == childrenCount) {
                                        double average = 0.0;
                                        double total = 0.0;

                                        if (normalizedAverageList.size() > 0) {
                                            for (Double normAvg: normalizedAverageList) {
                                                total += normAvg;
                                            }
                                            average = total / normalizedAverageList.size();
                                        }

                                        String averageString = (int) average + "%";
                                        averagePerformance.setText(averageString);

                                        mySwipeRefreshLayout.setRefreshing(false);
                                        superLayout.setVisibility(View.VISIBLE);
                                        progressLayout.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            counter++;

                            if (counter == childrenCount) {
                                double average = 0.0;
                                double total = 0.0;

                                if (normalizedAverageList.size() > 0) {
                                    for (Double normAvg: normalizedAverageList) {
                                        total += normAvg;
                                    }
                                    average = total / normalizedAverageList.size();
                                }

                                String averageString = (int) average + "%";
                                averagePerformance.setText(averageString);

                                mySwipeRefreshLayout.setRefreshing(false);
                                superLayout.setVisibility(View.VISIBLE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    averagePerformance.setText("0%");

                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if(resultCode == RESULT_OK) {
                superLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                term = data.getStringExtra("Selected Term");
                termButton.setText(Term.Term(term));
                loadFromFirebase();
            }
        }

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                superLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                year = data.getStringExtra("Selected Year");
                yearButton.setText(year);
                loadFromFirebase();
            }
        }
    }
}
