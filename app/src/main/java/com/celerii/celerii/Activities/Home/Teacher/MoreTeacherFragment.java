package com.celerii.celerii.Activities.Home.Teacher;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.ELibrary.TeacherELibraryHomeActivity;
import com.celerii.celerii.Activities.Events.EventsRowActivity;
import com.celerii.celerii.Activities.Newsletters.NewsletterRowActivity;
import com.celerii.celerii.Activities.Profiles.ClassProfileActivity;
import com.celerii.celerii.Activities.Profiles.TeacherProfileOneActivity;
import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.Activities.Settings.SettingsActivityTeacher;
import com.celerii.celerii.Activities.StudentAttendance.TeacherAttendanceActivity;
import com.celerii.celerii.Activities.StudentAttendance.TeacherTakeAttendanceActivity;
import com.celerii.celerii.Activities.StudentPerformance.EnterResultsActivity;
import com.celerii.celerii.Activities.StudentPerformance.History.StudentAcademicHistoryActivity;
import com.celerii.celerii.Activities.TeacherPerformance.TeacherPerformanceRowActivity;
import com.celerii.celerii.Activities.Timetable.TeacherTimetableActivity;
import com.celerii.celerii.Activities.Utility.SwitchActivityTeacherParent;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.MoreTeacherAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.LogoutProtocol;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.MoreTeacherHeaderModel;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Student;
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
import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreTeacherFragment extends Fragment {
    Context context;

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private ArrayList<Class> moreTeachersModelList;
    private MoreTeacherHeaderModel moreHeader;
    public RecyclerView recyclerView;
    public MoreTeacherAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SharedPreferencesManager sharedPreferencesManager;

    SwipeRefreshLayout mySwipeRefreshLayout;
    TextView teacherName, email;
    ImageView teacherProfilePic;
    LinearLayout profilePictureLayout, noClassLabel;
    Button searchButton;
    AHBottomNavigation bottomNavigation;

    LinearLayout profileLayout, attendanceRecordsLayout, attendanceLayout, timetableLayout, eLibraryLayout, enterClassResultLayout, personalPerformanceLayout, studentPerformanceLayout, eventsLayout, newslettersLayout,
            settingsLayout, switchAccountLayout, logoutLayout;
    TextView profile, attendanceRecords, attendance, timetable, eLibrary, enterClassResult, personalPerformance, studentPerformance, events, newsletters, settings, switchAccount, logout;
    TextView eventsBadge, newslettersBadge;
    ImageView profileMarker, attendanceRecordsMarker, attendanceMarker, timetableMarker, eLibraryMarker, enterClassResultMarker, personalPerformanceMarker, studentPerformanceMarker, eventsMarker, newslettersMarker,
            settingsMarker, switchAccountMarker, logoutMarker;

    ArrayList<String> classesFirebase = new ArrayList<>();
    ArrayList<String> childrenFirebase = new ArrayList<>();
    private static ArrayList<Student> myChildren = new ArrayList<>();
    private static ArrayList<Class> myClasses = new ArrayList<>();
    private static ArrayList<String> childrenKeyHolder = new ArrayList<>();
    private static ArrayList<String> classesKeyHolder = new ArrayList<>();
    int classesCounter = 0;
    int childrenCounter = 0;

    String featureUseKey = "";
    String featureName = "More Teacher";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public MoreTeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more_teacher, container, false);
        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(getContext());

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = mAuth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        teacherName = (TextView) view.findViewById(R.id.myprofilename);
        email = (TextView) view.findViewById(R.id.email);
        teacherProfilePic = (ImageView) view.findViewById(R.id.myprofileimage);
        profilePictureLayout = (LinearLayout) view.findViewById(R.id.profilepicturelayout);
        noClassLabel = (LinearLayout) view.findViewById(R.id.noclasslabel);
        searchButton = (Button) view.findViewById(R.id.searchbutton);

        TeacherMainActivityTwo activity = (TeacherMainActivityTwo) getActivity();
        bottomNavigation = activity.getData();

        profileLayout = (LinearLayout) view.findViewById(R.id.profileLayout);
        attendanceLayout = (LinearLayout) view.findViewById(R.id.attendanceLayout);
        attendanceRecordsLayout = (LinearLayout) view.findViewById(R.id.attendancerecordsLayout);
        timetableLayout = (LinearLayout) view.findViewById(R.id.timetableLayout);
        eLibraryLayout = (LinearLayout) view.findViewById(R.id.elibraryLayout);
        enterClassResultLayout = (LinearLayout) view.findViewById(R.id.enterclassresultLayout);
        personalPerformanceLayout = (LinearLayout) view.findViewById(R.id.personalperformanceLayout);
        studentPerformanceLayout = (LinearLayout) view.findViewById(R.id.studentperformanceLayout);
        eventsLayout = (LinearLayout) view.findViewById(R.id.eventsLayout);
        newslettersLayout = (LinearLayout) view.findViewById(R.id.newslettersLayout);
        settingsLayout = (LinearLayout) view.findViewById(R.id.settingsLayout);
        switchAccountLayout = (LinearLayout) view.findViewById(R.id.switchaccountLayout);
        logoutLayout = (LinearLayout) view.findViewById(R.id.logoutLayout);

        profile = (TextView) view.findViewById(R.id.profile);
        attendance = (TextView) view.findViewById(R.id.attendance);
        attendanceRecords = (TextView) view.findViewById(R.id.attendancerecords);
        timetable = (TextView) view.findViewById(R.id.timetable);
        eLibrary = (TextView) view.findViewById(R.id.elibrary);
        enterClassResult = (TextView) view.findViewById(R.id.enterclassresult);
        personalPerformance = (TextView) view.findViewById(R.id.personalperformance);
        studentPerformance = (TextView) view.findViewById(R.id.studentperformance);
        events = (TextView) view.findViewById(R.id.events);
        newsletters = (TextView) view.findViewById(R.id.newsletters);
        settings = (TextView) view.findViewById(R.id.settings);
        switchAccount = (TextView) view.findViewById(R.id.switchaccount);
        logout = (TextView) view.findViewById(R.id.logout);

        eventsBadge = (TextView) view.findViewById(R.id.eventsbadge);
        newslettersBadge = (TextView) view.findViewById(R.id.newslettersbadge);

        profileMarker = (ImageView) view.findViewById(R.id.profilemarker);
        attendanceMarker = (ImageView) view.findViewById(R.id.attendancemarker);
        attendanceRecordsMarker = (ImageView) view.findViewById(R.id.attendancerecordsmarker);
        timetableMarker = (ImageView) view.findViewById(R.id.timetablemarker);
        eLibraryMarker = (ImageView) view.findViewById(R.id.elibrarymarker);
        enterClassResultMarker = (ImageView) view.findViewById(R.id.enterclassresultmarker);
        personalPerformanceMarker = (ImageView) view.findViewById(R.id.personalperformancemarker);
        studentPerformanceMarker = (ImageView) view.findViewById(R.id.studentperformancemarker);
        eventsMarker = (ImageView) view.findViewById(R.id.eventsmarker);
        newslettersMarker = (ImageView) view.findViewById(R.id.newslettersmarker);
