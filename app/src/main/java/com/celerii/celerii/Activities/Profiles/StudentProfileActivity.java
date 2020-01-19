package com.celerii.celerii.Activities.Profiles;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditProfiles.EditStudentProfileActivity;
import com.celerii.celerii.Activities.StudentAttendance.ParentAttendanceActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.helperClasses.CreateDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.AcademicRecordStudent;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.StudentReview;
import com.celerii.celerii.models.SubscriptionModel;
import com.celerii.celerii.models.Teacher;
import com.celerii.celerii.models.TeacherAttendanceRow;
import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class StudentProfileActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    TreeMap<Integer, String> subjectScores = new TreeMap<>();
    private ArrayList<String> subjectList, subjectKey;

    NestedScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, attendanceErrorLayout, performanceErrorLayout, behaviouralErrorLayout, reviewErrorLayout, progressLayout;

    Toolbar toolbar;
    LinearLayout editStudentProfile;
    TextView editStudentProfileText;

    ImageView kidPic, kidPicBackground, attendancePic1, attendancePic2, attendanceMarker1, attendanceMarker2,
            subjectPic1, subjectPic2, behaviourPic1, behaviourPic2, reviewsPic1, reviewsPic2;

    LinearLayout attendanceLayoutOne, attendanceLayoutTwo, performanceLayoutOne, performanceLayoutTwo, behaviouralLayoutOne, behaviouralLayoutTwo, reviewLayoutOne, reviewLayoutTwo;
    LinearLayout fullNameLayout, classLayout, schoolLayout, genderLayout, punctualityRatingLayout, averagePerformanceLayout, bPointsLayout, temperamentLayout;

    TextView headerfullname, name, className, school, gender, punctualityRating, averageAcademicPerformance, behaviouralPoints, temperament;
    TextView status, subscriptionTier, lastSubscription, expiry, bio;
    TextView attendanceDateOne, attendanceDetailOne, attendanceDateTwo, attendanceDetailTwo;
    TextView overallStrongest, overallWeakest, subjectOne, classOne, scoreOne, subjectTwo, classTwo, scoreTwo;
    TextView awarded, fined, earned, actionOne, classOneSocial, pointOne, actionTwo, classTwoSocial, pointTwo;
    TextView possibleCareerChoice;
    TextView reviewOne, reviewerOne, reviewTwo, reviewerTwo;
    TextView viewMoreSubscription, viewMoreAttendance, viewMorePerformance, viewMoreBehaviouralPoints, viewMoreTeachersThink;
    String dateOne = "", dateTwo, detailOne = "", detailTwo, statusOne = "", statusTwo, classNameOne = "", classNameTwo = "";
    String subjectName = "", class_testtype = "", score = "";

    String studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        Bundle bundle = getIntent().getExtras();
        studentID = bundle.getString("childID");

        sharedPreferencesManager = new SharedPreferencesManager(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        subjectList = new ArrayList<>();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        superLayout = (NestedScrollView) findViewById(R.id.superlayout);
        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        attendanceErrorLayout = (LinearLayout) findViewById(R.id.attendanceerrorlayout);
        performanceErrorLayout = (LinearLayout) findViewById(R.id.performanceerrorlayout);
        behaviouralErrorLayout = (LinearLayout) findViewById(R.id.behaviouralerrorlayout);
        reviewErrorLayout = (LinearLayout) findViewById(R.id.reviewerrorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        editStudentProfile = (LinearLayout) findViewById(R.id.editstudentprofile);
        editStudentProfileText = (TextView) findViewById(R.id.editstudentprofiletext);

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")){
            editStudentProfile.setVisibility(View.VISIBLE);
        }

        headerfullname = (TextView) findViewById(R.id.headerfullname);
        name = (TextView) findViewById(R.id.fullname);
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

        attendanceDateOne = (TextView) findViewById(R.id.attendancedate1);
        attendanceDetailOne = (TextView) findViewById(R.id.attendancedetail1);
        attendanceDateTwo = (TextView) findViewById(R.id.attendancedate2);
        attendanceDetailTwo = (TextView) findViewById(R.id.attendancedetail2);

        overallStrongest = (TextView) findViewById(R.id.strongestsubject);
        overallWeakest = (TextView) findViewById(R.id.weakestsubject);
        subjectOne = (TextView) findViewById(R.id.subject1);
        classOne = (TextView) findViewById(R.id.class1);
        scoreOne = (TextView) findViewById(R.id.score1);
        subjectTwo = (TextView) findViewById(R.id.subject2);
        classTwo = (TextView) findViewById(R.id.class2);
        scoreTwo = (TextView) findViewById(R.id.score2);

        awarded = (TextView) findViewById(R.id.pointawarded);
        fined = (TextView) findViewById(R.id.pointsfined);
        earned = (TextView) findViewById(R.id.totalpointsearned);
        actionOne = (TextView) findViewById(R.id.action1);
        classOneSocial = (TextView) findViewById(R.id.class1social);
        pointOne = (TextView) findViewById(R.id.point1);
        actionTwo = (TextView) findViewById(R.id.action2);
        classTwoSocial = (TextView) findViewById(R.id.class2social);
        pointTwo = (TextView) findViewById(R.id.point2);

        possibleCareerChoice = (TextView) findViewById(R.id.possiblecareerchoice);
        reviewOne = (TextView) findViewById(R.id.review1);
        reviewerOne = (TextView) findViewById(R.id.reviewerName1);
        reviewTwo = (TextView) findViewById(R.id.review2);
        reviewerTwo = (TextView) findViewById(R.id.reviewerName2);

        viewMoreSubscription = (TextView) findViewById(R.id.viewmoresubscription);
        viewMoreAttendance = (TextView) findViewById(R.id.viewmoreattendance);
        viewMorePerformance = (TextView) findViewById(R.id.viewmoreperformance);
        viewMoreBehaviouralPoints = (TextView) findViewById(R.id.viewmorebehaviouralpoints);
        viewMoreTeachersThink = (TextView) findViewById(R.id.viewmoreteachersthink);

        attendanceLayoutOne = (LinearLayout) findViewById(R.id.attendancelayout1);
        attendanceLayoutTwo = (LinearLayout) findViewById(R.id.attendancelayout2);
        performanceLayoutOne = (LinearLayout) findViewById(R.id.performancelayout1);
        performanceLayoutTwo = (LinearLayout) findViewById(R.id.performancelayout2);
        behaviouralLayoutOne = (LinearLayout) findViewById(R.id.behaviourallayout1);
        behaviouralLayoutTwo = (LinearLayout) findViewById(R.id.behaviourallayout2);
        reviewLayoutOne = (LinearLayout) findViewById(R.id.reviewlayout1);
        reviewLayoutTwo = (LinearLayout) findViewById(R.id.reviewlayout2);

        attendanceLayoutOne.setVisibility(View.GONE);
        attendanceLayoutTwo.setVisibility(View.GONE);
        performanceLayoutOne.setVisibility(View.GONE);
        performanceLayoutTwo.setVisibility(View.GONE);
        behaviouralLayoutOne.setVisibility(View.GONE);
        behaviouralLayoutTwo.setVisibility(View.GONE);
        reviewLayoutOne.setVisibility(View.GONE);
        reviewLayoutTwo.setVisibility(View.GONE);

        kidPic = (ImageView) findViewById(R.id.profilepic);
        kidPicBackground = (ImageView) findViewById(R.id.backgroundimage);
        attendancePic1 = (ImageView) findViewById(R.id.attendencepic1);
        attendancePic2 = (ImageView) findViewById(R.id.attendencepic2);
        attendanceMarker1 = (ImageView) findViewById(R.id.attendancemarker1);
        attendanceMarker2 = (ImageView) findViewById(R.id.attendancemarker2);
        subjectPic1 = (ImageView) findViewById(R.id.subjectpic1);
        subjectPic2 = (ImageView) findViewById(R.id.subjectpic2);
        behaviourPic1 = (ImageView) findViewById(R.id.behavoirpic1);
        behaviourPic2 = (ImageView) findViewById(R.id.behaviorpic2);
        reviewsPic1 = (ImageView) findViewById(R.id.reviewpic1);
        reviewsPic2 = (ImageView) findViewById(R.id.reviewpic2);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
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

        viewMoreAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(StudentProfileActivity.this, ParentAttendanceActivity.class);
                Bundle b = new Bundle();
                b.putString("Child ID", studentID + " " + headerfullname.getText());
                I.putExtras(b);
                startActivity(I);
            }
        });

        viewMorePerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(StudentProfileActivity.this, StudentPerformanceForParentsActivity.class);
                Bundle b = new Bundle();
                b.putString("Child ID", studentID + " " + headerfullname.getText());
                I.putExtras(b);
                startActivity(I);
            }
        });

        viewMoreBehaviouralPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewMoreTeachersThink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        loadFromFirebase();
    }

    void loadFromFirebase(){
        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(studentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final Student student = dataSnapshot.getValue(Student.class);
                    name.setText(student.getFirstName() + " " + student.getLastName());
                    getSupportActionBar().setTitle(student.getFirstName() + " " + student.getLastName());
                    editStudentProfileText.setText("Edit " + student.getFirstName() + "'s Profile");
                    headerfullname.setText(student.getFirstName() + " " + student.getLastName());
                    gender.setText(student.getGender());
                    Glide.with(getBaseContext())
                            .load(student.getImageURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                            .into(kidPic);
                    Glide.with(getBaseContext())
                            .load(student.getImageURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new BlurTransformation(getBaseContext(), 50))
                            .into(kidPicBackground);

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
                                className.setText("Class not found");
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
                                        school.setText("School not found");
                                    }

                                    Calendar calendar = Calendar.getInstance();
                                    final String year = String.valueOf(calendar.get(Calendar.YEAR));
                                    mDatabaseReference = mFirebaseDatabase.getReference("AttendanceStudent").child(studentID);
                                    mDatabaseReference.orderByChild("year").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Double present = 0.0;
                                                Double total = 0.0;
                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    TeacherAttendanceRow teacherAttendanceRow = postSnapshot.getValue(TeacherAttendanceRow.class);
                                                    if (teacherAttendanceRow.getAttendanceStatus().equals("Present")){
                                                        present++;
                                                    }
                                                    total++;
                                                }
                                                Double puncRating = (present / total) * 100;
                                                punctualityRating.setText(String.valueOf(puncRating.intValue()) + "%");
                                            }
                                            else
                                            {
                                                punctualityRating.setText("0%");
                                            }

                                            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(studentID);
                                            mDatabaseReference.orderByChild("year").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){
                                                        double summer = 0;
                                                        double counter = 0;
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                            AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                                            summer = summer + Double.valueOf(academicRecordStudent.getScore());
                                                            counter++;
                                                        }
                                                        double score = (summer / counter);
                                                        averageAcademicPerformance.setText(String.valueOf(score));
                                                    } else {
                                                        averageAcademicPerformance.setText("0%");
                                                    }

                                                    mDatabaseReference = mFirebaseDatabase.getReference("StudentTemperament").child(studentID);
                                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()){
                                                                temperament.setText(dataSnapshot.getValue(String.class));
                                                            } else {
                                                                mDatabaseReference = mFirebaseDatabase.getReference("PredictedStudentTemperament").child(studentID);
                                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()){
                                                                            temperament.setText(dataSnapshot.getValue(String.class));
                                                                        }
                                                                        else {
                                                                            temperament.setText("No data");
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });

                                                            }

                                                            progressLayout.setVisibility(View.GONE);
                                                            errorLayout.setVisibility(View.GONE);
                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                            superLayout.setVisibility(View.VISIBLE);
                                                            //Collect behavoiral points here
                                                            behaviouralPoints.setText("coming");
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
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("Student Subscription").child(studentID);
        mDatabaseReference.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        SubscriptionModel subscriptionModel = postSnapshot.getValue(SubscriptionModel.class);
                        String stat;
                        String subTier = subscriptionModel.getSubscriptionTier();
                        String subsDate = subscriptionModel.getSubscriptionDate();
                        String expiryDate = subscriptionModel.getExpiryDate();
                        Calendar cal = Calendar.getInstance();
                        String todaysDate = String.valueOf(cal.get(Calendar.YEAR)) + "/" +
                                String.valueOf(cal.get(Calendar.MONTH) + 1) + "/" +
                                String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) + " " +
                                String.valueOf(cal.get(Calendar.HOUR)) + ":" +
                                String.valueOf(cal.get(Calendar.MINUTE)) + ":" +
                                String.valueOf(cal.get(Calendar.SECOND)) + ":" +
                                String.valueOf(cal.get(Calendar.MILLISECOND));
                        if (Date.compareDates(todaysDate, expiryDate)){
                            stat = "Expired";
                            subTier = "None";
                        } else {
                            stat = "Subscribed";
                            subTier = subscriptionModel.getSubscriptionTier();
                        }
                        status.setText(stat);
                        subscriptionTier.setText(subTier);
                        lastSubscription.setText(Date.DateFormatMMDDYYYY(subsDate));
                        expiry.setText(Date.DateFormatMMDDYYYY(expiryDate));
                    }
                } else {
                    status.setText("Subscription record does not exist");
                    subscriptionTier.setText("Subscription record does not exist");
                    lastSubscription.setText("Subscription record does not exist");
                    expiry.setText("Subscription record does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("Student Bio").child(studentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String bioString = dataSnapshot.getValue(String.class);
                    bio.setText(bioString);
                } else {
                    bio.setText("Bio hasn't been written");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        final String year = String.valueOf(calendar.get(Calendar.YEAR));
        mDatabaseReference = mFirebaseDatabase.getReference("AttendenceStudent").child(studentID);
        mDatabaseReference.orderByChild("date").limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int counter = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final TeacherAttendanceRow teacherAttendanceRow = postSnapshot.getValue(TeacherAttendanceRow.class);
                        String classID = teacherAttendanceRow.getClassID();
                        if (counter == 0){
                            attendanceLayoutOne.setVisibility(View.VISIBLE);
                            mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classID);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        Class classInstance = dataSnapshot.getValue(Class.class);
                                        detailOne = classInstance.getClassName();
//                                        detailOne = classInstance.getClassName() + " - ";
//                                        detailOne = detailOne + teacherAttendanceRow.getSubject();
                                        attendanceDetailOne.setText(detailOne);
                                    }
                                    else {
                                        detailOne = "Class not found";
                                    }

                                    dateOne = Date.DateFormatMMDDYYYY(teacherAttendanceRow.getDate());
                                    statusOne = teacherAttendanceRow.getAttendanceStatus();
                                    attendanceDateOne.setText(dateOne);
                                    attendanceLayoutOne.setVisibility(View.VISIBLE);
                                    attendancePic1.setImageDrawable(CreateDrawable.attendanceDrawable(statusOne));
                                    attendanceMarker1.setImageResource(CreateDrawable.attendanceMarkerDrawable(statusOne));

                                    attendanceDetailTwo.setText(detailOne);
                                    dateTwo = dateOne;
                                    detailTwo = detailOne;
                                    statusTwo = statusOne;
                                    attendanceDateTwo.setText(dateTwo);
                                    attendancePic2.setImageDrawable(CreateDrawable.attendanceDrawable(statusTwo));
                                    attendanceMarker2.setImageResource(CreateDrawable.attendanceMarkerDrawable(statusTwo));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            attendanceLayoutTwo.setVisibility(View.VISIBLE);
                            mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classID);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        Class classInstance = dataSnapshot.getValue(Class.class);
                                        detailOne = classInstance.getClassName();
