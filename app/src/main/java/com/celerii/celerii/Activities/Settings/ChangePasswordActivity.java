package com.celerii.celerii.Activities.Settings;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.Html;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    EditText currentPasswordEditText, newPasswordEditText, reTypeNewPasswordEditText;
    Button edit;
    ImageButton toggleCurrentPasswordVisibility, toggleNewPasswordVisibility, toggleRetypePasswordVisibility;

    CustomProgressDialogOne progressDialog;
    boolean isCurrentPasswordVisible = false, isNewPasswordVisible = false, isRetypePasswordVisible = false;

    String featureUseKey = "";
    String featureName = "Change Password";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        progressDialog = new CustomProgressDialogOne(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        currentPasswordEditText = (EditText) findViewById(R.id.oldpassword);
        newPasswordEditText = (EditText) findViewById(R.id.newpassword);
        reTypeNewPasswordEditText = (EditText) findViewById(R.id.retypenewpassword);
        toggleCurrentPasswordVisibility = (ImageButton) findViewById(R.id.togglecurrentpasswordvisibility);
        toggleNewPasswordVisibility = (ImageButton) findViewById(R.id.togglenewpasswordvisibility);
        toggleRetypePasswordVisibility = (ImageButton) findViewById(R.id.toggleretypepasswordvisibility);
        edit = (Button) findViewById(R.id.edit);

        toggleCurrentPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCurrentPasswordVisible) {
                    currentPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    currentPasswordEditText.setSelection(currentPasswordEditText.length());
                    toggleCurrentPasswordVisibility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_search_black_24dp));
                    isCurrentPasswordVisible = true;
                } else {
                    currentPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    currentPasswordEditText.setSelection(currentPasswordEditText.length());
                    toggleCurrentPasswordVisibility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_view_password_eye_24));
                    isCurrentPasswordVisible = false;
                }
            }
        });

        toggleNewPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNewPasswordVisible) {
                    newPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    newPasswordEditText.setSelection(newPasswordEditText.length());
                    toggleNewPasswordVisibility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_search_black_24dp));
                    isNewPasswordVisible = true;
                } else {
                    newPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPasswordEditText.setSelection(newPasswordEditText.length());
                    toggleNewPasswordVisibility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_view_password_eye_24));
                    isNewPasswordVisible = false;
                }
            }
        });

        toggleRetypePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRetypePasswordVisible) {
                    reTypeNewPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    reTypeNewPasswordEditText.setSelection(reTypeNewPasswordEditText.length());
                    toggleRetypePasswordVisibility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_search_black_24dp));
                    isRetypePasswordVisible = true;
                } else {
                    reTypeNewPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    reTypeNewPasswordEditText.setSelection(reTypeNewPasswordEditText.length());
                    toggleRetypePasswordVisibility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_view_password_eye_24));
                    isRetypePasswordVisible = false;
                }
            }
        });

        for (UserInfo user: mFirebaseUser.getProviderData()) {
            String provider = user.getProviderId();
            if (provider.equals("google.com") || provider.equals("facebook.com") || provider.equals("twitter.com")) {
                String message = "You signed in using either of Google, Facebook or Twitter, your password is not mutable";
                showDialogWithMessageWithClose(Html.fromHtml(message));
                return;
            }
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(Html.fromHtml(messageString));
                    return;
                }

                final String currentPassword = currentPasswordEditText.getText().toString();
                final String newPassword = newPasswordEditText.getText().toString();
                String reTypeNewPassword = reTypeNewPasswordEditText.getText().toString();

//                if (!validatePassword(currentPassword, currentPasswordEditText, "Current Password"))
//                    return;

                if (!validatePassword(newPassword, newPasswordEditText, "New Password"))
                    return;

                if (!newPassword.equals(reTypeNewPassword)){
                    String message = "The " + "<b>" + "Retype New Password" + "</b>" + " field does not match the " + "<b>" + "New Password" + "</b>" + " field";
                    showDialogWithMessage(Html.fromHtml(message));
                    return;
                }

                progressDialog.show();

                AuthCredential credential = EmailAuthProvider.getCredential(mFirebaseUser.getEmail(), currentPassword);
                mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mFirebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        CustomToast.blueBackgroundToast(getBaseContext(), "Password changed successfully!");
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        String messageString = "Your password could not be changed at this time, please try again.";
                                        showDialogWithMessage(Html.fromHtml(messageString));
                                        currentPasswordEditText.requestFocus();
                                        currentPasswordEditText.setSelectAllOnFocus(true);
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            String messageString = "Your email and password combination don't match. Confirm your password information and try again. If you have lost your password, " +
                                    "logout and use the " + "<b>" +  "Forgot Password" + "</b>" + " area and we'll help you recover it";
                            showDialogWithMessage(Html.fromHtml(messageString));
                            currentPasswordEditText.requestFocus();
                            currentPasswordEditText.setSelectAllOnFocus(true);
                        }
                    }
                });
            }
        });
    }

    private boolean validatePassword(String password, EditText passwordEditText, String passwordType) {
        if (password.isEmpty()) {
            String messageString = "You need to enter a valid password in the " + "<b>" + passwordType + "</b>" + " field.";
            showDialogWithMessage(Html.fromHtml(messageString));
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 8) {
            String messageString = "Your selected password is not strong enough. It needs to be at least 8 characters long.";
            showDialogWithMessage(Html.fromHtml(messageString));
            passwordEditText.requestFocus();
            passwordEditText.setSelectAllOnFocus(true);
            return false;
        }

        if (!password.matches(".*\\d.*")){
            String messageString = "Your selected password is not strong enough. It needs to contain a number.";
            showDialogWithMessage(Html.fromHtml(messageString));
            passwordEditText.requestFocus();
            passwordEditText.setSelectAllOnFocus(true);
            return false;
        }
        return true;
    }

    void showDialogWithMessage (Spanned messageString) {
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

    void showDialogWithMessageWithClose (Spanned messageString) {
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
        String day = Date.getDay();
        String month = Date.getMonth();
        String year = Date.getYear();
        String day_month_year = day + "_" + month + "_" + year;
        String month_year = month + "_" + year;

        HashMap<String, Object> featureUseUpdateMap = new HashMap<>();
        String mFirebaseUserID = mFirebaseUser.getUid();

        featureUseUpdateMap.put("Analytics/Feature Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics User/" + mFirebaseUserID + "/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        featureUseUpdateMap.put("Analytics/Feature Use Analytics/" + featureName + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Daily Use Analytics/" + featureName + "/" + day_month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Monthly Use Analytics/" + featureName + "/" + month_year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);
        featureUseUpdateMap.put("Analytics/Feature Yearly Use Analytics/" + featureName + "/" + year + "/" + featureUseKey + "/sessionDurationInSeconds", sessionDurationInSeconds);

        DatabaseReference featureUseUpdateRef = FirebaseDatabase.getInstance().getReference();
        featureUseUpdateRef.updateChildren(featureUseUpdateMap);
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
