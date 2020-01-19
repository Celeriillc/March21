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
import com.celerii.celerii.helperClasses.StringComparer;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.SearchResultsRow;
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
public class ParentSearchResultsSchoolFragment extends Fragment {

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
        loadSchoolDataFromFirebase();
        mAdapter = new SearchResultsAdapter(searchResultsRowList, getContext(), existingConnections, pendingIncomingRequests, pendingOutgoingRequests);
        recyclerView.setAdapter(mAdapter);

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
                        String searchSubject = schoolName + " " + location;

                        if (StringComparer.contains(query, searchSubject)) {
                            location = school.getLocation() + ", " + school.getState() + ", " + school.getCountry();
                            SearchResultsRow searchHistoryRow = new SearchResultsRow(postSnapshot.getKey(), school.getSchoolName(), location, school.getProfilePhotoUrl(), "School");
                            searchResultsRowList.add(searchHistoryRow);
                        }
                    }

                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
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
}
