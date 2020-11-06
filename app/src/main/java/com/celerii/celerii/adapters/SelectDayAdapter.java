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

public class SelectDayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> selectDayModelList;
    private String selectedDay;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton day;

        public MyViewHolder(final View view) {
            super(view);
            day = (RadioButton) view.findViewById(R.id.year);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public SelectDayAdapter(List<String> selectDayModelList, String selectedDay, Context context) {
        this.selectDayModelList = selectDayModelList;
        this.context = context;
        this.selectedDay = selectedDay;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_row, parent, false);
                return new SelectDayAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_header, parent, false);
                return new SelectDayAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_row, parent, false);
                return new SelectDayAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  HeaderViewHolder) {
            ((HeaderViewHolder) holder).header.setText("Select Day of the Week");
        }
        else if (holder instanceof SelectDayAdapter.MyViewHolder) {
            final String selectDayModel = selectDayModelList.get(position);

            ((SelectDayAdapter.MyViewHolder) holder).day.setText(selectDayModel);

            if (selectedDay == null){
                selectedDay = selectDayModel;
                ((SelectDayAdapter.MyViewHolder) holder).day.setChecked(true);
            } else if (selectedDay.equals(selectDayModel)){
                ((SelectDayAdapter.MyViewHolder) holder).day.setChecked(true);
            } else if (!selectedDay.equals(selectDayModel)){
                ((SelectDayAdapter.MyViewHolder) holder).day.setChecked(false);
            }

            Intent intent = new Intent("Selected Day");
            intent.putExtra("SelectedDay", selectedDay);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            ((SelectDayAdapter.MyViewHolder) holder).day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SelectDayAdapter.MyViewHolder) holder).day.setChecked(true);
                    selectedDay = selectDayModel;
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectDayModelList.size();
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
        return position == selectDayModelList.size () + 1;
    }
}

