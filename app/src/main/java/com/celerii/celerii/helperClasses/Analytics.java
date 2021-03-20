package com.celerii.celerii.helperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.celerii.celerii.models.LoginAnalyticsModel;
import com.celerii.celerii.models.SignupAnalyticsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
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
        String day = Date.makeTwoDigits(Date.getDay());
        String month = Date.makeTwoDigits(Date.getMonth());
        String year = Date.getYear();
        String year_month_day = year + "_" + month + "_" + day;
        String year_month = year + "_" + month;
        String platform = "Android";

        SignupAnalyticsModel signupAnalyticsModel = new SignupAnalyticsModel(accountType, date, sortableDate, day, month, year, platform);
        signupUpdateMap.put("Analytics/Signups/" + mFirebaseUserID, signupAnalyticsModel);
        signupUpdateMap.put("Analytics/Daily Signups/" + year_month_day + "/" + mFirebaseUserID, signupAnalyticsModel);
        signupUpdateMap.put("Analytics/Monthly Signups/" + year_month + "/" + mFirebaseUserID, signupAnalyticsModel);
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
        String day = Date.makeTwoDigits(Date.getDay());
        String month = Date.makeTwoDigits(Date.getMonth());
        String year = Date.getYear();
        String year_month_day = year + "_" + month + "_" + day;
        String year_month = year + "_" + month;
        String platform = "Android";

        String loginKey = FirebaseDatabase.getInstance().getReference().child("Analytics").child("Login History").push().getKey();
        sharedPreferencesManager.setCurrentLoginSessionKey(loginKey);
        sharedPreferencesManager.setCurrentLoginSessionDayMonthYear(year_month_day);
        sharedPreferencesManager.setCurrentLoginSessionMonthYear(year_month);
        sharedPreferencesManager.setCurrentLoginSessionYear(year);

        LoginAnalyticsModel loginAnalyticsModel = new LoginAnalyticsModel(mFirebaseUserID, accountType, date, sortableDate, day, month, year, platform, loginKey);

        loginUpdateMap.put("Analytics/User Login History/" + mFirebaseUserID + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/User Daily Login History/" + mFirebaseUserID + "/" + year_month_day + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/User Monthly Login History/" + mFirebaseUserID + "/" + year_month + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/User Yearly Login History/" + mFirebaseUserID + "/" + year + "/" + loginKey, loginAnalyticsModel);

        loginUpdateMap.put("Analytics/Login History/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/Daily Login History/" + year_month_day + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/Monthly Login History/" + year_month + "/" + loginKey, loginAnalyticsModel);
        loginUpdateMap.put("Analytics/Yearly Login History/" + year + "/" + loginKey, loginAnalyticsModel);

        loginUpdateMap.put("Analytics/Daily Unique Login/" + year_month_day + "/" + mFirebaseUserID, true);
        loginUpdateMap.put("Analytics/Monthly Unique Login/" + year_month + "/" + mFirebaseUserID, true);
        loginUpdateMap.put("Analytics/Yearly Unique Login/" + year + "/" + mFirebaseUserID, true);

        mDatabaseReference.updateChildren(loginUpdateMap);
    }

    public static String featureAnalytics(String accountType, String mFirebaseUserID, String featureName) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference();
        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();

        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);
        String day = Date.makeTwoDigits(Date.getDay());
        String month = Date.makeTwoDigits(Date.getMonth());
        String year = Date.getYear();
        String year_month_day = year + "_" + month + "_" + day;
        String year_month = year + "_" + month;
        String platform = "Android";

        String key = FirebaseDatabase.getInstance().getReference().child("Analytics").child("Feature Use Analytics").child(featureName).push().getKey();

        LoginAnalyticsModel loginAnalyticsModel = new LoginAnalyticsModel(mFirebaseUserID, accountType, date, sortableDate, day, month, year, platform);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year_month_day + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year_month + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + key, loginAnalyticsModel);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + year_month_day + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + year_month + "/" + key, loginAnalyticsModel);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + key, loginAnalyticsModel);

        mDatabaseReference.updateChildren(featureUseUpdateMap);

        return key;
    }

    public static void featureAnalyticsUpdateSessionDuration(String featureName, String featureUseKey, String mFirebaseUserID, String sessionDurationInSeconds) {
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String year_month_day = year + "_" + month + "_" + day;
        String year_month = year + "_" + month;

        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year_month_day + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year_month + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + year_month_day + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + year_month + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        DatabaseReference featureUseUpdateRef = FirebaseDatabase.getInstance().getReference();
        featureUseUpdateRef.updateChildren(featureUseUpdateMap);
    }
}
