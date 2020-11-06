package com.celerii.celerii.Activities.LoginAndSignup;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;

public class WelcomeToBetaActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;

    private Toolbar mToolbar;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_to_beta);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(this);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        continueButton = (Button) findViewById(R.id.continuebutton);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applicationLauncherSharedPreferences.setLauncherActivity("Home");
                if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                    Intent I = new Intent(WelcomeToBetaActivity.this, TeacherMainActivityTwo.class);
                    startActivity(I);
                    finishAffinity();
                } else {
                    Intent I = new Intent(WelcomeToBetaActivity.this, ParentMainActivityTwo.class);
                    startActivity(I);
                    finishAffinity();
                }
            }
        });
    }
}
