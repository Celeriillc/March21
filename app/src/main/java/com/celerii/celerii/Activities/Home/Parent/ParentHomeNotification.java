package com.celerii.celerii.Activities.Home.Parent;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
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

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ClassNotificationAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.Admin;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.School;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentHomeNotification extends Fragment {

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
    AHBottomNavigation bottomNavigation;

    private ArrayList<NotificationModel> notificationModelList;
    public RecyclerView recyclerView;
    public ClassNotificationAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeAccount = "";
    int counter;

    String featureUseKey = "";
    String featureName = "Parent Notification";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public ParentHomeNotification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_parent_home_notification, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        activeAccount = sharedPreferencesManager.getActiveAccount();

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

        ParentMainActivityTwo activity = (ParentMainActivityTwo) getActivity();
        bottomNavigation = activity.getData();

        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        notificationModelList = new ArrayList<>();
        loadFromSharedPreferences();
        mAdapter = new ClassNotificationAdapter(notificationModelList, getContext());
        recyclerView.setAdapter(mAdapter);
        loadFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ParentSearchActivity.class));
            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Parents").child(mFirebaseUser.getUid()).child("Notifications");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (!notificationBadgeModel.getStatus()){
                        bottomNavigation.setNotification("", 2);
                    }
                } else {
                    bottomNavigation.setNotification("", 2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadFromSharedPreferences() {
        Gson gson = new Gson();
        notificationModelList = new ArrayList<>();
        String messagesJSON = sharedPreferencesManager.getParentNotification();
        Type type = new TypeToken<ArrayList<NotificationModel>>() {}.getType();
        notificationModelList = gson.fromJson(messagesJSON, type);

        if (notificationModelList == null) {
            notificationModelList = new ArrayList<>();
            mAdapter = new ClassNotificationAdapter(notificationModelList, getContext());
            recyclerView.setAdapter(mAdapter);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            mySwipeRefreshLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText(Html.fromHtml("You don't have any notifications at this time, if you're not connected to any of your children's account, click the " + "<b>" + "Search" + "</b>" + " button to find your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
            errorLayoutButton.setText("Find my child");
            errorLayoutButton.setVisibility(View.VISIBLE);
        } else {
            mAdapter = new ClassNotificationAdapter(notificationModelList, getContext());
            recyclerView.setAdapter(mAdapter);
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mySwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            if (errorLayout.getVisibility() == View.VISIBLE) {
                errorLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                errorLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            CustomToast.blueBackgroundToast(context, "No Internet");
            return;
        }


        mDatabaseReference = mFirebaseDatabase.getReference().child("NotificationParent").child(mFirebaseUser.getUid());
        mDatabaseReference./*orderByChild("time").*/limitToLast(50).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    notificationModelList.clear();
//                    mAdapter.notifyDataSetChanged();
                    counter = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        final NotificationModel notificationModel = postSnapshot.getValue(NotificationModel.class);
                        notificationModel.setSortableTime(Date.convertToSortableDate(notificationModel.getTime()));

                        if (notificationModel.getFromAccountType().equals("School")) {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(notificationModel.getFromID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    counter++;
                                    if (dataSnapshot.exists()) {
                                        School school = dataSnapshot.getValue(School.class);
                                        notificationModel.setFromName(school.getSchoolName());
                                        notificationModel.setFromProfilePicture(school.getProfilePhotoUrl());
                                    } else {
                                        notificationModel.setFromName("A user");
                                    }
//                                    if (!notificationModel.getNotificationType().equals("ConnectionRequest")) {
                                        notificationModelList.add(notificationModel);
//                                    }

                                    if (counter == childrenCount) {
                                        if (notificationModelList.size() > 1) {
                                            Collections.sort(notificationModelList, new Comparator<NotificationModel>() {
                                                @Override
                                                public int compare(NotificationModel o1, NotificationModel o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }

                                        Collections.reverse(notificationModelList);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(notificationModelList);
                                        sharedPreferencesManager.setParentNotification(json);
//                                        loadFromSharedPreferences();
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
//                                        progressLayout.setVisibility(View.GONE);
//                                        errorLayout.setVisibility(View.GONE);
//                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else if (notificationModel.getFromAccountType().equals("Teacher") || notificationModel.getFromAccountType().equals("Parent")) {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(notificationModel.getFromID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    counter++;
                                    if (dataSnapshot.exists()) {
                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                        notificationModel.setFromName(teacher.getFirstName() + " " + teacher.getLastName());
                                        notificationModel.setFromProfilePicture(teacher.getProfilePicURL());
                                    } else {
                                        notificationModel.setFromName("A user");
                                    }
//                                    if (!notificationModel.getNotificationType().equals("ConnectionRequest")) {
                                        notificationModelList.add(notificationModel);
//                                    }

                                    if (counter == childrenCount) {
                                        if (notificationModelList.size() > 1) {
                                            Collections.sort(notificationModelList, new Comparator<NotificationModel>() {
                                                @Override
                                                public int compare(NotificationModel o1, NotificationModel o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }

                                        Collections.reverse(notificationModelList);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(notificationModelList);
                                        sharedPreferencesManager.setParentNotification(json);
//                                        loadFromSharedPreferences();
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
//                                        progressLayout.setVisibility(View.GONE);
//                                        errorLayout.setVisibility(View.GONE);
//                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Admin").child(notificationModel.getFromID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    counter++;
                                    if (dataSnapshot.exists()) {
                                        Admin admin = dataSnapshot.getValue(Admin.class);
                                        notificationModel.setFromName(admin.getDisplayName());
                                        notificationModel.setFromProfilePicture(admin.getProfilePictureURL());
                                    } else {
                                        notificationModel.setFromName("A user");
                                    }
//                                    if (!notificationModel.getNotificationType().equals("ConnectionRequest")) {
                                        notificationModelList.add(notificationModel);
//                                    }

                                    if (counter == childrenCount) {
                                        if (notificationModelList.size() > 1) {
                                            Collections.sort(notificationModelList, new Comparator<NotificationModel>() {
                                                @Override
                                                public int compare(NotificationModel o1, NotificationModel o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }

                                        Collections.reverse(notificationModelList);
                                        Gson gson = new Gson();
                                        String json = gson.toJson(notificationModelList);
                                        sharedPreferencesManager.setParentNotification(json);
//                                        loadFromSharedPreferences();
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
//                                        progressLayout.setVisibility(View.GONE);
//                                        errorLayout.setVisibility(View.GONE);
//                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You don't have any notifications at this time, if you're not connected to any of your children's account, click the " + "<b>" + "Search" + "</b>" + " button to find your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(message);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

//        if (hidden) {
//
//        } else {
//
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

        DatabaseReference bottomNavBadgeRef = mFirebaseDatabase.getReference();
        HashMap<String, Object> bottomNavBadgeMap = new HashMap<String, Object>();
        NotificationBadgeModel notificationBadgeModel = new NotificationBadgeModel(false, 0);
        bottomNavBadgeMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications", notificationBadgeModel);
        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
//        UpdateDataFromFirebase.populateEssentials(getContext());
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

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
