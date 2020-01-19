package com.celerii.celerii.Activities.RatingAndReview;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.celerii.celerii.R;
import com.celerii.celerii.adapters.ReviewAdapter;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.RatingSummary;
import com.celerii.celerii.models.Review;
import com.celerii.celerii.models.School;
import com.celerii.celerii.models.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ReviewActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    SwipeRefreshLayout mySwipeRefreshLayout;
    LinearLayout errorLayout, progressLayout;

    Toolbar toolbar;
    private ArrayList<Review> reviewList;
    private RatingSummary ratingSummary;
    public RecyclerView recyclerView;
    public ReviewAdapter mAdapter;
    LinearLayoutManager mLayoutManager;

    String entityType = "";
    String entityID = "";
    String entityName = "";
    int reviewIterator = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Bundle bundle = getIntent().getExtras();
        entityType = bundle.getString("EntityType");
        entityID = bundle.getString("EntityID");
        entityName = bundle.getString("EntityName");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(entityName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(mLayoutManager);

        errorLayout = (LinearLayout) findViewById(R.id.errorlayout);
        progressLayout = (LinearLayout) findViewById(R.id.progresslayout);

        recyclerView.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);

        reviewList = new ArrayList<>();
        reviewList.add(new Review());
        ratingSummary = new RatingSummary();
        mAdapter = new ReviewAdapter(reviewList, ratingSummary, mFirebaseUser, entityID, entityName, entityType, this);
//        loadHeader();
//        loadFromFirebase();
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
                        loadHeader();
                        loadFromFirebase();
                    }
                }
        );
    }

    private void loadHeader() {
        if (entityType.equals("Teacher")) {
            mDatabaseReference = mFirebaseDatabase.getReference("Teacher").child(entityID);
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                        ratingSummary.setUrlPic(teacher.getProfilePicURL());
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mDatabaseReference = mFirebaseDatabase.getReference("School").child(entityID);
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        School school = dataSnapshot.getValue(School.class);
                        ratingSummary.setUrlPic(school.getProfilePhotoUrl());
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        if (entityType.equals("Teacher")){
            mDatabaseReference = mFirebaseDatabase.getReference("Ratings Teacher").child(entityID);
        } else {
            mDatabaseReference = mFirebaseDatabase.getReference("Ratings School").child(entityID);
        }

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int childrenCount = (int) dataSnapshot.getChildrenCount();
                    double averageRating = 0.0;
                    int totalRating = 0;
                    int numberOfFives = 0;
                    int numberOfFours = 0;
                    int numberOfThrees = 0;
                    int numberOfTwos = 0;
                    int numberOfOnes = 0;

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final Review review = postSnapshot.getValue(Review.class);
                        final String reviewerID = review.getReviewerID();
                        int rating = Integer.valueOf(review.getRating());
                        totalRating += rating;
                        if (rating == 5) { numberOfFives++; }
                        else if (rating == 4) { numberOfFours++; }
                        else if (rating == 3) { numberOfThrees++; }
                        else if (rating == 2) { numberOfTwos++; }
                        else if (rating == 1) { numberOfOnes++; }
                    }

                    averageRating = Math.round(((double) totalRating / (double) childrenCount) * 10.0) / 10.0;
                    ratingSummary.setNumberOfVotes(childrenCount);
                    ratingSummary.setNoOfFive(numberOfFives);
                    ratingSummary.setNoOfFour(numberOfFours);
                    ratingSummary.setNoOfThree(numberOfThrees);
                    ratingSummary.setNoOfTwo(numberOfTwos);
                    ratingSummary.setNoOfOne(numberOfOnes);
                    ratingSummary.setRating(averageRating);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadFromFirebase(){
        if (entityType.equals("Teacher")){
            mDatabaseReference = mFirebaseDatabase.getReference("Ratings Teacher").child(entityID);
        } else {
            mDatabaseReference = mFirebaseDatabase.getReference("Ratings School").child(entityID);
        }

        mDatabaseReference.orderByChild("sortableDate").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    reviewList.clear();
                    count = 0;
                    final int childrenCount = (int) dataSnapshot.getChildrenCount();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final Review review = postSnapshot.getValue(Review.class);
                        final String reviewerID = review.getReviewerID();
                        final String reviewString = review.getReview();
                        review.setReview(reviewString);

                        mDatabaseReference = mFirebaseDatabase.getReference("Parent").child(reviewerID);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    Parent parent = dataSnapshot.getValue(Parent.class);
                                    review.setReviewer(parent.getFirstName() + " " + parent.getLastName());
                                    review.setReviewerPicURL(parent.getProfilePicURL());
                                } else {
                                    review.setReviewer("Anonymous");
                                }

                                count++;
                                if (!reviewString.equals("")) {
                                    reviewList.add(review);
                                }

                                if (childrenCount == count) {
                                    if (reviewList.size() > 1) {
                                        Collections.sort(reviewList, new Comparator<Review>() {
                                            @Override
                                            public int compare(Review o1, Review o2) {
                                                return o1.getSortableDate().compareTo(o2.getSortableDate());
                                            }
                                        });
                                    }
                                    Collections.reverse(reviewList);
                                    reviewList.add(0, new Review());
                                    mAdapter.notifyDataSetChanged();
                                    mySwipeRefreshLayout.setRefreshing(false);
                                    progressLayout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    errorLayout.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    mySwipeRefreshLayout.setRefreshing(false);
                    progressLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        loadHeader();
        loadFromFirebase();
        super.onResume();
    }
}
