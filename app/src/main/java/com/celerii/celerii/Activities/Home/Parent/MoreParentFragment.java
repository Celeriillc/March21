package com.celerii.celerii.Activities.Home.Parent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.Events.EventsRowActivity;
import com.celerii.celerii.Activities.Newsletters.NewsletterRowActivity;
import com.celerii.celerii.Activities.Profiles.ParentProfileActivity;
import com.celerii.celerii.Activities.Profiles.StudentProfileActivity;
import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.Activities.Settings.SettingsActivityParent;
import com.celerii.celerii.Activities.StudentAttendance.ParentAttendanceActivity;
import com.celerii.celerii.Activities.StudentBehaviouralPerformance.BehaviouralResultActivity;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.Activities.Subscription.SubscriptionHomeActivity;
import com.celerii.celerii.Activities.Timetable.TeacherTimetableActivity;
import com.celerii.celerii.Activities.Utility.SwitchActivityParentTeacher;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.InboxAdapter;
import com.celerii.celerii.adapters.MoreParentsAdapter;
import com.celerii.celerii.adapters.ParentAttendanceRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.LogoutProtocol;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.MoreParentsHeaderModel;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.SubscriptionModel;
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
public class MoreParentFragment extends Fragment {
    Context context = getContext();

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private ArrayList<Student> moreParentsModelList;
    private MoreParentsHeaderModel moreHeader;
    public RecyclerView recyclerView;
    public MoreParentsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SharedPreferencesManager sharedPreferencesManager;
    ProgressBar progressBar;

    ArrayList<String> childrenFirebase = new ArrayList<>();
    ArrayList<String> classesFirebase = new ArrayList<>();
    private static ArrayList<Student> myChildren = new ArrayList<>();
    private static ArrayList<Class> myClasses = new ArrayList<>();
    private static ArrayList<String> childrenKeyHolder = new ArrayList<>();
    private static ArrayList<String> classesKeyHolder = new ArrayList<>();
    int classesCounter = 0;
    int childrenCounter = 0;

    SwipeRefreshLayout mySwipeRefreshLayout;
    TextView parentName, editMyProfile;
    ImageView parentProfilePic, subscriptionFlag, paymentFlag;
    LinearLayout profilePictureLayout, noKidLabel;
    Button searchButton;
    AHBottomNavigation bottomNavigation;

    LinearLayout profileLayout, subscriptionLayout, attendanceLayout, timetableLayout, performanceLayout, behaviouralPerformanceLayout, paymentLayout, eventsLayout, newslettersLayout,
            settingsLayout, switchAccountLayout, logoutLayout;
    TextView profile, subscription, attendance, timetable, performance, behaviouralPerformance, payment, events, newsletters, settings, switchAccount, logout;
    TextView profileBadge, subscriptionBadge, attendanceBadge, performanceBadge, behaviouralPerformanceBadge, paymentBadge, eventsBadge, newslettersBadge;
    TextView profileMarker, subscriptionMarker, attendanceMarker, timetableMarker, performanceMarker, behaviouralPerformanceMarker, paymentMarker, eventsMarker, newslettersMarker,
            settingsMarker, switchAccountMarker, logoutMarker;

    CoordinatorLayout coordinatorLayout;

    String featureUseKey = "";
    String featureName = "More Parent";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public MoreParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_parent, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(context);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = mAuth.getCurrentUser();
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        ParentMainActivityTwo activity = (ParentMainActivityTwo) getActivity();
        bottomNavigation = activity.getData();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        parentName = (TextView) view.findViewById(R.id.myprofilename);
        editMyProfile = (TextView) view.findViewById(R.id.editmyprofile);
        parentProfilePic = (ImageView) view.findViewById(R.id.myprofileimage);
        subscriptionFlag = (ImageView) view.findViewById(R.id.subscriptionflag);
        paymentFlag = (ImageView) view.findViewById(R.id.paymentflag);
        profilePictureLayout = (LinearLayout) view.findViewById(R.id.profilepicturelayout);
        noKidLabel = (LinearLayout) view.findViewById(R.id.nokidlabel);
        searchButton = (Button) view.findViewById(R.id.searchbutton);

        profileLayout = (LinearLayout) view.findViewById(R.id.profileLayout);
        subscriptionLayout = (LinearLayout) view.findViewById(R.id.subscriptionLayout);
        attendanceLayout = (LinearLayout) view.findViewById(R.id.attendanceLayout);
        timetableLayout = (LinearLayout) view.findViewById(R.id.timetableLayout);
        performanceLayout = (LinearLayout) view.findViewById(R.id.performanceLayout);
        behaviouralPerformanceLayout = (LinearLayout) view.findViewById(R.id.behaviouralperformanceLayout);
        paymentLayout = (LinearLayout) view.findViewById(R.id.paymentsLayout);
        eventsLayout = (LinearLayout) view.findViewById(R.id.eventsLayout);
        newslettersLayout = (LinearLayout) view.findViewById(R.id.newslettersLayout);
        settingsLayout = (LinearLayout) view.findViewById(R.id.settingsLayout);
        switchAccountLayout = (LinearLayout) view.findViewById(R.id.switchaccountLayout);
        logoutLayout = (LinearLayout) view.findViewById(R.id.logoutLayout);