//                                        detailOne = classInstance.getClassName() + " - ";
//                                        detailOne = detailOne + teacherAttendanceRow.getSubject();
                                        attendanceDetailOne.setText(detailOne);
                                    }
                                    else {
                                        detailOne = "Class not found";
                                    }

                                    dateOne = Date.DateFormatMMDDYYYY(teacherAttendanceRow.getDate());
                                    statusOne = teacherAttendanceRow.getAttendanceStatus();
                                    attendanceDateOne.setText(dateOne);
                                    attendancePic1.setImageDrawable(CreateDrawable.attendanceDrawable(statusOne));
                                    attendanceMarker1.setImageResource(CreateDrawable.attendanceMarkerDrawable(statusOne));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        counter++;
                    }
                }
                else {
                    attendanceErrorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(studentID);
        mDatabaseReference.orderByChild("sortableDate").limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int counter = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final AcademicRecordStudent acacAcademicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                        String classID = acacAcademicRecordStudent.getClassID();
                        if (counter == 0){
                            performanceLayoutOne.setVisibility(View.VISIBLE);
                            mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classID);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        Class classInstance = dataSnapshot.getValue(Class.class);
                                        class_testtype = classInstance.getClassName();
//                                        class_testtype = classInstance.getClassName() + " - ";
//                                        class_testtype = class_testtype + acacAcademicRecordStudent.getTestType();
                                        classOne.setText(class_testtype);
                                    }
                                    else {
                                        class_testtype = "Class not found";
                                    }

                                    subjectName = acacAcademicRecordStudent.getSubject();
                                    score = acacAcademicRecordStudent.getScore();
                                    subjectOne.setText(subjectName);
                                    scoreOne.setText(score + "%");
                                    subjectPic1.setImageDrawable(CreateDrawable.subjectNameDrawable(subjectName));

                                    subjectTwo.setText(subjectName);
                                    classTwo.setText(class_testtype);
                                    scoreTwo.setText(score + "%");
                                    subjectPic2.setImageDrawable(CreateDrawable.subjectNameDrawable(subjectName));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            performanceLayoutTwo.setVisibility(View.VISIBLE);

                            mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classID);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        Class classInstance = dataSnapshot.getValue(Class.class);
                                        class_testtype = classInstance.getClassName();
