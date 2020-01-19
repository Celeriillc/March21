package com.celerii.celerii.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.AcademicRecordStudent;

import java.util.List;

/**
 * Created by DELL on 4/27/2019.
 */

public class PerformanceCurrentDetailAdapter extends RecyclerView.Adapter<PerformanceCurrentDetailAdapter.MyViewHolder> {
    private List<AcademicRecordStudent> AcademicRecordStudentList;
    SharedPreferencesManager sharedPreferencesManager;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView examType, score, className, date, maxObtainable, percentageOfTotal, term, year, newBadge;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            examType = (TextView) view.findViewById(R.id.examtype);
            score = (TextView) view.findViewById(R.id.score);
            className = (TextView) view.findViewById(R.id.classname);
            date = (TextView) view.findViewById(R.id.date);
            maxObtainable = (TextView) view.findViewById(R.id.maxobtainable);
            percentageOfTotal = (TextView) view.findViewById(R.id.percentageoftotal);
            term = (TextView) view.findViewById(R.id.term);
            year = (TextView) view.findViewById(R.id.year);
            newBadge = (TextView) view.findViewById(R.id.newbadge);
            clickableView = view;
        }
    }

    public PerformanceCurrentDetailAdapter(List<AcademicRecordStudent> AcademicRecordStudentList, Context context) {
        this.AcademicRecordStudentList = AcademicRecordStudentList;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.performance_current_detail_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        AcademicRecordStudent academicRecordStudent = AcademicRecordStudentList.get(position);

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            if (academicRecordStudent.isNew()) {
                holder.newBadge.setVisibility(View.VISIBLE);
            } else {
                holder.newBadge.setVisibility(View.GONE);
            }
        }

        holder.examType.setText(academicRecordStudent.getTestType());
        holder.score.setText(academicRecordStudent.getScore());
        holder.className.setText(academicRecordStudent.getClassName());
        holder.date.setText(Date.DateFormatMMDDYYYY(academicRecordStudent.getDate()));
        holder.maxObtainable.setText(academicRecordStudent.getMaxObtainable());
        holder.percentageOfTotal.setText(academicRecordStudent.getPercentageOfTotal() + "%");
        holder.term.setText(Term.Term(academicRecordStudent.getTerm()));
        holder.year.setText(academicRecordStudent.getAcademicYear());
    }

    @Override
    public int getItemCount() {
        return AcademicRecordStudentList.size();
    }
}
