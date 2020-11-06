package com.celerii.celerii.Activities.Profiles.SchoolProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Home.Parent.ParentMainActivityTwo;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.R;
import com.celerii.celerii.adapters.SchoolProfileGalleryAdapter;
import com.celerii.celerii.models.GalleryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SchoolProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    LinearLayout reviewOneLayout, reviewTwoLayout;
    private ArrayList<GalleryModel> galleryModelList;
    public RecyclerView recyclerView;
    public SchoolProfileGalleryAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    ImageView profilePic, backgroundPic, reviewerPhotoOne, reviewerPhotoTwo;

    TextView schoolName, schoolAddress, schoolCity, schoolState, schoolContactNumber, schoolEmail, schoolWebsite;
    TextView aboutUs, yearOfEstablishment, motto, numberOfEmployees, studentSize, schoolFeesRange, schoolCurriculum;
    TextView reviewOne, reviewTwo, reviewerOne, reviewerTwo;

    String schoolID = "Grac0001", parentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_profile_one);
    }

//        auth = FirebaseAuth.getInstance();
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mDatabaseReference = mFirebaseDatabase.getReference();

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null)
//        {
//            parentActivity = bundle.getString("parentActivity");
//        }
//
//        profilePic = (ImageView) findViewById(R.id.profilepic);
//        backgroundPic = (ImageView) findViewById(R.id.backgroundimage);
//        reviewerPhotoOne = (ImageView) findViewById(R.id.reviewerphoto);
//        reviewerPhotoTwo = (ImageView) findViewById(R.id.reviewerphoto2);
//
//        reviewOneLayout = (LinearLayout) findViewById(R.id.reviewonelayout);
//        reviewTwoLayout = (LinearLayout) findViewById(R.id.reviewtwolayout);
//
//        schoolName = (TextView) findViewById(R.id.fullname);
//        schoolAddress = (TextView) findViewById(R.id.fullname);
//        schoolCity = (TextView) findViewById(R.id.fullname);
//        schoolState = (TextView) findViewById(R.id.fullname);
//        schoolContactNumber = (TextView) findViewById(R.id.fullname);
//        schoolEmail = (TextView) findViewById(R.id.fullname);
//        schoolWebsite = (TextView) findViewById(R.id.fullname);
//        aboutUs = (TextView) findViewById(R.id.fullname);
//        yearOfEstablishment = (TextView) findViewById(R.id.fullname);
//        motto = (TextView) findViewById(R.id.fullname);
//        numberOfEmployees = (TextView) findViewById(R.id.fullname);
//        studentSize = (TextView) findViewById(R.id.fullname);
//        schoolFeesRange = (TextView) findViewById(R.id.fullname);
//        schoolCurriculum = (TextView) findViewById(R.id.fullname);
//        reviewOne = (TextView) findViewById(R.id.fullname);
//        reviewTwo = (TextView) findViewById(R.id.fullname);
//        reviewerOne = (TextView) findViewById(R.id.fullname);
//        reviewerTwo = (TextView) findViewById(R.id.fullname);
//
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(mLayoutManager);
//
//        galleryModelList = new ArrayList<>();
//        loadDetailsFromFirebase();
//        yeah();
//        mAdapter = new SchoolProfileGalleryAdapter(galleryModelList, this);
//        recyclerView.setAdapter(mAdapter);
//
//        mDatabaseReference = mFirebaseDatabase.getReference("School").child(schoolID);
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    School school = dataSnapshot.getValue(School.class);
//                    schoolName.setText(school.getSchoolName());
//                    schoolAddress.setText(school.getLocation());
//                    schoolCity.setText(school.getCity());
//                    schoolState.setText(school.getState());
////                    schoolContactNumber
//                    schoolEmail.setText(school.getEmail());
//                    schoolWebsite.setText(school.getWebsite());
//                    aboutUs.setText(school.getAboutUs());
//                    yearOfEstablishment.setText(school.getYearOfEstablishment());
//                    motto.setText(school.getMotto());
//                    numberOfEmployees.setText(school.getNumberOfEmployees());
//                    studentSize.setText(school.getSize());
//                    schoolFeesRange.setText(school.getSchoolFeesRange());
//                    Glide.with(getBaseContext())
//                            .load(school.getProfilePhotoUrl())
//                            .placeholder(R.drawable.profileimageplaceholder)
//                            .error(R.drawable.profileimageplaceholder)
//                            .centerCrop()
//                            .bitmapTransform(new CropCircleTransformation(getBaseContext()))
//                            .into(profilePic);
//                    Glide.with(getBaseContext())
//                            .load(school.getBackgroundPhotoUrl())
//                            .placeholder(R.drawable.profileimageplaceholder)
//                            .error(R.drawable.profileimageplaceholder)
//                            .centerCrop()
//                            .bitmapTransform(new BlurTransformation(getBaseContext(), 50))
//                            .into(backgroundPic);
//
//
//                    mDatabaseReference = mFirebaseDatabase.getReference("SchoolAddress").child(schoolID);
//                    mDatabaseReference.limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()){
//                                String addresses = "";
//                                int counter = 0;
//                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                                    String address = postSnapshot.getValue(String.class);
//                                    if (counter == 0){
//                                        addresses = address;
//                                    } else if (counter == 1){
//                                        addresses = addresses + ", " + address;
//                                    }
//                                    counter++;
//                                }
//                                schoolAddress.setText(addresses);
//                            }
//
//
//                            mDatabaseReference = mFirebaseDatabase.getReference("School Phone Numbers").child(schoolID);
//                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()){
//                                        String phonenos = "";
//                                        int counter = 0;
//                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                                            String phoneNo = postSnapshot.getValue(String.class);
//                                            if (counter == 0){
//                                                phonenos = phoneNo;
//                                            } else if (counter == 1){
//                                                phonenos = phonenos + ", " + phoneNo;
//                                            }
//                                            counter++;
//                                        }
//                                        schoolContactNumber.setText(phonenos);
//                                    }
//
//                                    mDatabaseReference = mFirebaseDatabase.getReference("SchoolGallery").child(schoolID);
//                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot.exists()) {
//                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                                    GalleryModel galleryModel = postSnapshot.getValue(GalleryModel.class);
//                                                    galleryModelList.add(galleryModel);
//                                                    mAdapter.notifyDataSetChanged();
//                                                }
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//
//                    mDatabaseReference = mFirebaseDatabase.getReference("Ratings School").child(schoolID);
//                    mDatabaseReference.limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()){
//                                int counter = 0;
//                                if (dataSnapshot.getChildrenCount() == 1){
//                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                        Review review = postSnapshot.getValue(Review.class);
//                                        reviewTwoLayout.setVisibility(View.GONE);
//                                        reviewOne.setText(review.getReview());
//                                        mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(review.getReviewerID());
//                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot.exists()){
//                                                    Parent parent = dataSnapshot.getValue(Parent.class);
//                                                    reviewerOne.setText(parent.getFirstName() + " " + parent.getLastName());
//                                                    Glide.with(getBaseContext())
//                                                            .load(parent.getProfilePicURL())
//                                                            .placeholder(R.drawable.profileimageplaceholder)
//                                                            .error(R.drawable.profileimageplaceholder)
//                                                            .centerCrop()
//                                                            .bitmapTransform(new CropCircleTransformation(getBaseContext()))
//                                                            .into(reviewerPhotoOne);
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                    }
//                                } else if (dataSnapshot.getChildrenCount() > 1){
//                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                        if (counter == 0){
//                                            Review review = postSnapshot.getValue(Review.class);
//                                            reviewOne.setText(review.getReview());
//                                            mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(review.getReviewerID());
//                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot.exists()){
//                                                        Parent parent = dataSnapshot.getValue(Parent.class);
//                                                        reviewerOne.setText(parent.getFirstName() + " " + parent.getLastName());
//                                                        Glide.with(getBaseContext())
//                                                                .load(parent.getProfilePicURL())
//                                                                .placeholder(R.drawable.profileimageplaceholder)
//                                                                .error(R.drawable.profileimageplaceholder)
//                                                                .centerCrop()
//                                                                .bitmapTransform(new CropCircleTransformation(getBaseContext()))
//                                                                .into(reviewerPhotoOne);
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        } else if (counter == 1){
//                                            Review review = postSnapshot.getValue(Review.class);
//                                            reviewTwo.setText(review.getReview());
//                                            mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(review.getReviewerID());
//                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot.exists()){
//                                                        Parent parent = dataSnapshot.getValue(Parent.class);
//                                                        reviewerTwo.setText(parent.getFirstName() + " " + parent.getLastName());
//                                                        Glide.with(getBaseContext())
//                                                                .load(parent.getProfilePicURL())
//                                                                .placeholder(R.drawable.profileimageplaceholder)
//                                                                .error(R.drawable.profileimageplaceholder)
//                                                                .centerCrop()
//                                                                .bitmapTransform(new CropCircleTransformation(getBaseContext()))
//                                                                .into(reviewerPhotoTwo);
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        }
//                                        counter++;
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//        if (!("http://thenet.ng/wp-content/uploads/2015/06/mari-okann.png").isEmpty()) {
//            Glide.with(this)
//                    .load("http://thenet.ng/wp-content/uploads/2015/06/mari-okann.png")
//                    .placeholder(R.drawable.profileimageplaceholder)
//                    .error(R.drawable.profileimageplaceholder)
//                    .centerCrop()
//                    .bitmapTransform(new CropCircleTransformation(this))
//                    .into(profilePic);
//        }
//        else {
//            Glide.with(this)
//                    .load(R.drawable.profileimageplaceholder)
//                    .centerCrop()
//                    .bitmapTransform(new CropCircleTransformation(this))
//                    .into(profilePic);
//        }
//
//        if (!("http://thenet.ng/wp-content/uploads/2015/06/mari-okann.png").isEmpty()) {
//            Glide.with(this)
//                    .load("http://thenet.ng/wp-content/uploads/2015/06/mari-okann.png")
//                    .placeholder(R.drawable.profileimageplaceholder)
//                    .error(R.drawable.profileimageplaceholder)
//                    .centerCrop()
//                    .bitmapTransform(new BlurTransformation(this, 50))
//                    .into(backgroundPic);
//        }
//        else {
//            Glide.with(this)
//                    .load(R.drawable.profileimageplaceholder)
//                    .centerCrop()
//                    .bitmapTransform(new BlurTransformation(this, 50))
//                    .into(backgroundPic);
//        }
//
//        if (!("http://loudestgist.com/wiki/wp-content/uploads/2016/06/Maria-Okanrende-Biography.jpg").isEmpty()) {
//            Glide.with(this)
//                    .load("http://loudestgist.com/wiki/wp-content/uploads/2016/06/Maria-Okanrende-Biography.jpg")
//                    .placeholder(R.drawable.profileimageplaceholder)
//                    .error(R.drawable.profileimageplaceholder)
//                    .centerCrop()
//                    .bitmapTransform(new CropCircleTransformation(this))
//                    .into(reviewerPhotoOne);
//        }
//        else {
//            Glide.with(this)
//                    .load(R.drawable.profileimageplaceholder)
//                    .centerCrop()
//                    .bitmapTransform(new CropCircleTransformation(this))
//                    .into(reviewerPhotoOne);
//        }
//
//        if (!("http://loudestgist.com/wiki/wp-content/uploads/2016/06/Maria-Okanrende-Biography.jpg").isEmpty()) {
//            Glide.with(this)
//                    .load("http://loudestgist.com/wiki/wp-content/uploads/2016/06/Maria-Okanrende-Biography.jpg")
//                    .placeholder(R.drawable.profileimageplaceholder)
//                    .error(R.drawable.profileimageplaceholder)
//                    .centerCrop()
//                    .bitmapTransform(new CropCircleTransformation(this))
//                    .into(reviewerPhotoTwo);
//        }
//        else {
//            Glide.with(this)
//                    .load(R.drawable.profileimageplaceholder)
//                    .centerCrop()
//                    .bitmapTransform(new CropCircleTransformation(this))
//                    .into(reviewerPhotoTwo);
//        }
//    }
//
//    private void loadDetailsFromFirebase() {
//    }