//        settingsMarker = (ImageView) view.findViewById(R.id.settingsmarker);
        switchAccountMarker = (ImageView) view.findViewById(R.id.switchaccountmarker);
        logoutMarker = (ImageView) view.findViewById(R.id.logoutmarker);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        moreTeachersModelList = new ArrayList<>();
        moreHeader = new MoreTeacherHeaderModel("", "");
        loadDataFromSharedPreferences();
        mAdapter = new MoreTeacherAdapter(moreTeachersModelList, getContext(), this);
        loadBasicInfo();
        recyclerView.setAdapter(mAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadBasicInfo();
                    }
                }
        );

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Teachers").child(mFirebaseUser.getUid()).child("More");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (!notificationBadgeModel.getStatus()){
                        bottomNavigation.setNotification("", 4);
                    }
                } else {
                    bottomNavigation.setNotification("", 4);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadDataFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    sharedPreferencesManager.setMyFirstName(teacher.getFirstName());
                    sharedPreferencesManager.setMyLastName(teacher.getLastName());
                    sharedPreferencesManager.setMyPicURL(teacher.getProfilePicURL());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        classesCounter = 0;
        myClasses.clear();
//        mAdapter.notifyDataSetChanged();
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Class").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    myClasses.clear();
//                    mAdapter.notifyDataSetChanged();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        final String classKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                classesCounter++;
                                if (dataSnapshot.exists()){
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    classInstance.setID(dataSnapshot.getKey());
                                    myClasses.add(classInstance);
                                }

                                if (classesCounter == childrenCount) {
                                    sharedPreferencesManager.deleteMyClasses();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(myClasses);
                                    sharedPreferencesManager.setMyClasses(json);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    sharedPreferencesManager.deleteMyClasses();
                    sharedPreferencesManager.deleteActiveClass();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        childrenCounter = 0;
        myChildren.clear();
//        mAdapter.notifyDataSetChanged();
        mDatabaseReference = mFirebaseDatabase.getReference("Parents Students").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    myChildren.clear();
//                    mAdapter.notifyDataSetChanged();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        final String childKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                childrenCounter++;
                                if (dataSnapshot.exists()){
                                    Student childInstance = dataSnapshot.getValue(Student.class);
                                    childInstance.setStudentID(dataSnapshot.getKey());
                                    myChildren.add(childInstance);
                                }

                                if (childrenCounter == childrenCount) {
                                    sharedPreferencesManager.deleteMyChildren();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(myChildren);
                                    sharedPreferencesManager.setMyChildren(json);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    sharedPreferencesManager.deleteMyChildren();
                    sharedPreferencesManager.deleteActiveKid();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadDataFromSharedPreferences() {
        Gson gson = new Gson();
        ArrayList<Class> moreTeachersModelListLocal = new ArrayList<>();
        String myClassesJSON = sharedPreferencesManager.getMyClasses();
        Type type = new TypeToken<ArrayList<Class>>() {}.getType();
        moreTeachersModelListLocal = gson.fromJson(myClassesJSON, type);

        if (moreTeachersModelListLocal == null) {
            moreTeachersModelListLocal = new ArrayList<>();
        }

        moreTeachersModelList.clear();
        moreTeachersModelList.addAll(moreTeachersModelListLocal);

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        mySwipeRefreshLayout.setRefreshing(false);
        loadHeader();
        loadFooter();
    }

    private void loadBasicInfo() {
        if (mFirebaseUser == null) {
            return;
        }
//        UpdateDataFromFirebase.populateEssentials(context);
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    sharedPreferencesManager.setMyUserID(mFirebaseUser.getUid());
                    sharedPreferencesManager.setMyFirstName(teacher.getFirstName());
                    sharedPreferencesManager.setMyMiddleName(teacher.getMiddleName());
                    sharedPreferencesManager.setMyLastName(teacher.getLastName());
                    sharedPreferencesManager.setMyPicURL(teacher.getProfilePicURL());
                    sharedPreferencesManager.setMyPhoneNumber(teacher.getPhone());
                    sharedPreferencesManager.setMyGender(teacher.getGender());
                    sharedPreferencesManager.setMyRelationshipStatus(teacher.getMaritalStatus());
                    sharedPreferencesManager.setMyBio(teacher.getBio());
                }

                getMyParentInfo(mFirebaseUser.getUid());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMyParentInfo(final String myID) {
        if (mFirebaseUser == null) {
            return;
        }
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Parent parent = dataSnapshot.getValue(Parent.class);
                    sharedPreferencesManager.setMyOccupation(parent.getOccupation());
                }

                getMyChildren();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMyChildren() {
        if (mFirebaseUser == null) {
            return;
        }

        childrenCounter = 0;
        myChildren.clear();
        childrenKeyHolder.clear();
//        mAdapter.notifyDataSetChanged();
        final ArrayList<String> childrenKeyList = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    myChildren.clear();
//                    mAdapter.notifyDataSetChanged();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                        final String childKey = postSnapShot.getKey();
                        childrenKeyList.add(childKey);
                    }

                    getMyChildrenRecursive(0, childrenKeyList);
                } else {
                    sharedPreferencesManager.deleteMyChildren();
                    getMyClasses();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMyChildrenRecursive(final Integer index, final ArrayList<String> childrenKeyList) {
        String childKey = childrenKeyList.get(index);
        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childrenCounter++;
                if (dataSnapshot.exists()) {
                    Student childInstance = dataSnapshot.getValue(Student.class);
                    childInstance.setStudentID(dataSnapshot.getKey());
                    if (!childrenKeyHolder.contains(dataSnapshot.getKey())) {
                        childrenKeyHolder.add(dataSnapshot.getKey());
                        myChildren.add(childInstance);
                    }
                }

                if (index == childrenKeyList.size() - 1) {
                    sharedPreferencesManager.deleteMyChildren();
                    Gson gson = new Gson();
                    String json = gson.toJson(myChildren);
                    sharedPreferencesManager.setMyChildren(json);
                    getMyClasses();
                } else {
                    getMyChildrenRecursive(index + 1, childrenKeyList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMyClasses() {
        if (mFirebaseUser == null) {
            return;
        }

        classesCounter = 0;
        myClasses.clear();
        classesKeyHolder.clear();
        mAdapter.notifyDataSetChanged();
        final ArrayList<String> classesKeyList = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Class").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int classesCount = (int) dataSnapshot.getChildrenCount();
                    myClasses.clear();
                    mAdapter.notifyDataSetChanged();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                        final String classKey = postSnapShot.getKey();
                        classesKeyList.add(classKey);
                    }

                    getMyClassesRecursive(0, classesKeyList);
                } else {
                    sharedPreferencesManager.deleteMyClasses();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMyClassesRecursive(final Integer index, final ArrayList<String> classesKeyList) {
        String classKey = classesKeyList.get(index);
        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classesCounter++;
                if (dataSnapshot.exists()) {
                    Class classInstance = dataSnapshot.getValue(Class.class);
                    classInstance.setID(dataSnapshot.getKey());
                    if (!classesKeyHolder.contains(dataSnapshot.getKey())) {
                        classesKeyHolder.add(dataSnapshot.getKey());
                        myClasses.add(classInstance);
                    }
                }

                if (index == classesKeyList.size() - 1) {
                    sharedPreferencesManager.deleteMyClasses();
                    Gson gson = new Gson();
                    String json = gson.toJson(myClasses);
                    sharedPreferencesManager.setMyClasses(json);
                    loadDataFromSharedPreferences();
                } else {
                    getMyClassesRecursive(index + 1, classesKeyList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadHeader() {
        teacherName.setText(sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName());
        email.setText(mFirebaseUser.getEmail());
        profilePictureLayout.setClipToOutline(true);

        if (moreTeachersModelList.size() <= 0){
            noClassLabel.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noClassLabel.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        Drawable textDrawable;
        String myName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        if (!myName.trim().isEmpty()) {
            String[] nameArray = myName.replaceAll("\\s+", " ").trim().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], 100);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], nameArray[1], 100);
            }
            teacherProfilePic.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 100);
        }

        if (!sharedPreferencesManager.getMyPicURL().isEmpty()) {
            try {
                Glide.with(context)
                        .load(sharedPreferencesManager.getMyPicURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(teacherProfilePic);
            } catch (Exception e) {
                return;
            }
        }

//        editMyProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent I = new Intent(context, EditTeacherProfileActivity.class);
//                Bundle b = new Bundle();
//                b.putString("id", mAuth.getCurrentUser().getUid());
//                context.startActivity(I);
//            }
//        });

        teacherName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, TeacherProfileOneActivity.class);
                Bundle b = new Bundle();
                b.putString("ID", mAuth.getCurrentUser().getUid());
                I.putExtras(b);
                context.startActivity(I);
            }
        });

        teacherProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, TeacherProfileOneActivity.class);
                Bundle b = new Bundle();
                b.putString("ID", mAuth.getCurrentUser().getUid());
                I.putExtras(b);
                context.startActivity(I);
            }
        });
    }

    public void loadFooter() {
        loadUpBadgesAndMarkers();

        String prf = "Class Profile";
        String att = "Take Class Attendance";
        String atr = "Class Attendance Records";
        String ecr = "Enter Class Results";
        String ttb = "My TimeTable";
        String elb = "E Library";
        String mpef = "My Performance Analytics";
        String cpef = "Class Academic Records";
        String evt = "Events";
        String nws = "Newsletters";
        String set = "Settings";
        String swt = "Switch to Parenting";
        String lgt = "Logout";

        String activeClass = null;
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
                }
            }
        } else {
            Boolean activeClassExist = false;
            Gson gson = new Gson();
            Type type = new TypeToken<Class>() {}.getType();
            Class activeClassModel = gson.fromJson(activeClass, type);

            String myClassesJSON = sharedPreferencesManager.getMyClasses();
            type = new TypeToken<ArrayList<Class>>() {}.getType();
            ArrayList<Class> myClasses = gson.fromJson(myClassesJSON, type);

            if (myClasses != null) {
                if (myClasses.size() > 0)  {
                    for (Class classInstance : myClasses) {
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
                        }
                    }
                } else {
                    activeClass = null;
                    sharedPreferencesManager.deleteActiveClass();
                }
            } else {
                activeClass = null;
                sharedPreferencesManager.deleteActiveClass();
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Class>() {}.getType();
        final Class activeClassModel = gson.fromJson(activeClass, type);

        if (activeClass != null) {
            String myClassesJSON = sharedPreferencesManager.getMyClasses();
            type = new TypeToken<ArrayList<Class>>() {}.getType();
            ArrayList<Class> myClasses = gson.fromJson(myClassesJSON, type);
            ArrayList<String> myClassesString = new ArrayList<>();

            if (myClasses != null) {
                if (myClasses.size() > 0) {
                    for (Class classInstance : myClasses) {
                        myClassesString.add(gson.toJson(classInstance));
                    }

                    final int indexOfActiveClass = myClassesString.indexOf(activeClass);
                    if (indexOfActiveClass < myClassesString.size()) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(indexOfActiveClass);
                            }
                        }, 100);
                    }

                    String className = activeClassModel.getClassName();
                    prf = className + "'s Profile";
                    att = "Take " + className + "'s Attendance";
                    atr = "View " + className + "'s Attendance Records";
                    ecr = "Enter Results for " + className ;
                    cpef = "View " + className + "'s Academic Records";
                }
            }
        }

        profile.setText(prf);
        attendance.setText(att);
        attendanceRecords.setText(atr);
        timetable.setText(ttb);
        eLibrary.setText(elb);
        enterClassResult.setText(ecr);
        personalPerformance.setText(mpef);
        studentPerformance.setText(cpef);
        events.setText(evt);
        newsletters.setText(nws);
        settings.setText(set);
        switchAccount.setText(swt);
        logout.setText(lgt);

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, ClassProfileActivity.class);
                Bundle bundle = new Bundle();

                String activeClassID = null;
                if (activeClassModel != null) {
                    Gson gson = new Gson();
                    activeClassID = gson.toJson(activeClassModel);
                }

                bundle.putString("ClassID", activeClassID);
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
        attendanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent I = new Intent(context, TeacherAttendanceActivity.class);
                Intent I = new Intent(context, TeacherTakeAttendanceActivity.class);
                context.startActivity(I);
            }
        });
        attendanceRecordsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, TeacherAttendanceActivity.class);
                context.startActivity(I);
            }
        });
        timetableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, TeacherTimetableActivity.class);
                context.startActivity(I);
            }
        });
        eLibraryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, TeacherELibraryHomeActivity.class);
                context.startActivity(I);
            }
        });
        enterClassResultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, EnterResultsActivity.class);
                context.startActivity(I);
            }
        });
        personalPerformanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, TeacherPerformanceRowActivity.class);
                context.startActivity(I);
            }
        });
        studentPerformanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, StudentAcademicHistoryActivity.class);
                context.startActivity(I);
            }
        });
        eventsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, EventsRowActivity.class);
                context.startActivity(I);
            }
        });
        newslettersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, NewsletterRowActivity.class);
                context.startActivity(I);
            }
        });
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, SettingsActivityTeacher.class);
                context.startActivity(I);
            }
        });
        switchAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, SwitchActivityTeacherParent.class);
                context.startActivity(I);
                ((Activity)context).finish();
            }
        });
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Test how logging out without internet works
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "No Internet";
                    CustomToast.blueBackgroundToast(context, messageString);
                    return;
                }
                logoutConfirmation();
            }
        });

    }

    private void loadUpBadgesAndMarkers() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Teachers").child(mFirebaseUser.getUid()).child("Events").child("status");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean status = dataSnapshot.getValue(boolean.class);
                    if (status) {
                        eventsBadge.setVisibility(View.VISIBLE);
                    } else {
                        eventsBadge.setVisibility(View.GONE);
                    }
                } else {
                    eventsBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Teachers").child(mFirebaseUser.getUid()).child("Newsletter").child("status");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean status = dataSnapshot.getValue(boolean.class);
                    if (status) {
                        newslettersBadge.setVisibility(View.VISIBLE);
                    } else {
                        newslettersBadge.setVisibility(View.GONE);
                    }
                } else {
                    newslettersBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {

        loadDataFromSharedPreferences();
        loadBasicInfo();
        loadUpBadgesAndMarkers();
        DatabaseReference bottomNavBadgeRef = mFirebaseDatabase.getReference();
        HashMap<String, Object> bottomNavBadgeMap = new HashMap<String, Object>();
        NotificationBadgeModel notificationBadgeModel = new NotificationBadgeModel(false, 0);
        bottomNavBadgeMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/More", notificationBadgeModel);
        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);
        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
//        UpdateDataFromFirebase.populateEssentials(getContext());
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Gson gson = new Gson();
        String myClassesJSON;
        String activeClass = sharedPreferencesManager.getActiveClass();

        if (activeClass != null) {
            myClassesJSON = sharedPreferencesManager.getMyClasses();
            Type type = new TypeToken<ArrayList<Class>>() {}.getType();
            ArrayList<Class> myClasses = gson.fromJson(myClassesJSON, type);
            ArrayList<String> myClassesString = new ArrayList<>();

            if (myClasses != null) {
                if (myClasses.size() > 0) {
                    for (Class classInstance : myClasses) {
                        myClassesString.add(gson.toJson(classInstance));
                    }

                    final int indexOfActiveClass = myClassesString.indexOf(activeClass);
                    if (indexOfActiveClass < myClassesString.size()) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(indexOfActiveClass);
                            }
                        }, 100);
                    }
                }
            }
        }
    }

    private void logoutConfirmation() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_binary_selection_dialog_with_cancel);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button delete = (Button) dialog.findViewById(R.id.optionone);
        Button cancel = (Button) dialog.findViewById(R.id.optiontwo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        message.setText("Are you sure you want to logout of Celerii?");

        delete.setText("Logout");
        cancel.setText("Cancel");

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutProtocol.logout(context, "You're being logged out");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
