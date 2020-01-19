package com.celerii.celerii.Activities.LoginAndSignup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.ProviderQueryResult;

public class SignUpActivityThree extends AppCompatActivity {

    Context context = this;
    private Toolbar mToolbar;
    Button continue_;
    private EditText email;
    private String firstName, lastName, accountType;
    Bundle bundle;
    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_three);

        bundle = getIntent().getExtras();
        firstName = bundle.getString("firstName");
        lastName = bundle.getString("lastName");
        accountType = bundle.getString("accountType");

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        continue_ = (Button) findViewById(R.id.continue_);
        email = (EditText) findViewById(R.id.email);
        email.requestFocus();

        continue_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String emailString = email.getText().toString().trim();
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString);
                    return;
                }

                if (!validateEmail(emailString))
                    return;

                final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(SignUpActivityThree.this);
                progressDialog.show();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.fetchProvidersForEmail(emailString).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if (task.getResult().getProviders().size() > 0){
                            progressDialog.dismiss();
                            String messageString = "This email has already been registered. You should try a different email address. If you have lost your password, " +
                                    "use the Forgot Password section and we'll help recover it";
                            showDialogWithMessage(messageString);
                            email.requestFocus();
                            email.setSelectAllOnFocus(true);
                        } else {
                            Bundle infoBundle = new Bundle();
                            infoBundle.putString("firstName", firstName);
                            infoBundle.putString("lastName", lastName);
                            infoBundle.putString("accountType", accountType);
                            infoBundle.putString("email", emailString);

                            Intent I = new Intent(SignUpActivityThree.this, SignUpActivityFour.class);
                            I.putExtras(infoBundle);
                            startActivity(I);
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private boolean validateEmail(String emailString) {
        if (emailString.isEmpty()) {
            String messageString = "You need to enter an email address in the email field.";
            showDialogWithMessage(messageString);
            email.requestFocus();
            return false;
        }

        if (!isValidEmail(emailString)) {
            String messageString = "The email you entered is not a valid email address. Enter a valid email to continue";
            showDialogWithMessage(messageString);
            email.requestFocus();
            email.setSelectAllOnFocus(true);
            return false;
        }
        return true;
    }

    void showDialogWithMessage (String messageString) {
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
                dialog.dismiss();
            }
        });
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
