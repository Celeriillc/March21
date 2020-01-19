package com.celerii.celerii.Activities.Inbox.Teacher;


import android.os.Bundle;
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
import com.celerii.celerii.adapters.NewChatRowAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.NewChatRowModel;
import com.celerii.celerii.models.School;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherSchoolMessageList extends Fragment {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private ArrayList<NewChatRowModel> newChatRowModelList;
    public RecyclerView recyclerView;
    public NewChatRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    int counter;

    public TeacherSchoolMessageList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_school_message_list, container, false);

        sharedPreferencesManager = new SharedPreferencesManager(getContext());

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

        newChatRowModelList = new ArrayList<>();
        mAdapter = new NewChatRowAdapter(newChatRowModelList, getContext());
        recyclerView.setAdapter(mAdapter);
        loadFromFirebase();

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

    void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        counter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher School").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String schoolID = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                counter++;
                                if (dataSnapshot.exists()){
                                    School school = dataSnapshot.getValue(School.class);
                                    String schoolName = school.getSchoolName();
                                    final String relationship = "";

                                    NewChatRowModel newChatRowModel = new NewChatRowModel(schoolName, relationship, school.getProfilePhotoUrl(), schoolID);
                                    newChatRowModelList.add(newChatRowModel);

                                    if (counter == childrenCount) {
                                        Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
                                            @Override
                                            public int compare(NewChatRowModel o1, NewChatRowModel o2) {
                                                return o1.getName().compareTo(o2.getName());
                                            }
                                        });
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        progressLayout.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    counter++;
                                    if (counter == childrenCount) {
                                        Collections.sort(newChatRowModelList, new Comparator<NewChatRowModel>() {
                                            @Override
                                            public int compare(NewChatRowModel o1, NewChatRowModel o2) {
                                                return o1.getName().compareTo(o2.getName());
                                            }
                                        });
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
                                        progressLayout.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
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
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("You don't have any Schools to message at this time");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
