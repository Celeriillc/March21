package com.celerii.celerii.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.R;

import java.util.List;

public class SchoolProfileCoCurricularAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> activities;
    private String schoolName;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            subject = (TextView) view.findViewById(R.id.subject);
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

    public SchoolProfileCoCurricularAdapter(List<String> activities, Context context) {
        this.activities = activities;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_offered_subjects_row, parent, false);
                return new SchoolProfileCoCurricularAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_offered_subjects_header, parent, false);
                return new SchoolProfileCoCurricularAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_offered_subjects_row, parent, false);
                return new SchoolProfileCoCurricularAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            if (activities.size() > 0) {
                ((HeaderViewHolder) holder).header.setText(activities.get(0));
            }
        } else if (holder instanceof MyViewHolder) {
            final String activity = activities.get(position);
            ((MyViewHolder) holder).subject.setText(activity);
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
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
        return position == activities.size () + 1;
    }
}