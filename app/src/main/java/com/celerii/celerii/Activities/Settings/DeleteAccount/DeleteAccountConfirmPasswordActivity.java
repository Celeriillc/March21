package com.celerii.celerii.Activities.Settings.DeleteAccount;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.Html;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.LogoutProtocol;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.HashMap;

public class DeleteAccountConfirmPasswordActivity extends AppCompatActivity {

    Context context = this;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    TextView title, message;
    Button deleteAccount;
    EditText password;
    ImageButton togglePasswordVisisbility;
    RelativeLayout passwordLayout;
//    String reasonForDelete;
    String email;
    Boolean isPasswordVisible = false;
    Bundle bundle;

    CustomProgressDialogOne progressDialog;
    HashMap<String, Object> deleteAccountUpdatemap = new HashMap<>();
    HashMap<String, Object> deleteAccountReverseUpdatemap = new HashMap<>();

    String featureUseKey = "";
    String featureName = "Delete Account Confirm Password";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account_confirm_password);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        progressDialog = new CustomProgressDialogOne(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
//        reasonForDelete = bundle.getString("Reason For Delete");
        email = mFirebaseUser.getEmail();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Confirm Account Deletion");

        title = (TextView) findViewById(R.id.title);
        message = (TextView) findViewById(R.id.message);
        deleteAccount = (Button) findViewById(R.id.deleteaccount);
        password = (EditText) findViewById(R.id.password);
        togglePasswordVisisbility = (ImageButton) findViewById(R.id.togglepasswordvisibility);
        passwordLayout = (RelativeLayout) findViewById(R.id.passwordlayout);
        password.requestFocus();
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        for (UserInfo user: mFirebaseUser.getProviderData()) {
            String provider = user.getProviderId();
            if (provider.equals("google.com") || provider.equals("facebook.com") || provider.equals("twitter.com"))  {
                title.setText("Confirm Delete");
                message.setText(Html.fromHtml("We are taking this extra step to ensure you do not delete your account in error. If you wish to proceed with the delete operation, click the " + "<b>" + "Delete Account" + "</b>" + " button below"));
                passwordLayout.setVisibility(View.GONE);
            }
        }

        togglePasswordVisisbility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordVisible) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password.setSelection(password.length());
                    togglePasswordVisisbility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_off));
                    isPasswordVisible = true;
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setSelection(password.length());
                    togglePasswordVisisbility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye));
                    isPasswordVisible = false;
                }
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(Html.fromHtml(messageString));
                    return;
                }

                final String date = Date.getDate();

                progressDialog.show();

                mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String studentID = postSnapshot.getKey();

                                deleteAccountUpdatemap.put("Parents Students/" + mFirebaseUser.getUid() + "/" + studentID, null);
                                deleteAccountUpdatemap.put("Student Parent/" + studentID + "/" + mFirebaseUser.getUid(), null);

                                deleteAccountReverseUpdatemap.put("Parents Students/" + mFirebaseUser.getUid() + "/" + studentID, true);
                                deleteAccountReverseUpdatemap.put("Student Parent/" + studentID + "/" + mFirebaseUser.getUid(), true);
                            }
                        }

                        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Class").child(mFirebaseUser.getUid());
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        String classID = postSnapshot.getKey();

                                        deleteAccountUpdatemap.put("Teacher Class/" + mFirebaseUser.getUid() + "/" + classID, null);
                                        deleteAccountUpdatemap.put("Class Teacher/" + classID + "/" + mFirebaseUser.getUid(), null);

                                        deleteAccountReverseUpdatemap.put("Teacher Class/" + mFirebaseUser.getUid() + "/" + classID, true);
                                        deleteAccountReverseUpdatemap.put("Class Teacher/" + classID + "/" + mFirebaseUser.getUid(), true);
                                    }
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher School").child(mFirebaseUser.getUid());
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                String schoolID = postSnapshot.getKey();

                                                deleteAccountUpdatemap.put("Teacher School/" + mFirebaseUser.getUid() + "/" + schoolID, null);
                                                deleteAccountUpdatemap.put("School Teacher/" + schoolID + "/" + mFirebaseUser.getUid(), null);

                                                deleteAccountReverseUpdatemap.put("Teacher School/" + mFirebaseUser.getUid() + "/" + schoolID, true);
                                                deleteAccountReverseUpdatemap.put("School Teacher/" + schoolID + "/" + mFirebaseUser.getUid(), true);
                                            }
                                        }

                                        String refactoredEmail = email.replace(".", "_fullStop_");
                                        deleteAccountUpdatemap.put("Parent/" + mFirebaseUser.getUid() + "/" + "isDeleted", true);
                                        deleteAccountUpdatemap.put("Teacher/" + mFirebaseUser.getUid() + "/" + "isDeleted", true);
                                        deleteAccountUpdatemap.put("Email/" + refactoredEmail, null);
                                        deleteAccountUpdatemap.put("DeletedAccounts/Parent/" + mFirebaseUser.getUid() + "/" + "isDeleted", true);
                                        deleteAccountUpdatemap.put("DeletedAccounts/Teacher/" + mFirebaseUser.getUid() + "/" + "isDeleted", true);
                                        deleteAccountUpdatemap.put("DeletedAccounts/Parent/" + mFirebaseUser.getUid() + "/" + "date", date);
                                        deleteAccountUpdatemap.put("DeletedAccounts/Teacher/" + mFirebaseUser.getUid() + "/" + "date", date);
