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

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Settings.ReportAbuseActivity;
import com.celerii.celerii.models.ReportUserModel;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 5/12/2018.
 */

public class ReportAbuseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ReportUserModel> reportUserModelList;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView profilePic;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            profilePic = (ImageView) view.findViewById(R.id.picture);
            this.view = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public ReportAbuseAdapter(List<ReportUserModel> reportUserModelList,
                        Context context) {
        this.reportUserModelList = reportUserModelList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_abuse, parent, false);
                return new ReportAbuseAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_abuse_header, parent, false);
                return new ReportAbuseAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_abuse, parent, false);
                return new ReportAbuseAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof HeaderViewHolder){

        }
        else if (holder instanceof MyViewHolder){
            final ReportUserModel reportUserModel = reportUserModelList.get(position);
            ((MyViewHolder) holder).name.setText(reportUserModel.getName());
            Glide.with(context)
                    .load(reportUserModel.getURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder) holder).profilePic);

            ((MyViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReportAbuseActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userID", reportUserModel.getUserID());
                    bundle.putString("name", reportUserModel.getName());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }
    }

    public int getItemCount() {
        return reportUserModelList.size();
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
        return position == reportUserModelList.size () + 1;
    }
}
