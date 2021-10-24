package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.SelectExamTypeModel;

import java.util.List;

public class SelectExamTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SelectExamTypeModel> selectExamTypeModelList;
    private String selectedExamType;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton examType;

        public MyViewHolder(final View view) {
            super(view);
            examType = (RadioButton) view.findViewById(R.id.examtype);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public SelectExamTypeAdapter(List<SelectExamTypeModel> selectExamTypeModelList, String selectedExamType, Context context) {
        this.selectExamTypeModelList = selectExamTypeModelList;
        this.selectedExamType = selectedExamType;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_exam_type_row, parent, false);
                return new MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_exam_type_header, parent, false);
                return new HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_exam_type_row, parent, false);
                return new MyViewHolder(rowView);
        }
    }



    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder){
            final SelectExamTypeModel selectExamTypeModel = selectExamTypeModelList.get(position);

            ((MyViewHolder) holder).examType.setText(selectExamTypeModel.getExamType());

            if (selectedExamType == null){
                selectedExamType = selectExamTypeModel.getExamType();
                ((MyViewHolder) holder).examType.setChecked(true);
            } else if (selectedExamType.equals(selectExamTypeModel.getExamType())){
                ((MyViewHolder) holder).examType.setChecked(true);
            } else if (!selectedExamType.equals(selectExamTypeModel.getExamType())){
                ((MyViewHolder) holder).examType.setChecked(false);
            }

            Intent intent = new Intent("Selected Exam Type");
            intent.putExtra("SelectedExamType", selectedExamType);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            ((MyViewHolder) holder).examType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyViewHolder) holder).examType.setChecked(true);
                    selectedExamType = selectExamTypeModel.getExamType();
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectExamTypeModelList.size();
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
        return position == selectExamTypeModelList.size () + 1;
    }
}
