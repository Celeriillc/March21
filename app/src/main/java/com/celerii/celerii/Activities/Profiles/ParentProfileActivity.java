package com.celerii.celerii.Activities.Profiles;

import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditProfiles.EditParentProfileActivity;
import com.celerii.celerii.Activities.EditProfiles.EditTeacherProfileActivity;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Inbox.ChatActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Parent;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ParentProfileActivity extends AppCompatActivity {

    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    ScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    LinearLayout profilePictureClipper;
    ImageView parentPic;
    TextView name, gender, occupation;
    Button editYourProfile, message;

    String parentID, parentName = "", parentActivity;

    String featureUseKey = "";
    String featureName = "Parent Profile";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        parentID = bundle.getString("parentID");
        parentActivity = bundle.getString("parentActivity");
        if (parentActivity != null) {
            if (!parentActivity.isEmpty()) {
                sharedPreferencesManager.setActiveAccount(parentActivity);
                mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
                mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue(parentActivity);
            }
        }

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        superLayout = (ScrollView) findViewById(R.id.superlayout);
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
        message = (Button) findViewById(R.id.message);
        name = (TextView) findViewById(R.id.fullname);
        gender = (TextView) findViewById(R.id.gender);
        occupation = (TextView) findViewById(R.id.occupation);
        profilePictureClipper = (LinearLayout) findViewById(R.id.profilepictureclipper);
        profilePictureClipper.setClipToOutline(true);
        parentPic = (ImageView) findViewById(R.id.profilepic);

        if (parentID.equals(auth.getCurrentUser().getUid())){
            editYourProfile.setVisibility(View.VISIBLE);
            message.setVisibility(View.GONE);
        } else {
            editYourProfile.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
        }

        editYourProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(ParentProfileActivity.this, EditParentProfileActivity.class);
                startActivity(I);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ParentProfileActivity.this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ID", parentID);
                bundle.putString("name", parentName);
                I.putExtras(bundle);
                startActivity(I);
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );

        loadFromFirebase();
    }

    private void loadFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(parentID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Parent parent = dataSnapshot.getValue(Parent.class);
                    if (!parent.getDeleted()) {
                        parentName = parent.getFirstName() + " " + parent.getLastName();
                        getSupportActionBar().setTitle(parentName);
                        name.setText(parentName);

                        if (!parent.getGender().isEmpty()) {
                            gender.setText(parent.getGender());
                        } else {
                            gender.setText("Gender not set yet");
                        }

                        if (!parent.getOccupation().isEmpty()) {
                            occupation.setText(parent.getOccupation());
                        } else {
                            occupation.setText("Occupation not set yet");
                        }

                        Drawable textDrawable;
                        if (!parentName.isEmpty()) {
                            String[] nameArray = parentName.replaceAll("\\s+", " ").trim().split(" ");
                            if (nameArray.length == 1) {
                                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], 150);
                            } else {
                                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], nameArray[1], 150);
                            }
                            parentPic.setImageDrawable(textDrawable);
                        } else {
                            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
                        }

                        if (!parent.getProfilePicURL().isEmpty()) {
                            Glide.with(context)
                                    .load(parent.getProfilePicURL())
                                    .placeholder(textDrawable)
                                    .error(textDrawable)
                                    .centerCrop()
                                    .bitmapTransform(new CropCircleTransformation(context))
                                    .into(parentPic);
                        }

                        progressLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setRefreshing(false);
                        superLayout.setVisibility(View.VISIBLE);
                    } else {
                        superLayout.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setRefreshing(false);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText("This account has been deleted by the owner");
                    }
                } else {
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
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
                    Intent i = new Intent(this, ParentMainActivityTwo.class);
                    startActivity(i);
                } else if (parentActivity.equals("Teacher")) {
                    Intent i = new Intent(this, TeacherMainActivityTwo.class);
                    startActivity(i);
                }
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                Intent i = new Intent(this, ParentMainActivityTwo.class);
                startActivity(i);
            } else if (parentActivity.equals("Teacher")) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
                startActivity(i);
            }
        }
    }
}
