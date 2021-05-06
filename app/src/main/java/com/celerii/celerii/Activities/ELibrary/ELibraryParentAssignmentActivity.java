package com.celerii.celerii.Activities.ELibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.ELibraryMaterialsModel;
import com.celerii.celerii.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ELibraryParentAssignmentActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    ScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    TextView errorLayoutButton;

    Toolbar toolbar;
    LinearLayout imageClipper;
    TextView title, author, description;
    ImageView image;
    Button start, takeTest;

    Bundle bundle;
    String materialId, materialTitle, assignmentID, activeStudent;

    String activeStudentID = "";
    String activeStudentName;

    String featureUseKey = "";
    String featureName = "E Library Parent Assignment";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_library_parent_assignment);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Assignment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        superLayout = (ScrollView) findViewById(R.id.superlayout);
        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        errorLayoutText = (TextView) findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) findViewById(R.id.errorlayoutbutton);

        bundle = getIntent().getExtras();
        materialId = bundle.getString("materialId");
        assignmentID = bundle.getString("assignmentID");
        activeStudent = bundle.getString("activeStudent");

        if (activeStudent == null) {
            Gson gson = new Gson();
            ArrayList<Student> myChildren = new ArrayList<>();
            String myChildrenJSON = sharedPreferencesManager.getMyChildren();
            Type type = new TypeToken<ArrayList<Student>>() {}.getType();
            myChildren = gson.fromJson(myChildrenJSON, type);

            if (myChildren != null) {
                if (myChildren.size() > 0) {
                    gson = new Gson();
                    activeStudent = gson.toJson(myChildren.get(0));
                    sharedPreferencesManager.setActiveKid(activeStudent);
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    mySwipeRefreshLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        errorLayoutButton.setText("Find my child");
                        errorLayoutButton.setVisibility(View.VISIBLE);
                    } else {
                        errorLayoutText.setText("You do not have the permission to view this student's academic record");
                    }

                    return;
                }
            } else {
                mySwipeRefreshLayout.setRefreshing(false);
                superLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    errorLayoutButton.setText("Find my child");
                    errorLayoutButton.setVisibility(View.VISIBLE);
                } else {
                    errorLayoutText.setText("You do not have the permission to view this student's academic record");
                }

                return;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {}.getType();
                Student activeKidModel = gson.fromJson(activeStudent, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {}.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student: myChildren) {
                    if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                        activeKidExist = true;
                        activeKidModel = student;
                        activeStudent = gson.toJson(activeKidModel);
                        sharedPreferencesManager.setActiveKid(activeStudent);
                        break;
                    }
                }

                if (!activeKidExist) {
                    if (myChildren.size() > 0) {
                        if (myChildren.size() > 1) {
                            gson = new Gson();
                            activeStudent = gson.toJson(myChildren.get(0));
                            sharedPreferencesManager.setActiveKid(activeStudent);
                        }
                    } else {
                        mySwipeRefreshLayout.setRefreshing(false);
                        superLayout.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        mySwipeRefreshLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                            errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                            errorLayoutButton.setText("Find my child");
                            errorLayoutButton.setVisibility(View.VISIBLE);
                        } else {
                            errorLayoutText.setText("You do not have the permission to view this student's academic record");
                        }

                        return;
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeStudentModel = gson.fromJson(activeStudent, type);

        activeStudentID = activeStudentModel.getStudentID();
        activeStudentName = activeStudentModel.getFirstName() + " " + activeStudentModel.getLastName();

        imageClipper = (LinearLayout) findViewById(R.id.imageclipper);
        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);
        description = (TextView) findViewById(R.id.description);
        image = (ImageView) findViewById(R.id.image);
        start = (Button) findViewById(R.id.start);
        takeTest = (Button) findViewById(R.id.taketest);

        superLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        loadFromFirebase();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        takeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTest();
            }
        });
    }

    private void loadFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            mySwipeRefreshLayout.setRefreshing(false);
            superLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Materials").child(materialId);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ELibraryMaterialsModel eLibraryMaterialsModel = dataSnapshot.getValue(ELibraryMaterialsModel.class);

                    materialTitle = eLibraryMaterialsModel.getTitle();
                    title.setText(eLibraryMaterialsModel.getTitle());
                    author.setText(eLibraryMaterialsModel.getAuthor());
                    description.setText(eLibraryMaterialsModel.getDescription());

                    Glide.with(context)
                            .load(eLibraryMaterialsModel.getMaterialThumbnailURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .into(image);

                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("We couldn't find the resource you're looking for.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startTest() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_binary_selection_dialog_with_cancel);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button start = (Button) dialog.findViewById(R.id.optionone);
        Button cancel = (Button) dialog.findViewById(R.id.optiontwo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        message.setText("You're about to answer this assignment's questions. Ensure you've studied the material and only proceed when you've understood its content.");

        start.setText("Start Assignment");
        cancel.setText("Cancel");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ELibraryParentAssignmentActivity.this, ELibraryTakeTestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("materialTitle", materialTitle);
                bundle.putString("assignmentID", assignmentID);
                bundle.putString("activeStudent", activeStudent);
//                bundle.putString("description", descriptionString);
                intent.putExtras(bundle);
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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