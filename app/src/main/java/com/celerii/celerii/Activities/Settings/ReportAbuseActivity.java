package com.celerii.celerii.Activities.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.models.ReportAbuseModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ReportAbuseActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Bundle bundle;
    String userID, userName;
    private Toolbar toolbar;
    TextView header, hint;
    EditText message;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_abuse);

        bundle = getIntent().getExtras();
        userID = bundle.getString("userID");
        userName = bundle.getString("name");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Report User");

        header = (TextView) findViewById(R.id.reportheader);
        hint = (TextView) findViewById(R.id.reportuserhint);
        message = (EditText) findViewById(R.id.reportmessage);

        header.setText("Report " + userName);

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
            final String report = message.getText().toString().trim();
            if (report.equals("")) {
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
                DatabaseReference reportKey = mFirebaseDatabase.getReference("Customer Feedback").child("Report User").push();
                String pushKey = reportKey.getKey();
                DatabaseReference reportRef = mFirebaseDatabase.getReference();
                ReportAbuseModel reportAbuseModel = new ReportAbuseModel(header.getText().toString(), report, mFirebaseUser.getUid(), mFirebaseUser.getEmail(), userID, date, year, month, day, seen, responded);
                Map<String, Object> newReportFeatureMap = new HashMap<String, Object>();
                newReportFeatureMap.put("Customer Feedback/Report User/" + pushKey + "/", reportAbuseModel);
                reportRef.updateChildren(newReportFeatureMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        CustomToast.whiteBackgroundBottomToast(ReportAbuseActivity.this, "Report sent, Thank you");
                    }
                });
            } else {
                CustomToast.whiteBackgroundBottomToast(ReportAbuseActivity.this, "Connection lost, check and try again");
            }

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
