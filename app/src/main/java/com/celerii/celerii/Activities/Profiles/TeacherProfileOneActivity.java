package com.celerii.celerii.Activities.Profiles;

import android.content.Context;
import android.content.Intent;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditProfiles.EditTeacherProfileActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Teacher;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TeacherProfileOneActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    Bundle bundle;

    NestedScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    boolean maritalStatusShowStatus;

    Toolbar toolbar;
    ImageView profilePic;
    LinearLayout profilePictureClipper, maritalStatusLayout;
    TextView fullName, gender, maritalStatus, pointsAwarded, pointsFined, classPost, postLikes, teacherBio;
    Button editYourProfile;
//    Button message;

    String teacherID = "", teacherName = "";

    String featureUseKey = "";
    String featureName = "Teacher Profile";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile_one);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        bundle = getIntent().getExtras();
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        teacherID = bundle.getString("ID");

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        superLayout = (NestedScrollView) findViewById(R.id.superlayout);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        editYourProfile = (Button) findViewById(R.id.editprofile);
//        message = (Button) findViewById(R.id.message);
        profilePic = (ImageView) findViewById(R.id.profilepic);
        profilePictureClipper = (LinearLayout) findViewById(R.id.profilepictureclipper);
        maritalStatusLayout = (LinearLayout) findViewById(R.id.maritalstatuslayout);

        fullName = (TextView) findViewById(R.id.fullname);
        gender = (TextView) findViewById(R.id.gender);
        maritalStatus = (TextView) findViewById(R.id.maritalstatus);
        pointsAwarded = (TextView) findViewById(R.id.pointsawarded);
        pointsFined = (TextView) findViewById(R.id.pointsfined);
        classPost = (TextView) findViewById(R.id.classposts);
        postLikes = (TextView) findViewById(R.id.postlikes);
        teacherBio = (TextView) findViewById(R.id.bio);

        if (teacherID.equals(auth.getCurrentUser().getUid())){
            editYourProfile.setVisibility(View.VISIBLE);
//            message.setVisibility(View.GONE);
        } else {
            editYourProfile.setVisibility(View.GONE);
//            message.setVisibility(View.VISIBLE);
        }

        loadFromFirebase();

        editYourProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(TeacherProfileOneActivity.this, EditTeacherProfileActivity.class);
                startActivity(I);
            }
        });

//        message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent I = new Intent(TeacherProfileOneActivity.this, ChatActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("ID", teacherID);
//                bundle.putString("name", teacherName);
//                I.putExtras(bundle);
//                startActivity(I);
//            }
//        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );
    }

    void loadFromFirebase(){
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            superLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference("Teacher").child(teacherID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final Teacher teacher = dataSnapshot.getValue(Teacher.class);

                    if (!teacher.getIsDeleted()) {
                        teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                        fullName.setText(teacherName);
                        getSupportActionBar().setTitle(teacherName);

                        if (!teacher.getGender().equals("")) {
                            gender.setText(teacher.getGender());
                        } else {
                            gender.setText("Gender not set");
                        }

                        if (!teacher.getMaritalStatus().equals("")) {
                            maritalStatus.setText(teacher.getMaritalStatus());
                        } else {
                            maritalStatus.setText("Relationship status not set");
                        }

                        Drawable textDrawable;
                        if (!teacherName.isEmpty()) {
                            String[] nameArray = teacherName.replaceAll("\\s+", " ").trim().split(" ");
                            if (nameArray.length == 1) {
                                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], 150);
                            } else {
                                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], nameArray[1], 150);
                            }
                            profilePic.setImageDrawable(textDrawable);
                        } else {
                            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 150);
                        }

                        if (!teacher.getProfilePicURL().isEmpty()) {
                            Glide.with(context)
                                    .load(teacher.getProfilePicURL())
                                    .placeholder(textDrawable)
                                    .error(textDrawable)
                                    .centerCrop()
                                    .bitmapTransform(new CropCircleTransformation(context))
                                    .into(profilePic);
                        }

                        mDatabaseReference = mFirebaseDatabase.getReference("Teacher Bio").child(teacherID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String bio = dataSnapshot.getValue(String.class);
                                    teacherBio.setVisibility(View.VISIBLE);
                                    teacherBio.setText(bio);
                                }
                                else {
                                    teacherBio.setText("No available bio");
                                }

//                                                if (!maritalStatusShowStatus) { maritalStatusLayout.setVisibility(View.GONE); }

                                superLayout.setVisibility(View.VISIBLE);
                                progressLayout.setVisibility(View.GONE);
                                mySwipeRefreshLayout.setRefreshing(false);
                                errorLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

//                        mDatabaseReference = mFirebaseDatabase.getReference("TeacherActivityStatistics").child(teacherID);
//                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot.exists()){
//                                    TeacherActivityStatistics teacherActivityStatistics = dataSnapshot.getValue(TeacherActivityStatistics.class);
//                                    pointsAwarded.setText(String.valueOf(teacherActivityStatistics.getTotalPointsAwarded()));
//                                    pointsFined.setText(String.valueOf(teacherActivityStatistics.getTotalPointsFined()));
//                                    classPost.setText(String.valueOf(teacherActivityStatistics.getTotalClassPosts()));
//                                    postLikes.setText(String.valueOf(teacherActivityStatistics.getTotalPostLikes()));
//                                }
//
//                                else {
//                                    pointsAwarded.setText("0");
//                                    pointsFined.setText("0");
//                                    classPost.setText("0");
//                                    postLikes.setText("0");
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
                    } else {
                        superLayout.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setRefreshing(false);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText("This account has been deleted by the owner");
                    }
                }
                else{
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(false);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("This account has been deleted by the owner");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            featureUseKey = Analytics.featureAnalytics("Parent", mFirebaseUser.getUid(), featureName);
        } else {
            featureUseKey = Analytics.featureAnalytics("Teacher", mFirebaseUser.getUid(), featureName);
        }
        sessionStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();

        sessionDurationInSeconds = String.valueOf((System.currentTimeMillis() - sessionStartTime) / 1000);
        Analytics.featureAnalyticsUpdateSessionDuration(featureName, featureUseKey, mFirebaseUser.getUid(), sessionDurationInSeconds);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
