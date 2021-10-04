package com.celerii.celerii.Activities.EditProfiles;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditPersonalInformationDetails.GenderEditActivity;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.GeneralEditActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.SendPictureForEditProfileActivity;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.Student;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class EditStudentProfileActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    Context context;
    Bundle bundle;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;

    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    public static final int REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE  = 1000;
    public static final int REQUESTPPERMISSIONCODECAMERA  = 1001;
    String downloadURL;
    Bitmap bitmap;
    byte[] byteArray;
    CustomProgressDialogOne progressDialog;

    ScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;
    Button errorLayoutButton;
    LinearLayout newProfilePictureLayout;

    Toolbar toolbar;
    LinearLayout genderLayout;
    EditText firstName, middleName, lastName;
    TextView gender, composeBio, composeBioDescription, writeSomething;
    ImageView profilePicturePrimary, changeProfilePicture;

    HashMap<String, Object> studentProfileUpdate;

    String childID = "";
    String childFirstName = "";

    Handler internetConnectionHandler = new Handler();
    Runnable internetConnectionRunnable;

    String featureUseKey = "";
    String featureName = "Edit Student Profile";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student_profile);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        progressDialog = new CustomProgressDialogOne(context);
        byteArray = null;

        bundle = getIntent().getExtras();
        childID = bundle.getString("StudentID");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile"); //TODO: make dynamic
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        superLayout = (ScrollView) findViewById(R.id.superlayout);

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        profilePicturePrimary = (ImageView) findViewById(R.id.profilepictureprimary);
        newProfilePictureLayout = (LinearLayout) findViewById(R.id.newprofilepicturelayout);
        newProfilePictureLayout.setClipToOutline(true);
        changeProfilePicture = (ImageView) findViewById(R.id.changeprofilepicture);
        genderLayout = (LinearLayout) findViewById(R.id.genderlayout);

        firstName = (EditText) findViewById(R.id.firstname);
        middleName = (EditText) findViewById(R.id.middlename);
        lastName = (EditText) findViewById(R.id.lastname);
        gender = (TextView) findViewById(R.id.gender);
        composeBio = (TextView) findViewById(R.id.composebio);
        writeSomething = (TextView) findViewById(R.id.writesomething);
        composeBioDescription = (TextView) findViewById(R.id.composebiodescrption);

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
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                try {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                } catch (Exception e) {
                    return;
                }

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

        genderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditStudentProfileActivity.this, GenderEditActivity.class);
                Bundle b = new Bundle();
                b.putString("gender", gender.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 1);
            }
        });
    }

    private void loadProfileInformation(){
//        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
//            mySwipeRefreshLayout.setRefreshing(false);
//            superLayout.setVisibility(View.GONE);
//            progressLayout.setVisibility(View.GONE);
//            errorLayout.setVisibility(View.VISIBLE);
//            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
//            return;
//        }
        internetConnectionRunnable = new Runnable() {
            @Override
            public void run() {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(getString(R.string.no_internet_message_for_offline_download));
                }
            }
        };
        internetConnectionHandler.postDelayed(internetConnectionRunnable, 7000);

        mDatabaseReference = mFirebaseDatabase.getReference("Student").child(childID);
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Student student = dataSnapshot.getValue(Student.class);
                    String childName = student.getFirstName() + " " + student.getLastName();
                    childFirstName = student.getFirstName();
                    firstName.setText(childFirstName);
                    getSupportActionBar().setTitle("Edit " + childFirstName + "'s Profile");
                    lastName.setText(student.getLastName());
                    middleName.setText(student.getMiddleName());
                    gender.setText(student.getGender());
                    writeSomething.setText("Write something about " + childFirstName);
                    composeBioDescription.setText("We know there's something special and amazing you'll want present and future teachers to know about " + childFirstName + ", let the teachers know here");
                    composeBio.setText(student.getBio());

                    Drawable textDrawable;
                    if (!childName.isEmpty()) {
                        String[] nameArray = childName.replaceAll("\\s+", " ").trim().split(" ");
                        if (nameArray.length == 1) {
                            textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], 150);
                        } else {
                            textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], nameArray[1], 150);
                        }
                        profilePicturePrimary.setImageDrawable(textDrawable);
                    } else {
                        textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 150);
                    }

                    if (!student.getImageURL().equals("")) {
                        Glide.with(getBaseContext())
                                .load(student.getImageURL())
                                .placeholder(textDrawable)
                                .error(textDrawable)
                                .centerCrop()
                                .into(profilePicturePrimary);
                    }

                    internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
                    superLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                }
                else {
                    internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                    mySwipeRefreshLayout.setRefreshing(false);
                    superLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("This student account has been deleted");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                internetConnectionHandler.removeCallbacks(internetConnectionRunnable);
                mySwipeRefreshLayout.setRefreshing(false);
                superLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(message);
                return;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                gender.setText(data.getStringExtra("selectedgender"));
            }
        }
        if (requestCode == 10 && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    bitmap = data.getExtras().getParcelable("data");
                    profilePicturePrimary.setImageDrawable(null);
                    profilePicturePrimary.setImageBitmap(bitmap);
                } catch (Exception e){
                    //tODO:
                }
            }
        }
        if (requestCode == 11) {
            if (data != null) {
                uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    profilePicturePrimary.setImageDrawable(null);
                    profilePicturePrimary.setImageBitmap(bitmap);
                } catch (Exception e) {
                    //tODO:
                }
            }
        }
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
            final String firstNameString = firstName.getText().toString().trim();
            final String lastNameString = lastName.getText().toString().trim();

            if (!validateName(firstNameString, firstName))
                return false;

            if (!validateName(lastNameString, lastName))
                return false;

            progressDialog.show();

            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }

            if (byteArray == null) {
                studentProfileUpdate = new HashMap<String, Object>();
                studentProfileUpdate.put("Student/" + childID + "/firstName", firstNameString);
                studentProfileUpdate.put("Student/" + childID + "/lastName", lastNameString);
                studentProfileUpdate.put("Student/" + childID + "/middleName", middleName.getText().toString().trim());
                studentProfileUpdate.put("Student/" + childID + "/searchableFirstName", firstNameString.toLowerCase());
                studentProfileUpdate.put("Student/" + childID + "/searchableLastName", lastNameString.toLowerCase());
                studentProfileUpdate.put("Student/" + childID + "/searchableMiddleName", middleName.getText().toString().trim().toLowerCase());
                studentProfileUpdate.put("Student/" + childID + "/gender", gender.getText().toString().trim());
                studentProfileUpdate.put("Student/" + childID + "/bio", composeBio.getText().toString().trim());

                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(studentProfileUpdate, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        progressDialog.dismiss();
                        if (databaseError == null) {
                            if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                                showDialogWithMessage(Html.fromHtml("<b>" + firstNameString + "</b>" + "'s profile has been successfully updated. " + R.string.offline_write_message));
                            } else {
                                showDialogWithMessage(Html.fromHtml("<b>" + firstNameString + "</b>" + "'s profile has been successfully updated"));
                            }
                            UpdateDataFromFirebase.populateEssentials(context);
                        } else {
                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                            ShowDialogWithMessage.showDialogWithMessage(context, message);
                        }
                    }
                });
            } else {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(Html.fromHtml(messageString));
                    return false;
                }

                mStorageReference = mFirebaseStorage.getReference().child("CeleriiProfilePicture/" + childID + "/profilepicture");
                UploadTask uploadTask = mStorageReference.putBytes(byteArray);
                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            return null;
                        }

                        return mStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final String downloadURL = task.getResult().toString();

                            studentProfileUpdate = new HashMap<String, Object>();
                            studentProfileUpdate.put("Student/" + childID + "/firstName", firstNameString);
                            studentProfileUpdate.put("Student/" + childID + "/lastName", lastNameString);
                            studentProfileUpdate.put("Student/" + childID + "/middleName", middleName.getText().toString().trim());
                            studentProfileUpdate.put("Student/" + childID + "/searchableFirstName", firstNameString.toLowerCase());
                            studentProfileUpdate.put("Student/" + childID + "/searchableLastName", lastNameString.toLowerCase());
                            studentProfileUpdate.put("Student/" + childID + "/searchableMiddleName", middleName.getText().toString().trim().toLowerCase());
                            studentProfileUpdate.put("Student/" + childID + "/gender", gender.getText().toString().trim());
                            studentProfileUpdate.put("Student/" + childID + "/bio", composeBio.getText().toString().trim());
                            studentProfileUpdate.put("Student/" + childID + "/imageURL", downloadURL);

                            mDatabaseReference = mFirebaseDatabase.getReference();
                            mDatabaseReference.updateChildren(studentProfileUpdate, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    progressDialog.dismiss();
                                    if (databaseError == null) {
                                        UpdateDataFromFirebase.populateEssentials(context);
                                        String messageString = "<b>" + firstNameString + "</b>" + "'s profile has been successfully updated";
                                        showDialogWithMessage(Html.fromHtml(messageString));
                                    } else {
                                        String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                        showDialogWithMessage(Html.fromHtml(message));
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            String messageString = "We couldn't update " + "<b>" + firstNameString + "</b>" + "'s profile at this time, please try again later";
                            showDialogWithMessage(Html.fromHtml(messageString));
                        }
                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateName(String nameString, EditText name) {
        if (nameString.isEmpty()) {
            String messageString = "You need to enter a name in both name fields";
            showDialogWithMessage(Html.fromHtml(messageString));
            name.requestFocus();
            return false;
        }

        String[] nameArray = nameString.split(" ");
        if (nameArray.length > 1) {
            String messageString = "You should enter only one name in this field. If you have a double name, you can separate them with a hyphen (-). E.g. Ava-Grace.";
            showDialogWithMessage(Html.fromHtml(messageString));
            name.requestFocus();
            name.setSelectAllOnFocus(true);
            return false;
        }

        return true;
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

    void showDialogWithMessageWithClose (Spanned messageString) {
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
                finish();
            }
        });
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
        if (ContextCompat.checkSelfPermission(EditStudentProfileActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditStudentProfileActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);

        } else {
            // Permission has already been granted
            GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(GalIntent, "Select Picture From Gallery"), 11);
        }
    }

    private void ClickImageFromCamera() {
        if (ContextCompat.checkSelfPermission(EditStudentProfileActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditStudentProfileActivity.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUESTPPERMISSIONCODECAMERA);
        } else {
            CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//            File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/ProfilePicture");
//
//            if(!directory.exists() && !directory.isDirectory()) {
//                if (directory.mkdirs()) {
//                    file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                } else {
//                    file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                }
//            } else {
//                file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//            }
//            uri = Uri.fromFile(file);
//            CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
//            CamIntent.putExtra("return-data", true);
            startActivityForResult(CamIntent, 10);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUESTPPERMISSIONCODECAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/ProfilePicture");
//
//                    if(!directory.exists() && !directory.isDirectory()) {
//                        if (directory.mkdirs()) {
//                            file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                        } else {
//                            file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                        }
//                    } else {
//                        file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                    }

//                    uri = Uri.fromFile(file);
//                    CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
//                    CamIntent.putExtra("return-data", true);
                    startActivityForResult(CamIntent, 10);
                } else {

                }
                return;
            }
            case REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(GalIntent, "Select Picture From Gallery"), 11);
                } else {

                }
                return;
            }
        }
    }
}