//                                        class_testtype = classInstance.getClassName() + " - ";
//                                        class_testtype = class_testtype + acacAcademicRecordStudent.getTestType();
                                        classOne.setText(class_testtype);
                                    }
                                    else {
                                        class_testtype = "Class not found";
                                    }

                                    subjectName = acacAcademicRecordStudent.getSubject();
                                    score = acacAcademicRecordStudent.getScore();
                                    subjectOne.setText(subjectName);
                                    scoreOne.setText(score + "%");
                                    subjectPic1.setImageDrawable(CreateDrawable.subjectNameDrawable(subjectName));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        counter++;
                    }


                    mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent-Subject").child(studentID);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    subjectList.add(postSnapshot.getKey());
                                }

                                for (int i = 0; i < subjectList.size(); i++) {
                                    final String subject = subjectList.get(i);

                                    mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordTotal/AcademicRecordStudent").child(studentID);
                                    mDatabaseReference.orderByChild("subject").equalTo(subject).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                double summer = 0;
                                                double counter = 0;
                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                    AcademicRecordStudent academicRecordStudent = postSnapshot.getValue(AcademicRecordStudent.class);
                                                    summer = summer + Double.valueOf(academicRecordStudent.getScore());
                                                    counter++;
                                                }
                                                double score = (summer / counter);
                                                int scoreInt = (int)score;

                                                if (!subjectScores.containsKey(scoreInt)){
                                                    subjectScores.put(scoreInt, subject);
                                                }

                                                String lowestSubject, lowestAverage;
                                                lowestSubject = subjectScores.get(subjectScores.firstKey());
                                                lowestAverage = String.valueOf(subjectScores.firstKey());
                                                overallWeakest.setText(lowestSubject + " (" + lowestAverage + "%)");

                                                String highestSubject, highestAverage;
                                                highestSubject = subjectScores.get(subjectScores.lastKey());
                                                highestAverage = String.valueOf(subjectScores.lastKey());
                                                overallStrongest.setText(highestSubject + " (" + highestAverage + "%)");
                                            }
                                            else {
//                                                overallWeakest.setText("(0%)");
//                                                overallStrongest.setText("(0%)");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                            else {
                                overallWeakest.setText("(0%)");
                                overallStrongest.setText("(0%)");

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    performanceErrorLayout.setVisibility(View.VISIBLE);
                    overallWeakest.setText("(0%)");
                    overallStrongest.setText("(0%)");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("PossibleCareerChoice").child(studentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    possibleCareerChoice.setText(dataSnapshot.getValue(String.class));
                }
                else {
                    possibleCareerChoice.setText("No records found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference("ReviewStudent").child(studentID);
        mDatabaseReference.limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int counter = 0;
                    if (dataSnapshot.getChildrenCount() == 1){
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            StudentReview review = postSnapshot.getValue(StudentReview.class);
                            reviewLayoutOne.setVisibility(View.VISIBLE);
                            reviewOne.setText(review.getReview());
                            mDatabaseReference = mFirebaseDatabase.getReference("Teacher").child(review.getTeacherID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                        reviewerOne.setText(teacher.getFirstName() + " " + teacher.getLastName());
                                        Glide.with(getBaseContext())
                                                .load(teacher.getProfilePicURL())
                                                .placeholder(R.drawable.profileimageplaceholder)
                                                .error(R.drawable.profileimageplaceholder)
                                                .centerCrop()
                                                .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                                                .into(reviewsPic1);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    } else if (dataSnapshot.getChildrenCount() > 1){
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (counter == 0){
                                reviewLayoutOne.setVisibility(View.VISIBLE);
                                StudentReview review = postSnapshot.getValue(StudentReview.class);
                                reviewOne.setText(review.getReview());
                                mDatabaseReference = mFirebaseDatabase.getReference("Teacher").child(review.getTeacherID());
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                            reviewerOne.setText(teacher.getFirstName() + " " + teacher.getLastName());
                                            Glide.with(getBaseContext())
                                                    .load(teacher.getProfilePicURL())
                                                    .placeholder(R.drawable.profileimageplaceholder)
                                                    .error(R.drawable.profileimageplaceholder)
                                                    .centerCrop()
                                                    .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                                                    .into(reviewsPic2);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else if (counter == 1){
                                reviewLayoutTwo.setVisibility(View.VISIBLE);
                                StudentReview review = postSnapshot.getValue(StudentReview.class);
                                reviewTwo.setText(review.getReview());
                                mDatabaseReference = mFirebaseDatabase.getReference("Teacher").child(review.getTeacherID());
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                            reviewerTwo.setText(teacher.getFirstName() + " " + teacher.getLastName());
                                            Glide.with(getBaseContext())
                                                    .load(teacher.getProfilePicURL())
                                                    .placeholder(R.drawable.profileimageplaceholder)
                                                    .error(R.drawable.profileimageplaceholder)
                                                    .centerCrop()
                                                    .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                                                    .into(reviewsPic2);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            counter++;
                        }
                    }
                }
                else {
                    reviewErrorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadImages(){
        if (!("https://s-media-cache-ak0.pinimg.com/736x/7c/af/28/7caf28d3112d4a9885d932610f51727a--beautiful-black-babies-beautiful-children.jpg").isEmpty()) {
            Glide.with(this)
                    .load("https://s-media-cache-ak0.pinimg.com/736x/7c/af/28/7caf28d3112d4a9885d932610f51727a--beautiful-black-babies-beautiful-children.jpg")
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(kidPic);
        }

        if (!("https://s-media-cache-ak0.pinimg.com/736x/7c/af/28/7caf28d3112d4a9885d932610f51727a--beautiful-black-babies-beautiful-children.jpg").isEmpty()) {
            Glide.with(this)
                    .load("https://s-media-cache-ak0.pinimg.com/736x/7c/af/28/7caf28d3112d4a9885d932610f51727a--beautiful-black-babies-beautiful-children.jpg")
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new BlurTransformation(this, 50))
                    .into(kidPicBackground);
        }

        String attendance1 = "P";
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(attendance1, Color.argb(255, 0, 200, 0));
        attendancePic1.setImageDrawable(textDrawable);

        String attendance2 = "A";
        textDrawable = TextDrawable.builder()
                .buildRound(attendance2, Color.argb(255, 255, 0, 0));
        attendancePic2.setImageDrawable(textDrawable);

        String subject1 = "M";
        textDrawable = TextDrawable.builder()
                .buildRound(subject1, Color.GRAY);
        subjectPic1.setImageDrawable(textDrawable);

        String subject2 = "P";
        textDrawable = TextDrawable.builder()
                .buildRound(subject2, Color.GRAY);
        subjectPic2.setImageDrawable(textDrawable);

        String behavoir1 = "+1";
        textDrawable = TextDrawable.builder()
                .buildRound(behavoir1, Color.GREEN);
        behaviourPic1.setImageDrawable(textDrawable);

        String behavoir2 = "-1";
        textDrawable = TextDrawable.builder()
                .buildRound(behavoir2, Color.RED);
        behaviourPic2.setImageDrawable(textDrawable);

        if (!("http://thenet.ng/wp-content/uploads/2015/06/mari-okann.png").isEmpty()) {
            Glide.with(this)
                    .load("http://thenet.ng/wp-content/uploads/2015/06/mari-okann.png")
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(reviewsPic1);
        }

        if (!("http://thenet.ng/wp-content/uploads/2015/06/mari-okann.png").isEmpty()) {
            Glide.with(this)
                    .load("http://thenet.ng/wp-content/uploads/2015/06/mari-okann.png")
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(reviewsPic2);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
