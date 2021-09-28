package com.celerii.celerii.Activities.Profiles.SchoolProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EditYearActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecord;
import com.celerii.celerii.models.Address;
import com.celerii.celerii.models.Award;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.EEAP;
import com.celerii.celerii.models.NotableAlumni;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.SchoolSettings;
import com.celerii.celerii.models.TeacherAttendanceHeader;
import com.celerii.celerii.models.TeacherSchoolConnectionRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class SchoolProfileActivity extends AppCompatActivity {
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
    Button connect, disconnect;

    LinearLayout generalLayout, contactInfoLayout, missionVisionHistoryLayout, gallerySubjectsCoCurricularActivitiesLayout, eeapLayout, awardsLayout, notableAlumniLayout,
            collegeLayout;
    LinearLayout schoolTypeLayout, curriculumLayout, totalNumberOfSeatsLayout, averageNumberOfSeatsPerClassLayout, termAverageAttendanceLayout,
            termAveragePunctualityLayout, termAveragePerformanceInInternalExamsLayout;
    LinearLayout profilePictureClipper;
    LinearLayout notableAlumniPic1Clipper, notableAlumniPic2Clipper, notableAlumniPic3Clipper;
    LinearLayout gallery, offeredSubjects, coCurricularActivities;
    LinearLayout eEAPErrorLayout, eEAPLayout1, eEAPLayout2, eEAPLayout3;
    LinearLayout awardsErrorLayout, awardLayout1, awardLayout2, awardLayout3;
    LinearLayout notableAlumniErrorLayout, notableAlumniLayout1, notableAlumniLayout2, notableAlumniLayout3;
    LinearLayout collegeErrorLayout, collegeLayout1, collegeLayout2, collegeLayout3, collegeLayout4, collegeLayout5;
    LinearLayout collegePic1Clipper, collegePic2Clipper, collegePic3Clipper, collegePic4Clipper, collegePic5Clipper;

    ImageView schoolPic;
    ImageView missionMarker, visionMarker, historyMarker;
    ImageView notableAlumniPic1, notableAlumniPic2, notableAlumniPic3;
    ImageView viewMoreGallery, viewMoreOfferedSubjects, viewMoreCoCurricularActivities, viewMoreEEAP, viewMoreAwards, viewMoreNotableAlumni;
    ImageView collegePic1, collegePic2, collegePic3, collegePic4, collegePic5;

    TextView schoolFullName, schoolType, curriculum, totalNumberOfSeats, averageNumberOfSeatsPerClass, averageAttendance, averagePunctuality, averageAcademicPerformanceInInternalExams;
    TextView address, viewMap;
    TextView missionTab, visionTab, historyTab, missionVisionHistory;
    TextView eEAPErrorText, eEAP1, eEAP2, eEAP3, eEAPScore1, eEAPScore2, eEAPScore3;
    TextView awardsErrorText, award1, award2, award3, awardYear1, awardYear2, awardYear3;
    TextView notableAlumniErrorText, notableAlumni1, notableAlumni2, notableAlumni3, notableAlumniSet1, notableAlumniSet2, notableAlumniSet3;
    TextView collegeErrorText, college1, college2, college3, college4, college5, collegeSN1, collegeSN2, collegeSN3, collegeSN4, collegeSN5;

    ProgressBar eEAPProgressBar1, eEAPProgressBar2, eEAPProgressBar3;

    Bundle bundle;
    String schoolID = "", schoolName, parentActivity;
    String term, year, term_year, year_term;
    String missionString = "", visionString = "", historyString = "";
    int numberOfStudents = 0, schoolAttendanceCounter = 0;
    double totalAttendance = 0.0, totalPunctuality = 0.0;
    double schoolTotalScore = 0.0;
    double schoolAverageScore = 0.0;
    int schoolCounter = 0;
    ArrayList<String> classesWithResults = new ArrayList<>();
    int totalChildrenCount = 0;
    int counter;
    int classesWithAttendance = 0;
    ArrayList<String> classes = new ArrayList<>();
    ArrayList<Address> addresses = new ArrayList<>();
    ArrayList<EEAP> eEAPList = new ArrayList<>();
    ArrayList<Award> awardList = new ArrayList<>();
    ArrayList<NotableAlumni> notableAlumniList = new ArrayList<>();
    ArrayList<String> collegeStringList = new ArrayList<>();
    ArrayList<String> pendingIncomingRequestKey = new ArrayList<>();
    ArrayList<String> pendingOutgoingRequestKey = new ArrayList<>();
    ArrayList<Double> normalizedAverageList = new ArrayList<>();

    ArrayList<LinearLayout> eEAPLinearLayoutList = new ArrayList<>();
    ArrayList<LinearLayout> awardsLinearLayoutList = new ArrayList<>();
    ArrayList<LinearLayout> notableAlumniLinearLayoutList = new ArrayList<>();
    ArrayList<LinearLayout> collegeLinearLayoutList = new ArrayList<>();

    ArrayList<TextView> eEAPTextList = new ArrayList<>();
    ArrayList<TextView> eEAPScoreList = new ArrayList<>();
    ArrayList<ProgressBar> eEAPProgressBarList = new ArrayList<>();
    ArrayList<TextView> awardsTextList = new ArrayList<>();
    ArrayList<TextView> awardsYearList = new ArrayList<>();
    ArrayList<ImageView> notableAlumniImageList = new ArrayList<>();
    ArrayList<TextView> notableAlumniTextList = new ArrayList<>();
    ArrayList<TextView> notableAlumniSetList = new ArrayList<>();
    ArrayList<ImageView> collegePicList = new ArrayList<>();
    ArrayList<TextView> collegeList = new ArrayList<>();

    SchoolSettings schoolSettings = new SchoolSettings();

    String featureUseKey = "";
    String featureName = "School Profile";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_profile_one);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        schoolID = bundle.getString("schoolID");
        parentActivity = bundle.getString("parentActivity");
        if (parentActivity != null) {
            if (!parentActivity.isEmpty()) {
                sharedPreferencesManager.setActiveAccount(parentActivity);
                mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
                mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue(parentActivity);
            }
        }
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        superLayout = (ScrollView) findViewById(R.id.superlayout);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("School Profile");

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        termButton = (Button) findViewById(R.id.term);
        yearButton = (Button) findViewById(R.id.year);

        connect = (Button) findViewById(R.id.connect);
        disconnect = (Button) findViewById(R.id.disconnect);

        generalLayout = (LinearLayout) findViewById(R.id.generallayout);
        contactInfoLayout = (LinearLayout) findViewById(R.id.contactinfolayout);
        missionVisionHistoryLayout = (LinearLayout) findViewById(R.id.missionvisionhistorylayout);
        gallerySubjectsCoCurricularActivitiesLayout = (LinearLayout) findViewById(R.id.gallerysubjectscocurricularactivitieslayout);
        eeapLayout = (LinearLayout) findViewById(R.id.eeaplayout);
        awardsLayout = (LinearLayout) findViewById(R.id.awardslayout);
        notableAlumniLayout = (LinearLayout) findViewById(R.id.notablealumnilayout);
        collegeLayout = (LinearLayout) findViewById(R.id.collegelayout);

        schoolTypeLayout = (LinearLayout) findViewById(R.id.schooltypelayout);
        curriculumLayout = (LinearLayout) findViewById(R.id.curriculumlayout);
        totalNumberOfSeatsLayout = (LinearLayout) findViewById(R.id.totalnumberofseatslayout);
        averageNumberOfSeatsPerClassLayout = (LinearLayout) findViewById(R.id.averagenumberofseatsperclasslayout);
        termAverageAttendanceLayout = (LinearLayout) findViewById(R.id.termaverageattendancelayout);
        termAveragePunctualityLayout = (LinearLayout) findViewById(R.id.termaveragepunctualitylayout);
        termAveragePerformanceInInternalExamsLayout = (LinearLayout) findViewById(R.id.termaverageperformanceininternalexamslayout);

        profilePictureClipper = (LinearLayout) findViewById(R.id.profilepictureclipper);
        notableAlumniPic1Clipper = (LinearLayout) findViewById(R.id.notablealumnipic1clipper);
        notableAlumniPic2Clipper = (LinearLayout) findViewById(R.id.notablealumnipic2clipper);
        notableAlumniPic3Clipper = (LinearLayout) findViewById(R.id.notablealumnipic3clipper);
        gallery = (LinearLayout) findViewById(R.id.gallery);
        offeredSubjects = (LinearLayout) findViewById(R.id.offeredsubjects);
        coCurricularActivities = (LinearLayout) findViewById(R.id.cocurricularactivities);
        eEAPErrorLayout = (LinearLayout) findViewById(R.id.eeaperrorlayout);
        eEAPLayout1 = (LinearLayout) findViewById(R.id.eeaplayout1);
        eEAPLayout2 = (LinearLayout) findViewById(R.id.eeaplayout2);
        eEAPLayout3 = (LinearLayout) findViewById(R.id.eeaplayout3);
        awardsErrorLayout = (LinearLayout) findViewById(R.id.awardserrorlayout);
        awardLayout1 = (LinearLayout) findViewById(R.id.awardlayout1);
        awardLayout2 = (LinearLayout) findViewById(R.id.awardlayout2);
        awardLayout3 = (LinearLayout) findViewById(R.id.awardlayout3);
        notableAlumniErrorLayout = (LinearLayout) findViewById(R.id.notablealumnierrorlayout);
        notableAlumniLayout1 = (LinearLayout) findViewById(R.id.notablealumnilayout1);
        notableAlumniLayout2 = (LinearLayout) findViewById(R.id.notablealumnilayout2);
        notableAlumniLayout3 = (LinearLayout) findViewById(R.id.notablealumnilayout3);
        collegeErrorLayout = (LinearLayout) findViewById(R.id.collegeerrorlayout);
        collegeLayout1 = (LinearLayout) findViewById(R.id.collegelayout1);
        collegeLayout2 = (LinearLayout) findViewById(R.id.collegelayout2);
        collegeLayout3 = (LinearLayout) findViewById(R.id.collegelayout3);
        collegeLayout4 = (LinearLayout) findViewById(R.id.collegelayout4);
        collegeLayout5 = (LinearLayout) findViewById(R.id.collegelayout5);
        collegePic1Clipper = (LinearLayout) findViewById(R.id.collegepic1clipper);
        collegePic2Clipper = (LinearLayout) findViewById(R.id.collegepic2clipper);
        collegePic3Clipper = (LinearLayout) findViewById(R.id.collegepic3clipper);
        collegePic4Clipper = (LinearLayout) findViewById(R.id.collegepic4clipper);
        collegePic5Clipper = (LinearLayout) findViewById(R.id.collegepic5clipper);

        schoolPic = (ImageView) findViewById(R.id.schoolpic);
        missionMarker = (ImageView) findViewById(R.id.missionmarker);
        visionMarker = (ImageView) findViewById(R.id.visionmarker);
        historyMarker = (ImageView) findViewById(R.id.historymarker);
        notableAlumniPic1 = (ImageView) findViewById(R.id.notablealumnipic1);
        notableAlumniPic2 = (ImageView) findViewById(R.id.notablealumnipic2);
        notableAlumniPic3 = (ImageView) findViewById(R.id.notablealumnipic3);
        viewMoreGallery = (ImageView) findViewById(R.id.viewmoregallery);
        viewMoreOfferedSubjects = (ImageView) findViewById(R.id.viewmoreofferedsubjects);
        viewMoreCoCurricularActivities = (ImageView) findViewById(R.id.viewmorecocurricularactivities);
        viewMoreEEAP = (ImageView) findViewById(R.id.viewmoreeeap);
        viewMoreAwards = (ImageView) findViewById(R.id.viewmoreawards);
        viewMoreNotableAlumni = (ImageView) findViewById(R.id.viewmorenotablealumni);
        collegePic1 = (ImageView) findViewById(R.id.collegepic1);
        collegePic2 = (ImageView) findViewById(R.id.collegepic2);
        collegePic3 = (ImageView) findViewById(R.id.collegepic3);
        collegePic4 = (ImageView) findViewById(R.id.collegepic4);
        collegePic5 = (ImageView) findViewById(R.id.collegepic5);

        schoolFullName = (TextView) findViewById(R.id.schoolname);
        schoolType = (TextView) findViewById(R.id.schooltype);
        curriculum = (TextView) findViewById(R.id.curriculum);
        totalNumberOfSeats = (TextView) findViewById(R.id.totalnumberofseats);
        averageNumberOfSeatsPerClass = (TextView) findViewById(R.id.averagenumberofseatsperclass);
        averageAttendance = (TextView) findViewById(R.id.averageattendance);
        averagePunctuality = (TextView) findViewById(R.id.averagepunctuality);
        averageAcademicPerformanceInInternalExams = (TextView) findViewById(R.id.averageacademicperformanceininternalexams);
        address = (TextView) findViewById(R.id.address);
        viewMap = (TextView) findViewById(R.id.viewmap);
        missionTab = (TextView) findViewById(R.id.missiontab);
        visionTab = (TextView) findViewById(R.id.visiontab);
        historyTab = (TextView) findViewById(R.id.historytab);
        missionVisionHistory = (TextView) findViewById(R.id.missionvisionhistory);
        eEAPErrorText = (TextView) findViewById(R.id.eeaperrortext);
        eEAP1 = (TextView) findViewById(R.id.eeap1);
        eEAP2 = (TextView) findViewById(R.id.eeap2);
        eEAP3 = (TextView) findViewById(R.id.eeap3);
        eEAPScore1 = (TextView) findViewById(R.id.eeapscore1);
        eEAPScore2 = (TextView) findViewById(R.id.eeapscore2);
        eEAPScore3 = (TextView) findViewById(R.id.eeapscore3);
        awardsErrorText = (TextView) findViewById(R.id.awardserrortext);
        award1 = (TextView) findViewById(R.id.award1);
        award2 = (TextView) findViewById(R.id.award2);
        award3 = (TextView) findViewById(R.id.award3);
        awardYear1 = (TextView) findViewById(R.id.awardyear1);
        awardYear2 = (TextView) findViewById(R.id.awardyear2);
        awardYear3 = (TextView) findViewById(R.id.awardyear3);
        notableAlumniErrorText = (TextView) findViewById(R.id.notablealumnierrortext);
        notableAlumni1 = (TextView) findViewById(R.id.notablealumni1);
        notableAlumni2 = (TextView) findViewById(R.id.notablealumni2);
        notableAlumni3 = (TextView) findViewById(R.id.notablealumni3);
        notableAlumniSet1 = (TextView) findViewById(R.id.notablealumniset1);
        notableAlumniSet2 = (TextView) findViewById(R.id.notablealumniset2);
        notableAlumniSet3 = (TextView) findViewById(R.id.notablealumniset3);
        collegeErrorText = (TextView) findViewById(R.id.collegeerrortext);
        college1 = (TextView) findViewById(R.id.college1);
        college2 = (TextView) findViewById(R.id.college2);
        college3 = (TextView) findViewById(R.id.college3);
        college4 = (TextView) findViewById(R.id.college4);
        college5 = (TextView) findViewById(R.id.college5);

        eEAPProgressBar1 = (ProgressBar) findViewById(R.id.eeapprogressbar1);
        eEAPProgressBar2 = (ProgressBar) findViewById(R.id.eeapprogressbar2);
        eEAPProgressBar3 = (ProgressBar) findViewById(R.id.eeapprogressbar3);

        profilePictureClipper.setClipToOutline(true);
        notableAlumniPic1Clipper.setClipToOutline(true);
        notableAlumniPic2Clipper.setClipToOutline(true);
        notableAlumniPic3Clipper.setClipToOutline(true);
        collegePic1Clipper.setClipToOutline(true);
        collegePic2Clipper.setClipToOutline(true);
        collegePic3Clipper.setClipToOutline(true);
        collegePic4Clipper.setClipToOutline(true);
        collegePic5Clipper.setClipToOutline(true);

        eEAPLinearLayoutList.add(eEAPLayout1);
        eEAPLinearLayoutList.add(eEAPLayout2);
        eEAPLinearLayoutList.add(eEAPLayout3);

        awardsLinearLayoutList.add(awardLayout1);
        awardsLinearLayoutList.add(awardLayout2);
        awardsLinearLayoutList.add(awardLayout3);

        notableAlumniLinearLayoutList.add(notableAlumniLayout1);
        notableAlumniLinearLayoutList.add(notableAlumniLayout2);
        notableAlumniLinearLayoutList.add(notableAlumniLayout3);

        collegeLinearLayoutList.add(collegeLayout1);
        collegeLinearLayoutList.add(collegeLayout2);
        collegeLinearLayoutList.add(collegeLayout3);
        collegeLinearLayoutList.add(collegeLayout4);
        collegeLinearLayoutList.add(collegeLayout5);

