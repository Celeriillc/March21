package com.celerii.celerii.Activities.Profiles;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditProfiles.EditStudentProfileActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EditYearActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.Activities.StudentAttendance.ParentAttendanceActivity;
import com.celerii.celerii.Activities.StudentBehaviouralPerformance.BehaviouralResultActivity;
import com.celerii.celerii.Activities.Subscription.SubscriptionHomeActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateDrawable;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.BehaviouralRecordModel;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.DisconnectionModel;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.StudentAcademicHistoryRowModel;
import com.celerii.celerii.models.StudentReview;
import com.celerii.celerii.models.SubscriptionModel;
import com.celerii.celerii.models.Teacher;
import com.celerii.celerii.models.TeacherAttendanceRow;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class StudentProfileActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    TreeMap<Integer, String> subjectScores = new TreeMap<>();
    private ArrayList<String> subjectList, subjectKey;

    ScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;
    LinearLayout attendanceErrorLayout, performanceErrorLayout, behaviouralErrorLayout, bioLayout;

    Toolbar toolbar;
    Button termButton, yearButton;
    Button editStudentProfile, disconnect;

    ImageView kidPic, behaviourPic1, behaviourPic1Background, behaviourPic2, behaviourPic2Background;

    LinearLayout performanceLayoutOne, performanceLayoutTwo, performanceLayoutThree, behaviouralLayoutOne, behaviouralLayoutTwo;

    TextView headerfullname, className, school, gender, punctualityRating, averageAcademicPerformance, behaviouralPoints, temperament;
    TextView status, subscriptionTier, lastSubscription, expiry, bio;
    TextView present, absent, late, attendance1, attendance2, attendance3, attendance4, attendance5, attendance6, attendance7, attendance8, attendance9, attendance10;
    TextView strongest, weakest, subjectOne, scoreOne, subjectTwo, scoreTwo, subjectThree, scoreThree;
    TextView awarded, fined, earned, actionOne, pointOne, actionTwo, pointTwo;
    ImageView viewMoreSubscription, viewMoreAttendance, viewMorePerformance, viewMoreBehaviouralPoints;
    HorizontalScrollView attendanceHorizontalScrollView;
    View bioSeparatorView;
    String term, year, term_year, year_term;
    ArrayList<TextView> attendanceTextViewList = new ArrayList<>();
    int subjectCounter = 0;
    HashMap<String, Double> subjectAverages = new HashMap<>();
    ArrayList<AcademicRecordStudent> academicRecordStudentList = new ArrayList<>();
    ArrayList<TextView> academicSubjectTextViewList = new ArrayList<>();
    ArrayList<TextView> academicScoresTextViewList = new ArrayList<>();
    ArrayList<BehaviouralRecordModel> behaviouralResultRowModelList = new ArrayList<>();
    int totalPointsAwarded, totalPointsFined, totalPointsEarned;
    String dateOne = "", dateTwo, detailOne = "", detailTwo, statusOne = "", statusTwo, classNameOne = "", classNameTwo = "";
    String subjectName = "", class_testtype = "", score = "";

    String studentID = "", studentName = "", studentProfilePicURL = "";
    String activeKid = "";
    Boolean isOpenToAll = false;
    Boolean isExpired = false;

    String featureUseKey = "";
    String featureName = "Student Profile";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        subjectList = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        activeKid = b.getString("childID");
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        superLayout = (ScrollView) findViewById(R.id.superlayout);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ParentSearchActivity.class));
            }
        });

        if (activeKid == null) {
            Gson gson = new Gson();
            ArrayList<Student> myChildren = new ArrayList<>();
            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            Type type = new TypeToken<ArrayList<Student>>() {}.getType();
            myChildren = gson.fromJson(myChildrenJSON, type);

            if (myChildren != null) {
                if (myChildren.size() > 0) {
                    gson = new Gson();
                    activeKid = gson.toJson(myChildren.get(0));
                    sharedPreferencesManager.setActiveKid(activeKid);
                } else {
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle("Profile");
                    getSupportActionBar().setHomeButtonEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                    return;
                }
            } else {
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Profile");
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                mySwipeRefreshLayout.setRefreshing(false);
                superLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                errorLayoutButton.setText("Find my child");
                errorLayoutButton.setVisibility(View.VISIBLE);
                return;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {
                }.getType();
                Student activeKidModel = gson.fromJson(activeKid, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student : myChildren) {
                    if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                        activeKidExist = true;
                        activeKidModel = student;
                        activeKid = gson.toJson(activeKidModel);
                        sharedPreferencesManager.setActiveKid(activeKid);
                        break;
                    }
                }

                if (!activeKidExist) {
                    if (myChildren.size() > 0) {
                        gson = new Gson();
                        activeKid = gson.toJson(myChildren.get(0));
                        sharedPreferencesManager.setActiveKid(activeKid);
                    } else {
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setTitle("Profile");
                        getSupportActionBar().setHomeButtonEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        mySwipeRefreshLayout.setRefreshing(false);
                        superLayout.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                        return;
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeKidModel = gson.fromJson(activeKid, type);
        studentID = activeKidModel.getStudentID();
        studentName = activeKidModel.getFirstName() + " " + activeKidModel.getLastName();

        attendanceErrorLayout = (LinearLayout) findViewById(R.id.attendanceerrorlayout);
        performanceErrorLayout = (LinearLayout) findViewById(R.id.performanceerrorlayout);
        behaviouralErrorLayout = (LinearLayout) findViewById(R.id.behaviouralerrorlayout);
        bioLayout = (LinearLayout) findViewById(R.id.biolayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(studentName);

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        termButton = (Button) findViewById(R.id.term);
        yearButton = (Button) findViewById(R.id.year);

        editStudentProfile = (Button) findViewById(R.id.editstudentprofile);
        disconnect = (Button) findViewById(R.id.disconnect);

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")){
            editStudentProfile.setVisibility(View.VISIBLE);
            disconnect.setVisibility(View.VISIBLE);
        } else {
            editStudentProfile.setVisibility(View.GONE);
            disconnect.setVisibility(View.GONE);
        }

        headerfullname = (TextView) findViewById(R.id.headerfullname);
        className = (TextView) findViewById(R.id.classname);
        school = (TextView) findViewById(R.id.school);
        gender = (TextView) findViewById(R.id.gender);
        punctualityRating = (TextView) findViewById(R.id.punctuality);
        averageAcademicPerformance = (TextView) findViewById(R.id.performance);
        behaviouralPoints = (TextView) findViewById(R.id.behaviouralpoints);
        temperament = (TextView) findViewById(R.id.temperament);

        status = (TextView) findViewById(R.id.status);
        subscriptionTier  = (TextView) findViewById(R.id.subscriptiontier);
        lastSubscription = (TextView) findViewById(R.id.lastsubscription);
        expiry = (TextView) findViewById(R.id.expiry);
        bio = (TextView) findViewById(R.id.bio);

        present = (TextView) findViewById(R.id.present);
        absent = (TextView) findViewById(R.id.absent);
        late = (TextView) findViewById(R.id.late);
        attendance1 = (TextView) findViewById(R.id.attendance1);
        attendance2 = (TextView) findViewById(R.id.attendance2);
        attendance3 = (TextView) findViewById(R.id.attendance3);
        attendance4 = (TextView) findViewById(R.id.attendance4);
        attendance5 = (TextView) findViewById(R.id.attendance5);
        attendance6 = (TextView) findViewById(R.id.attendance6);
        attendance7 = (TextView) findViewById(R.id.attendance7);
        attendance8 = (TextView) findViewById(R.id.attendance8);
        attendance9 = (TextView) findViewById(R.id.attendance9);
        attendance10 = (TextView) findViewById(R.id.attendance10);

        attendanceHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.attendancehorizontalscrollview);

        strongest = (TextView) findViewById(R.id.strongestsubject);
        weakest = (TextView) findViewById(R.id.weakestsubject);
        subjectOne = (TextView) findViewById(R.id.subject1);
        scoreOne = (TextView) findViewById(R.id.score1);
        subjectTwo = (TextView) findViewById(R.id.subject2);
        scoreTwo = (TextView) findViewById(R.id.score2);
        subjectThree = (TextView) findViewById(R.id.subject3);
        scoreThree = (TextView) findViewById(R.id.score3);

        awarded = (TextView) findViewById(R.id.pointsawarded);
        fined = (TextView) findViewById(R.id.pointsfined);
        earned = (TextView) findViewById(R.id.totalpointsearned);
        actionOne = (TextView) findViewById(R.id.action1);
        pointOne = (TextView) findViewById(R.id.point1);
        actionTwo = (TextView) findViewById(R.id.action2);
        pointTwo = (TextView) findViewById(R.id.point2);

        viewMoreSubscription = (ImageView) findViewById(R.id.viewmoresubscription);
        viewMoreAttendance = (ImageView) findViewById(R.id.viewmoreattendance);
        viewMorePerformance = (ImageView) findViewById(R.id.viewmoreperformance);
        viewMoreBehaviouralPoints = (ImageView) findViewById(R.id.viewmorebehaviouralpoints);

        performanceLayoutOne = (LinearLayout) findViewById(R.id.performancelayout1);
        performanceLayoutTwo = (LinearLayout) findViewById(R.id.performancelayout2);
        performanceLayoutThree = (LinearLayout) findViewById(R.id.performancelayout3);
        behaviouralLayoutOne = (LinearLayout) findViewById(R.id.behaviourallayout1);
        behaviouralLayoutTwo = (LinearLayout) findViewById(R.id.behaviourallayout2);

        bioSeparatorView = findViewById(R.id.bioseparator);

//        performanceLayoutOne.setVisibility(View.GONE);
//        performanceLayoutTwo.setVisibility(View.GONE);
//        performanceLayoutThree.setVisibility(View.GONE);
//        behaviouralLayoutOne.setVisibility(View.GONE);
//        behaviouralLayoutTwo.setVisibility(View.GONE);

        kidPic = (ImageView) findViewById(R.id.profilepic);
        behaviourPic1 = (ImageView) findViewById(R.id.behaviourpic1);
        behaviourPic2 = (ImageView) findViewById(R.id.behaviourpic2);
        behaviourPic1Background = (ImageView) findViewById(R.id.behaviourpic1background);
        behaviourPic2Background = (ImageView) findViewById(R.id.behaviourpic2background);

        attendanceTextViewList.add(attendance1);
        attendanceTextViewList.add(attendance2);
        attendanceTextViewList.add(attendance3);
        attendanceTextViewList.add(attendance4);
        attendanceTextViewList.add(attendance5);
        attendanceTextViewList.add(attendance6);
        attendanceTextViewList.add(attendance7);
        attendanceTextViewList.add(attendance8);
        attendanceTextViewList.add(attendance9);
        attendanceTextViewList.add(attendance10);

        academicSubjectTextViewList.add(subjectOne);
        academicSubjectTextViewList.add(subjectTwo);
        academicSubjectTextViewList.add(subjectThree);

        academicScoresTextViewList.add(scoreOne);
        academicScoresTextViewList.add(scoreTwo);
        academicScoresTextViewList.add(scoreThree);

        term = Term.getTermShort();
        year = Date.getYear();

        termButton.setText(Term.Term(term));
        yearButton.setText(year);

        isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
        gson = new Gson();
        String subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationTeachers();
        type = new TypeToken<HashMap<String, SubscriptionModel>>() {}.getType();
        HashMap<String, SubscriptionModel> subscriptionModelMap = gson.fromJson(subscriptionModelJSON, type);
        SubscriptionModel subscriptionModel = new SubscriptionModel();
        if (subscriptionModelMap != null) {
            subscriptionModel = subscriptionModelMap.get(studentID);
            if (subscriptionModel == null) {
                subscriptionModel = new SubscriptionModel();
            }
        }
        if (subscriptionModel.getStudentAccount().equals("")) {
            gson = new Gson();
            subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationParents();
            type = new TypeToken<HashMap<String, ArrayList<SubscriptionModel>>>() {}.getType();
            HashMap<String, ArrayList<SubscriptionModel>> subscriptionModelMapParent = gson.fromJson(subscriptionModelJSON, type);
            subscriptionModel = new SubscriptionModel();
            if (subscriptionModelMapParent != null) {
                ArrayList<SubscriptionModel> subscriptionModelList = subscriptionModelMapParent.get(studentID);
                String latestSubscriptionDate = "0000/00/00 00:00:00:000";
                if (subscriptionModelList != null) {
                    for (SubscriptionModel subscriptionModel1 : subscriptionModelList) {
                        if (Date.compareDates(subscriptionModel1.getExpiryDate(), latestSubscriptionDate)) {
                            subscriptionModel = subscriptionModel1;
                            latestSubscriptionDate = subscriptionModel1.getExpiryDate();
                        }
                    }
                } else {
                    latestSubscriptionDate = "0000/00/00 00:00:00:000";
                }
            }
        }
        isExpired = Date.compareDates(Date.getDate(), subscriptionModel.getExpiryDate());

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

        editStudentProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(StudentProfileActivity.this, EditStudentProfileActivity.class);
                Bundle bundleOne = new Bundle();
                bundleOne.putString("StudentID", studentID);
                I.putExtras(bundleOne);
                startActivity(I);
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });

        viewMoreSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(context, SubscriptionHomeActivity.class);
                Bundle b = new Bundle();
                b.putString("Child ID", activeKid);
                I.putExtras(b);
                context.startActivity(I);
            }
        });

        viewMoreAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(StudentProfileActivity.this, ParentAttendanceActivity.class);
                Bundle b = new Bundle();
                b.putString("Child ID", activeKid);
                I.putExtras(b);
                startActivity(I);
            }
        });

        viewMorePerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(StudentProfileActivity.this, StudentPerformanceForParentsActivity.class);
                Bundle b = new Bundle();
                b.putString("Child ID", activeKid);
                I.putExtras(b);
                startActivity(I);
            }
        });

        viewMoreBehaviouralPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(context, BehaviouralResultActivity.class);
                Bundle b = new Bundle();
                b.putString("ChildID", activeKid);
                I.putExtras(b);
                context.startActivity(I);
            }
        });