        profile = (TextView) view.findViewById(R.id.profile);
        subscription = (TextView) view.findViewById(R.id.subscription);
        attendance = (TextView) view.findViewById(R.id.attendance);
        timetable = (TextView) view.findViewById(R.id.timetable);
        performance = (TextView) view.findViewById(R.id.performance);
        behaviouralPerformance = (TextView) view.findViewById(R.id.behaviouralperformance);
        payment = (TextView) view.findViewById(R.id.payments);
        events = (TextView) view.findViewById(R.id.events);
        newsletters = (TextView) view.findViewById(R.id.newsletters);
        settings = (TextView) view.findViewById(R.id.settings);
        switchAccount = (TextView) view.findViewById(R.id.switchaccount);
        logout = (TextView) view.findViewById(R.id.logout);

        profileBadge = (TextView) view.findViewById(R.id.profilebadge);
        subscriptionBadge = (TextView) view.findViewById(R.id.subscriptionbadge);
        attendanceBadge = (TextView) view.findViewById(R.id.attendancebadge);
        performanceBadge = (TextView) view.findViewById(R.id.performancebadge);
        behaviouralPerformanceBadge = (TextView) view.findViewById(R.id.behaviouralperformancebadge);
        paymentBadge = (TextView) view.findViewById(R.id.paymentsbadge);
        eventsBadge = (TextView) view.findViewById(R.id.eventsbadge);
        newslettersBadge = (TextView) view.findViewById(R.id.newsletterbadge);

        profileMarker = (TextView) view.findViewById(R.id.profilemarker);
        subscriptionMarker = (TextView) view.findViewById(R.id.subscriptionmarker);
        attendanceMarker = (TextView) view.findViewById(R.id.attendancemarker);
        timetableMarker = (TextView) view.findViewById(R.id.timetablemarker);
        performanceMarker = (TextView) view.findViewById(R.id.performancemarker);
        behaviouralPerformanceMarker = (TextView) view.findViewById(R.id.behaviouralperformancemarker);
        paymentMarker = (TextView) view.findViewById(R.id.paymentsmarker);
        eventsMarker = (TextView) view.findViewById(R.id.eventsmarker);
        newslettersMarker = (TextView) view.findViewById(R.id.newslettersmarker);
        settingsMarker = (TextView) view.findViewById(R.id.settingsmarker);
        switchAccountMarker = (TextView) view.findViewById(R.id.switchaccountmarker);
        logoutMarker = (TextView) view.findViewById(R.id.logoutmarker);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorlayout);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        moreParentsModelList = new ArrayList<>();
        loadDataFromSharedPreferences();
        mAdapter = new MoreParentsAdapter(moreParentsModelList, getContext(), this);
        loadBasicInfo();
        recyclerView.setAdapter(mAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ParentSearchActivity.class));
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

        String activeKidModelID;
        if (sharedPreferencesManager.getActiveKid() != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Student>() {}.getType();
            Student activeKidModel = gson.fromJson(sharedPreferencesManager.getActiveKid(), type);
            activeKidModelID = activeKidModel.getStudentID();
        } else {
            activeKidModelID = "";
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Parents").child(mFirebaseUser.getUid()).child(activeKidModelID).child("More");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (!notificationBadgeModel.getStatus()){
                        bottomNavigation.setNotification("", 3);
                    }
                } else {
                    bottomNavigation.setNotification("", 3);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadDataFromSharedPreferences() {
        Gson gson = new Gson();
        ArrayList<Student> moreParentsModelListLocal = new ArrayList<>();
        String myChildrenJSON = sharedPreferencesManager.getMyChildren();
        Type type = new TypeToken<ArrayList<Student>>() {}.getType();
        moreParentsModelListLocal = gson.fromJson(myChildrenJSON, type);

        if (moreParentsModelListLocal == null) {
            moreParentsModelListLocal = new ArrayList<>();
        }

        moreParentsModelList.clear();
        moreParentsModelList.addAll(moreParentsModelListLocal);

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

        mySwipeRefreshLayout.setRefreshing(false);
        loadHeader();
        loadFooter();
    }

    private void loadDataFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(mFirebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Parent parent = dataSnapshot.getValue(Parent.class);
                    sharedPreferencesManager.setMyFirstName(parent.getFirstName());
                    sharedPreferencesManager.setMyLastName(parent.getLastName());
                    sharedPreferencesManager.setMyPicURL(parent.getProfilePicURL());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        childrenCounter = 0;
        myChildren.clear();
        mDatabaseReference = mFirebaseDatabase.getReference("Parents Students").child(mFirebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    myChildren.clear();
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

        classesCounter = 0;
        myClasses.clear();
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Class").child(mFirebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    myClasses.clear();
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

                                if (childrenCounter == childrenCount) {
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
    }

    private void loadBasicInfo() {
        if (mFirebaseUser == null) {
            return;
        }
        UpdateDataFromFirebase.populateEssentials(context);
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
        final ArrayList<String> childrenKeyList = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    myChildren.clear();
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
        final ArrayList<String> classesKeyList = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Class").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int classesCount = (int) dataSnapshot.getChildrenCount();
                    myClasses.clear();
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

    void loadHeader() {
        parentName.setText(sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName());
        profilePictureLayout.setClipToOutline(true);

        if (moreParentsModelList.size() <= 0){
            noKidLabel.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noKidLabel.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        Drawable textDrawable;
        if (!sharedPreferencesManager.getMyFirstName().isEmpty() || !sharedPreferencesManager.getMyLastName().isEmpty()) {
            String[] nameArray = (sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName()).split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 100);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 100);
            }
            parentProfilePic.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
        }

        if (!sharedPreferencesManager.getMyPicURL().isEmpty()) {
            Glide.with(context)
                    .load(sharedPreferencesManager.getMyPicURL())
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(parentProfilePic);
        }

        parentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, ParentProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("parentID", mAuth.getCurrentUser().getUid());
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });

        parentProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, ParentProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("parentID", mAuth.getCurrentUser().getUid());
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });

//        editMyProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent I = new Intent(context, EditParentProfileActivity.class);
//                context.startActivity(I);
//            }
//        });
    }

    public void loadFooter() {
        loadUpBadgesAndMarkers();

        String prf = "Profile";
        String sub = "Subscription Status";
        String att = "Attendance";
        String ttb = "TimeTable";
        String pef = "Academic Performance";
        String bvr = "Behavioural Performance";
        String pmt = "Payments";
        String evt = "Events";
        String nws = "Newsletters";
        String set = "Settings";
        String swt = "Switch to Teaching";
        String lgt = "Logout";

        String activeKid = null;
        activeKid = sharedPreferencesManager.getActiveKid();

        if (activeKid == null) {
            Gson gson = new Gson();
            ArrayList<Student> myChildren = new ArrayList<>();
            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            Type type = new TypeToken<ArrayList<Student>>() {}.getType();
            myChildren = gson.fromJson(myChildrenJSON, type);

            if (myChildren != null) {
                if (myChildren.size() > 1) {
                    gson = new Gson();
                    activeKid = gson.toJson(myChildren.get(0));
                    sharedPreferencesManager.setActiveKid(activeKid);
                }
            }
        } else {
            Boolean activeKidExist = false;
            Gson gson = new Gson();
            Type type = new TypeToken<Student>() {}.getType();
            Student activeKidModel = gson.fromJson(activeKid, type);

            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            type = new TypeToken<ArrayList<Student>>() {}.getType();
            ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

            for (Student student: myChildren) {
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
                    if (myChildren.size() > 1) {
                        gson = new Gson();
                        activeKid = gson.toJson(myChildren.get(0));
                        sharedPreferencesManager.setActiveKid(activeKid);
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeKidModel = gson.fromJson(activeKid, type);

        if (activeKid != null){
            String firstName = activeKidModel.getFirstName();
            prf = firstName + "'s Profile";
            sub = firstName + "'s Subscription Status";
            att = firstName + "'s Attendance";
            ttb = firstName + "'s Timetable";
            pef = firstName + "'s Academic Performance";
            bvr = firstName + "'s Behavioural Performance";

            Boolean isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
            String subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationParents();
            type = new TypeToken<HashMap<String, ArrayList<SubscriptionModel>>>() {}.getType();
            HashMap<String, ArrayList<SubscriptionModel>> subscriptionModelMap = gson.fromJson(subscriptionModelJSON, type);
            SubscriptionModel subscriptionModel = new SubscriptionModel();
            if (subscriptionModelMap != null) {
                ArrayList<SubscriptionModel> subscriptionModelList = subscriptionModelMap.get(activeKidModel.getStudentID());
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
            Boolean isExpired = Date.compareDates(Date.getDate(), subscriptionModel.getExpiryDate());

            if (isOpenToAll) {
                subscriptionFlag.setVisibility(View.GONE);
            } else {
                if (!isExpired) {
                    subscriptionFlag.setVisibility(View.GONE);
                } else {
                    subscriptionFlag.setVisibility(View.VISIBLE);
                }
            }
        }

        profile.setText(prf);
        subscription.setText(sub);
        attendance.setText(att);
        timetable.setText(ttb);
        performance.setText(pef);
        behaviouralPerformance.setText(bvr);
        payment.setText(pmt);
        events.setText(evt);
        newsletters.setText(nws);
        settings.setText(set);
        switchAccount.setText(swt);
        logout.setText(lgt);

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, StudentProfileActivity.class);
                Bundle b = new Bundle();
                b.putString("childID", sharedPreferencesManager.getActiveKid());
                I.putExtras(b);
                context.startActivity(I);
            }
        });
        subscriptionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, SubscriptionHomeActivity.class);
                Bundle b = new Bundle();
                b.putString("Child ID", sharedPreferencesManager.getActiveKid());
                I.putExtras(b);
                context.startActivity(I);
            }
        });
        attendanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, ParentAttendanceActivity.class);
                Bundle b = new Bundle();
                b.putString("Child ID", sharedPreferencesManager.getActiveKid());
                I.putExtras(b);
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
        performanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, StudentPerformanceForParentsActivity.class);
                Bundle b = new Bundle();
                b.putString("Child ID", sharedPreferencesManager.getActiveKid());
                I.putExtras(b);
                context.startActivity(I);
            }
        });
        behaviouralPerformanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, BehaviouralResultActivity.class);
                Bundle b = new Bundle();
                b.putString("ChildID", sharedPreferencesManager.getActiveKid());
                I.putExtras(b);
                context.startActivity(I);
            }
        });
