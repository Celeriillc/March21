package com.celerii.celerii.Activities.ELibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ELibraryBooksListAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.ELibraryMaterialsModel;
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

public class TeacherELibraryBooksListActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;
    Context context;

    private ArrayList<ELibraryMaterialsModel> eLibraryMaterialsModelList;
    private ArrayList<String> eLibraryMaterialsModelListKeys;
    public RecyclerView recyclerView;
    public ELibraryBooksListAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Toolbar mtoolbar;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Bundle bundle;
    String requestType;
    int schoolCount;

    String featureUseKey = "";
    String featureName = "Teacher E Library Books List";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_e_library_books_list);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        requestType = bundle.getString("requestType");

        mtoolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (requestType.equals("MyBooks")) {
            getSupportActionBar().setTitle("My Books");
        } else {
            getSupportActionBar().setTitle("Recommended Books");
        }
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        eLibraryMaterialsModelList = new ArrayList<>();
        eLibraryMaterialsModelListKeys = new ArrayList<>();

        mAdapter = new ELibraryBooksListAdapter(eLibraryMaterialsModelList, this);
        recyclerView.setAdapter(mAdapter);

        if (requestType.equals("MyBooks")) {
            loadMyBooksFromFirebase();
            featureName = "Teacher E Library Books List (My Books)";
        } else {
            loadRecommendedBooksFromFirebase();
            featureName = "Teacher E Library Books List (Recommended Books)";
        }

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (requestType.equals("MyBooks")) {
                            loadMyBooksFromFirebase();
                        } else {
                            loadRecommendedBooksFromFirebase();
                        }
                    }
                }
        );

        errorLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ELibraryUploadBookActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private void loadMyBooksFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        schoolCount = 0;
        eLibraryMaterialsModelList.clear();
        eLibraryMaterialsModelListKeys.clear();

        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher School").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        final String schoolID = postSnapshot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Private Materials").child("School").child(schoolID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                schoolCount++;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        ELibraryMaterialsModel eLibraryMaterialsModel = postSnapshot.getValue(ELibraryMaterialsModel.class);
                                        eLibraryMaterialsModel.setMaterialID(postSnapshot.getKey());
                                        if (!eLibraryMaterialsModelListKeys.contains(eLibraryMaterialsModel.getMaterialID())) {
                                            eLibraryMaterialsModelList.add(eLibraryMaterialsModel);
                                            eLibraryMaterialsModelListKeys.add(eLibraryMaterialsModel.getMaterialID());
                                        }
                                    }
                                }

                                if (schoolCount == childrenCount) {
                                    mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Private Materials").child("Teacher").child(mFirebaseUser.getUid());
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                    ELibraryMaterialsModel eLibraryMaterialsModel = postSnapshot.getValue(ELibraryMaterialsModel.class);
                                                    eLibraryMaterialsModel.setMaterialID(postSnapshot.getKey());
                                                    if (!eLibraryMaterialsModelListKeys.contains(eLibraryMaterialsModel.getMaterialID())) {
                                                        eLibraryMaterialsModelList.add(eLibraryMaterialsModel);
                                                        eLibraryMaterialsModelListKeys.add(eLibraryMaterialsModel.getMaterialID());
                                                    }
                                                }
                                            }

                                            if (eLibraryMaterialsModelList.size() > 0) {
                                                if (eLibraryMaterialsModelList.size() > 1) {
                                                    Collections.sort(eLibraryMaterialsModelList, new Comparator<ELibraryMaterialsModel>() {
                                                        @Override
                                                        public int compare(ELibraryMaterialsModel o1, ELibraryMaterialsModel o2) {
                                                            return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                        }
                                                    });
                                                }

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
                                                if (requestType.equals("MyBooks")) {
                                                    errorLayoutText.setText("You haven't uploaded any instructional materials (books, videos or audio books) yet. Click the Upload a Book button or the Floating Add button below to add a material");
                                                    errorLayoutButton.setText("Upload a Book");
                                                    errorLayoutButton.setVisibility(View.VISIBLE);
                                                } else {
                                                    errorLayoutText.setText("The books, videos and audio books we recommend for you will appear here");
                                                    errorLayoutButton.setVisibility(View.GONE);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Private Materials").child("Teacher").child(mFirebaseUser.getUid());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    ELibraryMaterialsModel eLibraryMaterialsModel = postSnapshot.getValue(ELibraryMaterialsModel.class);
                                    eLibraryMaterialsModel.setMaterialID(postSnapshot.getKey());
                                    if (!eLibraryMaterialsModelListKeys.contains(eLibraryMaterialsModel.getMaterialID())) {
                                        eLibraryMaterialsModelList.add(eLibraryMaterialsModel);
                                        eLibraryMaterialsModelListKeys.add(eLibraryMaterialsModel.getMaterialID());
                                    }
                                }
                            }

                            if (eLibraryMaterialsModelList.size() > 0) {
                                if (eLibraryMaterialsModelList.size() > 1) {
                                    Collections.sort(eLibraryMaterialsModelList, new Comparator<ELibraryMaterialsModel>() {
                                        @Override
                                        public int compare(ELibraryMaterialsModel o1, ELibraryMaterialsModel o2) {
                                            return o1.getSortableDate().compareTo(o2.getSortableDate());
                                        }
                                    });
                                }

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
                                if (requestType.equals("MyBooks")) {
                                    errorLayoutText.setText("You haven't uploaded any instructional materials (books, videos or audio books) yet. Click the Upload a Book button or the Floating Add button below to add a material");
                                    errorLayoutButton.setText("Upload a Book");
                                    errorLayoutButton.setVisibility(View.VISIBLE);
                                } else {
                                    errorLayoutText.setText("The books, videos and audio books we recommend for you will appear here");
                                    errorLayoutButton.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadRecommendedBooksFromFirebase() {

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateDataFromFirebase.populateEssentials(this);
    }
}