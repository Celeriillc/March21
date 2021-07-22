package com.celerii.celerii.Activities.ELibrary.Parent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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
import com.celerii.celerii.Activities.ELibrary.ELibraryBooksDetailActivity;
import com.celerii.celerii.Activities.ELibrary.ELibraryListenToAudioActivity;
import com.celerii.celerii.Activities.ELibrary.ELibraryReadPDFActivity;
import com.celerii.celerii.Activities.ELibrary.ELibraryViewImageActivity;
import com.celerii.celerii.Activities.ELibrary.ELibraryWatchVideoActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.ELibraryMaterialsModel;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.SubscriptionModel;
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
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

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
    ImageView image, icon, authorProfilePicture;
    Button start, takeTest;

    Bundle bundle;
    String materialId, materialTitle, materialAuthor, materialType, materialURL, materialThumbnailURL, materialUploader, assignmentID, activeStudent;

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
        activeStudent = bundle.getString("Child ID");

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
        icon = (ImageView) findViewById(R.id.icon);
        authorProfilePicture = (ImageView) findViewById(R.id.authorprofilepicture);
        start = (Button) findViewById(R.id.start);
        takeTest = (Button) findViewById(R.id.taketest);

        imageClipper.setClipToOutline(true);
        superLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        loadFromFirebase();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(Html.fromHtml(messageString));
                    return;
                }

                if (materialUploader.equals("Celerii")) {
                    Gson gson = new Gson();
                    Boolean isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
                    String subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationParents();
                    Type type = new TypeToken<HashMap<String, ArrayList<SubscriptionModel>>>() {}.getType();
                    HashMap<String, ArrayList<SubscriptionModel>> subscriptionModelMap = gson.fromJson(subscriptionModelJSON, type);
                    SubscriptionModel subscriptionModel = new SubscriptionModel();
                    if (subscriptionModelMap != null) {
                        ArrayList<SubscriptionModel> subscriptionModelList = subscriptionModelMap.get(activeStudentID);
                        String latestSubscriptionDate = "0000/00/00 00:00:00:000";
                        if (subscriptionModelList != null) {
                            for (SubscriptionModel subscriptionModel1 : subscriptionModelList) {
                                if (Date.compareDates(subscriptionModel1.getExpiryDate(), latestSubscriptionDate)) {
                                    subscriptionModel = subscriptionModel1;
                                    latestSubscriptionDate = subscriptionModel1.getExpiryDate();
                                }
                            }
                        } else {
                            latestSubscriptionDate = "0000/00/00 00:00:00:000";
                        }
                    }
                    Boolean isExpired = Date.compareDates(Date.getDate(), subscriptionModel.getExpiryDate());

                    if (!isOpenToAll) {
                        if (isExpired) {
                            String messageString = "This material and its assignment questions are not currently available because you do not have an active subscription, please subscribe " + "<b>" + activeStudentName + "</b>" + " to a Celerii plan to get access to this material and its assignment questions";
                            showDialogWithMessage(Html.fromHtml(messageString));
                            return;
                        }
                    }
                }

                if (materialType.equals("pdf")) {
                    Intent intent = new Intent(ELibraryParentAssignmentActivity.this, ELibraryReadPDFActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("materialID", materialId);
                    bundle.putString("materialTitle", materialTitle);
                    bundle.putString("materialURL", materialURL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (materialType.equals("video")) {
                    Intent intent = new Intent(ELibraryParentAssignmentActivity.this, ELibraryWatchVideoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("materialID", materialId);
                    bundle.putString("materialTitle", materialTitle);
                    bundle.putString("materialURL", materialURL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (materialType.equals("audio")) {
                    Intent intent = new Intent(ELibraryParentAssignmentActivity.this, ELibraryListenToAudioActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("materialID", materialId);
                    bundle.putString("materialTitle", materialTitle);
                    bundle.putString("materialAuthor", materialAuthor);
                    bundle.putString("materialURL", materialURL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (materialType.equals("image")) {
                    Intent intent = new Intent(ELibraryParentAssignmentActivity.this, ELibraryViewImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("materialID", materialId);
                    bundle.putString("materialTitle", materialTitle);
                    bundle.putString("materialURL", materialURL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        takeTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(Html.fromHtml(messageString));
                    return;
                }

                if (materialUploader.equals("Celerii")) {
                    Gson gson = new Gson();
                    Boolean isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
                    String subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationParents();
                    Type type = new TypeToken<HashMap<String, ArrayList<SubscriptionModel>>>() {}.getType();
                    HashMap<String, ArrayList<SubscriptionModel>> subscriptionModelMap = gson.fromJson(subscriptionModelJSON, type);
                    SubscriptionModel subscriptionModel = new SubscriptionModel();
                    if (subscriptionModelMap != null) {
                        ArrayList<SubscriptionModel> subscriptionModelList = subscriptionModelMap.get(activeStudentID);
                        String latestSubscriptionDate = "0000/00/00 00:00:00:000";
                        if (subscriptionModelList != null) {
                            for (SubscriptionModel subscriptionModel1 : subscriptionModelList) {
                                if (Date.compareDates(subscriptionModel1.getExpiryDate(), latestSubscriptionDate)) {
                                    subscriptionModel = subscriptionModel1;
                                    latestSubscriptionDate = subscriptionModel1.getExpiryDate();
                                }
                            }
                        } else {
                            latestSubscriptionDate = "0000/00/00 00:00:00:000";
                        }
                    }
                    Boolean isExpired = Date.compareDates(Date.getDate(), subscriptionModel.getExpiryDate());

                    if (!isOpenToAll) {
                        if (isExpired) {
                            String messageString = "This material and its assignment questions are not currently available because you do not have an active subscription, please subscribe " + "<b>" + activeStudentName + "</b>" + " to a Celerii plan to get access to this material and its assignment questions";
                            showDialogWithMessage(Html.fromHtml(messageString));
                            return;
                        }
                    }
                }
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
                    materialAuthor = eLibraryMaterialsModel.getAuthor();
                    materialType = eLibraryMaterialsModel.getType();
                    materialURL = eLibraryMaterialsModel.getMaterialURL();
                    materialThumbnailURL = eLibraryMaterialsModel.getMaterialThumbnailURL();
                    materialUploader = eLibraryMaterialsModel.getUploader();
                    title.setText(materialTitle);
                    author.setText(eLibraryMaterialsModel.getAuthor());
                    description.setText(eLibraryMaterialsModel.getDescription());
                    loadAuthorTextDrawable();
                    loadIcon();
                    setStartButtonText();

                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("We couldn't find the assignment you're looking for.");
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

    private void loadAuthorTextDrawable() {
        Drawable textDrawable;
        if (!materialAuthor.isEmpty()) {
            String[] nameArray = materialAuthor.replaceAll("\\s+", " ").trim().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 40);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 40);
            }
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 40);
        }
        authorProfilePicture.setImageDrawable(textDrawable);
    }

    private void loadIcon() {
        final int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
        if (materialThumbnailURL.trim().isEmpty()) {
            if (materialType.equals("pdf")) {
                if (randomNum % 2 == 0) {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_purple_24));
                } else {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_accent_24));
                }
            } else if (materialType.equals("video")) {
                if (randomNum % 2 == 0) {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_purple_24));
                } else {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_accent_24));
                }
            } else if (materialType.equals("audio")) {
                if (randomNum % 2 == 0) {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_purple_24));
                } else {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_accent_24));
                }
            } else {
                if (randomNum % 2 == 0) {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_purple_24));
                } else {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_accent_24));
                }
            }
        } else {
            if (materialType.equals("pdf")) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_white_24));
            } else if (materialType.equals("video")) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_white_24));
            } else if (materialType.equals("audio")) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_white_24));
            } else {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_white_24));
            }

            Glide.with(context)
                    .load(materialThumbnailURL)
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .into(image);
        }
    }

    private void setStartButtonText() {
        if (materialType.equals("pdf")) {
            start.setText("Open E-book");
        } else if (materialType.equals("video")) {
            start.setText("Play Video");
        } else if (materialType.equals("audio")) {
            start.setText("Play Audiobook");
        } else if (materialType.equals("image")) {
            start.setText("Open Image");
        }
    }

    void showDialogWithMessage (Spanned messageString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
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