//        loadFromFirebase();
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

        term_year = term + "_" + year;
        year_term = year + "_" +  term;
        totalPointsAwarded = totalPointsFined = totalPointsEarned = 0;

        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(studentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final Student student = dataSnapshot.getValue(Student.class);
                    studentName = student.getFirstName() + " " + student.getLastName();
                    studentProfilePicURL = student.getImageURL();
                    getSupportActionBar().setTitle(studentName);
                    editStudentProfile.setText("Edit " + student.getFirstName() + "'s Profile");
                    headerfullname.setText(studentName);

                    String studentGender = student.getGender();
                    if (!studentGender.equals("")) {
                        gender.setText(student.getGender());
                    } else {
                        gender.setText("Gender not set");
                    }

                    String studentBio = student.getBio();
                    if (!studentBio.equals("")){
                        bio.setText(studentBio);
                        bioLayout.setVisibility(View.VISIBLE);
                        bioSeparatorView.setVisibility(View.VISIBLE);
                    } else {
                        bio.setText("Bio hasn't been written");
                        bioLayout.setVisibility(View.GONE);
                        bioSeparatorView.setVisibility(View.GONE);
                    }

                    Drawable textDrawable;
                    if (!studentName.isEmpty()) {
                        String[] nameArray = studentName.replaceAll("\\s+", " ").trim().split(" ");
                        if (nameArray.length == 1) {
                            textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], 150);
                        } else {
                            textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], nameArray[1], 150);
                        }
                        kidPic.setImageDrawable(textDrawable);
                    } else {
                        textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
                    }

                    if (!studentProfilePicURL.isEmpty()) {
                        Glide.with(context)
                                .load(studentProfilePicURL)
                                .placeholder(textDrawable)
                                .error(textDrawable)
                                .centerCrop()
                                .bitmapTransform(new CropCircleTransformation(context))
                                .into(kidPic);
                    }

                    mDatabaseReference = mFirebaseDatabase.getReference("Student Class").child(studentID);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String classKey = postSnapshot.getKey();

                                    mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                Class classInstance = dataSnapshot.getValue(Class.class);
                                                className.setText(classInstance.getClassName());
                                            }
                                            else{
                                                className.setText("Class not found");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            } else {
                                className.setText("Class not set");
                            }

                            mDatabaseReference = mFirebaseDatabase.getReference("Student School").child(studentID);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                            String schoolKey = postSnapshot.getKey();

                                            mDatabaseReference = mFirebaseDatabase.getReference("School").child(schoolKey);
                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){
                                                        School schoolInstance = dataSnapshot.getValue(School.class);
                                                        school.setText(schoolInstance.getSchoolName());
                                                    } else {
                                                        school.setText("School not found");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    } else {
                                        school.setText("School not set");
                                    }
