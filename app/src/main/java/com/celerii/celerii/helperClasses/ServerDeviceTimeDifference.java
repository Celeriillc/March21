package com.celerii.celerii.helperClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.celerii.celerii.Activities.Utility.CorrectSystemTimeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServerDeviceTimeDifference {
    public static void getDeviceServerTimeDifference(final Context context) {

        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double offset = snapshot.getValue(Double.class);
                double timeDifferenceMinutes = offset / (1000.0); //In seconds

                if (Math.abs(timeDifferenceMinutes) > 43200.0) {
                    Intent intent = new Intent(context, CorrectSystemTimeActivity.class);
//                    ((Activity)context).finish();
                    ((Activity)context).startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