//        paymentLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent I = new Intent(context, StudentPerformanceForParentsActivity.class);
//                context.startActivity(I);
//            }
//        });
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
                Intent I = new Intent(context, SettingsActivityParent.class);
                context.startActivity(I);
            }
        });
        switchAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, SwitchActivityParentTeacher.class);
                context.startActivity(I);
                ((Activity)context).finish();
//                    loginTeacher("Teacher", ((FooterViewHolder) holder).coordinatorLayout);
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
                LogoutProtocol.logout(context, "You're being logged out");
            }
        });
    }

    private void loadUpBadgesAndMarkers() {
        if (sharedPreferencesManager.getActiveKid() != null){
            if (!sharedPreferencesManager.getActiveKid().equals("")) {
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {}.getType();
                Student activeKidModel = gson.fromJson(sharedPreferencesManager.getActiveKid(), type);
                String activeKidID = activeKidModel.getStudentID();

                mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(mFirebaseUser.getUid()).child(activeKidID).child("status");
                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            boolean status = dataSnapshot.getValue(boolean.class);
                            if (status) {
                                performanceBadge.setVisibility(View.VISIBLE);
                            } else {
                                performanceBadge.setVisibility(View.GONE);
                            }
                        } else {
                            performanceBadge.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabaseReference = mFirebaseDatabase.getReference().child("AttendanceParentNotification").child(mFirebaseUser.getUid()).child(activeKidID).child("status");
                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            boolean status = dataSnapshot.getValue(boolean.class);
                            if (status) {
                                attendanceBadge.setVisibility(View.VISIBLE);
                            } else {
                                attendanceBadge.setVisibility(View.GONE);
                            }
                        } else {
                            attendanceBadge.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecordParentNotification").child(mFirebaseUser.getUid()).child(activeKidID).child("status");
                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            boolean status = dataSnapshot.getValue(boolean.class);
                            if (status) {
                                behaviouralPerformanceBadge.setVisibility(View.VISIBLE);
                            } else {
                                behaviouralPerformanceBadge.setVisibility(View.GONE);
                            }
                        } else {
                            behaviouralPerformanceBadge.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Parents").child(mFirebaseUser.getUid()).child("Events").child("status");
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

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Parents").child(mFirebaseUser.getUid()).child("Newsletter").child("status");
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

        if (sharedPreferencesManager.getActiveKid() != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<Student>() {}.getType();
            Student activeKidModel = gson.fromJson(sharedPreferencesManager.getActiveKid(), type);
            String activeKidModelID = activeKidModel.getStudentID();

            DatabaseReference bottomNavBadgeRef = mFirebaseDatabase.getReference();
            HashMap<String, Object> bottomNavBadgeMap = new HashMap<String, Object>();
            NotificationBadgeModel notificationBadgeModel = new NotificationBadgeModel(false, 0);
            bottomNavBadgeMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/" + activeKidModelID + "/More", notificationBadgeModel);
            bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);
        }


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
}
