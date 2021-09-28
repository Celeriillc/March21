package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.celerii.celerii.Activities.StudentPerformance.History.StudentYearTermPerformanceDetailActivity;
import com.celerii.celerii.Activities.TeacherPerformance.TeacherPerformanceActivityMain;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.models.StudentYearTermPerformanceHeader;
import com.celerii.celerii.models.StudentYearTermPerformanceModel;
import com.celerii.celerii.models.TeacherPerformanceHeader;
import com.celerii.celerii.models.TeacherPerformanceRow;

import java.util.List;

public class StudentYearTermPerformanceAdapter extends RecyclerView.Adapter<StudentYearTermPerformanceAdapter.MyViewHolder>{
    private List<StudentYearTermPerformanceModel> studentYearTermPerformanceModelList;
    private StudentYearTermPerformanceHeader studentYearTermPerformanceHeader;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView pastScore, subject;
        public ImageView subjectPic;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            pastScore = (TextView) view.findViewById(R.id.previousaveragescore);
            subject = (TextView) view.findViewById(R.id.subject);
            subjectPic = (ImageView) view.findViewById(R.id.subjectimg);
            clickableView = view;
        }
    }

    public StudentYearTermPerformanceAdapter(List<StudentYearTermPerformanceModel> studentYearTermPerformanceModelList, StudentYearTermPerformanceHeader studentYearTermPerformanceHeader, Context context) {
        this.studentYearTermPerformanceModelList = studentYearTermPerformanceModelList;
        this.studentYearTermPerformanceHeader = studentYearTermPerformanceHeader;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_year_term_performance_row, parent, false);
        return new MyViewHolder(rowView);

    }

    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final StudentYearTermPerformanceModel studentYearTermPerformanceModel = studentYearTermPerformanceModelList.get(position);

        if (!studentYearTermPerformanceModel.isWithValue()){
            holder.clickableView.setVisibility(View.GONE);
            return;
        }

        String score = studentYearTermPerformanceModel.getPreviousScore() + "%";
        ((MyViewHolder) holder).pastScore.setText(score);
        ((MyViewHolder) holder).subject.setText(studentYearTermPerformanceModel.getSubject());
        String letter = String.valueOf(studentYearTermPerformanceModel.getSubject().charAt(0));

        Drawable textDrawable = CreateTextDrawable.createTextDrawable(context, letter,40);
        ((MyViewHolder) holder).subjectPic.setImageDrawable(textDrawable);

        ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentYearTermPerformanceDetailActivity.studentID = studentYearTermPerformanceHeader.getStudentID();
                StudentYearTermPerformanceDetailActivity.subject = studentYearTermPerformanceModel.getSubject();
                StudentYearTermPerformanceDetailActivity.subjectRecord = studentYearTermPerformanceHeader.getSubjectRecord().get(studentYearTermPerformanceModel.getSubject());
                Intent I = new Intent(context, StudentYearTermPerformanceDetailActivity.class);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentYearTermPerformanceModelList.size();
    }
}
