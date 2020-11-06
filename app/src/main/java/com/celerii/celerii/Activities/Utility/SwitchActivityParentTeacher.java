package com.celerii.celerii.Activities.Utility;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SwitchActivityParentTeacher extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_parent_teacher);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        sharedPreferencesManager.setActiveAccount("Teacher");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
        mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue("Teacher");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SwitchActivityParentTeacher.this, TeacherMainActivityTwo.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }
}
