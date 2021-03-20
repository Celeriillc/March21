package com.celerii.celerii.Activities.EditPersonalInformationDetails;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
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

public class MaritalStatusEditActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar toolbar;
    RadioButton single, married, widowed, divorced, separated, registeredPartnership;
    RadioGroup radioGroup;

    String featureUseKey = "";
    String featureName = "Edit Relationship Status";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marital_status_edit);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        String maritalstatus = b.getString("maritalstatus");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change your marital status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        single = (RadioButton) findViewById(R.id.single);
        married = (RadioButton) findViewById(R.id.married);
        widowed = (RadioButton) findViewById(R.id.widowed);
        divorced = (RadioButton) findViewById(R.id.divorced);
        separated = (RadioButton) findViewById(R.id.separated);
        registeredPartnership = (RadioButton) findViewById(R.id.registeredpartnership);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

        if (maritalstatus.equals("Single")){
            single.setChecked(true);
        } else if (maritalstatus.equals("Married")){
            married.setChecked(true);
        } else if (maritalstatus.equals("Widowed")){
            widowed.setChecked(true);
        } else if (maritalstatus.equals("Divorced")){
            divorced.setChecked(true);
        } else if (maritalstatus.equals("Separated")){
            separated.setChecked(true);
        } else if (maritalstatus.equals("Registered Partnership")){
            registeredPartnership.setChecked(true);
        } else {
            single.setChecked(true);
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

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_send){
            //TODO: Validate against null values
            Intent intent = new Intent();
            String selectedMaritalStatus;

            if (single.isChecked()){
                selectedMaritalStatus = "Single";
            } else if (married.isChecked()){
                selectedMaritalStatus = "Married";
            } else if (widowed.isChecked()){
                selectedMaritalStatus = "Widowed";
            } else if (divorced.isChecked()){
                selectedMaritalStatus = "Divorced";
            } else if (separated.isChecked()){
                selectedMaritalStatus = "Separated";
            } else {
                selectedMaritalStatus = "Registered Partnership";
            }

            intent.putExtra("selectedmaritalstatus", selectedMaritalStatus);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
