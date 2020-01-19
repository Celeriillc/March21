package com.celerii.celerii.Activities.LoginAndSignup;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class SignUpActivityFive extends AppCompatActivity {

    Context context = this;

    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;

    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    public static final int REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE  = 1000;
    public static final int REQUESTPPERMISSIONCODECAMERA  = 1001;
    String downloadURL;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    FirebaseUser mFirebaseUser;

    private Toolbar mToolbar;
    ImageView newProfilePicture;
    LinearLayout newProfilePictureLayout;
    Button begin;
    EditText gender;
    ImageView tapToUpload;

    private String accountType, UID, genderSelected;
    CustomProgressDialogOne progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_five);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);
        progressDialog = new CustomProgressDialogOne(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        accountType = sharedPreferencesManager.getActiveAccount();
        UID = sharedPreferencesManager.getMyUserID();

        mToolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        newProfilePicture = (ImageView) findViewById(R.id.newprofilepicture);
        newProfilePictureLayout = (LinearLayout) findViewById(R.id.newprofilepicturelayout);
        newProfilePictureLayout.setClipToOutline(true);
        begin = (Button) findViewById(R.id.begin);
        gender = (EditText) findViewById(R.id.gender);
        gender.setText("Female");
        tapToUpload = (ImageView) findViewById(R.id.taptoupload);

        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_binary_selection_dialog);
                TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
                TextView female = (TextView) dialog.findViewById(R.id.optionone);
                TextView male = (TextView) dialog.findViewById(R.id.optiontwo);
                dialog.show();

                message.setText("Please select your gender. This will help us build a more custom experience for you.");

                female.setText("Female");
                male.setText("Male");

                female.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender.setText("Female");
                        dialog.dismiss();
                    }
                });

                male.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gender.setText("Male");
                        dialog.dismiss();
                    }
                });
            }
        });

        tapToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    showDialogWithMessage(Html.fromHtml(messageString));
                    return;
                }

                genderSelected = gender.getText().toString();
                registerGenderAndProceed();
            }
        });
    }

    void showDialogWithMessage (Spanned messageString) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        TextView OK = (TextView) dialog.findViewById(R.id.optionone);
        dialog.show();

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    void registerGenderAndProceed() {
        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(SignUpActivityFive.this);
        progressDialog.show();
        mDatabaseReference = mFirebaseDatabase.getReference();
        Map<String, Object> updater = new HashMap<String, Object>();
        updater.put("Parent/" + UID + "/gender", genderSelected);
        updater.put("Teacher/" + UID + "/gender", genderSelected);
        mDatabaseReference.updateChildren(updater, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                applicationLauncherSharedPreferences.setLauncherActivity("Home");
                if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                    Intent I = new Intent(SignUpActivityFive.this, TeacherMainActivityTwo.class);
                    startActivity(I);
                    progressDialog.dismiss();
                    finish();
                } else {
                    Intent I = new Intent(SignUpActivityFive.this, ParentMainActivityTwo.class);
                    startActivity(I);
                    progressDialog.dismiss();
                    finish();
                }
            }
        });
    }

    void registerGenderAndProceed(final String downloadURL) {
        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(SignUpActivityFive.this);
        progressDialog.show();
        mDatabaseReference = mFirebaseDatabase.getReference();
        Map<String, Object> updater = new HashMap<String, Object>();
        updater.put("Parent/" + UID + "/gender", genderSelected);
        updater.put("Teacher/" + UID + "/gender", genderSelected);
        updater.put("Parent/" + UID + "/profilePicURL", downloadURL);
        updater.put("Teacher/" + UID + "/profilePicURL", downloadURL);
        mDatabaseReference.updateChildren(updater, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                applicationLauncherSharedPreferences.setLauncherActivity("Home");
                sharedPreferencesManager.setMyPicURL(downloadURL);
                if (accountType.equals("Teacher")) {
                    Intent I = new Intent(SignUpActivityFive.this, TeacherMainActivityTwo.class);
                    startActivity(I);
                    progressDialog.dismiss();
                    finish();
                } else {
                    Intent I = new Intent(SignUpActivityFive.this, ParentMainActivityTwo.class);
                    startActivity(I);
                    progressDialog.dismiss();
                    finish();
                }
            }
        });
    }

    public void ImageCropFunction() {
        try {
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("aspectX", 8);
            CropIntent.putExtra("aspectY", 8);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent, 2);

        } catch (ActivityNotFoundException e) {
            return;
        }
    }

    private void GetImageFromGallery() {
        if (ContextCompat.checkSelfPermission(SignUpActivityFive.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(SignUpActivityFive.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);

        } else {
            // Permission has already been granted
            GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 1);
        }
    }

    private void ClickImageFromCamera() {
        if (ContextCompat.checkSelfPermission(SignUpActivityFive.this,
            android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(SignUpActivityFive.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUESTPPERMISSIONCODECAMERA);

        } else {
            CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/ProfilePicture");

            if(!directory.exists() && !directory.isDirectory()) {
                if (directory.mkdirs()) {
                    file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                } else {
                    file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                }
            } else {
                file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            }
            uri = Uri.fromFile(file);
            CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
            CamIntent.putExtra("return-data", true);
            startActivityForResult(CamIntent, 0);
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
                    File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/ProfilePicture");

                    if(!directory.exists() && !directory.isDirectory()) {
                        if (directory.mkdirs()) {
                            file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        } else {
                            file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        }
                    } else {
                        file = new File(directory, "CeleriiProfilePicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    }
                    uri = Uri.fromFile(file);
                    CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                    CamIntent.putExtra("return-data", true);
                    startActivityForResult(CamIntent, 0);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                            Uri.fromParts("package", getPackageName(), null)));
                }
                return;
            }
            case REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 1);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
