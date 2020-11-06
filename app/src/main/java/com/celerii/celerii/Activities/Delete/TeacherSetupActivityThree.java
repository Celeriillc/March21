package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TeacherSetupActivityThree extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;
    Button findSchoolsAround, skip;

    FirebaseAuth auth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_setup_three);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseUser = auth.getCurrentUser();
        if (mFirebaseUser == null){
            sharedPreferencesManager.deleteActiveAccount();
            sharedPreferencesManager.deleteMyUserID();
            sharedPreferencesManager.deleteMyFirstName();
            sharedPreferencesManager.deleteMyLastName();
            sharedPreferencesManager.deleteMyPicURL();
            sharedPreferencesManager.deleteActiveKid();
            sharedPreferencesManager.deleteActiveClass();
            sharedPreferencesManager.deleteMyClasses();
            sharedPreferencesManager.deleteMyChildren();
            Intent I = new Intent(TeacherSetupActivityThree.this, IntroSlider.class);
            startActivity(I);
            finish();
            return;
        }

        findSchoolsAround = (Button) findViewById(R.id.btn_findschoolsaround);
        skip = (Button) findViewById(R.id.btn_skip);

        findSchoolsAround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applicationLauncherSharedPreferences.setLauncherActivity("SetupSchoolList");
                Intent I = new Intent(TeacherSetupActivityThree.this, SetupSchoolListActivity.class);
                startActivity(I);
                finish();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applicationLauncherSharedPreferences.setLauncherActivity("Home");
                Intent I = new Intent(TeacherSetupActivityThree.this, TeacherMainActivityTwo.class);
                startActivity(I);
                finish();
            }
        });
    }
}
