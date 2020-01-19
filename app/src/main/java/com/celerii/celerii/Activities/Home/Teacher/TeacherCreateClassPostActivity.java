package com.celerii.celerii.Activities.Home.Teacher;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ClassListAdapterHorizontal;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.ClassStoryNotification;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.NotificationModel;
import com.bumptech.glide.Glide;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TeacherCreateClassPostActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;
    Context context = this;
    String UID;

    Toolbar toolbar;
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

    private ArrayList<Class> classList;
    private ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList;
    private ArrayList<String> classListString, parentList, studentList, downloadURLs;
    private ArrayList<Bitmap> bitmaps;
    public RecyclerView recyclerView;
    public ClassListAdapterHorizontal mAdapter;
    LinearLayoutManager mLayoutManager;
    ClassStory classStory;
    NotificationModel notificationModelParent, notificationModelSchool;
    ProgressBar progressBar;
    ScrollView superLayout;

    HorizontalScrollView imageContainer;
    RelativeLayout imageLayoutOne, imageLayoutTwo, imageLayoutThree, imageLayoutFour, imageLayoutFive, imageLayoutSix, imageLayoutSeven, imageLayoutEight, imageLayoutNine, imageLayoutTen;
    ImageView storyImageOne, storyImageTwo, storyImageThree, storyImageFour, storyImageFive, storyImageSix, storyImageSeven, storyImageEight, storyImageNine, storyImageTen;
    ImageView iconOne, iconTwo, iconThree, iconFour, iconFive, iconSix, iconSeven, iconEight, iconNine, iconTen;

    ImageView posterPic;
    EditText classPost;
    TextView posterName, maxCharacters, chooseClassToPostToDescriptor, noDataLayout;
    LinearLayout recyclerViewLayout;

    String story, date, sortableDate, dateDue, posterID, imageURL, url, posterNameString, posterProfilePicURL;
    List<String> classReciepients = new ArrayList<String>();
    List<String> parentReciepients = new ArrayList<>();
    List<String> schoolReciepients = new ArrayList<>();
    int maxNumberOfCharacters = 500;
    Button post;
    HashMap<String, ArrayList<String>> classParentMap;
    HashMap<String, ArrayList<String>> classSchoolMap;
    String classReciepientString = "", downloadURLString = "";
    CustomProgressDialogOne progressDialog;

    boolean isClassLoaded, isParentsLoaded, isTextNotEmpty, isTeacherInfoLoaded;
    int downloadURLCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_create_class_post);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        sharedPreferencesManager = new SharedPreferencesManager(this);
        progressDialog = new CustomProgressDialogOne(this);
        UID = sharedPreferencesManager.getMyUserID();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        posterPic = (ImageView) findViewById(R.id.posterpic);
        posterName = (TextView) findViewById(R.id.postername);
        classPost = (EditText) findViewById(R.id.classpost);
        maxCharacters = (TextView) findViewById(R.id.maxcharacters);
        chooseClassToPostToDescriptor = (TextView) findViewById(R.id.chooseclasstoposttodescriptor);
        noDataLayout = (TextView) findViewById(R.id.nodatalayout);
        recyclerViewLayout = (LinearLayout) findViewById(R.id.recycler_view_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        superLayout = (ScrollView) findViewById(R.id.superlayout);
        post = (Button) findViewById(R.id.post);

        imageContainer = (HorizontalScrollView) findViewById(R.id.imagecontainer);

        imageLayoutOne = (RelativeLayout) findViewById(R.id.imagelayoutone);
        imageLayoutTwo = (RelativeLayout) findViewById(R.id.imagelayouttwo);
        imageLayoutThree = (RelativeLayout) findViewById(R.id.imagelayoutthree);
        imageLayoutFour = (RelativeLayout) findViewById(R.id.imagelayoutfour);
        imageLayoutFive = (RelativeLayout) findViewById(R.id.imagelayoutfive);
        imageLayoutSix = (RelativeLayout) findViewById(R.id.imagelayoutsix);
        imageLayoutSeven = (RelativeLayout) findViewById(R.id.imagelayoutseven);
        imageLayoutEight = (RelativeLayout) findViewById(R.id.imagelayouteight);
        imageLayoutNine = (RelativeLayout) findViewById(R.id.imagelayoutnine);
        imageLayoutTen = (RelativeLayout) findViewById(R.id.imagelayoutten);

        storyImageOne = (ImageView) findViewById(R.id.storyimageone);
        storyImageTwo = (ImageView) findViewById(R.id.storyimagetwo);
        storyImageThree = (ImageView) findViewById(R.id.storyimagethree);
        storyImageFour = (ImageView) findViewById(R.id.storyimagefour);
        storyImageFive = (ImageView) findViewById(R.id.storyimagefive);
        storyImageSix = (ImageView) findViewById(R.id.storyimagesix);
        storyImageSeven = (ImageView) findViewById(R.id.storyimageseven);
        storyImageEight = (ImageView) findViewById(R.id.storyimageeight);
        storyImageNine = (ImageView) findViewById(R.id.storyimagenine);
        storyImageTen = (ImageView) findViewById(R.id.storyimageten);

        iconOne = (ImageView) findViewById(R.id.iconone);
        iconTwo = (ImageView) findViewById(R.id.icontwo);
        iconThree = (ImageView) findViewById(R.id.iconthree);
        iconFour = (ImageView) findViewById(R.id.iconfour);
        iconFive = (ImageView) findViewById(R.id.iconfive);
        iconSix = (ImageView) findViewById(R.id.iconsix);
        iconSeven = (ImageView) findViewById(R.id.iconseven);
        iconEight = (ImageView) findViewById(R.id.iconeight);
        iconNine = (ImageView) findViewById(R.id.iconnine);
        iconTen = (ImageView) findViewById(R.id.iconten);

        imageLayoutOne.setClipToOutline(true);
        imageLayoutTwo.setClipToOutline(true);
        imageLayoutThree.setClipToOutline(true);
        imageLayoutFour.setClipToOutline(true);
        imageLayoutFive.setClipToOutline(true);
        imageLayoutSix.setClipToOutline(true);
        imageLayoutSeven.setClipToOutline(true);
        imageLayoutEight.setClipToOutline(true);
        imageLayoutNine.setClipToOutline(true);
        imageLayoutTen.setClipToOutline(true);

        imageLayoutOne.setVisibility(View.VISIBLE);
        imageLayoutTwo.setVisibility(View.GONE);
        imageLayoutThree.setVisibility(View.GONE);
        imageLayoutFour.setVisibility(View.GONE);
        imageLayoutFive.setVisibility(View.GONE);
        imageLayoutSix.setVisibility(View.GONE);
        imageLayoutSeven.setVisibility(View.GONE);
        imageLayoutEight.setVisibility(View.GONE);
        imageLayoutNine.setVisibility(View.GONE);
        imageLayoutTen.setVisibility(View.GONE);

        classPost.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxNumberOfCharacters)});

        posterNameString = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        posterProfilePicURL = sharedPreferencesManager.getMyPicURL();

        Glide.with(this)
                .load(posterProfilePicURL)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(this))
                .into(posterPic);

        posterName.setText(posterNameString);
        noDataLayout.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        classList = new ArrayList<>();
        classListString = new ArrayList<>();
        parentList = new ArrayList<>();
        studentList = new ArrayList<>();
        downloadURLs = new ArrayList<>();
        bitmaps = new ArrayList<>();
        classParentMap = new HashMap<String, ArrayList<String>>();
        classSchoolMap = new HashMap<String, ArrayList<String>>();
        loadClasses();
        mAdapter = new ClassListAdapterHorizontal(classList, this);
        recyclerView.setAdapter(mAdapter);

        classPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int presentNumOfChars = classPost.getText().length();
                int remainingNumOfChars = maxNumberOfCharacters - presentNumOfChars;
                maxCharacters.setText(String.valueOf(remainingNumOfChars));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int presentNumOfChars = classPost.getText().length();
                int remainingNumOfChars = maxNumberOfCharacters - presentNumOfChars;
                maxCharacters.setText(String.valueOf(remainingNumOfChars));
            }

            @Override
            public void afterTextChanged(Editable s) {
                int presentNumOfChars = classPost.getText().length();
                int remainingNumOfChars = maxNumberOfCharacters - presentNumOfChars;
                maxCharacters.setText(String.valueOf(remainingNumOfChars));
            }
        });

        storyImageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageOne.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        storyImageTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageTwo.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        storyImageThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageThree.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        storyImageFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageFour.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        storyImageFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageFive.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        storyImageSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageSix.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        storyImageSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageSeven.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        storyImageEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageEight.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        storyImageNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageNine.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        storyImageTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyImageTen.getDrawable() == null) {
                    addNewImage();
                }
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    showDialogWithMessage("Your device is not connected to the internet. Check your connection and try again.");
                    return;
                }

                story = classPost.getText().toString().trim();
                if (TextUtils.isEmpty(story)) {
                    showDialogWithMessage("You have not told us what's happening in your class yet.");
                    return;
                }

                if (classList.size() == 0) {
                    showDialogWithMessage("You're not connected to any classes yet. Use the search button to search for a school and request connection to their classes.");
                    return;
                }

                String replaceWithLastClass = "";
                String replaceLastClass = "";
                String replaceLastClassSingle = "";
                String lastClass = "";
                for (int i = 0; i < classList.size(); i++){
                    if (classList.get(i).isTicked()){
                        classReciepients.add(classList.get(i).getID());
                        if (classParentMap.containsKey(classList.get(i).getID())) {
                            parentReciepients.addAll(classParentMap.get(classList.get(i).getID()));
                        }
                        if (classSchoolMap.containsKey(classList.get(i).getID())) {
                            schoolReciepients.addAll(classSchoolMap.get(classList.get(i).getID()));
                        }
                        replaceWithLastClass = " and " + classList.get(i).getClassName();
                        replaceLastClass = ", " + classList.get(i).getClassName() + ",";
                        replaceLastClassSingle = classList.get(i).getClassName() + ", ";
                        lastClass = classList.get(i).getClassName();
                        classReciepientString = classReciepientString + classList.get(i).getClassName() + ", ";
                    }
                }

                classReciepientString = classReciepientString.replace(replaceLastClass, replaceWithLastClass);
                classReciepientString = classReciepientString.replace(replaceLastClassSingle, lastClass);
                downloadURLCounter = 0;

                if (classReciepients.size() < 1) {
                    showDialogWithMessage("You need to select at least one class to post a story to.");
                    return;
                }

                date = Date.getDate();
                sortableDate = Date.convertToSortableDate(date);
                posterID = UID;
                url = "";
                progressDialog.show();

                uploadStory();
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

    void loadClasses(){
        Set<String> classSet = sharedPreferencesManager.getMyClasses();
        String classStudentParentJSON = sharedPreferencesManager.getClassesStudentParent();
        Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
        ArrayList<String> classes = new ArrayList<>();
        if (classSet != null) { classes = new ArrayList<>(classSet); }
        Gson gson = new Gson();
        classesStudentsAndParentsModelList = new ArrayList<>();
        classesStudentsAndParentsModelList.clear();
        classList.clear();
        classListString.clear();

        classesStudentsAndParentsModelList = gson.fromJson(classStudentParentJSON, type);
        if (classes.size() > 0) {
            for (int i = 0; i < classes.size(); i++) {
                String[] classInfo = classes.get(i).split(" ");
                Class classModel = new Class(classInfo[1], classInfo[2], classInfo[0], true);
                classList.add(classModel);
                classListString.add(classInfo[0]);
            }
        }

        if (classesStudentsAndParentsModelList == null) {
            classesStudentsAndParentsModelList = new ArrayList<>();
        } else {
            for (int i = 0; i < classesStudentsAndParentsModelList.size(); i++) {
                 if (classParentMap.containsKey(classesStudentsAndParentsModelList.get(i).getClassID())) {
                     if (!classParentMap.get(classesStudentsAndParentsModelList.get(i).getClassID()).contains(classesStudentsAndParentsModelList.get(i).getParentID())) {
                         classParentMap.get(classesStudentsAndParentsModelList.get(i).getClassID()).add(classesStudentsAndParentsModelList.get(i).getParentID());
                     }
                 } else {
                     classParentMap.put(classesStudentsAndParentsModelList.get(i).getClassID(), new ArrayList<String>());
                     classParentMap.get(classesStudentsAndParentsModelList.get(i).getClassID()).add(classesStudentsAndParentsModelList.get(i).getParentID());
                 }

                 if (classSchoolMap.containsKey(classesStudentsAndParentsModelList.get(i).getClassID())) {
                     if (!classSchoolMap.get(classesStudentsAndParentsModelList.get(i).getClassID()).contains(classesStudentsAndParentsModelList.get(i).getSchoolID())) {
                         classSchoolMap.get(classesStudentsAndParentsModelList.get(i).getClassID()).add(classesStudentsAndParentsModelList.get(i).getSchoolID());
                     }
                 } else {
                     classSchoolMap.put(classesStudentsAndParentsModelList.get(i).getClassID(), new ArrayList<String>());
                     classSchoolMap.get(classesStudentsAndParentsModelList.get(i).getClassID()).add(classesStudentsAndParentsModelList.get(i).getSchoolID());
                 }
            }
        }
    }

    public void uploadStory(){

        try {
            for (int i = 0; i < bitmaps.size(); i++) {
                Bitmap bitmap = bitmaps.get(i);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                mStorageReference = mFirebaseStorage.getReference().child("ClassStory/" + UID + "/" + sortableDate + "_" + i + ".jpg");
                UploadTask uploadTask = mStorageReference.putBytes(byteArray);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadURLCounter++;
                        String downloadURL = "";
                        downloadURL = taskSnapshot.getDownloadUrl().toString();

                        downloadURLs.add(downloadURL);
                        downloadURLString = downloadURLString + downloadURL + " ";

                        if (downloadURLCounter == bitmaps.size()) {
                            downloadURLString.trim();
                            classStory = new ClassStory(story, date, sortableDate, "Teacher", posterID, posterNameString, posterProfilePicURL, classReciepientString, downloadURLString, url);
                            classStory.setClassReciepients(classReciepients);
                            notificationModelParent = new NotificationModel(mFirebaseUser.getUid(), "", "Parent", "Teacher", date, sortableDate, "", "ClassPost", imageURL, classReciepientString, false);
                            notificationModelSchool = new NotificationModel(mFirebaseUser.getUid(), "", "School", "Teacher", date, sortableDate, "", "ClassPost", imageURL, classReciepientString, false);

                            DatabaseReference newStoryR = mDatabaseReference.child("ClassStory").push();
                            String pushID = newStoryR.getKey();
                            DatabaseReference newStoryRef = mFirebaseDatabase.getReference();
                            classStory.setPostID(pushID);
                            notificationModelParent.setActivityID(pushID);
                            notificationModelSchool.setActivityID(pushID);

                            Map<String, Object> newStory = new HashMap<String, Object>();
                            newStory.put("ClassStory/" + pushID, classStory);
                            newStory.put("ClassStoryTeacherTimeline/" + posterID + "/" + pushID, false);
                            newStory.put("ClassStoryTeacherFeed/" + posterID + "/" + pushID, false);

                            for (int i = 0; i < classReciepients.size(); i++){
                                String classStoryReciepientsPush = "ClassStoryReciepients/" + pushID + "/";
                                newStory.put(classStoryReciepientsPush + classReciepients.get(i), true);
                                newStory.put("ClassStoryClass/" + classReciepients.get(i) + "/" + pushID, true);
                            }
                            for (int i = 0; i < schoolReciepients.size(); i++) {
                                newStory.put("ClassStorySchoolFeed/" + schoolReciepients.get(i) + "/" + pushID, false);
                                notificationModelSchool.setToID(schoolReciepients.get(i));
                                newStory.put("NotificationSchool/" + schoolReciepients.get(i) + "/" + pushID, notificationModelSchool);
                                newStory.put("Notification Badges/Schools/" + schoolReciepients.get(i) + "/ClassStory/status", true);
                            }
                            for (int i = 0; i < parentReciepients.size(); i++){
                                newStory.put("ClassStoryParentFeed/" + parentReciepients.get(i) + "/" + pushID, false);
                                notificationModelParent.setToID(parentReciepients.get(i));
                                newStory.put("NotificationParent/" + parentReciepients.get(i) + "/" + pushID, notificationModelParent);
                                newStory.put("Notification Badges/Parents/" + parentReciepients.get(i) + "/ClassStory/status", true);
                            }

                            newStoryRef.updateChildren(newStory, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        progressDialog.dismiss();
                                        CustomToast.blueBackgroundToast(context, "Your class story has been posted");
                                        finish();
                                    } else{
                                        progressDialog.dismiss();
                                        CustomToast.blueBackgroundToast(context, "Your class story could not be posted, try again");
                                    }
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        String messageString = "An error occured while uploading your story, please try again";
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
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            String messageString = "An error occured while uploading your story, please try again";
            showDialogWithMessage((messageString));
            Log.d("Upload Story", e.getMessage());
        }
    }

    void showDialogWithMessage (String messageString) {
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

    void addNewImage() {
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

    private void GetImageFromGallery() {
        if (ContextCompat.checkSelfPermission(TeacherCreateClassPostActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(TeacherCreateClassPostActivity.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUESTPPERMISSIONCODEWRITEEXTERNALSTORAGE);

        } else {
            // Permission has already been granted
            GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 1);
        }
    }

    private void ClickImageFromCamera() {
        if (ContextCompat.checkSelfPermission(TeacherCreateClassPostActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(TeacherCreateClassPostActivity.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    REQUESTPPERMISSIONCODECAMERA);

        } else {
            CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/ClassFeedPicture");

            if(!directory.exists() && !directory.isDirectory()) {
                if (directory.mkdirs()) {
                    file = new File(directory, "CeleriiClassFeedPicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                } else {
                    file = new File(directory, "CeleriiClassFeedPicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                }
            } else {
                file = new File(directory, "CeleriiClassFeedPicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            }
            uri = Uri.fromFile(file);
            CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
            CamIntent.putExtra("return-data", true);
            startActivityForResult(CamIntent, 0);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUESTPPERMISSIONCODECAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"Celerii/Images/ClassFeedPicture");

                    if(!directory.exists() && !directory.isDirectory()) {
                        if (directory.mkdirs()) {
                            file = new File(directory, "CeleriiClassFeedPicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        } else {
                            file = new File(directory, "CeleriiClassFeedPicture" + "_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
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
                    Bitmap bitmap = data.getExtras().getParcelable("data");
                    loadImageViews(bitmap);
                } catch (Exception e){
                    //tODO:
                }
            }
        }
    }

    void loadImageViews(Bitmap bitmap) {
        if (storyImageOne.getDrawable() == null) {
            storyImageOne.setImageDrawable(null);
            storyImageOne.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            animateOn(imageLayoutTwo);
            iconOne.setVisibility(View.GONE);
            imageLayoutTwo.setVisibility(View.VISIBLE);
        } else if (storyImageTwo.getDrawable() == null) {
            storyImageTwo.setImageDrawable(null);
            storyImageTwo.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            animateOn(imageLayoutThree);
            iconTwo.setVisibility(View.GONE);
            imageLayoutThree.setVisibility(View.VISIBLE);
        } else if (storyImageThree.getDrawable() == null) {
            storyImageThree.setImageDrawable(null);
            storyImageThree.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            animateOn(imageLayoutFour);
            iconThree.setVisibility(View.GONE);
            imageLayoutFour.setVisibility(View.VISIBLE);
        } else if (storyImageFour.getDrawable() == null) {
            storyImageFour.setImageDrawable(null);
            storyImageFour.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            animateOn(imageLayoutFive);
            iconFour.setVisibility(View.GONE);
            imageLayoutFive.setVisibility(View.VISIBLE);
        } else if (storyImageFive.getDrawable() == null) {
            storyImageFive.setImageDrawable(null);
            storyImageFive.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            animateOn(imageLayoutSix);
            iconFive.setVisibility(View.GONE);
            imageLayoutSix.setVisibility(View.VISIBLE);
        } else if (storyImageSix.getDrawable() == null) {
            storyImageSix.setImageDrawable(null);
            storyImageSix.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            animateOn(imageLayoutSeven);
            iconSix.setVisibility(View.GONE);
            imageLayoutSeven.setVisibility(View.VISIBLE);
        } else if (storyImageSeven.getDrawable() == null) {
            storyImageSeven.setImageDrawable(null);
            storyImageSeven.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            animateOn(imageLayoutEight);
            iconSeven.setVisibility(View.GONE);
            imageLayoutEight.setVisibility(View.VISIBLE);
        } else if (storyImageEight.getDrawable() == null) {
            storyImageEight.setImageDrawable(null);
            storyImageEight.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            animateOn(imageLayoutNine);
            iconEight.setVisibility(View.GONE);
            imageLayoutNine.setVisibility(View.VISIBLE);
        } else if (storyImageNine.getDrawable() == null) {
            storyImageNine.setImageDrawable(null);
            storyImageNine.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            animateOn(imageLayoutTen);
            iconNine.setVisibility(View.GONE);
            imageLayoutTen.setVisibility(View.VISIBLE);
        } else if (storyImageTen.getDrawable() == null) {
            storyImageTen.setImageDrawable(null);
            storyImageTen.setImageBitmap(bitmap);
            bitmaps.add(bitmap);
            iconTen.setVisibility(View.GONE);
        }
    }

    public void animateOn(final RelativeLayout view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.15f, 0.0f, 1.15f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimationOn(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimationOn(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(300);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    private Animation prepareAnimationOn(Animation animation){
        animation.setRepeatCount(0);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}
