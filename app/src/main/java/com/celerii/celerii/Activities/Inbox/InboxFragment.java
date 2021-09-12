package com.celerii.celerii.Activities.Inbox;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Inbox.Parent.ParentMessageHome;
import com.celerii.celerii.Activities.Inbox.Teacher.TeacherMessageHome;
import com.celerii.celerii.Activities.Search.Parent.ParentSearchActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.InboxAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.Admin;
import com.celerii.celerii.models.Chats;
import com.celerii.celerii.models.MessageList;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.School;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    FloatingActionButton newMessage;
    AHBottomNavigation bottomNavigation;

    private ArrayList<MessageList> inboxList = new ArrayList<>();
    private ArrayList<MessageList> subList = new ArrayList<>();
    public RecyclerView recyclerView;
    public InboxAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    int inboxCounter = 0;

    String featureUseKey = "";
    String featureName = "Inbox";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        context = getContext();
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);
        newMessage = (FloatingActionButton) view.findViewById(R.id.newmessage);

        try {
            ParentMainActivityTwo activity = (ParentMainActivityTwo) getActivity();
            bottomNavigation = activity.getData();
        } catch (Exception e) {
            TeacherMainActivityTwo activity = (TeacherMainActivityTwo) getActivity();
            bottomNavigation = activity.getData();
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        inboxList.add(new MessageList());
        mAdapter = new InboxAdapter(inboxList, getContext());
        recyclerView.setAdapter(mAdapter);
        loadFromSharedPreferences();
        loadFromFirebase();

        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")){
                    Intent I = new Intent(context, ParentMessageHome.class);
                    Bundle b = new Bundle();
                    b.putString("ID", sharedPreferencesManager.getMyUserID());
                    I.putExtras(b);
                    startActivity(I);
                } else {
                    Intent I = new Intent(context, TeacherMessageHome.class);
                    Bundle b = new Bundle();
                    b.putString("ID", sharedPreferencesManager.getMyUserID());
                    I.putExtras(b);
                    startActivity(I);
                }
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );


        mDatabaseReference = mFirebaseDatabase.getReference().child("Notification Badges").child("General").child(mFirebaseUser.getUid()).child("Inbox");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NotificationBadgeModel notificationBadgeModel = dataSnapshot.getValue(NotificationBadgeModel.class);
                    if (!notificationBadgeModel.getStatus()) {
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                            bottomNavigation.setNotification("", 1);
                        } else if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                            bottomNavigation.setNotification("", 2);
                        }
                    } else {
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                            bottomNavigation.setNotification(" ", 1);
                        } else if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                            bottomNavigation.setNotification(" ", 2);
                        }
                    }
                } else {
                    if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                        bottomNavigation.setNotification("", 1);
                    } else if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                        bottomNavigation.setNotification("", 2);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadFromSharedPreferences() {
        Gson gson = new Gson();
        subList = new ArrayList<>();
        String messagesJSON = sharedPreferencesManager.getMessages();
        Type type = new TypeToken<ArrayList<MessageList>>() {}.getType();
        subList = gson.fromJson(messagesJSON, type);

        if (subList == null) {
            subList = new ArrayList<>();
            inboxList.clear();
            inboxList.add(new MessageList());
            mAdapter.notifyDataSetChanged();
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
        } else {
            inboxList.clear();
            inboxList.addAll(subList);
            inboxList.add(0, new MessageList());
            mAdapter.notifyDataSetChanged();
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            CustomToast.blueBackgroundToast(context, "No Internet");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("Messages Recent").child(mFirebaseUser.getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    inboxList.clear();
                    subList.clear();
                    mAdapter.notifyDataSetChanged();
                    inboxCounter = 0;
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Chats chat = postSnapshot.getValue(Chats.class);
                        final MessageList message = new MessageList();
                        message.setMessage(chat.getMessage());
                        message.setTime(chat.getDatestamp());
                        message.setReceived(chat.isReceived());
                        message.setSeen(chat.isSeen());
                        message.setReceiverID(chat.getReceiverID());
                        message.setSenderID(chat.getSenderID());
                        message.setSortableTime(chat.getSortableDate());

                        String otherPartyID;
                        if (chat.getSenderID().equals(mFirebaseUser.getUid())) {
                            otherPartyID = chat.getReceiverID();
                        } else {
                            otherPartyID = chat.getSenderID();
                        }

                        message.setOtherParty(otherPartyID);
                        subList.add(message);
                    }

                    if (childrenCount == subList.size()) {
                        for (MessageList lcvMessage : subList) {
                            final MessageList newMessage = lcvMessage;
                            String otherPartyID = newMessage.getOtherParty();

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(newMessage.getOtherParty());
                            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Parent parent = dataSnapshot.getValue(Parent.class);
                                        newMessage.setName(parent.getFirstName() + " " + parent.getLastName());
                                        newMessage.setProfilepicUrl(parent.getProfilePicURL());
                                    } else {
                                        newMessage.setName("Deleted User");
                                        newMessage.setProfilepicUrl("");
                                    }

                                    inboxList.add(newMessage);

                                    if (subList.size() == inboxList.size()) {
                                        if (inboxList.size() > 1) {
                                            Collections.sort(inboxList, new Comparator<MessageList>() {
                                                @Override
                                                public int compare(MessageList o1, MessageList o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }
                                        Collections.reverse(inboxList);
                                        inboxList.add(0, new MessageList());
                                        Gson gson = new Gson();
                                        String json = gson.toJson(inboxList);
                                        sharedPreferencesManager.setMessages(json);
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(newMessage.getOtherParty());
                            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        School school = dataSnapshot.getValue(School.class);
                                        newMessage.setName(school.getSchoolName());
                                        newMessage.setProfilepicUrl(school.getProfilePhotoUrl());
                                    } else {
                                        newMessage.setName("Deleted User");
                                        newMessage.setProfilepicUrl("");
                                    }

                                    inboxList.add(newMessage);

                                    if (subList.size() == inboxList.size()) {
                                        if (inboxList.size() > 1) {
                                            Collections.sort(inboxList, new Comparator<MessageList>() {
                                                @Override
                                                public int compare(MessageList o1, MessageList o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }
                                        Collections.reverse(inboxList);
                                        inboxList.add(0, new MessageList());
                                        Gson gson = new Gson();
                                        String json = gson.toJson(inboxList);
                                        sharedPreferencesManager.setMessages(json);
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            mDatabaseReference = mFirebaseDatabase.getReference().child("Admin").child(newMessage.getOtherParty());
                            mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Admin admin = dataSnapshot.getValue(Admin.class);
                                        newMessage.setName(admin.getDisplayName());
                                        newMessage.setProfilepicUrl(admin.getProfilePictureURL());
//                                        newMessage.setOtherParty("Admin");
                                    } else {
                                        newMessage.setName("Deleted User");
                                        newMessage.setProfilepicUrl("");
                                    }

                                    inboxList.add(newMessage);

                                    if (subList.size() == inboxList.size()) {
                                        if (inboxList.size() > 1) {
                                            Collections.sort(inboxList, new Comparator<MessageList>() {
                                                @Override
                                                public int compare(MessageList o1, MessageList o2) {
                                                    return o1.getSortableTime().compareTo(o2.getSortableTime());
                                                }
                                            });
                                        }
                                        Collections.reverse(inboxList);
                                        inboxList.add(0, new MessageList());
                                        Gson gson = new Gson();
                                        String json = gson.toJson(inboxList);
                                        sharedPreferencesManager.setMessages(json);
                                        mAdapter.notifyDataSetChanged();
                                        mySwipeRefreshLayout.setRefreshing(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                } else {
                    inboxList.clear();
                    subList.clear();
                    inboxCounter = 0;

                    Gson gson = new Gson();
                    String json = gson.toJson(subList);
                    sharedPreferencesManager.setMessages(json);
                    inboxList.add(new MessageList());
                    mAdapter.notifyDataSetChanged();
                    mySwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

//        if (!hidden) {
//
//        } else {
//
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Map<String, Object> updateBadgeMap = new HashMap<String, Object>();
        NotificationBadgeModel notificationBadgeModel = new NotificationBadgeModel(false, 0);
        updateBadgeMap.put("Notification Badges/General/" + mFirebaseUser.getUid() + "/Inbox", notificationBadgeModel);
        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(updateBadgeMap);

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
//        UpdateDataFromFirebase.populateEssentials(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