//        ArrayList<TextView> eEAPTextList = new ArrayList<>();
//        ArrayList<TextView> eEAPScore = new ArrayList<>();
//        ArrayList<ProgressBar> eEAPProgressBarList = new ArrayList<>();
//        ArrayList<TextView> awardsTextList = new ArrayList<>();
//        ArrayList<TextView> awardsYearList = new ArrayList<>();
//        ArrayList<TextView> notableAlumniImageList = new ArrayList<>();
//        ArrayList<TextView> notableAlumniTextList = new ArrayList<>();
//        ArrayList<TextView> notableAlumniSetList = new ArrayList<>();
//        ArrayList<TextView> collegeList = new ArrayList<>();

        eEAPTextList.add(eEAP1);
        eEAPTextList.add(eEAP2);
        eEAPTextList.add(eEAP3);

        eEAPScoreList.add(eEAPScore1);
        eEAPScoreList.add(eEAPScore2);
        eEAPScoreList.add(eEAPScore3);

        eEAPProgressBarList.add(eEAPProgressBar1);
        eEAPProgressBarList.add(eEAPProgressBar2);
        eEAPProgressBarList.add(eEAPProgressBar3);

        awardsTextList.add(award1);
        awardsTextList.add(award2);
        awardsTextList.add(award3);

        awardsYearList.add(awardYear1);
        awardsYearList.add(awardYear2);
        awardsYearList.add(awardYear3);

        notableAlumniImageList.add(notableAlumniPic1);
        notableAlumniImageList.add(notableAlumniPic2);
        notableAlumniImageList.add(notableAlumniPic3);

        notableAlumniTextList.add(notableAlumni1);
        notableAlumniTextList.add(notableAlumni2);
        notableAlumniTextList.add(notableAlumni3);

        notableAlumniSetList.add(notableAlumniSet1);
        notableAlumniSetList.add(notableAlumniSet2);
        notableAlumniSetList.add(notableAlumniSet3);

        collegeList.add(college1);
        collegeList.add(college2);
        collegeList.add(college3);
        collegeList.add(college4);
        collegeList.add(college5);

        collegePicList.add(collegePic1);
        collegePicList.add(collegePic2);
        collegePicList.add(collegePic3);
        collegePicList.add(collegePic4);
        collegePicList.add(collegePic5);

        term = Term.getTermShort();
        year = Date.getYear();

        termButton.setText(Term.Term(term));
        yearButton.setText(year);

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            connect.setVisibility(View.GONE);
            disconnect.setVisibility(View.GONE);
        }

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

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parentActivity != null) {
                    if (parentActivity.equals("Teacher")) {
                        connect();
                    }
                } else {
                    if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                        connect();
                    }
                }
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parentActivity != null) {
                    if (parentActivity.equals("Teacher")) {
                        disconnect();
                    }
                } else {
                    if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                        disconnect();
                    }
                }
            }
        });

        missionTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!missionString.trim().isEmpty()) {
                    missionVisionHistory.setText(missionString);
                } else {
                    String message = schoolName + " hasn't set its mission yet";
                    missionVisionHistory.setText(message);
                }
                missionMarker.setVisibility(View.VISIBLE);
                visionMarker.setVisibility(View.INVISIBLE);
                historyMarker.setVisibility(View.INVISIBLE);
                missionTab.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                visionTab.setTextColor(ContextCompat.getColor(context, R.color.black));
                historyTab.setTextColor(ContextCompat.getColor(context, R.color.black));
                missionTab.setTypeface(null, Typeface.BOLD);
                visionTab.setTypeface(null, Typeface.NORMAL);
                historyTab.setTypeface(null, Typeface.NORMAL);
            }
        });

        visionTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!visionString.trim().isEmpty()) {
                    missionVisionHistory.setText(visionString);
                } else {
                    String message = schoolName + " hasn't set its vision yet";
                    missionVisionHistory.setText(message);
                }
                missionMarker.setVisibility(View.INVISIBLE);
                visionMarker.setVisibility(View.VISIBLE);
                historyMarker.setVisibility(View.INVISIBLE);
                missionTab.setTextColor(ContextCompat.getColor(context, R.color.black));
                visionTab.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                historyTab.setTextColor(ContextCompat.getColor(context, R.color.black));
                missionTab.setTypeface(null, Typeface.NORMAL);
                visionTab.setTypeface(null, Typeface.BOLD);
                historyTab.setTypeface(null, Typeface.NORMAL);
            }
        });

        historyTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!historyString.trim().isEmpty()) {
                    missionVisionHistory.setText(historyString);
                } else {
                    String message = schoolName + " hasn't set its history yet";
                    missionVisionHistory.setText(message);
                }
                missionMarker.setVisibility(View.INVISIBLE);
                visionMarker.setVisibility(View.INVISIBLE);
                historyMarker.setVisibility(View.VISIBLE);
                missionTab.setTextColor(ContextCompat.getColor(context, R.color.black));
                visionTab.setTextColor(ContextCompat.getColor(context, R.color.black));
                historyTab.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                missionTab.setTypeface(null, Typeface.NORMAL);
                visionTab.setTypeface(null, Typeface.NORMAL);
                historyTab.setTypeface(null, Typeface.BOLD);
            }
        });

        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolProfileActivity.this, SchoolGalleryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("schoolID", schoolID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        offeredSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolProfileActivity.this, SchoolProfileOfferedSubjectsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("schoolID", schoolID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        coCurricularActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolProfileActivity.this, SchoolProfileCoCurricularActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("schoolID", schoolID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        viewMoreEEAP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolProfileActivity.this, SchoolProfileEEAPActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("schoolID", schoolID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        viewMoreAwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolProfileActivity.this, SchoolProfileAwardsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("schoolID", schoolID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        viewMoreNotableAlumni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchoolProfileActivity.this, SchoolProfileNotableAlumniActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("schoolID", schoolID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            superLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        numberOfStudents = 0;
        schoolAttendanceCounter = 0;
        totalAttendance = 0.0;
        totalPunctuality = 0.0;
        classes.clear();
        addresses.clear();
        eEAPList.clear();
        awardList.clear();
        notableAlumniList.clear();
        collegeStringList.clear();
        pendingIncomingRequestKey.clear();
        pendingOutgoingRequestKey.clear();

        term_year = term + "_" + year;
        year_term = year + "_" +  term;

        mDatabaseReference = mFirebaseDatabase.getReference().child("School Settings").child(schoolID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    schoolSettings = dataSnapshot.getValue(SchoolSettings.class);
                }

                mDatabaseReference = mFirebaseDatabase.getReference("School").child(schoolID);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            School school = dataSnapshot.getValue(School.class);

                            boolean isDeleted = (school.getIsDeleted() == null) ? false : school.getIsDeleted();
                            if (!isDeleted) {
                                schoolName = school.getSchoolName();
                                String schoolPicURL = school.getProfilePhotoUrl();
                                schoolFullName.setText(schoolName);
                                getSupportActionBar().setTitle(schoolName);

                                Drawable textDrawable;
                                if (!schoolName.trim().isEmpty()) {
                                    String[] nameArray = schoolName.replaceAll("\\s+", " ").trim().split(" ");
                                    if (nameArray.length == 1) {
                                        textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], 150);
                                    } else {
                                        textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], nameArray[1], 150);
                                    }
                                    schoolPic.setImageDrawable(textDrawable);
                                } else {
                                    textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 150);
                                }

                                if (!schoolPicURL.isEmpty()) {
                                    Glide.with(context)
                                            .load(schoolPicURL)
                                            .placeholder(textDrawable)
                                            .error(textDrawable)
                                            .centerCrop()
                                            .bitmapTransform(new CropCircleTransformation(context))
                                            .into(schoolPic);
                                }

                                String schoolTypeString = school.getSchoolType();
                                if (schoolSettings.isShowSchoolType()) {
                                    schoolTypeLayout.setVisibility(View.VISIBLE);
                                    if (!schoolTypeString.trim().isEmpty()) {
                                        schoolType.setText(schoolTypeString);
                                    } else {
                                        String message = schoolName + " hasn't set its type yet";
                                        schoolType.setText(message);
                                    }
                                } else {
                                    schoolTypeLayout.setVisibility(View.GONE);
                                }

                                String curriculumString = school.getCurriculum();
                                if (schoolSettings.isShowCurriculum()) {
                                    curriculumLayout.setVisibility(View.VISIBLE);
                                    if (!curriculumString.trim().isEmpty()) {
                                        curriculum.setText(curriculumString);
                                    } else {
                                        String message = schoolName + " hasn't set its curriculum yet";
                                        curriculum.setText(message);
                                    }
                                } else {
                                    curriculumLayout.setVisibility(View.GONE);
                                }

                                if (schoolSettings.isShowMissionVisionAndHistory()) {
                                    missionVisionHistoryLayout.setVisibility(View.VISIBLE);
                                    missionString = school.getMission();
                                    visionString = school.getVision();
                                    historyString = school.getHistory();

                                    if (!missionString.trim().isEmpty()) {
                                        missionVisionHistory.setText(missionString);
                                    } else {
                                        String message = schoolName + " hasn't set its mission yet";
                                        missionVisionHistory.setText(message);
                                    }

                                    missionMarker.setVisibility(View.VISIBLE);
                                    visionMarker.setVisibility(View.INVISIBLE);
                                    historyMarker.setVisibility(View.INVISIBLE);
                                    missionTab.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
                                    visionTab.setTextColor(ContextCompat.getColor(context, R.color.black));
                                    historyTab.setTextColor(ContextCompat.getColor(context, R.color.black));
                                    missionTab.setTypeface(null, Typeface.BOLD);
                                    visionTab.setTypeface(null, Typeface.NORMAL);
                                    historyTab.setTypeface(null, Typeface.NORMAL);
                                } else {
                                    missionVisionHistoryLayout.setVisibility(View.GONE);
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference().child("School Students").child(schoolID);
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            numberOfStudents = (int) dataSnapshot.getChildrenCount();
                                            totalNumberOfSeats.setText(String.valueOf(numberOfStudents));
                                        } else {
                                            totalNumberOfSeats.setText("0");
                                        }

                                        if (schoolSettings.isShowTotalSeats()) {
                                            totalNumberOfSeatsLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            totalNumberOfSeatsLayout.setVisibility(View.GONE);
                                        }

                                        mDatabaseReference = mFirebaseDatabase.getReference().child("School Class").child(schoolID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    int numberOfClasses = (int) dataSnapshot.getChildrenCount();
                                                    int studentsPerClass = (int) (numberOfStudents / numberOfClasses);
                                                    averageNumberOfSeatsPerClass.setText(String.valueOf(studentsPerClass));
                                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                        classes.add(postSnapshot.getKey());
                                                    }
                                                } else {
                                                    averageNumberOfSeatsPerClass.setText(String.valueOf(numberOfStudents));
                                                }

                                                if (schoolSettings.isShowAverageSeatsPerClass()) {
                                                    averageNumberOfSeatsPerClassLayout.setVisibility(View.VISIBLE);
                                                } else {
                                                    averageNumberOfSeatsPerClassLayout.setVisibility(View.GONE);
                                                }

                                                if (classes.size() > 0) {
                                                    for (String classID : classes) {
                                                        classesWithAttendance = 0;
                                                        String subject_term_year = "General_" + term_year;
                                                        mDatabaseReference = mFirebaseDatabase.getReference().child("AttendanceClass").child(classID);
                                                        mDatabaseReference.orderByChild("subject_term_year").equalTo(subject_term_year).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                schoolAttendanceCounter++;
                                                                int nos = (int) dataSnapshot.getChildrenCount();
                                                                if (dataSnapshot.exists()) {
                                                                    double presentPercentageSummer = 0.0;
                                                                    double punctualPercentageSummer = 0.0;
                                                                    int attendanceCounter = 0;
                                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                        TeacherAttendanceHeader teacherAttendanceHeader = postSnapshot.getValue(TeacherAttendanceHeader.class);
                                                                        double present = Double.parseDouble(teacherAttendanceHeader.getPresent());
                                                                        double absent = Double.parseDouble(teacherAttendanceHeader.getAbsent());
                                                                        double late = Double.parseDouble(teacherAttendanceHeader.getLate());
                                                                        double presentPercentage = ((present + late) / (present + absent + late)) * 100;
                                                                        double punctualPercentage = ((present) / (present + absent + late)) * 100;
                                                                        presentPercentageSummer += presentPercentage;
                                                                        punctualPercentageSummer += punctualPercentage;
                                                                        attendanceCounter += 1;
                                                                    }

                                                                    double averagePresent = presentPercentageSummer / attendanceCounter;
                                                                    double averagePunctual = punctualPercentageSummer / attendanceCounter;
                                                                    totalAttendance += averagePresent;
                                                                    totalPunctuality += averagePunctual;
                                                                    classesWithAttendance++;
                                                                }

                                                                if (schoolAttendanceCounter == classes.size()) {
                                                                    int averageAttendanceInt = (int) (totalAttendance / classesWithAttendance);
                                                                    int averagePunctualityInt = (int) (totalPunctuality / classesWithAttendance);
                                                                    String messagePresent = String.valueOf(averageAttendanceInt) + "%";
                                                                    String messagePunctual = String.valueOf(averagePunctualityInt) + "%";
                                                                    averageAttendance.setText(messagePresent);
                                                                    averagePunctuality.setText(messagePunctual);
                                                                    loadTeacherRelationships();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                } else {
                                                    averageAttendance.setText("0%");
                                                    averagePunctuality.setText("0%");
                                                    loadTeacherRelationships();
                                                }

                                                if (schoolSettings.isShowAverageAttendance()) {
                                                    termAverageAttendanceLayout.setVisibility(View.VISIBLE);
                                                } else {
                                                    termAverageAttendanceLayout.setVisibility(View.GONE);
                                                }

                                                if (schoolSettings.isShowAveragePunctuality()) {
                                                    termAveragePunctualityLayout.setVisibility(View.VISIBLE);
                                                } else {
                                                    termAveragePunctualityLayout.setVisibility(View.GONE);
                                                }

                                                if (schoolSettings.isShowGallery()) {
                                                    gallery.setVisibility(View.VISIBLE);
                                                } else {
                                                    gallery.setVisibility(View.GONE);
                                                }

                                                if (schoolSettings.isShowOfferedSubjects()) {
                                                    offeredSubjects.setVisibility(View.VISIBLE);
                                                } else {
                                                    offeredSubjects.setVisibility(View.GONE);
                                                }

                                                if (schoolSettings.isShowCoCurricularActivities()) {
                                                    coCurricularActivities.setVisibility(View.VISIBLE);
                                                } else {
                                                    coCurricularActivities.setVisibility(View.GONE);
                                                }

                                                if (!schoolSettings.isShowGallery() && !schoolSettings.isShowOfferedSubjects() && !schoolSettings.isShowCoCurricularActivities()) {
                                                    gallerySubjectsCoCurricularActivitiesLayout.setVisibility(View.GONE);
                                                } else {
                                                    gallerySubjectsCoCurricularActivitiesLayout.setVisibility(View.VISIBLE);
                                                }

//                                                if (!schoolSettings.isShowSchoolType() &&
//                                                        !schoolSettings.isShowCurriculum() &&
//                                                        !schoolSettings.isShowTotalSeats() &&
//                                                        !schoolSettings.isShowAverageSeatsPerClass() &&
//                                                        !schoolSettings.isShowAverageAttendance() &&
//                                                        !schoolSettings.isShowAveragePunctuality() &&
//                                                        !schoolSettings.isShowAverageInternalExamsPerformance()) {
//                                                    generalLayout.setVisibility(View.GONE);
//                                                } else {
//                                                    generalLayout.setVisibility(View.VISIBLE);
//                                                }
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
                            } else {
                                mySwipeRefreshLayout.setRefreshing(false);
                                superLayout.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.VISIBLE);
                                errorLayoutText.setText("The school whose profile you've requested doesn't exist.");
                            }
                        } else {
                            mySwipeRefreshLayout.setRefreshing(false);
                            superLayout.setVisibility(View.GONE);
                            progressLayout.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                            errorLayoutText.setText("The school whose profile you've requested doesn't exist.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadTeacherRelationships() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("School Teacher").child(schoolID).child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                    if (dataSnapshot.exists()) {
                        connect.setVisibility(View.GONE);
                        disconnect.setVisibility(View.VISIBLE);
                    } else {
                        connect.setVisibility(View.VISIBLE);
                        disconnect.setVisibility(View.GONE);
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference().child("School Address").child(schoolID);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String addressString = "";
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                Address address = postSnapshot.getValue(Address.class);
                                addresses.add(address);
                                addressString = addressString + address.getAddress() + "\n";
                            }
                            addressString = addressString.trim();
                            address.setText(addressString);
                        } else {
                            String message = schoolName + " hasn't set their address yet";
                            address.setText(message);
                        }

                        mDatabaseReference = mFirebaseDatabase.getReference().child("School Phone Numbers").child(schoolID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String phoneNumberString = "";
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        String phoneNumber = postSnapshot.getValue(String.class);
                                        phoneNumberString += phoneNumber + ", ";
                                    }
                                    phoneNumberString = phoneNumberString.trim();
                                    phoneNumberString = phoneNumberString.substring(0, phoneNumberString.length() - 1);
                                    String add_pho = address.getText() + "\n\n" + phoneNumberString;
                                    address.setText(add_pho);
                                }

                                if (schoolSettings.isShowLocationAndContact()) {
                                    contactInfoLayout.setVisibility(View.VISIBLE);
                                } else {
                                    contactInfoLayout.setVisibility(View.GONE);
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference().child("School External Examination Average Performance").child(schoolID);
                                mDatabaseReference.limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            eEAPErrorLayout.setVisibility(View.GONE);
                                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                EEAP eEAP = postSnapshot.getValue(EEAP.class);
                                                eEAPList.add(eEAP);
                                            }
                                        } else {
                                            eEAPErrorLayout.setVisibility(View.VISIBLE);
                                            eEAPErrorText.setText(schoolName + " hasn't uploaded any external examination average performance yet");
                                        }

                                        int counter = 0;
                                        for (EEAP eEAP: eEAPList) {
                                            String score = eEAP.getAverage() + "%";
                                            int scoreInt = Integer.parseInt(eEAP.getAverage());
                                            String color = "#" + eEAP.getColor();
                                            eEAPTextList.get(counter).setText(eEAP.getExamName());
                                            eEAPScoreList.get(counter).setText(score);
                                            eEAPProgressBarList.get(counter).setProgress(scoreInt);
                                            counter++;
                                        }

                                        if (eEAPList.size() == 2) {
                                            eEAPLinearLayoutList.get(2).setVisibility(View.GONE);
                                        } else if (eEAPList.size() == 1) {
                                            eEAPLinearLayoutList.get(1).setVisibility(View.GONE);
                                            eEAPLinearLayoutList.get(2).setVisibility(View.GONE);
                                        } else if (eEAPList.size() == 0) {
                                            eEAPLinearLayoutList.get(0).setVisibility(View.GONE);
                                            eEAPLinearLayoutList.get(1).setVisibility(View.GONE);
                                            eEAPLinearLayoutList.get(2).setVisibility(View.GONE);
                                        }

                                        if (schoolSettings.isShowExternalExaminationAveragePerformance()) {
                                            eeapLayout.setVisibility(View.VISIBLE);
                                        } else {
                                            eeapLayout.setVisibility(View.GONE);
                                        }

                                        mDatabaseReference = mFirebaseDatabase.getReference().child("School Awards").child(schoolID);
                                        mDatabaseReference.limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    awardsErrorLayout.setVisibility(View.GONE);
                                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                        Award award = postSnapshot.getValue(Award.class);
                                                        awardList.add(award);
                                                    }
                                                } else {
                                                    awardsErrorLayout.setVisibility(View.VISIBLE);
                                                    awardsErrorText.setText(schoolName + " hasn't uploaded any awards yet");
                                                }

                                                int counter = 0;
                                                for (Award award: awardList) {
                                                    awardsTextList.get(counter).setText(award.getAwardName());
                                                    awardsYearList.get(counter).setText(award.getAwardYear());
                                                    counter++;
                                                }

                                                if (awardList.size() == 2) {
                                                    awardsLinearLayoutList.get(2).setVisibility(View.GONE);
                                                } else if (awardList.size() == 1) {
                                                    awardsLinearLayoutList.get(1).setVisibility(View.GONE);
                                                    awardsLinearLayoutList.get(2).setVisibility(View.GONE);
                                                } else if (awardList.size() == 0) {
                                                    awardsLinearLayoutList.get(0).setVisibility(View.GONE);
                                                    awardsLinearLayoutList.get(1).setVisibility(View.GONE);
                                                    awardsLinearLayoutList.get(2).setVisibility(View.GONE);
                                                }

                                                if (schoolSettings.isShowAwards()) {
                                                    awardsLayout.setVisibility(View.VISIBLE);
                                                } else {
                                                    awardsLayout.setVisibility(View.GONE);
                                                }

                                                mDatabaseReference = mFirebaseDatabase.getReference().child("School Notable Alumni").child(schoolID);
                                                mDatabaseReference.limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            notableAlumniErrorLayout.setVisibility(View.GONE);
                                                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                                NotableAlumni notableAlumni = postSnapshot.getValue(NotableAlumni.class);
                                                                notableAlumniList.add(notableAlumni);
                                                            }
                                                        } else {
                                                            notableAlumniErrorLayout.setVisibility(View.VISIBLE);
                                                            notableAlumniErrorText.setText(schoolName + " hasn't uploaded any notable alum yet");
                                                        }

                                                        int counter = 0;
                                                        for (NotableAlumni notableAlumni: notableAlumniList) {
                                                            String set_note = notableAlumni.getSet() + " - " + notableAlumni.getNote();
                                                            String name = notableAlumni.getName().trim();
                                                            String profilePictureURL = notableAlumni.getProfilePictureURL();

                                                            Drawable textDrawable;
                                                            if (!name.isEmpty()) {
                                                                String[] nameArray = name.replaceAll("\\s+", " ").trim().split(" ");
                                                                if (nameArray.length == 1) {
                                                                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 45);
                                                                } else {
                                                                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 45);
                                                                }
                                                                notableAlumniImageList.get(counter).setImageDrawable(textDrawable);
                                                            } else {
                                                                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 45);
                                                            }

                                                            if (!profilePictureURL.isEmpty()) {
                                                                Glide.with(context)
                                                                        .load(profilePictureURL)
                                                                        .placeholder(textDrawable)
                                                                        .error(textDrawable)
                                                                        .centerCrop()
                                                                        .bitmapTransform(new CropCircleTransformation(context))
                                                                        .into(notableAlumniImageList.get(counter));
                                                            }

                                                            notableAlumniTextList.get(counter).setText(notableAlumni.getName());
                                                            notableAlumniSetList.get(counter).setText(set_note);
                                                            counter++;
                                                        }

                                                        if (notableAlumniList.size() == 2) {
                                                            notableAlumniLinearLayoutList.get(2).setVisibility(View.GONE);
                                                        } else if (notableAlumniList.size() == 1) {
                                                            notableAlumniLinearLayoutList.get(1).setVisibility(View.GONE);
                                                            notableAlumniLinearLayoutList.get(2).setVisibility(View.GONE);
                                                        } else if (notableAlumniList.size() == 0) {
                                                            notableAlumniLinearLayoutList.get(0).setVisibility(View.GONE);
                                                            notableAlumniLinearLayoutList.get(1).setVisibility(View.GONE);
                                                            notableAlumniLinearLayoutList.get(2).setVisibility(View.GONE);
                                                        }

                                                        if (schoolSettings.isShowNotableAlumni()) {
                                                            notableAlumniLayout.setVisibility(View.VISIBLE);
                                                        } else {
                                                            notableAlumniLayout.setVisibility(View.GONE);
                                                        }

                                                        mDatabaseReference = mFirebaseDatabase.getReference().child("School Top Five Colleges").child(schoolID);
                                                        mDatabaseReference.limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    collegeErrorLayout.setVisibility(View.GONE);
                                                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                                        String college = postSnapshot.getValue(String.class);
                                                                        collegeStringList.add(college);
                                                                    }
                                                                } else {
                                                                    collegeErrorLayout.setVisibility(View.VISIBLE);
                                                                    collegeErrorText.setText(schoolName + " hasn't uploaded any colleges or universities yet");
                                                                }

                                                                int counter = 0;
                                                                for (String college: collegeStringList) {
                                                                    collegeList.get(counter).setText(college);
                                                                    Drawable textDrawable;
                                                                    if (!college.trim().isEmpty()) {
                                                                        String[] nameArray = college.trim().replaceAll("\\s+", " ").trim().split(" ");
                                                                        textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 45);
                                                                    } else {
                                                                        textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 45);
                                                                    }
                                                                    collegePicList.get(counter).setImageDrawable(textDrawable);

                                                                    counter++;
                                                                }

                                                                if (collegeStringList.size() == 4) {
                                                                    collegeLinearLayoutList.get(4).setVisibility(View.GONE);
                                                                } else if (collegeStringList.size() == 3) {
                                                                    collegeLinearLayoutList.get(3).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(4).setVisibility(View.GONE);
                                                                } else if (collegeStringList.size() == 2) {
                                                                    collegeLinearLayoutList.get(2).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(3).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(4).setVisibility(View.GONE);
                                                                } else if (collegeStringList.size() == 1) {
                                                                    collegeLinearLayoutList.get(1).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(2).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(3).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(4).setVisibility(View.GONE);
                                                                } else if (collegeStringList.size() == 0) {
                                                                    collegeLinearLayoutList.get(0).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(1).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(2).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(3).setVisibility(View.GONE);
                                                                    collegeLinearLayoutList.get(4).setVisibility(View.GONE);
                                                                }

                                                                if (schoolSettings.isShowTop5CollegesAndUniversities()) {
                                                                    collegeLayout.setVisibility(View.VISIBLE);
                                                                } else {
                                                                    collegeLayout.setVisibility(View.GONE);
                                                                }

                                                                loadConnectionStatus();
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

    private void loadConnectionStatus() {
        if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
            mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(schoolID);
            mDatabaseReference.orderByChild("status").equalTo("Pending").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pendingIncomingRequestKey.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            pendingIncomingRequestKey.add(postSnapshot.getKey());
                        }
                    }

                    mDatabaseReference = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid()).child(schoolID);
                    mDatabaseReference.orderByChild("status").equalTo("Pending").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            pendingOutgoingRequestKey.clear();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    pendingOutgoingRequestKey.add(postSnapshot.getKey());
                                }
                            }

                            if (pendingOutgoingRequestKey.size() > 0) {
                                connect.setText("Revoke");
                                connect.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
                                connect.setTextColor(ContextCompat.getColor(context, R.color.black));
                            }

                            loadSchoolIEPA();
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
        } else {
            loadSchoolIEPA();
        }
    }

    private void loadSchoolIEPA() {
        schoolTotalScore = 0.0;
        schoolAverageScore = 0.0;
        schoolCounter = 0;
        classesWithResults = new ArrayList<>();
        totalChildrenCount = 0;
        counter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("School Class").child(schoolID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int classCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot:  dataSnapshot.getChildren()) {
                        final String classID = postSnapshot.getKey();

                        normalizedAverageList = new ArrayList<>();
//                        counter = 0;
                        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordClass").child(classID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                                    totalChildrenCount += childrenCount;
                                    schoolCounter++;

                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        String subject_year_term = postSnapshot.getKey();
                                        String yearTermKey = subject_year_term.split("_")[1] + "_" + subject_year_term.split("_")[2];

                                        if (yearTermKey.equals(year_term)) {
                                            if (!classesWithResults.contains(classID)) {
                                                classesWithResults.add(classID);
                                            }
                                            mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordClass").child(classID).child(subject_year_term);
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

                                                    if (counter == totalChildrenCount) {
                                                        double average = 0.0;
                                                        double total = 0.0;

                                                        if (normalizedAverageList.size() > 0) {
                                                            for (Double normAvg: normalizedAverageList) {
                                                                total += normAvg;
                                                            }
                                                            average = total / normalizedAverageList.size();
                                                        }
                                                        schoolTotalScore += average;
//                                                        schoolCounter++;
                                                        counter = 0;
                                                        normalizedAverageList.clear();

                                                        if (schoolCounter == classCount) {
                                                            if (classesWithResults.size() > 0) {
                                                                schoolAverageScore = schoolTotalScore / classesWithResults.size();
                                                            } else {
                                                                schoolAverageScore = schoolTotalScore / schoolCounter;
                                                            }
                                                            String schoolAverageScoreString = String.valueOf((int) schoolAverageScore) + "%";
                                                            averageAcademicPerformanceInInternalExams.setText(schoolAverageScoreString);
                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                            superLayout.setVisibility(View.VISIBLE);
                                                            progressLayout.setVisibility(View.GONE);
                                                            errorLayout.setVisibility(View.GONE);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        } else {
                                            counter++;

                                            if (counter == totalChildrenCount) {
                                                double average = 0.0;
                                                double total = 0.0;

                                                if (normalizedAverageList.size() > 0) {
                                                    for (Double normAvg: normalizedAverageList) {
                                                        total += normAvg;
                                                    }
                                                    average = total / normalizedAverageList.size();
                                                }
                                                schoolTotalScore += average;
//                                                schoolCounter++;
                                                counter = 0;
                                                normalizedAverageList.clear();

                                                if (schoolCounter == classCount) {
                                                    if (classesWithResults.size() > 0) {
                                                        schoolAverageScore = schoolTotalScore / classesWithResults.size();
                                                    } else {
                                                        schoolAverageScore = schoolTotalScore / schoolCounter;
                                                    }
                                                    String schoolAverageScoreString = String.valueOf((int)schoolAverageScore) + "%";
                                                    averageAcademicPerformanceInInternalExams.setText(schoolAverageScoreString);
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    superLayout.setVisibility(View.VISIBLE);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    schoolCounter++;

                                    if (schoolCounter == classCount) {
                                        if (classesWithResults.size() > 0) {
                                            schoolAverageScore = schoolTotalScore / classesWithResults.size();
                                        } else {
                                            schoolAverageScore = schoolTotalScore / schoolCounter;
                                        }
                                        String schoolAverageScoreString = String.valueOf(schoolAverageScore) + "%";
                                        averageAcademicPerformanceInInternalExams.setText(schoolAverageScoreString);
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        superLayout.setVisibility(View.VISIBLE);
                                        progressLayout.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                } else {
                    averageAcademicPerformanceInInternalExams.setText("0%");
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.VISIBLE);
                }

                if (schoolSettings.isShowAverageInternalExamsPerformance()) {
                    termAveragePerformanceInInternalExamsLayout.setVisibility(View.VISIBLE);
                } else {
                    termAveragePerformanceInInternalExamsLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        progressLayout.setVisibility(View.GONE);
//        errorLayout.setVisibility(View.GONE);
//        mySwipeRefreshLayout.setRefreshing(false);
//        superLayout.setVisibility(View.VISIBLE);
    }

    private void connect() {
        if (pendingOutgoingRequestKey.size() > 0) {
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
//                        dialog.getWindow().setLayout((19 * width) / 20, RecyclerView.LayoutParams.WRAP_CONTENT);

            String messageString = "Do you want to revoke your request to connect to " + "<b>" + schoolName + "</b>" + "?";
            message.setText(Html.fromHtml(messageString));

            action.setText("Revoke");

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CustomProgressDialogOne customProgressDialogOne = new CustomProgressDialogOne(context);
                    customProgressDialogOne.show();

                    mDatabaseReference = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid()).child(schoolID);
                    mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                Map<String, Object> newRequestMap = new HashMap<String, Object>();
                                DatabaseReference newRef = mFirebaseDatabase.getReference();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String pendingRequestKey = postSnapshot.getKey();
                                    newRequestMap.put("Teacher To School Request Teacher/" + mFirebaseUser.getUid() + "/" + schoolID + "/" + pendingRequestKey + "/" + "status", "Revoked");
                                    newRequestMap.put("Teacher To School Request School/" + schoolID + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Revoked");
                                    newRequestMap.put("NotificationSchool/" + schoolID + "/" + pendingRequestKey, null);
                                    pendingOutgoingRequestKey.remove(pendingRequestKey);
                                }

                                newRef.updateChildren(newRequestMap);
                            }

                            connect.setText("Connect");
                            connect.setBackgroundResource(R.drawable.roundedbutton);
                            connect.setTextColor(Color.WHITE);
                            customProgressDialogOne.dismiss();
                            String message = "You've successfully revoked your request to connect to " + "<b>" + schoolName + "</b>" + "'s account.";
                            showDialogWithMessage(Html.fromHtml(message));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        } else if (pendingIncomingRequestKey.size() > 0) {
            final CustomProgressDialogOne customProgressDialogOne = new CustomProgressDialogOne(context);
            customProgressDialogOne.show();

            String time = Date.getDate();
            String sorttableTime = Date.convertToSortableDate(time);

            final Map<String, Object> newConnectionMap = new HashMap<String, Object>();
            final DatabaseReference newConnectionRef = mFirebaseDatabase.getReference();
            final String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(schoolID).push().getKey();
            final NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), schoolID, "School", "Teacher", time, sorttableTime, notificationPushID, "Connection", "", "", false);

            mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(schoolID);
            mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String pendingRequestKey = postSnapshot.getKey();
                            newConnectionMap.put("School To Teacher Request Teacher/" + mFirebaseUser.getUid() + "/" + schoolID + "/" + pendingRequestKey + "/" + "status", "Accepted");
                            newConnectionMap.put("School To Teacher Request School/" + schoolID + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Accepted");
                            pendingIncomingRequestKey.remove(pendingRequestKey);
                        }
                        newConnectionMap.put("NotificationSchool/" + schoolID + "/" + notificationPushID, notificationModel);
                        newConnectionMap.put("School Teacher/" + schoolID + "/" + mFirebaseUser.getUid(), true);
                        newConnectionMap.put("Teacher School/" + mFirebaseUser.getUid() + "/" + schoolID, true);
                    }

                    disconnect.setVisibility(View.VISIBLE);
                    connect.setVisibility(View.GONE);
                    newConnectionRef.updateChildren(newConnectionMap);
                    customProgressDialogOne.dismiss();

                    String message = "You've been successfully connected to " + "<b>" + schoolName + "</b>" + "'s account. You can now share messages with them and gain access to their classes, students and records";
                    showDialogWithMessage(Html.fromHtml(message));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            final CustomProgressDialogOne customProgressDialogOne = new CustomProgressDialogOne(context);
            customProgressDialogOne.show();

            mDatabaseReference = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid()).child(schoolID).push();
            String refKey = mDatabaseReference.getKey();

            String timeSent = Date.getDate();
            String sorttableTimeSent = Date.convertToSortableDate(timeSent);
            TeacherSchoolConnectionRequest teacherSchoolConnectionRequest = new TeacherSchoolConnectionRequest("Pending", timeSent, sorttableTimeSent, mFirebaseUser.getUid(), schoolID);
            NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), schoolID, "School", "Teacher", timeSent, sorttableTimeSent, refKey, "ConnectionRequest", "", "", false);

            Map<String, Object> newRequestMap = new HashMap<String, Object>();
            mDatabaseReference = mFirebaseDatabase.getReference();
            newRequestMap.put("Teacher To School Request Teacher/" + mFirebaseUser.getUid() + "/" + schoolID + "/" + refKey, teacherSchoolConnectionRequest);
            newRequestMap.put("Teacher To School Request School/" + schoolID + "/" + mFirebaseUser.getUid() + "/" + refKey, teacherSchoolConnectionRequest);
            newRequestMap.put("NotificationSchool/" + schoolID + "/" + refKey, notificationModel);

            mDatabaseReference.updateChildren(newRequestMap);
            connect.setText("Revoke");
            connect.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
            connect.setTextColor(ContextCompat.getColor(context, R.color.black));
            pendingOutgoingRequestKey.add(refKey);
            customProgressDialogOne.dismiss();
            String message = "Your request to connect to " + "<b>" + schoolName + "</b>" + "'s account has been sent to them. We'll notify you once they respond";
            showDialogWithMessage(Html.fromHtml(message));
        }
    }

    int disconnectionCounter = 0;
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
//      dialog.getWindow().setLayout((19 * width) / 20, RecyclerView.LayoutParams.WRAP_CONTENT);

        String messageString = "Disconnecting would restrict your access to all " + "<b>" + schoolName + "</b>" + "'s information, including class and " +
                "student information. To regain access, you'll need to send a new request. Do you wish to disconnect?";
        message.setText(Html.fromHtml(messageString));

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
                final CustomProgressDialogOne customProgressDialogOne = new CustomProgressDialogOne(context);
                customProgressDialogOne.show();
                disconnect.setEnabled(false);

                disconnectionCounter = 0;
                String time = Date.getDate();
                String sortableTime = Date.convertToSortableDate(time);

                final Map<String, Object> newDisconnectionMap = new HashMap<String, Object>();
                final DatabaseReference newDisconnectionRef = mFirebaseDatabase.getReference();
                String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(schoolID).push().getKey();
                String disconnectionKey = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid()).child(schoolID).push().getKey();
                TeacherSchoolConnectionRequest teacherSchoolConnectionRequest = new TeacherSchoolConnectionRequest("Disconnected", time, sortableTime, mFirebaseUser.getUid(), schoolID);
                NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), schoolID, "School", "Teacher", time, sortableTime, notificationPushID, "Disconnection", "", "", false);

                newDisconnectionMap.put("Teacher School/" + mFirebaseUser.getUid() + "/" + schoolID, null);
                newDisconnectionMap.put("School Teacher/" + schoolID + "/" + mFirebaseUser.getUid(), null);
                newDisconnectionMap.put("Teacher To School Request Teacher/" + mFirebaseUser.getUid() + "/" + schoolID + "/" + disconnectionKey, teacherSchoolConnectionRequest);
                newDisconnectionMap.put("Teacher To School Request School/" + schoolID + "/" + mFirebaseUser.getUid() + "/" + disconnectionKey, teacherSchoolConnectionRequest);
                newDisconnectionMap.put("NotificationSchool/" + schoolID + "/" + notificationPushID, notificationModel);

                mDatabaseReference = mFirebaseDatabase.getReference().child("School Class").child(schoolID);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int childrenCount = (int) dataSnapshot.getChildrenCount();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String classID = postSnapshot.getKey();
                                newDisconnectionMap.put("Teacher Class/" + mFirebaseUser.getUid() + "/" + classID, null);
                                newDisconnectionMap.put("Class Teacher/" + classID + "/" + mFirebaseUser.getUid(), null);

                                mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(classID);
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        disconnectionCounter++;
                                        if (dataSnapshot.exists()) {
                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                            String classTeacher = classInstance.getClassTeacher();
                                            if (mFirebaseUser.getUid().equals(classTeacher)) {
                                                newDisconnectionMap.put("Class/" + classID + "/classTeacher", null);
                                            }
                                        }

                                        if (disconnectionCounter == childrenCount) {
                                            disconnect.setVisibility(View.GONE);
                                            connect.setVisibility(View.VISIBLE);
                                            newDisconnectionRef.updateChildren(newDisconnectionMap);
                                            dialog.dismiss();
                                            customProgressDialogOne.dismiss();
                                            String message = "You've been successfully disconnected from " + "<b>" + schoolName + "</b>" + "'s account. You will no longer have access to or receive notifications from their account. To reconnect, use the search button to send a fresh connection request";
                                            showDialogWithMessage(Html.fromHtml(message));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
                    Intent i = new Intent(this, ParentMainActivityTwo.class);
                    startActivity(i);
                } else if (parentActivity.equals("Teacher")) {
                    Intent i = new Intent(this, TeacherMainActivityTwo.class);
                    startActivity(i);
                }
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                Intent i = new Intent(this, ParentMainActivityTwo.class);
                startActivity(i);
            } else if (parentActivity.equals("Teacher")) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
                startActivity(i);
            }
        }
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

    void showDialogWithMessage (Spanned messageString) {
        final Dialog dialog = new Dialog(context);
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
}
