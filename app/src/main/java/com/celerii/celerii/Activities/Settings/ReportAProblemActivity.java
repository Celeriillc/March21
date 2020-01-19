package com.celerii.celerii.Activities.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.models.ReportFeatureModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ReportAProblemActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    TextView title, hint;
    EditText setTitle, body;
    String feature = "";
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_aproblem);

        Bundle bundle = getIntent().getExtras();
        feature = bundle.getString("problemTitle");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report " + feature);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        title = (TextView) findViewById(R.id.problemtitle);
        hint = (TextView) findViewById(R.id.problemhint);
        body = (EditText) findViewById(R.id.problem);
        setTitle = (EditText) findViewById(R.id.setproblemtitle);

        title.setText(feature);

        if (feature.equals("Other")){
            setTitle.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);
            return;
        }

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
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
            //TODO; Ensure we save updated data to database
            if (feature.equals("")){
                if (title.getText().toString().trim().equals("")){
                    feature = "No title";
                } else {
                    feature = title.getText().toString();
                }
            }

            final String message = body.getText().toString().trim();
            if (message.equals("")) {
                CustomToast.whiteBackgroundBottomToast(this, "Error: The report section is empty");
                return false;
            }

            if (connected){
                String date = Date.getDate();
                String year = Date.getYear();
                String month = Date.getMonth();
                String day = Date.getDay();
                boolean seen = false;
                boolean responded = false;
                DatabaseReference reportKey = mFirebaseDatabase.getReference("Customer Feedback").child("Report Problem").push();
                String pushKey = reportKey.getKey();
                DatabaseReference reportRef = mFirebaseDatabase.getReference();
                ReportFeatureModel reportFeatureModel = new ReportFeatureModel(feature, message, mFirebaseUser.getUid(), mFirebaseUser.getEmail(), date, year, month, day, seen, responded);
                Map<String, Object> newReportFeatureMap = new HashMap<String, Object>();
                newReportFeatureMap.put("Customer Feedback/Report Problem/" + pushKey + "/", reportFeatureModel);
                reportRef.updateChildren(newReportFeatureMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        CustomToast.whiteBackgroundBottomToast(ReportAProblemActivity.this, "Report sent, Thank you");
                    }
                });
            } else {
                CustomToast.whiteBackgroundBottomToast(ReportAProblemActivity.this, "Connection lost, check and try again");
            }

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
