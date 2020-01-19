package com.celerii.celerii.Activities.Home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.ParentSchoolConnectionRequest;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class NotificationDetailActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;

    Toolbar mToolbar;
    Bundle bundle;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    LinearLayout notificationLayout;
    RelativeLayout progressLayout;
    ImageView profilePicURL, notificationPic;
    TextView notification;

    String activityID = "";
    String notificationType = "";
    String requestSender = "";
    String requestSenderURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        bundle = getIntent().getExtras();
        activityID = bundle.getString("postKey");
        notificationType = bundle.getString("notificationType");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Notification");

        notificationLayout = (LinearLayout) findViewById(R.id.notificationlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        profilePicURL = (ImageView) findViewById(R.id.pic);
        notificationPic = (ImageView) findViewById(R.id.notificationpic);
        notification = (TextView) findViewById(R.id.notification);

        progressLayout.setVisibility(View.VISIBLE);
        notificationLayout.setVisibility(View.GONE);

        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Student Connection Request Sender").child(mFirebaseUser.getUid()).child(activityID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final ParentSchoolConnectionRequest parentSchoolConnectionRequest = dataSnapshot.getValue(ParentSchoolConnectionRequest.class);
                    final ArrayList<String> recipients = parentSchoolConnectionRequest.getRequestReciepients();

                    mDatabaseReference = mFirebaseDatabase.getReference().child("Student").child(parentSchoolConnectionRequest.getStudentID());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Student student = dataSnapshot.getValue(Student.class);
                                final String studentName = student.getFirstName() + " " + student.getLastName();
                                final String studentPicURL = student.getImageURL();

                                if (parentSchoolConnectionRequest.getRequestResponder().equals(mFirebaseUser.getUid())) {
                                    if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(parentSchoolConnectionRequest.getRequestSenderID());
                                    } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(parentSchoolConnectionRequest.getRequestSenderID());
                                    }
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String requestSender = "";
                                                String requestSenderURL = "";
                                                if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                                    Parent parent = dataSnapshot.getValue(Parent.class);
                                                    requestSender = parent.getFirstName() + " " + parent.getLastName();
                                                    requestSenderURL = parent.getProfilePicURL();
                                                } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                                    School school = dataSnapshot.getValue(School.class);
                                                    requestSender = school.getSchoolName();
                                                    requestSenderURL = school.getProfilePhotoUrl();
                                                } else {
                                                    requestSender = dataSnapshot.getKey();
                                                }

                                                if (parentSchoolConnectionRequest.getRequestResponse().equals("Accepted")) {
                                                    String message = "You accepted " + requestSender + "'s request to connect to " + studentName + " account";
                                                    notification.setText(message);
                                                } else if (parentSchoolConnectionRequest.getRequestResponse().equals("Declined")) {
                                                    String message = "You declined " + requestSender + "'s request to connect to " + studentName + " account";
                                                    notification.setText(message);
                                                }

                                                Glide.with(NotificationDetailActivity.this)
                                                        .load(requestSenderURL)
                                                        .placeholder(R.drawable.profileimageplaceholder)
                                                        .error(R.drawable.profileimageplaceholder)
                                                        .centerCrop()
                                                        .bitmapTransform(new CropCircleTransformation(NotificationDetailActivity.this))
                                                        .into(profilePicURL);

                                                Glide.with(NotificationDetailActivity.this)
                                                        .load(studentPicURL)
                                                        .placeholder(R.drawable.profileimageplaceholder)
                                                        .error(R.drawable.profileimageplaceholder)
                                                        .centerCrop()
                                                        .into(notificationPic);

                                                progressLayout.setVisibility(View.GONE);
                                                notificationLayout.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else if (parentSchoolConnectionRequest.getRequestSenderID().equals(mFirebaseUser.getUid())) {
                                    mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(parentSchoolConnectionRequest.getRequestResponder());
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Parent parent = dataSnapshot.getValue(Parent.class);
                                                String requestResponder = parent.getFirstName() + " " + parent.getLastName();

                                                if (parentSchoolConnectionRequest.getRequestResponse().equals("Accepted")) {
                                                    String message = requestResponder + " accepted your request to connect to " + studentName + " account";
                                                    notification.setText(message);
                                                } else if (parentSchoolConnectionRequest.getRequestResponse().equals("Declined")) {
                                                    String message = requestResponder + " declined your request to connect to " + studentName + " account";
                                                    notification.setText(message);
                                                }

                                                Glide.with(NotificationDetailActivity.this)
                                                        .load(sharedPreferencesManager.getMyPicURL())
                                                        .placeholder(R.drawable.profileimageplaceholder)
                                                        .error(R.drawable.profileimageplaceholder)
                                                        .centerCrop()
                                                        .bitmapTransform(new CropCircleTransformation(NotificationDetailActivity.this))
                                                        .into(profilePicURL);

                                                Glide.with(NotificationDetailActivity.this)
                                                        .load(studentPicURL)
                                                        .placeholder(R.drawable.profileimageplaceholder)
                                                        .error(R.drawable.profileimageplaceholder)
                                                        .centerCrop()
                                                        .into(notificationPic);

                                                progressLayout.setVisibility(View.GONE);
                                                notificationLayout.setVisibility(View.VISIBLE);
                                            } else {
                                                mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(parentSchoolConnectionRequest.getRequestResponder());
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            School school = dataSnapshot.getValue(School.class);
                                                            String requestResponder = school.getSchoolName();

                                                            if (parentSchoolConnectionRequest.getRequestResponse().equals("Accepted")) {
                                                                String message = requestResponder + " accepted your request to connect to " + studentName + " account";
                                                                notification.setText(message);
                                                            } else if (parentSchoolConnectionRequest.getRequestResponse().equals("Declined")) {
                                                                String message = requestResponder + " declined your request to connect to " + studentName + " account";
                                                                notification.setText(message);
                                                            }

                                                            Glide.with(NotificationDetailActivity.this)
                                                                    .load(sharedPreferencesManager.getMyPicURL())
                                                                    .placeholder(R.drawable.profileimageplaceholder)
                                                                    .error(R.drawable.profileimageplaceholder)
                                                                    .centerCrop()
                                                                    .bitmapTransform(new CropCircleTransformation(NotificationDetailActivity.this))
                                                                    .into(profilePicURL);

                                                            Glide.with(NotificationDetailActivity.this)
                                                                    .load(studentPicURL)
                                                                    .placeholder(R.drawable.profileimageplaceholder)
                                                                    .error(R.drawable.profileimageplaceholder)
                                                                    .centerCrop()
                                                                    .into(notificationPic);

                                                            progressLayout.setVisibility(View.GONE);
                                                            notificationLayout.setVisibility(View.VISIBLE);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else if (recipients.contains(mFirebaseUser.getUid())) {
                                    if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(parentSchoolConnectionRequest.getRequestSenderID());
                                    } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                        mDatabaseReference = mFirebaseDatabase.getReference().child("School").child(parentSchoolConnectionRequest.getRequestSenderID());
                                    }
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                                    Parent parent = dataSnapshot.getValue(Parent.class);
                                                    requestSender = parent.getFirstName() + " " + parent.getLastName();
                                                    requestSenderURL = parent.getProfilePicURL();
                                                } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                                    School school = dataSnapshot.getValue(School.class);
                                                    requestSender = school.getSchoolName();
                                                    requestSenderURL = school.getProfilePhotoUrl();
                                                } else {
                                                    requestSender = dataSnapshot.getKey();
                                                }

                                                mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(parentSchoolConnectionRequest.getRequestResponder());
                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            Parent parent = dataSnapshot.getValue(Parent.class);
                                                            String requestResponder = parent.getFirstName() + " " + parent.getLastName();

                                                            if (parentSchoolConnectionRequest.getRequestResponse().equals("Accepted")) {
                                                                String message = requestResponder + " accepted " + requestSender + "s request to connect to " + studentName + " account";
                                                                notification.setText(message);
                                                            } else if (parentSchoolConnectionRequest.getRequestResponse().equals("Declined")) {
                                                                String message = requestResponder + " declined " + requestSender + "s request to connect to " + studentName + " account";
                                                                notification.setText(message);
                                                            }

                                                            Glide.with(NotificationDetailActivity.this)
                                                                    .load(requestSenderURL)
                                                                    .placeholder(R.drawable.profileimageplaceholder)
                                                                    .error(R.drawable.profileimageplaceholder)
                                                                    .centerCrop()
                                                                    .bitmapTransform(new CropCircleTransformation(NotificationDetailActivity.this))
                                                                    .into(profilePicURL);

                                                            Glide.with(NotificationDetailActivity.this)
                                                                    .load(studentPicURL)
                                                                    .placeholder(R.drawable.profileimageplaceholder)
                                                                    .error(R.drawable.profileimageplaceholder)
                                                                    .centerCrop()
                                                                    .into(notificationPic);

                                                            progressLayout.setVisibility(View.GONE);
                                                            notificationLayout.setVisibility(View.VISIBLE);
                                                        } else {
                                                            mDatabaseReference = mFirebaseDatabase.getReference().child("Parent").child(parentSchoolConnectionRequest.getRequestResponder());
                                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        School school = dataSnapshot.getValue(School.class);
                                                                        String requestResponder = school.getSchoolName();

                                                                        if (parentSchoolConnectionRequest.getRequestResponse().equals("Accepted")) {
                                                                            String message = requestResponder + " accepted " + requestSender + "s request to connect to " + studentName + " account";
                                                                            notification.setText(message);
                                                                        } else if (parentSchoolConnectionRequest.getRequestResponse().equals("Declined")) {
                                                                            String message = requestResponder + " declined " + requestSender + "s request to connect to " + studentName + " account";
                                                                            notification.setText(message);
                                                                        }

                                                                        Glide.with(NotificationDetailActivity.this)
                                                                                .load(requestSenderURL)
                                                                                .placeholder(R.drawable.profileimageplaceholder)
                                                                                .error(R.drawable.profileimageplaceholder)
                                                                                .centerCrop()
                                                                                .bitmapTransform(new CropCircleTransformation(NotificationDetailActivity.this))
                                                                                .into(profilePicURL);

                                                                        Glide.with(NotificationDetailActivity.this)
                                                                                .load(studentPicURL)
                                                                                .placeholder(R.drawable.profileimageplaceholder)
                                                                                .error(R.drawable.profileimageplaceholder)
                                                                                .centerCrop()
                                                                                .into(notificationPic);

                                                                        progressLayout.setVisibility(View.GONE);
                                                                        notificationLayout.setVisibility(View.VISIBLE);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
