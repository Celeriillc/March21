package com.celerii.celerii.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.SchoolProfileTableModel;

import java.util.List;

/**
 * Created by DELL on 4/28/2019.
 */

public class SchoolProfileTableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SchoolProfileTableModel> schoolProfileTableModelList;
    private String title;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mainText, subText;

        public MyViewHolder(final View view) {
            super(view);
            mainText = (TextView) view.findViewById(R.id.maintext);
            subText = (TextView) view.findViewById(R.id.subtext);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public HeaderViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    public SchoolProfileTableAdapter(List<SchoolProfileTableModel> schoolProfileTableModelList, String title, Context context) {
        this.schoolProfileTableModelList = schoolProfileTableModelList;
        this.title = title;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_table_row, parent, false);
                return new SchoolProfileTableAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_table_header, parent, false);
                return new SchoolProfileTableAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_profile_table_row, parent, false);
                return new SchoolProfileTableAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).title.setText(title);
        } else if (holder instanceof MyViewHolder) {
            SchoolProfileTableModel schoolProfileTableModel = schoolProfileTableModelList.get(position);
            ((MyViewHolder) holder).mainText.setText(schoolProfileTableModel.getMainText());
            ((MyViewHolder) holder).subText.setText(schoolProfileTableModel.getSubText());
        }
    }

    @Override
    public int getItemCount() {
        return schoolProfileTableModelList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position)) {
            return Header;
        } else if (isPositionFooter(position)) {
            return Footer;
        }
        return Normal;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position == schoolProfileTableModelList.size() + 1;
    }
}

