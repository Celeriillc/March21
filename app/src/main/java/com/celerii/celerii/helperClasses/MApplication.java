package com.celerii.celerii.helperClasses;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Created by DELL on 11/28/2017.
 */

public class MApplication extends Application implements LifecycleObserver {
    Context context;
    FirebaseAuth auth;
    FirebaseUser mFirebaseUser;
    SharedPreferencesManager sharedPreferencesManager;
    String accountType;
    long sessionStartTime;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        auth = FirebaseAuth.getInstance();
        mFirebaseUser = auth.getCurrentUser();
        sharedPreferencesManager = new SharedPreferencesManager(context);
        accountType = sharedPreferencesManager.getActiveAccount();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        MyFirebaseInstanceIdService myFirebaseInstanceIdService = new MyFirebaseInstanceIdService();
        myFirebaseInstanceIdService.onTokenRefresh();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        if (mFirebaseUser != null) {
            String sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
            String currentSessionLoginKey = sharedPreferencesManager.getCurrentLoginSessionKey();
            String day_month_year = sharedPreferencesManager.getCurrentLoginSessionDayMonthYear();
            String month_year = sharedPreferencesManager.getCurrentLoginSessionMonthYear();
            String year = sharedPreferencesManager.getCurrentLoginSessionYear();
            HashMap<String, Object> loginUpdateMap = new HashMap<>();
            String mFirebaseUserID = mFirebaseUser.getUid();

            loginUpdateMap.put("Analytics/User Login History/" + mFirebaseUserID + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
            loginUpdateMap.put("Analytics/User Daily Login History/" + mFirebaseUserID + "/" + day_month_year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
            loginUpdateMap.put("Analytics/User Monthly Login History/" + mFirebaseUserID + "/" + month_year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
            loginUpdateMap.put("Analytics/User Yearly Login History/" + mFirebaseUserID + "/" + year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

            loginUpdateMap.put("Analytics/Login History/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
            loginUpdateMap.put("Analytics/Daily Login History/" + day_month_year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
            loginUpdateMap.put("Analytics/Monthly Login History/" + month_year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
            loginUpdateMap.put("Analytics/Yearly Login History/" + year + "/" + currentSessionLoginKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

            DatabaseReference loginUpdateRef = FirebaseDatabase.getInstance().getReference();
            loginUpdateRef.updateChildren(loginUpdateMap);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground

        if (mFirebaseUser != null) {
            Analytics.loginAnalytics(context, mFirebaseUser.getUid(), accountType);
            sessionStartTime = System.currentTimeMillis();
        }
    }
}
