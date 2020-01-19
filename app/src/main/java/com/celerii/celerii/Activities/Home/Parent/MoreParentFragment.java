package com.celerii.celerii.Activities.Home.Parent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.EditProfiles.EditParentProfileActivity;
import com.celerii.celerii.Activities.Events.EventsRowActivity;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.Activities.Newsletters.NewsletterRowActivity;
import com.celerii.celerii.Activities.Profiles.ParentProfileActivity;
import com.celerii.celerii.Activities.Settings.SettingsActivityParent;
import com.celerii.celerii.Activities.StudentAttendance.ParentAttendanceActivity;
import com.celerii.celerii.Activities.StudentBehaviouralPerformance.BehaviouralResultActivity;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.Activities.Timetable.TeacherTimetableActivity;
import com.celerii.celerii.Activities.Utility.SwitchActivityParentTeacher;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.MoreParentsAdapter;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.MoreParentsHeaderModel;
import com.celerii.celerii.models.MoreParentsModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    private ArrayList<MoreParentsModel> moreParentsModelList;
    private MoreParentsHeaderModel moreHeader;
    public RecyclerView recyclerView;
    public MoreParentsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SharedPreferencesManager sharedPreferencesManager;
    ProgressBar progressBar;

    ArrayList<String> childrenFirebase = new ArrayList<>();
    ArrayList<String> classesFirebase = new ArrayList<>();

    TextView parentName, editMyProfile, noKidLabel;
    ImageView parentProfilePic;
    LinearLayout profilePictureLayout;

    LinearLayout attendanceLayout, timetableLayout, performanceLayout, behaviouralPerformanceLayout, paymentLayout, eventsLayout, newslettersLayout,
            settingsLayout, switchAccountLayout, logoutLayout;
    TextView attendance, timetable, performance, behaviouralPerformance, payment, events, newsletters, settings, switchAccount, logout;
    TextView attendanceBadge, performanceBadge, behaviouralPerformanceBadge, paymentBadge, eventsBadge, newslettersBadge;
    TextView attendanceMarker, timetableMarker, performanceMarker, behaviouralPerformanceMarker, paymentMarker, eventsMarker, newslettersMarker,
            settingsMarker, switchAccountMarker, logoutMarker;

    CoordinatorLayout coordinatorLayout;

    public MoreParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_parent, container, false);

        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = mAuth.getCurrentUser();
        context = getContext();
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        parentName = (TextView) view.findViewById(R.id.myprofilename);
        editMyProfile = (TextView) view.findViewById(R.id.editmyprofile);
        noKidLabel = (TextView) view.findViewById(R.id.nokidlabel);
        parentProfilePic = (ImageView) view.findViewById(R.id.myprofileimage);
        profilePictureLayout = (LinearLayout) view.findViewById(R.id.profilepicturelayout);

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

        attendanceBadge = (TextView) view.findViewById(R.id.attendancebadge);
        performanceBadge = (TextView) view.findViewById(R.id.performancebadge);
        behaviouralPerformanceBadge = (TextView) view.findViewById(R.id.behaviouralperformancebadge);
        paymentBadge = (TextView) view.findViewById(R.id.paymentsbadge);
        eventsBadge = (TextView) view.findViewById(R.id.eventsbadge);
        newslettersBadge = (TextView) view.findViewById(R.id.newsletterbadge);

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
        mAdapter = new MoreParentsAdapter(moreParentsModelList, getContext(), this);
        loadDataFromSharedPreferences();
        loadDataFromFirebase();
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void loadDataFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(mAuth.getCurrentUser().getUid());
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

        //Format for children is - ID Full Name URL TODO: Check for names with spaces in them
        mDatabaseReference = mFirebaseDatabase.getReference("Parents Students").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    sharedPreferencesManager.deleteMyChildren();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        final String childKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Student childInstance = dataSnapshot.getValue(Student.class);
                                    childrenFirebase.add(childKey + " " + childInstance.getFirstName() + " " + childInstance.getLastName() + " " + childInstance.getImageURL());

                                    sharedPreferencesManager.deleteMyChildren();
                                    sharedPreferencesManager.setMyChildren(new HashSet<String>(childrenFirebase));
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

        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Class").child(mAuth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()){
                        final String classKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    classesFirebase.add(classKey + " " + classInstance.getClassName() + " " + classInstance.getClassPicURL());

                                    sharedPreferencesManager.deleteMyClasses();
                                    sharedPreferencesManager.setMyClasses(new HashSet<String>(classesFirebase));

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

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

    private void loadDataFromSharedPreferences() {
        Set<String> childrenSet = sharedPreferencesManager.getMyChildren();
        ArrayList<String> children = new ArrayList<>();

        if (childrenSet != null) {children = new ArrayList<>(childrenSet); }

        moreParentsModelList.clear();

        if (children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                String[] childrenInfo = children.get(i).split(" ");
                MoreParentsModel moreParentsModel = new MoreParentsModel(childrenInfo[0], childrenInfo[1] + " " + childrenInfo[2], childrenInfo[3]);
                moreParentsModelList.add(moreParentsModel);
            }
        }

        mAdapter.notifyDataSetChanged();
        loadHeader();
        loadFooter();
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

        Glide.with(context)
                .load(sharedPreferencesManager.getMyPicURL())
                .centerCrop()
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(parentProfilePic);

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

        editMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, EditParentProfileActivity.class);
                context.startActivity(I);
            }
        });
    }

    public void loadFooter() {
        loadUpBadgesAndMarkers();

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
        if (activeKid != null){
            String firstName = activeKid.split(" ")[1];
            att = firstName + "'s Attendance";
            pef = firstName + "'s Academic Performance";
            bvr = firstName + "'s Behavioural Performance";
        }

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
                b.putString("childID", sharedPreferencesManager.getActiveKid().split(" ")[0]);
                b.putString("childName", sharedPreferencesManager.getActiveKid().split(" ")[1]);
                I.putExtras(b);
                context.startActivity(I);
            }
        });
        paymentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, StudentPerformanceForParentsActivity.class);
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
                sharedPreferencesManager.deleteActiveAccount();
                sharedPreferencesManager.deleteMyUserID();
                sharedPreferencesManager.deleteMyFirstName();
                sharedPreferencesManager.deleteMyLastName();
                sharedPreferencesManager.deleteMyPicURL();
                sharedPreferencesManager.deleteActiveKid();
                sharedPreferencesManager.deleteActiveClass();
                sharedPreferencesManager.deleteMyClasses();
                sharedPreferencesManager.deleteMyChildren();
                Intent I = new Intent(context, IntroSlider.class);
                context.startActivity(I);
                mAuth.signOut();
                ((Activity)context).finish();
            }
        });
    }

    private void loadUpBadgesAndMarkers() {
        if (sharedPreferencesManager.getActiveKid() != null){
            if (!sharedPreferencesManager.getActiveKid().equals("")) {
                mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(mFirebaseUser.getUid()).child(sharedPreferencesManager.getActiveKid().split(" ")[0]).child("status");
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

//                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentNotification").child(mFirebaseUser.getUid()).child(sharedPreferencesManager.getActiveKid().split(" ")[0]).child("count");
//                        mDatabaseReference.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    Integer count = dataSnapshot.getValue(Integer.class);
//                                    holder.performanceMarker.setText(String.valueOf(count));
//                                    holder.performanceMarker.setVisibility(View.VISIBLE);
//                                } else {
//                                    holder.performanceMarker.setVisibility(View.GONE);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        loadDataFromSharedPreferences();
        loadDataFromFirebase();
        super.onResume();
    }
}
