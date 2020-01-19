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
import com.celerii.celerii.models.NewChatRowModel;
import com.celerii.celerii.models.School;
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
    ArrayList<String> classList;
    HashMap<String, NewChatRowModel> teachersList;
    ArrayList<StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelList;
    int counterStudents = 0, counterClasses = 0, counterTeachers = 0;

    public ParentTeacherMessageList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_parent_teacher_message_list, container, false);

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
        classList = new ArrayList<>();
        teachersList = new HashMap<>();

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
                mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(studentsSchoolsClassesandTeachersModel.getTeacherID());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Teacher teacher = dataSnapshot.getValue(Teacher.class);
                            final String teacherID = dataSnapshot.getKey();
                            final String teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                            final String teacherProfilePictureURL = teacher.getProfilePicURL();

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(studentsSchoolsClassesandTeachersModel.getStudentID());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    counterTeachers++;
                                    if (dataSnapshot.exists()) {
                                        Student student = dataSnapshot.getValue(Student.class);
                                        String studentFirstName = student.getFirstName();
                                        String relationship = studentFirstName + "'s School";

                                        NewChatRowModel newChatRowModel = new NewChatRowModel(teacherName, relationship, teacherProfilePictureURL, teacherID);
                                        if (!teachersList.containsKey(teacherID)){
                                            teachersList.put(teacherID, newChatRowModel);
                                            newChatRowModelList.add(newChatRowModel);
                                        }

                                        if (counterTeachers == studentsSchoolsClassesandTeachersModelList.size()) {
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
                            counterTeachers++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
//
//        counterStudents = 0;
//        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    kidsList.clear();
//                    classList.clear();
//                    teachersList.clear();
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
//                                counterStudents++;
//                                if (dataSnapshot.exists()){
//                                    Student student = dataSnapshot.getValue(Student.class);
//                                    final String studentName = student.getFirstName() + "'s";
//
//                                    mDatabaseReference = mFirebaseDatabase.getReference().child("Student Class").child(kidID);
//                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.exists()){
//                                                classList.clear();
//                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                                                    String classID = postSnapshot.getKey();
//                                                    classList.add(classID);
//                                                }
//
//                                                counterClasses = 0;
//                                                for (String classID : classList){
//                                                    counterClasses++;
//                                                    mDatabaseReference = mFirebaseDatabase.getReference().child("Class Teacher").child(classID);
//                                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                                            if (dataSnapshot.exists()){
//                                                                counterTeachers = 0;
//                                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                                                                    final int childrenCountTeachers = (int) dataSnapshot.getChildrenCount();
//                                                                    final String teacherID = postSnapshot.getKey();
//
//                                                                    mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherID);
//                                                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                        @Override
//                                                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                            counterTeachers++;
//                                                                            if (dataSnapshot.exists()){
//                                                                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
//                                                                                String teacherName = teacher.getLastName() + " " + teacher.getFirstName();
//                                                                                String relationship = studentName + " Teacher";
//
//                                                                                NewChatRowModel newChatRowModel = new NewChatRowModel(teacherName, relationship, teacher.getProfilePicURL(), teacherID);
//
//                                                                                if (!teachersList.containsKey(teacherID)){
//                                                                                    teachersList.put(teacherID, newChatRowModel);
//                                                                                    newChatRowModelList.add(newChatRowModel);
//                                                                                }
//                                                                            }
//
//
//
//                                                                            if (counterStudents == kidsList.size()) {
//                                                                                if (counterClasses == classList.size()) {
//                                                                                    if (counterTeachers == childrenCountTeachers) {
//                                                                                        Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
//                                                                                            @Override
//                                                                                            public int compare(NewChatRowModel o1, NewChatRowModel o2) {
//                                                                                                return o1.getName().compareTo(o2.getName());
//                                                                                            }
//                                                                                        });
//                                                                                        mAdapter.notifyDataSetChanged();
//                                                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                                                        progressLayout.setVisibility(View.GONE);
//                                                                                        errorLayout.setVisibility(View.GONE);
//                                                                                        recyclerView.setVisibility(View.VISIBLE);
//                                                                                    }
//                                                                                }
//                                                                            }
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onCancelled(DatabaseError databaseError) {
//
//                                                                        }
//                                                                    });
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
//                    errorLayoutText.setText("You don't have any Teachers to message at this time. If you're not connected to any of your children's account, use the search button to search for your children and send a request to connect to their accounts.");
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
