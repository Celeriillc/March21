package com.celerii.celerii.Activities.ELibrary.Parent;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ELibraryMyAssignmentAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ELibraryMyAssignmentModel;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ParentMyAssignmentFragment extends Fragment {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    public RecyclerView recyclerView;
    private ArrayList<ELibraryMyAssignmentModel> eLibraryMyAssignmentModelList;
    public ELibraryMyAssignmentAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String activeStudentID = "";
    String activeStudent = "";
    String activeStudentName;

    String featureUseKey = "";
    String featureName = "Parent My Assignment";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    public ParentMyAssignmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_my_assignment, container, false);

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
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) view.findViewById(R.id.progresslayout);

        ParentELibraryHomeActivity activity = (ParentELibraryHomeActivity) getActivity();
        activeStudent = activity.getData();

        if (activeStudent == null) {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Gson gson = new Gson();
                ArrayList<Student> myChildren = new ArrayList<>();
                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                Type type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                myChildren = gson.fromJson(myChildrenJSON, type);

                if (myChildren != null) {
                    if (myChildren.size() > 0) {
                        gson = new Gson();
                        activeStudent = gson.toJson(myChildren.get(0));
                        sharedPreferencesManager.setActiveKid(activeStudent);
                    } else {
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                        return view;
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                    return view;
                }
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText("We couldn't find this student's account");
                return view;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {}.getType();
                Student activeKidModel = gson.fromJson(activeStudent, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {}.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student: myChildren) {
                    if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                        activeKidExist = true;
                        activeKidModel = student;
                        activeStudent = gson.toJson(activeKidModel);
                        sharedPreferencesManager.setActiveKid(activeStudent);
                        break;
                    }
                }

                if (!activeKidExist) {
                    if (myChildren.size() > 0) {
                        if (myChildren.size() > 1) {
                            gson = new Gson();
                            activeStudent = gson.toJson(myChildren.get(0));
                            sharedPreferencesManager.setActiveKid(activeStudent);
                        }
                    } else {
                        mySwipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                        return view;
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeStudentModel = gson.fromJson(activeStudent, type);

        activeStudentID = activeStudentModel.getStudentID();
        activeStudentName = activeStudentModel.getFirstName() + " " + activeStudentModel.getLastName();

        mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        eLibraryMyAssignmentModelList = new ArrayList<>();
        mAdapter = new ELibraryMyAssignmentAdapter(eLibraryMyAssignmentModelList, activeStudent,"ParentMyAssignment", context);
        recyclerView.setAdapter(mAdapter);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadAssignmentsFromFirebase();
                    }
                }
        );

        return view;
    }

    private void loadAssignmentsFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        updateBadges();
        mDatabaseReference = mFirebaseDatabase.getReference("E Library Assignment").child("Student").child(activeStudentID);
        mDatabaseReference.orderByChild("submitted").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eLibraryMyAssignmentModelList.clear();
                mAdapter.notifyDataSetChanged();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        final ELibraryMyAssignmentModel eLibraryMyAssignmentModel = postSnapshot.getValue(ELibraryMyAssignmentModel.class);
                        eLibraryMyAssignmentModelList.add(eLibraryMyAssignmentModel);
                    }

                    if (eLibraryMyAssignmentModelList.size() > 1) {
                        Collections.sort(eLibraryMyAssignmentModelList, new Comparator<ELibraryMyAssignmentModel>() {
                            @Override
                            public int compare(ELibraryMyAssignmentModel o1, ELibraryMyAssignmentModel o2) {
                                return o1.getSortableDateGiven().compareTo(o2.getSortableDateGiven());
                            }
                        });
                    }

                    Collections.reverse(eLibraryMyAssignmentModelList);
                    mAdapter.notifyDataSetChanged();
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You don't have any pending assignments yet."));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateBadges() {
        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();
            updateBadgesMap.put("ELibraryAssignmentParentNotification/" + mFirebaseUser.getUid() + "/" + activeStudentID + "/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
            mDatabaseReference = mFirebaseDatabase.getReference();
            mDatabaseReference.updateChildren(updateBadgesMap);
        }
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

    @Override
    public void onResume() {
        super.onResume();
        loadAssignmentsFromFirebase();
    }
}