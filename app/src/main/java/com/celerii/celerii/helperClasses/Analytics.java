package com.celerii.celerii.helperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.celerii.celerii.models.LoginAnalyticsModel;
import com.celerii.celerii.models.SignupAnalyticsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Analytics {

    public static void signupAnalytics(String mFirebaseUserID, String accountType) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
        HashMap<String, Object> signupUpdateMap = new HashMap<>();

        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;
        String platform = "Android";

        SignupAnalyticsModel signupAnalyticsModel = new SignupAnalyticsModel(accountType, date, sortableDate, day, month, year, platform);
        signupUpdateMap.put("Analytics/Signups/" + mFirebaseUserID, signupAnalyticsModel);
        signupUpdateMap.put("Analytics/Daily Signups/" + day_month_year + "/" + mFirebaseUserID, signupAnalyticsModel);
        signupUpdateMap.put("Analytics/Monthly Signups/" + month_year + "/" + mFirebaseUserID, signupAnalyticsModel);
        signupUpdateMap.put("Analytics/Yearly Signups/" + year + "/" + mFirebaseUserID, signupAnalyticsModel);

        mDatabaseReference.updateChildren(signupUpdateMap);
    }

    public static void loginAnalytics(Context context, String mFirebaseUserID, String accountType) {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
        HashMap<String, Object> loginUpdateMap = new HashMap<>();

        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;
        String platform = "Android";

        String loginKey = FirebaseDatabase.getInstance().getReference().child("Analytics").child("Login History").push().getKey();
        sharedPreferencesManager.setCurrentLoginSessionKey(loginKey);
        sharedPreferencesManager.setCurrentLoginSessionDayMonthYear(day_month_year);
        sharedPreferencesManager.setCurrentLoginSessionMonthYear(month_year);
        sharedPreferencesManager.setCurrentLoginSessionYear(year);

        LoginAnalyticsModel loginAnalyticsModel = new LoginAnalyticsModel(mFirebaseUserID, accountType, date, sortableDate, day, month, year, platform, loginKey);

        loginUpdateMap.put("Analytics/User Login History/" + mFirebaseUserID + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/User Daily Login History/" + mFirebaseUserID + "/" + day_month_year + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/User Monthly Login History/" + mFirebaseUserID + "/" + month_year + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/User Yearly Login History/" + mFirebaseUserID + "/" + year + "/" + loginKey, loginAnalyticsModel);

        loginUpdateMap.put("Analytics/Login History/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/Daily Login History/" + day_month_year + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/Monthly Login History/" + month_year + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/Yearly Login History/" + year + "/" + loginKey, loginAnalyticsModel);

        loginUpdateMap.put("Analytics/Daily Unique Login/" + day_month_year + "/" + mFirebaseUserID, true);
        loginUpdateMap.put("Analytics/Monthly Unique Login/" + month_year + "/" + mFirebaseUserID, true);
        loginUpdateMap.put("Analytics/Yearly Unique Login/" + year + "/" + mFirebaseUserID, true);

        mDatabaseReference.updateChildren(loginUpdateMap);
    }

    public static String featureAnalytics(String accountType, String mFirebaseUserID, String featureName) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();

        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;
        String platform = "Android";

        String key = FirebaseDatabase.getInstance().getReference().child("Analytics").child("Feature Use Analytics").child(featureName).push().getKey();

        LoginAnalyticsModel loginAnalyticsModel = new LoginAnalyticsModel(mFirebaseUserID, accountType, date, sortableDate, day, month, year, platform);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + day_month_year + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + month_year + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + key, loginAnalyticsModel);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + day_month_year + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + month_year + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + key, loginAnalyticsModel);

        mDatabaseReference.updateChildren(featureUseUpdateMap);

        return key;
    }
}
