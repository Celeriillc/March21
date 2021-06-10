package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.Activities.ELibrary.Teacher.CreateEditTemplateActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ELibraryMyTemplateModel;

import java.util.List;

public class ELibraryMyTemplateAdapter extends RecyclerView.Adapter<ELibraryMyTemplateAdapter.MyViewHolder> {

    private SharedPreferencesManager sharedPreferencesManager;
    private List<ELibraryMyTemplateModel> eLibraryMyTemplateModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, noOfUses, date;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            noOfUses = (TextView) view.findViewById(R.id.noofuses);
            date = (TextView) view.findViewById(R.id.date);
            clickableView = view;
        }
    }

    public ELibraryMyTemplateAdapter(List<ELibraryMyTemplateModel> eLibraryMyTemplateModelList, Context context) {
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
        this.eLibraryMyTemplateModelList = eLibraryMyTemplateModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_library_my_templates_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ELibraryMyTemplateModel eLibraryMyTemplateModel = eLibraryMyTemplateModelList.get(position);

        String numberOfUses = "Number of uses: " + eLibraryMyTemplateModel.getNumberOfUses();
        String date = "Date Created: " + Date.DateFormatMMDDYYYY(eLibraryMyTemplateModel.getDate());

        holder.title.setText(eLibraryMyTemplateModel.getTemplateTitle());
        holder.noOfUses.setText(numberOfUses);
        holder.date.setText(date);

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, CreateEditTemplateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Nav Type", "Edit");
                bundle.putString("templateID", eLibraryMyTemplateModel.getTemplateID());
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eLibraryMyTemplateModelList.size();
    }
}
