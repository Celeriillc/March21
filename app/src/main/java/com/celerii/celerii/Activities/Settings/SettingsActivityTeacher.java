package com.celerii.celerii.Activities.Settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.celerii.celerii.Activities.EditProfiles.EditTeacherProfileActivity;
import com.celerii.celerii.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivityTeacher extends AppCompatActivity {

    private Toolbar toolbar;
    FirebaseAuth auth;
    LinearLayout inviteFriends, editProfile, changePassword, manageMyClasses, pushNotifications, chat, appUpdate,
        privacySettings, FAQ, reportAProblem, reportAbuse, contactUs, blog, termsOfService, privacyPolicy, appInfo,
        aboutUs, viewMyAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_teacher);

        auth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Options");

        inviteFriends = (LinearLayout) findViewById(R.id.inviteFriendsLayout);
        editProfile = (LinearLayout) findViewById(R.id.editprofilelayout);
        changePassword = (LinearLayout) findViewById(R.id.changepasswordlayout);
        manageMyClasses = (LinearLayout) findViewById(R.id.managemyclasseslayout);
        pushNotifications = (LinearLayout) findViewById(R.id.pushnotificationlayout);
        chat = (LinearLayout) findViewById(R.id.chatlayout);
        appUpdate = (LinearLayout) findViewById(R.id.appupdateslayout);
        privacySettings = (LinearLayout) findViewById(R.id.privacysettingslayout);
        FAQ = (LinearLayout) findViewById(R.id.faqlayout);
        reportAProblem = (LinearLayout) findViewById(R.id.reportaproblemlayout);
        reportAbuse = (LinearLayout) findViewById(R.id.reportabuselayout);
        contactUs = (LinearLayout) findViewById(R.id.contactuslayout);
        blog = (LinearLayout) findViewById(R.id.bloglayout);
        termsOfService = (LinearLayout) findViewById(R.id.termsofservicelayout);
        privacyPolicy = (LinearLayout) findViewById(R.id.privacypolicylayout);
        appInfo = (LinearLayout) findViewById(R.id.appinfolayout);
        aboutUs = (LinearLayout) findViewById(R.id.aboutuslayout);

        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, EditTeacherProfileActivity.class);
                Bundle b = new Bundle();
                //b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, ChangePasswordActivity.class);
                startActivity(I);
            }
        });

        manageMyClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, ManageMyClassesActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        pushNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, PushNotificationSettingsActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        appUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, AppUpdateActivity.class);
                startActivity(I);
            }
        });

        privacySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, PrivacySettingsTeacher.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        FAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, FAQRowActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        reportAProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, ReportAProblemListActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        reportAbuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, ReportAbuseListActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, ContactUsActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        termsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, BrowserActivityForInfo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Header", "Terms of Service");
                bundle.putString("URL", "");
                I.putExtras(bundle);
                startActivity(I);
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, BrowserActivityForInfo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Header", "Privacy Policy");
                bundle.putString("URL", "");
                I.putExtras(bundle);
                startActivity(I);
            }
        });

        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, BrowserActivityForInfo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Header", "App Info");
                bundle.putString("URL", "");
                I.putExtras(bundle);
                startActivity(I);
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityTeacher.this, BrowserActivityForInfo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Header", "About Us");
                bundle.putString("URL", "");
                I.putExtras(bundle);
                startActivity(I);
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
