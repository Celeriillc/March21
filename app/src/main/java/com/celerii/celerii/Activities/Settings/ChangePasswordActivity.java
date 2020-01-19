package com.celerii.celerii.Activities.Settings;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    EditText oldPassword, newPassword, retypeNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        oldPassword = (EditText) findViewById(R.id.oldpassword);
        newPassword = (EditText) findViewById(R.id.newpassword);
        retypeNewPassword = (EditText) findViewById(R.id.retypenewpassword);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_dialog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_save){
            //TODO; Ensure we save updated data to database

            String oldPass = oldPassword.getText().toString();
            final String newPass = newPassword.getText().toString();
            String retypeNewPass = retypeNewPassword.getText().toString();

            if (!newPass.equals(retypeNewPass)){
                CustomToast.whiteBackgroundBottomToast(this, "The two passwords do not match");
                return false;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(mFirebaseUser.getEmail(), oldPass);
            mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mFirebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    CustomToast.whiteBackgroundBottomToast(ChangePasswordActivity.this, "Password Changed Successfully");
                                    finish();
                                } else {
                                    CustomToast.whiteBackgroundBottomToast(ChangePasswordActivity.this, "Password could not be changed");
                                }
                            }
                        });
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
}
