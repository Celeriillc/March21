package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.Activities.Newsletters.NewsletterDetailActivity;
import com.celerii.celerii.Activities.Newsletters.NewsletterDetailKTActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
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
        public TextView newsletterTitle, newsletterDate, newsletterPoster;
        public ImageView newsletterImage;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            newsletterTitle = (TextView) view.findViewById(R.id.newslettertitle);
            newsletterDate = (TextView) view.findViewById(R.id.newsletterdate);
            newsletterPoster = (TextView) view.findViewById(R.id.newsletterposter);
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
        holder.newsletterDate.setText(Date.getRelativeTimeSpan(newsletterRow.getNewsletterDate()));
        holder.newsletterPoster.setText(newsletterRow.getSchoolID());

        Glide.with(context)
                .load(newsletterRow.getNewsletterImageURL())
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .into(holder.newsletterImage);

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsletterDetailKTActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", newsletterRow.getNewsletterTitle());
                bundle.putString("imageURL", newsletterRow.getNewsletterImageURL());
                bundle.putString("date", newsletterRow.getNewsletterDate());
                bundle.putString("poster", newsletterRow.getSchoolID());
                bundle.putString("body", newsletterRow.getNewsletterBody());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsletterRowsList.size();
    }
}
