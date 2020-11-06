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

public class SelectClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> selectClassModelList;
    private String selectedClass;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton className;

        public MyViewHolder(final View view) {
            super(view);
            className = (RadioButton) view.findViewById(R.id.year);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public SelectClassAdapter(List<String> selectClassModelList, String selectedClass, Context context) {
        this.selectClassModelList = selectClassModelList;
        this.context = context;
        this.selectedClass = selectedClass;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_row, parent, false);
                return new SelectClassAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_header, parent, false);
                return new SelectClassAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_row, parent, false);
                return new SelectClassAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  HeaderViewHolder) {
            ((HeaderViewHolder) holder).header.setText("Select Class");
        }
        else if (holder instanceof MyViewHolder) {
            final String selectClassModel = selectClassModelList.get(position);

            ((MyViewHolder) holder).className.setText(selectClassModel);

            if (selectedClass == null){
                selectedClass = selectClassModel;
                ((MyViewHolder) holder).className.setChecked(true);
            } else if (selectedClass.equals(selectClassModel)){
                ((MyViewHolder) holder).className.setChecked(true);
            } else if (!selectedClass.equals(selectClassModel)){
                ((MyViewHolder) holder).className.setChecked(false);
            }

            Intent intent = new Intent("Selected Class");
            intent.putExtra("SelectedClass", selectedClass);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            ((MyViewHolder) holder).className.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyViewHolder) holder).className.setChecked(true);
                    selectedClass = selectClassModel;
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectClassModelList.size();
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
        return position == selectClassModelList.size () + 1;
    }
}
