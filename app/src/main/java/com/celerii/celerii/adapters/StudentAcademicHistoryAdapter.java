package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentAcademicHistoryRowModel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

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
        private LinearLayout clipper;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            kidName = (TextView) view.findViewById(R.id.kidname);
            average = (TextView) view.findViewById(R.id.average);
            kidPic = (ImageView) view.findViewById(R.id.kidpic);
            clipper = (LinearLayout) view.findViewById(R.id.clipper);
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
        final StudentAcademicHistoryRowModel studentAcademicHistoryRowModel = studentAcademicHistoryRowModelList.get(position);

        holder.kidName.setText(studentAcademicHistoryRowModel.getName());
        holder.average.setText(studentAcademicHistoryRowModel.getAverage());
        holder.clipper.setClipToOutline(true);

        Drawable textDrawable;
        if (!studentAcademicHistoryRowModel.getName().isEmpty()) {
            String[] nameArray = studentAcademicHistoryRowModel.getName().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
            }
            holder.kidPic.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
        }

        if (!studentAcademicHistoryRowModel.getImageURL().isEmpty()) {
            Glide.with(context)
                    .load(studentAcademicHistoryRowModel.getImageURL())
                    .placeholder(textDrawable)
                    .error(textDrawable)
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
                Gson gson = new Gson();
                String activeStudentJSON = gson.toJson(new Student(kidName.split(" ")[0], kidName.split(" ")[1], studentAcademicHistoryRowModel.getImageURL(), kidNo));
                bundle.putString("Child ID", activeStudentJSON);
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

