package com.celerii.celerii.Activities.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.models.ContactUsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    EditText subject, message;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        subject = (EditText) findViewById(R.id.messagesubject);
        message = (EditText) findViewById(R.id.message);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
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
            //TODO; Ensure we save updated data to database
            if (subject.getText().toString().trim().equals("")){
                CustomToast.whiteBackgroundBottomToast(this, "Error: The Subject field is empty");
                return false;
            }

            if (message.getText().toString().trim().equals("")){
                CustomToast.whiteBackgroundBottomToast(this, "Error: The Message field is empty");
                return false;
            }

            if (connected){
                String date = Date.getDate();
                String year = Date.getYear();
                String month = Date.getMonth();
                String day = Date.getDay();
                boolean seen = false;
                boolean responded = false;
                DatabaseReference contactKey = mFirebaseDatabase.getReference("Customer Feedback").child("Contact Us").push();
                String pushKey = contactKey.getKey();
                DatabaseReference contactRef = mFirebaseDatabase.getReference();
                ContactUsModel contactUsModel = new ContactUsModel(subject.getText().toString().trim(), message.getText().toString().trim(), mFirebaseUser.getUid(), mFirebaseUser.getEmail(), date, year, month, day, seen, responded);
                Map<String, Object> newContactUsMap = new HashMap<String, Object>();
                newContactUsMap.put("Customer Feedback/Contact Us/" + pushKey + "/", contactUsModel);
                contactRef.updateChildren(newContactUsMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        CustomToast.whiteBackgroundBottomToast(ContactUsActivity.this, "Thank you for reaching out");
                    }
                });
            } else {
                CustomToast.whiteBackgroundBottomToast(ContactUsActivity.this, "Connection lost, check and try again");
            }

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
