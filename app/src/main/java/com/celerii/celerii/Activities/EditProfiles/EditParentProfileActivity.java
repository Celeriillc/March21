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
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.TextView;

import com.celerii.celerii.Activities.EditPersonalInformationDetails.GenderEditActivity;
import com.celerii.celerii.Activities.EditPersonalInformationDetails.GeneralEditActivity;
import com.celerii.celerii.Activities.Settings.EditPhoneNumberActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.InboxAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.bumptech.glide.Glide;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class EditParentProfileActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    final Context context = this;

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
    Bitmap bitmap;
    byte[] byteArray;
    CustomProgressDialogOne progressDialog;

    LinearLayout newProfilePictureLayout;

    Toolbar toolbar;
    LinearLayout genderLayout, phoneNumberLayout;
    TextView gender, phoneNumber;
    ImageView profilePicture, tapToUpload;
    EditText firstName, middleName, lastName, occupation;

    String featureUseKey = "";
    String featureName = "Edit Parent Profile";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_parent_profile);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        progressDialog = new CustomProgressDialogOne(EditParentProfileActivity.this);
        byteArray = null;

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        profilePicture = (ImageView) findViewById(R.id.profilepicture);
        newProfilePictureLayout = (LinearLayout) findViewById(R.id.newprofilepicturelayout);
        newProfilePictureLayout.setClipToOutline(true);
        tapToUpload = (ImageView) findViewById(R.id.taptoupload);
        genderLayout = (LinearLayout) findViewById(R.id.genderlayout);
        phoneNumberLayout = (LinearLayout) findViewById(R.id.phonenumberlayout);

        firstName = (EditText) findViewById(R.id.firstname);
        middleName = (EditText) findViewById(R.id.middlename);
        lastName = (EditText) findViewById(R.id.lastname);
        gender = (TextView) findViewById(R.id.gender);
        phoneNumber = (TextView) findViewById(R.id.phonenumber);
        occupation = (EditText) findViewById(R.id.occupation);

        loadProfileInformation();

        tapToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent i = new Intent(EditParentProfileActivity.this, GenderEditActivity.class);
                Bundle b = new Bundle();
                b.putString("gender", gender.getText().toString());
                i.putExtras(b);
                startActivityForResult(i, 1);
            }
        });

        phoneNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(EditParentProfileActivity.this, GeneralEditActivity.class);
