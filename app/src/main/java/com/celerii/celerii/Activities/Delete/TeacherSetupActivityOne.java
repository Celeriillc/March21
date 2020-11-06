package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TeacherSetupActivityOne extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;

    FirebaseAuth auth;
    FirebaseUser mFirebaseUser;

    public Button btn_continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_setup_one);

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
            Intent I = new Intent(TeacherSetupActivityOne.this, IntroSlider.class);
            startActivity(I);
            finish();
            return;
        }

        btn_continue = (Button) findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applicationLauncherSharedPreferences.setLauncherActivity("TeacherSetupTwo");
                Intent I = new Intent(TeacherSetupActivityOne.this, TeacherSetupActivityTwo.class);
                startActivity(I);
                finish();
            }
        });
    }
}
