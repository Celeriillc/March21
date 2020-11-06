package com.celerii.celerii.Activities.Home.Parent;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ClassStoryAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.LinearLayoutManagerWrapper;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.Admin;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import java.util.List;
import java.util.Map;

public class ParentHomeClassFeed extends Fragment {

    private Context context;
    private ArrayList<ClassStory> classStoryList;
    public RecyclerView recyclerView;
    public ClassStoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SharedPreferencesManager sharedPreferencesManager;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout;
    RelativeLayout progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;
    AHBottomNavigation bottomNavigation;

    List<String> storyList;
    List<String> storyKeyList;

    String posterName, posterProfilePicURL;
    String classStoryKeys;
    Boolean stillLoading = true;
    int numberOfPostsPerLoad = 20;

    String featureUseKey = "";
    String featureName = "Parent Feed";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public ParentHomeClassFeed() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        setRetainInstance(true);
        super.onResume();

        DatabaseReference bottomNavBadgeRef = mFirebaseDatabase.getReference();
        Map<String, Object> bottomNavBadgeMap = new HashMap<String, Object>();
        NotificationBadgeModel notificationBadgeModel = new NotificationBadgeModel(false, 0);
        bottomNavBadgeMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/ClassStory", notificationBadgeModel);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_home_class_feed, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
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

