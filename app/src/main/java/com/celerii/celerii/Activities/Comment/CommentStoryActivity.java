package com.celerii.celerii.Activities.Comment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.CommentStoryAdapter;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.Comment;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.Parent;
import com.bumptech.glide.Glide;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CommentStoryActivity extends AppCompatActivity {

    private ArrayList<Comment> commentList;
    public RecyclerView recyclerView;
    public CommentStoryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Toolbar mtoolbar;
    ClassStory story;
    String storyKey;

    ImageView myProfilePic;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    TextView addComment;
    Button sendComment;
    String comment, posterName, posterImageURL;

    SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_story);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        posterName = sharedPreferencesManager.getMyFirstName() + " " + sharedPreferencesManager.getMyLastName();
        posterImageURL = sharedPreferencesManager.getMyPicURL();

        Bundle bundle = getIntent().getExtras();
        storyKey = bundle.getString("postKey");

        mtoolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        addComment = (TextView) findViewById(R.id.messageedittext);
        sendComment = (Button) findViewById(R.id.sendMessageButton);
        myProfilePic = (ImageView) findViewById(R.id.posterpic);
        addComment.setEnabled(false);
        sendComment.setEnabled(false);
        sendComment.setAlpha(0.4f);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        story = new ClassStory();
        commentList = new ArrayList<>();
        commentList.add(new Comment());

        loadCommentsFromFirebase();
        mAdapter = new CommentStoryAdapter(commentList, story, this);
        recyclerView.setAdapter(mAdapter);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

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
                comment = addComment.getText().toString();

                if (TextUtils.isEmpty(comment)){ return; }

                DatabaseReference commentKey = mFirebaseDatabase.getReference("ClassStoryComment").child(storyKey).push();
                String pushKey = commentKey.getKey();
                DatabaseReference commentRef = mFirebaseDatabase.getReference();

                Calendar calendar = Calendar.getInstance();
                String time = String.valueOf(calendar.get(Calendar.YEAR)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "/" +
                        String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar.get(Calendar.MINUTE))
                        + ":" + String.valueOf(calendar.get(Calendar.SECOND)) + ":" + String.valueOf(calendar.get(Calendar.MILLISECOND));
                String sorttableTime = Date.convertToSortableDate(time);
                Comment newComment = new Comment(posterImageURL, posterName, auth.getCurrentUser().getUid(),
                        time, comment, pushKey, sharedPreferencesManager.getActiveAccount());

                NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), story.getPosterID(), "Teacher", "Parent", time, sorttableTime, pushKey, "Comment", story.getImageURL(), "", false);

                Map<String, Object> newCommentMap = new HashMap<String, Object>();
                newCommentMap.put("ClassStoryComment/" + storyKey + "/" + pushKey, newComment);
                newCommentMap.put("ClassStory/" + storyKey + "/comment", newComment);
                if (!auth.getCurrentUser().getUid().equals(story.getPosterID())) {
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
                }
                commentRef.updateChildren(newCommentMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        recyclerView.scrollToPosition(0);
                        CustomToast.blueBackgroundToast(CommentStoryActivity.this, "Your comment has been added");
                        addComment.setText(null);

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
                    }
                });
            }
        });
    }

    private void loadCommentsFromFirebase() {
        Glide.with(this)
                .load(sharedPreferencesManager.getMyPicURL())
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(this))
                .into(myProfilePic);

        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory/" + storyKey);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    commentList.clear();
                    commentList.add(new Comment());
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

                    //mAdapter = new CommentStoryAdapter(commentList, story, getBaseContext());
                    //recyclerView.setAdapter(mAdapter);

                    mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryComment/" + storyKey);
                    mDatabaseReference.orderByChild("time").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    final Comment comment = postSnapshot.getValue(Comment.class);
                                    String commenterID = comment.getPosterID();

                                    if (comment.getAccountType().equals("Parent")) {
                                        mDatabaseReference = mFirebaseDatabase.getReference("Parent/" + commenterID);
                                    } else {
                                        mDatabaseReference = mFirebaseDatabase.getReference("Teacher/" + commenterID);
                                    }

                                    mDatabaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                Parent user = dataSnapshot.getValue(Parent.class);
                                                String name = user.getFirstName() + " " + user.getLastName();
                                                String profilePicURL = user.getProfilePicURL();

                                                comment.setPosterName(name);
                                                comment.setPosterPic(profilePicURL);

                                                commentList.add(1, comment);
                                                mAdapter.notifyDataSetChanged();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            addComment.setEnabled(true);
                            sendComment.setEnabled(true);
                            sendComment.setAlpha(1f);
                            mySwipeRefreshLayout.setRefreshing(false);
                            progressLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    recyclerView.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
