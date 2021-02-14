package com.celerii.celerii.helperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.R;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

public class LogoutProtocol {
    public static void logout(Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        if (mFirebaseUser != null) {
            reference.child("UserRoles").child(mFirebaseUser.getUid()).child("Tokens").setValue(null);
            reference.child("UserRoles").child(mFirebaseUser.getUid()).child("token").setValue(null);
            FacebookSdk.sdkInitialize(context);
            TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(String.valueOf(R.string.twitter_consumer_key), String.valueOf(R.string.twitter_consumer_secret));
            TwitterConfig twitterConfig = new TwitterConfig.Builder(context).twitterAuthConfig(twitterAuthConfig).build();
            Twitter.initialize(twitterConfig);
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
            ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(context);
            sharedPreferencesManager.clear();
            applicationLauncherSharedPreferences.setLauncherActivity("IntroSlider");
            Intent I = new Intent(context, IntroSlider.class);
            context.startActivity(I);
            SessionManager<TwitterSession> twitterSessionManager = TwitterCore.getInstance().getSessionManager();
            if (twitterSessionManager.getActiveSession() != null) {
                twitterSessionManager.clearActiveSession();
            }
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
            auth.signOut();
            ((Activity)context).finishAffinity();
        } else {
            goToIntro(context);
        }
    }

    public static void logout(Context context, String signOutMessage) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if (mFirebaseUser != null) {
            reference.child("UserRoles").child(mFirebaseUser.getUid()).child("Tokens").setValue(null);
            reference.child("UserRoles").child(mFirebaseUser.getUid()).child("token").setValue(null);
            FacebookSdk.sdkInitialize(context);
            TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(String.valueOf(R.string.twitter_consumer_key), String.valueOf(R.string.twitter_consumer_secret));
            TwitterConfig twitterConfig = new TwitterConfig.Builder(context).twitterAuthConfig(twitterAuthConfig).build();
            Twitter.initialize(twitterConfig);
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
            ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(context);
            sharedPreferencesManager.clear();
            applicationLauncherSharedPreferences.setLauncherActivity("IntroSlider");
            Intent I = new Intent(context, IntroSlider.class);
            context.startActivity(I);
            SessionManager<TwitterSession> twitterSessionManager = TwitterCore.getInstance().getSessionManager();
            if (twitterSessionManager.getActiveSession() != null) {
                twitterSessionManager.clearActiveSession();
            }
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
            auth.signOut();
            ((Activity)context).finishAffinity();
            CustomToast.blueBackgroundToast(context, signOutMessage);
        } else {
            goToIntro(context);
        }
    }

    public static void goToIntro(Context context) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if (mFirebaseUser != null) {
            reference.child("UserRoles").child(mFirebaseUser.getUid()).child("Tokens").setValue(null);
            reference.child("UserRoles").child(mFirebaseUser.getUid()).child("token").setValue(null);
        }
        FacebookSdk.sdkInitialize(context);
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(String.valueOf(R.string.twitter_consumer_key), String.valueOf(R.string.twitter_consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(context).twitterAuthConfig(twitterAuthConfig).build();
        Twitter.initialize(twitterConfig);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
        ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(context);
        sharedPreferencesManager.clear();
        applicationLauncherSharedPreferences.setLauncherActivity("IntroSlider");
        Intent I = new Intent(context, IntroSlider.class);
        context.startActivity(I);
        SessionManager<TwitterSession> twitterSessionManager = TwitterCore.getInstance().getSessionManager();
        if (twitterSessionManager.getActiveSession() != null) {
            twitterSessionManager.clearActiveSession();
        }
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        ((Activity)context).finishAffinity();
    }
}
