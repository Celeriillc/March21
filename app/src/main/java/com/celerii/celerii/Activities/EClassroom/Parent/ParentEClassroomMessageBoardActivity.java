package com.celerii.celerii.Activities.EClassroom.Parent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.os.Bundle;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.EClassroom.EClassroomScheduledClassParticipantsActivity;
import com.celerii.celerii.Activities.EClassroom.Teacher.TeacherEClassroomMessageBoardActivity;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ChatRowAdapter;
import com.celerii.celerii.adapters.ParentEClassroomMessageBoardAdapter;
import com.celerii.celerii.adapters.TeacherEClassroomMessageBoardAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.Month;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.models.Chats;
import com.celerii.celerii.models.EClassroomMessageBoardModel;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.SubscriptionModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ParentEClassroomMessageBoardActivity extends AppCompatActivity {

    final Context context = this;
    SharedPreferencesManager sharedPreferencesManager;
    Bundle bundle;

    FirebaseAuth auth;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    private ArrayList<EClassroomMessageBoardModel> eClassroomMessageBoardModelList;
    private HashMap<String, EClassroomMessageBoardModel> EClassroomMessageBoardModelMaps;
    public RecyclerView recyclerView;
    public ParentEClassroomMessageBoardAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    RelativeLayout errorLayout, progressLayout;
    LinearLayout profilePictureClipper;
    TextView errorLayoutText;
    ImageView participants, sendMessage, attachments, profilePicture;
    EditText editMessage;
    Button joinClass;

    File file;
    Uri uri;
    Intent CamIntent, GalIntent, CropIntent ;
    public static final int REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE  = 1000;
    public static final int REQUESTPPERMISSIONCODECAMERA  = 1001;
    CustomProgressDialogOne progressDialog;

    Toolbar mToolbar;

    String activeStudentID = "";
    String activeStudent = "";
    String activeStudentName = "";
    String activeStudentProfilePictureURL = "";

    String scheduledClassID = "";
    String scheduledClassLink = "";
    String scheduledClassState = "";
    String scheduledClassScheduledDate = "";
    String parentActivity;

    String featureUseKey = "";
    String featureName = "Parent E Classroom Message Board";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_eclassroom_message_board);

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
        activeStudent = bundle.getString("Child ID");
        scheduledClassID = bundle.getString("Scheduled Class ID");
        scheduledClassLink = bundle.getString("Scheduled Class Link");
        scheduledClassState = bundle.getString("Scheduled Class State");
        scheduledClassScheduledDate = bundle.getString("Scheduled Class Scheduled Date");
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
        getSupportActionBar().setTitle("Message Board");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setReverseLayout(true);
