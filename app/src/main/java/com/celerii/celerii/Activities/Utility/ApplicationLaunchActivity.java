package com.celerii.celerii.Activities.Utility;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.LoginAndSignup.SignUpActivityFive;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicationLaunchActivity extends AppCompatActivity {

    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_launch);

        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);
        sharedPreferencesManager = new SharedPreferencesManager(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
//        applicationLauncherSharedPreferences.deleteLauncherActivity();

        if (mFirebaseUser == null){
            Intent I = new Intent(ApplicationLaunchActivity.this, IntroSlider.class);
            startActivity(I);
            finish();
        } else {
            String launchActivity = applicationLauncherSharedPreferences.getLauncherActivity();
            if (launchActivity.equals("SignupFive")) {
                Intent I = new Intent(ApplicationLaunchActivity.this, SignUpActivityFive.class);
                startActivity(I);
                finish();
            } else if (launchActivity.equals("IntroSlider")) {
                Intent I = new Intent(ApplicationLaunchActivity.this, IntroSlider.class);
                startActivity(I);
                finish();
            } else if (launchActivity.equals("Home")) {
//                sharedPreferencesManager.setActiveAccount("Teacher");
                if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                    Intent I = new Intent(ApplicationLaunchActivity.this, TeacherMainActivityTwo.class);
                    startActivity(I);
                    finish();
                } else {
                    Intent I = new Intent(ApplicationLaunchActivity.this, ParentMainActivityTwo.class);
                    startActivity(I);
                    finish();
                }
            } else {
                sharedPreferencesManager.clear();
                Intent I = new Intent(ApplicationLaunchActivity.this, IntroSlider.class);
                startActivity(I);
                finish();
            }
        }
    }
}
