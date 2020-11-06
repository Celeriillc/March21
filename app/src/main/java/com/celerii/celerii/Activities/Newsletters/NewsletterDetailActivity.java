package com.celerii.celerii.Activities.Newsletters;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.bumptech.glide.Glide;

public class NewsletterDetailActivity extends AppCompatActivity {

    TextView title, body, poster, date, views, favorites, comments;
    ImageView expandedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsletter_detail);
        Bundle b = getIntent().getExtras();

        String newsletterTitle = b.getString("title");
        String newsletterBody = b.getString("body");
        String newsletterPoster = b.getString("poster");
        String newsletterDate = b.getString("date");
        String newsletterNoOfViews = b.getString("noOfViews") + " Views";
        String newsletterNoOfFavorites = b.getString("noOfFavorites") + " Favorites";
        String newsletterNoOfComments = b.getString("noOfComments") + " Comments";
        String newsletterImageURL = b.getString("imageURL");

        title = (TextView) findViewById(R.id.newslettertitle);
        body = (TextView) findViewById(R.id.newsletterbody);
        poster = (TextView) findViewById(R.id.newsletterposter);
        date = (TextView) findViewById(R.id.newsletterdate);
        views = (TextView) findViewById(R.id.newsletternoofviews);
        favorites = (TextView) findViewById(R.id.newsletternooffavorites);
        comments = (TextView) findViewById(R.id.newsletternoofcomments);
        expandedImage = (ImageView) findViewById(R.id.expandedimage);

        title.setText(newsletterTitle);
        body.setText(newsletterBody);
        poster.setText(newsletterPoster);
        date.setText(newsletterDate);
        views.setText(newsletterNoOfViews);
        favorites.setText(newsletterNoOfFavorites);
        comments.setText(newsletterNoOfComments);

        Glide.with(this)
                .load(newsletterImageURL)
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .centerCrop()
                .into(expandedImage);
    }
}
