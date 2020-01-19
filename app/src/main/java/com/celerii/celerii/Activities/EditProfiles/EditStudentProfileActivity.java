package com.celerii.celerii.Activities.EditProfiles;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
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

import com.celerii.celerii.Activities.EditPersonalInformationDetails.GenderEditActivity;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.GeneralEditActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.SendPictureForEditProfileActivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Student;
import com.bumptech.glide.Glide;
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

public class EditStudentProfileActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    final Context context = this;
    Bundle bundle;

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
    LinearLayout refNumberLayout, firstNameLayout, lastNameLayout, middleNameLayout, genderLayout;
    TextView refNumber, firstName, middleName, lastName, headerFullName, gender, composeBio, composeBioDescription;
    ImageView profilePicture, profilePicturePrimary, changeProfilePicture;

    HashMap<String, Object> studentProfileUpdate;

    String childID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_profile);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        bundle = getIntent().getExtras();
        childID = bundle.getString("StudentID");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile"); //TODO: make dynamic
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
        refNumberLayout = (LinearLayout) findViewById(R.id.refnumberlayout);
        firstNameLayout = (LinearLayout) findViewById(R.id.firstnamelayout);
        middleNameLayout = (LinearLayout) findViewById(R.id.middlenamelayout);
        lastNameLayout = (LinearLayout) findViewById(R.id.lastnamelayout);
        genderLayout = (LinearLayout) findViewById(R.id.genderlayout);

        refNumber = (TextView) findViewById(R.id.refnumber);
        firstName = (TextView) findViewById(R.id.firstname);
        middleName = (TextView) findViewById(R.id.middlename);
        headerFullName = (TextView) findViewById(R.id.headerfullname);
        lastName = (TextView) findViewById(R.id.lastname);
        gender = (TextView) findViewById(R.id.gender);
        composeBio = (TextView) findViewById(R.id.composebio);
        composeBioDescription = (TextView) findViewById(R.id.composebiodescrption);

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
                Intent i = new Intent(EditStudentProfileActivity.this, GeneralEditActivity.class);
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
                Intent i = new Intent(EditStudentProfileActivity.this, GeneralEditActivity.class);
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
                Intent i = new Intent(EditStudentProfileActivity.this, GeneralEditActivity.class);
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
                Intent i = new Intent(EditStudentProfileActivity.this, GenderEditActivity.class);
                Bundle b = new Bundle();
                b.putString("gender", gender.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 4);
            }
        });
    }

    private void loadProfileInformation(){
        mDatabaseReference = mFirebaseDatabase.getReference("Student/" + childID);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
                    superLayout.setVisibility(View.VISIBLE);
                    Student student = dataSnapshot.getValue(Student.class);
                    refNumber.setText(childID);
                    firstName.setText(student.getFirstName());
                    getSupportActionBar().setTitle("Edit " + student.getFirstName() + "'s Profile");
                    lastName.setText(student.getLastName());
                    middleName.setText(student.getMiddleName());
                    headerFullName.setText(student.getFirstName() + " " + student.getLastName());
                    gender.setText(student.getGender());
                    composeBioDescription.setText("We know there's something special and amazing you'll want present and future teachers to know about " + student.getFirstName() + ", let the teachers know here");

                    Glide.with(getBaseContext())
                            .load(student.getImageURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                            .into(profilePicturePrimary);
                    Glide.with(getBaseContext())
                            .load(student.getImageURL())
                            .placeholder(R.drawable.profileimageplaceholder)
                            .error(R.drawable.profileimageplaceholder)
                            .centerCrop()
                            .bitmapTransform(new BlurTransformation(getBaseContext(), 50))
                            .into(profilePicture);

                    mDatabaseReference = mFirebaseDatabase.getReference("Student Bio/" + childID);
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
                }
                else {
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
                    bundle.putString("AccountType", "Student");
                    bundle.putString("AccountID", childID);
                    Intent i = new Intent(EditStudentProfileActivity.this, SendPictureForEditProfileActivity.class);
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
                        studentProfileUpdate = new HashMap<String, Object>();
                        if (!firstName.getText().toString().isEmpty()){
                            studentProfileUpdate.put("Student/" + childID + "/firstName", firstName.getText().toString().trim());
                            //TODO Consider shared preferences
                        } else {
                            return;
                        }

                        if (!lastName.getText().toString().isEmpty()){
                            studentProfileUpdate.put("Student/" + childID + "/lastName", lastName.getText().toString().trim());
                        } else {
                            return;
                        }

                        studentProfileUpdate.put("Student/" + childID + "/middleName", middleName.getText().toString().trim());
                        studentProfileUpdate.put("Student/" + childID + "/gender", gender.getText().toString().trim());
                        studentProfileUpdate.put("Student Bio/" + childID, composeBio.getText().toString().trim());

                        mDatabaseReference.updateChildren(studentProfileUpdate, new DatabaseReference.CompletionListener() {
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
