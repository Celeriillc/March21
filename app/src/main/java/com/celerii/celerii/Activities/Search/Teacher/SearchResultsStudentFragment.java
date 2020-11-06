package com.celerii.celerii.Activities.Search.Teacher;


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
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.SearchResultsRow;
import com.celerii.celerii.models.Student;
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
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsStudentFragment extends Fragment {
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    ArrayList<SearchResultsRow> searchResultsRowList;
    HashMap<String, Student> studentMap;
    HashMap<String, SearchResultsRow> searchResultsRowMap;
    ArrayList<String> existingConnections;
    ArrayList<String> pendingIncomingRequests;
    ArrayList<String> pendingOutgoingRequests;
    ArrayList<ClassesStudentsAndParentsModel> classesStudentsModelList;
    public RecyclerView recyclerView;
    public SearchResultsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    int primaryLoopControl, secondaryLoopControl;
    String query, key;
    int counter = 0;

    String featureUseKey = "";
    String featureName = "Teacher Search Results (Student)";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public SearchResultsStudentFragment() {
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

        View view = inflater.inflate(R.layout.fragment_search_results_student, container, false);

        sharedPreferencesManager = new SharedPreferencesManager(getContext());

        Bundle args = getArguments();
        query = args.getString("Query");
        key = args.getString("Search Key");

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
        searchResultsRowMap = new HashMap<>();
        existingConnections = new ArrayList<>();
        pendingIncomingRequests = new ArrayList<>();
        pendingOutgoingRequests = new ArrayList<>();
        loadStudentDataFromFirebase();
        mAdapter = new SearchResultsAdapter(searchResultsRowList, getContext(), existingConnections, pendingIncomingRequests, pendingOutgoingRequests);
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadStudentDataFromFirebase();
                    }
                }
        );


        return view;
    }

    void loadStudentDataFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        counter = 0;
        Gson gson = new Gson();
        classesStudentsModelList = new ArrayList<>();
        String classStudentJSON = sharedPreferencesManager.getClassesStudent();
        Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
        classesStudentsModelList = gson.fromJson(classStudentJSON, type);

        if (classesStudentsModelList == null) {
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("There are no students fitting the search criteria. Please check the search term and try again.");
        } else {
            for (int i = 0; i < classesStudentsModelList.size(); i++) {
                final ClassesStudentsAndParentsModel classesStudentsModel = classesStudentsModelList.get(i);
                mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(classesStudentsModel.getStudentID());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        counter++;
                        if (dataSnapshot.exists()) {
                            final Student student = dataSnapshot.getValue(Student.class);
                            final String studentKey = dataSnapshot.getKey();
                            final String studentFirstName = student.getFirstName();
                            final String studentLastName = student.getLastName();
                            final String studentMiddleName = student.getMiddleName();
                            final String studentPicURL = student.getImageURL();
                            String searchSubject = studentFirstName + " " + studentLastName + " " + studentMiddleName;

                            if (StringComparer.contains(query, searchSubject)) {
                                mDatabaseReference = mFirebaseDatabase.getReference().child("Class").child(classesStudentsModel.getClassID());
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Class classModel = dataSnapshot.getValue(Class.class);
                                            final String classID = dataSnapshot.getKey();
                                            final String className = classModel.getClassName();
                                            SearchResultsRow searchHistoryRow;
                                            if (studentMiddleName.isEmpty()) {
                                                searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentLastName, className, studentPicURL, "Student");
                                            } else {
                                                searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentMiddleName + " " + studentLastName, className, studentPicURL, "Student");
                                            }
                                            if (!searchResultsRowMap.containsKey(studentKey)) {
                                                searchResultsRowMap.put(studentKey, searchHistoryRow);
                                                searchResultsRowList.add(searchHistoryRow);
                                                studentMap.put(studentKey, student);
                                            }

                                            if (counter == classesStudentsModelList.size()) {
                                                sendSearchAnalytics();
                                                if (searchResultsRowList.size() > 0) {
                                                    //Collections.shuffle(searchResultsRowList);
                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                } else {
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.VISIBLE);
                                                    errorLayoutText.setText("There are no students fitting the search criteria. Please check the search term and try again.");
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                if (counter == classesStudentsModelList.size()) {
                                    sendSearchAnalytics();
                                    if (searchResultsRowList.size() > 0) {
                                        //Collections.shuffle(searchResultsRowList);
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        progressLayout.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    } else {
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        progressLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.VISIBLE);
                                        errorLayoutText.setText("There are no students fitting the search criteria. Please check the search term and try again.");
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
//
//        primaryLoopControl = 0;
//        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Student").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    searchResultsRowList.clear();
//                    final int primaryChildrenCount = (int) dataSnapshot.getChildrenCount();
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        String studentID = postSnapshot.getKey();
//                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(studentID);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()) {
//                                    Student student = dataSnapshot.getValue(Student.class);
//                                    final String studentKey = dataSnapshot.getKey();
//                                    final String studentFirstName = student.getFirstName();
//                                    final String studentLastName = student.getLastName();
//                                    final String studentMiddleName = student.getMiddleName();
//                                    final String studentPicURL = student.getImageURL();
//                                    String searchSubject = studentFirstName + " " + studentLastName + " " + studentMiddleName;
//
//                                    primaryLoopControl++;
//                                    if (StringComparer.contains(query, searchSubject)) {
//                                        mDatabaseReference = mFirebaseDatabase.getReference("Student Class").child(studentKey);
//                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot.exists()) {
//                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                                        String classKey = postSnapshot.getKey();
//                                                        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
//                                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                if (dataSnapshot.exists()) {
//                                                                    Class classInstance = dataSnapshot.getValue(Class.class);
//                                                                    SearchResultsRow searchHistoryRow = new SearchResultsRow(studentKey, studentFirstName + " " + studentLastName, classInstance.getClassName(), studentPicURL, "Student");
//                                                                    searchResultsRowList.add(searchHistoryRow);
//
//                                                                    if (primaryChildrenCount == primaryLoopControl){
//
//                                                                        if (searchResultsRowList.size() > 0) {
//                                                                            Collections.shuffle(searchResultsRowList);
//                                                                            mAdapter.notifyDataSetChanged();
//                                                                            mySwipeRefreshLayout.setRefreshing(false);
//                                                                            progressLayout.setVisibility(View.GONE);
//                                                                            errorLayout.setVisibility(View.GONE);
//                                                                            recyclerView.setVisibility(View.VISIBLE);
//                                                                        } else {
//                                                                            mySwipeRefreshLayout.setRefreshing(false);
//                                                                            progressLayout.setVisibility(View.GONE);
//                                                                            recyclerView.setVisibility(View.GONE);
//                                                                        }
//
//                                                                    }
//                                                                }
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(DatabaseError databaseError) {
//
//                                                            }
//                                                        });
//                                                        break;
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                    }
//
//                                    if (primaryChildrenCount == primaryLoopControl) {
//                                        if (searchResultsRowList.size() > 0) {
//                                            mySwipeRefreshLayout.setRefreshing(false);
//                                            progressLayout.setVisibility(View.GONE);
//                                            errorLayout.setVisibility(View.GONE);
//                                            recyclerView.setVisibility(View.VISIBLE);
//                                        } else {
//                                            mySwipeRefreshLayout.setRefreshing(false);
//                                            progressLayout.setVisibility(View.GONE);
//                                            recyclerView.setVisibility(View.GONE);
//                                        }
//                                    }
//
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

    private void sendSearchAnalytics() {
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
}
