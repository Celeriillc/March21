package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.Settings.FAQActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.ELibraryMyAssignmentModel;
import com.celerii.celerii.models.FAQModel;

import java.util.List;

public class ELibraryMyAssignmentAdapter extends RecyclerView.Adapter<ELibraryMyAssignmentAdapter.MyViewHolder> {

    private SharedPreferencesManager sharedPreferencesManager;
    private List<ELibraryMyAssignmentModel> eLibraryMyAssignmentModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title, className, dueDate, averagePerformance;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            title = (TextView) view.findViewById(R.id.title);
            className = (TextView) view.findViewById(R.id.classname);
            dueDate = (TextView) view.findViewById(R.id.duedate);
            averagePerformance = (TextView) view.findViewById(R.id.averageperformance);
            clickableView = view;
        }
    }

    public ELibraryMyAssignmentAdapter(List<ELibraryMyAssignmentModel> eLibraryMyAssignmentModelList, Context context) {
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
        this.eLibraryMyAssignmentModelList = eLibraryMyAssignmentModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_library_my_assignment_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ELibraryMyAssignmentModel eLibraryMyAssignmentModel = eLibraryMyAssignmentModelList.get(position);

        holder.title.setText(eLibraryMyAssignmentModel.getMaterialTitle());
        holder.className.setText(eLibraryMyAssignmentModel.getClassName());
        holder.dueDate.setText(Date.getRelativeTimeSpanForward(eLibraryMyAssignmentModel.getDueDate()));
        holder.averagePerformance.setText(eLibraryMyAssignmentModel.getMaterialTitle());

        Glide.with(context)
                .load(eLibraryMyAssignmentModel.getMaterialThumbnailURL())
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .into(holder.image);

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("header", header);
//                bundle.putString("body", body);
//                Intent I = new Intent(context, FAQActivity.class);
//                I.putExtras(bundle);
//                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eLibraryMyAssignmentModelList.size();
    }
}
