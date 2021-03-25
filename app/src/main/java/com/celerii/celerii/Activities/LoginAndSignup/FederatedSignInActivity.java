package com.celerii.celerii.Activities.LoginAndSignup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebaseForLogin;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Teacher;
import com.celerii.celerii.models.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthCredential;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FederatedSignInActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;
    Context context = this;

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    CallbackManager mCallbackManager;

    private Toolbar mToolbar;
    TextView termsAndPrivacy;
    ImageButton google, facebook, twitter;
    Button signUpWithEmail;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN_GOOGLE = 9001;
    private static final String TAG = "FederatedAuthActivity";
    TwitterAuthClient mTwitterAuthClient;

    CustomProgressDialogOne progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess");
                FirebaseUserAuthFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
                showDialogWithMessage("Your Facebook sign in attempt was cancelled by you.");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError", error);
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                    showDialogWithMessage("We've just logged you out of your previous session, please try again to continue");
                } else {
                    showDialogWithMessage("Your Facebook sign in attempt could not be completed, please try again, possibly with an email and password.");
                }
            }
        });

        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this).twitterAuthConfig(twitterAuthConfig).build();
        Twitter.initialize(twitterConfig);
        mTwitterAuthClient = new TwitterAuthClient();

        setContentView(R.layout.activity_federated_sign_in);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = mAuth.getCurrentUser();
        progressDialog = new CustomProgressDialogOne(FederatedSignInActivity.this);

        termsAndPrivacy = (TextView) findViewById(R.id.termsandprivacy);
        termsAndPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        google = (ImageButton) findViewById(R.id.google);
        facebook = (ImageButton) findViewById(R.id.facebook);
        twitter = (ImageButton) findViewById(R.id.twitter);
        signUpWithEmail = (Button) findViewById(R.id.signupwithemail);

        //region Google
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(FederatedSignInActivity.this)
                .enableAutoManage(FederatedSignInActivity.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString);
                    return;
                }

                googleUserSignInMethod();
            }
        });
        //endregion

        //region Facebook
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString);
                    return;
                }

                LoginManager.getInstance().logInWithReadPermissions(FederatedSignInActivity.this, Arrays.asList("public_profile", "email"));
            }
        });
        //endregion

        //region Twitter
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString);
                    return;
                }

                mTwitterAuthClient.authorize(FederatedSignInActivity.this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        FirebaseUserAuthTwitter(twitterSessionResult.data);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                        showDialogWithMessage("Your Twitter sign in attempt could not be completed, please try again, possibly with an email and password.");
                    }
                });
            }
        });
        //endregion

        signUpWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(FederatedSignInActivity.this, SignUpActivityOne.class);
                startActivity(I);
            }
        });
    }

    public void googleUserSignInMethod(){
        Intent AuthIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(AuthIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleSignInResult.isSuccess()){
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                FirebaseUserAuthGoogle(googleSignInAccount);
            } else {
                Log.d("TAG", googleSignInResult.getStatus().toString());
                showDialogWithMessage("Your Google sign in attempt could not be completed, please try again, possibly with an email and password.");
            }
        }

        try {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {}

        try {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {}
    }

    public void FirebaseUserAuthGoogle(GoogleSignInAccount googleSignInAccount) {
        progressDialog.showWithMessage("Please hold a little while we get things ready, this might take up to a minute depending on your connection strength.");
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(FederatedSignInActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> AuthResultTask) {
                if (AuthResultTask.isSuccessful()){
                    mFirebaseUser = mAuth.getCurrentUser();
                    if (mFirebaseUser != null) { authenticateUser(mFirebaseUser); }
                } else {
                    progressDialog.dismiss();
                    Log.d(TAG, "Google error", AuthResultTask.getException());
                    showDialogWithMessage("Your Google sign in attempt could not be completed, please try again, possibly with an email and password.");
                }
            }
        });
    }

    public void FirebaseUserAuthFacebook(AccessToken facebookToken) {
        progressDialog.showWithMessage("Please hold a little while we get things ready, this might take up to a minute depending on your connection strength.");
        AuthCredential authCredential = FacebookAuthProvider.getCredential(facebookToken.getToken());
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> AuthResultTask) {
                if (AuthResultTask.isSuccessful()){
                    mFirebaseUser = mAuth.getCurrentUser();
                    if (mFirebaseUser != null) { authenticateUser(mFirebaseUser); }
                } else {
                    progressDialog.dismiss();
                    Log.d(TAG, "Facebook error", AuthResultTask.getException());
                    showDialogWithMessage("Your Facebook sign in attempt could not be completed, please try again, possibly with an email and password.");
                }
            }
        });
    }

    public void FirebaseUserAuthTwitter(TwitterSession twitterSession) {
        progressDialog.showWithMessage("Please hold a little while we get things ready, this might take up to a minute depending on your connection strength.");
        AuthCredential credential = TwitterAuthProvider.getCredential(twitterSession.getAuthToken().token, twitterSession.getAuthToken().secret);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> AuthResultTask) {
                if (AuthResultTask.isSuccessful()){
                    mFirebaseUser = mAuth.getCurrentUser();
                    if (mFirebaseUser != null) { authenticateUser(mFirebaseUser); }
                } else {
                    progressDialog.dismiss();
                    Log.d(TAG, "Twitter error", AuthResultTask.getException());
                    showDialogWithMessage("Your Twitter sign in attempt could not be completed, please try again, possibly with an email and password.");
                }
            }
        });
    }

    public void authenticateUser(final FirebaseUser mFirebaseUser) {
        final String userID = mFirebaseUser.getUid();
        mDatabaseReference = mFirebaseDatabase.getReference().child("UserRoles").child(userID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    sharedPreferencesManager.clear();
                    User user = dataSnapshot.getValue(User.class);
                    String activeAccount = user.getRole();
                    if (activeAccount.equals("Parent") || activeAccount.equals("Teacher")) {
                        Analytics.loginAnalytics(context, mFirebaseUser.getUid(), activeAccount);
                        UpdateDataFromFirebaseForLogin.populateEssentials(context, activeAccount, progressDialog);
                    } else {
                        String message = "Celerii mobile only works with " + "<b>" + "Teacher" + "</b>" + " and " + "<b>" + "Parent" + "</b>" + " accounts. If your account is a " + "<b>" + "School" + "</b>" + " account, please use Celerii web at \n " + "<b>" + "www.celerii.io" + "</b";
                        showDialogWithMessageAndLogout(Html.fromHtml(message));
                    }
                } else {
                    sharedPreferencesManager.clear();
                    Map<String, Object> updateMap = new HashMap<String, Object>();
                    final String userName = mFirebaseUser.getDisplayName().trim().replaceAll("\\s+", " ");
                    String userEmail = mFirebaseUser.getEmail().trim();
                    String refactoredEmail = userEmail.replace(".", "_fullStop_");
                    Parent parent = new Parent(userName, "", userName.toLowerCase(), "", userEmail);
                    Teacher teacher = new Teacher(userName, "", userName.toLowerCase(), "", userEmail);
                    final User user = new User(userEmail, "Parent");
                    updateMap.put("Parent/" + userID, parent);
                    updateMap.put("Teacher/" + userID, teacher);
                    updateMap.put("UserRoles/" + userID, user);
                    updateMap.put("Email/" + refactoredEmail, "true");
                    Analytics.signupAnalytics( userID, "Parent" );

                    mDatabaseReference = mFirebaseDatabase.getReference();
                    mDatabaseReference.updateChildren(updateMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                saveToSharedPreferencesAndProceed("Parent", userID, userName, "");
                            } else {
                                progressDialog.dismiss();
                                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                ShowDialogWithMessage.showDialogWithMessageAndDelete(context, message, mFirebaseUser);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void showDialogWithMessage (String messageString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    void showDialogWithMessageAndLogout (Spanned messageString) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        sharedPreferencesManager.clear();
        mAuth.signOut();
        applicationLauncherSharedPreferences.setLauncherActivity("IntroSlider");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finishAffinity();
                Intent intent = new Intent(((Activity)context), IntroSlider.class);
                ((Activity)context).startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    void saveToSharedPreferencesAndProceed(String accountType, String activeUserID, String firstName, String lastName) {
        sharedPreferencesManager.setActiveAccount(accountType);
        sharedPreferencesManager.setMyUserID(activeUserID);
        sharedPreferencesManager.setMyFirstName(firstName);
        sharedPreferencesManager.setMyLastName(lastName);
        applicationLauncherSharedPreferences.setLauncherActivity("FederatedSignInAccountType");
        progressDialog.dismiss();
        finishAffinity();
        Intent I = new Intent(FederatedSignInActivity.this, FederatedSignInAccountTypeActivity.class);
        startActivity(I);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
