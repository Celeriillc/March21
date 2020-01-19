package com.celerii.celerii.Activities.LoginAndSignup;

import android.app.Dialog;
import android.content.Intent;
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

public class SignUpActivityOne extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button getStarted;
    private EditText firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_one);

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getStarted = (Button) findViewById(R.id.getstarted);
        firstName = (EditText) findViewById(R.id.firstname);
        lastName = (EditText) findViewById(R.id.lastname);
        firstName.requestFocus();

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String firstNameString = firstName.getText().toString().trim();
                final String lastNameString = lastName.getText().toString().trim();

                if (!validateName(firstNameString, firstName))
                    return;
                if (!validateName(lastNameString, lastName))
                    return;

                Bundle infoBundle = new Bundle();
                infoBundle.putString("firstName", firstNameString);
                infoBundle.putString("lastName", lastNameString);
                Intent I = new Intent(SignUpActivityOne.this, SignUpActivityTwo.class);
                I.putExtras(infoBundle);
                startActivity(I);
            }
        });

    }

    private boolean validateName(String nameString, EditText name) {
        if (nameString.isEmpty()) {
            String messageString = "You need to enter a name in both name fields";
            showDialogWithMessage(messageString);
            name.requestFocus();
            return false;
        }

        String[] nameArray = nameString.split(" ");
        if (nameArray.length > 1) {
            String messageString = "You should enter only one name in this field. If you have a double name, you can separate them with a hyphen (-). E.g. Ava-Grace.";
            showDialogWithMessage(messageString);
            name.requestFocus();
            name.setSelectAllOnFocus(true);
            return false;
        }

        return true;
    }

    void showDialogWithMessage (String messageString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
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
