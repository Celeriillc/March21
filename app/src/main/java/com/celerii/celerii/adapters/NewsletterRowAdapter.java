package com.celerii.celerii.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.Activities.Utility.OpenPDFActivity;
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

        if (!newsletterRow.getNewsletterHeaderImageURL().equals("")) {
            Glide.with(context)
                    .load(newsletterRow.getNewsletterHeaderImageURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .into(holder.newsletterImage);
        } else {
            holder.newsletterImage.setVisibility(View.GONE);
        }

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newsletterRow.getNewsletterPdfURL().trim().equals("")) {
                    String messageString = "We couldn't find this newsletter.";
                    showDialogWithMessage(Html.fromHtml(messageString));
                } else {
                    Intent intent = new Intent(context, OpenPDFActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("PDFTitle", newsletterRow.getNewsletterTitle());
                    bundle.putString("PDFURL", newsletterRow.getNewsletterPdfURL());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsletterRowsList.size();
    }

    void showDialogWithMessage (Spanned messageString) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final Dialog dialog = new Dialog(context);
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
}