//                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                            Uri.fromParts("package", getPackageName(), null)));
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            ImageCropFunction();
        }
        if (requestCode == 1) {
            if (data != null) {
                uri = data.getData();
                ImageCropFunction();
            }
        }
        if (requestCode == 2) {
            if (data != null) {
                try {
                    if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                        String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                        showDialogWithMessage(Html.fromHtml(messageString));
                        return;
                    }

                    progressDialog.show();
                    Bitmap bitmap = data.getExtras().getParcelable("data");
                    newProfilePicture.setImageBitmap(null);
                    newProfilePicture.setImageBitmap(bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    String sortableDate = Date.convertToSortableDate(Date.getDate());
                    mStorageReference = mFirebaseStorage.getReference().child("ProfilePictures/" + UID + "/" + sortableDate + ".jpg");
                    UploadTask uploadTask = mStorageReference.putBytes(byteArray);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadURL = "";
                            downloadURL = taskSnapshot.getDownloadUrl().toString();

                            HashMap<String, Object> newPhotoMap = new HashMap<>();
                            newPhotoMap.put("Parent/" + UID + "/profilePicURL", downloadURL);
                            newPhotoMap.put("Teacher/" + UID + "/profilePicURL", downloadURL);

                            mDatabaseReference = mFirebaseDatabase.getReference();
                            mDatabaseReference.updateChildren(newPhotoMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    progressDialog.dismiss();
                                    sharedPreferencesManager.setMyPicURL(downloadURL);
                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.custom_upload_successful_dialog);
                                    dialog.setCancelable(false);
                                    dialog.setCanceledOnTouchOutside(false);
                                    TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialogmessage);
                                    TextView close = (TextView) dialog.findViewById(R.id.close);
                                    dialog.show();

                                    dialogMessage.setText("Picture uploaded successfully!");

                                    close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            String messageString = "Your picture upload attempt failed. However, you can upload a profile picture through the " + "<b>" + "Edit my profile" + "</b>" + " setting. " +
                                    "You can try again or push the " + "<b>" + "Begin" + "</b>" + " button to continue";
                            showDialogWithMessage(Html.fromHtml(messageString));
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
                } catch (Exception e){
                    progressDialog.dismiss();
                    Log.d("Crop Exception", e.getMessage());
                    return;
                }
            }
        }
    }
}
