package com.celerii.celerii.Activities.Search.Parent;


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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SearchResultsAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.StringComparer;
import com.celerii.celerii.models.ParentSchoolConnectionRequest;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.SearchResultsRow;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentSearchResultsStudentFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

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

    public ParentSearchResultsStudentFragment() {
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
        View view = inflater.inflate(R.layout.fragment_parent_search_results_student, container, false);

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

        errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        searchResultsRowList = new ArrayList<>();
        existingConnections = new ArrayList<>();
        pendingIncomingRequests = new ArrayList<>();
        pendingOutgoingRequests = new ArrayList<>();
        loadExistingConnections();
        mAdapter = new SearchResultsAdapter(searchResultsRowList, getContext(), existingConnections, pendingIncomingRequests, pendingOutgoingRequests);
        recyclerView.setAdapter(mAdapter);

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
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        existingConnections = new ArrayList<>();
        pendingIncomingRequests = new ArrayList<>();
        pendingOutgoingRequests = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference("Parents Students").child(mFirebaseUser.getUid());
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
        mDatabaseReference = mFirebaseDatabase.getReference("Student Connection Request Recipients").child(mFirebaseUser.getUid());
        mDatabaseReference.orderByChild("requestStatus").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        pendingIncomingRequests.add(postSnapshot.getValue(ParentSchoolConnectionRequest.class).getStudentID());
                    }
                }

                mAdapter.notifyDataSetChanged();
                loadPendingOutgoingRequests();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//
//        mDatabaseReference = mFirebaseDatabase.getReference("School To Parent Request Parent").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        pendingIncomingRequests.add(postSnapshot.getKey());
//                    }
//                }
//
//                mAdapter.notifyDataSetChanged();
//                loadPendingOutgoingRequests();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    int loopControl;
    void loadPendingOutgoingRequests(){
        mDatabaseReference = mFirebaseDatabase.getReference("Student Connection Request Sender").child(mFirebaseUser.getUid());
        mDatabaseReference.orderByChild("requestStatus").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        pendingOutgoingRequests.add(postSnapshot.getValue(ParentSchoolConnectionRequest.class).getStudentID());
                    }
                }

                mAdapter.notifyDataSetChanged();
                loadStudentDataFromFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//
