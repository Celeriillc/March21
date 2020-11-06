package com.celerii.celerii.Activities.Search.Parent;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SearchResultsAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.StringComparer;
import com.celerii.celerii.models.ParentSchoolConnectionRequest;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentSearchResultsStudentFragment extends Fragment {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<SearchResultsRow> searchResultsRowList;
    private HashMap<String, Student> studentMap;
    private ArrayList<String> existingConnections;
    private ArrayList<String> pendingIncomingRequests;
    private ArrayList<String> pendingOutgoingRequests;
    public RecyclerView recyclerView;
    public SearchResultsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String query, key;
    int primaryLoopControl = 0;
    int primaryLoopTerminator = 0;

    String featureUseKey = "";
    String featureName = "Parent Search Results (Student)";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public ParentSearchResultsStudentFragment() {

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

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(context);

        Bundle args = getArguments();
        query = args.getString("Query");
        key = args.getString("Search Key");
        query = query.trim();

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
        studentMap = new HashMap<>();
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
    }

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
                loadNewStudentDataFromFirebase();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    int counter = 0;
    boolean state = true;
    final HashMap<String, Integer> searchMap = new HashMap<>();
    final HashMap<String, Student> searchValueMap = new HashMap<>();
    final HashMap<String, Integer> searchCheckerMap = new HashMap<>();
    String[] queryArray;
    int queryArraySize;

    void loadNewStudentDataFromFirebase() {
        counter = 0;
        searchResultsRowList.clear();
        studentMap.clear();
        searchCheckerMap.clear();

        queryArray = query.toLowerCase().split(" ");
        queryArraySize = queryArray.length;

        if (queryArraySize >= 1) {
            search(0);
        } else {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("There are no students fitting the search criteria. Please check the search term and try again.");
        }
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

                    if (searchResultsRowList.size() > 0) {
                        mySwipeRefreshLayout.setRefreshing(false);
                        progressLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText("There are no students fitting the search criteria. Please check the search term and try again.");
                    }

//
//                    for (int i = 0; i < searchResultsRowList.size(); i++) {
//                        final SearchResultsRow searchHistoryRow = searchResultsRowList.get(i);
//                        final String studentID = parsedStudents.get(i);
//
//                        for (int i = 0; i < searchResultsRowList.size(); i++) {
//                            mAdapter.notifyDataSetChanged();
//                            final SearchResultsRow searchHistoryRow = searchResultsRowList.get(i);
//
//                            mDatabaseReference = mFirebaseDatabase.getReference("School").child(searchHistoryRow.getEntityAddressID());
//                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    String schoolName;
//                                    counterTwo++;
//                                    if (dataSnapshot.exists()) {
//                                        School school = dataSnapshot.getValue(School.class);
//                                        schoolName = school.getSchoolName();
//                                    } else {
//                                        schoolName = "";
//                                    }
//
//                                    searchHistoryRow.setEntityAddress(schoolName);
//                                    if (counterTwo == searchResultsRowList.size()) {
//                                        mySwipeRefreshLayout.setRefreshing(false);
//                                        progressLayout.setVisibility(View.GONE);
//                                        errorLayout.setVisibility(View.GONE);
//                                        recyclerView.setVisibility(View.VISIBLE);
//                                        mAdapter.notifyDataSetChanged();
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
////
////                        mDatabaseReference = mFirebaseDatabase.getReference("Student Parent").child(studentID);
////                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
////                            @Override
////                            public void onDataChange(DataSnapshot dataSnapshot) {
////                                if (dataSnapshot.exists()) {
////                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
////                                        if (!guardians.containsKey(studentID)) {
////                                            guardians.put(studentID, new ArrayList<String>());
////                                            guardians.get(studentID).add(postSnapshot.getKey() + " Parent");
////                                        } else {
////                                            guardians.get(studentID).add(postSnapshot.getKey() + " Parent");
////                                        }
////                                    }
////                                }
////
////                                mDatabaseReference = mFirebaseDatabase.getReference("Student School").child(studentID);
////                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
////                                    @Override
////                                    public void onDataChange(DataSnapshot dataSnapshot) {
////                                        counterOne++;
////                                        if (dataSnapshot.exists()) {
////                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
////                                                searchResultsRowList.get(counterOne - 1).setEntityAddressID(postSnapshot.getKey());
////                                                if (!guardians.containsKey(studentID)) {
////                                                    guardians.put(studentID, new ArrayList<String>());
////                                                    guardians.get(studentID).add(postSnapshot.getKey() + " School");
////                                                } else {
////                                                    guardians.get(studentID).add(postSnapshot.getKey() + " School");
////                                                }
////                                            }
////                                        }
////
////                                        if (counterOne == searchResultsRowList.size()) {
////                                        }
////                                    }
////
////                                    @Override
////                                    public void onCancelled(DatabaseError databaseError) {
////
////                                    }
////                                });
////                            }
////
////                            @Override
////                            public void onCancelled(DatabaseError databaseError) {
////
////                            }
////                        });
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
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;

        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();
        String mFirebaseUserID = mFirebaseUser.getUid();

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        DatabaseReference featureUseUpdateRef = FirebaseDatabase.getInstance().getReference();
        featureUseUpdateRef.updateChildren(featureUseUpdateMap);
    }

    private void search(final int queryTermIndex) {
        if (queryTermIndex < queryArraySize) {
            state = true;
            final String queryTerm = queryArray[queryTermIndex];
            mDatabaseReference = mFirebaseDatabase.getReference().child("Student");
            mDatabaseReference.orderByChild("searchableFirstName").startAt(queryTerm).endAt(queryTerm + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            String key = postSnapshot.getKey();
                            Student student = postSnapshot.getValue(Student.class);
                            if (searchCheckerMap.containsKey(key)) {
                                if (searchCheckerMap.get(key) != queryTermIndex) {
                                    searchCheckerMap.put(key, queryTermIndex);
                                    if (searchMap.containsKey(key)) {
                                        searchMap.put(key, searchMap.get(key) + 1);
                                    } else {
                                        searchMap.put(key, 1);
                                        searchValueMap.put(key, student);
                                    }
                                }
                            } else {
                                searchCheckerMap.put(key, queryTermIndex);
                                if (searchMap.containsKey(key)) {
                                    searchMap.put(key, searchMap.get(key) + 1);
                                } else {
                                    searchMap.put(key, 1);
                                    searchValueMap.put(key, student);
                                }
                            }
                        }

                        if (state) {
                            state = false;
                        }
                    }

                    mDatabaseReference = mFirebaseDatabase.getReference().child("Student");
                    mDatabaseReference.orderByChild("searchableMiddleName").startAt(queryTerm).endAt(queryTerm + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    String key = postSnapshot.getKey();
                                    Student student = postSnapshot.getValue(Student.class);
                                    if (searchCheckerMap.containsKey(key)) {
                                        if (searchCheckerMap.get(key) != queryTermIndex) {
                                            searchCheckerMap.put(key, queryTermIndex);
                                            if (searchMap.containsKey(key)) {
                                                searchMap.put(key, searchMap.get(key) + 1);
                                            } else {
                                                searchMap.put(key, 1);
                                                searchValueMap.put(key, student);
                                            }
                                        }
                                    } else {
                                        searchCheckerMap.put(key, queryTermIndex);
                                        if (searchMap.containsKey(key)) {
                                            searchMap.put(key, searchMap.get(key) + 1);
                                        } else {
                                            searchMap.put(key, 1);
                                            searchValueMap.put(key, student);
                                        }
                                    }
//                                    if (state) {
//                                        if (searchMap.containsKey(key)) {
//                                            searchMap.put(key, searchMap.get(key) + 1);
//                                        } else {
//                                            searchMap.put(key, 1);
//                                            searchValueMap.put(key, student);
//                                        }
//                                    }
                                }

                                if (state) {
                                    state = false;
                                }
                            }

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Student");
                            mDatabaseReference.orderByChild("searchableLastName").startAt(queryTerm).endAt(queryTerm + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                            String key = postSnapshot.getKey();
                                            Student student = postSnapshot.getValue(Student.class);
                                            if (searchCheckerMap.containsKey(key)) {
                                                if (searchCheckerMap.get(key) != queryTermIndex) {
                                                    searchCheckerMap.put(key, queryTermIndex);
                                                    if (searchMap.containsKey(key)) {
                                                        searchMap.put(key, searchMap.get(key) + 1);
                                                    } else {
                                                        searchMap.put(key, 1);
                                                        searchValueMap.put(key, student);
                                                    }
                                                }
                                            } else {
                                                searchCheckerMap.put(key, queryTermIndex);
                                                if (searchMap.containsKey(key)) {
                                                    searchMap.put(key, searchMap.get(key) + 1);
                                                } else {
                                                    searchMap.put(key, 1);
                                                    searchValueMap.put(key, student);
                                                }
                                            }
//                                            if (state) {
//                                                if (searchMap.containsKey(key)) {
//                                                    searchMap.put(key, searchMap.get(key) + 1);
//                                                } else {
//                                                    searchMap.put(key, 1);
//                                                    searchValueMap.put(key, student);
//                                                }
//                                            }
                                        }

                                        if (state) {
                                            state = false;
                                        }
                                    }
                                    counter++;
                                    state = true;

                                    if (queryTermIndex == queryArraySize - 1) {
                                        for (Map.Entry<String, Integer> entry : searchMap.entrySet()) {
                                            String studentKey = entry.getKey();
                                            if (entry.getValue() == queryArraySize) {
                                                Student student = searchValueMap.get(studentKey);
                                                String studentFirstName = student.getFirstName();
                                                String studentLastName = student.getLastName();
                                                String studentMiddleName = student.getMiddleName();
                                                String studentPicURL = student.getImageURL();
                                                SearchResultsRow searchHistoryRow;
                                                if (studentMiddleName.equals("")) {
                                                    searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentLastName, null, studentPicURL, "Student");
                                                } else {
                                                    searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentMiddleName + " " + studentLastName, null, studentPicURL, "Student");
                                                }
                                                searchResultsRowList.add(searchHistoryRow);
                                                studentMap.put(studentKey, student);
//                                                parsedStudents.add(studentKey);
                                            }
                                        }

                                        String numberOfHits = String.valueOf(searchResultsRowList.size());

                                        String day = Date.getDay();
                                        String month = Date.getMonth();
                                        String year = Date.getYear();
                                        String day_month_year = day + "_" + month + "_" + year;
                                        String month_year = month + "_" + year;

                                        HashMap<String, Object> searchUpdateMap = new HashMap<>();
                                        String mFirebaseUserID = mFirebaseUser.getUid();

                                        searchUpdateMap.put("Search Analytics/Search/" + key + "/studentHits", numberOfHits);
                                        searchUpdateMap.put("Search Analytics/Daily Search/" + day_month_year + "/" + key + "/studentHits", numberOfHits);
                                        searchUpdateMap.put("Search Analytics/Monthly Search/" + month_year + "/" + key + "/studentHits", numberOfHits);
                                        searchUpdateMap.put("Search Analytics/Yearly Search/" + year + "/" + key + "/studentHits", numberOfHits);

                                        searchUpdateMap.put("Search Analytics/User Search/" + mFirebaseUserID + "/" + key + "/studentHits", numberOfHits);
                                        searchUpdateMap.put("Search Analytics/User Daily Search/" + mFirebaseUserID + "/" + day_month_year + "/" + key + "/studentHits", numberOfHits);
                                        searchUpdateMap.put("Search Analytics/User Monthly Search/" + mFirebaseUserID + "/" + month_year + "/" + key + "/studentHits", numberOfHits);
                                        searchUpdateMap.put("Search Analytics/User Yearly Search/" + mFirebaseUserID + "/" + year + "/" + key + "/studentHits", numberOfHits);

                                        searchUpdateMap.put("Search Analytics/Search/" + key + "/studentResults", studentMap);
                                        searchUpdateMap.put("Search Analytics/Daily Search/" + day_month_year + "/" + key + "/studentResults", studentMap);
                                        searchUpdateMap.put("Search Analytics/Monthly Search/" + month_year + "/" + key + "/studentResults", studentMap);
                                        searchUpdateMap.put("Search Analytics/Yearly Search/" + year + "/" + key + "/studentResults", studentMap);

                                        searchUpdateMap.put("Search Analytics/User Search/" + mFirebaseUserID + "/" + key + "/studentResults", studentMap);
                                        searchUpdateMap.put("Search Analytics/User Daily Search/" + mFirebaseUserID + "/" + day_month_year + "/" + key + "/studentResults", studentMap);
                                        searchUpdateMap.put("Search Analytics/User Monthly Search/" + mFirebaseUserID + "/" + month_year + "/" + key + "/studentResults", studentMap);
                                        searchUpdateMap.put("Search Analytics/User Yearly Search/" + mFirebaseUserID + "/" + year + "/" + key + "/studentResults", studentMap);

                                        DatabaseReference searchUpdateRef = FirebaseDatabase.getInstance().getReference();
                                        searchUpdateRef.updateChildren(searchUpdateMap);

                                        if (searchResultsRowList.size() > 0) {
                                            mAdapter.notifyDataSetChanged();
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            progressLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                            errorLayout.setVisibility(View.GONE);
                                        } else {
                                            mySwipeRefreshLayout.setRefreshing(false);
                                            recyclerView.setVisibility(View.GONE);
                                            progressLayout.setVisibility(View.GONE);
                                            errorLayout.setVisibility(View.VISIBLE);
                                            errorLayoutText.setText("There are no students fitting the search criteria. Please check the search term and try again.");
                                        }
                                    } else {
                                        search(queryTermIndex + 1);
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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
