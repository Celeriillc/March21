package com.celerii.celerii.Activities.LoginAndSignup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Teacher;
import com.celerii.celerii.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivityFour extends AppCompatActivity {

    Context context = this;
    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    private Toolbar mToolbar;
    Button createAccount;
    private EditText password;
    private String firstName, lastName, accountType, email;
    private ImageButton togglePasswordVisisbility;
    Bundle bundle;

    CustomProgressDialogOne progressDialog;

    String activeAccount, activeUserID;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_four);

        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        sharedPreferencesManager = new SharedPreferencesManager(this);

        bundle = getIntent().getExtras();
        firstName = bundle.getString("firstName");
        lastName = bundle.getString("lastName");
        accountType = bundle.getString("accountType");
        email = bundle.getString("email");

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        createAccount = (Button) findViewById(R.id.createaccount);
        password = (EditText) findViewById(R.id.password);
        togglePasswordVisisbility = (ImageButton) findViewById(R.id.togglepasswordvisibility);
        password.requestFocus();
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        togglePasswordVisisbility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordVisible) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password.setSelection(password.length());
                    togglePasswordVisisbility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye));
                    isPasswordVisible = true;
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setSelection(password.length());
                    togglePasswordVisisbility.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_eye_off));
                    isPasswordVisible = false;
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String passwordString = password.getText().toString();
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString);
                    return;
                }

                if (!validatePassword(passwordString))
                    return;

                progressDialog = new CustomProgressDialogOne(SignUpActivityFour.this);
                progressDialog.show();

                auth.createUserWithEmailAndPassword(email, passwordString).addOnCompleteListener(SignUpActivityFour.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sharedPreferencesManager.clear();
                            String refactoredEmail = email.replace(".", "_fullStop_");
                            activeUserID = auth.getCurrentUser().getUid();
                            Map<String, Object> updateMap = new HashMap<String, Object>();
                            Parent parent = new Parent(firstName, lastName, firstName.toLowerCase(), lastName.toLowerCase(), email);
                            Teacher teacher = new Teacher(firstName, lastName, firstName.toLowerCase(), lastName.toLowerCase(), email);
                            User user = new User(email, accountType);
//                            String token = FirebaseInstanceId.getInstance().getToken();
//                            String deviceID = FirebaseInstanceId.getInstance().getId();
                            updateMap.put("Parent/" + activeUserID, parent);
                            updateMap.put("Teacher/" + activeUserID, teacher);
                            updateMap.put("UserRoles/" + activeUserID, user);
                            updateMap.put("Email/" + refactoredEmail, "true");
//                            updateMap.put("UserRoles/Tokens/" + deviceID, token);
                            Analytics.signupAnalytics(activeUserID, accountType);
                            Analytics.loginAnalytics(context, activeUserID, accountType);

                            mDatabaseReference = mFirebaseDatabase.getReference();
                            mDatabaseReference.updateChildren(updateMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        saveToSharedPreferencesAndProceed();
                                        return;
                                    } else {
                                        progressDialog.dismiss();
                                        String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                        ShowDialogWithMessage.showDialogWithMessageAndDelete(context, message, auth.getCurrentUser());
                                    }


//                                    if (databaseError == null) {
//                                        saveToSharedPreferencesAndProceed();
//                                    } else {
//                                        progressDialog.dismiss();
//                                        String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
//                                        ShowDialogWithMessage.showDialogWithMessageAndDelete(context, message, auth.getCurrentUser());
//                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            String messageString = "Your signup attempt failed, this could be due to a network error. Please try again";
                            showDialogWithMessage(messageString);
                        }
                    }
                });
            }
        });
    }

    private boolean validatePassword(String passwordString) {
        if (passwordString.isEmpty()) {
            String messageString = "You need to enter a valid password in the password field.";
            showDialogWithMessage(messageString);
            password.requestFocus();
            return false;
        }

        if (passwordString.length() < 8) {
            String messageString = "Your selected password is not strong enough. It needs to be at least 8 characters long.";
            showDialogWithMessage(messageString);
            password.requestFocus();
            password.setSelectAllOnFocus(true);
            return false;
        }

        if (!passwordString.matches(".*\\d.*")){
            String messageString = "Your selected password is not strong enough. It needs to contain a number.";
            showDialogWithMessage(messageString);
            password.requestFocus();
            password.setSelectAllOnFocus(true);
            return false;
        }
        return true;
    }

    void saveToSharedPreferencesAndProceed() {
        sharedPreferencesManager.setActiveAccount(accountType);
        sharedPreferencesManager.setMyUserID(activeUserID);
        sharedPreferencesManager.setMyFirstName(firstName);
        sharedPreferencesManager.setMyLastName(lastName);
        Bundle infoBundle = new Bundle();
        infoBundle.putString("UID", activeUserID);
        infoBundle.putString("accountType", accountType);
        applicationLauncherSharedPreferences.setLauncherActivity("SignupFive");
        progressDialog.dismiss();
        finishAffinity();
        Intent I = new Intent(SignUpActivityFour.this, SignUpActivityFive.class);
        I.putExtras(infoBundle);
        startActivity(I);
    }

    void showDialogWithMessage (String messageString) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
