package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.celerii.celerii.R;

import java.util.List;

/**
 * Created by DELL on 1/8/2019.
 */

public class SelectYearAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> selectYearModelList;
    private String selectedYear;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton year;

        public MyViewHolder(final View view) {
            super(view);
            year = (RadioButton) view.findViewById(R.id.year);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public SelectYearAdapter(List<String> selectYearModelList, String selectedYear, Context context) {
        this.selectYearModelList = selectYearModelList;
        this.context = context;
        this.selectedYear = selectedYear;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_row, parent, false);
                return new SelectYearAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_header, parent, false);
                return new SelectYearAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_row, parent, false);
                return new SelectYearAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SelectYearAdapter.MyViewHolder) {
            final String selectYearModel = selectYearModelList.get(position);

            ((MyViewHolder) holder).year.setText(selectYearModel);

            if (selectedYear == null){
                selectedYear = selectYearModel;
                ((MyViewHolder) holder).year.setChecked(true);
            } else if (selectedYear.equals(selectYearModel)){
                ((MyViewHolder) holder).year.setChecked(true);
            } else if (!selectedYear.equals(selectYearModel)){
                ((MyViewHolder) holder).year.setChecked(false);
            }

            Intent intent = new Intent("Selected Year");
            intent.putExtra("SelectedYear", selectedYear);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            ((MyViewHolder) holder).year.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyViewHolder) holder).year.setChecked(true);
                    selectedYear = selectYearModel;
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectYearModelList.size();
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
        return position == selectYearModelList.size () + 1;
    }
}
