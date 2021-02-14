package com.celerii.celerii.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.Award;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SchoolProfileAwardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Award> awardList;
    private String schoolName;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView award, awardYear;
        public ImageView image;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            award = (TextView) view.findViewById(R.id.award);
            awardYear = (TextView) view.findViewById(R.id.awardyear);
            image = (ImageView) view.findViewById(R.id.image);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);

            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public SchoolProfileAwardsAdapter(List<Award> awardList, Context context) {
        this.awardList = awardList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_awards_row, parent, false);
                return new SchoolProfileAwardsAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_awards_header, parent, false);
                return new SchoolProfileAwardsAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_awards_row, parent, false);
                return new SchoolProfileAwardsAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            if (awardList.size() > 0) {
                ((HeaderViewHolder) holder).header.setText(awardList.get(0).getAwardName());
            }
        } else if (holder instanceof MyViewHolder) {
            final Award award = awardList.get(position);
            ((MyViewHolder) holder).award.setText(award.getAwardName());
            ((MyViewHolder) holder).awardYear.setText(award.getAwardYear());
            final int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
            if (randomNum == 0) { ((MyViewHolder) holder).image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple)); }
            else if (randomNum == 1) { ((MyViewHolder) holder).image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_accent)); }
            else if (randomNum == 2) { ((MyViewHolder) holder).image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_green)); }
            else { ((MyViewHolder) holder).image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_kilogarm_orange)); }
        }
    }

    @Override
    public int getItemCount() {
        return awardList.size();
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
        return position == awardList.size () + 1;
    }
}