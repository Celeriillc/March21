package com.celerii.celerii.Activities.EditTermAndYearInfo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.AcademicRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EnterResultsEditPercentageOfTotalScoreActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar toolbar;
    TextView captionView, descriptionView;
    EditText editItem;
    Button save;
    Double previousPercentageOfTotal = 0.0;
    String subjectYearTerm, classSubjectYearTerm, activeClass;

    String featureUseKey = "";
    String featureName = "Edit Percentage of Total";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_results_edit_percentage_of_total_score);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        Bundle bundle = getIntent().getExtras();
        previousPercentageOfTotal = bundle.getDouble("PreviousPercentageOfTotal");
        subjectYearTerm = bundle.getString("SubjectYearTerm");
        classSubjectYearTerm = bundle.getString("ClassSubjectYearTerm");
        activeClass = bundle.getString("ClassID");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mDatabaseReference = mFirebaseDatabase.getReference("AcademicRecordClass").child(activeClass).child(subjectYearTerm);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        AcademicRecord academicRecord = postSnapshot.getValue(AcademicRecord.class);
                        previousPercentageOfTotal += Double.valueOf(academicRecord.getPercentageOfTotal());
                    }
                } else {
                    previousPercentageOfTotal = 0.0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Enter Percentage of Total");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        captionView = (TextView) findViewById(R.id.caption);
        descriptionView = (TextView) findViewById(R.id.description);
        editItem = (EditText) findViewById(R.id.edititem);
        save = (Button) findViewById(R.id.save);
        editItem.setText(bundle.getString("PercentageOfTotal"));
        editItem.setSelectAllOnFocus(true);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Validate against null values
                Intent intent = new Intent();
                String percentageOfTotal = editItem.getText().toString().trim();

                if (!validatePercentageOfTotal(percentageOfTotal))
                    return;

                if ((100.0 - previousPercentageOfTotal) < Double.valueOf(percentageOfTotal)){
                    percentageOfTotal = String.valueOf((int)(100.0 - previousPercentageOfTotal));
                }

                if ((Double.valueOf(percentageOfTotal) + previousPercentageOfTotal) > 100.0){
                    CustomToast.whiteBackgroundBottomToast(EnterResultsEditPercentageOfTotalScoreActivity.this, "Error: The percentage of total is more than 100");
                    return;
                }

                intent.putExtra("PercentageOfTotal", percentageOfTotal);
                setResult(RESULT_OK, intent);
                finish();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validatePercentageOfTotal(String percentageOfTotal) {
        if (percentageOfTotal.isEmpty()) {
            String messageString = "You need to enter the percentage this test constitutes of the total.";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            editItem.setSelectAllOnFocus(true);
            return false;
        }

        if (!isNumeric(percentageOfTotal)) {
            String messageString = "You need to enter only numeric values.";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            editItem.setSelectAllOnFocus(true);
            return false;
        }

        if (Integer.valueOf(percentageOfTotal) < 0 || Integer.valueOf(percentageOfTotal) > 100) {
            String messageString = "The Percentage of Total must be a whole number between 0 and 100 (it is a percentage)";
            showDialogWithMessage(messageString);
            editItem.requestFocus();
            editItem.setSelectAllOnFocus(true);
            return false;
        }

        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
