package com.celerii.celerii.helperClasses;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

public class CheckLoggedInState {
    public static void isLoggedIn(final Context context) {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseAuth.IdTokenListener authIDTokenListener;

        authIDTokenListener = new FirebaseAuth.IdTokenListener() {
            @Override
            public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (auth.getCurrentUser() == null) {
                    LogoutProtocol.logout(context);
                }
            }
        };
    }
}
