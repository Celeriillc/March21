package com.celerii.celerii.Activities.Inbox;

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
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ChatRowAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Chats;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.NewChatRowModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.SchoolSettings;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentsSchoolsClassesandTeachersModel;
import com.celerii.celerii.models.Teacher;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
    private HashMap<String, Chats> chatMaps;
    public RecyclerView recyclerView;
    public ChatRowAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    RelativeLayout errorLayout, progressLayout;
    LinearLayout profilePictureClipper;
    TextView errorLayoutText;
    ImageView sendMessage, attachments, profilePicture;
    EditText editMessage;
    Boolean schoolAllowsParentTeacherMessaging = true;

    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    public static final int REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE  = 1000;
    public static final int REQUESTPPERMISSIONCODECAMERA  = 1001;
    CustomProgressDialogOne progressDialog;

    Toolbar mToolbar;

    String IDofChatPartner = "";
    String nameOfChatPartner = "";
    String receiverNode = "";
    String parentActivity;

    String featureUseKey = "";
    String featureName = "Chat";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progressDialog = new CustomProgressDialogOne(this);

        auth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        bundle = getIntent().getExtras();
        IDofChatPartner = bundle.getString("ID");
        nameOfChatPartner = bundle.getString("name");
        parentActivity = bundle.getString("parentActivity");
        if (parentActivity != null) {
            if (!parentActivity.isEmpty()) {
                sharedPreferencesManager.setActiveAccount(parentActivity);
                mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
                mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue(parentActivity);
            }
        }

        mToolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(nameOfChatPartner);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        profilePicture = (ImageView) findViewById(R.id.myprofilepic);
        profilePictureClipper = (LinearLayout) findViewById(R.id.profilepictureclipper);
        sendMessage = (ImageView) findViewById(R.id.sendMessageButton);
        attachments = (ImageView) findViewById(R.id.attachments);
        editMessage = (EditText) findViewById(R.id.messageEditText);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        profilePictureClipper.setClipToOutline(true);

        chatsList = new ArrayList<>();
        chatMaps = new HashMap<>();
        mAdapter = new ChatRowAdapter(chatsList, nameOfChatPartner, this);
        recyclerView.setAdapter(mAdapter);
        loadMessagesFromFirebase();

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

        Drawable textDrawable;
        String myName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        if (!myName.trim().isEmpty()) {
            String[] nameArray = myName.replaceAll("\\s+", " ").trim().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawableColor(context, nameArray[0], 35,4);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawableColor(context, nameArray[0], nameArray[1], 35, 4);
            }
            profilePicture.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 35);
        }

        if (!sharedPreferencesManager.getMyPicURL().isEmpty()) {
            Glide.with(context)
                    .load(sharedPreferencesManager.getMyPicURL())
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(profilePicture);
        }
    }

    int schoolAllowsParentTeacherMessagingCounter;
    ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList;
    ArrayList<StudentsSchoolsClassesandTeachersModel> studentsSchoolsClassesandTeachersModelList;
    HashMap<String, String> teacherSchoolMap = new HashMap<>();
    HashMap<String, String> parentSchoolMap = new HashMap<>();
    HashMap<String, Boolean> schoolAllowsParentTeacherMessagingMap = new HashMap<>();
    ArrayList<String> schoolList = new ArrayList<>();
    ArrayList<String> teacherList = new ArrayList<>();
    private void loadMessagesFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            Gson gson = new Gson();
            String studentsSchoolsClassesandTeachersJSON = sharedPreferencesManager.getStudentsSchoolsClassesTeachers();
            Type type = new TypeToken<ArrayList<StudentsSchoolsClassesandTeachersModel>>() {}.getType();
            studentsSchoolsClassesandTeachersModelList = gson.fromJson(studentsSchoolsClassesandTeachersJSON, type);
            schoolAllowsParentTeacherMessagingCounter = 0;

            if (studentsSchoolsClassesandTeachersModelList == null) {
                studentsSchoolsClassesandTeachersModelList = new ArrayList<>();
            }

            if (studentsSchoolsClassesandTeachersModelList.size() > 0) {
                for (int i = 0; i < studentsSchoolsClassesandTeachersModelList.size(); i++) {
                    final StudentsSchoolsClassesandTeachersModel studentsSchoolsClassesandTeachersModel = studentsSchoolsClassesandTeachersModelList.get(i);
                    String teacherID = studentsSchoolsClassesandTeachersModel.getTeacherID();
                    String schoolID = studentsSchoolsClassesandTeachersModel.getSchoolID();

                    if (!teacherID.equals("")) {
                        teacherSchoolMap.put(teacherID, schoolID);
                    }

                    if (!schoolList.contains(schoolID)) {
                        schoolList.add(schoolID);
                    }

                    mDatabaseReference = mFirebaseDatabase.getReference().child("School Settings").child(schoolID);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            schoolAllowsParentTeacherMessagingCounter++;
                            if (dataSnapshot.exists()) {
                                SchoolSettings schoolSettings = dataSnapshot.getValue(SchoolSettings.class);
                                schoolAllowsParentTeacherMessagingMap.put(schoolID, schoolSettings.isAllowParentTeacherMessaging());
                            } else {
                                schoolAllowsParentTeacherMessagingMap.put(schoolID, true);
                            }

                            if (schoolAllowsParentTeacherMessagingCounter == studentsSchoolsClassesandTeachersModelList.size()) {
                                loadMessages();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                loadMessages();
            }
        } else {
            Gson gson = new Gson();
            String classStudentParentJSON = sharedPreferencesManager.getClassesStudentParent();
            Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
            classesStudentsAndParentsModelList = gson.fromJson(classStudentParentJSON, type);
            schoolAllowsParentTeacherMessagingCounter = 0;

            if (classesStudentsAndParentsModelList == null) {
                classesStudentsAndParentsModelList = new ArrayList<>();
            }

            if (classesStudentsAndParentsModelList.size() > 0) {
                for (int i = 0; i < classesStudentsAndParentsModelList.size(); i++) {
                    final ClassesStudentsAndParentsModel classesStudentsAndParentsModel = classesStudentsAndParentsModelList.get(i);
                    String parentID = classesStudentsAndParentsModel.getParentID();
                    String schoolID = classesStudentsAndParentsModel.getSchoolID();
                    if (!parentID.equals("")) {
                        parentSchoolMap.put(parentID, schoolID);
                    }

                    if (!schoolList.contains(schoolID)) {
                        schoolList.add(schoolID);
                    }

                    mDatabaseReference = mFirebaseDatabase.getReference().child("School Teacher").child(schoolID);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    String teacherID = postSnapshot.getKey();
                                    if (!teacherList.contains(teacherID)) {
                                        teacherList.add(teacherID);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    mDatabaseReference = mFirebaseDatabase.getReference().child("School Settings").child(schoolID);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            schoolAllowsParentTeacherMessagingCounter++;
                            if (dataSnapshot.exists()) {
                                SchoolSettings schoolSettings = dataSnapshot.getValue(SchoolSettings.class);
                                schoolAllowsParentTeacherMessagingMap.put(schoolID, schoolSettings.isAllowParentTeacherMessaging());
                            } else {
                                schoolAllowsParentTeacherMessagingMap.put(schoolID, true);
                            }

                            if (schoolAllowsParentTeacherMessagingCounter == classesStudentsAndParentsModelList.size()) {
                                loadMessages();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                loadMessages();
            }
        }
    }

    private void loadMessages() {
        mDatabaseReference = mFirebaseDatabase.getReference().child("Messages").child(mFirebaseUser.getUid()).child(IDofChatPartner);
        mDatabaseReference.orderByChild("sortableDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    receiverNode = IDofChatPartner;
                    chatsList.clear();
                    mAdapter.notifyDataSetChanged();
                    for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                        String messageID = postSnapShot.getKey();
                        Chats chat = postSnapShot.getValue(Chats.class);
                        chat.setMessageID(messageID);
                        chatsList.add(chat);
                    }
//                    Collections.reverse(chatsList);
                    recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    mAdapter.notifyDataSetChanged();

                    if (recyclerView.getVisibility() == View.GONE) {
                        progressLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);
                    }
                } else {
                    mDatabaseReference = mFirebaseDatabase.getReference().child("Admin").child(IDofChatPartner);
                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mDatabaseReference = mFirebaseDatabase.getReference().child("Messages").child(mFirebaseUser.getUid()).child("Admin");
                                mDatabaseReference.orderByChild("sortableDate").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            receiverNode = "Admin";
                                            chatsList.clear();
                                            mAdapter.notifyDataSetChanged();
                                            for (DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                                                String messageID = postSnapShot.getKey();
                                                Chats chat = postSnapShot.getValue(Chats.class);
                                                chat.setMessageID(messageID);
                                                if (!chat.getReceiverID().equals(mFirebaseUser.getUid())) {
                                                    chat.setReceiverID("Admin");
                                                }
                                                if (!chat.getSenderID().equals(mFirebaseUser.getUid())) {
                                                    chat.setSenderID("Admin");
                                                }
                                                chatsList.add(chat);
                                            }

                                            recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                                            mAdapter.notifyDataSetChanged();
                                        }

                                        if (recyclerView.getVisibility() == View.GONE) {
                                            progressLayout.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                            errorLayout.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                if (recyclerView.getVisibility() == View.GONE) {
                                    progressLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    errorLayout.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void postMessageToFirebase(String fileURL) {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            String messageString = "Your device is not connected to the internet. Check your connection and try again.";
            showDialogWithMessage(Html.fromHtml(messageString));
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

        if (message.isEmpty()) {
            return;
        }

        if (!receiverNode.equals("Admin")) {
            receiverNode = IDofChatPartner;

            if (!schoolList.contains(IDofChatPartner)) {
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    if (!teacherSchoolMap.containsKey(IDofChatPartner)) {
                        String messageString = "You cannot send messages to " + "<b>" + nameOfChatPartner + "</b>" + " because " +
                                "your parent account is not connected to their account";
                        showDialogWithMessage(Html.fromHtml(messageString));
                        return;
                    } else {
                        String schoolID = teacherSchoolMap.get(IDofChatPartner);
                        if (schoolAllowsParentTeacherMessagingMap.get(schoolID) != null) {
                            if (!schoolAllowsParentTeacherMessagingMap.get(schoolID)) {
                                String messageString = "You cannot send messages to " + "<b>" + nameOfChatPartner + "</b>" + " because " +
                                        "your child(ren)'s school(s) doesn't allow parent to teacher messaging";
                                showDialogWithMessage(Html.fromHtml(messageString));
                                return;
                            }
                        }
                    }
                } else {
                    if (!teacherList.contains(IDofChatPartner)) {
                        if (!parentSchoolMap.containsKey(IDofChatPartner)) {
                            String messageString = "You cannot send messages to " + "<b>" + nameOfChatPartner + "</b>" + " because " +
                                    "your teacher account is not connected to their account";
                            showDialogWithMessage(Html.fromHtml(messageString));
                            return;
                        } else {
                            String schoolID = parentSchoolMap.get(IDofChatPartner);
                            if (schoolAllowsParentTeacherMessagingMap.get(schoolID) != null) {
                                if (!schoolAllowsParentTeacherMessagingMap.get(schoolID)) {
                                    String messageString = "You cannot send messages to " + "<b>" + nameOfChatPartner + "</b>" + " because " +
                                            "your school doesn't allow teacher to parent messaging";
                                    showDialogWithMessage(Html.fromHtml(messageString));
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        final String senderId = mFirebaseUser.getUid();
        final String receiverId = IDofChatPartner;
        boolean isSeen = false;
        boolean isMine = true;
        boolean isRow = false;
        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);
        final Chats senderChat = new Chats(message, senderId, receiverId, date, sortableDate, isSeen, isMine, fileURL, "", isRow);
        final Chats receiverChat = new Chats(message, senderId, receiverId, date, sortableDate, isSeen, !isMine, fileURL, sharedPreferencesManager.getMyPicURL(), isRow);

        Map<String, Object> newChatMessageMap = new HashMap<String, Object>();
        newChatMessageMap.put("Messages/" + senderId + "/" + receiverNode + "/" + senderKey, senderChat);
        newChatMessageMap.put("Messages/" + receiverNode + "/" + senderId + "/" + senderKey, receiverChat);
        newChatMessageMap.put("Messages Recent/" + senderId + "/" + receiverNode, senderChat);
        newChatMessageMap.put("Messages Recent/" + receiverNode + "/" + senderId, receiverChat);
        newChatMessageMap.put("Notification Badges/General/" + receiverId + "/Inbox/status", true);
        DatabaseReference updateBottomNotificationBadgeRef = mFirebaseDatabase.getReference("Notification Badges/General/" + receiverId + "/Inbox/number");
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

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(newChatMessageMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                    CustomToast.primaryBackgroundToast(context, message);
                }
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
//            File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/Chat/Sent");
//
//            if(!directory.exists() && !directory.isDirectory()) {
//                if (directory.mkdirs()) {
//                    file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                } else {
//                    file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                }
//            } else {
//                file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//            }
//            uri = Uri.fromFile(file);
//            CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
//            CamIntent.putExtra("return-data", true);
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
//                    File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/Chat/Sent");
//
//                    if(!directory.exists() && !directory.isDirectory()) {
//                        if (directory.mkdirs()) {
//                            file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                        } else {
//                            file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                        }
//                    } else {
//                        file = new File(directory, "CeleriiChat" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//                    }
//                    uri = Uri.fromFile(file);
//                    CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
//                    CamIntent.putExtra("return-data", true);
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
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    String sortableDate = Date.convertToSortableDate(Date.getDate());
                    mStorageReference = mFirebaseStorage.getReference().child("Chat/" + mFirebaseUser.getUid() + "/" + IDofChatPartner + "/" + sortableDate);
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
                            String downloadURL = "";
                            downloadURL = task.getResult().toString();
                            postMessageToFirebase(downloadURL);
                            progressDialog.dismiss();
                        }
                    });


//                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            String downloadURL = "";
//                            downloadURL = taskSnapshot.getDownloadUrl().toString();
//                            postMessageToFirebase(downloadURL);
//                            progressDialog.dismiss();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            String messageString = "Your image failed to upload, please try again later";
//                            showDialogWithMessage((messageString));
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
////                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
////                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
//                        }
//                    });
                } catch (Exception e){
//                    progressDialog.dismiss();
//                    Log.d("Crop Exception", e.getMessage());
//                    return;
                }
            }
        }
    }

    void showDialogWithMessage (Spanned messageString) {
        final Dialog dialog = new Dialog(context);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
                    Intent i = new Intent(this, ParentMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "1");
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (parentActivity.equals("Teacher")) {
                    Intent i = new Intent(this, TeacherMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "2");
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (parentActivity != null) {
            if (parentActivity.equals("Parent")) {
                Intent i = new Intent(this, ParentMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "1");
                i.putExtras(bundle);
                startActivity(i);
            } else if (parentActivity.equals("Teacher")) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "2");
                i.putExtras(bundle);
                startActivity(i);
            }
        }
    }
}
