package com.celerii.celerii.Activities.Delete;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SearchResultsAdapter;
import com.celerii.celerii.helperClasses.StringComparer;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.SearchExistingIncomingAndOutgoingConnections;
import com.celerii.celerii.models.SearchResultsRow;
import com.celerii.celerii.models.Student;
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
public class ParentSearchResultsAllFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    private ArrayList<SearchResultsRow> searchResultsRowList;
    private ArrayList<String> existingConnections;
    private ArrayList<String> pendingIncomingRequests;
    private ArrayList<String> pendingOutgoingRequests;
    public RecyclerView recyclerView;
    public SearchResultsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String query;
    int primaryLoopControl = 0, secondaryLoopControl = 0;
    int primaryLoopTerminator = 0, secondaryLoopTerminator = 0;

    public ParentSearchResultsAllFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_search_results_all, container, false);

        Bundle args = getArguments();
        query = args.getString("Query");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (LinearLayout) view.findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) view.findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        searchResultsRowList = new ArrayList<>();
        existingConnections = new ArrayList<>();
        pendingIncomingRequests = new ArrayList<>();
        pendingOutgoingRequests = new ArrayList<>();
        loadSchoolDataFromFirebase();
        SearchExistingIncomingAndOutgoingConnections searchExistingIncomingAndOutgoingConnections = new SearchExistingIncomingAndOutgoingConnections();
        mAdapter = new SearchResultsAdapter(searchResultsRowList, getContext(), searchExistingIncomingAndOutgoingConnections);
        recyclerView.setAdapter(mAdapter);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadSchoolDataFromFirebase();
                    }
                }
        );

        return view;
    }

    void loadSchoolDataFromFirebase(){
        primaryLoopControl = 0;
        primaryLoopTerminator = 0;
        mDatabaseReference = mFirebaseDatabase.getReference("School");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    searchResultsRowList.clear();
                    mAdapter.notifyDataSetChanged();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        School school = postSnapshot.getValue(School.class);
                        String schoolName = school.getSchoolName();
                        String location = school.getLocation();
                        String searchSubject = schoolName + " " + location;

                        if (StringComparer.contains(query, searchSubject)) {
                            location = school.getLocation() + ", " + school.getState() + ", " + school.getCountry();
                            SearchResultsRow searchHistoryRow = new SearchResultsRow(postSnapshot.getKey(), school.getSchoolName(), location, school.getProfilePhotoUrl(), "School");
                            searchResultsRowList.add(searchHistoryRow);
                        }
                    }
                }

                loadStudentDataFromFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadStudentDataFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("Student");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int primaryChildrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        primaryLoopControl++;
                        final Student student = postSnapshot.getValue(Student.class);
                        final String studentKey = postSnapshot.getKey();
                        final String studentFirstName = student.getFirstName();
                        final String studentLastName = student.getLastName();
                        final String studentMiddleName = student.getMiddleName();
                        final String studentPicURL = student.getImageURL();
                        String searchSubject = studentFirstName + " " + studentLastName + " " + studentMiddleName;

                        if (StringComparer.contains(query, searchSubject)) {
                            primaryLoopTerminator++;
                            SearchResultsRow searchHistoryRow;
                            if (studentMiddleName.isEmpty()) {
                                searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentLastName, "School", studentPicURL, "Student");
                            } else {
                                searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentMiddleName + " " + studentLastName, "School", studentPicURL, "Student");
                            }
                            searchResultsRowList.add(searchHistoryRow);
                        }

                        primaryLoopTerminator = 0;
                        if (primaryChildrenCount == primaryLoopControl) {

                            if (searchResultsRowList.size() > 0) {
                                primaryLoopControl = 0;
                                for (int i = 0; i < searchResultsRowList.size(); i++) {
                                    final SearchResultsRow searchHistoryRow = searchResultsRowList.get(i);
                                    String studentID = searchHistoryRow.getEntityId();
                                    mDatabaseReference = mFirebaseDatabase.getReference("Student School").child(studentID);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                primaryLoopControl++;
                                            }
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                final String schoolKey = postSnapshot.getKey();
                                                mDatabaseReference = mFirebaseDatabase.getReference("School").child(schoolKey);
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String schoolName;
                                                        if (dataSnapshot.exists()) {
                                                            School school = dataSnapshot.getValue(School.class);
                                                            schoolName = school.getSchoolName();
                                                        } else {
                                                            schoolName = "";
                                                        }
                                                        searchHistoryRow.setEntityAddress(schoolName);
                                                        searchHistoryRow.setEntityAddressID(schoolKey);
                                                        primaryLoopTerminator++;

                                                        if (primaryLoopTerminator == primaryLoopControl) {
                                                            loadTeacherDataFromFirebase();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                                break;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            } else {
                                loadTeacherDataFromFirebase();
                            }
                        }
                    }
                } else {
                    loadTeacherDataFromFirebase();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    ArrayList<String> studentsList = new ArrayList<>();
    int studentCount = 0;
    void loadTeacherDataFromFirebase() {

        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        studentsList.add(postSnapshot.getKey());
                    }

                    for (String student : studentsList) {
                        studentCount++;

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Teacher").child(student);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        final String teacherKey = postSnapshot.getKey();

                                        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherKey);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                    String teacherName = teacher.getLastName() + " " + teacher.getFirstName();
                                                    String searchSubject = teacher.getLastName() + " " + teacher.getFirstName() + " " + teacher.getMiddleName();

                                                    if (StringComparer.contains(query, searchSubject)) {
                                                        SearchResultsRow searchResultsRow = new SearchResultsRow(teacherKey, teacherName, "", teacher.getProfilePicURL(), "Teacher");
                                                        searchResultsRowList.add(searchResultsRow);
                                                    }
                                                }

                                                Collections.shuffle(searchResultsRowList);
                                                mySwipeRefreshLayout.setRefreshing(false);
                                                progressLayout.setVisibility(View.GONE);
                                                errorLayout.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                                mAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                if (studentCount == studentsList.size()) {
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    recyclerView.setVisibility(View.GONE);
                                    progressLayout.setVisibility(View.GONE);
                                    errorLayout.setVisibility(View.VISIBLE);
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent Teacher").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        final String teacherID = postSnapshot.getKey();
//
//                        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherID);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()){
//                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
//                                    String teacherName = teacher.getLastName() + " " + teacher.getFirstName();
//                                    String searchSubject = teacher.getLastName() + " " + teacher.getFirstName() + " " + teacher.getMiddleName();
//
//                                    if (StringComparer.contains(query, searchSubject)) {
//                                        SearchResultsRow searchResultsRow = new SearchResultsRow(teacherID, teacherName, "", teacher.getProfilePicURL(), "Teacher");
//                                        searchResultsRowList.add(searchResultsRow);
//                                    }
//                                }
//
//                                Collections.shuffle(searchResultsRowList);
//                                mySwipeRefreshLayout.setRefreshing(false);
//                                progressLayout.setVisibility(View.GONE);
//                                errorLayout.setVisibility(View.GONE);
//                                recyclerView.setVisibility(View.VISIBLE);
//                                mAdapter.notifyDataSetChanged();
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
//                    progressLayout.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.GONE);
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
