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
import com.celerii.celerii.models.SearchExistingIncomingAndOutgoingConnections;
import com.celerii.celerii.models.SearchResultsRow;
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
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentSearchResultsTeacherFragment extends Fragment {
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
    private HashMap<String, Teacher> teacherMap;
    private ArrayList<String> existingConnections;
    private ArrayList<String> pendingIncomingRequests;
    private ArrayList<String> pendingOutgoingRequests;
    ArrayList<StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelList;
    public RecyclerView recyclerView;
    public SearchResultsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    int counter = 0;
    String query, key;

    String featureUseKey = "";
    String featureName = "Parent Search Results (Teacher)";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public ParentSearchResultsTeacherFragment() {
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
        View view = inflater.inflate(R.layout.fragment_parent_search_results_teacher, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(context);

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
        teacherMap = new HashMap<>();
        SearchExistingIncomingAndOutgoingConnections searchExistingIncomingAndOutgoingConnections = new SearchExistingIncomingAndOutgoingConnections();
        mAdapter = new SearchResultsAdapter(searchResultsRowList, getContext(), searchExistingIncomingAndOutgoingConnections);
        recyclerView.setAdapter(mAdapter);
        loadTeacherDataFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadTeacherDataFromFirebase();
                    }
                }
        );

        return view;
    }


    ArrayList<String> studentsList = new ArrayList<>();
    HashMap<String, SearchResultsRow> teachersList = new HashMap<>();
    int studentCount = 0;
    void loadTeacherDataFromFirebase() {
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
        studentsSchoolsClassesandTeachersModelList = new ArrayList<>();
        String studentClassTeacherJSON = sharedPreferencesManager.getStudentsSchoolsClassesTeachers();
        Type type = new TypeToken<ArrayList<StudentsSchoolsClassesandTeachersModel>>() {}.getType();
        studentsSchoolsClassesandTeachersModelList = gson.fromJson(studentClassTeacherJSON, type);

        if (studentsSchoolsClassesandTeachersModelList == null) {
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("There are no teachers fitting the search criteria. Please check the search term and try again.");
        } else {
            for (int i = 0; i < studentsSchoolsClassesandTeachersModelList.size(); i++) {
                final StudentsSchoolsClassesandTeachersModel studentsSchoolsClassesandTeachersModel = studentsSchoolsClassesandTeachersModelList.get(i);
                if (!studentsSchoolsClassesandTeachersModel.getTeacherID().isEmpty()) {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(studentsSchoolsClassesandTeachersModel.getTeacherID());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            counter++;
                            if (dataSnapshot.exists()) {
                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                String teacherKey = dataSnapshot.getKey();
                                String teacherName = teacher.getLastName() + " " + teacher.getFirstName();
                                String teacherFirstName = teacher.getFirstName();
                                String teacherLastName = teacher.getLastName();
                                String teacherMiddleName = teacher.getMiddleName();
                                String teacherPicURL = teacher.getProfilePicURL();
                                String searchSubject = teacherLastName + " " + teacherFirstName + " " + teacherMiddleName;

                                if (StringComparer.contains(query, searchSubject)) {
                                    if (!teachersList.containsKey(teacherKey)) {
                                        SearchResultsRow searchResultsRow = new SearchResultsRow(teacherKey, searchSubject, "", teacherPicURL, "Teacher");
                                        if (!teacher.getDeleted()) {
                                            searchResultsRowList.add(searchResultsRow);
                                        }
                                        teacherMap.put(teacherKey, teacher);
                                        teachersList.put(teacherKey, searchResultsRow);
                                    }

                                    if (counter == studentsSchoolsClassesandTeachersModelList.size()) {
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
                                            errorLayoutText.setText("There are no teachers fitting the search criteria. Please check the search term and try again.");
                                        }
                                    }
                                } else {
                                    if (counter == studentsSchoolsClassesandTeachersModelList.size()) {
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
                                            errorLayoutText.setText("There are no teachers fitting the search criteria. Please check the search term and try again.");
                                        }
                                    }
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
                            errorLayoutText.setText("There are no teachers fitting the search criteria. Please check the search term and try again.");
                        }
                    }
                }
            }
        }


