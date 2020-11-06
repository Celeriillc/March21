package com.celerii.celerii.Activities.LoginAndSignup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FederatedSignInAccountTypeActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;

    final Context context = this;

    FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar mToolbar;
    private Button continue_;
    private EditText accountType;

    CustomProgressDialogOne progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_federated_sign_in_account_type);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = mAuth.getCurrentUser();
        progressDialog = new CustomProgressDialogOne(FederatedSignInAccountTypeActivity.this);

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        continue_ = (Button) findViewById(R.id.continue_);
        accountType = (EditText) findViewById(R.id.accounttype);

        accountType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_binary_selection_dialog);
                TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
                Button parent = (Button) dialog.findViewById(R.id.optionone);
                Button teacher = (Button) dialog.findViewById(R.id.optiontwo);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                message.setText("Please select an account type for your home screen from the options below. Don't worry, you can still switch between modes within the app");

                parent.setText("Parent");
                teacher.setText("Teacher");

                parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        accountType.setText("Parent");
                        accountType.clearFocus();
                        dialog.dismiss();
                    }
                });

                teacher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        accountType.setText("Teacher");
                        accountType.clearFocus();
                        dialog.dismiss();
                    }
                });
            }
        });

        continue_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String accountTypeString = accountType.getText().toString();

                if (!validateAccountType(accountTypeString))
                    return;

                sharedPreferencesManager.setActiveAccount(accountTypeString);
                applicationLauncherSharedPreferences.setLauncherActivity("SignupFive");

                progressDialog.show();
                Map<String, Object> updateMap = new HashMap<String, Object>();
                updateMap.put("UserRoles/" + mFirebaseUser.getUid() + "/role", accountTypeString);
                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(updateMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        progressDialog.dismiss();
                        Intent I = new Intent(FederatedSignInAccountTypeActivity.this, SignUpActivityFive.class);
                        startActivity(I);
                        finish();
                    }
                });


            }
        });
    }

    private boolean validateAccountType(String accountTypeString) {
        if (accountTypeString.isEmpty()) {
            String messageString = "You need to select an account type from the Account Type dialog";
            showDialogWithMessage(Html.fromHtml(messageString));
            return false;
        }

        return true;
    }

    void showDialogWithMessage (Spanned messageString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
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
}
