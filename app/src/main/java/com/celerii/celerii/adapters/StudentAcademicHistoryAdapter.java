package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.models.StudentAcademicHistoryRowModel;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 8/21/2017.
 */

public class StudentAcademicHistoryAdapter extends RecyclerView.Adapter<StudentAcademicHistoryAdapter.MyViewHolder>{
    private List<StudentAcademicHistoryRowModel> studentAcademicHistoryRowModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView kidName, average;
        public ImageView kidPic;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            kidName = (TextView) view.findViewById(R.id.kidname);
            average = (TextView) view.findViewById(R.id.average);
            kidPic = (ImageView) view.findViewById(R.id.kidpic);
            clickableView = view;
        }
    }

    public StudentAcademicHistoryAdapter(List<StudentAcademicHistoryRowModel> studentAcademicHistoryRowModelList, Context context) {
        this.studentAcademicHistoryRowModelList = studentAcademicHistoryRowModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_academic_history_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StudentAcademicHistoryRowModel studentAcademicHistoryRowModel = studentAcademicHistoryRowModelList.get(position);

        holder.kidName.setText(studentAcademicHistoryRowModel.getName());
        holder.average.setText(studentAcademicHistoryRowModel.getAverage());
        if (!studentAcademicHistoryRowModel.getImageURL().isEmpty()) {
            Glide.with(context)
                    .load(studentAcademicHistoryRowModel.getImageURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.kidPic);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.kidPic);
        }
        final String kidNo = studentAcademicHistoryRowModel.getStudentID();
        final String kidName = studentAcademicHistoryRowModel.getName();

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Child ID", kidNo + " " + kidName);
                Intent I = new Intent(context, StudentPerformanceForParentsActivity.class);
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentAcademicHistoryRowModelList.size();
    }
}

