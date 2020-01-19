package com.celerii.celerii.Activities.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.celerii.celerii.R;
import com.celerii.celerii.models.TeacherPrivacyModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PrivacySettingsTeacher extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar toolbar;
    HashMap<String, Object> teacherPrivacyUpdate;
    boolean timelineShowStatus, locationShowStatus, phoneNumberShowStatus, maritalStatusShowStatus;

    RadioButton hideTimeline, showTimeline, hideLocation, showLocation, hidePhoneNumber, showPhoneNumber, hideMaritalStatus, showMaritalStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings_teacher);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Privacy Settings");

        hideTimeline = (RadioButton) findViewById(R.id.hidetimeline);
        showTimeline = (RadioButton) findViewById(R.id.showtimeline);
        hideLocation = (RadioButton) findViewById(R.id.hidelocation);
        showLocation = (RadioButton) findViewById(R.id.showlocation);
        hidePhoneNumber = (RadioButton) findViewById(R.id.hidephonenumber);
        showPhoneNumber = (RadioButton) findViewById(R.id.showphonenumber);
        hideMaritalStatus = (RadioButton) findViewById(R.id.hidemaritalstatus);
        showMaritalStatus = (RadioButton) findViewById(R.id.showmaritalstatus);

        loadCurrentPrivacyState();

        hideTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentPrivacyState();
            }
        });

        showTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentPrivacyState();
            }
        });

        hideLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentPrivacyState();
            }
        });

        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentPrivacyState();
            }
        });

        hidePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentPrivacyState();
            }
        });

        showPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentPrivacyState();
            }
        });

        hideMaritalStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentPrivacyState();
            }
        });

        showMaritalStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentPrivacyState();
            }
        });
    }

    void loadCurrentPrivacyState(){
        mDatabaseReference = mFirebaseDatabase.getReference().child("TeacherPrivacySettings").child(auth.getCurrentUser().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TeacherPrivacyModel teacherPrivacyModel = dataSnapshot.getValue(TeacherPrivacyModel.class);
                    timelineShowStatus = teacherPrivacyModel.isTimeline();
                    locationShowStatus = teacherPrivacyModel.isLocation();
                    phoneNumberShowStatus = teacherPrivacyModel.isPhoneNumber();
                    maritalStatusShowStatus = teacherPrivacyModel.isMaritalStatus();

                    if (timelineShowStatus){
                        showTimeline.setChecked(true);
                    } else {
                        hideTimeline.setChecked(true);
                    }

                    if (locationShowStatus){
                        showLocation.setChecked(true);
                    } else {
                        hideLocation.setChecked(true);
                    }

                    if (phoneNumberShowStatus){
                        showPhoneNumber.setChecked(true);
                    } else {
                        hidePhoneNumber.setChecked(true);
                    }

                    if (maritalStatusShowStatus){
                        showMaritalStatus.setChecked(true);
                    } else {
                        hideMaritalStatus.setChecked(true);
                    }

                } else {
                    showTimeline.setChecked(true);
                    showLocation.setChecked(true);
                    showPhoneNumber.setChecked(true);
                    showMaritalStatus.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void saveCurrentPrivacyState(){
        if (hideTimeline.isChecked()){
            timelineShowStatus = false;
        } else {
            timelineShowStatus = true;
        }

        if (hideLocation.isChecked()){
            locationShowStatus = false;
        } else {
            locationShowStatus = true;
        }

        if (hidePhoneNumber.isChecked()){
            phoneNumberShowStatus = false;
        } else {
            phoneNumberShowStatus = true;
        }

        if (hideMaritalStatus.isChecked()){
            maritalStatusShowStatus = false;
        } else {
            maritalStatusShowStatus = true;
        }

        final TeacherPrivacyModel teacherPrivacyModel = new TeacherPrivacyModel(timelineShowStatus, locationShowStatus, phoneNumberShowStatus, maritalStatusShowStatus);

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherPrivacyUpdate = new HashMap<String, Object>();
                teacherPrivacyUpdate.put("TeacherPrivacySettings/" + auth.getCurrentUser().getUid(), teacherPrivacyModel);

                mDatabaseReference.updateChildren(teacherPrivacyUpdate, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