//        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);

        if (activeStudent == null) {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Gson gson = new Gson();
                ArrayList<Student> myChildren = new ArrayList<>();
                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                Type type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                myChildren = gson.fromJson(myChildrenJSON, type);

                if (myChildren != null) {
                    if (myChildren.size() > 0) {
                        gson = new Gson();
                        activeStudent = gson.toJson(myChildren.get(0));
                        sharedPreferencesManager.setActiveKid(activeStudent);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        return;
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                    return;
                }
            } else {
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText("We couldn't find this student's account");
                return;
            }
        } else {
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                Boolean activeKidExist = false;
                Gson gson = new Gson();
                Type type = new TypeToken<Student>() {
                }.getType();
                Student activeKidModel = gson.fromJson(activeStudent, type);

                String myChildrenJSON = sharedPreferencesManager.getMyChildren();
                type = new TypeToken<ArrayList<Student>>() {
                }.getType();
                ArrayList<Student> myChildren = gson.fromJson(myChildrenJSON, type);

                for (Student student : myChildren) {
                    if (activeKidModel.getStudentID().equals(student.getStudentID())) {
                        activeKidExist = true;
                        activeKidModel = student;
                        activeStudent = gson.toJson(activeKidModel);
                        sharedPreferencesManager.setActiveKid(activeStudent);
                        break;
                    }
                }

                if (!activeKidExist) {
                    if (myChildren.size() > 0) {
                        if (myChildren.size() > 1) {
                            gson = new Gson();
                            activeStudent = gson.toJson(myChildren.get(0));
                            sharedPreferencesManager.setActiveKid(activeStudent);
                        }
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        progressLayout.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                        errorLayoutText.setText(Html.fromHtml("You're not connected to any of your children's account. Click the " + "<b>" + "Search" + "</b>" + " button to search for your child to get started or get started by clicking the " + "<b>" + "Find my child" + "</b>" + " button below"));
                        return;
                    }
                }
            }
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Student>() {}.getType();
        Student activeStudentModel = gson.fromJson(activeStudent, type);

        activeStudentID = activeStudentModel.getStudentID();
        activeStudentName = activeStudentModel.getFirstName() + " " + activeStudentModel.getLastName();
        activeStudentProfilePictureURL = activeStudentModel.getImageURL();

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);
        profilePicture = (ImageView) findViewById(R.id.myprofilepic);
        profilePictureClipper = (LinearLayout) findViewById(R.id.profilepictureclipper);
        participants = (ImageView) findViewById(R.id.participants);
        sendMessage = (ImageView) findViewById(R.id.sendMessageButton);
        attachments = (ImageView) findViewById(R.id.attachments);
        editMessage = (EditText) findViewById(R.id.messageEditText);
        joinClass = (Button) findViewById(R.id.joinclass);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        profilePictureClipper.setClipToOutline(true);

        eClassroomMessageBoardModelList = new ArrayList<>();
        EClassroomMessageBoardModelMaps = new HashMap<>();
        mAdapter = new ParentEClassroomMessageBoardAdapter(eClassroomMessageBoardModelList, this);
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
        String myName = activeStudentName;
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

        if (!activeStudentProfilePictureURL.isEmpty()) {
            Glide.with(context)
                    .load(activeStudentProfilePictureURL)
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(profilePicture);
        }

        participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParentEClassroomMessageBoardActivity.this, EClassroomScheduledClassParticipantsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Scheduled Class ID", scheduledClassID);
                bundle.putString("Scheduled Class State", scheduledClassState);
                bundle.putString("Scheduled Class Scheduled Date", scheduledClassScheduledDate);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        joinClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
                    String messageString = "Your device is not connected to the internet. Check your connection and try again.";
                    ShowDialogWithMessage.showDialogWithMessage(context, Html.fromHtml(messageString));
                    return;
                }

                if (scheduledClassState.equals("Concluded")) {
                    String messageString = "This class has been closed by its creator. You can still post on its message board but audio and video access has been restricted.";
                    ShowDialogWithMessage.showDialogWithMessage(context, Html.fromHtml(messageString));
                    return;
                }

                Gson gson = new Gson();
                Boolean isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
                String subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationParents();
                Type type = new TypeToken<HashMap<String, ArrayList<SubscriptionModel>>>() {}.getType();
                HashMap<String, ArrayList<SubscriptionModel>> subscriptionModelMap = gson.fromJson(subscriptionModelJSON, type);
                SubscriptionModel subscriptionModel = new SubscriptionModel();
                if (subscriptionModelMap != null) {
                    ArrayList<SubscriptionModel> subscriptionModelList = subscriptionModelMap.get(activeStudentID);
                    String latestSubscriptionDate = "0000/00/00 00:00:00:000";
                    if (subscriptionModelList != null) {
                        for (SubscriptionModel subscriptionModel1 : subscriptionModelList) {
                            if (Date.compareDates(subscriptionModel1.getExpiryDate(), latestSubscriptionDate)) {
                                subscriptionModel = subscriptionModel1;
                                latestSubscriptionDate = subscriptionModel1.getExpiryDate();
                            }
                        }
                    } else {
                        latestSubscriptionDate = "0000/00/00 00:00:00:000";
                    }
                }
                Boolean isExpired = Date.compareDates(Date.getDate(), subscriptionModel.getExpiryDate());

                if (!isOpenToAll) {
                    if (isExpired) {
                        String messageString = "This classroom is not currently available because you do not have an active subscription, please subscribe " + "<b>" + activeStudentName + "</b>" + " to a Celerii plan to get access to this classroom.";
                        showDialogWithMessage(Html.fromHtml(messageString));
                        return;
                    }
                }

                Calendar calendar = Calendar.getInstance();
                int year = Integer.parseInt(scheduledClassScheduledDate.split(" ")[0].split("/")[0]);
                int month = Integer.parseInt(scheduledClassScheduledDate.split(" ")[0].split("/")[1]) - 1;
                int day = Integer.parseInt(scheduledClassScheduledDate.split(" ")[0].split("/")[2]);
                int hourOfDay = Integer.parseInt(scheduledClassScheduledDate.split(" ")[1].split(":")[0]);
                int minute = Integer.parseInt(scheduledClassScheduledDate.split(" ")[1].split(":")[1]);
                calendar.set(year, month, day, hourOfDay, minute);
                calendar.add(Calendar.MINUTE, -10);

                String scheduledClassFormattedDate = String.valueOf(calendar.get(Calendar.YEAR)) + "/" +
                        Month.MonthBase1(calendar.get(Calendar.MONTH)) + "/" +
                        String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                        String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                        String.valueOf(calendar.get(Calendar.MINUTE)) + ":" +
                        String.valueOf(0) + ":" +
                        String.valueOf(0);

                String currentDate = Date.getDate();

                if (Date.compareDates(scheduledClassFormattedDate, currentDate)) {
                    String messageString = "E Classrooms open 10 minutes before the scheduled time. This class will open " + "<b>" + Date.DateFormatMMDDYYYY(scheduledClassFormattedDate) + "</b>" + " by " + "<b>" + Date.DateFormatHHMM(scheduledClassFormattedDate) + "</b>" + ".";
                    ShowDialogWithMessage.showDialogWithMessage(context, Html.fromHtml(messageString));
                    return;
                }

                try {
                    URL serverURL = new URL("https://meet.jit.si");
                    JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
                    if (!activeStudentProfilePictureURL.isEmpty()) {
                        userInfo.setAvatar(new URL(activeStudentProfilePictureURL));
                    }
                    userInfo.setDisplayName(activeStudentName);
                    JitsiMeetConferenceOptions options =
                            new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverURL)
                                    .setWelcomePageEnabled(false)
                                    .setUserInfo(userInfo)
