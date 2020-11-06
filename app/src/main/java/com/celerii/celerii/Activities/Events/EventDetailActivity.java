package com.celerii.celerii.Activities.Events;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Day;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Time;
import com.celerii.celerii.models.EventsRow;
import com.celerii.celerii.models.School;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class EventDetailActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    Toolbar toolbar;
    LinearLayout background;
    TextView eventTitle, eventDate, eventTime, eventSchool, eventDescription;

    String eventID, parentActivity;
    int colorNumber = 0;

    String featureUseKey = "";
    String featureName = "Event Detail";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle b = getIntent().getExtras();
        eventID = b.getString("Event ID");
        colorNumber = Integer.valueOf(b.getString("Color Number"));
        parentActivity = b.getString("parentActivity");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        background = (LinearLayout) findViewById(R.id.background);
        eventTitle = (TextView) findViewById(R.id.eventtitle);
        eventDate = (TextView) findViewById(R.id.eventdate);
        eventTime = (TextView) findViewById(R.id.eventtime);
        eventSchool = (TextView) findViewById(R.id.eventschool);
        eventDescription = (TextView) findViewById(R.id.eventdescription);

        progressLayout.setVisibility(View.VISIBLE);
        background.setVisibility(View.GONE);

        loadDetailsFromFirebase();
    }

    private void loadDetailsFromFirebase() {
        updateBadges();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Event").child(eventID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final EventsRow eventRow = dataSnapshot.getValue(EventsRow.class);
                    eventRow.setKey(dataSnapshot.getKey());

                    String schoolID = eventRow.getSchoolID();
                    mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(schoolID);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                School schoolInstance = dataSnapshot.getValue(School.class);
                                eventRow.setSchoolID(schoolInstance.getSchoolName());
                            } else {
                                eventRow.setSchoolID("This school account has been deleted or doesn't exist");
                            }
                            if (colorNumber == 0) { background.setBackground(ContextCompat.getDrawable(context, R.drawable.event_card_primary_purple)); }
                            else if (colorNumber == 1) { background.setBackground(ContextCompat.getDrawable(context, R.drawable.event_card_accent)); }
                            else if (colorNumber == 2) { background.setBackground(ContextCompat.getDrawable(context, R.drawable.event_card_instagram_blue)); }
                            else { background.setBackground(ContextCompat.getDrawable(context, R.drawable.event_card_accent_secondary)); }

                            String[] datearray = eventRow.getEventDate().split(" ")[0].split("/");
                            Calendar c = Calendar.getInstance();
                            c.set(Integer.parseInt(datearray[0]), Integer.parseInt(datearray[1]) - 1, Integer.parseInt(datearray[2]));
                            final int day = c.get(Calendar.DAY_OF_WEEK);

                            eventTitle.setText(eventRow.getEventTitle());
                            getSupportActionBar().setTitle(eventRow.getEventTitle());
                            eventDate.setText(Day.Day(day) + ", " + Date.DateFormatMMDDYYYY(eventRow.getEventDate()));
                            eventTime.setText(Time.TimeFormatHHMM(eventRow.getEventDate()));
                            eventSchool.setText(eventRow.getSchoolID());
                            eventDescription.setText(eventRow.getEventDescription());

                            progressLayout.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.GONE);
                            background.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    progressLayout.setVisibility(View.GONE);
                    background.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("This event has been deleted");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
                    Intent i = new Intent(this, ParentMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "3");
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (parentActivity.equals("Teacher")) {
                    Intent i = new Intent(this, TeacherMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "4");
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateBadges(){
        HashMap<String, Object> updateBadgesMap = new HashMap<String, Object>();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Events/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/Notifications/status", false);
            updateBadgesMap.put("Notification Badges/Parents/" + mFirebaseUser.getUid() + "/More/status", false);
        } else {
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/Events/status", false);
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/Notifications/status", false);
            updateBadgesMap.put("Notification Badges/Teachers/" + mFirebaseUser.getUid() + "/More/status", false);
        }

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(updateBadgesMap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                Intent i = new Intent(this, ParentMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "3");
                i.putExtras(bundle);
                startActivity(i);
            } else if (parentActivity.equals("Teacher")) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "4");
                i.putExtras(bundle);
                startActivity(i);
            }
        }
    }
}
