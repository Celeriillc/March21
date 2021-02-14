package com.celerii.celerii.Activities.Delete;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.ApplicationLauncherSharedPreferences;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ParentSetupActivityTwo extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    ApplicationLauncherSharedPreferences applicationLauncherSharedPreferences;

    Toolbar toolbar;
    Button btn_continue;
    ImageView imageView;
    Button buttonCamera, buttonGallery ;
    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    public  static final int RequestPermissionCode  = 1 ;
    DisplayMetrics displayMetrics ;
    int width, height;

    FirebaseAuth auth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_setup_two);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        applicationLauncherSharedPreferences = new ApplicationLauncherSharedPreferences(this);auth = FirebaseAuth.getInstance();

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        mFirebaseUser = auth.getCurrentUser();
        if (mFirebaseUser == null){
            sharedPreferencesManager.deleteActiveAccount();
            sharedPreferencesManager.deleteMyUserID();
            sharedPreferencesManager.deleteMyFirstName();
            sharedPreferencesManager.deleteMyLastName();
            sharedPreferencesManager.deleteMyPicURL();
            sharedPreferencesManager.deleteActiveKid();
            sharedPreferencesManager.deleteActiveClass();
            sharedPreferencesManager.deleteMyClasses();
            sharedPreferencesManager.deleteMyChildren();
            Intent I = new Intent(ParentSetupActivityTwo.this, IntroSlider.class);
            startActivity(I);
            finish();
            return;
        }

        toolbar = (Toolbar) findViewById(R.id.introtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Upload Photo");
        imageView = (ImageView) findViewById(R.id.profilephoto);

        btn_continue = (Button) findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null) {
                    //displaying a progress dialog while upload is going on
//                    final ProgressDialog progressDialog = new ProgressDialog(ParentSetupActivityTwo.this);
                    final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(ParentSetupActivityTwo.this);
                    progressDialog.show();

                    mStorageReference = mFirebaseStorage.getReference().child("ProfilePictures/" + sharedPreferencesManager.getMyUserID() + "/" + sharedPreferencesManager.getMyFirstName() + sharedPreferencesManager.getMyLastName() + ".jpg");
                    final UploadTask uploadTask = mStorageReference.putFile(uri);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            CustomToast.whiteBackgroundBottomToast(getApplicationContext(), "File Uploaded");

                            mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadURL = uri.toString();
                                    mDatabaseReference = mFirebaseDatabase.getReference();
                                    Map<String, Object> updater = new HashMap<String, Object>();
                                    updater.put("Teacher/" + sharedPreferencesManager.getMyUserID() + "/" + "profilePicURL", downloadURL);
                                    updater.put("Parent/" + sharedPreferencesManager.getMyUserID() + "/" + "profilePicURL", downloadURL);
                                    mDatabaseReference.updateChildren(updater, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            applicationLauncherSharedPreferences.setLauncherActivity("Home");
                                            Intent I = new Intent(ParentSetupActivityTwo.this, ParentMainActivityTwo.class);
                                            startActivity(I);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            CustomToast.whiteBackgroundBottomToast(getApplicationContext(), exception.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
                }
                else {
                    applicationLauncherSharedPreferences.setLauncherActivity("Home");
                    Intent I = new Intent(ParentSetupActivityTwo.this, ParentMainActivityTwo.class);
                    startActivity(I);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setup_two_upload_picture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_camera) {
//            EnableRuntimePermission();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Upload Photo");
            builder.setMessage("Upload an existing photo from your library or take a new one");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Camera",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ClickImageFromCamera() ;
                        }
                    });

            builder.setNegativeButton(
                    "Gallery",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            GetImageFromGallery();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ClickImageFromCamera() {
        CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory(),
                "CeleriiImage" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);
        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        CamIntent.putExtra("return-data", true);
        startActivityForResult(CamIntent, 0);
    }

    private void GetImageFromGallery() {
        GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 1);
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

            startActivityForResult(CropIntent, 2);

        } catch (ActivityNotFoundException e) {
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            ImageCropFunction();
        }
        else if (requestCode == 1) {
            if (data != null) {
                uri = data.getData();
                ImageCropFunction();
            }
        }
        else if (requestCode == 2) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                try {
                    Bitmap bitmap = bundle.getParcelable("data");
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e){ return;}
            }
        }
    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(ParentSetupActivityTwo.this,
                Manifest.permission.CAMERA))
        {
            CustomToast.whiteBackgroundBottomToast(ParentSetupActivityTwo.this,"CAMERA permission allows us to access your CAMERA app");
        } else {
            ActivityCompat.requestPermissions(ParentSetupActivityTwo.this,new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(ParentSetupActivityTwo.this,"Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(ParentSetupActivityTwo.this,"Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
