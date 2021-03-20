package com.celerii.celerii.Activities.EditTermAndYearInfo;

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

public class EnterResultsEditTermActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar toolbar;
    RadioButton firstTerm, secondTerm, thirdTerm, other;
    RadioGroup termGroup;
    String selectedTerm;
    String term;

    String featureUseKey = "";
    String featureName = "Edit Term";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results_edit_term);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        term = b.getString("Term");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Term");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        firstTerm = (RadioButton) findViewById(R.id.firstterm);
        secondTerm = (RadioButton) findViewById(R.id.secondterm);
        thirdTerm = (RadioButton) findViewById(R.id.thirdterm);
        other = (RadioButton) findViewById(R.id.other);
        termGroup = (RadioGroup) findViewById(R.id.termgroup);

        if (term.equals("1")){
            firstTerm.setChecked(true);
            selectedTerm = "1";
        } else if (term.equals("2")){
            secondTerm.setChecked(true);
            selectedTerm = "2";
        } else if (term.equals("3")){
            thirdTerm.setChecked(true);
            selectedTerm = "3";
        } else {
            other.setChecked(true);
            selectedTerm = "0";
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
            Intent intent = new Intent();

            if (selectedTerm == null) {return false;}

            if (firstTerm.isChecked()){
                selectedTerm = "1";
            } else if (secondTerm.isChecked()){
                selectedTerm = "2";
            } else if (thirdTerm.isChecked()){
                selectedTerm = "3";
            } else {
                selectedTerm = "0";
            }

            intent.putExtra("Selected Term", selectedTerm);
            setResult(RESULT_OK, intent);
            finish();
        }
        else if (id == R.id.action_send){
            //TODO: Validate against null values
            Intent intent = new Intent();

            if (selectedTerm == null) {return false;}

            if (firstTerm.isChecked()){
                selectedTerm = "1";
            } else if (secondTerm.isChecked()){
                selectedTerm = "2";
            } else if (thirdTerm.isChecked()){
                selectedTerm = "3";
            } else {
                selectedTerm = "0";
            }

            intent.putExtra("Selected Term", selectedTerm);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