//    void yeah() {
//        GalleryModel model = new GalleryModel("http://dailymail.com.ng/wp-content/uploads/2015/03/toolzo.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://www.gistus.com/gs-c/uploads/2013/08/Shuga-Leonora-Okine.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://static.pulse.ng/img/incoming/origs3746832/0355563074-w644-h429/Sharon-Ezeamaka.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://static.pulse.ng/img/incoming/origs3800133/5805561420-w644-h429/Toolz-WCW.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://www.gistus.com/gs-c/uploads/2013/08/Shuga-Dorcas-Shola-Fapson.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://i1.wp.com/thenet.ng/wp-content/uploads/2016/06/tiwa-savage-mavin-e1468221794971-600x589.jpg?resize=600%2C589", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://i0.wp.com/thenet.ng/wp-content/uploads/2012/09/Toolz-photoshoot-with-Moussa-Moussa-10.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://static.pulse.ng/img/incoming/origs3458474/1636366867-w644-h960/Sharon-Chisom-Ezeamaka-plays-Princess-in-Shuga-Pulse.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("https://1.bp.blogspot.com/-vh7RR6T056I/V0NEHawIf5I/AAAAAAAAGvs/rjnJZugf6DQv0idI_T13a958a8dkcN3LQCKgB/s640/tolu.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://dev.mtvshuga.com/wp-content/uploads/2015/06/Sophie-760x760.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://stargist.com/wp-content/uploads/2016/03/Tiwa-Savage-8-fashionpheeva.png", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://1.bp.blogspot.com/-bGfxgmKs1cE/VTgJULVOBEI/AAAAAAAFBoA/CBpuRSLdM7I/s1600/4.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://www.authorityngr.com/app/views/images/uploads/content_images/2016_08_26_22587.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://3.bp.blogspot.com/-c7dgWpRNPHo/VTgJU3F1FyI/AAAAAAAFBoM/3jA867FTxQY/s1600/5.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://cdn1.dailypost.ng/wp-content/uploads/2015/12/Ini-Edo1.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://www.tori.ng/userfiles/image/2017/feb/06/actress-Rita-Dominic-5.jpg", "");
//        galleryModelList.add(model);
//
//        model = new GalleryModel("http://dailymail.com.ng/wp-content/uploads/2015/04/Ini-Edo1.jpg", "");
//        galleryModelList.add(model);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
                    Intent i = new Intent(this, ParentMainActivityTwo.class);
                    startActivity(i);
                } else if (parentActivity.equals("Teacher")) {
                    Intent i = new Intent(this, TeacherMainActivityTwo.class);
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
                startActivity(i);
            } else if (parentActivity.equals("Teacher")) {
                Intent i = new Intent(this, TeacherMainActivityTwo.class);
                startActivity(i);
            }
        }
    }
}
