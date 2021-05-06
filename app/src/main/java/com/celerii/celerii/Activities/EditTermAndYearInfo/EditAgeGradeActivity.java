package com.celerii.celerii.Activities.EditTermAndYearInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAgeGradeActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar toolbar;
    RadioButton zeroToTwo, twoToFour, fourToSix, sixToEight, eightToTen, tenToTwelve, twelveToFourteen, fourteenToSixteen, sixteenToEighteen;
    RadioGroup ageGradeGroup;

    String selectedAgeGrade;
    String ageGrade;

    String featureUseKey = "";
    String featureName = "Edit Age Grade";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_age_grade);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        ageGrade = b.getString("Age Grade");

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Age Grade");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        zeroToTwo = (RadioButton) findViewById(R.id.zerototwo);
        twoToFour = (RadioButton) findViewById(R.id.twotofour);
        fourToSix = (RadioButton) findViewById(R.id.fourtosix);
        sixToEight = (RadioButton) findViewById(R.id.sixtoeight);
        eightToTen = (RadioButton) findViewById(R.id.eighttoten);
        tenToTwelve = (RadioButton) findViewById(R.id.tentotwelve);
        twelveToFourteen = (RadioButton) findViewById(R.id.twelvetofourteen);
        fourteenToSixteen = (RadioButton) findViewById(R.id.fourteentosixteen);
        sixteenToEighteen = (RadioButton) findViewById(R.id.sixteentoeighteen);
        ageGradeGroup = (RadioGroup) findViewById(R.id.agegradegroup);

        if (ageGrade.equals("0 - 2 years")){
            zeroToTwo.setChecked(true);
            selectedAgeGrade = "0 - 2 years";
        } else if (ageGrade.equals("2 - 4 years")){
            twoToFour.setChecked(true);
            selectedAgeGrade = "2 - 4 years";
        } else if (ageGrade.equals("4 - 6 years")){
            fourToSix.setChecked(true);
            selectedAgeGrade = "4 - 6 years";
        } else if (ageGrade.equals("6 - 8 years")){
            sixToEight.setChecked(true);
            selectedAgeGrade = "6 - 8 years";
        } else if (ageGrade.equals("8 - 10 years")){
            eightToTen.setChecked(true);
            selectedAgeGrade = "8 - 10 years";
        } else if (ageGrade.equals("10 - 12 years")){
            tenToTwelve.setChecked(true);
            selectedAgeGrade = "10 - 12 years";
        } else if (ageGrade.equals("12 - 14 years")){
            twelveToFourteen.setChecked(true);
            selectedAgeGrade = "12 - 14 years";
        } else if (ageGrade.equals("14 - 16 years")){
            fourteenToSixteen.setChecked(true);
            selectedAgeGrade = "14 - 16 years";
        } else {
            sixteenToEighteen.setChecked(true);
            selectedAgeGrade = "16 - 18 years";
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

            if (selectedAgeGrade == null) {return false;}

            if (zeroToTwo.isChecked()){
                selectedAgeGrade = "0 - 2 years";
            } else if (twoToFour.isChecked()){
                selectedAgeGrade = "2 - 4 years";
            }  else if (fourToSix.isChecked()){
                selectedAgeGrade = "4 - 6 years";
            }  else if (sixToEight.isChecked()){
                selectedAgeGrade = "6 - 8 years";
            }  else if (eightToTen.isChecked()){
                selectedAgeGrade = "8 - 10 years";
            }  else if (tenToTwelve.isChecked()){
                selectedAgeGrade = "10 - 12 years";
            }  else if (twelveToFourteen.isChecked()){
                selectedAgeGrade = "12 - 14 years";
            }  else if (fourteenToSixteen.isChecked()){
                selectedAgeGrade = "14 - 16 years";
            } else {
                selectedAgeGrade = "16 - 18 years";
            }

            intent.putExtra("Selected Age Grade", selectedAgeGrade);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}