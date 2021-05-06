package com.celerii.celerii.Activities.ELibrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EditAgeGradeActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.TagsAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.ELibraryMaterialsModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class ELibraryUploadBookActivity extends AppCompatActivity {
    Context context;
    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;
    FirebaseUser mFirebaseUser;

    Toolbar toolbar;
    LinearLayout fileLayout;
    RelativeLayout tapToUploadLayout;
    TextView tapToUploadTextView, fileName, ageGrade;
    EditText title, author, tags, approximateDuration, description;
    ImageView fileImage, deleteFile;
    RecyclerView recyclerView;
    Button upload;

    private ArrayList<String> tagList;
    public TagsAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String titleString, authorString, ageGradeString, tagString, descriptionString;
    String downloadURL;

    ArrayList<String> schoolList;

    File file;
    Uri uri;
    String fileType;
    Intent GalIntent;
    public static final int REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE  = 1000;

    String featureUseKey = "";
    String featureName = "E Library Upload Book";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_library_upload_book);

        context = this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upload");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fileLayout = (LinearLayout) findViewById(R.id.filelayout);
        tapToUploadLayout = (RelativeLayout) findViewById(R.id.uploadrelativelayout);
        tapToUploadTextView = (TextView) findViewById(R.id.taptouploadtextview);
        fileName = (TextView) findViewById(R.id.filename);
        ageGrade = (TextView) findViewById(R.id.agegrade);
        title = (EditText) findViewById(R.id.title);
        author = (EditText) findViewById(R.id.author);
        tags = (EditText) findViewById(R.id.tags);
        approximateDuration = (EditText) findViewById(R.id.approximateduration);
        description = (EditText) findViewById(R.id.description);
        fileImage = (ImageView) findViewById(R.id.fileimage);
        deleteFile = (ImageView) findViewById(R.id.deletefile);
        upload = (Button) findViewById(R.id.upload);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        tagList = new ArrayList<>();
        schoolList = new ArrayList<>();
        tagString = "";
        mAdapter = new TagsAdapter(tagList, this);
        recyclerView.setAdapter(mAdapter);

        fileLayout.setVisibility(View.GONE);
        ageGrade.setText("0 - 2 years");
        loadSchool();

        tapToUploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileSelectionDialog();
            }
        });

        ageGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ageGradeString = ageGrade.getText().toString().trim();
                Intent intent = new Intent(context, EditAgeGradeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Age Grade", ageGradeString);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });

        tags.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    tagList.clear();
                    tagString = "";
                    mAdapter.notifyDataSetChanged();

                    String[] tagArray = tags.getText().toString().trim().split(", ");
                    if (tagArray.length > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        for (String tag : tagArray) {
                            tag = tag.trim().toLowerCase();
                            tagList.add(tag);
                        }
                        tagString = ", " + tags.getText().toString().trim();
                        mAdapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFileToFirebase();
            }
        });

        deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri = null;
                fileType = null;
                fileName.setText("");
                title.setText("");
                fileImage.setImageDrawable(null);
                fileLayout.setVisibility(View.GONE);
                tapToUploadLayout.setVisibility(View.VISIBLE);
                tapToUploadTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showFileSelectionDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_layout_select_file_for_e_library);
        LinearLayout pdf = (LinearLayout) dialog.findViewById(R.id.pdf);
        LinearLayout video = (LinearLayout) dialog.findViewById(R.id.video);
        LinearLayout audio = (LinearLayout) dialog.findViewById(R.id.audio);
        LinearLayout gallery = (LinearLayout) dialog.findViewById(R.id.gallery);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPDFFromDevice();
                dialog.dismiss();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVideoFromGallery();
                dialog.dismiss();
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAudioFromGallery();
                dialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
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

    void loadSchool() {
        Gson gson = new Gson();
        String classStudentParentJSON = sharedPreferencesManager.getClassesStudentParent();
        Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
        ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList = gson.fromJson(classStudentParentJSON, type);

        if (classesStudentsAndParentsModelList == null) {
            classesStudentsAndParentsModelList = new ArrayList<>();
        } else if (classesStudentsAndParentsModelList.size() == 0) {
            classesStudentsAndParentsModelList = new ArrayList<>();
        } else {
            for (int i = 0; i < classesStudentsAndParentsModelList.size(); i++) {
                String schoolID = classesStudentsAndParentsModelList.get(i).getSchoolID();
                if (!schoolList.contains(schoolID)) {
                    schoolList.add(schoolID);
                }
            }
        }
    }

    private void getPDFFromDevice() {
        if (ContextCompat.checkSelfPermission(ELibraryUploadBookActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(ELibraryUploadBookActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);

        } else {
            // Permission has already been granted
            Intent pdfIntent = new Intent();
            pdfIntent.setAction(Intent.ACTION_GET_CONTENT);
            pdfIntent.setType("application/pdf");
            startActivityForResult(Intent.createChooser(pdfIntent, "Select PDF"), 2);
        }
    }

    private void getVideoFromGallery() {
        if (ContextCompat.checkSelfPermission(ELibraryUploadBookActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(ELibraryUploadBookActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);

        } else {
            // Permission has already been granted
            Intent videoIntent = new Intent();
            videoIntent.setAction(Intent.ACTION_GET_CONTENT);
            videoIntent.setType("application/video");
            startActivityForResult(Intent.createChooser(videoIntent, "Select Video"), 3);
        }
    }

    private void getAudioFromGallery() {
        if (ContextCompat.checkSelfPermission(ELibraryUploadBookActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(ELibraryUploadBookActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);

        } else {
            // Permission has already been granted
            Intent audioIntent = new Intent();
            audioIntent.setAction(Intent.ACTION_GET_CONTENT);
            audioIntent.setType("application/audio");
            startActivityForResult(Intent.createChooser(audioIntent, "Select Audio Book"), 4);
        }
    }

    private void getImageFromGallery() {
        if (ContextCompat.checkSelfPermission(ELibraryUploadBookActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(ELibraryUploadBookActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);

        } else {
            // Permission has already been granted
            GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 5);
        }
    }

    private void uploadFileToFirebase() {
        if (!CheckNetworkConnectivity.isNetworkAvailable(getBaseContext())) {
            String messageString = "Your device is not connected to the internet. Check your connection and try again.";
            showDialogWithMessage(Html.fromHtml(messageString));
            return;
        }

        if (uri == null) {
            String messageString = "You have not selected a file to upload. Tap the upload button above to select a pdf, video, audio book or image to upload";
            showDialogWithMessage(Html.fromHtml(messageString));
            return;
        }

        if (!validateTitle())
            return;

        if (!validateAuthor())
            return;

        if (!validateDescription())
            return;

        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(context);
        progressDialog.show();

        mStorageReference = mFirebaseStorage.getReference().child("ELibrary/" + mFirebaseUser.getUid() + "/" + getFileName(uri));
        UploadTask uploadTask = mStorageReference.putFile(uri);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    String messageString = "We could not upload this file at this time, please try again later.";
                    showDialogWithMessage(Html.fromHtml(messageString));
                    return null;
                }

                return mStorageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadURL = "";
                    downloadURL = task.getResult().toString();

                    String date = Date.getDate();
                    String sortableDate = Date.convertToSortableDate(date);
                    String titleString = title.getText().toString().trim();
                    String authorString = author.getText().toString().trim();
                    String descriptionString = description.getText().toString().trim();
                    String approximateDurationString = approximateDuration.getText().toString().trim();
                    ageGradeString = ageGrade.getText().toString().trim();

                    String materialID = mFirebaseDatabase.getReference().child("E Library Private Materials").child("Teacher").push().getKey();

                    ELibraryMaterialsModel eLibraryMaterialsModel = new ELibraryMaterialsModel(materialID, titleString, descriptionString, authorString,
                            ageGradeString, downloadURL, "", fileType, "0", approximateDurationString, date, tagString, tagList);

                    HashMap<String, Object> newFileMap = new HashMap<>();
                    newFileMap.put("E Library Private Materials/Teacher/" + mFirebaseUser.getUid() + "/" + materialID, eLibraryMaterialsModel);
                    newFileMap.put("E Library Materials/" + materialID, eLibraryMaterialsModel);

                    for (String schoolID: schoolList) {
                        newFileMap.put("E Library Private Materials/School/" + schoolID + "/" + materialID, eLibraryMaterialsModel);
                    }

                    mDatabaseReference = mFirebaseDatabase.getReference();
                    mDatabaseReference.updateChildren(newFileMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                progressDialog.dismiss();
                                sharedPreferencesManager.setMyPicURL(downloadURL);
                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.custom_upload_successful_dialog);
                                dialog.setCancelable(false);
                                dialog.setCanceledOnTouchOutside(false);
                                TextView dialogMessage = (TextView) dialog.findViewById(R.id.dialogmessage);
                                TextView close = (TextView) dialog.findViewById(R.id.close);
                                dialog.show();

                                dialogMessage.setText("File uploaded successfully!");

                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                ShowDialogWithMessage.showDialogWithMessage(context, message);
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean validateTitle() {
        String string = title.getText().toString().trim();
        if (string.isEmpty()) {
            String messageString = "You need to enter a resource title in the title field";
            showDialogWithMessage(Html.fromHtml(messageString));
            title.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateAuthor() {
        String string = author.getText().toString().trim();
        if (string.isEmpty()) {
            String messageString = "You need to enter an author in the author field";
            showDialogWithMessage(Html.fromHtml(messageString));
            author.requestFocus();
            return false;
        }

        return true;
    }

    private boolean validateDescription() {
        String string = description.getText().toString().trim();
        if (string.isEmpty()) {
            String messageString = "You need to enter a description in the description field";
            showDialogWithMessage(Html.fromHtml(messageString));
            description.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileSelectionDialog();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
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
    protected void onResume() {
        super.onResume();
        UpdateDataFromFirebase.populateEssentials(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                ageGrade.setText(data.getStringExtra("Selected Age Grade"));
            }
        }
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    fileType = "pdf";
                    fileName.setText(getFileName(uri));
                    title.setText(getFileName(uri));
                    fileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_picture_as_pdf_purple_24));
                    fileLayout.setVisibility(View.VISIBLE);
                    tapToUploadLayout.setVisibility(View.GONE);
                    tapToUploadTextView.setVisibility(View.GONE);
                }
            }
        }
        if (requestCode == 3) {
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    fileType = "video";
                    fileName.setText(getFileName(uri));
                    title.setText(getFileName(uri));
                    fileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_movie_purple_24));
                    fileLayout.setVisibility(View.VISIBLE);
                    tapToUploadLayout.setVisibility(View.GONE);
                    tapToUploadTextView.setVisibility(View.GONE);
                }
            }
        }
        if (requestCode == 4) {
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    fileType = "audio";
                    fileName.setText(getFileName(uri));
                    title.setText(getFileName(uri));
                    fileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_audiotrack_purple_24));
                    fileLayout.setVisibility(View.VISIBLE);
                    tapToUploadLayout.setVisibility(View.GONE);
                    tapToUploadTextView.setVisibility(View.GONE);
                }
            }
        }
        if (requestCode == 5) {
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    uri = data.getData();
                    fileType = "image";
                    fileName.setText(getFileName(uri));
                    title.setText(getFileName(uri));
                    fileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_purple_24));
                    fileLayout.setVisibility(View.VISIBLE);
                    tapToUploadLayout.setVisibility(View.GONE);
                    tapToUploadTextView.setVisibility(View.GONE);
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String uriString = uri.toString();

        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        String displayName = null;

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                displayName = Date.convertToSortableDate(Date.getDate());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
        }

        return displayName;
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