package com.celerii.celerii.Activities.Profiles;

import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Parent;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ParentProfileActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    Toolbar toolbar;
    NestedScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    ImageView parentPic, parentPicBackground;

    TextView headerfullname, name, gender, occupation;

    String parentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile);

        Bundle bundle = getIntent().getExtras();
        parentID = bundle.getString("parentID");

        sharedPreferencesManager = new SharedPreferencesManager(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        superLayout = (NestedScrollView) findViewById(R.id.superlayout);
        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        headerfullname = (TextView) findViewById(R.id.headerfullname);
        name = (TextView) findViewById(R.id.fullname);
        gender = (TextView) findViewById(R.id.gender);
        occupation = (TextView) findViewById(R.id.occupation);

        parentPic = (ImageView) findViewById(R.id.profilepic);
        parentPicBackground = (ImageView) findViewById(R.id.backgroundimage);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
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
                    String fullName = parent.getFirstName() + " " + parent.getLastName();
                    headerfullname.setText(fullName);
                    getSupportActionBar().setTitle(fullName);
                    name.setText(fullName);
                    gender.setText(parent.getGender());
                    occupation.setText(parent.getOccupation());
                    Glide.with(getBaseContext())
                            .load(parent.getProfilePicURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                            .into(parentPic);
                    Glide.with(getBaseContext())
                            .load(parent.getProfilePicURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new BlurTransformation(getBaseContext(), 50))
                            .into(parentPicBackground);

                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.VISIBLE);
                } else {
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setRefreshing(false);
                    errorLayout.setVisibility(View.VISIBLE);
                }
             }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
