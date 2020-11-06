package com.celerii.celerii.helperClasses;

import android.app.Activity;
import com.google.android.material.textfield.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by user on 6/25/2017.
 */

public class Validate {

    Activity myActivityReference;

    public Validate(Activity myActivityReference) {
        this.myActivityReference = myActivityReference;
    }

    public boolean validateName(EditText Name, TextInputLayout nameLayout) {
        if (Name.getText().toString().trim().isEmpty()) {
            nameLayout.setError("Please enter your name");
            requestFocus(Name);
            return false;
        } else {
            nameLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateEmail(EditText eMail, TextInputLayout emailLayout) {
        String email = eMail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailLayout.setError("Valid Email");
            requestFocus(eMail);
            return false;
        } else {
            emailLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean validatePassword(EditText passWord, TextInputLayout passWordLayout) {
        if (passWord.getText().toString().trim().isEmpty()) {
            passWordLayout.setError("Enter password");
            requestFocus(passWord);
            return false;
        } else {
            passWordLayout.setErrorEnabled(false);
        }

        return true;
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            myActivityReference.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