        mLayoutManager = new LinearLayoutManagerWrapper(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        classStoryList = new ArrayList<>();
        storyList = new ArrayList<>();
        storyKeyList = new ArrayList<>();

        loadFromSharedPreferences();
        mAdapter = new ClassStoryAdapter(classStoryList, stillLoading, getContext());
        recyclerView.setAdapter(mAdapter);

//        recyclerView.setVisibility(View.GONE);
//        progressLayout.setVisibility(View.VISIBLE);

        loadParentFeed();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int id = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if(id >= classStoryList.size() - 1 && classStoryList.size() != 0 && id > 0){
                    loadMoreParentFeed(getLastKey());
                }
            }
        });

        DatabaseReference childEventRef = mFirebaseDatabase.getReference("ClassStory");
        childEventRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (classStoryList.size() > 0) {
                    String newClassStoryKey = dataSnapshot.getKey();
                    ClassStory newClassStory = dataSnapshot.getValue(ClassStory.class);
                    classStoryList.add(0, newClassStory);
                    storyKeyList.add(newClassStoryKey);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String changedClassStoryKey = dataSnapshot.getKey();
                ClassStory changedClassStory = dataSnapshot.getValue(ClassStory.class);

                for (int i = 0; i < classStoryList.size(); i++){
                    if (classStoryList.get(i).getPostID() != null) {
                        if (classStoryList.get(i).getPostID().equals(changedClassStoryKey) && changedClassStory != null) {
                            classStoryList.get(i).setNoOfLikes(changedClassStory.getNoOfLikes());
                            classStoryList.get(i).setNumberOfComments(changedClassStory.getNumberOfComments());
                            classStoryList.get(i).setComment(changedClassStory.getComment());
                            break;
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadParentFeed();
                    }
                }
        );

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ParentSearchActivity.class));
            }
        });

        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("Parents").child(mFirebaseUser.getUid()).child("ClassStory");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (!notificationBadgeModel.getStatus()){
                        bottomNavigation.setNotification("", 0);
                    }
                } else {
                    bottomNavigation.setNotification("", 0);
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
        classStoryList = new ArrayList<>();
        String messagesJSON = sharedPreferencesManager.getParentFeed();
        Type type = new TypeToken<ArrayList<ClassStory>>() {}.getType();
        classStoryList = gson.fromJson(messagesJSON, type);

        if (classStoryList == null) {
            classStoryList = new ArrayList<>();
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            mySwipeRefreshLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText(Html.fromHtml("The feed from your children's class is empty, if you're not connected to any of your children's account, click the " + "<b>" + "Search" + "</b>" + " button to find your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
            errorLayoutButton.setText("Find my child");
            errorLayoutButton.setVisibility(View.VISIBLE);
        } else {
            for (ClassStory classStory: classStoryList) {
                if (!storyKeyList.contains(classStory.getPostID())) {
                    storyKeyList.add(classStory.getPostID());
                }
            }
            classStoryList.add(new ClassStory());
            mAdapter = new ClassStoryAdapter(classStoryList, true, context);
            recyclerView.setAdapter(mAdapter);
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mySwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    int counter;
    int childrenCount;
    void loadParentFeed(){
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

        mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryParentFeed/" + auth.getCurrentUser().getUid());
        mDatabaseReference.orderByKey().limitToLast(numberOfPostsPerLoad).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classStoryList.clear();
                storyKeyList.clear();
                stillLoading = true;
                mAdapter.stillLoading = true;
//                mAdapter.notifyDataSetChanged();
                if (dataSnapshot.exists()) {
                    counter = 0;
                    childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String classStoryKeys = postSnapshot.getKey();
                        if (storyKeyList.contains(classStoryKeys)) { continue; }
                        storyKeyList.add(classStoryKeys);
                        final boolean liked = postSnapshot.getValue(boolean.class);

                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory/" + classStoryKeys);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final ClassStory classStoryServer = dataSnapshot.getValue(ClassStory.class);
                                    String posterAccountType = classStoryServer.getPosterAccountType();

                                    if (posterAccountType.equals("School")) {
                                        String schoolID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("School/" + schoolID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                counter++;
                                                if (dataSnapshot.exists()) {
                                                    School school = dataSnapshot.getValue(School.class);
                                                    posterName = school.getSchoolName();
                                                    posterProfilePicURL = school.getProfilePhotoUrl();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    classStoryList.add(classStoryServer);

                                                    if (counter == childrenCount) {
                                                        if (classStoryList.size() > 1) {
                                                            Collections.sort(classStoryList, new Comparator<ClassStory>() {
                                                                @Override
                                                                public int compare(ClassStory o1, ClassStory o2) {
                                                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                                }
                                                            });
                                                        }

                                                        Collections.reverse(classStoryList);
                                                        classStoryList.add(new ClassStory());
                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else if (posterAccountType.equals("Teacher") || posterAccountType.equals("Parent")) {
                                        String teacherID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Teacher/" + teacherID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                counter++;
                                                if (dataSnapshot.exists()) {
                                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                    posterName = teacher.getFirstName() + " " + teacher.getLastName();
                                                    posterProfilePicURL = teacher.getProfilePicURL();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    classStoryList.add(classStoryServer);

                                                    if (counter == childrenCount) {
                                                        if (classStoryList.size() > 1) {
                                                            Collections.sort(classStoryList, new Comparator<ClassStory>() {
                                                                @Override
                                                                public int compare(ClassStory o1, ClassStory o2) {
                                                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                                }
                                                            });
                                                        }

                                                        Collections.reverse(classStoryList);
                                                        classStoryList.add(new ClassStory());
                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else {
                                        String adminID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Admin/" + adminID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                counter++;
                                                if (dataSnapshot.exists()) {
                                                    Admin admin = dataSnapshot.getValue(Admin.class);
                                                    posterName = admin.getDisplayName();
                                                    posterProfilePicURL = admin.getProfilePictureURL();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    classStoryList.add(classStoryServer);

                                                    if (counter == childrenCount) {
                                                        if (classStoryList.size() > 1) {
                                                            Collections.sort(classStoryList, new Comparator<ClassStory>() {
                                                                @Override
                                                                public int compare(ClassStory o1, ClassStory o2) {
                                                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                                }
                                                            });
                                                        }

                                                        Collections.reverse(classStoryList);
                                                        classStoryList.add(new ClassStory());
                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                } else {
                                    counter++;

                                    if (counter == childrenCount) {
                                        if (classStoryList.size() > 1) {
                                            Collections.sort(classStoryList, new Comparator<ClassStory>() {
                                                @Override
                                                public int compare(ClassStory o1, ClassStory o2) {
                                                    return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                }
                                            });
                                        }

                                        Collections.reverse(classStoryList);
                                        classStoryList.add(new ClassStory());
                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
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
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("The feed from your children's class is empty, if you're not connected to any of your children's account, click the " + "<b>" + "Search" + "</b>" + " button to find your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadMoreParentFeed(String lastKey){
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            CustomToast.blueBackgroundToast(getContext(), "No internet");
            return;
        }

        final int sizeOnEntry = classStoryList.size() - 1;
        mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryParentFeed/" + auth.getCurrentUser().getUid());
        mDatabaseReference.orderByKey().endAt(lastKey).limitToLast(numberOfPostsPerLoad).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 1) {
                    counter = 0;
                    childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        classStoryKeys = postSnapshot.getKey();
                        if (storyKeyList.contains(classStoryKeys)) { counter++; continue; }
                        storyKeyList.add(classStoryKeys);
                        final boolean liked = postSnapshot.getValue(boolean.class);

                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory/" + classStoryKeys);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final ClassStory classStoryServer = dataSnapshot.getValue(ClassStory.class);
                                    String posterAccountType = classStoryServer.getPosterAccountType();

                                    if (posterAccountType.equals("School")) {
                                        String schoolID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("School/" + schoolID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                counter++;
                                                if (dataSnapshot.exists()) {
                                                    School school = dataSnapshot.getValue(School.class);
                                                    posterName = school.getSchoolName();
                                                    posterProfilePicURL = school.getProfilePhotoUrl();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    classStoryList.add(sizeOnEntry, classStoryServer);

                                                    if (counter == childrenCount) {
//                                                    if (classStoryList.size() > 1) {
//                                                        Collections.sort(classStoryList, new Comparator<ClassStory>() {
//                                                            @Override
//                                                            public int compare(ClassStory o1, ClassStory o2) {
//                                                                return o1.getTime().compareTo(o2.getTime());
//                                                            }
//                                                        });
//                                                    }
//
//                                                    Collections.reverse(classStoryList);
                                                        mySwipeRefreshLayout.setRefreshing(false);
                                                        progressLayout.setVisibility(View.GONE);
                                                        errorLayout.setVisibility(View.GONE);
                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } else if (posterAccountType.equals("Teacher") || posterAccountType.equals("Parent")) {
                                        String teacherID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Teacher/" + teacherID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                counter++;
                                                if (dataSnapshot.exists()) {
                                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                    posterName = teacher.getFirstName() + " " + teacher.getLastName();
                                                    posterProfilePicURL = teacher.getProfilePicURL();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    classStoryList.add(sizeOnEntry, classStoryServer);

                                                    if (counter == childrenCount) {
//                                                    if (classStoryList.size() > 1) {
//                                                        Collections.sort(classStoryList, new Comparator<ClassStory>() {
//                                                            @Override
//                                                            public int compare(ClassStory o1, ClassStory o2) {
//                                                                return o1.getTime().compareTo(o2.getTime());
//                                                            }
//                                                        });
//                                                    }
//
//                                                    Collections.reverse(classStoryList);
                                                        mySwipeRefreshLayout.setRefreshing(false);
                                                        progressLayout.setVisibility(View.GONE);
                                                        errorLayout.setVisibility(View.GONE);
                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        String adminID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Admin/" + adminID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                counter++;
                                                if (dataSnapshot.exists()) {
                                                    Admin admin = dataSnapshot.getValue(Admin.class);
                                                    posterName = admin.getDisplayName();
                                                    posterProfilePicURL = admin.getProfilePictureURL();

                                                    classStoryServer.setPosterName(posterName);
                                                    classStoryServer.setProfilePicURL(posterProfilePicURL);
                                                    classStoryServer.setLiked(liked);
                                                    classStoryList.add(sizeOnEntry, classStoryServer);

                                                    if (counter == childrenCount) {
//                                                    if (classStoryList.size() > 1) {
//                                                        Collections.sort(classStoryList, new Comparator<ClassStory>() {
//                                                            @Override
//                                                            public int compare(ClassStory o1, ClassStory o2) {
//                                                                return o1.getTime().compareTo(o2.getTime());
//                                                            }
//                                                        });
//                                                    }
//
//                                                    Collections.reverse(classStoryList);
                                                        mySwipeRefreshLayout.setRefreshing(false);
                                                        progressLayout.setVisibility(View.GONE);
                                                        errorLayout.setVisibility(View.GONE);
                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                } else {
                                    counter++;
                                    if (counter == childrenCount) {
//                                                    if (classStoryList.size() > 1) {
//                                                        Collections.sort(classStoryList, new Comparator<ClassStory>() {
//                                                            @Override
//                                                            public int compare(ClassStory o1, ClassStory o2) {
//                                                                return o1.getTime().compareTo(o2.getTime());
//                                                            }
//                                                        });
//                                                    }
//
//                                                    Collections.reverse(classStoryList);
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        progressLayout.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    mAdapter.stillLoading = false;
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    String getLastKey() {
        try{
            int size = classStoryList.size() - 2;
            String lastKey = classStoryList.get(size).getPostID();
            return lastKey;
        } catch (Exception e) {
            return "None";
        }
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
