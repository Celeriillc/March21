package com.celerii.celerii.Activities.ELibrary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ELibraryBooksDetailActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    LinearLayout imageClipper;
    TextView title, author, description;
    ImageView image;
    Button start, giveAsAssignment, delete;

    Bundle bundle;
    String id, titleString, authorString, typeString, descriptionString, thumbnailURL;
    String assignmentID, studentID, accountType;

    String featureUseKey = "";
    String featureName = "E Library Books Detail";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_library_books_detail);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        titleString = bundle.getString("titleString");
        authorString = bundle.getString("authorString");
        typeString = bundle.getString("typeString");
        descriptionString = bundle.getString("descriptionString");
        thumbnailURL = bundle.getString("thumbnailURL");

        imageClipper = (LinearLayout) findViewById(R.id.imageclipper);
        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);
        description = (TextView) findViewById(R.id.description);
        image = (ImageView) findViewById(R.id.image);
        start = (Button) findViewById(R.id.start);
        giveAsAssignment = (Button) findViewById(R.id.giveasassignment);
        delete = (Button) findViewById(R.id.delete);

        title.setText(titleString);
        author.setText(authorString);
        description.setText(descriptionString);

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            giveAsAssignment.setVisibility(View.GONE);
        } else {
            giveAsAssignment.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(thumbnailURL)
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .into(image);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        giveAsAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ELibraryBooksDetailActivity.this, CreateAssignmentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                bundle.putString("title", titleString);
                bundle.putString("author", authorString);
                bundle.putString("description", descriptionString);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
    protected void onResume() {
        super.onResume();
        UpdateDataFromFirebase.populateEssentials(this);
    }
}