//                                    .setFeatureFlag("meeting-name.enabled", false)
                                    .setFeatureFlag("chat.enabled", false)
                                    .setFeatureFlag("invite.enabled", false)
                                    .setFeatureFlag("meeting-password.enabled", false)
                                    .setFeatureFlag("live-streaming.enabled", false)
                                    .setFeatureFlag("recording.enabled", false)
                                    .setFeatureFlag("help.enabled", false)
                                    .setFeatureFlag("fullscreen.enabled", false)
                                    .setFeatureFlag("call-integration.enabled", false)
                                    .setRoom(scheduledClassLink)
                                    .build();
                    JitsiMeetActivity.launch(context, options);
                    mFirebaseDatabase.getReference("E Classroom Scheduled Class Participants").child(scheduledClassID).child(activeStudentID).setValue(true);
                } catch (Exception e) {

                }
            }
        });
    }

    private void loadMessagesFromFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        mDatabaseReference = mFirebaseDatabase.getReference().child("E Classroom Scheduled Class Message Board").child(scheduledClassID);
        mDatabaseReference.orderByChild("sortableDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eClassroomMessageBoardModelList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        EClassroomMessageBoardModel eClassroomMessageBoardModel = postSnapshot.getValue(EClassroomMessageBoardModel.class);
                        eClassroomMessageBoardModelList.add(eClassroomMessageBoardModel);
                    }

                    recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText(Html.fromHtml("This class doesn't have any messages on its board yet."));
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
            showDialogWithMessage(messageString);
            return;
        }

        DatabaseReference messageKeyRef = mFirebaseDatabase.getReference().child("E Classroom Scheduled Class Message Board").child(scheduledClassID).push();
        final String messageKey = messageKeyRef.getKey();
        String message;
        if (fileURL.isEmpty()) {
            message = editMessage.getText().toString().trim();
        } else {
            message = "Image";
        }

        final String senderId = activeStudentID;
        final String senderName = activeStudentName;
        final String senderProfilePictureURL = activeStudentProfilePictureURL;
        String date = Date.getDate();
        String sortableDate = Date.convertToSortableDate(date);
        final EClassroomMessageBoardModel eClassroomMessageBoardModel = new EClassroomMessageBoardModel(scheduledClassID, senderId,
                senderName, senderProfilePictureURL, fileURL, message, date, sortableDate);

        Map<String, Object> newMessageMap = new HashMap<String, Object>();
        newMessageMap.put("E Classroom Scheduled Class Message Board/" + scheduledClassID + "/" + messageKey, eClassroomMessageBoardModel);

        mDatabaseReference = mFirebaseDatabase.getReference();
        mDatabaseReference.updateChildren(newMessageMap, new DatabaseReference.CompletionListener() {
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

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUESTPPERMISSIONCODECAMERA);
        } else {
            CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(CamIntent, 0);
        }
    }

    private void GetImageFromGallery() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);
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
                    startActivityForResult(CamIntent, 0);
                } else {
                    // permission denied
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
                    // permission denied
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

                    String sortableDate = Date.convertToSortableDate(Date.getDate());
                    mStorageReference = mFirebaseStorage.getReference().child("E Classroom Scheduled Class Message Board/" + scheduledClassID + "/" + sortableDate);
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

    void showDialogWithMessage (String messageString) {
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.e_classroom_message_board_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
                    Intent i = new Intent(this, ParentMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "2");
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (parentActivity.equals("Teacher")) {
                    Intent i = new Intent(this, TeacherMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "3");
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
                bundle.putString("Fragment Int", "2");
                i.putExtras(bundle);
                startActivity(i);
            } else if (parentActivity.equals("Teacher")) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "3");
                i.putExtras(bundle);
                startActivity(i);
            }
        }
    }
}