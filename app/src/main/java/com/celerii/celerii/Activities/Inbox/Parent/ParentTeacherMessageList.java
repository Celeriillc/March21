package com.celerii.celerii.Activities.Inbox.Parent;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.NewChatRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.NewChatRowModel;
import com.celerii.celerii.models.SchoolSettings;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentsSchoolsClassesandTeachersModel;
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
public class ParentTeacherMessageList extends Fragment {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private ArrayList<NewChatRowModel> newChatRowModelList;
    public RecyclerView recyclerView;
    public NewChatRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    ArrayList<String> kidsList;
    ArrayList<String> classList;
    HashMap<String, NewChatRowModel> teachersList;
    ArrayList<StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelList;
    int counterStudents = 0, counterClasses = 0, counterTeachers = 0;

    Handler internetConnectionHandler = new Handler();
    Runnable internetConnectionRunnable;

    String featureUseKey = "";
    String featureName = "Parent New Message to Teachers";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public ParentTeacherMessageList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_parent_teacher_message_list, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        newChatRowModelList = new ArrayList<>();
        mAdapter = new NewChatRowAdapter(newChatRowModelList, getContext());
        recyclerView.setAdapter(mAdapter);

        kidsList = new ArrayList<>();
        classList = new ArrayList<>();
        teachersList = new HashMap<>();
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

