package com.celerii.celerii.Activities.LoginAndSignup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.R;

public class SignUpActivityTwo extends AppCompatActivity {

    final Context context = this;

    private Toolbar mToolbar;
    private Button continue_;
    private EditText accountType;
    private String firstName, lastName;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_two);

        bundle = getIntent().getExtras();
        firstName = bundle.getString("firstName");
        lastName = bundle.getString("lastName");

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                TextView parent = (TextView) dialog.findViewById(R.id.optionone);
                TextView teacher = (TextView) dialog.findViewById(R.id.optiontwo);
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

                Bundle infoBundle = new Bundle();
                infoBundle.putString("firstName", firstName);
                infoBundle.putString("lastName", lastName);
                infoBundle.putString("accountType", accountTypeString);

                Intent I = new Intent(SignUpActivityTwo.this, SignUpActivityThree.class);
                I.putExtras(infoBundle);
                startActivity(I);

            }
        });
    }

    private boolean validateAccountType(String accountTypeString) {
        if (accountTypeString.isEmpty()) {
            String messageString = "You need to select an account type from the Account Type dialog";
            showDialogWithMessage(messageString);
            return false;
        }

        return true;
    }

    void showDialogWithMessage (String messageString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final Dialog dialog = new Dialog(context);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