//                Bundle b = new Bundle();
//                b.putString("Caption", "Phone Number");
//                b.putString("Description", "Phone Number Description");
//                b.putString("EditHint", "Phone Number");
//                b.putString("EditItem", phoneNumber.getText().toString());
//                i.putExtras(b);
                Intent i = new Intent(EditParentProfileActivity.this, EditPhoneNumberActivity.class);
                startActivityForResult(i, 2);
            }
        });
    }

    private void loadProfileInformation() {
        firstName.setText(sharedPreferencesManager.getMyFirstName());
        middleName.setText(sharedPreferencesManager.getMyMiddleName());
        lastName.setText(sharedPreferencesManager.getMyLastName());
        gender.setText(sharedPreferencesManager.getMyGender());
        phoneNumber.setText(sharedPreferencesManager.getMyPhoneNumber());
        occupation.setText(sharedPreferencesManager.getMyOccupation());

        Drawable textDrawable;
        String myName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        if (!myName.trim().isEmpty()) {
            String[] nameArray = myName.replaceAll("\\s+", " ").split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], 150);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawableTransparent(context, nameArray[0], nameArray[1], 150);
            }
            profilePicture.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
        }

        if (!sharedPreferencesManager.getMyPicURL().isEmpty()) {
            Glide.with(getBaseContext())
                    .load(sharedPreferencesManager.getMyPicURL())
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .into(profilePicture);
        }
    }

    private boolean validateName(String nameString, EditText name) {
        if (nameString.isEmpty()) {
            String messageString = "You need to enter a name in the name field";
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
            if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                showDialogWithMessage(Html.fromHtml(messageString));
                return false;
            }

            final String firstNameString = firstName.getText().toString().trim();
            final String middleNameString = middleName.getText().toString().trim();
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
                HashMap<String, Object> editProfileMap = new HashMap<String, Object>();
                editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/firstName", firstNameString);
                editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/searchableFirstName", firstNameString.toLowerCase());
                editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/middleName", middleNameString);
                editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/searchableMiddleName", middleNameString.toLowerCase());
                editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/lastName", lastNameString);
                editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/searchableLastName", lastNameString.toLowerCase());
                editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/gender", gender.getText().toString().trim());
                editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/phone", phoneNumber.getText().toString().trim());

                editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/firstName", firstNameString);
                editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/searchableFirstName", firstNameString.toLowerCase());
                editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/middleName", middleNameString);
                editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/searchableMiddleName", middleNameString.toLowerCase());
                editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/lastName", lastNameString);
                editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/searchableLastName", lastNameString.toLowerCase());
                editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/gender", gender.getText().toString().trim());
                editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/phone", phoneNumber.getText().toString().trim());
                editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/occupation", occupation.getText().toString().trim());

                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(editProfileMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            progressDialog.dismiss();
                            sharedPreferencesManager.setMyFirstName(firstNameString);
                            sharedPreferencesManager.setMyMiddleName(middleNameString);
                            sharedPreferencesManager.setMyLastName(lastNameString);
                            sharedPreferencesManager.setMyGender(gender.getText().toString().trim());
                            sharedPreferencesManager.setMyPhoneNumber(phoneNumber.getText().toString().trim());
                            sharedPreferencesManager.setMyOccupation(occupation.getText().toString().trim());

                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.custom_upload_successful_dialog);
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialogmessage);
                            TextView close = (TextView) dialog.findViewById(R.id.close);
                            dialog.show();

                            dialogMessage.setText("Your profile has been successfully updated");

                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                            ShowDialogWithMessage.showDialogWithMessage(context, message);
                        }
                    }
                });
            } else {
                mStorageReference = mFirebaseStorage.getReference().child("CeleriiProfilePicture/" + mFirebaseUser.getUid() + "/profilepicture");
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

                            HashMap<String, Object> editProfileMap = new HashMap<String, Object>();
                            editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/firstName", firstNameString);
                            editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/searchableFirstName", firstNameString.toLowerCase());
                            editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/middleName", middleNameString);
                            editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/searchableMiddleName", middleNameString.toLowerCase());
                            editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/lastName", lastNameString);
                            editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/searchableLastName", lastNameString.toLowerCase());
                            editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/profilePicURL", downloadURL);
                            editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/gender", gender.getText().toString().trim());
                            editProfileMap.put("Teacher/" + auth.getCurrentUser().getUid() + "/phone", phoneNumber.getText().toString().trim());

                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/firstName", firstNameString);
                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/searchableFirstName", firstNameString.toLowerCase());
                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/middleName", middleNameString);
                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/searchableMiddleName", middleNameString.toLowerCase());
                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/lastName", lastNameString);
                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/searchableLastName", lastNameString.toLowerCase());
                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/profilePicURL", downloadURL);
                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/gender", gender.getText().toString().trim());
                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/phone", phoneNumber.getText().toString().trim());
                            editProfileMap.put("Parent/" + auth.getCurrentUser().getUid() + "/occupation", occupation.getText().toString().trim());

                            mDatabaseReference = mFirebaseDatabase.getReference();
                            mDatabaseReference.updateChildren(editProfileMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        progressDialog.dismiss();
                                        sharedPreferencesManager.setMyFirstName(firstNameString);
                                        sharedPreferencesManager.setMyMiddleName(middleNameString);
                                        sharedPreferencesManager.setMyLastName(lastNameString);
                                        sharedPreferencesManager.setMyPicURL(downloadURL);
                                        sharedPreferencesManager.setMyGender(gender.getText().toString().trim());
                                        sharedPreferencesManager.setMyPhoneNumber(phoneNumber.getText().toString().trim());
                                        sharedPreferencesManager.setMyOccupation(occupation.getText().toString().trim());

                                        final Dialog dialog = new Dialog(context);
                                        dialog.setContentView(R.layout.custom_upload_successful_dialog);
                                        dialog.setCancelable(false);
                                        dialog.setCanceledOnTouchOutside(false);
                                        TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialogmessage);
                                        TextView close = (TextView) dialog.findViewById(R.id.close);
                                        dialog.show();

                                        dialogMessage.setText("Your profile has been successfully updated");

                                        close.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                        showDialogWithMessage(Html.fromHtml(message));
                                    }
                                }
                            });
                        }
                    }
                });

//                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        String messageString = "An error occured while uploading your story, please try again";
//                        showDialogWithMessage(Html.fromHtml(messageString));
//                    }
//                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
////                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
////                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
//                    }
//                });
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                gender.setText(data.getStringExtra("selectedgender"));
            }
        }
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                phoneNumber.setText(data.getStringExtra("Caption"));
            }
        }
        if (requestCode == 10 && resultCode == RESULT_OK) {
//            ImageCropFunction();
            if (data != null) {
                try {
                    bitmap = data.getExtras().getParcelable("data");
                    profilePicture.setImageDrawable(null);
                    profilePicture.setImageBitmap(bitmap);
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
                    profilePicture.setImageDrawable(null);
                    profilePicture.setImageBitmap(bitmap);
                } catch (Exception e) {
                    //tODO:
                }
            }
//            if (data != null) {
//                uri = data.getData();
//                ImageCropFunction();
//            }
        }
//        if (requestCode == 12) {
//            if (data != null) {
//                uri = data.getData();
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                    profilePicture.setImageDrawable(null);
//                    profilePicture.setImageBitmap(bitmap);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byteArray = stream.toByteArray();
//                } catch (Exception e) {
//                    return;
//                }

//                try {
//                    Bitmap bitmap = data.getExtras().getParcelable("data");
//                    profilePicture.setImageDrawable(null);
//                    profilePicture.setImageBitmap(bitmap);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byteArray = stream.toByteArray();
//                } catch (Exception e){
//                    return;
//                }
//            }
//        }
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

            startActivityForResult(CropIntent, 12);

        } catch (ActivityNotFoundException e) {
            return;
        }
    }

    private void GetImageFromGallery() {
        if (ContextCompat.checkSelfPermission(EditParentProfileActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditParentProfileActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);

        } else {
            // Permission has already been granted
            GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(GalIntent, "Select Picture From Gallery"), 11);
        }
    }

    private void ClickImageFromCamera() {
        if (ContextCompat.checkSelfPermission(EditParentProfileActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(EditParentProfileActivity.this,
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
}
