package com.celerii.celerii.Activities.Search.Teacher;


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
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.SchoolTeacherConnectionRequest;
import com.celerii.celerii.models.SearchResultsRow;
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
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsSchoolFragment extends Fragment {
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
    private HashMap<String, School> schoolMap;
    private HashMap<String, Integer> searchMap = new HashMap<>();
    private ArrayList<String> existingConnections;
    private ArrayList<String> pendingIncomingRequests;
    private ArrayList<String> pendingOutgoingRequests;
    public RecyclerView recyclerView;
    public SearchResultsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String query, key;

    String featureUseKey = "";
    String featureName = "Teacher Search Results (School)";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public SearchResultsSchoolFragment() {
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

        View view = inflater.inflate(R.layout.fragment_search_results_school, container, false);

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
        schoolMap = new HashMap<>();
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

    int loopControl = 0;
    void loadPendingIncomingRequests(){
        loopControl = 0;
        mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String schoolKey = postSnapshot.getKey();
                        loopControl++;

                        mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(schoolKey);
                        mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        SchoolTeacherConnectionRequest schoolTeacherConnectionRequest = postSnapshot.getValue(SchoolTeacherConnectionRequest.class);
                                        String schoolID = schoolTeacherConnectionRequest.getSchool();
                                        pendingIncomingRequests.add(schoolID);
                                    }
                                }

                                if (childrenCount == loopControl) {
                                    mAdapter.notifyDataSetChanged();
                                    loadPendingOutgoingRequests();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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
                                    loadNewFromFirebase();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    mAdapter.notifyDataSetChanged();
                    loadNewFromFirebase();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void loadNewFromFirebase() {
        query = query.toLowerCase();

        searchResultsRowList.clear();
        schoolMap.clear();
        searchMap.clear();

        mDatabaseReference = mFirebaseDatabase.getReference().child("School");
        mDatabaseReference.orderByChild("searchableSchoolName").startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchResultsRowList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        School school = postSnapshot.getValue(School.class);
                        if (searchMap.containsKey(key)) {
                            searchMap.put(key, searchMap.get(key) + 1);
                        } else {
                            searchMap.put(key, 1);
                            String location = school.getLocation() + ", " + school.getState() + ", " + school.getCountry();
                            SearchResultsRow searchHistoryRow = new SearchResultsRow(key, school.getSchoolName(), location, school.getProfilePhotoUrl(), "School");
                            if (!school.getDeleted()) {
                                searchResultsRowList.add(searchHistoryRow);
                            }
                            schoolMap.put(key, school);
                        }
                    }
                }

                mDatabaseReference = mFirebaseDatabase.getReference().child("School");
                mDatabaseReference.orderByChild("searchableLocation").startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                String key = postSnapshot.getKey();
                                School school = postSnapshot.getValue(School.class);
                                if (searchMap.containsKey(key)) {
                                    searchMap.put(key, searchMap.get(key) + 1);
                                } else {
                                    searchMap.put(key, 1);
                                    String location = school.getLocation() + ", " + school.getState() + ", " + school.getCountry();
                                    SearchResultsRow searchHistoryRow = new SearchResultsRow(key, school.getSchoolName(), location, school.getProfilePhotoUrl(), "School");
                                    if (!school.getDeleted()) {
                                        searchResultsRowList.add(searchHistoryRow);
                                    }
                                    schoolMap.put(key, school);
                                }
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

                        searchUpdateMap.put("Search Analytics/Search/" + key + "/schoolHits", numberOfHits);
                        searchUpdateMap.put("Search Analytics/Daily Search/" + day_month_year + "/" + key + "/schoolHits", numberOfHits);
                        searchUpdateMap.put("Search Analytics/Monthly Search/" + month_year + "/" + key + "/schoolHits", numberOfHits);
                        searchUpdateMap.put("Search Analytics/Yearly Search/" + year + "/" + key + "/schoolHits", numberOfHits);

                        searchUpdateMap.put("Search Analytics/User Search/" + mFirebaseUserID + "/" + key + "/schoolHits", numberOfHits);
                        searchUpdateMap.put("Search Analytics/User Daily Search/" + mFirebaseUserID + "/" + day_month_year + "/" + key + "/schoolHits", numberOfHits);
                        searchUpdateMap.put("Search Analytics/User Monthly Search/" + mFirebaseUserID + "/" + month_year + "/" + key + "/schoolHits", numberOfHits);
                        searchUpdateMap.put("Search Analytics/User Yearly Search/" + mFirebaseUserID + "/" + year + "/" + key + "/schoolHits", numberOfHits);

                        searchUpdateMap.put("Search Analytics/Search/" + key + "/schoolResults", schoolMap);
                        searchUpdateMap.put("Search Analytics/Daily Search/" + day_month_year + "/" + key + "/schoolResults", schoolMap);
                        searchUpdateMap.put("Search Analytics/Monthly Search/" + month_year + "/" + key + "/schoolResults", schoolMap);
                        searchUpdateMap.put("Search Analytics/Yearly Search/" + year + "/" + key + "/schoolResults", schoolMap);

                        searchUpdateMap.put("Search Analytics/User Search/" + mFirebaseUserID + "/" + key + "/schoolResults", schoolMap);
                        searchUpdateMap.put("Search Analytics/User Daily Search/" + mFirebaseUserID + "/" + day_month_year + "/" + key + "/schoolResults", schoolMap);
                        searchUpdateMap.put("Search Analytics/User Monthly Search/" + mFirebaseUserID + "/" + month_year + "/" + key + "/schoolResults", schoolMap);
                        searchUpdateMap.put("Search Analytics/User Yearly Search/" + mFirebaseUserID + "/" + year + "/" + key + "/schoolResults", schoolMap);

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
                            errorLayoutText.setText("There are no schools fitting the search criteria. Please check the search term and try again.");
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
                        String location = school.getLocation();
                        String state = school.getState();
                        String searchSubject = schoolName + " " + location + " " + state;

                        if (StringComparer.contains(query, searchSubject)) {
                            location = school.getLocation() + ", " + school.getState() + ", " + school.getCountry();
                            SearchResultsRow searchHistoryRow = new SearchResultsRow(postSnapshot.getKey(), school.getSchoolName(), location, school.getProfilePhotoUrl(), "School");
                            searchResultsRowList.add(searchHistoryRow);
                            Collections.shuffle(searchResultsRowList);
                        }
                    }

                    if (searchResultsRowList.size() > 0) {
                        mySwipeRefreshLayout.setRefreshing(false);
                        progressLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText("There are no schools fitting the search criteria. Please check the search term and try again.");
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("There are no schools fitting the search criteria. Please check the search term and try again.");
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
}
