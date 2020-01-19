package com.celerii.celerii.Activities.LoginAndSignup;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPasswordActivity extends AppCompatActivity {

    Context context = this;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private Toolbar mToolbar;
    Button resetPassword;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        email = (EditText) findViewById(R.id.email);
        resetPassword = (Button) findViewById(R.id.resetpassword);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailString = email.getText().toString().trim();
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString, false);
                    return;
                }

                if (!validateEmail(emailString))
                    return;

                final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(ForgotPasswordActivity.this);
                progressDialog.show();

                FirebaseAuth.getInstance().sendPasswordResetEmail(emailString).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String messageString = "We have sent you instructions to reset your password.";
                            showDialogWithMessage(messageString, true);
                        } else {
                            String messageString = "We could not send you a password reset email at this time, please try again.";
                            showDialogWithMessage(messageString, false);
                        }
                    }
                });
            }
        });
    }

    private boolean validateEmail(String emailString) {
        if (emailString.isEmpty()) {
            String messageString = "You need to enter an email address in the email field.";
            showDialogWithMessage(messageString, false);
            email.requestFocus();
            return false;
        }

        if (!isValidEmail(emailString)) {
            String messageString = "The email you entered is not a valid email address. Enter a valid email to continue";
            showDialogWithMessage(messageString, false);
            email.requestFocus();
            email.setSelectAllOnFocus(true);
            return false;
        }
        return true;
    }

    void showDialogWithMessage (String messageString, final Boolean finish) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        TextView OK = (TextView) dialog.findViewById(R.id.optionone);
        dialog.show();

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finish) {
                    finish();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
