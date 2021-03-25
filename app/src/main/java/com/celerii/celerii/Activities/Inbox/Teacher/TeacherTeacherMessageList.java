package com.celerii.celerii.Activities.Inbox.Teacher;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.Activities.Search.Teacher.SearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.NewChatRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.NewChatRowModel;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherTeacherMessageList extends Fragment {

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

    ArrayList<String> schoolList, schoolName;
    HashMap<String, NewChatRowModel> teacherList;
    HashMap<String, String> schoolTeacher;
    int counterSchool = 0, counterTeacher = 0, counterSchoolName = 0, counterSchoolTeacher = 0;

    String featureUseKey = "";
    String featureName = "Teacher New Message to Teachers";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";


    public TeacherTeacherMessageList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_teacher_message_list, container, false);

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
        loadFromFirebase();

        schoolList = new ArrayList<>();
        schoolName = new ArrayList<>();
        schoolTeacher= new HashMap<>();
        teacherList = new HashMap<>();

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
                startActivity(new Intent(context, SearchActivity.class));
            }
        });

        return view;
    }

    void loadFromFirebase(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        counterSchool = counterSchoolName = counterSchoolTeacher = counterTeacher = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher School").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    schoolList.clear();
                    schoolName.clear();
                    schoolTeacher.clear();
                    teacherList.clear();
                    newChatRowModelList.clear();
                    mAdapter.notifyDataSetChanged();
                    final int childrenCountSchool = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        counterSchool++;
                        String schoolID = postSnapshot.getKey();
                        schoolList.add(schoolID);
                    }

                    if (counterSchool == schoolList.size()) {
                        for (final String schoolID : schoolList) {
                            mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    counterSchoolName++;
                                    if (dataSnapshot.exists()) {
                                        School school = dataSnapshot.getValue(School.class);
                                        schoolName.add(school.getSchoolName());
                                    } else {
                                        schoolName.add("");
                                    }

                                    if (counterSchoolName == schoolList.size()) {
                                        for (final String schoolID : schoolList){
                                            mDatabaseReference = mFirebaseDatabase.getReference().child("School Teacher").child(schoolID);
                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    counterSchoolTeacher++;
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                            String teacherID = postSnapshot.getKey();
                                                            if (!schoolTeacher.containsKey(teacherID)) {
                                                                schoolTeacher.put(teacherID, schoolName.get(schoolList.indexOf(schoolID)));
                                                            }
                                                        }
                                                    }

                                                    if (counterSchoolTeacher == schoolList.size()) {
                                                        for (final Map.Entry<String, String> entry : schoolTeacher.entrySet()){
                                                            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(entry.getKey());
                                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    counterTeacher++;
                                                                    if (dataSnapshot.exists()) {
                                                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                                        String teacherName = teacher.getLastName() + " " + teacher.getFirstName();

                                                                        NewChatRowModel newChatRowModel = new NewChatRowModel(teacherName, entry.getValue(), teacher.getProfilePicURL(), entry.getKey());

                                                                        if (!newChatRowModel.getIDofPartner().equals(mFirebaseUser.getUid())) {
                                                                            newChatRowModelList.add(newChatRowModel);
                                                                        }

                                                                        if (counterSchool == childrenCountSchool) {
                                                                            if (counterTeacher == schoolTeacher.size()) {
                                                                                if (newChatRowModelList.size() > 1) {
                                                                                    Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
                                                                                        @Override
                                                                                        public int compare(NewChatRowModel o1, NewChatRowModel o2) {
                                                                                            return o1.getName().compareTo(o2.getName());
                                                                                        }
                                                                                    });
                                                                                }
                                                                                mAdapter.notifyDataSetChanged();
                                                                                mySwipeRefreshLayout.setRefreshing(false);
                                                                                progressLayout.setVisibility(View.GONE);
                                                                                errorLayout.setVisibility(View.GONE);
                                                                                recyclerView.setVisibility(View.VISIBLE);
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                        if (schoolTeacher.size() == 0) {
                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                            recyclerView.setVisibility(View.GONE);
                                                            progressLayout.setVisibility(View.GONE);
                                                            mySwipeRefreshLayout.setVisibility(View.GONE);
                                                            errorLayout.setVisibility(View.VISIBLE);
                                                            errorLayoutText.setText(Html.fromHtml("You don't have any colleagues to message at this time. If you're not connected to any of your classes' account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your school to access your classes or get started by clicking the " + "<b>" + "Find my school" + "</b>" + " button below"));
                                                            errorLayoutButton.setText("Find my school");
                                                            errorLayoutButton.setVisibility(View.VISIBLE);
                                                        }
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
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You don't have any colleagues to message at this time. If you're not connected to any of your classes' account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your school to access your classes or get started by clicking the " + "<b>" + "Find my school" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my school");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        counterSchool = counterTeacher = 0;
//        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher School").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    schoolList.clear();
//                    teacherList.clear();
//                    newChatRowModelList.clear();
//                    final int childrenCountSchool = (int) dataSnapshot.getChildrenCount();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        counterSchool++;
//                        final String schoolID = postSnapshot.getKey();
//                        schoolList.add(schoolID);
//
//                        mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()){
//                                    School school = dataSnapshot.getValue(School.class);
//                                    final String relationship = school.getSchoolName();
//
//                                    mDatabaseReference = mFirebaseDatabase.getReference().child("School Teacher").child(schoolID);
//                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.exists()){
//                                                final int childrenCountTeacher = (int) dataSnapshot.getChildrenCount();
//                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                                                    final String teacherID = postSnapshot.getKey();
//                                                    if (teacherID.equals(mFirebaseUser.getUid())){ continue; }
//
//                                                    counterTeacher = 0;
//                                                    mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherID);
//                                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                                            if (dataSnapshot.exists()){
//                                                                counterTeacher++;
//                                                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
//                                                                String teacherName = teacher.getLastName() + " " + teacher.getFirstName();
//
//                                                                NewChatRowModel newChatRowModel = new NewChatRowModel(teacherName, relationship, teacher.getProfilePicURL(), teacherID);
//
//                                                                if (!teacherList.containsKey(teacherID)){
//                                                                    teacherList.put(teacherID, newChatRowModel);
//                                                                    newChatRowModelList.add(newChatRowModel);
//                                                                }
//
//
//                                                                if (counterSchool == childrenCountSchool) {
//                                                                    if (counterTeacher == childrenCountTeacher) {
//                                                                        Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
//                                                                            @Override
//                                                                            public int compare(NewChatRowModel o1, NewChatRowModel o2) {
//                                                                                return o1.getName().compareTo(o2.getName());
//                                                                            }
//                                                                        });
//                                                                        mAdapter.notifyDataSetChanged();
//                                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                                        progressLayout.setVisibility(View.GONE);
//                                                                        errorLayout.setVisibility(View.GONE);
//                                                                        recyclerView.setVisibility(View.VISIBLE);
//                                                                    }
//                                                                }
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(DatabaseError databaseError) {
//
//                                                        }
//                                                    });
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                } else {
//                    mySwipeRefreshLayout.setRefreshing(false);
//                    recyclerView.setVisibility(View.GONE);
//                    progressLayout.setVisibility(View.GONE);
//                    errorLayout.setVisibility(View.VISIBLE);
//                    errorLayoutText.setText("You don't have any Colleagues to message at this time");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
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
