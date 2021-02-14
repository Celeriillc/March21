package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.Activities.Settings.BrowserActivityForInfo;
import com.celerii.celerii.R;
import com.celerii.celerii.models.TutorialModel;

import java.util.List;

public class SchoolProfileOfferedSubjectsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> subjects;
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

    public SchoolProfileOfferedSubjectsAdapter(List<String> subjects, Context context) {
        this.subjects = subjects;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_offered_subjects_row, parent, false);
                return new SchoolProfileOfferedSubjectsAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_offered_subjects_header, parent, false);
                return new SchoolProfileOfferedSubjectsAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_offered_subjects_row, parent, false);
                return new SchoolProfileOfferedSubjectsAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            if (subjects.size() > 0) {
                ((HeaderViewHolder) holder).header.setText(subjects.get(0));
            }
        } else if (holder instanceof MyViewHolder) {
            final String subject = subjects.get(position);
            ((MyViewHolder) holder).subject.setText(subject);
        }
    }

    @Override
    public int getItemCount() {
        return subjects.size();
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
        return position == subjects.size () + 1;
    }
}
