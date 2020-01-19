package com.celerii.celerii.Activities.Home.Parent;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ClassStoryAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParentHomeClassFeed extends Fragment {

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
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    List<String> storyList;
    List<String> storyKeyList;

    String teacherName, teacherProfilePicURL;
    String classStoryKeys;
    Boolean stillLoading = true;
    int numberOfPostsPerLoad = 5;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_home_class_feed, container, false);

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

        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        classStoryList = new ArrayList<>();
        storyList = new ArrayList<>();
        storyKeyList = new ArrayList<>();

        mAdapter = new ClassStoryAdapter(classStoryList, stillLoading, getContext());
        recyclerView.setAdapter(mAdapter);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

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
//                classStoryList.clear();
//                ClassStory newClassStory = dataSnapshot.getValue(ClassStory.class);
//                classStoryList.add(newClassStory);
//                mAdapter.notifyDataSetChanged();
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

        return view;
    }

    int counter;
    void loadParentFeed(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
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
                mAdapter.notifyDataSetChanged();
                if (dataSnapshot.exists()) {
                    counter = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        String classStoryKeys = postSnapshot.getKey();
                        if (storyKeyList.contains(classStoryKeys)) { continue; }
                        storyKeyList.add(classStoryKeys);
                        final boolean liked = postSnapshot.getValue(boolean.class);

                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory/" + classStoryKeys);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final ClassStory classStoryServer = dataSnapshot.getValue(ClassStory.class);
                                    String teacherID = classStoryServer.getPosterID();

                                    mDatabaseReference = mFirebaseDatabase.getReference("Teacher/" + teacherID);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            counter++;
                                            if (dataSnapshot.exists()) {
                                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                                                teacherProfilePicURL = teacher.getProfilePicURL();

                                                classStoryServer.setPosterName(teacherName);
                                                classStoryServer.setProfilePicURL(teacherProfilePicURL);
                                                classStoryServer.setLiked(liked);
                                                classStoryList.add(classStoryServer);

                                                if (counter == childrenCount) {
//                                                    if (classStoryList.size() > 1) {
//                                                        Collections.sort(classStoryList, new Comparator<ClassStory>() {
//                                                            @Override
//                                                            public int compare(ClassStory o1, ClassStory o2) {
//                                                                return o1.getTime().compareTo(o2.getTime());
//                                                            }
//                                                        });
//                                                    }

                                                    Collections.reverse(classStoryList);
                                                    classStoryList.add(new ClassStory());
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
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("The feed from your children's class is empty, if you're not connected to any of your children's account, use the search button to search for them and send a request to connect to their accounts");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadMoreParentFeed(String lastKey){
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            CustomToast.blueBackgroundToast(getContext(), "Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        final int sizeOnEntry = classStoryList.size() - 1;
        mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryParentFeed/" + auth.getCurrentUser().getUid());
        mDatabaseReference.orderByKey().endAt(lastKey).limitToLast(numberOfPostsPerLoad).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 1) {
                    counter = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        classStoryKeys = postSnapshot.getKey();
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        if (storyKeyList.contains(classStoryKeys)) { counter++; continue; }
                        storyKeyList.add(classStoryKeys);
                        final boolean liked = postSnapshot.getValue(boolean.class);

                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory/" + classStoryKeys);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final ClassStory classStoryServer = dataSnapshot.getValue(ClassStory.class);
                                    String teacherID = classStoryServer.getPosterID();

                                    mDatabaseReference = mFirebaseDatabase.getReference("Teacher/" + teacherID);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            counter++;
                                            if (dataSnapshot.exists()) {
                                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                                                teacherProfilePicURL = teacher.getProfilePicURL();

                                                classStoryServer.setPosterName(teacherName);
                                                classStoryServer.setProfilePicURL(teacherProfilePicURL);
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
}
