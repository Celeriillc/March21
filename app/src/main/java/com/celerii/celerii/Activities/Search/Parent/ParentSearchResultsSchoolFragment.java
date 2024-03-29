package com.celerii.celerii.Activities.Search.Parent;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
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
import com.celerii.celerii.models.NewChatRowModel;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.SearchExistingIncomingAndOutgoingConnections;
import com.celerii.celerii.models.SearchResultsRow;
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
public class ParentSearchResultsSchoolFragment extends Fragment {
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
    private ArrayList<String> schoolMap;
    private HashMap<String, Integer> searchMap = new HashMap<>();
    public RecyclerView recyclerView;
    public SearchResultsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String query, key;

    String featureUseKey = "";
    String featureName = "Parent Search Results (School)";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public ParentSearchResultsSchoolFragment() {
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
        View view = inflater.inflate(R.layout.fragment_parent_search_results_school, container, false);;

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
        schoolMap = new ArrayList<>();
        SearchExistingIncomingAndOutgoingConnections searchExistingIncomingAndOutgoingConnections = new SearchExistingIncomingAndOutgoingConnections();
        mAdapter = new SearchResultsAdapter(searchResultsRowList, getContext(), searchExistingIncomingAndOutgoingConnections);
        recyclerView.setAdapter(mAdapter);
        loadNewSchoolDataFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadNewSchoolDataFromFirebase();
                    }
                }
        );

        return view;
    }

    int counter = 0;
    void loadNewSchoolDataFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        Gson gson = new Gson();
        String studentsSchoolsClassesandTeachersJSON = sharedPreferencesManager.getStudentsSchoolsClassesTeachers();
        Type type = new TypeToken<ArrayList<StudentsSchoolsClassesandTeachersModel>>() {}.getType();
        ArrayList<StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelList = gson.fromJson(studentsSchoolsClassesandTeachersJSON, type);

        if (studentsSchoolsClassesandTeachersModelList == null) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            mySwipeRefreshLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText(Html.fromHtml("None of your children's schools fit the search criteria. Please check the search term and try again."));
        } else {
            counter = 0;
            ArrayList<String> schoolList = new ArrayList<>();
            for (int i = 0; i < studentsSchoolsClassesandTeachersModelList.size(); i++) {
                final StudentsSchoolsClassesandTeachersModel studentsSchoolsClassesandTeachersModel = studentsSchoolsClassesandTeachersModelList.get(i);
                if (!studentsSchoolsClassesandTeachersModel.getSchoolID().isEmpty()) {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(studentsSchoolsClassesandTeachersModel.getSchoolID());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            counter++;
                            if (dataSnapshot.exists()) {
                                School school = dataSnapshot.getValue(School.class);
                                final String schoolID = dataSnapshot.getKey();
                                final String schoolName = school.getSchoolName();
                                final String schoolProfilePictureURL = school.getProfilePhotoUrl();

                                if (StringComparer.contains(query, schoolName)) {
                                    if (!schoolList.contains(schoolID)) {
                                        String location = "";
                                        SearchResultsRow searchHistoryRow = new SearchResultsRow(schoolID, schoolName, location, schoolProfilePictureURL, "School");
                                        if (!school.getIsDeleted()) {
                                            searchResultsRowList.add(searchHistoryRow);
                                        }
                                        schoolList.add(schoolID);
                                    }
                                }
                            }

                            if (counter == studentsSchoolsClassesandTeachersModelList.size()) {
                                sendSearchAnalytics();

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
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    counter++;

                    if (counter == studentsSchoolsClassesandTeachersModelList.size()) {
                        sendSearchAnalytics();

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
                }
            }
        }




//        query = query.toLowerCase();
//
//        searchResultsRowList.clear();
//        schoolMap.clear();
//        searchMap.clear();
//        mAdapter.notifyDataSetChanged();
//
//        mDatabaseReference = mFirebaseDatabase.getReference().child("School");
//        mDatabaseReference.orderByChild("searchableSchoolName").startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                searchResultsRowList.clear();
//                mAdapter.notifyDataSetChanged();
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                        String key = postSnapshot.getKey();
//                        School school = postSnapshot.getValue(School.class);
//                        if (searchMap.containsKey(key)) {
//                            searchMap.put(key, searchMap.get(key) + 1);
//                        } else {
//                            searchMap.put(key, 1);
//                            String location = "";
//                            SearchResultsRow searchHistoryRow = new SearchResultsRow(key, school.getSchoolName(), location, school.getProfilePhotoUrl(), "School");
//                            if (!school.getIsDeleted()) {
//                                searchResultsRowList.add(searchHistoryRow);
//                            }
//                            schoolMap.add(key);
//                        }
//                    }
//                }
//
//                mDatabaseReference = mFirebaseDatabase.getReference().child("School");
//                mDatabaseReference.orderByChild("searchableLocation").startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                                String key = postSnapshot.getKey();
//                                School school = postSnapshot.getValue(School.class);
//                                if (searchMap.containsKey(key)) {
//                                    searchMap.put(key, searchMap.get(key) + 1);
//                                } else {
//                                    searchMap.put(key, 1);
//                                    String location = "";
//                                    SearchResultsRow searchHistoryRow = new SearchResultsRow(key, school.getSchoolName(), location, school.getProfilePhotoUrl(), "School");
//                                    if (!school.getIsDeleted()) {
//                                        searchResultsRowList.add(searchHistoryRow);
//                                    }
//                                    schoolMap.add(key);
//                                }
//                            }
//                        }
//
//                        String numberOfHits = String.valueOf(searchResultsRowList.size());
//
//                        String day = Date.getDay();
//                        String month = Date.getMonth();
//                        String year = Date.getYear();
//                        String day_month_year = day + "_" + month + "_" + year;
//                        String month_year = month + "_" + year;
//
//                        HashMap<String, Object> searchUpdateMap = new HashMap<>();
//                        String mFirebaseUserID = mFirebaseUser.getUid();
//
//                        searchUpdateMap.put("Search Analytics/Search/" + key + "/schoolHits", numberOfHits);
//                        searchUpdateMap.put("Search Analytics/Daily Search/" + day_month_year + "/" + key + "/schoolHits", numberOfHits);
//                        searchUpdateMap.put("Search Analytics/Monthly Search/" + month_year + "/" + key + "/schoolHits", numberOfHits);
//                        searchUpdateMap.put("Search Analytics/Yearly Search/" + year + "/" + key + "/schoolHits", numberOfHits);
//
//                        searchUpdateMap.put("Search Analytics/User Search/" + mFirebaseUserID + "/" + key + "/schoolHits", numberOfHits);
//                        searchUpdateMap.put("Search Analytics/User Daily Search/" + mFirebaseUserID + "/" + day_month_year + "/" + key + "/schoolHits", numberOfHits);
//                        searchUpdateMap.put("Search Analytics/User Monthly Search/" + mFirebaseUserID + "/" + month_year + "/" + key + "/schoolHits", numberOfHits);
//                        searchUpdateMap.put("Search Analytics/User Yearly Search/" + mFirebaseUserID + "/" + year + "/" + key + "/schoolHits", numberOfHits);
//
//                        searchUpdateMap.put("Search Analytics/Search/" + key + "/schoolResults", schoolMap);
//                        searchUpdateMap.put("Search Analytics/Daily Search/" + day_month_year + "/" + key + "/schoolResults", schoolMap);
//                        searchUpdateMap.put("Search Analytics/Monthly Search/" + month_year + "/" + key + "/schoolResults", schoolMap);
//                        searchUpdateMap.put("Search Analytics/Yearly Search/" + year + "/" + key + "/schoolResults", schoolMap);
//
//                        searchUpdateMap.put("Search Analytics/User Search/" + mFirebaseUserID + "/" + key + "/schoolResults", schoolMap);
//                        searchUpdateMap.put("Search Analytics/User Daily Search/" + mFirebaseUserID + "/" + day_month_year + "/" + key + "/schoolResults", schoolMap);
//                        searchUpdateMap.put("Search Analytics/User Monthly Search/" + mFirebaseUserID + "/" + month_year + "/" + key + "/schoolResults", schoolMap);
//                        searchUpdateMap.put("Search Analytics/User Yearly Search/" + mFirebaseUserID + "/" + year + "/" + key + "/schoolResults", schoolMap);
//
//                        DatabaseReference searchUpdateRef = FirebaseDatabase.getInstance().getReference();
//                        searchUpdateRef.updateChildren(searchUpdateMap);
//
//                        if (searchResultsRowList.size() > 0) {
//                            mAdapter.notifyDataSetChanged();
//                            mySwipeRefreshLayout.setRefreshing(false);
//                            progressLayout.setVisibility(View.GONE);
//                            recyclerView.setVisibility(View.VISIBLE);
//                            errorLayout.setVisibility(View.GONE);
//                        } else {
//                            mySwipeRefreshLayout.setRefreshing(false);
//                            recyclerView.setVisibility(View.GONE);
//                            progressLayout.setVisibility(View.GONE);
//                            errorLayout.setVisibility(View.VISIBLE);
//                            errorLayoutText.setText("There are no schools fitting the search criteria. Please check the search term and try again.");
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
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
    }

    void loadSchoolDataFromFirebase(){
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

    void loadFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("School");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    searchResultsRowList.clear();
                    mAdapter.notifyDataSetChanged();
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
