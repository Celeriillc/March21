package com.celerii.celerii.Activities.Settings;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AppUpdateActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar toolbar;
    RadioButton autoUpdate, notify, notAutoUpdate;
    RadioGroup updateGroup;

    String featureUseKey = "";
    String featureName = "App Update Setting";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("App Updates");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        autoUpdate = (RadioButton) findViewById(R.id.autoupdate);
        notify = (RadioButton) findViewById(R.id.notify);
        notAutoUpdate = (RadioButton) findViewById(R.id.notautoupdate);
        updateGroup = (RadioGroup) findViewById(R.id.updategroup);

        if (sharedPreferencesManager.getApplicationUpdateState().equals("0")) {
            autoUpdate.setChecked(true);
        } else if (sharedPreferencesManager.getApplicationUpdateState().equals("1")) {
            notify.setChecked(true);
        } else if (sharedPreferencesManager.getApplicationUpdateState().equals("2")) {
            notAutoUpdate.setChecked(true);
        } else {
            autoUpdate.setChecked(true);
        }
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

        if (autoUpdate.isChecked()) {
            sharedPreferencesManager.setApplicationUpdateState("0");
        } else if (notify.isChecked()) {
            sharedPreferencesManager.setApplicationUpdateState("1");
        } else if (notAutoUpdate.isChecked()) {
            sharedPreferencesManager.setApplicationUpdateState("2");
        } else {
            sharedPreferencesManager.setApplicationUpdateState("0");
        }

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
}
