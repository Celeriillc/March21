package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.Activities.Newsletters.NewsletterDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.models.NewsletterRow;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by user on 7/27/2017.
 */

public class NewsletterRowAdapter extends RecyclerView.Adapter<NewsletterRowAdapter.MyViewHolder> {

    private List<NewsletterRow> newsletterRowsList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView newsletterTitle, newsletterDate, newsletterBody, newsletterPoster, noOfViews, noOfFavorites, noOfComments;
        public ImageView newsletterImage;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            newsletterTitle = (TextView) view.findViewById(R.id.newslettertitle);
            newsletterDate = (TextView) view.findViewById(R.id.newsletterdate);
            newsletterBody = (TextView) view.findViewById(R.id.newsletterbody);
            newsletterPoster = (TextView) view.findViewById(R.id.newsletterposter);
            noOfViews = (TextView) view.findViewById(R.id.newsletternoofviews);
            noOfFavorites = (TextView) view.findViewById(R.id.newsletternumberoffavoirites);
            noOfComments = (TextView) view.findViewById(R.id.newsletternoofcomments);
            newsletterImage = (ImageView) view.findViewById(R.id.newsletterimage);
            clickableView = view;
        }
    }

    public NewsletterRowAdapter(List<NewsletterRow> newsletterRowsList, Context context) {
        this.newsletterRowsList = newsletterRowsList;
        this.context = context;
    }

    @Override
    public NewsletterRowAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsletter_row, parent, false);
        return new NewsletterRowAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final NewsletterRow newsletterRow = newsletterRowsList.get(position);

        holder.newsletterTitle.setText(newsletterRow.getNewsletterTitle());
        holder.newsletterDate.setText(newsletterRow.getNewsletterDate());
        holder.newsletterBody.setText(newsletterRow.getNewsletterBody());
        holder.newsletterPoster.setText(newsletterRow.getNewsletterPoster());

        String noOfViews = String.valueOf(newsletterRow.getNoOfViews()) + " Views";
        String noOfFavorites = String.valueOf(newsletterRow.getNoOfFavorites()) + " Favourites";
        String noOfComments = String.valueOf(newsletterRow.getNoOfComments()) + " Comments";

        holder.noOfViews.setText(noOfViews);
        holder.noOfFavorites.setText(noOfFavorites);
        holder.noOfComments.setText(noOfComments);

        if (!newsletterRow.getNewsletterImageURL().isEmpty()) {
            Glide.with(context)
                    .load(newsletterRow.getNewsletterImageURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .into(holder.newsletterImage);
        }

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noOfViews = String.valueOf(newsletterRow.getNoOfViews());
                String noOfFavorites = String.valueOf(newsletterRow.getNoOfFavorites());
                String noOfComments = String.valueOf(newsletterRow.getNoOfComments());

                Bundle b = new Bundle();
                b.putString("date", newsletterRow.getNewsletterDate());
                b.putString("body", newsletterRow.getNewsletterBody());
                b.putString("title", newsletterRow.getNewsletterTitle());
                b.putString("poster", newsletterRow.getNewsletterPoster());
                b.putString("noOfViews", noOfViews);
                b.putString("noOfFavorites", noOfFavorites);
                b.putString("noOfComments", noOfComments);
                b.putString("imageURL", newsletterRow.getNewsletterImageURL());
                Intent I = new Intent(context, NewsletterDetailActivity.class);
                I.putExtras(b);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsletterRowsList.size();
    }
}