;
                                    mDatabaseReference = mFirebaseDatabase.getReference("AttendanceStudent").child(studentID);
                                    mDatabaseReference.orderByChild("term_year").equalTo(term_year).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Double presentDouble = 0.0;
                                                Double absentDouble = 0.0;
                                                Double total = 0.0;
                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    TeacherAttendanceRow teacherAttendanceRow = postSnapshot.getValue(TeacherAttendanceRow.class);
                                                    if (teacherAttendanceRow.getAttendanceStatus().equals("Present")) {
                                                        presentDouble++;
                                                    } else if (teacherAttendanceRow.getAttendanceStatus().equals("Absent")){
                                                        absentDouble++;
                                                    }
                                                    total++;
                                                }
                                                Double puncRating = (presentDouble / total) * 100;
                                                Double absRating = (absentDouble / total) * 100;
                                                Double lateRating = (100 - (puncRating + absRating));

                                                String presentString = String.valueOf(puncRating.intValue()) + "%";
                                                String absentString = String.valueOf(absRating.intValue()) + "%";
                                                String lateString = String.valueOf(lateRating.intValue()) + "%";

                                                if (isOpenToAll) {
                                                    punctualityRating.setText(presentString);
                                                    present.setText("Present: " + presentString);
                                                    absent.setText("Absent: " + absentString);
                                                    late.setText("Came in Late: " + lateString);
                                                } else {
                                                    if (!isExpired) {
                                                        punctualityRating.setText(presentString);
                                                        present.setText("Present: " + presentString);
                                                        absent.setText("Absent: " + absentString);
                                                        late.setText("Came in Late: " + lateString);
                                                    } else {
                                                        punctualityRating.setText(R.string.not_subscribed_long);
                                                        present.setText("Present: " + context.getResources().getString(R.string.not_subscribed_short));
                                                        absent.setText("Absent: " + context.getResources().getString(R.string.not_subscribed_short));
                                                        late.setText("Came in Late: " + context.getResources().getString(R.string.not_subscribed_short));
                                                    }
                                                }

                                            }
                                            else
                                            {
                                                punctualityRating.setText("0%");
                                                present.setText("0%");
                                                absent.setText("0%");
                                                late.setText("0%");
                                            }

                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            superLayout.setVisibility(View.VISIBLE);

