package com.celerii.celerii.Activities.Profiles.SchoolProfile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.GalleryAdapter;
import com.celerii.celerii.adapters.SchoolProfileAwardsAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.Award;
import com.celerii.celerii.models.GalleryModel;
import com.celerii.celerii.models.School;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class SchoolGalleryActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;
    Context context;

    private ArrayList<String> galleryModelList;
    public RecyclerView recyclerView;
    public GalleryAdapter mAdapter;
    GridLayoutManager mLayoutManager;
    Toolbar mtoolbar;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    String schoolID = "", schoolName = "";

    String featureUseKey = "";
    String featureName = "School Profile Gallery";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_gallery);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        context = this;

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        schoolID = bundle.getString("schoolID");

        mtoolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Gallery");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

        galleryModelList = new ArrayList<>();
        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        mAdapter = new GalleryAdapter(galleryModelList, this);
        recyclerView.setAdapter(mAdapter);
        loadImagesFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadImagesFromFirebase();
                    }
                }
        );
    }

    private void loadImagesFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    School school = dataSnapshot.getValue(School.class);
                    schoolName = school.getSchoolName();
                } else {
                    schoolName = "School";
                }

                getSupportActionBar().setTitle(schoolName  + "'s Gallery");
                mDatabaseReference = mFirebaseDatabase.getReference("School Gallery").child(schoolID);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        galleryModelList.clear();
                        mAdapter.notifyDataSetChanged();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String imageURL = postSnapshot.getValue(String.class);
                                galleryModelList.add(imageURL);
                            }

                            Gson gson = new Gson();
                            String urlJson = gson.toJson(galleryModelList);
                            sharedPreferencesManager.setSchoolGallery(urlJson);
                            mAdapter.notifyDataSetChanged();
                            mySwipeRefreshLayout.setRefreshing(false);
                            progressLayout.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            mySwipeRefreshLayout.setRefreshing(false);
                            recyclerView.setVisibility(View.GONE);
                            progressLayout.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                            errorLayoutText.setText(schoolName + " hasn't uploaded any pictures yet.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateDataFromFirebase.populateEssentials(this);
    }

    void yeah() {
//        GalleryModel model = new GalleryModel("http://dailymail.com.ng/wp-content/uploads/2015/03/toolzo.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://www.gistus.com/gs-c/uploads/2013/08/Shuga-Leonora-Okine.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://static.pulse.ng/img/incoming/origs3746832/0355563074-w644-h429/Sharon-Ezeamaka.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://static.pulse.ng/img/incoming/origs3800133/5805561420-w644-h429/Toolz-WCW.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://www.gistus.com/gs-c/uploads/2013/08/Shuga-Dorcas-Shola-Fapson.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://i1.wp.com/thenet.ng/wp-content/uploads/2016/06/tiwa-savage-mavin-e1468221794971-600x589.jpg?resize=600%2C589", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://i0.wp.com/thenet.ng/wp-content/uploads/2012/09/Toolz-photoshoot-with-Moussa-Moussa-10.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://static.pulse.ng/img/incoming/origs3458474/1636366867-w644-h960/Sharon-Chisom-Ezeamaka-plays-Princess-in-Shuga-Pulse.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://1.bp.blogspot.com/-vh7RR6T056I/V0NEHawIf5I/AAAAAAAAGvs/rjnJZugf6DQv0idI_T13a958a8dkcN3LQCKgB/s640/tolu.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://dev.mtvshuga.com/wp-content/uploads/2015/06/Sophie-760x760.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://stargist.com/wp-content/uploads/2016/03/Tiwa-Savage-8-fashionpheeva.png", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://1.bp.blogspot.com/-bGfxgmKs1cE/VTgJULVOBEI/AAAAAAAFBoA/CBpuRSLdM7I/s1600/4.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://www.authorityngr.com/app/views/images/uploads/content_images/2016_08_26_22587.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://3.bp.blogspot.com/-c7dgWpRNPHo/VTgJU3F1FyI/AAAAAAAFBoM/3jA867FTxQY/s1600/5.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://cdn1.dailypost.ng/wp-content/uploads/2015/12/Ini-Edo1.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://www.tori.ng/userfiles/image/2017/feb/06/actress-Rita-Dominic-5.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://dailymail.com.ng/wp-content/uploads/2015/04/Ini-Edo1.jpg", "");
//        galleryModelList.add(model);
//
//        mySwipeRefreshLayout.setRefreshing(false);
//        progressLayout.setVisibility(View.GONE);
//        errorLayout.setVisibility(View.GONE);
//        recyclerView.setVisibility(View.VISIBLE);
    }
}
