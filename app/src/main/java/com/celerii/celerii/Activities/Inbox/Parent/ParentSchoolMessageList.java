package com.celerii.celerii.Activities.Inbox.Parent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.NewChatRowAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.NewChatRowModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentsSchoolsClassesandTeachersModel;
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
public class ParentSchoolMessageList extends Fragment {

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

    ArrayList<String> kidsList;
    HashMap<String, NewChatRowModel> schoolList;
    ArrayList<StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelList;
    int counterStudent = 0, counterSchool = 0;

    public ParentSchoolMessageList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_school_message_list, container, false);

        sharedPreferencesManager = new SharedPreferencesManager(getContext());

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
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        newChatRowModelList = new ArrayList<>();
        mAdapter = new NewChatRowAdapter(newChatRowModelList, getContext());
        recyclerView.setAdapter(mAdapter);
        loadFromFirebase();

        kidsList = new ArrayList<>();
        schoolList = new HashMap<>();

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

    void loadFromFirebase(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        Gson gson = new Gson();
        studentsSchoolsClassesandTeachersModelList = new ArrayList<>();
        String studentsSchoolsClassesandTeachersJSON = sharedPreferencesManager.getStudentsSchoolsClassesTeachers();
        Type type = new TypeToken<ArrayList<StudentsSchoolsClassesandTeachersModel>>() {}.getType();
        studentsSchoolsClassesandTeachersModelList = gson.fromJson(studentsSchoolsClassesandTeachersJSON, type);

        if (studentsSchoolsClassesandTeachersModelList == null) {
            studentsSchoolsClassesandTeachersModelList = new ArrayList<>();
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("You don't have any Schools to message at this time. If you're not connected to any of your children's account, use the search button to search for your children and send a request to connect to their accounts.");
        } else {
            for (int i = 0; i < studentsSchoolsClassesandTeachersModelList.size(); i++) {
                final StudentsSchoolsClassesandTeachersModel studentsSchoolsClassesandTeachersModel = studentsSchoolsClassesandTeachersModelList.get(i);
                mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(studentsSchoolsClassesandTeachersModel.getSchoolID());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            School school = dataSnapshot.getValue(School.class);
                            final String schoolID = dataSnapshot.getKey();
                            final String schoolName = school.getSchoolName();
                            final String schoolProfilePictureURL = school.getProfilePhotoUrl();

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(studentsSchoolsClassesandTeachersModel.getStudentID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    counterSchool++;
                                    if (dataSnapshot.exists()) {
                                        Student student = dataSnapshot.getValue(Student.class);
                                        String studentFirstName = student.getFirstName();
                                        String relationship = studentFirstName + "'s School";

                                        NewChatRowModel newChatRowModel = new NewChatRowModel(schoolName, relationship, schoolProfilePictureURL, schoolID);
                                        if (!schoolList.containsKey(schoolID)){
                                            schoolList.put(schoolID, newChatRowModel);
                                            newChatRowModelList.add(newChatRowModel);
                                        }

                                        if (counterSchool == studentsSchoolsClassesandTeachersModelList.size()) {
                                            Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
                                                @Override
                                                public int compare(NewChatRowModel o1, NewChatRowModel o2) {
                                                    return o1.getName().compareTo(o2.getName());
                                                }
                                            });
                                            mAdapter.notifyDataSetChanged();
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            counterSchool++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

//
//        counterStudent = 0;
//        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    kidsList.clear();
//                    schoolList.clear();
//                    newChatRowModelList.clear();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        String kidID = postSnapshot.getKey();
//                        kidsList.add(kidID);
//                    }
//
//                    for (final String kidID : kidsList) {
//                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(kidID);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                counterStudent++;
//                                if (dataSnapshot.exists()){
//                                    Student student = dataSnapshot.getValue(Student.class);
//                                    final String studentName = student.getFirstName() + "'s";
//
//                                    mDatabaseReference = mFirebaseDatabase.getReference().child("Student School").child(kidID);
//                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.exists()){
//                                                final int childrenCountSchool = (int) dataSnapshot.getChildrenCount();
//                                                counterSchool = 0;
//                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                                    final String schoolID = postSnapshot.getKey();
//
//                                                    mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
//                                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                                            counterSchool++;
//                                                            if (dataSnapshot.exists()) {
//                                                                School school = dataSnapshot.getValue(School.class);
//                                                                String schoolName = school.getSchoolName();
//                                                                String relationship = studentName + " school";
//
//                                                                NewChatRowModel newChatRowModel = new NewChatRowModel(schoolName, relationship, school.getProfilePhotoUrl(), schoolID);
//
//                                                                if (!schoolList.containsKey(schoolID)){
//                                                                    schoolList.put(schoolID, newChatRowModel);
//                                                                    newChatRowModelList.add(newChatRowModel);
//                                                                }
//                                                            }
//
//                                                            if (counterStudent == kidsList.size()) {
//                                                                if (counterSchool == childrenCountSchool) {
//                                                                    Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
//                                                                        @Override
//                                                                        public int compare(NewChatRowModel o1, NewChatRowModel o2) {
//                                                                            return o1.getName().compareTo(o2.getName());
//                                                                        }
//                                                                    });
//                                                                    mAdapter.notifyDataSetChanged();
//                                                                    mySwipeRefreshLayout.setRefreshing(false);
//                                                                    progressLayout.setVisibility(View.GONE);
//                                                                    errorLayout.setVisibility(View.GONE);
//                                                                    recyclerView.setVisibility(View.VISIBLE);
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
//                    //You have no kids
//                    mySwipeRefreshLayout.setRefreshing(false);
//                    recyclerView.setVisibility(View.GONE);
//                    progressLayout.setVisibility(View.GONE);
//                    errorLayout.setVisibility(View.VISIBLE);
//                    errorLayoutText.setText("You don't have any Schools to message at this time. If you're not connected to any of your children's account, use the search button to search for your children and send a request to connect to their accounts.");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