//                                            mDatabaseReference = mFirebaseDatabase.getReference("StudentTemperament").child(studentID);
//                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot.exists()){
//                                                        temperament.setText(dataSnapshot.getValue(String.class));
//                                                    } else {
//                                                        temperament.setText("Temperament not set");
////                                                        mDatabaseReference = mFirebaseDatabase.getReference("PredictedStudentTemperament").child(studentID);
////                                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
////                                                            @Override
////                                                            public void onDataChange(DataSnapshot dataSnapshot) {
////                                                                if (dataSnapshot.exists()){
////                                                                    temperament.setText(dataSnapshot.getValue(String.class));
////                                                                }
////                                                                else {
////                                                                    temperament.setText("Temperament not set");
////                                                                }
////                                                            }
////
////                                                            @Override
////                                                            public void onCancelled(DatabaseError databaseError) {
////
////                                                            }
////                                                        });
//                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
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
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(false);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("This student account has been deleted");
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("Student Subscription").child(studentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String latestDate = "0000/00/00 00:00:00:000";
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        SubscriptionModel subscriptionModel = postSnapshot.getValue(SubscriptionModel.class);
                        String todaysDate = Date.getDate();
                        if (Date.compareDates(subscriptionModel.getExpiryDate(), latestDate)) {
                            if (Date.compareDates(todaysDate, subscriptionModel.getExpiryDate())){
                                status.setText(R.string.not_subscribed_long);
                                subscriptionTier.setText("None");
                            } else {
                                status.setText("Subscribed");
                                subscriptionTier.setText(subscriptionModel.getSubscriptionTier());
                            }
                            lastSubscription.setText(Date.DateFormatMMDDYYYY(subscriptionModel.getSubscriptionDate()));
                            expiry.setText(Date.DateFormatMMDDYYYY(subscriptionModel.getExpiryDate()));
                            latestDate = subscriptionModel.getExpiryDate();
                        }

                    }
                } else {
                    status.setText(R.string.not_subscribed_long);
                    subscriptionTier.setText(R.string.not_subscribed_long);
                    lastSubscription.setText(R.string.not_subscribed_long);
                    expiry.setText(R.string.not_subscribed_long);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("AttendanceStudent").child(studentID);
        mDatabaseReference.orderByChild("term_year").equalTo(term_year).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<TeacherAttendanceRow> teacherAttendanceRowList = new ArrayList<>();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final TeacherAttendanceRow teacherAttendanceRow = postSnapshot.getValue(TeacherAttendanceRow.class);
                        teacherAttendanceRowList.add(teacherAttendanceRow);
                    }
                    Collections.reverse(teacherAttendanceRowList);

                    int counter = 0;
                    for (TeacherAttendanceRow teacherAttendanceRow: teacherAttendanceRowList) {
                        if (isOpenToAll) {
                            attendanceTextViewList.get(counter).setText(Date.getFormalDocumentDate(teacherAttendanceRow.getDate()));
                            attendanceTextViewList.get(counter).setVisibility(View.VISIBLE);
                            if (teacherAttendanceRow.getAttendanceStatus().equals("Present")) {
                                attendanceTextViewList.get(counter).setTextColor(ContextCompat.getColor(context,  R.color.colorPrimaryPurpleOpaque));
                                attendanceTextViewList.get(counter).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple_profile_icon));
                            } else if (teacherAttendanceRow.getAttendanceStatus().equals("Absent")) {
                                attendanceTextViewList.get(counter).setTextColor(ContextCompat.getColor(context,  R.color.colorAccent));
                                attendanceTextViewList.get(counter).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_accent_profile_icon));
                            } else {
                                attendanceTextViewList.get(counter).setTextColor(ContextCompat.getColor(context,  R.color.colorKilogarmOrange));
                                attendanceTextViewList.get(counter).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_kilogarm_yellow_profile_icon));
                            }
                        } else {
                            if (!isExpired) {
                                attendanceTextViewList.get(counter).setText(Date.getFormalDocumentDate(teacherAttendanceRow.getDate()));
                                attendanceTextViewList.get(counter).setVisibility(View.VISIBLE);
                                if (teacherAttendanceRow.getAttendanceStatus().equals("Present")) {
                                    attendanceTextViewList.get(counter).setTextColor(ContextCompat.getColor(context,  R.color.colorPrimaryPurpleOpaque));
                                    attendanceTextViewList.get(counter).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple_profile_icon));
                                } else if (teacherAttendanceRow.getAttendanceStatus().equals("Absent")) {
                                    attendanceTextViewList.get(counter).setTextColor(ContextCompat.getColor(context,  R.color.colorAccent));
                                    attendanceTextViewList.get(counter).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_accent_profile_icon));
                                } else {
                                    attendanceTextViewList.get(counter).setTextColor(ContextCompat.getColor(context,  R.color.colorKilogarmOrange));
                                    attendanceTextViewList.get(counter).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_kilogarm_yellow_profile_icon));
                                }
                            } else {
                                attendanceTextViewList.get(counter).setText(R.string.not_subscribed_long);
                                attendanceTextViewList.get(counter).setVisibility(View.VISIBLE);
                                attendanceTextViewList.get(counter).setTextColor(ContextCompat.getColor(context,  R.color.colorKilogarmOrange));
                                attendanceTextViewList.get(counter).setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_kilogarm_yellow_profile_icon));
                            }
                        }

                        counter++;
                    }
                }
                else {
                    attendanceHorizontalScrollView.setVisibility(View.GONE);
                    attendanceErrorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(studentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectAverages.clear();
                academicRecordStudentList.clear();
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    subjectCounter = 0;
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        final String subject_year_term = postSnapshot.getKey();
                        String yearTermKey = subject_year_term.split("_")[1] + "_" + subject_year_term.split("_")[2];

                        if (yearTermKey.equals(year_term)) {
                            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordStudent").child(studentID).child(subject_year_term);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Double termAverage = 0.0;
                                        String localStudentID = "";

                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                            localStudentID = academicRecordStudent.getStudentID();
                                            double testClassAverage = Double.valueOf(academicRecordStudent.getScore());
                                            double maxObtainable = Double.valueOf(academicRecordStudent.getMaxObtainable());
                                            double percentageOfTotal = Double.valueOf(academicRecordStudent.getPercentageOfTotal());

                                            double normalizedTestClassAverage = (testClassAverage / maxObtainable) * percentageOfTotal;
                                            termAverage += normalizedTestClassAverage;
                                            academicRecordStudentList.add(academicRecordStudent);
                                        }

                                        if (!subjectAverages.containsKey(subject_year_term)) {
                                            subjectAverages.put(subject_year_term, termAverage);
                                        }
                                    }
                                    subjectCounter++;

                                    if (subjectCounter == childrenCount) {
                                        Double totalScores = 0.0;
                                        Double strongestScoreDouble = -1000000000.0;
                                        String strongestScoreSubject = "No Test Yet";
                                        Double weakestScoreDouble = 1000000000.0;
                                        String weakestScoreSubject = "No Test Yet";
                                        for (Map.Entry<String, Double> entry : subjectAverages.entrySet()) {
                                            totalScores += entry.getValue();
                                            if (entry.getValue() > strongestScoreDouble) {
                                                strongestScoreDouble = entry.getValue();
                                                strongestScoreSubject = entry.getKey().split("_")[0];
                                            }
                                            if (entry.getValue() < weakestScoreDouble) {
                                                weakestScoreDouble = entry.getValue();
                                                weakestScoreSubject = entry.getKey().split("_")[0];
                                            }
                                        }

                                        strongest.setText(strongestScoreSubject);
                                        weakest.setText(weakestScoreSubject);

                                        Double averageScore = 0.0;
                                        if (subjectAverages.size() > 0) {
                                            averageScore = totalScores / subjectAverages.size();
                                        }

                                        if (isOpenToAll) {
                                            averageAcademicPerformance.setText(String.valueOf(averageScore.intValue()) + "%");
                                        } else {
                                            if (!isExpired) {
                                                averageAcademicPerformance.setText(String.valueOf(averageScore.intValue()) + "%");
                                            } else {
                                                averageAcademicPerformance.setText(R.string.not_subscribed_long);
                                            }
                                        }

                                        if (academicRecordStudentList.size() == 2) {
                                            performanceLayoutThree.setVisibility(View.GONE);
                                        } else if (academicRecordStudentList.size() == 1) {
                                            performanceLayoutTwo.setVisibility(View.GONE);
                                            performanceLayoutThree.setVisibility(View.GONE);
                                        } else if (academicRecordStudentList.size() == 0) {
                                            performanceLayoutOne.setVisibility(View.GONE);
                                            performanceLayoutTwo.setVisibility(View.GONE);
                                            performanceLayoutThree.setVisibility(View.GONE);
                                        }

                                        if (academicRecordStudentList.size() > 1) {
                                            Collections.sort(academicRecordStudentList, new Comparator<AcademicRecordStudent>() {
                                                @Override
                                                public int compare(AcademicRecordStudent o1, AcademicRecordStudent o2) {
                                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                }
                                            });
                                        }

                                        Collections.reverse(academicRecordStudentList);

                                        int counter = 0;
                                        for (AcademicRecordStudent academicRecordStudent: academicRecordStudentList) {
                                            academicSubjectTextViewList.get(counter).setText(academicRecordStudent.getSubject());
                                            if (isOpenToAll) {
                                                academicScoresTextViewList.get(counter).setText(academicRecordStudent.getScore());
                                            } else {
                                                if (!isExpired) {
                                                    academicScoresTextViewList.get(counter).setText(academicRecordStudent.getScore());
                                                } else {
                                                    academicScoresTextViewList.get(counter).setText(R.string.not_subscribed_short);
                                                }
                                            }

                                            counter++;
                                            if (counter == 3) { break; }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            subjectCounter++;

                            if (subjectCounter == childrenCount) {
                                Double totalScores = 0.0;
                                Double strongestScoreDouble = -1000000000.0;
                                String strongestScoreSubject = "No Test Yet";
                                Double weakestScoreDouble = 1000000000.0;
                                String weakestScoreSubject = "No Test Yet";
                                for (Map.Entry<String, Double> entry : subjectAverages.entrySet()) {
                                    totalScores += entry.getValue();
                                    if (entry.getValue() > strongestScoreDouble) {
                                        strongestScoreDouble = entry.getValue();
                                        strongestScoreSubject = entry.getKey();
                                    }
                                    if (entry.getValue() < weakestScoreDouble) {
                                        weakestScoreDouble = entry.getValue();
                                        weakestScoreSubject = entry.getKey();
                                    }
                                }

                                strongest.setText(strongestScoreSubject);
                                weakest.setText(weakestScoreSubject);

                                Double averageScore = 0.0;
                                if (subjectAverages.size() > 0) {
                                    averageScore = totalScores / subjectAverages.size();
                                }

                                if (isOpenToAll) {
                                    averageAcademicPerformance.setText(String.valueOf(averageScore.intValue()) + "%");
                                } else {
                                    if (!isExpired) {
                                        averageAcademicPerformance.setText(String.valueOf(averageScore.intValue()) + "%");
                                    } else {
                                        averageAcademicPerformance.setText(R.string.not_subscribed_long);
                                    }
                                }

                                if (academicRecordStudentList.size() == 2) {
                                    performanceLayoutThree.setVisibility(View.GONE);
                                } else if (academicRecordStudentList.size() == 1) {
                                    performanceLayoutTwo.setVisibility(View.GONE);
                                    performanceLayoutThree.setVisibility(View.GONE);
                                } else if (academicRecordStudentList.size() == 0) {
                                    performanceLayoutOne.setVisibility(View.GONE);
                                    performanceLayoutTwo.setVisibility(View.GONE);
                                    performanceLayoutThree.setVisibility(View.GONE);
                                }

                                int counter = 0;
                                for (AcademicRecordStudent academicRecordStudent: academicRecordStudentList) {
                                    academicSubjectTextViewList.get(counter).setText(academicRecordStudent.getSubject());
                                    if (isOpenToAll) {
                                        academicScoresTextViewList.get(counter).setText(academicRecordStudent.getScore());
                                    } else {
                                        if (!isExpired) {
                                            academicScoresTextViewList.get(counter).setText(academicRecordStudent.getScore());
                                        } else {
                                            academicScoresTextViewList.get(counter).setText(R.string.not_subscribed_short);
                                        }
                                    }

                                    counter++;
                                    if (counter == 3) { break; }
                                }
                            }
                        }
                    }
                } else {
                    averageAcademicPerformance.setText("No Test Yet");
                    strongest.setText("No Test Yet");
                    weakest.setText("No Test Yet");
                    performanceLayoutOne.setVisibility(View.GONE);
                    performanceLayoutTwo.setVisibility(View.GONE);
                    performanceLayoutThree.setVisibility(View.GONE);
                    performanceErrorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(studentID).child("Reward");
        mDatabaseReference.orderByChild("term_AcademicYear").equalTo(term_year).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                behaviouralResultRowModelList.clear();
                if (dataSnapshot.exists()) {
                    totalPointsAwarded = (int) dataSnapshot.getChildrenCount();
                    awarded.setText(Integer.toString(totalPointsEarned));
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        BehaviouralRecordModel behaviouralRecordModel = postSnapshot.getValue(BehaviouralRecordModel.class);
                        behaviouralResultRowModelList.add(behaviouralRecordModel);
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordStudent").child(studentID).child("Punishment");
                mDatabaseReference.orderByChild("term_AcademicYear").equalTo(term_year).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            totalPointsFined = (int) dataSnapshot.getChildrenCount();
                            fined.setText(Integer.toString(totalPointsFined));
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                BehaviouralRecordModel behaviouralRecordModel = postSnapshot.getValue(BehaviouralRecordModel.class);
                                behaviouralResultRowModelList.add(behaviouralRecordModel);
                            }
                        }

                        int totalPointsEarned = totalPointsAwarded - totalPointsFined;
                        awarded.setText(Integer.toString(totalPointsAwarded));
                        fined.setText(Integer.toString(totalPointsFined));
                        earned.setText(Integer.toString(totalPointsEarned));
                        behaviouralPoints.setText(Integer.toString(totalPointsEarned) + " Points");

                        if (behaviouralResultRowModelList.size() > 1) {
                            Collections.sort(behaviouralResultRowModelList, new Comparator<BehaviouralRecordModel>() {
                                @Override
                                public int compare(BehaviouralRecordModel o1, BehaviouralRecordModel o2) {
                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                }
                            });

                            Collections.reverse(behaviouralResultRowModelList);

                            BehaviouralRecordModel recordModel1 = behaviouralResultRowModelList.get(0);
                            actionOne.setText(recordModel1.getRewardDescription());
                            pointOne.setText(recordModel1.getPoint());
                            if (recordModel1.getRewardType().equals("Reward")) {
                                behaviourPic1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_plus_one));
                                behaviourPic1Background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple_profile_icon));
                            } else {
                                behaviourPic1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_minus_one));
                                behaviourPic1Background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_accent_profile_icon));
                            }

                            BehaviouralRecordModel recordModel2 = behaviouralResultRowModelList.get(1);
                            actionTwo.setText(recordModel2.getRewardDescription());
                            pointTwo.setText(recordModel2.getPoint());
                            if (recordModel2.getRewardType().equals("Reward")) {
                                behaviourPic2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_plus_one));
                                behaviourPic2Background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple_profile_icon));
                            } else {
                                behaviourPic2.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_minus_one));
                                behaviourPic2Background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_accent_profile_icon));
                            }
                        } else if (behaviouralResultRowModelList.size() == 1) {
                            behaviouralLayoutTwo.setVisibility(View.GONE);
                            BehaviouralRecordModel recordModel1 = behaviouralResultRowModelList.get(0);
                            actionOne.setText(recordModel1.getRewardDescription());
                            pointOne.setText(recordModel1.getPoint());
                            if (recordModel1.getRewardType().equals("Reward")) {
                                behaviourPic1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_plus_one));
                                behaviourPic1Background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple_profile_icon));
                            } else {
                                behaviourPic1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_minus_one));
                                behaviourPic1Background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_accent_profile_icon));
                            }
                        } else {
                            behaviouralLayoutOne.setVisibility(View.GONE);
                            behaviouralLayoutTwo.setVisibility(View.GONE);
                            behaviouralErrorLayout.setVisibility(View.VISIBLE);
                        }
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

    private void disconnect() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_request_connection);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        Button action = (Button) dialog.findViewById(R.id.action);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        String messageString = "Disconnecting would restrict your access to " + "<b>" + studentName + "</b>" + "'s information, including class stories and " +
                "attendance information. To regain access, you'll need to send a new request to their school. Do you wish to disconnect?";
        message.setText(Html.fromHtml(messageString));

        cancel.setText("Cancel");
        action.setText("Disconnect");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                final HashMap<String, ArrayList<String>> guardians = new HashMap<>();
                final CustomProgressDialogOne customProgressDialogOne = new CustomProgressDialogOne(context);
                customProgressDialogOne.show();