//                                        deleteAccountUpdatemap.put("DeletedAccounts/Parent/" + mFirebaseUser.getUid() + "/" + "reason", "");
//                                        deleteAccountUpdatemap.put("DeletedAccounts/Teacher/" + mFirebaseUser.getUid() + "/" + "reason", "");

                                        deleteAccountReverseUpdatemap.put("Parent/" + mFirebaseUser.getUid() + "/" + "isDeleted", null);
                                        deleteAccountReverseUpdatemap.put("Teacher/" + mFirebaseUser.getUid() + "/" + "isDeleted", null);
                                        deleteAccountReverseUpdatemap.put("Email/" + refactoredEmail, true);
                                        deleteAccountReverseUpdatemap.put("DeletedAccounts/Parent/" + mFirebaseUser.getUid(), null);
                                        deleteAccountReverseUpdatemap.put("DeletedAccounts/Teacher/" + mFirebaseUser.getUid(), null);

                                        for (UserInfo user: mFirebaseUser.getProviderData()) {
                                            if (user.getProviderId().equals("password")) {
                                                final String currentPassword = password.getText().toString();
                                                if (!validatePassword(currentPassword))
                                                    return;

                                                AuthCredential credential = EmailAuthProvider.getCredential(mFirebaseUser.getEmail(), currentPassword);
                                                mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){

                                                            mDatabaseReference = mFirebaseDatabase.getReference();
                                                            mDatabaseReference.updateChildren(deleteAccountUpdatemap, new DatabaseReference.CompletionListener() {
                                                                @Override
                                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                    if (databaseError == null) {
                                                                        mFirebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    progressDialog.dismiss();
                                                                                    LogoutProtocol.logout(context, "Your Celerii account has been successfully deleted");
                                                                                } else {
                                                                                    mDatabaseReference = mFirebaseDatabase.getReference();
                                                                                    mDatabaseReference.updateChildren(deleteAccountReverseUpdatemap);
                                                                                    progressDialog.dismiss();
                                                                                    String messageString = "An error occurred while deleting your account and could not be completed. Please try again";
                                                                                    ShowDialogWithMessage.showDialogWithMessage(context, messageString);
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        progressDialog.dismiss();
                                                                        String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                                                        showDialogWithMessage(Html.fromHtml(message));
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            progressDialog.dismiss();
                                                            String messageString = "Your password is incorrect. Confirm your password information and try again. If you have lost your password, " +
                                                                    "logout and use the " + "<b>" +  "Forgot Password" + "</b>" + " area and we'll help you recover it.";
                                                            showDialogWithMessage(Html.fromHtml(messageString));
                                                        }
                                                    }
                                                });
                                            }
                                            else if (user.getProviderId().equals("google.com")) {
                                                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
                                                if (acct != null) {
                                                    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                                                    mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                mDatabaseReference = mFirebaseDatabase.getReference();
                                                                mDatabaseReference.updateChildren(deleteAccountUpdatemap, new DatabaseReference.CompletionListener() {
                                                                    @Override
                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                        if (databaseError == null) {
                                                                            mFirebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        progressDialog.dismiss();
                                                                                        LogoutProtocol.logout(context, "Your Celerii account has been successfully deleted");
                                                                                    } else {
                                                                                        mDatabaseReference = mFirebaseDatabase.getReference();
                                                                                        mDatabaseReference.updateChildren(deleteAccountReverseUpdatemap);
                                                                                        progressDialog.dismiss();
                                                                                        String messageString = "An error occurred while deleting your account and could not be completed. Please try again";
                                                                                        showDialogWithMessage(Html.fromHtml(messageString));
                                                                                    }
                                                                                }
                                                                            });
                                                                        } else {
                                                                            progressDialog.dismiss();
                                                                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                                                            showDialogWithMessage(Html.fromHtml(message));
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            else {
                                                                progressDialog.dismiss();
                                                                String messageString = "An error occurred while deleting your account and could not be completed. Please try again";
                                                                showDialogWithMessage(Html.fromHtml(messageString));
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    progressDialog.dismiss();
                                                    String messageString = "An error occurred while deleting your account and could not be completed. Please try again";
                                                    showDialogWithMessage(Html.fromHtml(messageString));}

                                            }
                                            else if (user.getProviderId().equals("facebook.com")) {
                                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                                if (accessToken != null) {
                                                    AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
                                                    mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                mDatabaseReference = mFirebaseDatabase.getReference();
                                                                mDatabaseReference.updateChildren(deleteAccountUpdatemap, new DatabaseReference.CompletionListener() {
                                                                    @Override
                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                        if (databaseError == null) {
                                                                            mFirebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        progressDialog.dismiss();
                                                                                        LogoutProtocol.logout(context, "Your Celerii account has been successfully deleted");
                                                                                    } else {
                                                                                        mDatabaseReference = mFirebaseDatabase.getReference();
                                                                                        mDatabaseReference.updateChildren(deleteAccountReverseUpdatemap);
                                                                                        progressDialog.dismiss();
                                                                                        String messageString = "An error occurred while deleting your account and could not be completed. Please try again";
                                                                                        showDialogWithMessage(Html.fromHtml(messageString));
                                                                                    }
                                                                                }
                                                                            });
                                                                        } else {
                                                                            progressDialog.dismiss();
                                                                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                                                            showDialogWithMessage(Html.fromHtml(message));
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            else {
                                                                progressDialog.dismiss();
                                                                String messageString = "An error occurred while deleting your account and could not be completed. Please try again";
                                                                showDialogWithMessage(Html.fromHtml(messageString));
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                            else if (user.getProviderId().equals("twitter.com")) {
                                                SessionManager<TwitterSession> twitterSessionManager = TwitterCore.getInstance().getSessionManager();
                                                TwitterAuthToken twitterAuthToken = twitterSessionManager.getActiveSession().getAuthToken();
                                                if (twitterAuthToken != null) {
                                                    AuthCredential credential = TwitterAuthProvider.getCredential(twitterAuthToken.token, null);
                                                    mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                mDatabaseReference = mFirebaseDatabase.getReference();
                                                                mDatabaseReference.updateChildren(deleteAccountUpdatemap, new DatabaseReference.CompletionListener() {
                                                                    @Override
                                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                                        if (databaseError == null) {
                                                                            mFirebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        progressDialog.dismiss();
                                                                                        LogoutProtocol.logout(context, "Your Celerii account has been successfully deleted");
                                                                                    } else {
                                                                                        mDatabaseReference = mFirebaseDatabase.getReference();
                                                                                        mDatabaseReference.updateChildren(deleteAccountReverseUpdatemap);
                                                                                        progressDialog.dismiss();
                                                                                        String messageString = "An error occurred while deleting your account and could not be completed. Please try again";
                                                                                        showDialogWithMessage(Html.fromHtml(messageString));
                                                                                    }
                                                                                }
                                                                            });
                                                                        } else {
                                                                            progressDialog.dismiss();
                                                                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                                                            showDialogWithMessage(Html.fromHtml(message));
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                            else {
                                                                progressDialog.dismiss();
                                                                String messageString = "An error occurred while deleting your account and could not be completed. Please try again";
                                                                showDialogWithMessage(Html.fromHtml(messageString));
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private boolean validatePassword(String passwordString) {
        if (passwordString.isEmpty()) {
            String messageString = "You need to enter a valid password in the " + "<b>" + "Password" + "</b>" + " field.";
            showDialogWithMessage(Html.fromHtml(messageString));
            password.requestFocus();
            return false;
        }

        return true;
    }

    void showDialogWithMessage (Spanned messageString) {
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

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
