package com.celerii.celerii.Activities.Utility;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SwitchActivityTeacherParent extends AppCompatActivity {

    Context context;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    SharedPreferencesManager sharedPreferencesManager;
    String activeUser = "Parent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_teacher_parent);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        sharedPreferencesManager.setActiveAccount(activeUser);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
        mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue(activeUser);
        Analytics.loginAnalytics(context, mFirebaseUser.getUid(), activeUser);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SwitchActivityTeacherParent.this, ParentMainActivityTwo.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
