package com.celerii.celerii.Activities.Utility;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SwitchActivityTeacherParent extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_teacher_parent);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        sharedPreferencesManager.setActiveAccount("Parent");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
        mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue("Parent");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SwitchActivityTeacherParent.this, ParentMainActivityTwo.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
