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
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ELibraryMyTemplateModel;
import com.google.gson.Gson;

import java.util.List;

public class ELibraryLoadATemplateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ELibraryMyTemplateModel> eLibraryMyTemplateModelList;
    private String selectedTemplateString;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RadioButton templateTitle;

        public MyViewHolder(final View view) {
            super(view);
            templateTitle = (RadioButton) view.findViewById(R.id.year);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView header;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
        }
    }

    public ELibraryLoadATemplateAdapter(List<ELibraryMyTemplateModel> eLibraryMyTemplateModelList, String selectedTemplateString, Context context) {
        this.eLibraryMyTemplateModelList = eLibraryMyTemplateModelList;
        this.context = context;
        this.selectedTemplateString = selectedTemplateString;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_row, parent, false);
                return new MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_header, parent, false);
                return new HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_year_row, parent, false);
                return new MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).header.setText("Select Template");
        }
        else if (holder instanceof MyViewHolder) {
            final ELibraryMyTemplateModel eLibraryMyTemplateModel = eLibraryMyTemplateModelList.get(position);

            Gson gson = new Gson();
            final String selectedELibraryMyTemplateModelString = gson.toJson(eLibraryMyTemplateModel);

            ((MyViewHolder) holder).templateTitle.setText(eLibraryMyTemplateModel.getTemplateTitle());

            if (selectedTemplateString == null){
                selectedTemplateString = selectedELibraryMyTemplateModelString;
                ((MyViewHolder) holder).templateTitle.setChecked(true);
            } else if (selectedTemplateString.equals(selectedELibraryMyTemplateModelString)){
                ((MyViewHolder) holder).templateTitle.setChecked(true);
            } else if (!selectedTemplateString.equals(selectedELibraryMyTemplateModelString)){
                ((MyViewHolder) holder).templateTitle.setChecked(false);
            }

            Intent intent = new Intent("Selected Template");
            intent.putExtra("SelectedTemplate", selectedTemplateString);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            ((MyViewHolder) holder).templateTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MyViewHolder) holder).templateTitle.setChecked(true);
                    selectedTemplateString = selectedELibraryMyTemplateModelString;
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return eLibraryMyTemplateModelList.size();
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
        return position == eLibraryMyTemplateModelList.size () + 1;
    }
}
