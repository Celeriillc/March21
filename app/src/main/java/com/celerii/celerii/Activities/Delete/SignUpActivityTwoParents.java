package com.celerii.celerii.Activities.Delete;

import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.MyTextWatcher;
import com.celerii.celerii.helperClasses.Validate;

public class SignUpActivityTwoParents extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button next;
    private EditText inputFirstName, inputLastName, inputPhoneNumber;
    private TextInputLayout firstNameLayout, lastNameLayout, phoneNumberLayout;
    private ProgressBar mProgressBar;
    String[] genderSpinner = new String[]{"Female", "Male"}; //Convert to string array resource
    private Spinner GenderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_two_parents);

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        next = (Button) findViewById(R.id.btn_next);
        inputFirstName = (EditText) findViewById(R.id.firstName);
        inputLastName = (EditText) findViewById(R.id.lastName);
        inputPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        lastNameLayout = (TextInputLayout) findViewById(R.id.lastNameLayout);
        firstNameLayout = (TextInputLayout) findViewById(R.id.firstNameLayout);
        phoneNumberLayout = (TextInputLayout) findViewById(R.id.phoneNumberLayout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        GenderSpinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_signup_layout, genderSpinner);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_signup_dropdown_layout);
        GenderSpinner.setAdapter(arrayAdapter);

        final Validate validate = new Validate(this);
        inputLastName.addTextChangedListener(new MyTextWatcher(validate, inputLastName, "name", lastNameLayout));
        inputFirstName.addTextChangedListener(new MyTextWatcher(validate, inputFirstName, "name", firstNameLayout));
        inputPhoneNumber.addTextChangedListener(new MyTextWatcher(validate, inputPhoneNumber, "name", phoneNumberLayout));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = null;
                String password = null;
                String accType = null;

                if (!validate.validateName(inputLastName, lastNameLayout))
                {
                    return;
                }

                if (!validate.validateName(inputFirstName, firstNameLayout))
                {
                    return;
                }

                if (!validate.validateName(inputPhoneNumber, phoneNumberLayout))
                {
                    return;
                }

                Bundle bundle = getIntent().getExtras();
                if (bundle != null) {
                    email = bundle.getString("email");
                    password = bundle.getString("password");
                    accType = bundle.getString("accType");
                }

                final String firstName = inputFirstName.getText().toString().trim();
                final String lastName = inputLastName.getText().toString().trim();
                final String phoneNumber = inputPhoneNumber.getText().toString().trim();
                final String gender = GenderSpinner.getSelectedItem().toString();

                Bundle infoBundle = new Bundle();
                infoBundle.putString("email", email);
                infoBundle.putString("password", password);
                infoBundle.putString("accType", accType);
                infoBundle.putString("firstName", firstName);
                infoBundle.putString("lastName", lastName);
                infoBundle.putString("phoneNumber", phoneNumber);
                infoBundle.putString("gender", gender);

                //Intent I = new Intent(SignUpActivityTwoParents.this, )
            }
        });
    }


}
