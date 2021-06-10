package com.celerii.celerii.Activities.ELibrary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.ELibrary.Teacher.CreateAssignmentActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.ELibraryMaterialsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
    ImageView image, icon, authorProfilePicture;
    Button start, giveAsAssignment, delete;

    Bundle bundle;
    String id, titleString, authorString, typeString, descriptionString, thumbnailURL, materialURL, materialUploader;
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
        materialURL = bundle.getString("materialURL");
        materialUploader = bundle.getString("materialUploader");

        imageClipper = (LinearLayout) findViewById(R.id.imageclipper);
        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);
        description = (TextView) findViewById(R.id.description);
        image = (ImageView) findViewById(R.id.image);
        icon = (ImageView) findViewById(R.id.icon);
        authorProfilePicture = (ImageView) findViewById(R.id.authorprofilepicture);
        start = (Button) findViewById(R.id.start);
        giveAsAssignment = (Button) findViewById(R.id.giveasassignment);
        delete = (Button) findViewById(R.id.delete);

        imageClipper.setClipToOutline(true);
        title.setText(titleString);
        author.setText(authorString);
        description.setText(descriptionString);
        loadAuthorTextDrawable();
        loadIcon();

        if (typeString.equals("pdf")) {
            start.setText("Open E-book");
        } else if (typeString.equals("video")) {
            start.setText("Play Video");
        } else if (typeString.equals("audio")) {
            start.setText("Play Audiobook");
        } else if (typeString.equals("image")) {
            start.setText("Open Image");
        }

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            giveAsAssignment.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        } else {
            giveAsAssignment.setVisibility(View.VISIBLE);

            if (materialUploader.equals(mFirebaseUser.getUid())) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeString.equals("pdf")) {
                    Intent intent = new Intent(ELibraryBooksDetailActivity.this, ELibraryReadPDFActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("materialID", id);
                    bundle.putString("materialTitle", titleString);
                    bundle.putString("materialURL", materialURL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (typeString.equals("video")) {
                    Intent intent = new Intent(ELibraryBooksDetailActivity.this, ELibraryWatchVideoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("materialID", id);
                    bundle.putString("materialTitle", titleString);
                    bundle.putString("materialURL", materialURL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (typeString.equals("audio")) {
                    Intent intent = new Intent(ELibraryBooksDetailActivity.this, ELibraryListenToAudioActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("materialID", id);
                    bundle.putString("materialTitle", titleString);
                    bundle.putString("materialAuthor", authorString);
                    bundle.putString("materialURL", materialURL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (typeString.equals("image")) {
                    Intent intent = new Intent(ELibraryBooksDetailActivity.this, ELibraryViewImageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("materialID", id);
                    bundle.putString("materialTitle", titleString);
                    bundle.putString("materialURL", materialURL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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
                deleteBook();
            }
        });
    }

    private void loadAuthorTextDrawable() {
        Drawable textDrawable;
        if (!authorString.isEmpty()) {
            String[] nameArray = authorString.replaceAll("\\s+", " ").trim().split(" ");
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
        if (thumbnailURL.trim().isEmpty()) {
            if (typeString.equals("pdf")) {
                if (randomNum % 2 == 0) {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_purple_24));
                } else {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_accent_24));
                }
            } else if (typeString.equals("video")) {
                if (randomNum % 2 == 0) {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_purple));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_purple_24));
                } else {
                    image.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_background_for_options_light_accent));
                    icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_accent_24));
                }
            } else if (typeString.equals("audio")) {
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
            if (typeString.equals("pdf")) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_white_24));
            } else if (typeString.equals("video")) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_video_white_24));
            } else if (typeString.equals("audio")) {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audio_headphones_white_24));
            } else {
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_white_24));
            }

            Glide.with(context)
                    .load(thumbnailURL)
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .into(image);
        }
    }

    private void deleteBook() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_binary_selection_dialog_with_cancel);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button delete = (Button) dialog.findViewById(R.id.optionone);
        Button cancel = (Button) dialog.findViewById(R.id.optiontwo);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        message.setText(Html.fromHtml("Do you wish to delete this resource? This process can not be undone."));

        delete.setText("Delete");
        cancel.setText("Cancel");

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    ShowDialogWithMessage.showDialogWithMessage(context, Html.fromHtml(messageString));
                    return;
                }

                CustomProgressDialogOne progressDialogOne = new CustomProgressDialogOne(context);
                progressDialogOne.show();

                mDatabaseReference = mFirebaseDatabase.getReference().child("E Library Materials").child(id);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ELibraryMaterialsModel eLibraryMaterialsModel = dataSnapshot.getValue(ELibraryMaterialsModel.class);
                            String storageURl = eLibraryMaterialsModel.getMaterialURL();
                            ArrayList<String> teacherList = eLibraryMaterialsModel.getTeacherList();
                            ArrayList<String> schoolList = eLibraryMaterialsModel.getSchoolList();

                            HashMap<String, Object> deleteMap = new HashMap<>();

                            for (String teacher : teacherList) {
                                deleteMap.put("E Library Private Materials/Teacher/" + teacher + "/" + id, null);
                            }

                            for (String school : schoolList) {
                                deleteMap.put("E Library Private Materials/School/" + school + "/" + id, null);
                            }
                            deleteMap.put("E Library Materials/" + id, null);

                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storageURl);
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    mDatabaseReference = mFirebaseDatabase.getReference();
                                    mDatabaseReference.updateChildren(deleteMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference ref) {
                                            if (databaseError == null) {
                                                progressDialogOne.dismiss();
                                                final Dialog dialog = new Dialog(context);
                                                dialog.setContentView(R.layout.custom_upload_successful_dialog);
                                                dialog.setCancelable(false);
                                                dialog.setCanceledOnTouchOutside(false);
                                                TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialogmessage);
                                                TextView close = (TextView) dialog.findViewById(R.id.close);
                                                dialog.show();

                                                dialogMessage.setText("File deleted successfully!");

                                                close.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                });
                                            } else {
                                                progressDialogOne.dismiss();
                                                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                                ShowDialogWithMessage.showDialogWithMessage(context, message);
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String messageString = "We couldn't delete this resource, please try again later.";
                                    ShowDialogWithMessage.showDialogWithMessage(context, Html.fromHtml(messageString));
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        } else {
                            String messageString = "We couldn't find this resource.";
                            ShowDialogWithMessage.showDialogWithMessage(context, Html.fromHtml(messageString));
                            dialog.dismiss();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


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