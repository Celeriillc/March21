package com.celerii.celerii.Activities.RatingAndReview;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.models.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class WriteAReviewTeacher extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    Toolbar toolbar;
    TextView ratingLabel;
    RatingBar ratingBar;
    EditText review;

    String teacherID = "";
    String teacherName = "";
    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_areview_teacher);

        Bundle bundle = getIntent().getExtras();
        teacherID = bundle.getString("EntityID");
        teacherName = bundle.getString("EntityName");

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        toolbar = (Toolbar) findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Write a review");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        review = (EditText) findViewById(R.id.review);
        ratingLabel = (TextView) findViewById(R.id.ratingmessage);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ratingLabel.setText("Let everyone know how great of a teacher " + teacherName + " is, give a little review.");
        ratingBar.setRating(5);

        loadDetailsFromFirebase();

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                switch ((int)rating) {
                    case 1:
                        ratingLabel.setText("Really awful teacher");
                        break;
                    case 2:
                        ratingLabel.setText("Not a so great teacher after all");
                        break;
                    case 3:
                        ratingLabel.setText("An okay teacher");
                        break;
                    case 4:
                        ratingLabel.setText("Really cool teacher");
                        break;
                    case 5:
                        ratingLabel.setText("Such an awesome teacher, the best");
                        break;
                }
            }
        });
    }

    private void loadDetailsFromFirebase() {
        mDatabaseReference = mFirebaseDatabase.getReference("Ratings Teacher").child(teacherID).child(auth.getCurrentUser().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Review reviewExisting = dataSnapshot.getValue(Review.class);
                    review.setText(reviewExisting.getReview());
                    ratingBar.setRating(Integer.valueOf(reviewExisting.getRating()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }
        else if (id == R.id.action_send) {
            if (connected) {
                String reviewMessage = review.getText().toString().trim();
                String date = Date.getDate();
                String sortableDate = Date.convertToSortableDate(date);
                int rating = (int) ratingBar.getRating();

                Review reviewPackage = new Review(reviewMessage, auth.getCurrentUser().getUid(), teacherID, String.valueOf(rating), date, sortableDate);

                Map<String, Object> newReview = new HashMap<String, Object>();
                newReview.put("Ratings Teacher/" + teacherID + "/" + auth.getCurrentUser().getUid(), reviewPackage);
                DatabaseReference newReviewRef = mFirebaseDatabase.getReference();
                newReviewRef.updateChildren(newReview, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            finish();
                        } else {
                            CustomToast.whiteBackgroundBottomToast(WriteAReviewTeacher.this, "Looks like something is wrong, Try sending again");
                        }
                    }
                });
            } else {
                CustomToast.whiteBackgroundBottomToast(this, "No connection");
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
