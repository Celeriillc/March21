package com.celerii.celerii.Activities.Delete;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SearchResultsAdapter;
import com.celerii.celerii.helperClasses.StringComparer;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.SearchResultsRow;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.TeacherSchoolConnectionRequest;
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
public class SearchResultsAllFragment extends Fragment {

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
    int primaryLoopControl, secondaryLoopControl;
    Boolean firstPass = false;

    public SearchResultsAllFragment() {
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

        View view = inflater.inflate(R.layout.fragment_search_results_all, container, false);

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
        loadExistingConnections();
        mAdapter = new SearchResultsAdapter(searchResultsRowList, getContext(), existingConnections, pendingIncomingRequests, pendingOutgoingRequests);
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
                        loadExistingConnections();
                    }
                }
        );

        return view;
    }

    void loadExistingConnections(){
        existingConnections = new ArrayList<>();
        pendingIncomingRequests = new ArrayList<>();
        pendingOutgoingRequests = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher School").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        existingConnections.add(postSnapshot.getKey());
                    }
                }

                mAdapter.notifyDataSetChanged();
                loadPendingIncomingRequests();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void loadPendingIncomingRequests(){
        mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        pendingIncomingRequests.add(postSnapshot.getKey());
                    }
                }

                mAdapter.notifyDataSetChanged();
                loadPendingOutgoingRequests();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    int loopControl = 0;
    void loadPendingOutgoingRequests() {
        loopControl = 0;
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        String schoolKey = postSnapshot.getKey();
                        loopControl++;

                        mDatabaseReference = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid()).child(schoolKey);
                        mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        TeacherSchoolConnectionRequest teacherSchoolConnectionRequest = postSnapshot.getValue(TeacherSchoolConnectionRequest.class);
                                        String schoolID = teacherSchoolConnectionRequest.getSchool();
                                        pendingOutgoingRequests.add(schoolID);
                                    }
                                }

                                if (childrenCount == loopControl) {
                                    mAdapter.notifyDataSetChanged();
                                    loadFromFirebase();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    mAdapter.notifyDataSetChanged();
                    loadSchoolDataFromFirebase();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void loadSchoolDataFromFirebase(){
        mDatabaseReference = mFirebaseDatabase.getReference("School");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    searchResultsRowList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        School school = postSnapshot.getValue(School.class);
                        String schoolName = school.getSchoolName();
                        String location = school.getLocation();
                        String state = school.getState();
                        String searchSubject = schoolName + " " + location + " " + state;

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
        primaryLoopControl = 0;
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Student").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int primaryChildrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String studentID = postSnapshot.getKey();
                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(studentID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Student student = dataSnapshot.getValue(Student.class);
                                    final String studentKey = dataSnapshot.getKey();
                                    final String studentFirstName = student.getFirstName();
                                    final String studentLastName = student.getLastName();
                                    final String studentMiddleName = student.getMiddleName();
                                    final String studentPicURL = student.getImageURL();
                                    String searchSubject = studentFirstName + " " + studentLastName + " " + studentMiddleName;

                                    primaryLoopControl++;
                                    if (StringComparer.contains(query, searchSubject)) {
                                        mDatabaseReference = mFirebaseDatabase.getReference("Student Class").child(studentKey);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        String classKey = postSnapshot.getKey();
                                                        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
                                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                                                    SearchResultsRow searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentLastName, classInstance.getClassName(), studentPicURL, "Student");
                                                                    searchResultsRowList.add(searchHistoryRow);

                                                                    if (primaryChildrenCount == primaryLoopControl){

                                                                        if (searchResultsRowList.size() > 0) {
                                                                            Collections.shuffle(searchResultsRowList);
                                                                            mAdapter.notifyDataSetChanged();
                                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                                            progressLayout.setVisibility(View.GONE);
                                                                            errorLayout.setVisibility(View.GONE);
                                                                            recyclerView.setVisibility(View.VISIBLE);
                                                                        } else {
                                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                                            progressLayout.setVisibility(View.GONE);
                                                                            recyclerView.setVisibility(View.GONE);
                                                                        }

                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    if (primaryChildrenCount == primaryLoopControl) {
                                        if (searchResultsRowList.size() > 0) {
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                        } else {
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.GONE);
                                        }
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
                    progressLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void loadFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("School");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    searchResultsRowList.clear();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        School school = postSnapshot.getValue(School.class);
                        String schoolName = school.getSchoolName();

                        if (schoolName.toLowerCase().contains(query.toLowerCase())){
                            String location = school.getLocation() + ", " + school.getState() + ", " + school.getCountry();
                            SearchResultsRow searchHistoryRow = new SearchResultsRow(postSnapshot.getKey(), school.getSchoolName(), location, school.getProfilePhotoUrl(), "School");
                            searchResultsRowList.add(searchHistoryRow);
                            Collections.shuffle(searchResultsRowList);
                        }
                    }

                    if (searchResultsRowList.size() > 0) {
                        firstPass = true;
                    } else {
                        firstPass = false;
                    }
                } else {
                    firstPass = false;
                }

                mDatabaseReference = mFirebaseDatabase.getReference("Teacher Student").child(mFirebaseUser.getUid());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                                mDatabaseReference = mFirebaseDatabase.getReference("Student").child(postSnapshot.getKey());
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            Student student = dataSnapshot.getValue(Student.class);
                                            final String studentKey = dataSnapshot.getKey();
                                            final String studentFirstName = student.getFirstName();
                                            final String studentLastName = student.getLastName();
                                            final String studentPicURL = student.getImageURL();

                                            if (studentFirstName.toLowerCase().contains(query.toLowerCase()) || studentLastName.toLowerCase().contains(query.toLowerCase())){
                                                mDatabaseReference = mFirebaseDatabase.getReference("Student Class").child(studentKey);
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()){
                                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                                String classKey = postSnapshot.getKey();
                                                                mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
                                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()){
                                                                            Class classInstance = dataSnapshot.getValue(Class.class);
                                                                            SearchResultsRow searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentLastName, classInstance.getClassName(), studentPicURL, "Student");
                                                                            searchResultsRowList.add(searchHistoryRow);
                                                                            Collections.shuffle(searchResultsRowList);
                                                                            mySwipeRefreshLayout.setRefreshing(false);
                                                                            progressLayout.setVisibility(View.GONE);
                                                                            errorLayout.setVisibility(View.GONE);
                                                                            recyclerView.setVisibility(View.VISIBLE);
                                                                            mAdapter.notifyDataSetChanged();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                                break;
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                            if (searchResultsRowList.size() > 0) {
                                                mySwipeRefreshLayout.setRefreshing(false);
                                                progressLayout.setVisibility(View.GONE);
                                                recyclerView.setVisibility(View.VISIBLE);
                                                mAdapter.notifyDataSetChanged();
                                            } else {
                                                mySwipeRefreshLayout.setRefreshing(false);
                                                recyclerView.setVisibility(View.GONE);
                                                progressLayout.setVisibility(View.GONE);
                                                errorLayout.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else if (!dataSnapshot.exists() && !firstPass) {
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