//
//        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        studentsList.add(postSnapshot.getKey());
//                    }
//
//                    for (String student : studentsList) {
//
//                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Teacher").child(student);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                studentCount++;
//                                if (dataSnapshot.exists()) {
//                                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
//                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                        final String teacherKey = postSnapshot.getKey();
//                                        teachersList.add(teacherKey);
//                                    }
//                                }
//
//                                if (studentCount == studentsList.size()) {
//
//                                    if (teachersList.size() > 0) {
//                                        for (final String teacherID : teachersList) {
//
//                                            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(teacherID);
//                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot.exists()){
//                                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
//                                                        String teacherName = teacher.getLastName() + " " + teacher.getFirstName();
//                                                        String searchSubject = teacher.getLastName() + " " + teacher.getFirstName() + " " + teacher.getMiddleName();
//
//                                                        if (StringComparer.contains(query, searchSubject)) {
//                                                            SearchResultsRow searchResultsRow = new SearchResultsRow(teacherID, teacherName, "", teacher.getProfilePicURL(), "Teacher");
//                                                            searchResultsRowList.add(searchResultsRow);
//                                                        }
//                                                    }
//
//                                                    Collections.shuffle(searchResultsRowList);
//                                                    mySwipeRefreshLayout.setRefreshing(false);
//                                                    progressLayout.setVisibility(View.GONE);
//                                                    errorLayout.setVisibility(View.GONE);
//                                                    recyclerView.setVisibility(View.VISIBLE);
//                                                    mAdapter.notifyDataSetChanged();
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        }
//                                    } else {
//                                        mySwipeRefreshLayout.setRefreshing(false);
//                                        recyclerView.setVisibility(View.GONE);
//                                        progressLayout.setVisibility(View.GONE);
//                                        errorLayout.setVisibility(View.VISIBLE);
//                                    }
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
//                    mySwipeRefreshLayout.setRefreshing(false);
//                    recyclerView.setVisibility(View.GONE);
//                    progressLayout.setVisibility(View.GONE);
//                    errorLayout.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//
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

    private void sendSearchAnalytics() {
        String numberOfHits = String.valueOf(searchResultsRowList.size());

        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;

        HashMap<String, Object> searchUpdateMap = new HashMap<>();
        String mFirebaseUserID = mFirebaseUser.getUid();

        searchUpdateMap.put("Search Analytics/Search/" + key + "/teacherHits", numberOfHits);
        searchUpdateMap.put("Search Analytics/Daily Search/" + day_month_year + "/" + key + "/teacherHits", numberOfHits);
        searchUpdateMap.put("Search Analytics/Monthly Search/" + month_year + "/" + key + "/teacherHits", numberOfHits);
        searchUpdateMap.put("Search Analytics/Yearly Search/" + year + "/" + key + "/teacherHits", numberOfHits);

        searchUpdateMap.put("Search Analytics/User Search/" + mFirebaseUserID + "/" + key + "/teacherHits", numberOfHits);
        searchUpdateMap.put("Search Analytics/User Daily Search/" + mFirebaseUserID + "/" + day_month_year + "/" + key + "/teacherHits", numberOfHits);
        searchUpdateMap.put("Search Analytics/User Monthly Search/" + mFirebaseUserID + "/" + month_year + "/" + key + "/teacherHits", numberOfHits);
        searchUpdateMap.put("Search Analytics/User Yearly Search/" + mFirebaseUserID + "/" + year + "/" + key + "/teacherHits", numberOfHits);

        searchUpdateMap.put("Search Analytics/Search/" + key + "/teacherList", teacherMap);
        searchUpdateMap.put("Search Analytics/Daily Search/" + day_month_year + "/" + key + "/teacherList", teacherMap);
        searchUpdateMap.put("Search Analytics/Monthly Search/" + month_year + "/" + key + "/teacherList", teacherMap);
        searchUpdateMap.put("Search Analytics/Yearly Search/" + year + "/" + key + "/teacherList", teacherMap);

        searchUpdateMap.put("Search Analytics/User Search/" + mFirebaseUserID + "/" + key + "/teacherList", teacherMap);
        searchUpdateMap.put("Search Analytics/User Daily Search/" + mFirebaseUserID + "/" + day_month_year + "/" + key + "/teacherList", teacherMap);
        searchUpdateMap.put("Search Analytics/User Monthly Search/" + mFirebaseUserID + "/" + month_year + "/" + key + "/teacherList", teacherMap);
        searchUpdateMap.put("Search Analytics/User Yearly Search/" + mFirebaseUserID + "/" + year + "/" + key + "/teacherList", teacherMap);

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
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }
}
