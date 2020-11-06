package com.celerii.celerii.Activities.Delete;

import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.celerii.celerii.Activities.LoginAndSignup.ForgotPasswordActivity;
import com.celerii.celerii.Activities.LoginAndSignup.LoginActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.MyTextWatcher;
import com.celerii.celerii.helperClasses.Validate;

public class SignUpActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    Button forgotPassword, next, login;
    private EditText inputEmail, inputPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private ProgressBar mProgressBar;
    String[] accTypeSpinner = new String[]{"Parent", "Teacher"}; //Convert to string array resource
    private Spinner AccTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        forgotPassword = (Button) findViewById(R.id.btn_reset_password);
        next = (Button) findViewById(R.id.btn_next);
        login = (Button) findViewById(R.id.btn_login);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        emailLayout = (TextInputLayout) findViewById(R.id.emailLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        AccTypeSpinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_signup_layout, accTypeSpinner);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_signup_dropdown_layout);
        AccTypeSpinner.setAdapter(arrayAdapter);

        final Validate validate = new Validate(this);
        inputEmail.addTextChangedListener(new MyTextWatcher(validate, inputEmail, "email", emailLayout));
        inputPassword.addTextChangedListener(new MyTextWatcher(validate, inputPassword, "password", passwordLayout));


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(SignUpActivity.this, ForgotPasswordActivity.class);
                startActivity(I);
            }
        });

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent I = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(I);
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validate.validateEmail(inputEmail, emailLayout))
                {
                    return;
                }
                if (!validate.validatePassword(inputPassword, passwordLayout))
                {
                    return;
                }

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String AccountType = AccTypeSpinner.getSelectedItem().toString();

                Bundle infoBundle = new Bundle();
                infoBundle.putString("email", email);
                infoBundle.putString("password", password);
                infoBundle.putString("accType", AccountType);

                if (AccountType == "Parent")
                {
                    Intent I = new Intent(SignUpActivity.this, SignUpActivityTwoParents.class);
                    I.putExtras(infoBundle);
                    startActivity(I);
                }
            }
        });
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
