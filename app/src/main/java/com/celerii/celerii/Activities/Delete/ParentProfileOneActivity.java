package com.celerii.celerii.Activities.Delete;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.Parent;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ParentProfileOneActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    ImageView profilePic, backgroundPic;
    TextView fullName, gender, maritalStatus, occupation, location;
    LinearLayout fullNameLayout, genderLayout, maritalStatusLayout, occupationLayout, locationLayout;
    Button editYourProfile;
    String parentID = "31sQQgT5hYPE0YkpaAyJj5MtA0b2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile_one);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        editYourProfile = (Button) findViewById(R.id.edityourprofile);
        profilePic = (ImageView) findViewById(R.id.profilepic);
        backgroundPic = (ImageView) findViewById(R.id.backgroundimage);

        fullName = (TextView) findViewById(R.id.fullname);
        gender = (TextView) findViewById(R.id.gender);
        maritalStatus = (TextView) findViewById(R.id.maritalstatus);
        occupation = (TextView) findViewById(R.id.occupation);
        location = (TextView) findViewById(R.id.location);

        fullNameLayout = (LinearLayout) findViewById(R.id.fullnamelayout);
        genderLayout = (LinearLayout) findViewById(R.id.genderlayout);
        maritalStatusLayout = (LinearLayout) findViewById(R.id.maritalstatuslayout);
        occupationLayout = (LinearLayout) findViewById(R.id.occupationlayout);
        locationLayout = (LinearLayout) findViewById(R.id.locationlayout);

        if (!parentID.equals(mFirebaseUser.getUid())){
            editYourProfile.setVisibility(View.GONE);
        }

        editYourProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        loadFirebaseContent();
    }

    private void loadFirebaseContent() {
        mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(parentID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Parent parent = dataSnapshot.getValue(Parent.class);
                    String Sname = parent.getFirstName() + " " + parent.getLastName();
                    String Sgender = parent.getGender();
                    String SmaritalStat = parent.getMaritalStatus();
                    String Soccupation = parent.getOccupation();
                    String Slocation = parent.getLocation();

                    if (!Sname.isEmpty()){
                        fullName.setText(Sname);
                    } else {
                        fullNameLayout.setVisibility(View.GONE);
                    }

                    if (!Sgender.isEmpty()){
                        gender.setText(Sgender);
                    } else {
                        genderLayout.setVisibility(View.GONE);
                    }

                    if (!SmaritalStat.isEmpty()){
                        maritalStatus.setText(SmaritalStat);
                    } else {
                        maritalStatusLayout.setVisibility(View.GONE);
                    }

                    if (!Soccupation.isEmpty()){
                        occupation.setText(Soccupation);
                    } else {
                        occupationLayout.setVisibility(View.GONE);
                    }

                    if (!Slocation.isEmpty()){
                        location.setText(Slocation);
                    } else {
                        locationLayout.setVisibility(View.GONE);
                    }

                    Glide.with(getBaseContext())
                            .load(parent.getProfilePicURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                            .into(profilePic);

                    Glide.with(getBaseContext())
                            .load(parent.getProfilePicURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new BlurTransformation(getBaseContext(), 50))
                            .into(backgroundPic);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
