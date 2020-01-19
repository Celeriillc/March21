package com.celerii.celerii.Activities.Profiles;

import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditProfiles.EditTeacherProfileActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.RatingAndReview.ReviewActivity;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.models.ClassStory;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Review;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Teacher;
import com.celerii.celerii.models.TeacherActivityStatistics;
import com.celerii.celerii.models.TeacherPrivacyModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TeacherProfileOneActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    Bundle bundle;

    NestedScrollView superLayout;
    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, timelineErrorLayout, reviewErrorLayout, progressLayout;
    boolean timelineShowStatus, locationShowStatus, phoneNumberShowStatus, maritalStatusShowStatus;

    Toolbar toolbar;
    LinearLayout reviewOneLayout, reviewTwoLayout, timelineLayoutOne, timelineLayout;
    ImageView profilePic, backgroundPic, reviewerPhotoOne, reviewerPhotoTwo, timelineProfilePic, timelineStoryImage;
    TextView fullNamePrimary, rating, fullName, gender, maritalStatus, location, pointsAwarded, classPost, postLikes, assignmentPosts, assignmentViews, teacherBio, schoolName, schoolAddress,
    schoolState, reviewOne, reviewTwo, reviewerOne, reviewerTwo, viewMoreReviews, viewMoreTimeline, timelineName, timelineClass, timelineDate, timelineStory, timelineLink, timelineNoOfLikes,
            headerAssignmentPosts, headerClassPosts, timelineNoOfComments;
    LinearLayout editYourProfile, contact, placeHolderLayout;
    LinearLayout fullNameLayout, genderLayout, maritalStatusLayout, locationLayout;

    String teacherID = "", teacherName = "";
    String schoolKey;
    ArrayList<Review> reviewList = new ArrayList<Review>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile_one);

        bundle = getIntent().getExtras();
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        teacherID = bundle.getString("ID");

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        superLayout = (NestedScrollView) findViewById(R.id.superlayout);
        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        timelineErrorLayout = (LinearLayout) findViewById(R.id.timelineerrorlayout);
        reviewErrorLayout = (LinearLayout) findViewById(R.id.reviewerrorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("");

        superLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        editYourProfile = (LinearLayout) findViewById(R.id.edityourprofile);
        contact = (LinearLayout) findViewById(R.id.contact);
        placeHolderLayout = (LinearLayout) findViewById(R.id.placeholderlayout);
        profilePic = (ImageView) findViewById(R.id.profilepic);
        backgroundPic = (ImageView) findViewById(R.id.backgroundimage);
        reviewerPhotoOne = (ImageView) findViewById(R.id.reviewerphoto);
        reviewerPhotoTwo = (ImageView) findViewById(R.id.reviewerphoto2);
        timelineProfilePic = (ImageView) findViewById(R.id.profilePic);
        timelineStoryImage = (ImageView) findViewById(R.id.storyimage);

        reviewOneLayout = (LinearLayout) findViewById(R.id.reviewonelayout);
        reviewTwoLayout = (LinearLayout) findViewById(R.id.reviewtwolayout);
        timelineLayoutOne= (LinearLayout) findViewById(R.id.timelinelayoutone);
        timelineLayout = (LinearLayout) findViewById(R.id.timelinelayout);
        fullNameLayout = (LinearLayout) findViewById(R.id.fullnamelayout);
        genderLayout = (LinearLayout) findViewById(R.id.genderlayout);
        maritalStatusLayout = (LinearLayout) findViewById(R.id.maritalstatuslayout);
        locationLayout = (LinearLayout) findViewById(R.id.locationlayout);

        reviewOneLayout.setVisibility(View.GONE);
        reviewTwoLayout.setVisibility(View.GONE);
        timelineLayoutOne.setVisibility(View.GONE);

        fullNamePrimary = (TextView) findViewById(R.id.fullnameprimary);
        rating = (TextView) findViewById(R.id.rating);
        fullName = (TextView) findViewById(R.id.fullname);
        gender = (TextView) findViewById(R.id.gender);
        maritalStatus = (TextView) findViewById(R.id.maritalstatus);
        location = (TextView) findViewById(R.id.location);
        pointsAwarded = (TextView) findViewById(R.id.pointsawarded);
        classPost = (TextView) findViewById(R.id.classposts);
        postLikes = (TextView) findViewById(R.id.postlikes);
        assignmentPosts = (TextView) findViewById(R.id.assignmentposts);
        assignmentViews = (TextView) findViewById(R.id.assignmentpostviews);
        teacherBio = (TextView) findViewById(R.id.bio);
        schoolName = (TextView) findViewById(R.id.schoolname);
        schoolAddress = (TextView) findViewById(R.id.locationandstate);
        schoolState = (TextView) findViewById(R.id.country);
        reviewOne = (TextView) findViewById(R.id.review);
        reviewTwo = (TextView) findViewById(R.id.review2);
        reviewerOne = (TextView) findViewById(R.id.reviewerName);
        reviewerTwo = (TextView) findViewById(R.id.reviewerName2);
        viewMoreReviews = (TextView) findViewById(R.id.viewmorereviews);
        viewMoreTimeline = (TextView) findViewById(R.id.viewmoretimeline);
        timelineName = (TextView) findViewById(R.id.name);
        timelineClass = (TextView) findViewById(R.id.classreciepient);
        timelineDate = (TextView) findViewById(R.id.timestamp);
        timelineStory = (TextView) findViewById(R.id.txtstory);
        timelineLink = (TextView) findViewById(R.id.txtUrl);
        timelineNoOfLikes = (TextView) findViewById(R.id.likenumber);
        headerAssignmentPosts = (TextView) findViewById(R.id.headerassignmentpost);
        headerClassPosts = (TextView) findViewById(R.id.headerclasspost);
        timelineNoOfComments = (TextView) findViewById(R.id.commentnumber);

        teacherBio.setVisibility(View.GONE);
        schoolName.setVisibility(View.GONE);
        schoolAddress.setVisibility(View.GONE);
        schoolState.setVisibility(View.GONE);

        if (teacherID.equals(auth.getCurrentUser().getUid())){
            editYourProfile.setVisibility(View.VISIBLE);
            contact.setVisibility(View.GONE);
            placeHolderLayout.setVisibility(View.VISIBLE);
        } else {
            editYourProfile.setVisibility(View.GONE);
            contact.setVisibility(View.VISIBLE);
            placeHolderLayout.setVisibility(View.GONE);
        }

        loadFromFirebase();

        editYourProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(TeacherProfileOneActivity.this, EditTeacherProfileActivity.class);
                startActivity(I);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewMoreReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(TeacherProfileOneActivity.this, ReviewActivity.class);
                Bundle bundle =  new Bundle();
                bundle.putString("EntityType", "Teacher");
                bundle.putString("EntityID", teacherID);
                bundle.putString("EntityName", teacherName);
                I.putExtras(bundle);
                startActivity(I);
            }
        });

        viewMoreTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadFromFirebase();
                    }
                }
        );
    }

    void loadFromFirebase(){
        mDatabaseReference = mFirebaseDatabase.getReference().child("TeacherPrivacySettings").child(teacherID);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    TeacherPrivacyModel teacherPrivacyModel = dataSnapshot.getValue(TeacherPrivacyModel.class);
                    timelineShowStatus = teacherPrivacyModel.isTimeline();
                    locationShowStatus = teacherPrivacyModel.isLocation();
                    phoneNumberShowStatus = teacherPrivacyModel.isPhoneNumber();
                    maritalStatusShowStatus = teacherPrivacyModel.isMaritalStatus();
                } else {
                    timelineShowStatus = true;
                    locationShowStatus = true;
                    phoneNumberShowStatus = true;
                    maritalStatusShowStatus = true;
                }

                mDatabaseReference = mFirebaseDatabase.getReference("Teacher").child(teacherID);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            final Teacher teacher = dataSnapshot.getValue(Teacher.class);
                            teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                            fullNamePrimary.setText(teacherName);
                            fullName.setText(teacherName);
                            getSupportActionBar().setTitle(teacherName);
                            gender.setText(teacher.getGender());
                            maritalStatus.setText(teacher.getMaritalStatus());
                            location.setText(teacher.getLocation());

                            Glide.with(getBaseContext())
                                    .load(teacher.getProfilePicURL())
                                    .placeholder(R.drawable.profileimageplaceholder)
                                    .error(R.drawable.profileimageplaceholder)
                                    .centerCrop()
                                    .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                                    .into(profilePic);

                            Glide.with(getBaseContext())
                                    .load(teacher.getProfilePicURL())
                                    .placeholder(R.drawable.profileimageplaceholder)
                                    .error(R.drawable.profileimageplaceholder)
                                    .centerCrop()
                                    .bitmapTransform(new BlurTransformation(getBaseContext(), 50))
                                    .into(backgroundPic);

                            mDatabaseReference = mFirebaseDatabase.getReference("TeacherActivityStatistics").child(teacherID);
                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        TeacherActivityStatistics teacherActivityStatistics = dataSnapshot.getValue(TeacherActivityStatistics.class);
                                        pointsAwarded.setText(String.valueOf(teacherActivityStatistics.getTotalPointsAwarded()));
                                        classPost.setText(String.valueOf(teacherActivityStatistics.getTotalClassPosts()));
                                        headerClassPosts.setText(String.valueOf(teacherActivityStatistics.getTotalClassPosts()));
                                        postLikes.setText(String.valueOf(teacherActivityStatistics.getTotalPostLikes()));
                                        assignmentPosts.setText(String.valueOf(teacherActivityStatistics.getTotalAssignmentPosts()));
                                        headerAssignmentPosts.setText(String.valueOf(teacherActivityStatistics.getTotalAssignmentPosts()));
                                        assignmentViews.setText(String.valueOf(teacherActivityStatistics.getTotalAssignmentViews()));
                                    }

                                    else {
                                        pointsAwarded.setText("0");
                                        classPost.setText("0");
                                        headerClassPosts.setText("0");
                                        postLikes.setText("0");
                                        assignmentPosts.setText("0");
                                        headerAssignmentPosts.setText("0");
                                        assignmentViews.setText("0");
                                    }

                                    mDatabaseReference = mFirebaseDatabase.getReference("Teacher Bio").child(teacherID);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                String bio = dataSnapshot.getValue(String.class);
                                                teacherBio.setVisibility(View.VISIBLE);
                                                teacherBio.setText(bio);
                                            }
                                            else {
                                                teacherBio.setText("Bio not found");
                                            }

                                            mDatabaseReference = mFirebaseDatabase.getReference("Teacher School").child(teacherID);
                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                            schoolKey = postSnapshot.getKey();
                                                        }

                                                        mDatabaseReference = mFirebaseDatabase.getReference("School").child(schoolKey);
                                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()){
                                                                    School school = dataSnapshot.getValue(School.class);
                                                                    schoolName.setVisibility(View.VISIBLE);
                                                                    schoolAddress.setVisibility(View.VISIBLE);
                                                                    schoolState.setVisibility(View.VISIBLE);
                                                                    schoolName.setText(school.getSchoolName());
                                                                    schoolAddress.setText(school.getLocation());
                                                                    schoolState.setText(school.getState() + ", " + school.getCountry());
                                                                }
                                                                else {
                                                                    schoolName.setVisibility(View.VISIBLE);
                                                                    schoolName.setText("This school has been deleted or doesn't exist");
                                                                    schoolAddress.setVisibility(View.GONE);
                                                                    schoolState.setVisibility(View.GONE);
                                                                }

                                                                mDatabaseReference = mFirebaseDatabase.getReference("Ratings Teacher").child(teacherID);
                                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()) {
                                                                            int totalRating = 0;
                                                                            double averageRating = 0.0;
                                                                            int childrenCountForReview = (int) dataSnapshot.getChildrenCount();
                                                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                                                Review review = postSnapshot.getValue(Review.class);
                                                                                if (!review.getReview().equals("")) {
                                                                                    reviewList.add(review);
                                                                                }
                                                                                int rating = Integer.valueOf(review.getRating());
                                                                                totalRating += rating;
                                                                            }

                                                                            averageRating = Math.round(((double )totalRating / (double) childrenCountForReview) * 10.0) / 10.0;
                                                                            rating.setText(String.valueOf(averageRating));

                                                                            for (int i = 0; i < reviewList.size(); i++){
                                                                                if (reviewList.size() > 1) {
                                                                                    Collections.sort(reviewList, new Comparator<Review>() {
                                                                                        @Override
                                                                                        public int compare(Review o1, Review o2) {
                                                                                            return o1.getSortableDate().compareTo(o2.getSortableDate());
                                                                                        }
                                                                                    });
                                                                                }
                                                                                Collections.reverse(reviewList);
                                                                                reviewOneLayout.setVisibility(View.VISIBLE );
                                                                                reviewOne.setText(reviewList.get(i).getReview());

                                                                                mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(reviewList.get(i).getReviewerID());
                                                                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        if (dataSnapshot.exists()){
                                                                                            Parent parent = dataSnapshot.getValue(Parent.class);
                                                                                            reviewerOne.setText(parent.getFirstName() + " " + parent.getLastName());
                                                                                            Glide.with(getBaseContext())
                                                                                                    .load(parent.getProfilePicURL())
                                                                                                    .placeholder(R.drawable.profileimageplaceholder)
                                                                                                    .error(R.drawable.profileimageplaceholder)
                                                                                                    .centerCrop()
                                                                                                    .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                                                                                                    .into(reviewerPhotoOne);
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });
                                                                                break;
                                                                            }

                                                                        } else {
                                                                            reviewErrorLayout.setVisibility(View.VISIBLE);
                                                                            rating.setText("0.0");
                                                                        }

                                                                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStoryTeacherTimeline").child(teacherID);
                                                                        mDatabaseReference.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot.exists()){
                                                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                                                        final String classStoryKey = postSnapshot.getKey();

                                                                                        timelineLayoutOne.setVisibility(View.VISIBLE);
                                                                                        mDatabaseReference = mFirebaseDatabase.getReference("ClassStory").child(classStoryKey);
                                                                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                ClassStory classStory = dataSnapshot.getValue(ClassStory.class);
                                                                                                timelineName.setText(teacher.getFirstName() + " " + teacher.getLastName());
                                                                                                Glide.with(getBaseContext())
                                                                                                        .load(teacher.getProfilePicURL())
                                                                                                        .placeholder(R.drawable.profileimageplaceholder)
                                                                                                        .error(R.drawable.profileimageplaceholder)
                                                                                                        .centerCrop()
                                                                                                        .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                                                                                                        .into(timelineProfilePic);
                                                                                                timelineClass.setText(classStory.getClassReciepient());
                                                                                                timelineDate.setText(Date.DateFormatMMDDYYYY(classStory.getDate()));
                                                                                                if (!(classStory.getStory() == null || classStory.getStory().isEmpty())){
                                                                                                    timelineStory.setText(classStory.getStory());
                                                                                                } else { timelineStory.setVisibility(View.GONE); }
                                                                                                if (!(classStory.getUrl() == null || classStory.getUrl().isEmpty())){
                                                                                                    timelineLink.setText(classStory.getUrl());
                                                                                                } else { timelineLink.setVisibility(View.GONE); }
                                                                                                if (!(classStory.getImageURL() == null || classStory.getImageURL().isEmpty())) {
                                                                                                    Glide.with(getBaseContext())
                                                                                                            .load(classStory.getImageURL())
                                                                                                            .placeholder(R.drawable.profileimageplaceholder)
                                                                                                            .error(R.drawable.profileimageplaceholder)
                                                                                                            .centerCrop()
                                                                                                            .bitmapTransform(new CropCircleTransformation(getBaseContext()))
                                                                                                            .into(timelineStoryImage);
                                                                                                } else { timelineStoryImage.setVisibility(View.GONE); }
                                                                                                timelineNoOfLikes.setText(classStory.getNoOfLikes() + " Likes");
                                                                                                timelineNoOfComments.setText(classStory.getNumberOfComments() + " Comments");
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                                            }
                                                                                        });
                                                                                    }
                                                                                } else {
                                                                                    timelineErrorLayout.setVisibility(View.VISIBLE);
                                                                                }

                                                                                if (!timelineShowStatus) { timelineLayoutOne.setVisibility(View.GONE); timelineLayout.setVisibility(View.GONE); }
                                                                                if (!maritalStatusShowStatus) { maritalStatusLayout.setVisibility(View.GONE); }
                                                                                if (!locationShowStatus) { locationLayout.setVisibility(View.GONE); }

                                                                                superLayout.setVisibility(View.VISIBLE);
                                                                                progressLayout.setVisibility(View.GONE);
                                                                                mySwipeRefreshLayout.setRefreshing(false);
                                                                                errorLayout.setVisibility(View.GONE);
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }

                                                    else {
                                                        schoolName.setText("This teacher is not affiliated to any school");
                                                        schoolAddress.setVisibility(View.GONE);
                                                        schoolState.setVisibility(View.GONE);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else{
                            superLayout.setVisibility(View.GONE);
                            progressLayout.setVisibility(View.GONE);
                            mySwipeRefreshLayout.setRefreshing(false);
                            errorLayout.setVisibility(View.VISIBLE);
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
