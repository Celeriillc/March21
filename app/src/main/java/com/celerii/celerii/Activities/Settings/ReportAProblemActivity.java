package com.celerii.celerii.Activities.Settings;

import android.app.Dialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.models.ReportFeatureModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ReportAProblemActivity extends AppCompatActivity {
    Context context;;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    TextView title, hint;
    EditText setTitle, body;
    Button sendReport;
    String feature = "";

    String featureUseKey = "";
    String featureName = "Report a Problem Main";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_aproblem);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        Bundle bundle = getIntent().getExtras();
        feature = bundle.getString("problemTitle").trim();

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report " + feature);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = (TextView) findViewById(R.id.problemtitle);
        hint = (TextView) findViewById(R.id.problemhint);
        body = (EditText) findViewById(R.id.problem);
        setTitle = (EditText) findViewById(R.id.setproblemtitle);
        sendReport = (Button) findViewById(R.id.sendreport);

        title.setText(feature);

        if (feature.equals("Other")){
            setTitle.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);
        }

        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString);
                    return;
                }

                if (feature.equals("")){
                    if (title.getText().toString().trim().equals("")){
                        feature = "No title";
                    } else {
                        feature = title.getText().toString();
                    }
                }

                final String message = body.getText().toString().trim();
                if (!validate(message, "body", body))
                    return;

                final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(context);
                progressDialog.show();

                String date = Date.getDate();
                String year = Date.getYear();
                String month = Date.getMonth();
                String day = Date.getDay();
                boolean seen = false;
                boolean responded = false;
                DatabaseReference reportKey = mFirebaseDatabase.getReference("Customer Feedback").child("Report Problem").child(mFirebaseUser.getUid()).push();
                String pushKey = reportKey.getKey();
                DatabaseReference reportRef = mFirebaseDatabase.getReference();
                ReportFeatureModel reportFeatureModel = new ReportFeatureModel(feature, "Android", message, mFirebaseUser.getUid(), sharedPreferencesManager.getActiveAccount(), mFirebaseUser.getEmail(), date, year, month, day, seen, responded);
                Map<String, Object> newReportFeatureMap = new HashMap<String, Object>();
                newReportFeatureMap.put("Customer Feedback/Report Problem/" + pushKey + "/", reportFeatureModel);
                reportRef.updateChildren(newReportFeatureMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            progressDialog.dismiss();
                            CustomToast.blueBackgroundToast(ReportAProblemActivity.this, "Report sent, Thank you");
                            finish();
                        } else {
                            progressDialog.dismiss();
                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                            ShowDialogWithMessage.showDialogWithMessage(context, message);
                        }
                    }
                });
            }
        });
    }

    private boolean validate(String string, String type, EditText editText) {
        if (string.isEmpty()) {
            String messageString = "The " + type + " of your report is empty";
            showDialogWithMessage(messageString);
            editText.requestFocus();
            return false;
        }

        return true;
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
}
