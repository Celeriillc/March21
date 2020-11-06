package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.RatingAndReview.WriteAReviewSchool;
import com.celerii.celerii.Activities.RatingAndReview.WriteAReviewTeacher;
import com.celerii.celerii.helperClasses.DumbNumericals;
import com.celerii.celerii.models.RatingSummary;
import com.celerii.celerii.models.Review;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 7/9/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FirebaseUser mFirebaseUser;
    String entityID, entityName, entityType;

    private List<Review> reviewList;
    private RatingSummary ratingSummary;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView review, reviewerName;
        public ImageView reviewerPic;

        public MyViewHolder(final View view) {
            super(view);
            reviewerPic = (ImageView) view.findViewById(R.id.reviewerphoto);
            review = (TextView) view.findViewById(R.id.review);
            reviewerName = (TextView) view.findViewById(R.id.reviewerName);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView numberOfreviews, rating, noOfFives, noOfFours, noOfThrees, noOfTwos, noOfOnes;
        ImageView profilePic;
        RatingBar ratingBar;
        ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5;
        LinearLayout writeAReviewLayout, placeHolderLayout;

        public HeaderViewHolder(View view) {
            super(view);
            numberOfreviews = (TextView) view.findViewById(R.id.numberofreviews);
            rating = (TextView) view.findViewById(R.id.numberofstars);
            noOfFives = (TextView) view.findViewById(R.id.nooffives);
            noOfFours = (TextView) view.findViewById(R.id.nooffours);
            noOfThrees = (TextView) view.findViewById(R.id.noofthrees);
            noOfTwos = (TextView) view.findViewById(R.id.nooftwos);
            noOfOnes = (TextView) view.findViewById(R.id.noofones);
            ratingBar = (RatingBar) view.findViewById(R.id.rating);
            profilePic = (ImageView) view.findViewById(R.id.profilepic);
            progressBar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
            progressBar2 = (ProgressBar) view.findViewById(R.id.progressBar2);
            progressBar3 = (ProgressBar) view.findViewById(R.id.progressBar3);
            progressBar4 = (ProgressBar) view.findViewById(R.id.progressBar4);
            progressBar5 = (ProgressBar) view.findViewById(R.id.progressBar5);
            writeAReviewLayout = (LinearLayout) view.findViewById(R.id.writeareviewlayout);
            placeHolderLayout = (LinearLayout) view.findViewById(R.id.placeholderlayout);
        }
    }

    public ReviewAdapter(List<Review> reviewList, RatingSummary ratingSummary, FirebaseUser mFirebaseUser, String entiryID, String entityName, String entityType, Context context) {
        this.reviewList = reviewList;
        this.ratingSummary = ratingSummary;
        this.context = context;
        this.mFirebaseUser = mFirebaseUser;
        this.entityID = entiryID;
        this.entityName = entityName;
        this.entityType = entityType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_review_body, parent, false);
                return new ReviewAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_review_header, parent, false);
                return new ReviewAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_review_body, parent, false);
                return new ReviewAdapter.MyViewHolder(rowView);
        }
    }


    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof HeaderViewHolder){

            ((HeaderViewHolder) holder).writeAReviewLayout.setVisibility(View.VISIBLE);
            ((HeaderViewHolder) holder).placeHolderLayout.setVisibility(View.GONE);

            int max = DumbNumericals.maxOfFiveInts(ratingSummary.getNoOfOne(), ratingSummary.getNoOfTwo(), ratingSummary.getNoOfThree(),
                    ratingSummary.getNoOfFour(), ratingSummary.getNoOfFive());

            double normalizedOnes = 0;
            double normalizedTwos = 0;
            double normalizedThrees = 0;
            double normalizedFours = 0;
            double normalizedFives = 0;
            if (max != 0) {
                normalizedOnes = ((double) ratingSummary.getNoOfOne() / (double) max) * 100.0;
                normalizedTwos = ((double) ratingSummary.getNoOfTwo() / (double) max) * 100;
                normalizedThrees = ((double) ratingSummary.getNoOfThree() / (double) max) * 100;
                normalizedFours = ((double) ratingSummary.getNoOfFour() / (double) max) * 100;
                normalizedFives = ((double) ratingSummary.getNoOfFive() / (double) max) * 100;
            }

//            if (mFirebaseUser.getUid().equals(entityID)) {
//                ((HeaderViewHolder) holder).writeAReviewLayout.setVisibility(View.GONE);
//                ((HeaderViewHolder) holder).placeHolderLayout.setVisibility(View.VISIBLE);
//            }

            ((HeaderViewHolder) holder).noOfOnes.setText("(" + ratingSummary.getNoOfOne() + ")");
            ((HeaderViewHolder) holder).noOfTwos.setText("(" + ratingSummary.getNoOfTwo() + ")");
            ((HeaderViewHolder) holder).noOfThrees.setText("(" + ratingSummary.getNoOfThree() + ")");
            ((HeaderViewHolder) holder).noOfFours.setText("(" + ratingSummary.getNoOfFour() + ")");
            ((HeaderViewHolder) holder).noOfFives.setText("(" + ratingSummary.getNoOfFive() + ")");
            ((HeaderViewHolder) holder).progressBar1.setProgress((int) normalizedOnes);
            ((HeaderViewHolder) holder).progressBar1.setScaleY(1.5f);
            ((HeaderViewHolder) holder).progressBar1.getProgressDrawable().setColorFilter(
                    Color.rgb(191, 54, 12), android.graphics.PorterDuff.Mode.SRC_IN);
            ((HeaderViewHolder) holder).progressBar2.setProgress((int) normalizedTwos);
            ((HeaderViewHolder) holder).progressBar2.setScaleY(1.5f);
            ((HeaderViewHolder) holder).progressBar2.getProgressDrawable().setColorFilter(
                    Color.rgb(230, 74, 25), android.graphics.PorterDuff.Mode.SRC_IN);
            ((HeaderViewHolder) holder).progressBar3.setProgress((int) normalizedThrees);
            ((HeaderViewHolder) holder).progressBar3.setScaleY(1.5f);
            ((HeaderViewHolder) holder).progressBar3.getProgressDrawable().setColorFilter(
                    Color.rgb(255, 193, 7), android.graphics.PorterDuff.Mode.SRC_IN);
            ((HeaderViewHolder) holder).progressBar4.setProgress((int) normalizedFours);
            ((HeaderViewHolder) holder).progressBar4.setScaleY(1.5f);
            ((HeaderViewHolder) holder).progressBar4.getProgressDrawable().setColorFilter(
                    Color.rgb(76, 175, 80), android.graphics.PorterDuff.Mode.SRC_IN);
            ((HeaderViewHolder) holder).progressBar5.setProgress((int) normalizedFives);
            ((HeaderViewHolder) holder).progressBar5.setScaleY(1.5f);
            ((HeaderViewHolder) holder).progressBar5.getProgressDrawable().setColorFilter(
                    Color.rgb(27, 94, 32), android.graphics.PorterDuff.Mode.SRC_IN);
            ((HeaderViewHolder) holder).numberOfreviews.setText("(" + String.valueOf(ratingSummary.getNumberOfVotes()) + " Votes)");
            ((HeaderViewHolder) holder).rating.setText(String.valueOf(ratingSummary.getRating()) + " Stars");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                ((HeaderViewHolder) holder).progressBar1.setProgressTintList(ColorStateList.valueOf(Color.rgb(191, 54, 12)));
//                ((HeaderViewHolder) holder).progressBar2.setProgressTintList(ColorStateList.valueOf(Color.rgb(230, 74, 25)));
//                ((HeaderViewHolder) holder).progressBar3.setProgressTintList(ColorStateList.valueOf(Color.rgb(255, 193, 7)));
//                ((HeaderViewHolder) holder).progressBar4.setProgressTintList(ColorStateList.valueOf(Color.rgb(76, 175, 80)));
//                ((HeaderViewHolder) holder).progressBar5.setProgressTintList(ColorStateList.valueOf(Color.rgb(27, 94, 32)));
            }

            LayerDrawable stars = (LayerDrawable) ((HeaderViewHolder) holder).ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.rgb(255, 193, 7), PorterDuff.Mode.SRC_ATOP);
            ((HeaderViewHolder) holder).ratingBar.setRating((float) ratingSummary.getRating());

            Glide.with(context)
                    .load(ratingSummary.getUrlPic())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((HeaderViewHolder) holder).profilePic);

            ((HeaderViewHolder) holder).writeAReviewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;

                    if (entityType.equals("Teacher")) {
                        intent = new Intent(context, WriteAReviewTeacher.class);
                    } else {
                        intent = new Intent(context, WriteAReviewSchool.class);
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("EntityName", entityName);
                    bundle.putString("EntityID", entityID);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }
        else if (holder instanceof MyViewHolder){
            Review review = reviewList.get(position);

            ((MyViewHolder) holder).review.setText(review.getReview());
            ((MyViewHolder) holder).reviewerName.setText("- " + review.getReviewer());

            Glide.with(context)
                    .load(review.getReviewerPicURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder) holder).reviewerPic);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(isPositionHeader (position)) {
            return Header;
        } else if(isPositionFooter (position)) {
            return Footer;
        }
        return Normal;
    }

    private boolean isPositionHeader (int position) {
        return position == 0;
    }

    private boolean isPositionFooter (int position) {
        return position == reviewList.size () + 1;
    }
}
