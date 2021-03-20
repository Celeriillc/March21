package com.celerii.celerii.Activities.EditPersonalInformationDetails;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EmailEditActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    EditText newEmail, currentPassword;
    Button editEmail;
    Toolbar toolbar;
    private ImageButton togglePasswordVisibility;

    CustomProgressDialogOne progressDialog;
    boolean isPasswordVisible = false;

    String featureUseKey = "";
    String featureName = "Edit Email";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_edit);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        editEmail = (Button) findViewById(R.id.editemail);
        newEmail = (EditText) findViewById(R.id.newemail);
        currentPassword = (EditText) findViewById(R.id.currentpassword);
        togglePasswordVisibility = (ImageButton) findViewById(R.id.togglepasswordvisibility);
        currentPassword.setTypeface(Typeface.DEFAULT);
        currentPassword.setTransformationMethod(new PasswordTransformationMethod());

        togglePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordVisible) {
                    currentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    currentPassword.setSelection(currentPassword.length());
                    togglePasswordVisibility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_off));
                    isPasswordVisible = true;
                } else {
                    currentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    currentPassword.setSelection(currentPassword.length());
                    togglePasswordVisibility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye));
                    isPasswordVisible = false;
                }
            }
        });

        for (UserInfo user: mFirebaseUser.getProviderData()) {
            String provider = user.getProviderId();
            if (provider.equals("google.com") || provider.equals("facebook.com") || provider.equals("twitter.com")) {
                String message = "You signed in using either of Google, Facebook or Twitter, your email is not mutable";
                showDialogWithMessageAndClose(Html.fromHtml(message));
                return;
            }
        }

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailString = newEmail.getText().toString().trim();
                final String passwordString = currentPassword.getText().toString();

                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(Html.fromHtml(messageString));
                    return;
                }

                if (!validateEmail(emailString))
                    return;
//                if (!validatePassword(passwordString))
//                    return;

                final HashMap<String, Object> changeEmailMap = new HashMap<String, Object>();
                changeEmailMap.put("Parent/" + mFirebaseUser.getUid() + "/email", emailString);
                changeEmailMap.put("Teacher/" + mFirebaseUser.getUid() + "/email", emailString);

                //reauthenticate user
                progressDialog = new CustomProgressDialogOne(EmailEditActivity.this);
                progressDialog.show();

                AuthCredential credential = EmailAuthProvider.getCredential(mFirebaseUser.getEmail(), passwordString);
                mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mFirebaseUser.updateEmail(emailString).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDatabaseReference = mFirebaseDatabase.getReference();
                                        mDatabaseReference.updateChildren(changeEmailMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                progressDialog.dismiss();
                                                CustomToast.blueBackgroundToast(getBaseContext(), "Email changed successfully!");
                                                finish();
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        String messageString = "Your email update action could not be completed, please try again.";
                                        showDialogWithMessage(Html.fromHtml(messageString));
                                        currentPassword.requestFocus();
                                        currentPassword.setSelectAllOnFocus(true);
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            String messageString = "Your email and password combination don't match. Confirm your login information and try again. If you have lost your password, " +
                                    "logout and use the " + "<b>" +  "Forgot Password" + "</b>" + " area and we'll help you recover it";
                            showDialogWithMessage(Html.fromHtml(messageString));
                            currentPassword.requestFocus();
                            currentPassword.setSelectAllOnFocus(true);
                        }
                    }
                });
            }
        });
    }

    private boolean validatePassword(String passwordString) {
        return true;
    }

    private boolean validateEmail(String emailString) {
        if (emailString.isEmpty()) {
            String messageString = "You need to enter an email address in the email field.";
            showDialogWithMessage(Html.fromHtml(messageString));
            newEmail.requestFocus();
            return false;
        }

        if (!isValidEmail(emailString)) {
            String messageString = "The address you entered is not a valid email address. Enter a valid address to continue";
            showDialogWithMessage(Html.fromHtml(messageString));
            newEmail.requestFocus();
            newEmail.setSelectAllOnFocus(true);
            return false;
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void showDialogWithMessage (Spanned messageString) {
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

    void showDialogWithMessageAndClose (Spanned messageString) {
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
                finish();
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
//        else if (id == R.id.action_send){
//
//        }

        return super.onOptionsItemSelected(item);
    }
}
