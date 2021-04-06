package com.celerii.celerii.Activities.Home.Teacher;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TeacherClassStoryAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.LinearLayoutManagerWrapper;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.Admin;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Teacher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherHomeClassFeed extends Fragment {

    private Context context;
    private ArrayList<ClassStory> classStoryList;
    public RecyclerView recyclerView;
    public TeacherClassStoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    SharedPreferencesManager sharedPreferencesManager;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    FloatingActionButton newPost;

    List<String> storyList;
    List<String> storyKeyList;

    String posterName, posterProfilePicURL;
    String classStoryKeys;
    Boolean stillLoading = true;
    int numberOfPostsPerLoad = 20;

    String featureUseKey = "";
    String featureName = "Teacher Feed";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public TeacherHomeClassFeed() {
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
//        loadTeacherFeed();
//        UpdateDataFromFirebase.populateEssentials(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_home_class_feed, container, false);

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
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);
        newPost = (FloatingActionButton) view.findViewById(R.id.newpost);

        if (mFirebaseUser == null) {
            auth.signOut();
            Intent I = new Intent(getActivity(), IntroSlider.class);
            startActivity(I);
            getActivity().finish();
            return view;
        }

        mLayoutManager = new LinearLayoutManagerWrapper(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        classStoryList = new ArrayList<>();
        storyList = new ArrayList<>();
        storyKeyList = new ArrayList<>();

        loadFromSharedPreferences();
        mAdapter = new TeacherClassStoryAdapter(classStoryList, stillLoading, getContext());
        recyclerView.setAdapter(mAdapter);
        loadTeacherFeed();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int id = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if(id >= classStoryList.size() - 1 && classStoryList.size() != 0 && id > 0 && storyKeyList.size() >= numberOfPostsPerLoad){
                    loadMoreTeacherFeed(getLastKey());
                }
            }
        });

        DatabaseReference childEventRef = mFirebaseDatabase.getReference("ClassStory");
        childEventRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (classStoryList.size() > 1) {
                    String newClassStoryKey = dataSnapshot.getKey();
                    ClassStory newClassStory = dataSnapshot.getValue(ClassStory.class);
                    classStoryList.add(1, newClassStory);
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

//        DatabaseReference childEventRefTeacher = mFirebaseDatabase.getReference("ClassStoryTeacherFeed").child(mFirebaseUser.getUid());
//        childEventRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if (classStoryList.size() > 0) {
//                    final String storyKey = dataSnapshot.getKey();
//                    final Boolean liked = dataSnapshot.getValue(Boolean.class);
//                    if (!storyKeyList.contains(storyKey)) {
//                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory").child(storyKey);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    final ClassStory newClassStory = dataSnapshot.getValue(ClassStory.class);
//                                    String posterAccountType = newClassStory.getPosterAccountType();
//
//                                    if (posterAccountType.equals("School")) {
//                                        String schoolID = newClassStory.getPosterID();
//                                        mDatabaseReference = mFirebaseDatabase.getReference("School/" + schoolID);
//                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot.exists()) {
//                                                    School school = dataSnapshot.getValue(School.class);
//                                                    posterName = school.getSchoolName();
//                                                    posterProfilePicURL = school.getProfilePhotoUrl();
//
//                                                    newClassStory.setPosterName(posterName);
//                                                    newClassStory.setProfilePicURL(posterProfilePicURL);
//                                                    newClassStory.setLiked(liked);
//
//                                                    if (classStoryList.size() > 0) {
//                                                        if (!Date.compareDates(classStoryList.get(0).getDate(), newClassStory.getDate())) {
//                                                            classStoryList.add(0, newClassStory);
//                                                            storyKeyList.add(storyKey);
//                                                            mAdapter.notifyDataSetChanged();
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                    }
//                                    else if (posterAccountType.equals("Teacher") || posterAccountType.equals("Parent")) {
//                                        String teacherID = newClassStory.getPosterID();
//                                        mDatabaseReference = mFirebaseDatabase.getReference("Teacher/" + teacherID);
//                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot.exists()) {
//                                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
//                                                    posterName = teacher.getFirstName() + " " + teacher.getLastName();
//                                                    posterProfilePicURL = teacher.getProfilePicURL();
//
//                                                    newClassStory.setPosterName(posterName);
//                                                    newClassStory.setProfilePicURL(posterProfilePicURL);
//                                                    newClassStory.setLiked(liked);
//
//                                                    if (classStoryList.size() > 0) {
//                                                        if (!Date.compareDates(classStoryList.get(0).getDate(), newClassStory.getDate())) {
//                                                            classStoryList.add(0, newClassStory);
//                                                            storyKeyList.add(storyKey);
//                                                            mAdapter.notifyDataSetChanged();
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                    }
//                                    else {
//                                        String adminID = newClassStory.getPosterID();
//                                        mDatabaseReference = mFirebaseDatabase.getReference("Admin/" + adminID);
//                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot.exists()) {
//                                                    Admin admin = dataSnapshot.getValue(Admin.class);
//                                                    posterName = admin.getDisplayName();
//                                                    posterProfilePicURL = admin.getProfilePictureURL();
//
//                                                    newClassStory.setPosterName(posterName);
//                                                    newClassStory.setProfilePicURL(posterProfilePicURL);
//                                                    newClassStory.setLiked(liked);
//
//                                                    if (classStoryList.size() > 0) {
//                                                        if (!Date.compareDates(classStoryList.get(0).getDate(), newClassStory.getDate())) {
//                                                            classStoryList.add(0, newClassStory);
//                                                            storyKeyList.add(storyKey);
//                                                            mAdapter.notifyDataSetChanged();
//                                                        }
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                String changedClassStoryKey = dataSnapshot.getKey();
//                ClassStory changedClassStory = dataSnapshot.getValue(ClassStory.class);
//
////                if (changedClassStoryValue != null) {
////                    if (changedClassStoryKey != null) {
////                        for (int i = 0; i < classStoryList.size(); i++){
////                            if (classStoryList.get(i).getPostID() != null) {
////                                if (classStoryList.get(i).getPostID().equals(changedClassStoryKey)) {
////                                    classStoryList.get(i).setNoOfLikes(changedClassStory.getNoOfLikes());
////                                    classStoryList.get(i).setNumberOfComments(changedClassStory.getNumberOfComments());
////                                    classStoryList.get(i).setComment(changedClassStory.getComment());
////                                    break;
////                                }
////                            }
////                        }
////                    }
////                }
//
//                for (int i = 0; i < classStoryList.size(); i++){
//                    if (classStoryList.get(i).getPostID() != null) {
//                        if (classStoryList.get(i).getPostID().equals(changedClassStoryKey) && changedClassStory != null) {
//                            classStoryList.get(i).setNoOfLikes(changedClassStory.getNoOfLikes());
//                            classStoryList.get(i).setNumberOfComments(changedClassStory.getNumberOfComments());
//                            classStoryList.get(i).setComment(changedClassStory.getComment());
//                            break;
//                        }
//                    }
//                }
//
//                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(context, TeacherCreateClassPostActivity.class);
                context.startActivity(I);
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadTeacherFeed();
                    }
                }
        );

        return view;
    }

    private void loadFromSharedPreferences() {
        Gson gson = new Gson();
        classStoryList = new ArrayList<>();
        String messagesJSON = sharedPreferencesManager.getTeacherFeed();
        Type type = new TypeToken<ArrayList<ClassStory>>() {}.getType();
        classStoryList = gson.fromJson(messagesJSON, type);

        if (classStoryList == null) {
            classStoryList = new ArrayList<>();
            classStoryList.add(0, new ClassStory());
            mAdapter = new TeacherClassStoryAdapter(classStoryList, true, context);
            recyclerView.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
        } else {
            for (ClassStory classStory: classStoryList) {
                if (!storyKeyList.contains(classStory.getPostID())) {
                    storyKeyList.add(classStory.getPostID());
                }
            }
            classStoryList.add(0, new ClassStory());
            classStoryList.add(new ClassStory());
            mAdapter = new TeacherClassStoryAdapter(classStoryList, true, context);
            recyclerView.setAdapter(mAdapter);
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    int counter;
    int childrenCount;
    void loadTeacherFeed(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            CustomToast.blueBackgroundToast(context, "No Internet");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryTeacherFeed/" + auth.getCurrentUser().getUid());
        mDatabaseReference.orderByKey().limitToLast(numberOfPostsPerLoad).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                classStoryList.clear();
                storyKeyList.clear();
                mAdapter.notifyDataSetChanged();
                stillLoading = true;
                mAdapter.stillLoading = true;

                if (dataSnapshot.exists()) {
                    counter = 0;
                    childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        classStoryKeys = postSnapshot.getKey();
                        if (storyKeyList.contains(classStoryKeys)) { continue; }
                        storyKeyList.add(classStoryKeys);
                        final boolean liked = postSnapshot.getValue(boolean.class);

                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory/" + classStoryKeys);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
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
//
                                                        Collections.reverse(classStoryList);
                                                        Gson gson = new Gson();
                                                        String json = gson.toJson(classStoryList);
                                                        sharedPreferencesManager.setTeacherFeed(json);
                                                        classStoryList.add(0, new ClassStory());
                                                        classStoryList.add(new ClassStory());
                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        stillLoading = false;
                                                        mAdapter.stillLoading = false;
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
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
//
                                                    Collections.reverse(classStoryList);
                                                    classStoryList.add(0, new ClassStory());
                                                    classStoryList.add(new ClassStory());
                                                    mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                                    stillLoading = false;
                                                    mAdapter.stillLoading = false;
                                                    mAdapter.notifyDataSetChanged();
                                                }
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
//
                                                        Collections.reverse(classStoryList);
                                                        Gson gson = new Gson();
                                                        String json = gson.toJson(classStoryList);
                                                        sharedPreferencesManager.setTeacherFeed(json);
                                                        classStoryList.add(0, new ClassStory());
                                                        classStoryList.add(new ClassStory());
                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        stillLoading = false;
                                                        mAdapter.stillLoading = false;
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
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
//
                                                    Collections.reverse(classStoryList);
                                                    classStoryList.add(0, new ClassStory());
                                                    classStoryList.add(new ClassStory());
                                                    mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                                    stillLoading = false;
                                                    mAdapter.stillLoading = false;
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        String adminID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Admin/" + adminID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
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
//
                                                        Collections.reverse(classStoryList);
                                                        Gson gson = new Gson();
                                                        String json = gson.toJson(classStoryList);
                                                        sharedPreferencesManager.setTeacherFeed(json);
                                                        classStoryList.add(0, new ClassStory());
                                                        classStoryList.add(new ClassStory());
                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        stillLoading = false;
                                                        mAdapter.stillLoading = false;
                                                        mAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
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
//
                                                    Collections.reverse(classStoryList);
                                                    classStoryList.add(0, new ClassStory());
                                                    classStoryList.add(new ClassStory());
                                                    mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                                    stillLoading = false;
                                                    mAdapter.stillLoading = false;
                                                    mAdapter.notifyDataSetChanged();
                                                }
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
//
                                        Collections.reverse(classStoryList);
                                        classStoryList.add(0, new ClassStory());
                                        classStoryList.add(new ClassStory());
                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
                                        stillLoading = false;
                                        mAdapter.stillLoading = false;
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
                    classStoryList.add(0, new ClassStory());
                    mAdapter.notifyDataSetChanged();
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadMoreTeacherFeed(String lastKey) {
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            CustomToast.blueBackgroundToast(getContext(), "No internet");
            return;
        }

        final int sizeOnEntry = classStoryList.size() - 1;
        mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryTeacherTimeline/" + auth.getCurrentUser().getUid());
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
                            public void onDataChange(DataSnapshot dataSnapshot) {
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
//                                                    classStoryList.add(0, new ClassStory());
//                                                    classStoryList.add(new ClassStory());
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
                                                counter++;

                                                if (counter == childrenCount) {
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    mAdapter.notifyDataSetChanged();
                                                }
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
//                                                    classStoryList.add(0, new ClassStory());
//                                                    classStoryList.add(new ClassStory());
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
                                                counter++;

                                                if (counter == childrenCount) {
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        String adminID = classStoryServer.getPosterID();
                                        mDatabaseReference = mFirebaseDatabase.getReference("Admin/" + adminID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
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
//                                                    classStoryList.add(0, new ClassStory());
//                                                    classStoryList.add(new ClassStory());
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
                                                counter++;

                                                if (counter == childrenCount) {
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    mAdapter.notifyDataSetChanged();
                                                }
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
//                                                    classStoryList.add(0, new ClassStory());
//                                                    classStoryList.add(new ClassStory());
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

//        if (hidden) {
//
//        } else {
//
//        }
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
//        UpdateDataFromFirebase.populateEssentials(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }
}
