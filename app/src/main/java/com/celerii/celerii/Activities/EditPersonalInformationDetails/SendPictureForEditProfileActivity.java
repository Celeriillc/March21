package com.celerii.celerii.Activities.EditPersonalInformationDetails;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.bumptech.glide.Glide;
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
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class SendPictureForEditProfileActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    final Context context = this;

    FirebaseAuth auth;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    ImageView fullImage, backgroundImage, fullImageHidden;
    LinearLayout cancel, send;
    Bundle bundle;
    Uri uri;

    String accountType, accountID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_picture_for_edit_profile);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        bundle = getIntent().getExtras();
        accountType = bundle.getString("AccountType");
        accountID = bundle.getString("AccountID");
        auth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        fullImage = (ImageView) findViewById(R.id.fullimage);
        backgroundImage = (ImageView) findViewById(R.id.backgroundimage);
        fullImageHidden = (ImageView) findViewById(R.id.fullimagehidden);
        cancel = (LinearLayout) findViewById(R.id.cancel);
        send = (LinearLayout) findViewById(R.id.send);

        try {
            Bitmap bitmap = bundle.getParcelable("data");
            fullImage.setImageBitmap(bitmap);
            fullImageHidden.setImageBitmap(bitmap);

            if (bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Glide.with(this)
                        .load(stream.toByteArray())
                        .bitmapTransform(new BlurTransformation(context, 20))
                        .into(backgroundImage);
            }

        } catch (Exception e){ return;}
        uri = Uri.parse(bundle.getString("URI"));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPictureMessage();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void postPictureMessage() {
        if (uri != null) {
//            final ProgressDialog progressDialog = new ProgressDialog(SendPictureForEditProfileActivity.this);
            final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(SendPictureForEditProfileActivity.this);
            progressDialog.show();

            fullImageHidden.setDrawingCacheEnabled(true);
            fullImageHidden.buildDrawingCache();
            Bitmap bitmap = fullImageHidden.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            mStorageReference = mFirebaseStorage.getReference().child("ProfilePictures/" + sharedPreferencesManager.getMyUserID() + "/" + sharedPreferencesManager.getMyFirstName() + sharedPreferencesManager.getMyLastName() + ".jpg");
            UploadTask uploadTask = mStorageReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadURL = uri.toString();
                            mDatabaseReference = mFirebaseDatabase.getReference();
                            Map<String, Object> updater = new HashMap<String, Object>();
                            if (accountType.equals("Teacher") || accountType.equals("Parent")) {
                                updater.put("Teacher/" + sharedPreferencesManager.getMyUserID() + "/" + "profilePicURL", downloadURL);
                                updater.put("Parent/" + sharedPreferencesManager.getMyUserID() + "/" + "profilePicURL", downloadURL);
                            } else {
                                updater.put("Student/" + accountID + "/" + "imageURL", downloadURL);
                            }
                            mDatabaseReference.updateChildren(updater, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    progressDialog.dismiss();

                                    final Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.custom_upload_successful_dialog);
                                    dialog.setCancelable(false);
                                    dialog.setCanceledOnTouchOutside(false);
                                    TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialogmessage);
                                    TextView close = (TextView) dialog.findViewById(R.id.close);
                                    dialog.show();

                                    dialogMessage.setText("Profile picture uploaded successfully!");

                                    close.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            finish();
                                            dialog.dismiss();
                                        }
                                    });
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
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });
        }
    }
}
