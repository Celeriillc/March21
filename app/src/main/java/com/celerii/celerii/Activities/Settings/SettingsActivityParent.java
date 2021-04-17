package com.celerii.celerii.Activities.Settings;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.celerii.celerii.Activities.EditPersonalInformationDetails.EmailEditActivity;
import com.celerii.celerii.Activities.EditProfiles.EditParentProfileActivity;
import com.celerii.celerii.Activities.Settings.DeleteAccount.DeleteAccountReasonActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SettingsActivityParent extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar toolbar;
    LinearLayout editProfile, editEmail, changePassword, deleteAccount, manageMyClasses, pushNotifications, appUpdate,
            FAQ, tutorials, reportAProblem, reportAbuse, contactUs, blog, termsOfService, privacyPolicy, appInfo,
            aboutUs, viewMyAccounts;

    String featureUseKey = "";
    String featureName = "Settings Parent";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_parent);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Settings");

        editProfile = (LinearLayout) findViewById(R.id.editprofilelayout);
        editEmail = (LinearLayout) findViewById(R.id.editemaillayout);
        changePassword = (LinearLayout) findViewById(R.id.changepasswordlayout);
        deleteAccount = (LinearLayout) findViewById(R.id.deleteaccountlayout);
        manageMyClasses = (LinearLayout) findViewById(R.id.managemyclasseslayout);
        pushNotifications = (LinearLayout) findViewById(R.id.pushnotificationlayout);
        appUpdate = (LinearLayout) findViewById(R.id.appupdateslayout);
//        privacySettings = (LinearLayout) findViewById(R.id.privacysettingslayout);
        FAQ = (LinearLayout) findViewById(R.id.faqlayout);
        tutorials = (LinearLayout) findViewById(R.id.tutorialslayout);
        reportAProblem = (LinearLayout) findViewById(R.id.reportaproblemlayout);
        reportAbuse = (LinearLayout) findViewById(R.id.reportabuselayout);
        contactUs = (LinearLayout) findViewById(R.id.contactuslayout);
        blog = (LinearLayout) findViewById(R.id.bloglayout);
        termsOfService = (LinearLayout) findViewById(R.id.termsofservicelayout);
        privacyPolicy = (LinearLayout) findViewById(R.id.privacypolicylayout);
        appInfo = (LinearLayout) findViewById(R.id.appinfolayout);
        aboutUs = (LinearLayout) findViewById(R.id.aboutuslayout);

        appInfo.setVisibility(View.GONE);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, EditParentProfileActivity.class);
                Bundle b = new Bundle();
                //b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, EmailEditActivity.class);
                Bundle b = new Bundle();
                //b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, ChangePasswordActivity.class);
                startActivity(I);
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, DeleteAccountReasonActivity.class);
                startActivity(I);
            }
        });

        manageMyClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, ManageKidsRowActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        pushNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, PushNotificationSettingsActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        appUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, AppUpdateActivity.class);
                startActivity(I);
            }
        });

        FAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, FAQRowActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        tutorials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, TutorialsActivity.class);
                startActivity(I);
            }
        });

        reportAProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, ReportAProblemListActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        reportAbuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, ReportAbuseListActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(SettingsActivityParent.this, ContactUsActivity.class);
                Bundle b = new Bundle();
                b.putString("id", auth.getCurrentUser().getUid());
                startActivity(I);
            }
        });

        blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://celerii.com/blogs";
                try {
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                catch(ActivityNotFoundException e) {
                    // Chrome is not installed
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }

//                Intent I = new Intent(SettingsActivityParent.this, BrowserActivityForInfo.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("Header", "Blog");
//                bundle.putString("URL", url);
//                I.putExtras(bundle);
//                startActivity(I);
            }
        });

        termsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://celerii.com/tos#terms-of-service";
                try {
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                catch(ActivityNotFoundException e) {
                    // Chrome is not installed
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }

//                Intent I = new Intent(SettingsActivityParent.this, BrowserActivityForInfo.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("Header", "Terms of Service");
//                bundle.putString("URL", url);
//                I.putExtras(bundle);
//                startActivity(I);
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://celerii.com/tos#privacy-policy";
                try {
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                catch(ActivityNotFoundException e) {
                    // Chrome is not installed
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }

//                Intent I = new Intent(SettingsActivityParent.this, BrowserActivityForInfo.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("Header", "Privacy Policy");
//                bundle.putString("URL", url);
//                I.putExtras(bundle);
//                startActivity(I);
            }
        });

        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://celerii.com/app-info";
                try {
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                catch(ActivityNotFoundException e) {
                    // Chrome is not installed
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }

//                Intent I = new Intent(SettingsActivityParent.this, BrowserActivityForInfo.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("Header", "App Info");
//                bundle.putString("URL", url);
//                I.putExtras(bundle);
//                startActivity(I);
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://celerii.com/about";
                try {
                    Intent i = new Intent("android.intent.action.MAIN");
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                catch(ActivityNotFoundException e) {
                    // Chrome is not installed
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }

//                Intent I = new Intent(SettingsActivityParent.this, BrowserActivityForInfo.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("Header", "About Us");
//                bundle.putString("URL", url);
//                I.putExtras(bundle);
//                startActivity(I);
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
