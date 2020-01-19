package com.celerii.celerii.helperClasses;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by DELL on 11/28/2017.
 */

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
    }
}