//                holder.sendRequest.setEnabled(false);

                final String timeSent = Date.getDate();
                final String sorttableTimeSent = Date.convertToSortableDate(timeSent);

                final Map<String, Object> newDisconnectionMap = new HashMap<String, Object>();
                DatabaseReference newDisconnectionRef = mFirebaseDatabase.getReference().child("Disconnection Subject").child(mFirebaseUser.getUid()).push();
                final String disconnectionRefKey = newDisconnectionRef.getKey();
                DisconnectionModel disconnectionModel = new DisconnectionModel(mFirebaseUser.getUid(), studentID, disconnectionRefKey, timeSent, sorttableTimeSent);

                newDisconnectionMap.put("Parents Students/" + mFirebaseUser.getUid() + "/" + studentID, null);
                newDisconnectionMap.put("Student Parent/" + studentID + "/" + mFirebaseUser.getUid(), null);
                newDisconnectionMap.put("Disconnection Subject/" + mFirebaseUser.getUid() + "/" + disconnectionRefKey, disconnectionModel);
                newDisconnectionMap.put("Disconnection Object/" + studentID + "/" + disconnectionRefKey, disconnectionModel);

                mDatabaseReference = mFirebaseDatabase.getReference("Student Parent").child(studentID);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if (!guardians.containsKey(studentID)) {
                                    guardians.put(studentID, new ArrayList<String>());
                                }
                                guardians.get(studentID).add(postSnapshot.getKey() + " Parent");
                            }
                        }

                        mDatabaseReference = mFirebaseDatabase.getReference("Student School").child(studentID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (!guardians.containsKey(studentID)) {
                                            guardians.put(studentID, new ArrayList<String>());
                                        }
                                        guardians.get(studentID).add(postSnapshot.getKey() + " School");
                                    }
                                }

                                if (guardians.get(studentID) != null) {
                                    if (guardians.get(studentID).size() != 0) {
                                        for (int i = 0; i < guardians.get(studentID).size(); i++) {
                                            String recipientID = guardians.get(studentID).get(i).split(" ")[0];
                                            String recipientAccountType = guardians.get(studentID).get(i).split(" ")[1];

                                            if (!recipientID.equals(mFirebaseUser.getUid())) {
                                                NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), recipientID, recipientAccountType, "Parent", timeSent, sorttableTimeSent, disconnectionRefKey, "Disconnection", studentProfilePicURL, studentID, false);

                                                if (recipientAccountType.equals("School")) {
                                                    newDisconnectionMap.put("NotificationSchool/" + recipientID + "/" + disconnectionRefKey, notificationModel);
                                                } else if (recipientAccountType.equals("Parent")) {
                                                    newDisconnectionMap.put("NotificationParent/" + recipientID + "/" + disconnectionRefKey, notificationModel);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    //Todo: lost student account
                                }

                                DatabaseReference newDisconnectionRef = mFirebaseDatabase.getReference();
                                newDisconnectionRef.updateChildren(newDisconnectionMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error == null) {
                                            customProgressDialogOne.dismiss();
                                            sharedPreferencesManager.deleteActiveKid();
                                            String message = "You've been successfully disconnected from " + "<b>" + studentName + "</b>" + "'s account. You will no longer have access to or receive notifications from their account. To reconnect, use the search button to send a fresh connection request";
                                            showDialogWithMessageAndClose(Html.fromHtml(message));
                                        }
                                    }
                                });
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
        });

    }

    void showDialogWithMessage (Spanned messageString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
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
            }
        });
    }

    void showDialogWithMessageAndClose (Spanned messageString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
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
                finish();
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
    public void onResume() {
        if (activeKid != null) {
            loadFromFirebase();
        }
        UpdateDataFromFirebase.populateEssentials(this);
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if(resultCode == RESULT_OK) {
                superLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                term = data.getStringExtra("Selected Term");
                termButton.setText(term);
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
