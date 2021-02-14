package com.celerii.celerii.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.EEAP;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SchoolProfileEEAPAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<EEAP> eEAPList;
    private String schoolName;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eEAP, eEAPScore;
        public ProgressBar eEAPProgressBar;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            eEAP = (TextView) view.findViewById(R.id.eeap);
            eEAPScore = (TextView) view.findViewById(R.id.eeapscore);
            eEAPProgressBar = (ProgressBar) view.findViewById(R.id.eeapprogressbar);
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

    public SchoolProfileEEAPAdapter(List<EEAP> eEAPList, Context context) {
        this.eEAPList = eEAPList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_eeap_row, parent, false);
                return new SchoolProfileEEAPAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_eeap_header, parent, false);
                return new SchoolProfileEEAPAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_eeap_row, parent, false);
                return new SchoolProfileEEAPAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            if (eEAPList.size() > 0) {
                ((HeaderViewHolder) holder).header.setText(eEAPList.get(0).getExamName());
            }
        } else if (holder instanceof MyViewHolder) {
            final EEAP eEAP = eEAPList.get(position);
            int score = Integer.parseInt(eEAP.getAverage());
            String scoreString = eEAP.getAverage() + "%";
            ((MyViewHolder) holder).eEAP.setText(eEAP.getExamName());
            ((MyViewHolder) holder).eEAPScore.setText(scoreString);
            ((MyViewHolder) holder).eEAPProgressBar.setProgress(score);
            final int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
            if (randomNum == 0) { ((MyViewHolder) holder).eEAPProgressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_primary_purple)); }
            else if (randomNum == 1) { ((MyViewHolder) holder).eEAPProgressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_accent)); }
            else if (randomNum == 2) { ((MyViewHolder) holder).eEAPProgressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_kilogarm_orange)); }
            else if (randomNum == 3) { ((MyViewHolder) holder).eEAPProgressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_green)); }
            else { ((MyViewHolder) holder).eEAPProgressBar.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_dark_gray)); }
        }
    }

    @Override
    public int getItemCount() {
        return eEAPList.size();
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
        return position == eEAPList.size () + 1;
    }
}
