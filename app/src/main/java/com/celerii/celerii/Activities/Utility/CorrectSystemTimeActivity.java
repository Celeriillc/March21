package com.celerii.celerii.Activities.Utility;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.HashMap;

public class CorrectSystemTimeActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;

    ImageView icon;
    TextView title, text;
    Button setDeviceTime;
    Boolean buttonClicked = false;

    String featureUseKey = "";
    String featureName = "Correct System Time";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_system_time);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Incorrect Device Time");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        icon = (ImageView) findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.title);
        text = (TextView) findViewById(R.id.text);
        setDeviceTime = (Button) findViewById(R.id.setdevicetime);

        setDeviceTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                butonClicked = true;
//                startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (buttonClicked) {
//            Intent nextIntent = new Intent(CorrectSystemTimeActivity.this, ApplicationLaunchActivity.class);
//            ProcessPhoenix.triggerRebirth(this, nextIntent);
//            ProcessPhoenix.triggerRebirth(this);
//        }
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
}
