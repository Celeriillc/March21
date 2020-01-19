package com.celerii.celerii.Activities.EditPersonalInformationDetails;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.MyTextWatcher;
import com.celerii.celerii.helperClasses.Validate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailEditActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    TextInputLayout newEmailLayout, currentPasswordLayout;
    EditText newEmail, currentPassword;
    Toolbar toolbar;
    Validate validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_edit);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);


        newEmailLayout = (TextInputLayout) findViewById(R.id.newemaillayout);
        currentPasswordLayout = (TextInputLayout) findViewById(R.id.currentpasswordlayout);
        newEmail = (EditText) findViewById(R.id.newemail);
        currentPassword = (EditText) findViewById(R.id.currentpassword);

        validate = new Validate(this);
        newEmail.addTextChangedListener(new MyTextWatcher(validate, newEmail, "email", newEmailLayout));
        currentPassword.addTextChangedListener(new MyTextWatcher(validate, currentPassword, "password", currentPasswordLayout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_send){
            //TODO: Validate against null values

            if (!validate.validateEmail(newEmail, newEmailLayout))
            {
                return false;
            }
            if (!validate.validatePassword(currentPassword, currentPasswordLayout))
            {
                return false;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(mFirebaseUser.getEmail(), currentPassword.getText().toString());
            mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent();
                        String text = newEmail.getText().toString();
                        intent.putExtra("newEmail", text);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
}