//        loopControl = 0;
//        mDatabaseReference = mFirebaseDatabase.getReference("Parent To School Request Parent").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        String childKey = postSnapshot.getKey();
//                        loopControl++;
//
//                        mDatabaseReference = mFirebaseDatabase.getReference("Parent To School Request Parent").child(mFirebaseUser.getUid()).child(childKey);
//                        mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    ParentSchoolConnectionRequest parentSchoolConnectionRequest = dataSnapshot.getValue(ParentSchoolConnectionRequest.class);
//                                    String childID = parentSchoolConnectionRequest.getStudentID();
//                                    pendingOutgoingRequests.add(childID);
//                                }
//
//                                if (childrenCount == loopControl) {
//                                    mAdapter.notifyDataSetChanged();
//                                    loadStudentDataFromFirebase();
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                    }
//                } else {
//                    mAdapter.notifyDataSetChanged();
//                    loadStudentDataFromFirebase();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    int counterOne = 0, counterTwo = 0;
    ArrayList<String> parsedStudents = new ArrayList<>();
    HashMap<String, ArrayList<String>> guardians = new HashMap<>();
    void loadStudentDataFromFirebase() {
        counterOne = 0;
        counterTwo = 0;
        parsedStudents = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference("Student");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    searchResultsRowList.clear();
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
                                searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentLastName, null, studentPicURL, "Student");
                            } else {
                                searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentMiddleName + " " + studentLastName, null, studentPicURL, "Student");
                            }
                            searchResultsRowList.add(searchHistoryRow);
                            parsedStudents.add(studentKey);
                        }
                    }

                    for (int i = 0; i < searchResultsRowList.size(); i++) {
                        final SearchResultsRow searchHistoryRow = searchResultsRowList.get(i);
                        final String studentID = parsedStudents.get(i);

                        mDatabaseReference = mFirebaseDatabase.getReference("Student Parent").child(studentID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (!guardians.containsKey(studentID)) {
                                            guardians.put(studentID, new ArrayList<String>());
                                            guardians.get(studentID).add(postSnapshot.getKey() + " Parent");
                                        } else {
                                            guardians.get(studentID).add(postSnapshot.getKey() + " Parent");
                                        }
                                    }
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference("Student School").child(studentID);
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        counterOne++;
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                searchResultsRowList.get(counterOne - 1).setEntityAddressID(postSnapshot.getKey());
                                                if (!guardians.containsKey(studentID)) {
                                                    guardians.put(studentID, new ArrayList<String>());
                                                    guardians.get(studentID).add(postSnapshot.getKey() + " School");
                                                } else {
                                                    guardians.get(studentID).add(postSnapshot.getKey() + " School");
                                                }
                                            }
                                        }

                                        if (counterOne == searchResultsRowList.size()) {
                                            for (int i = 0; i < searchResultsRowList.size(); i++) {
                                                mAdapter.guardians = guardians;
                                                mAdapter.notifyDataSetChanged();
                                                final SearchResultsRow searchHistoryRow = searchResultsRowList.get(i);

                                                mDatabaseReference = mFirebaseDatabase.getReference("School").child(searchHistoryRow.getEntityAddressID());
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String schoolName;
                                                        counterTwo++;
                                                        if (dataSnapshot.exists()) {
                                                            School school = dataSnapshot.getValue(School.class);
                                                            schoolName = school.getSchoolName();
                                                        } else {
                                                            schoolName = "";
                                                        }

                                                        searchHistoryRow.setEntityAddress(schoolName);
                                                        if (counterTwo == searchResultsRowList.size()) {
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
                                            }
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

//                    primaryLoopTerminator = 0;
//                    if (primaryChildrenCount == primaryLoopControl) {
//                        if (searchResultsRowList.size() > 0) {
//                            primaryLoopControl = 0;
//                            for (int i = 0; i < searchResultsRowList.size(); i++) {
//                                final SearchResultsRow searchHistoryRow = searchResultsRowList.get(i);
//                                String studentID = searchHistoryRow.getEntityId();
//                                mDatabaseReference = mFirebaseDatabase.getReference("Student School").child(studentID);
//                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        if (dataSnapshot.exists()) {
//                                            primaryLoopControl++;
//                                        }
//                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                            final String schoolKey = postSnapshot.getKey();
//                                            mDatabaseReference = mFirebaseDatabase.getReference("School").child(schoolKey);
//                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    String schoolName;
//                                                    if (dataSnapshot.exists()) {
//                                                        School school = dataSnapshot.getValue(School.class);
//                                                        schoolName = school.getSchoolName();
//                                                    } else {
//                                                        schoolName = "";
//                                                    }
//                                                    searchHistoryRow.setEntityAddressID(schoolKey);
//                                                    searchHistoryRow.setEntityAddress(schoolName);
//                                                    primaryLoopTerminator++;
//
//                                                    if (primaryLoopTerminator == primaryLoopControl) {
//                                                        Collections.shuffle(searchResultsRowList);
//                                                        mySwipeRefreshLayout.setRefreshing(false);
//                                                        progressLayout.setVisibility(View.GONE);
//                                                        errorLayout.setVisibility(View.GONE);
//                                                        recyclerView.setVisibility(View.VISIBLE);
//                                                        mAdapter.notifyDataSetChanged();
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                            break;
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        } else {
//                            mySwipeRefreshLayout.setRefreshing(false);
//                            progressLayout.setVisibility(View.GONE);
//                            recyclerView.setVisibility(View.GONE);
//                        }
//                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("There are no students fitting the search criteria. Please check the search term and try again.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
