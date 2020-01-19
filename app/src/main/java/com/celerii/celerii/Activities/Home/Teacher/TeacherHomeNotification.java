package com.celerii.celerii.Activities.Home.Teacher;


import android.os.Bundle;
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
import com.celerii.celerii.adapters.ClassNotificationAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherHomeNotification extends Fragment {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<NotificationModel> notificationModelList;
    public RecyclerView recyclerView;
    public ClassNotificationAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeAccount = "";
    int counter;

    public TeacherHomeNotification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_home_notification, container, false);

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
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        notificationModelList = new ArrayList<>();
        loadFromFirebase();
        mAdapter = new ClassNotificationAdapter(notificationModelList, getContext());
        recyclerView.setAdapter(mAdapter);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );

        return view;
    }

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("NotificationTeacher").child(mFirebaseUser.getUid());
        mDatabaseReference.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    notificationModelList.clear();
                    mAdapter.notifyDataSetChanged();
                    counter = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final int childrenCount = (int) dataSnapshot.getChildrenCount();
                        final NotificationModel notificationModel = postSnapshot.getValue(NotificationModel.class);

                        if (notificationModel.getFromAccountType().equals("School")) {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(notificationModel.getFromID());
                        } else if (notificationModel.getFromAccountType().equals("Teacher")) {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(notificationModel.getFromID());
                        } else {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(notificationModel.getFromID());
                        }
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                counter++;
                                if (dataSnapshot.exists()) {
                                    if (notificationModel.getFromAccountType().equals("School")) {
                                        School school = dataSnapshot.getValue(School.class);
                                        notificationModel.setFromName(school.getSchoolName());
                                        notificationModel.setFromProfilePicture(school.getProfilePhotoUrl());
                                    } else if (notificationModel.getFromAccountType().equals("Teacher")) {
                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                        notificationModel.setFromName(teacher.getFirstName() + " " + teacher.getLastName());
                                        notificationModel.setFromProfilePicture(teacher.getProfilePicURL());
                                    } else {
                                        Parent parent = dataSnapshot.getValue(Parent.class);
                                        notificationModel.setFromName(parent.getFirstName() + " " + parent.getLastName());
                                        notificationModel.setFromProfilePicture(parent.getProfilePicURL());
                                    }
                                    notificationModelList.add(notificationModel);
                                } else {
                                    notificationModel.setFromName("A user");
                                    notificationModelList.add(notificationModel);
                                }

                                if (counter == childrenCount) {
                                    Collections.reverse(notificationModelList);
                                    mAdapter.notifyDataSetChanged();
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    progressLayout.setVisibility(View.GONE);
                                    errorLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
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
                    errorLayoutText.setText("You don't have any notifications at this time. If you're not connected to any of your classes' account, use the search button to search for your school and request access to your classes' accounts");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