        return view;
    }

    int schoolAllowsParentTeacherMessagingCounter;
    boolean schoolAllowsMessaging;
    void loadFromFirebase(){
//        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
//            mySwipeRefreshLayout.setRefreshing(false);
//            recyclerView.setVisibility(View.GONE);
//            progressLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
//            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
//            return;
//        }
        internetConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(getString(R.string.no_internet_message_for_offline_download));
                }
            }
        };
        internetConnectionHandler.postDelayed(internetConnectionRunnable, 7000);

        Gson gson = new Gson();
        studentsSchoolsClassesandTeachersModelList = new ArrayList<>();
        String studentsSchoolsClassesandTeachersJSON = sharedPreferencesManager.getStudentsSchoolsClassesTeachers();
        Type type = new TypeToken<ArrayList<StudentsSchoolsClassesandTeachersModel>>() {}.getType();
        studentsSchoolsClassesandTeachersModelList = gson.fromJson(studentsSchoolsClassesandTeachersJSON, type);
        schoolAllowsParentTeacherMessagingCounter = 0;

        if (studentsSchoolsClassesandTeachersModelList == null) {
            studentsSchoolsClassesandTeachersModelList = new ArrayList<>();
            internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            mySwipeRefreshLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText(Html.fromHtml("You don't have any teachers to message at this time. If you're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
            errorLayoutButton.setText("Find my child");
            errorLayoutButton.setVisibility(View.VISIBLE);
        } else {

            HashMap<String, Boolean> schoolAllowsParentTeacherMessagingMap = new HashMap<>();

            for (int i = 0; i < studentsSchoolsClassesandTeachersModelList.size(); i++) {
                final StudentsSchoolsClassesandTeachersModel studentsSchoolsClassesandTeachersModel = studentsSchoolsClassesandTeachersModelList.get(i);
                String schoolID = studentsSchoolsClassesandTeachersModel.getSchoolID();

                mDatabaseReference = mFirebaseDatabase.getReference().child("School Settings").child(schoolID);
                mDatabaseReference.keepSynced(true);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        schoolAllowsParentTeacherMessagingCounter++;
                        if (dataSnapshot.exists()) {
                            SchoolSettings schoolSettings = dataSnapshot.getValue(SchoolSettings.class);
                            schoolAllowsParentTeacherMessagingMap.put(schoolID, schoolSettings.isAllowParentTeacherMessaging());
                        } else {
                            schoolAllowsParentTeacherMessagingMap.put(schoolID, true);
                        }

                        if (schoolAllowsParentTeacherMessagingCounter == studentsSchoolsClassesandTeachersModelList.size()) {
                            counterTeachers = 0;
                            newChatRowModelList.clear();
                            teachersList.clear();
                            mAdapter.notifyDataSetChanged();

                            for (int i = 0; i < studentsSchoolsClassesandTeachersModelList.size(); i++) {
                                final StudentsSchoolsClassesandTeachersModel studentsSchoolsClassesandTeachersModel = studentsSchoolsClassesandTeachersModelList.get(i);

                                try {
                                    schoolAllowsMessaging = schoolAllowsParentTeacherMessagingMap.get(studentsSchoolsClassesandTeachersModel.getSchoolID());
                                } catch (NullPointerException e) {
                                    schoolAllowsMessaging = true;
                                }

                                if (!studentsSchoolsClassesandTeachersModel.getTeacherID().isEmpty()) {
                                    mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(studentsSchoolsClassesandTeachersModel.getTeacherID());
                                    mDatabaseReference.keepSynced(true);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                final String teacherID = dataSnapshot.getKey();
                                                final String teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                                                final String teacherProfilePictureURL = teacher.getProfilePicURL();

                                                mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(studentsSchoolsClassesandTeachersModel.getStudentID());
                                                mDatabaseReference.keepSynced(true);
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        counterTeachers++;
                                                        if (dataSnapshot.exists()) {
                                                            Student student = dataSnapshot.getValue(Student.class);
                                                            String studentFirstName = student.getFirstName();
                                                            String relationship = studentFirstName + "'s Teacher";

                                                            NewChatRowModel newChatRowModel = new NewChatRowModel(teacherName, relationship, teacherProfilePictureURL, teacherID);
                                                            if (!teachersList.containsKey(teacherID)) {
                                                                teachersList.put(teacherID, newChatRowModel);
                                                                if (!newChatRowModel.getIDofPartner().equals(mFirebaseUser.getUid())) {
                                                                    if (schoolAllowsMessaging) {
                                                                        newChatRowModelList.add(newChatRowModel);
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        if (counterTeachers == studentsSchoolsClassesandTeachersModelList.size()) {
                                                            if (newChatRowModelList.size() > 0) {
                                                                if (newChatRowModelList.size() > 1) {
                                                                    Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
                                                                        @Override
                                                                        public int compare(NewChatRowModel o1, NewChatRowModel o2) {
                                                                            return o1.getName().compareTo(o2.getName());
                                                                        }
                                                                    });
                                                                }
                                                                mAdapter.notifyDataSetChanged();
                                                                internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                                                mySwipeRefreshLayout.setRefreshing(false);
                                                                progressLayout.setVisibility(View.GONE);
                                                                errorLayout.setVisibility(View.GONE);
                                                                recyclerView.setVisibility(View.VISIBLE);
                                                            } else {
                                                                internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                                                mySwipeRefreshLayout.setRefreshing(false);
                                                                recyclerView.setVisibility(View.GONE);
                                                                progressLayout.setVisibility(View.GONE);
                                                                mySwipeRefreshLayout.setVisibility(View.GONE);
                                                                errorLayout.setVisibility(View.VISIBLE);
                                                                errorLayoutButton.setVisibility(View.GONE);
                                                                errorLayoutText.setText(Html.fromHtml("Your child(ren)'s teachers should appear here, however, your child(ren)'s school(s) doesn't allow parent to teacher messaging."));
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            } else {
                                                counterTeachers++;

                                                if (counterTeachers == studentsSchoolsClassesandTeachersModelList.size()) {
                                                    if (newChatRowModelList.size() > 0) {
                                                        if (newChatRowModelList.size() > 1) {
                                                            Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
                                                                @Override
                                                                public int compare(NewChatRowModel o1, NewChatRowModel o2) {
                                                                    return o1.getName().compareTo(o2.getName());
                                                                }
                                                            });
                                                        }
                                                        mAdapter.notifyDataSetChanged();
                                                        internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                                        mySwipeRefreshLayout.setRefreshing(false);
                                                        progressLayout.setVisibility(View.GONE);
                                                        errorLayout.setVisibility(View.GONE);
                                                        recyclerView.setVisibility(View.VISIBLE);
                                                    } else {
                                                        internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                                        mySwipeRefreshLayout.setRefreshing(false);
                                                        recyclerView.setVisibility(View.GONE);
                                                        progressLayout.setVisibility(View.GONE);
                                                        mySwipeRefreshLayout.setVisibility(View.GONE);
                                                        errorLayout.setVisibility(View.VISIBLE);
                                                        errorLayoutButton.setVisibility(View.GONE);
                                                        errorLayout.setVisibility(View.VISIBLE);errorLayoutText.setText(Html.fromHtml("You don't have any teachers to message at this time. If you're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    counterTeachers++;

                                    if (counterTeachers == studentsSchoolsClassesandTeachersModelList.size()) {
                                        if (newChatRowModelList.size() > 0) {
                                            if (newChatRowModelList.size() > 1) {
                                                Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
                                                    @Override
                                                    public int compare(NewChatRowModel o1, NewChatRowModel o2) {
                                                        return o1.getName().compareTo(o2.getName());
                                                    }
                                                });
                                            }
                                            mAdapter.notifyDataSetChanged();
                                            internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                        } else {
                                            internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            recyclerView.setVisibility(View.GONE);
                                            progressLayout.setVisibility(View.GONE);
                                            mySwipeRefreshLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.VISIBLE);errorLayoutText.setText(Html.fromHtml("You don't have any teachers to message at this time. If you're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                                            errorLayoutButton.setText("Find my child");
                                            errorLayoutButton.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }
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
    public void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }
}
