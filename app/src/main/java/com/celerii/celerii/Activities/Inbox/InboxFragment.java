package com.celerii.celerii.Activities.Inbox;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.InboxAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Chats;
import com.celerii.celerii.models.MessageList;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    private ArrayList<MessageList> inboxList;
    public RecyclerView recyclerView;
    public InboxAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    int numberOfResults = 0;

    boolean isLoaded;

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

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

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        inboxList = new ArrayList<>();
        loadFromFirebase();
        mAdapter = new InboxAdapter(inboxList, getContext());
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

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(getContext())) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("Messages Recent").child(mFirebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();

                    inboxList.clear();
                    inboxList.add(new MessageList());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        isLoaded = false;
                        Chats chat = postSnapshot.getValue(Chats.class);
                        final MessageList message = new MessageList();
                        message.setMessage(chat.getMessage());
                        message.setTime(chat.getDatestamp());
                        message.setReceived(chat.isReceived());
                        message.setSeen(chat.isSeen());
                        message.setRecieverID(chat.getRecieverID());
                        message.setSenderID(chat.getSenderID());

                        String otherPartyID = postSnapshot.getKey();
                        message.setOtherParty(otherPartyID);
                        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(otherPartyID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Parent parent = dataSnapshot.getValue(Parent.class);
                                    message.setName(parent.getFirstName() + " " + parent.getLastName());
                                    message.setProfilepicUrl(parent.getProfilePicURL());
                                    inboxList.add(1, message);
                                    mAdapter.notifyDataSetChanged();
                                    isLoaded = true;

                                    if (childrenCount + 1 == inboxList.size()) {
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

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(otherPartyID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && !isLoaded) {
                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                    message.setName(teacher.getFirstName() + " " + teacher.getLastName());
                                    message.setProfilepicUrl(teacher.getProfilePicURL());
                                    inboxList.add(message);
                                    mAdapter.notifyDataSetChanged();
                                    isLoaded = true;

                                    if (childrenCount + 1 == inboxList.size()) {
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

                        mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(otherPartyID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && !isLoaded) {
                                    School school = dataSnapshot.getValue(School.class);
                                    message.setName(school.getSchoolName());
                                    message.setProfilepicUrl(school.getProfilePhotoUrl());
                                    inboxList.add(message);
                                    mAdapter.notifyDataSetChanged();

                                    if (childrenCount + 1 == inboxList.size()) {
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
                    inboxList.add(new MessageList());
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

    @Override
    public void onStart() {
//        loadFromFirebase();
        super.onStart();
    }
}
