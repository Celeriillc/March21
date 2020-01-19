package com.celerii.celerii.helperClasses;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by user on 6/25/2017.
 */

public class MyTextWatcher implements TextWatcher {

    private EditText view;
    private String type;
    TextInputLayout textInputLayout;
    Validate validate;

    public MyTextWatcher(Validate validate, EditText view, String type, TextInputLayout textInputLayout) {
        this.validate = validate;
        this.view = view;
        this.type = type;
        this.textInputLayout = textInputLayout;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void afterTextChanged(Editable editable) {
        switch (type) {
            case "name":
                validate.validateName(view, textInputLayout);
                break;
            case "email":
                validate.validateEmail(view, textInputLayout);
                break;
            case "password":
                validate.validatePassword(view, textInputLayout);
                break;
        }
    }
}
