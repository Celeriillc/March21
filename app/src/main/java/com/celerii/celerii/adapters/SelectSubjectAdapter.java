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
import com.celerii.celerii.models.SelectSubjectModel;

import java.util.List;

/**
 * Created by DELL on 9/27/2018.
 */

public class SelectSubjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SelectSubjectModel> selectSubjectModelList;
    private String selectedSubject;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton subject;

        public MyViewHolder(final View view) {
            super(view);
            subject = (RadioButton) view.findViewById(R.id.subject);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public SelectSubjectAdapter(List<SelectSubjectModel> selectSubjectModelList, String selectedSubject, Context context) {
        this.selectSubjectModelList = selectSubjectModelList;
        this.context = context;
        this.selectedSubject = selectedSubject;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_subject_row, parent, false);
                return new SelectSubjectAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_subject_header, parent, false);
                return new SelectSubjectAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_subject_row, parent, false);
                return new SelectSubjectAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SelectSubjectAdapter.MyViewHolder){
            final SelectSubjectModel selectSubjectModel = selectSubjectModelList.get(position);

            ((MyViewHolder) holder).subject.setText(selectSubjectModel.getSubject());

            if (selectedSubject == null){
                selectedSubject = selectSubjectModel.getSubject();
                ((MyViewHolder) holder).subject.setChecked(true);
            } else if (selectedSubject.equals(selectSubjectModel.getSubject())){
                ((MyViewHolder) holder).subject.setChecked(true);
            } else if (!selectedSubject.equals(selectSubjectModel.getSubject())){
                ((MyViewHolder) holder).subject.setChecked(false);
            }

            Intent intent = new Intent("Selected Subject");
            intent.putExtra("SelectedSubject", selectedSubject);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            ((MyViewHolder) holder).subject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyViewHolder) holder).subject.setChecked(true);
                    selectedSubject = selectSubjectModel.getSubject();
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return selectSubjectModelList.size();
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
        return position == selectSubjectModelList.size () + 1;
    }
}
