package com.celerii.celerii.Activities.Home.Teacher;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TeacherHomeClassAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ManageKidsModel;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherHomeClass extends Fragment {
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<ManageKidsModel> manageKidsModelList;
    public RecyclerView recyclerView;
    public TeacherHomeClassAdapter mAdapter;
    GridLayoutManager mLayoutManager;

    String activeClass;
    String activeClassID;
    String activeClassName;

    public TeacherHomeClass() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.teacher_home_class, menu);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_home_class, container, false);

        sharedPreferencesManager = new SharedPreferencesManager(getContext());

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        activeClass = sharedPreferencesManager.getActiveClass();
        if (activeClass == null) {
            Set<String> classSet = sharedPreferencesManager.getMyClasses();
            ArrayList<String> classes = new ArrayList<>();
            if (classSet != null) {
                classes = new ArrayList<>(classSet);
                activeClass = classes.get(0);
                sharedPreferencesManager.setActiveClass(activeClass);
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayoutText.setText("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                return view;
            }
        }

        activeClassID = activeClass.split(" ")[0];
        activeClassName = activeClass.split(" ")[1];
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new GridLayoutManager(view.getContext(), 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        manageKidsModelList = new ArrayList<>();
        mAdapter = new TeacherHomeClassAdapter(manageKidsModelList, activeClassName, getContext());
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mAdapter.getItemViewType(position)){
                    case 1:
                        return 3;

                    case 2:
                    default:
                        return 1;
                }
            }
        });
        loadFromFirebase();
        recyclerView.setAdapter(mAdapter);

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

    int counter;
    void loadFromFirebase(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        counter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("Class Students").child(activeClassID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(postSnapshot.getKey());
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                counter++;
                                if (dataSnapshot.exists()) {
                                    Student student = dataSnapshot.getValue(Student.class);
                                    String name = student.getFirstName() + " " + student.getLastName();
                                    ManageKidsModel manageKidsModel = new ManageKidsModel(name, student.getImageURL(), dataSnapshot.getKey());
                                    manageKidsModelList.add(manageKidsModel);
                                }

                                if (childrenCount == counter) {
                                    manageKidsModelList.add(0, new ManageKidsModel());
                                    mAdapter.notifyDataSetChanged();
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    progressLayout.setVisibility(View.GONE);
                                    errorLayout.setVisibility(View.GONE);
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
                    errorLayoutText.setText("This class doesn't contain any students");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
