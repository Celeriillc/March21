package com.celerii.celerii.Activities.LoginAndSignup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class LoginActivity extends AppCompatActivity {

    Context context = this;
    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;

    private Toolbar mToolbar;
    Button forgotPassword, login;
    private EditText email, password;
//    private ProgressBar mProgressBar;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    CustomProgressDialogOne progressDialog;

    String activeAccount, activeUserId, firstName, lastName, activeUserURL;
    boolean connected;

    ArrayList<String> childrenFirebase = new ArrayList<>();
    ArrayList<String> classesFirebase = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        forgotPassword = (Button) findViewById(R.id.forgotpassword);
        login = (Button) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.email);
        email.requestFocus();
        password = (EditText) findViewById(R.id.password);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(I);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailString = email.getText().toString().trim();
                final String passwordString = password.getText().toString();

                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(messageString);
                    return;
                }

                if (!validateEmail(emailString))
                    return;
                if (!validatePassword(passwordString))
                    return;

                //authenticate user
                progressDialog = new CustomProgressDialogOne(LoginActivity.this);
                progressDialog.show();

                mFirebaseAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            String messageString = "There seems to be a problem with your login, confirm your login information and try again. If you have lost your password, " +
                                    "use the Forgot Password area and we'll help recover it";
                            showDialogWithMessage(messageString);
                        } else {
                            sharedPreferencesManager.clear();
                            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            mDatabaseReference = mFirebaseDatabase.getReference().child("UserRoles").child(mFirebaseUser.getUid());
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        User user = dataSnapshot.getValue(User.class);
                                        activeAccount = user.getRole();
                                        login();
                                    } else {
                                        activeAccount = "Parent";
                                        login();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private boolean validatePassword(String passwordString) {
        return true;
    }

    private boolean validateEmail(String emailString) {
        if (emailString.isEmpty()) {
            String messageString = "You need to enter an email address in the email field.";
            showDialogWithMessage(messageString);
            email.requestFocus();
            return false;
        }

        if (!isValidEmail(emailString)) {
            String messageString = "The address you entered is not a valid email address. Enter a valid address to continue";
            showDialogWithMessage(messageString);
            email.requestFocus();
            email.setSelectAllOnFocus(true);
            return false;
        }

        return true;
    }

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        TextView OK = (TextView) dialog.findViewById(R.id.optionone);
        dialog.show();

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void login() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Parent parent = dataSnapshot.getValue(Parent.class);
                    sharedPreferencesManager.setActiveAccount(activeAccount);
                    sharedPreferencesManager.setMyUserID(mFirebaseUser.getUid());
                    sharedPreferencesManager.setMyFirstName(parent.getFirstName());
                    sharedPreferencesManager.setMyLastName(parent.getLastName());
                    sharedPreferencesManager.setMyPicURL(parent.getProfilePicURL());

                    getMyChildren(mFirebaseUser.getUid());
                } else {
                    progressDialog.dismiss();
                    String messageString = "There seems to be a problem with your login, confirm your login information and try again. If you have lost your password, use the Forgot Password area and we'll help recover it";
                    showDialogWithMessage(messageString);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    int childrenCounter;
    void getMyChildren(final String myID) {
        childrenCounter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                        final String childKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                childrenCounter++;
                                if (dataSnapshot.exists()) {
                                    Student childInstance = dataSnapshot.getValue(Student.class);
                                    childrenFirebase.add(childKey + " " + childInstance.getFirstName() + " " + childInstance.getLastName() + " " + childInstance.getImageURL());

                                    sharedPreferencesManager.deleteMyChildren();
                                    sharedPreferencesManager.setMyChildren(new HashSet<String>(childrenFirebase));
                                }

                                if (childrenCounter == childrenCount) {
                                    getMyClasses(myID);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    getMyClasses(myID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    int classesCounter;
    void getMyClasses(String myID) {
        classesCounter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Class").child(myID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final int classesCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                        final String classKey = postSnapShot.getKey();

                        mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                classesCounter++;
                                if (dataSnapshot.exists()) {
                                    Class classInstance = dataSnapshot.getValue(Class.class);
                                    classesFirebase.add(classKey + " " + classInstance.getClassName() + " " + classInstance.getClassPicURL());

                                    sharedPreferencesManager.deleteMyClasses();
                                    sharedPreferencesManager.setMyClasses(new HashSet<String>(classesFirebase));
                                }

                                if (classesCount == classesCounter) {
                                    applicationLauncherSharedPreferences.setLauncherActivity("Home");
                                    if (activeAccount.equals("Parent")) {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, ParentMainActivityTwo.class);
                                        startActivity(intent);
                                    } else {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, TeacherMainActivityTwo.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    applicationLauncherSharedPreferences.setLauncherActivity("Home");
                    if (activeAccount.equals("Parent")) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, ParentMainActivityTwo.class);
                        startActivity(intent);
                    } else {
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, TeacherMainActivityTwo.class);
                        startActivity(intent);
                    }
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    void loginParent(final String activeRole){
//        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    Parent parent = dataSnapshot.getValue(Parent.class);
//                    activeAccount = activeRole;
//                    activeUserId = mFirebaseUser.getUid();
//                    firstName = parent.getFirstName();
//                    lastName = parent.getLastName();
//                    activeUserURL = parent.getProfilePicURL();
//                    mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
//                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()){
//                                for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
//                                    final String childKey = postSnapShot.getKey();
//
//                                    mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
//                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.exists()) {
//                                                Student childInstance = dataSnapshot.getValue(Student.class);
//                                                childrenFirebase.add(childKey + " " + childInstance.getFirstName() + " " + childInstance.getLastName() + " " + childInstance.getImageURL());
//
//                                                sharedPreferencesManager.deleteMyChildren();
//                                                sharedPreferencesManager.setMyChildren(new HashSet<String>(childrenFirebase));
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//                            } else {
//
//                            }
//
//                            mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Class").child(mFirebaseUser.getUid());
//                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()){
//                                        for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
//                                            final String classKey = postSnapShot.getKey();
//
//                                            mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
//                                            mDatabaseReference.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot.exists()){
//                                                        Class classInstance = dataSnapshot.getValue(Class.class);
//                                                        classesFirebase.add(classKey + " " + classInstance.getClassName() + " " + classInstance.getClassPicURL());
//
//                                                        sharedPreferencesManager.deleteMyClasses();
//                                                        sharedPreferencesManager.setMyClasses(new HashSet<String>(classesFirebase));
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        }
//
//                                    } else {
//
//                                    }
//
//                                    sharedPreferencesManager.setActiveAccount(activeAccount);
//                                    sharedPreferencesManager.setMyUserID(activeUserId);
//                                    sharedPreferencesManager.setMyFirstName(firstName);
//                                    sharedPreferencesManager.setMyLastName(lastName);
//                                    sharedPreferencesManager.setMyPicURL(activeUserURL);
//                                    Intent intent = new Intent(LoginActivity.this, ParentMainActivityTwo.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    //TODO: sharedPreferencesManager.setStartScreen() and sharedPreferencesManager.getStartScreen();
//
//    void loginTeacher(final String activeRole) {
//        mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher").child(mFirebaseUser.getUid());
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
//                    activeAccount = activeRole;
//                    activeUserId = mFirebaseUser.getUid();
//                    firstName = teacher.getFirstName();
//                    lastName = teacher.getLastName();
//                    activeUserURL = teacher.getProfilePicURL();
//                    mDatabaseReference = mFirebaseDatabase.getReference().child("Teacher Class").child(mFirebaseUser.getUid());
//                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()){
//                                for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
//                                    final String classKey = postSnapShot.getKey();
//
//                                    mDatabaseReference = mFirebaseDatabase.getReference("Class").child(classKey);
//                                    mDatabaseReference.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.exists()){
//                                                Class classInstance = dataSnapshot.getValue(Class.class);
//                                                classesFirebase.add(classKey + " " + classInstance.getClassName() + " " + classInstance.getClassPicURL());
//
//                                                sharedPreferencesManager.deleteMyClasses();
//                                                sharedPreferencesManager.setMyClasses(new HashSet<String>(classesFirebase));
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//
//                            } else {
//
//                            }
//
//                            mDatabaseReference = mFirebaseDatabase.getReference().child("Parents Students").child(mFirebaseUser.getUid());
//                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()){
//                                        for (DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
//                                            final String childKey = postSnapShot.getKey();
//
//                                            mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childKey);
//                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot.exists()) {
//                                                        Student childInstance = dataSnapshot.getValue(Student.class);
//                                                        childrenFirebase.add(childKey + " " + childInstance.getFirstName() + " " + childInstance.getLastName() + " " + childInstance.getImageURL());
//
//                                                        sharedPreferencesManager.deleteMyChildren();
//                                                        sharedPreferencesManager.setMyChildren(new HashSet<String>(childrenFirebase));
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        }
//                                    } else {
//
//                                    }
//
//                                    sharedPreferencesManager.setActiveAccount(activeAccount);
//                                    sharedPreferencesManager.setMyUserID(activeUserId);
//                                    sharedPreferencesManager.setMyFirstName(firstName);
//                                    sharedPreferencesManager.setMyLastName(lastName);
//                                    sharedPreferencesManager.setMyPicURL(activeUserURL);
//                                    Intent intent = new Intent(LoginActivity.this, TeacherMainActivityTwo.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
