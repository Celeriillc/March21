package com.celerii.celerii.Activities.Newsletters;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.NewsletterRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.NewsletterRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class NewsletterRowActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    private ArrayList<NewsletterRow> newsletterRowList;
    public RecyclerView recyclerView;
    public NewsletterRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    String string = "Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor  Lorem ipsum dolor sit amet, nsectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor" +
            "Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor  Lorem ipsum dolor sit amet, nsectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor Lorem ipsum dolor sit amet, consectet adipisc elit, sed do eiusmod tempor";

    String featureUseKey = "";
    String featureName = "Newsletter Home";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsletter_row);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Newsletter");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        newsletterRowList = new ArrayList<>();
        yeah();
        mAdapter = new NewsletterRowAdapter(newsletterRowList, this);
        recyclerView.setAdapter(mAdapter);
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

    public void updateBadges(){
        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Newsletter/status", false);
        } else {
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/Newsletter/status", false);
        }

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(updateBadgesMap);
    }

    @Override
    protected void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;

        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();
        String mFirebaseUserID = mFirebaseUser.getUid();

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        DatabaseReference featureUseUpdateRef = FirebaseDatabase.getInstance().getReference();
        featureUseUpdateRef.updateChildren(featureUseUpdateMap);
    }

    void yeah(){
        NewsletterRow newsletterRow = new NewsletterRow("New first newsletter, it's a test", string, "Lorem Ipsum High School", "May 27, 2019", "https://si.wsj.net/public/resources/images/MI-BX240_DWEEK_G_20130716135136.jpg", 434, 231, 32);
        newsletterRowList.add(newsletterRow);

        newsletterRow = new NewsletterRow("New first newsletter, it's a test", string, "Lorem Ipsum High School", "May 27, 2019", "http://www.foreverlawn.com/wp-content/uploads/2015/11/DSC9083.jpg", 434, 231, 32);
        newsletterRowList.add(newsletterRow);

        newsletterRow = new NewsletterRow("New first newsletter, it's a test", string, "Lorem Ipsum High School", "May 27, 2019", "http://www.foreverlawn.com/wp-content/uploads/2015/11/DSC9084.jpg", 434, 231, 32);
        newsletterRowList.add(newsletterRow);
    }
}
