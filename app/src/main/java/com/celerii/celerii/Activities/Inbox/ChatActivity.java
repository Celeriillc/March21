package com.celerii.celerii.Activities.Inbox;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ChatRowAdapter;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Chats;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    SharedPreferencesManager sharedPreferencesManager;
    Bundle bundle;
    final Context context = this;

    FirebaseAuth auth;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private ArrayList<Chats> chatsList;
    public RecyclerView recyclerView;
    public ChatRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ImageView sendMessage, attachments, camera, gallery;
    EditText editMessage;

    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    public static final int REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE  = 1000;
    public static final int REQUESTPPERMISSIONCODECAMERA  = 1001;
    CustomProgressDialogOne progressDialog;

    Toolbar mToolbar;

    String IDofChatPartner = "";
    String nameOfChatPartner = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progressDialog = new CustomProgressDialogOne(this);

        bundle = getIntent().getExtras();
        IDofChatPartner = bundle.getString("ID");
        nameOfChatPartner = bundle.getString("name");

        auth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mToolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(nameOfChatPartner);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(mLayoutManager);

        sendMessage = (ImageView) findViewById(R.id.sendMessageButton);
        attachments = (ImageView) findViewById(R.id.attachments);
        editMessage = (EditText) findViewById(R.id.messageEditText);

        chatsList = new ArrayList<>();
        loadMessagesFromFirebase();
        mAdapter = new ChatRowAdapter(chatsList, this);
        recyclerView.setAdapter(mAdapter);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMessage.getText().toString().trim().isEmpty() || editMessage.getText().toString().trim().equals("")){
                    return;
                }
                postMessageToFirebase("");
                editMessage.setText("");
            }
        });

        attachments.setOnClickListener(new View.OnClickListener() {
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
    }

    private void loadMessagesFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Messages").child(mFirebaseUser.getUid()).child(IDofChatPartner);
        mDatabaseReference.orderByChild("sortableDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatsList.clear();
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                        String messageID = postSnapShot.getKey();
                        Chats chat = postSnapShot.getValue(Chats.class);
                        chat.setMessageID(messageID);
                        chatsList.add(chat);
                    }
                    Collections.reverse(chatsList);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void postMessageToFirebase(String fileURL){
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            String messageString = "Your device is not connected to the internet. Check your connection and try again.";
            showDialogWithMessage(messageString);
            return;
        }

        DatabaseReference senderKeyDRef = mFirebaseDatabase.getReference().child("Messages").child(mFirebaseUser.getUid()).child(IDofChatPartner).push();
        final String senderKey = senderKeyDRef.getKey();
        String message;
        if (fileURL.isEmpty()) {
            message = editMessage.getText().toString().trim();
        } else {
            message = "Image";
        }
        final String senderId = mFirebaseUser.getUid();
        final String recieverId = IDofChatPartner;
        boolean isSeen = false;
        boolean isMine = true;
        boolean isRow = false;
        Calendar calendar = Calendar.getInstance();
        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);
        final Chats senderChat = new Chats(message, senderId, recieverId, date, sortableDate, isSeen, isMine, fileURL, "", isRow);
        final Chats recieverChat = new Chats(message, senderId, recieverId, date, sortableDate, isSeen, !isMine, fileURL, sharedPreferencesManager.getMyPicURL(), isRow);
        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> newChatMessageMap = new HashMap<String, Object>();
                newChatMessageMap.put("Messages/" + senderId + "/" + recieverId + "/" + senderKey, senderChat);
                newChatMessageMap.put("Messages/" + recieverId + "/" + senderId + "/" + senderKey, recieverChat);
                newChatMessageMap.put("Messages Recent/" + senderId + "/" + recieverId, senderChat);
                newChatMessageMap.put("Messages Recent/" + recieverId + "/" + senderId, recieverChat);
                newChatMessageMap.put("Notification Badges/General/" + recieverId + "/Inbox/status", true);
                DatabaseReference updateBottomNotificationBadgeRef = mFirebaseDatabase.getReference("Notification Badges/General/" + recieverId + "/Inbox/number");
                updateBottomNotificationBadgeRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Integer currentValue = mutableData.getValue(Integer.class);
                        if (currentValue == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(currentValue + 1);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
                mDatabaseReference.updateChildren(newChatMessageMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        //TODO: Change the message status image with a sound
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

            startActivityForResult(CropIntent, 2);

        } catch (ActivityNotFoundException e) {
            return;
        }
    }

    private void ClickImageFromCamera() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    android.Manifest.permission.CAMERA)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUESTPPERMISSIONCODECAMERA);
//            }
        } else {
            CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/Chat/Sent");

            if(!directory.exists() && !directory.isDirectory()) {
                if (directory.mkdirs()) {
                    file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                } else {
                    file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                }
            } else {
                file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            }
            uri = Uri.fromFile(file);
            CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
            CamIntent.putExtra("return-data", true);
            startActivityForResult(CamIntent, 0);
        }
    }

    private void GetImageFromGallery() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                int o, i, u;
//                o = 90;
//                i = 9;
//                u = o + i;
//            } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);
//            }
        } else {
            // Permission has already been granted
            GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 1);
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
                    File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/Chat/Sent");

                    if(!directory.exists() && !directory.isDirectory()) {
                        if (directory.mkdirs()) {
                            file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        } else {
                            file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        }
                    } else {
                        file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
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
                        showDialogWithMessage((messageString));
                        return;
                    }
                    progressDialog.show();
                    Bitmap bitmap = data.getExtras().getParcelable("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
//
                    String sortableDate = Date.convertToSortableDate(Date.getDate());
                    mStorageReference = mFirebaseStorage.getReference().child("Chat/" + mFirebaseUser.getUid() + "/" + IDofChatPartner + "/" + sortableDate + ".jpg");
                    UploadTask uploadTask = mStorageReference.putBytes(byteArray);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadURL = "";
                            downloadURL = taskSnapshot.getDownloadUrl().toString();
                            postMessageToFirebase(downloadURL);
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            String messageString = "Your image failed to upload, please try again later";
                            showDialogWithMessage((messageString));
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
//                    progressDialog.dismiss();
//                    Log.d("Crop Exception", e.getMessage());
//                    return;
                }
            }
        }
    }

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(context);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
