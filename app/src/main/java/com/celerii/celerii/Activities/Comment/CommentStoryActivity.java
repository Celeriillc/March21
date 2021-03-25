package com.celerii.celerii.Activities.Comment;

import android.app.Dialog;
import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Events.EventDetailActivity;
import com.celerii.celerii.Activities.Events.EventsRowActivity;
import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.CommentStoryAdapter;
import com.celerii.celerii.helperClasses.Analytics;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.UpdateDataFromFirebase;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.Comment;
import com.celerii.celerii.models.NotificationBadgeModel;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.Parent;
import com.bumptech.glide.Glide;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.SubscriptionModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CommentStoryActivity extends AppCompatActivity {
    SharedPreferencesManager sharedPreferencesManager;
    Context context;

    private ArrayList<Comment> commentList;
    public RecyclerView recyclerView;
    public CommentStoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Toolbar mtoolbar;
    ClassStory story;
    String storyKey, parentActivity;

    ImageView myProfilePic;

    SwipeRefreshLayout mySwipeRefreshLayout;
    RelativeLayout errorLayout, progressLayout;
    TextView errorLayoutText;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    TextView addComment;
    ImageView sendComment;
    String comment, posterName, posterImageURL;
    int commentCounter = 0;
    Comment newComment = new Comment();

    String featureUseKey = "";
    String featureName = "Comment";
    long sessionStartTime = 0;
    String sessionDurationInSeconds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_story);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        context = this;

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        posterName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        posterImageURL = sharedPreferencesManager.getMyPicURL();

        Bundle bundle = getIntent().getExtras();
        storyKey = bundle.getString("postKey");
        parentActivity = bundle.getString("parentActivity");
        if (parentActivity != null) {
            if (!parentActivity.isEmpty()) {
                sharedPreferencesManager.setActiveAccount(parentActivity);
                mDatabaseReference = mFirebaseDatabase.getReference("UserRoles");
                mDatabaseReference.child(sharedPreferencesManager.getMyUserID()).child("role").setValue(parentActivity);
            }
        }

        mtoolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        addComment = (TextView) findViewById(R.id.messageedittext);
        sendComment = (ImageView) findViewById(R.id.sendMessageButton);
        myProfilePic = (ImageView) findViewById(R.id.posterpic);
        addComment.setEnabled(false);
        sendComment.setEnabled(false);
        sendComment.setAlpha(0.4f);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (RelativeLayout) findViewById(R.id.errorlayout);
        errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        progressLayout = (RelativeLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        story = new ClassStory();
        commentList = new ArrayList<>();
        commentList.add(new Comment());

        mAdapter = new CommentStoryAdapter(commentList, story, this);
        recyclerView.setAdapter(mAdapter);
        loadCommentsFromFirebase();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadCommentsFromFirebase();
                    }
                }
        );

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    showDialogWithMessage("Your device is not connected to the internet. Check your connection and try again.");
                    return;
                }

                comment = addComment.getText().toString().trim();
                if (TextUtils.isEmpty(comment)){ return; }

                DatabaseReference commentKey = mFirebaseDatabase.getReference("ClassStoryComment").child(storyKey).push();
                String pushKey = commentKey.getKey();

                String time = Date.getDate();
                String sorttableTime = Date.convertToSortableDate(time);
                newComment = new Comment(posterImageURL, posterName, auth.getCurrentUser().getUid(),
                        time, comment, pushKey, sharedPreferencesManager.getActiveAccount());
                NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), story.getPosterID(), "Teacher", "Parent", time, sorttableTime, pushKey, "Comment", story.getImageURL(), "", false);

                Map<String, Object> newCommentMap = new HashMap<String, Object>();
                newCommentMap.put("ClassStoryComment/" + storyKey + "/" + pushKey, newComment);
                newCommentMap.put("ClassStory/" + storyKey + "/comment", newComment);

                if (!auth.getCurrentUser().getUid().equals(story.getPosterID())) {
                    if (story.getPosterAccountType().equals("Teacher")) {
                        newCommentMap.put("NotificationTeacher/" + story.getPosterID() + "/" + pushKey, notificationModel);
                        newCommentMap.put("Notification Badges/Teachers/" + story.getPosterID() + "/Notifications/status", true);
                        DatabaseReference updateBottomNotificationBadgeRef = mFirebaseDatabase.getReference("Notification Badges/Teachers/" + story.getPosterID() + "/Notifications/number");
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
                    } else if (story.getPosterAccountType().equals("School")) {
                        newCommentMap.put("NotificationSchool/" + story.getPosterID() + "/" + pushKey, notificationModel);
                        newCommentMap.put("Notification Badges/Schools/" + story.getPosterID() + "/Notifications/status", true);
                        DatabaseReference updateBottomNotificationBadgeRef = mFirebaseDatabase.getReference("Notification Badges/Schools/" + story.getPosterID() + "/Notifications/number");
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
                    }
                }

                DatabaseReference updateLikeRef = mFirebaseDatabase.getReference("ClassStory/" + storyKey + "/" + "numberOfComments");
                updateLikeRef.runTransaction(new Transaction.Handler() {
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

                DatabaseReference commentRef = mFirebaseDatabase.getReference();
                commentRef.updateChildren(newCommentMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        hideKeyboard();
                        recyclerView.scrollToPosition(0);
//                        CustomToast.primaryBackgroundToast(CommentStoryActivity.this, "Your comment has been added");
                        story.setNumberOfComments(story.getNumberOfComments() + 1);
                        commentList.add(1, newComment);
                        mAdapter.notifyDataSetChanged();
                        addComment.setText(null);
                    }
                });
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadCommentsFromFirebase() {
        Drawable textDrawable;
        String myName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        if (!myName.trim().isEmpty()) {
            String[] nameArray = myName.replaceAll("\\s+", " ").trim().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
            }
            myProfilePic.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
        }

        if (!sharedPreferencesManager.getMyPicURL().isEmpty()) {
            Glide.with(this)
                    .load(sharedPreferencesManager.getMyPicURL())
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(myProfilePic);
        }

        if (!CheckNetworkConnectivity.isNetworkAvailable(this)) {
            mySwipeRefreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorLayoutText.setText("Your device is not connected to the internet. Check your connection and try again.");
            return;
        }

        DatabaseReference bottomNavBadgeRef = mFirebaseDatabase.getReference();
        HashMap<String, Object> bottomNavBadgeMap = new HashMap<String, Object>();
        NotificationBadgeModel notificationBadgeModel = new NotificationBadgeModel(false, 0);
        bottomNavBadgeMap.put("Notification Badges/" + sharedPreferencesManager.getActiveAccount() + "/" + mFirebaseUser.getUid() + "/ClassStory", notificationBadgeModel);
        bottomNavBadgeRef.updateChildren(bottomNavBadgeMap);

        commentCounter = 0;
        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory/" + storyKey);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ClassStory storyInstance = dataSnapshot.getValue(ClassStory.class);

                    story.setNoOfLikes(storyInstance.getNoOfLikes());
                    story.setNumberOfComments(storyInstance.getNumberOfComments());
                    story.setProfilePicURL(storyInstance.getProfilePicURL());
                    story.setPosterName(storyInstance.getPosterName());
                    story.setClassReciepient(storyInstance.getClassReciepient());
                    story.setDate(storyInstance.getDate());
                    story.setStory(storyInstance.getStory());
                    story.setImageURL(storyInstance.getImageURL());
                    story.setPostID(storyInstance.getPostID());
                    story.setPosterID(storyInstance.getPosterID());
                    story.setLiked(storyInstance.isLiked());
                    story.setDate(storyInstance.getDate());
                    story.setUrl(storyInstance.getUrl());
                    story.setClassReciepients(storyInstance.getClassReciepients());
                    mAdapter.notifyDataSetChanged();

                    mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryComment/" + storyKey);
                    mDatabaseReference.orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            commentList.clear();
                            mAdapter.notifyDataSetChanged();
                            if (dataSnapshot.exists()) {
                                final int childrenCount = (int) dataSnapshot.getChildrenCount();
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    final Comment comment = postSnapshot.getValue(Comment.class);
                                    String commenterID = comment.getPosterID();

                                    if (comment.getAccountType().equals("Parent") || comment.getAccountType().equals("Teacher")) {
                                        mDatabaseReference = mFirebaseDatabase.getReference("Parent/" + commenterID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                commentCounter++;
                                                if (dataSnapshot.exists()){
                                                    Parent user = dataSnapshot.getValue(Parent.class);
                                                    String name = user.getFirstName() + " " + user.getLastName();
                                                    String profilePicURL = user.getProfilePicURL();

                                                    comment.setPosterName(name);
                                                    comment.setPosterPic(profilePicURL);

                                                    commentList.add(comment);
                                                }

                                                if (commentCounter == childrenCount) {
                                                    addComment.setEnabled(true);
                                                    sendComment.setEnabled(true);
                                                    sendComment.setAlpha(1f);
                                                    if (commentList.size() > 1) {
                                                        Collections.sort(commentList, new Comparator<Comment>() {
                                                            @Override
                                                            public int compare(Comment o1, Comment o2) {
                                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                            }
                                                        });
                                                    }
                                                    Collections.reverse(commentList);
                                                    commentList.add(0, new Comment());
                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                                mySwipeRefreshLayout.setRefreshing(false);
                                                recyclerView.setVisibility(View.GONE);
                                                progressLayout.setVisibility(View.GONE);
                                                errorLayout.setVisibility(View.VISIBLE);
                                                errorLayoutText.setText(message);
                                                return;
                                            }
                                        });
                                    } else {
                                        mDatabaseReference = mFirebaseDatabase.getReference("School/" + commenterID);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                commentCounter++;
                                                if (dataSnapshot.exists()){
                                                    School school = dataSnapshot.getValue(School.class);
                                                    String name = school.getSchoolName();
                                                    String profilePicURL = school.getProfilePhotoUrl();

                                                    comment.setPosterName(name);
                                                    comment.setPosterPic(profilePicURL);
                                                    commentList.add(comment);
                                                }

                                                if (commentCounter == childrenCount) {
                                                    addComment.setEnabled(true);
                                                    sendComment.setEnabled(true);
                                                    sendComment.setAlpha(1f);
                                                    if (commentList.size() > 1) {
                                                        Collections.sort(commentList, new Comparator<Comment>() {
                                                            @Override
                                                            public int compare(Comment o1, Comment o2) {
                                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                            }
                                                        });
                                                    }
                                                    Collections.reverse(commentList);
                                                    commentList.add(0, new Comment());
                                                    mAdapter.notifyDataSetChanged();
                                                    mySwipeRefreshLayout.setRefreshing(false);
                                                    progressLayout.setVisibility(View.GONE);
                                                    errorLayout.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                                mySwipeRefreshLayout.setRefreshing(false);
                                                recyclerView.setVisibility(View.GONE);
                                                progressLayout.setVisibility(View.GONE);
                                                errorLayout.setVisibility(View.VISIBLE);
                                                errorLayoutText.setText(message);
                                                return;
                                            }
                                        });
                                    }

                                }
                            } else {
                                addComment.setEnabled(true);
                                sendComment.setEnabled(true);
                                sendComment.setAlpha(1f);
                                commentList.add(0, new Comment());
                                mAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                                progressLayout.setVisibility(View.GONE);
                                errorLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                            mySwipeRefreshLayout.setRefreshing(false);
                            recyclerView.setVisibility(View.GONE);
                            progressLayout.setVisibility(View.GONE);
                            errorLayout.setVisibility(View.VISIBLE);
                            errorLayoutText.setText(message);
                            return;
                        }
                    });
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayoutText.setText("This post appears to have been deleted by the user or pulled down for offensive content");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                mySwipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                errorLayoutText.setText(message);
                return;
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
                    bundle.putString("Fragment Int", "0");
                    i.putExtras(bundle);
                    startActivity(i);
                } else if (parentActivity.equals("Teacher")) {
                    Intent i = new Intent(this, TeacherMainActivityTwo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Fragment Int", "1");
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
                bundle.putString("Fragment Int", "0");
                i.putExtras(bundle);
                startActivity(i);
            } else if (parentActivity.equals("Teacher")) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
                Bundle bundle = new Bundle();
                bundle.putString("Fragment Int", "1");
                i.putExtras(bundle);
                startActivity(i);
            }
        }
    }

    void showDialogWithMessage (String messageString) {
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

    @Override
    protected void onResume() {
        super.onResume();
        UpdateDataFromFirebase.populateEssentials(this);
    }
}
