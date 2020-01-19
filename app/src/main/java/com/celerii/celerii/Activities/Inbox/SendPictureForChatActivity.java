package com.celerii.celerii.Activities.Inbox;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class SendPictureForChatActivity extends AppCompatActivity {

    final Context context = this;

    FirebaseAuth auth;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    FirebaseUser mFirebaseUser;

    ImageView fullImage, backgroundImage, fullImageHidden;
    LinearLayout cancel, send;
    Bundle bundle;
    Uri uri;
    String IDofChatPartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_picture_for_chat);

        bundle = getIntent().getExtras();
        auth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
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
        IDofChatPartner = bundle.getString("IDofChatPartner");
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

    private void postPictureMessage(){
        if (uri != null) {
            final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(SendPictureForChatActivity.this);
            progressDialog.show();

            fullImageHidden.setDrawingCacheEnabled(true);
            fullImageHidden.buildDrawingCache();
            Bitmap bitmap = fullImageHidden.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            mStorageReference = mFirebaseStorage.getReference().child("InboxPictures/" + mFirebaseUser.getUid() + "/" + IDofChatPartner + "/" + "InboxPicture_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            UploadTask uploadTask = mStorageReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    CustomToast.whiteBackgroundBottomToast(getApplicationContext(), "File Uploaded");

                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    Intent intent = new Intent();
                    intent.putExtra("DownloadURL", downloadUrl);
                    setResult(RESULT_OK, intent);

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_upload_successful_dialog);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialogmessage);
                    TextView close = (TextView) dialog.findViewById(R.id.close);
                    dialog.show();

                    dialogMessage.setText("Picture sent successfully!");

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                            dialog.dismiss();
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
