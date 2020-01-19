package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.SchoolProfileActivity;
import com.celerii.celerii.Activities.Profiles.StudentProfileActivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.SearchHistoryHeader;
import com.celerii.celerii.models.SearchHistoryRow;

import java.util.List;

/**
 * Created by DELL on 9/1/2017.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SharedPreferencesManager sharedPreferencesManager;
    private List<SearchHistoryRow> searchHistoryRowList;
    private SearchHistoryHeader searchHistoryHeader;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView entityName, entityAddress;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            entityName = (TextView) view.findViewById(R.id.entityname);
            entityAddress = (TextView) view.findViewById(R.id.entitylocation);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header;
        public LinearLayout progressBarLayout;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
            progressBarLayout = (LinearLayout) view.findViewById(R.id.progressbarlayout);
        }
    }

    public SearchHistoryAdapter(List<SearchHistoryRow> searchHistoryRowList, SearchHistoryHeader searchHistoryHeader, Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.searchHistoryRowList = searchHistoryRowList;
        this.searchHistoryHeader = searchHistoryHeader;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_row, parent, false);
                return new SearchHistoryAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_header, parent, false);
                return new SearchHistoryAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_row, parent, false);
                return new SearchHistoryAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).header.setText(searchHistoryHeader.getHeader());
            if (searchHistoryHeader.isLoading()){
                ((HeaderViewHolder) holder).progressBarLayout.setVisibility(View.VISIBLE);
            } else {
                ((HeaderViewHolder) holder).progressBarLayout.setVisibility(View.GONE);
            }
        }
        else {
            final SearchHistoryRow searchHistoryRow = searchHistoryRowList.get(position);

            ((MyViewHolder)holder).entityName.setText(searchHistoryRow.getEntityName());
            ((MyViewHolder)holder).entityAddress.setText(searchHistoryRow.getEntityAddress());

            ((MyViewHolder)holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchHistoryRow.getEntityType().equals("Student")){
                        Bundle b = new Bundle();
                        b.putString("childID", searchHistoryRow.getEntityId());
                        Intent I = new Intent(context, StudentProfileActivity.class);
                        I.putExtras(b);
                        context.startActivity(I);
                    } else if (searchHistoryRow.getEntityType().equals("School")){
                        Bundle b = new Bundle();
                        b.putString("schoolID", searchHistoryRow.getEntityId());
                        Intent I = new Intent(context, SchoolProfileActivity.class);
                        I.putExtras(b);
                        context.startActivity(I);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return searchHistoryRowList.size();
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
        return position == searchHistoryRowList.size () + 1;
    }
}
