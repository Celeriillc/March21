package com.celerii.celerii.Activities.EditProfiles;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.Activities.Delete.AvailableForHomeLessonsEditActivity;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.EmailEditActivity;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.GenderEditActivity;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.GeneralEditActivity;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.MaritalStatusEditActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.SendPictureForEditProfileActivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Teacher;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class EditTeacherProfileActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    final Context context = this;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;
    ScrollView superLayout;

    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    public  static final int RequestPermissionCode  = 1;

    Toolbar toolbar;
    LinearLayout firstNameLayout, lastNameLayout, middleNameLayout, genderLayout, emailLayout, phoneNumberLayout, maritalStatusLayout, availableForHomeLessonsLayout, locationLayout, stateLayout;
    TextView firstName, middleName, lastName, headerFullName, gender, email, phoneNumber, maritalStatus, availableForHomeLessons, location, state, composeBio;
    ImageView profilePicture, profilePicturePrimary, changeProfilePicture;

    HashMap<String, Object> teacherProfileUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher_profile);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);
        superLayout = (ScrollView) findViewById(R.id.superlayout);

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        profilePicture = (ImageView) findViewById(R.id.profilepicture);
        profilePicturePrimary = (ImageView) findViewById(R.id.profilepictureprimary);
        changeProfilePicture = (ImageView) findViewById(R.id.changeprofilepicture);
        firstNameLayout = (LinearLayout) findViewById(R.id.firstnamelayout);
        lastNameLayout = (LinearLayout) findViewById(R.id.lastnamelayout);
        middleNameLayout = (LinearLayout) findViewById(R.id.middlenamelayout);
        genderLayout = (LinearLayout) findViewById(R.id.genderlayout);
        emailLayout = (LinearLayout) findViewById(R.id.emaillayout);
        phoneNumberLayout = (LinearLayout) findViewById(R.id.phonenumberlayout);
        maritalStatusLayout = (LinearLayout) findViewById(R.id.maritalstatuslayout);
        availableForHomeLessonsLayout = (LinearLayout) findViewById(R.id.availableforhomelessonslayout);
        locationLayout = (LinearLayout) findViewById(R.id.locationlayout);
        stateLayout = (LinearLayout) findViewById(R.id.statelayout);

        firstName = (TextView) findViewById(R.id.firstname);
        middleName = (TextView) findViewById(R.id.middlename);
        lastName = (TextView) findViewById(R.id.lastname);
        headerFullName = (TextView) findViewById(R.id.headerfullname);
        gender = (TextView) findViewById(R.id.gender);
        email = (TextView) findViewById(R.id.email);
        phoneNumber = (TextView) findViewById(R.id.phonenumber);
        maritalStatus = (TextView) findViewById(R.id.maritalstatus);
        availableForHomeLessons = (TextView) findViewById(R.id.availableforhomelessons);
        location = (TextView) findViewById(R.id.location);
        state = (TextView) findViewById(R.id.state);
        composeBio = (TextView) findViewById(R.id.composebio);

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
                        loadProfileInformation();
                    }
                }
        );

        loadProfileInformation();

        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog_layout_select_image_from_gallery_camera_two);
                LinearLayout camera = (LinearLayout) dialog.findViewById(R.id.camera);
                LinearLayout gallery = (LinearLayout) dialog.findViewById(R.id.gallery);
                TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
                dialog.show();

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClickImageFromCamera();
                        dialog.dismiss();
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetImageFromGallery();
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        firstNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, GeneralEditActivity.class);
                Bundle b = new Bundle();
                b.putString("Caption", "First Name");
                b.putString("Description", "First Name Description");
                b.putString("EditHint", "First Name");
                b.putString("EditItem", firstName.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 1);
            }
        });

        middleNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, GeneralEditActivity.class);
                Bundle b = new Bundle();
                b.putString("Caption", "Middle Name");
                b.putString("Description", "Middle Name Description");
                b.putString("EditHint", "Middle Name");
                String mid = middleName.getText().toString();
                b.putString("EditItem", middleName.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 2);
            }
        });

        lastNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, GeneralEditActivity.class);
                Bundle b = new Bundle();
                b.putString("Caption", "Last Name");
                b.putString("Description", "Last Name Description");
                b.putString("EditHint", "Last Name");
                b.putString("EditItem", lastName.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 3);
            }
        });

        genderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, GenderEditActivity.class);
                Bundle b = new Bundle();
                b.putString("gender", gender.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 4);
            }
        });

        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, EmailEditActivity.class);
                startActivityForResult(i, 5);
            }
        });

        phoneNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, GeneralEditActivity.class);
                Bundle b = new Bundle();
                b.putString("Caption", "Phone Number");
                b.putString("Description", "Phone Number Description");
                b.putString("EditHint", "Phone Number");
                b.putString("EditItem", phoneNumber.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 6);
            }
        });

        maritalStatusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, MaritalStatusEditActivity.class);
                Bundle b = new Bundle();
                b.putString("maritalstatus", maritalStatus.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 7);
            }
        });

        availableForHomeLessonsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, AvailableForHomeLessonsEditActivity.class);
                Bundle b = new Bundle();
                b.putString("availableforhomelessons", availableForHomeLessons.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 8);
            }
        });

        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, GeneralEditActivity.class);
                Bundle b = new Bundle();
                b.putString("Caption", "Location");
                b.putString("Description", "Location Description");
                b.putString("EditHint", "Location");
                b.putString("EditItem", location.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 9);
            }
        });

        stateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditTeacherProfileActivity.this, GeneralEditActivity.class);
                Bundle b = new Bundle();
                b.putString("Caption", "State");
                b.putString("Description", "State Description");
                b.putString("EditHint", "State");
                b.putString("EditItem", state.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 10);
            }
        });

    }

    private void loadProfileInformation() {
        mDatabaseReference = mFirebaseDatabase.getReference("Teacher/" + auth.getCurrentUser().getUid());
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
                    superLayout.setVisibility(View.VISIBLE);
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    firstName.setText(teacher.getFirstName());
                    middleName.setText(teacher.getMiddleName());
                    lastName.setText(teacher.getLastName());
                    headerFullName.setText(teacher.getFirstName() + " " + teacher.getLastName());
                    gender.setText(teacher.getGender());
                    email.setText(auth.getCurrentUser().getEmail());
                    phoneNumber.setText(teacher.getPhone());
                    maritalStatus.setText(teacher.getMaritalStatus());
                    availableForHomeLessons.setText(teacher.getAvailableForHomeLessons());
                    location.setText(teacher.getLocation());
                    state.setText(teacher.getState());

                    Glide.with(getBaseContext())
                            .load(teacher.getProfilePicURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                            .into(profilePicturePrimary);
                    Glide.with(getBaseContext())
                            .load(teacher.getProfilePicURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new BlurTransformation(getBaseContext(), 50))
                            .into(profilePicture);

                    mDatabaseReference = mFirebaseDatabase.getReference("Teacher Bio/" + auth.getCurrentUser().getUid());
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                String bio = dataSnapshot.getValue(String.class);
                                composeBio.setText(bio);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    composeBio.setText(teacher.getBio());
                }
                else{
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                firstName.setText(data.getStringExtra("Caption"));
                headerFullName.setText(firstName.getText() + " " + lastName.getText());
            }
        }
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                middleName.setText(data.getStringExtra("Caption"));
            }
        }
        if (requestCode == 3) {
            if(resultCode == RESULT_OK) {
                lastName.setText(data.getStringExtra("Caption"));
                headerFullName.setText(firstName.getText() + " " + lastName.getText());
            }
        }
        if (requestCode == 4) {
            if(resultCode == RESULT_OK) {
                gender.setText(data.getStringExtra("selectedgender"));
            }
        }
        if (requestCode == 5) {
            if(resultCode == RESULT_OK) {
                email.setText(data.getStringExtra("newEmail"));
            }
        }
        if (requestCode == 6) {
            if(resultCode == RESULT_OK) {
                phoneNumber.setText(data.getStringExtra("Caption"));
            }
        }
        if (requestCode == 7) {
            if(resultCode == RESULT_OK) {
                maritalStatus.setText(data.getStringExtra("selectedmaritalstatus"));
            }
        }
        if (requestCode == 8) {
            if(resultCode == RESULT_OK) {
                availableForHomeLessons.setText(data.getStringExtra("selectedavailableforhomelessons"));
            }
        }
        if (requestCode == 9) {
            if(resultCode == RESULT_OK) {
                location.setText(data.getStringExtra("Caption"));
            }
        }
        if (requestCode == 10) {
            if(resultCode == RESULT_OK) {
                state.setText(data.getStringExtra("Caption"));
            }
        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            ImageCropFunction();
        }
        if (requestCode == 101) {
            if (data != null) {
                uri = data.getData();
                ImageCropFunction();
            }
        }
        if (requestCode == 102) {
            if (data != null) {
                try {
                    Bundle bundle = data.getExtras();
                    bundle.putString("URI", uri.toString());
                    bundle.putString("AccountType", "Teacher");
                    bundle.putString("AccountID", sharedPreferencesManager.getMyUserID());
                    Intent i = new Intent(EditTeacherProfileActivity.this, SendPictureForEditProfileActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                } catch (Exception e){
                    return;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_dialog_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_save){
            mDatabaseReference = mFirebaseDatabase.getReference();
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        teacherProfileUpdate = new HashMap<String, Object>();
                        if (!firstName.getText().toString().isEmpty()){
                            teacherProfileUpdate.put("Teacher/" + auth.getCurrentUser().getUid() + "/firstName", firstName.getText().toString().trim());
                            teacherProfileUpdate.put("Parent/" + auth.getCurrentUser().getUid() + "/firstName", firstName.getText().toString().trim());
                            sharedPreferencesManager.setMyFirstName(firstName.getText().toString());
                        }else {
                            return;
                        }
                        if (!lastName.getText().toString().isEmpty()){
                            teacherProfileUpdate.put("Teacher/" + auth.getCurrentUser().getUid() + "/lastName", lastName.getText().toString().trim());
                            teacherProfileUpdate.put("Parent/" + auth.getCurrentUser().getUid() + "/lastName", lastName.getText().toString().trim());
                            sharedPreferencesManager.setMyLastName(lastName.getText().toString());
                        } else {
                            return;
                        }
                        if (!email.getText().toString().isEmpty()){
                            mFirebaseUser.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                    }
                                }
                            });
                        }else {
                            return;
                        }
                        teacherProfileUpdate.put("Teacher/" + auth.getCurrentUser().getUid() + "/middleName", middleName.getText().toString().trim());
                        teacherProfileUpdate.put("Parent/" + auth.getCurrentUser().getUid() + "/middleName", middleName.getText().toString().trim());
                        teacherProfileUpdate.put("Teacher/" + auth.getCurrentUser().getUid() + "/gender", gender.getText().toString().trim());
                        teacherProfileUpdate.put("Parent/" + auth.getCurrentUser().getUid() + "/gender", gender.getText().toString().trim());
                        teacherProfileUpdate.put("Teacher/" + auth.getCurrentUser().getUid() + "/phone", phoneNumber.getText().toString().trim());
                        teacherProfileUpdate.put("Parent/" + auth.getCurrentUser().getUid() + "/phone", phoneNumber.getText().toString().trim());
                        teacherProfileUpdate.put("Teacher/" + auth.getCurrentUser().getUid() + "/maritalStatus", maritalStatus.getText().toString().trim());
                        teacherProfileUpdate.put("Teacher/" + auth.getCurrentUser().getUid() + "/availableForHomeLessons", availableForHomeLessons.getText().toString().trim());
                        teacherProfileUpdate.put("Teacher/" + auth.getCurrentUser().getUid() + "/location", location.getText().toString().trim());
                        teacherProfileUpdate.put("Teacher/" + auth.getCurrentUser().getUid() + "/state", state.getText().toString().trim());
                        teacherProfileUpdate.put("Teacher Bio/" + auth.getCurrentUser().getUid(), composeBio.getText().toString().trim());

                        //TODO how about updating compose bio

                        mDatabaseReference.updateChildren(teacherProfileUpdate, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void ImageCropFunction() {
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 180);
            CropIntent.putExtra("outputY", 180);
            CropIntent.putExtra("aspectX", 8);
            CropIntent.putExtra("aspectY", 8);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, 102);

        } catch (ActivityNotFoundException e) {
            return;
        }
    }

    private void GetImageFromGallery() {
        GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 101);
    }

    private void ClickImageFromCamera() {
        CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory(),
                "CeleriiImage" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);
        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        CamIntent.putExtra("return-data", true);
        startActivityForResult(CamIntent, 100);
    }
}
