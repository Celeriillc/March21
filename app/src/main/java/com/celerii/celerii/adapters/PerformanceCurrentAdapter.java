package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EditYearActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.Activities.StudentPerformance.Current.PerformanceCurrentDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.PerformanceCurrentHeader;
import com.celerii.celerii.models.PerformanceCurrentModel;

import java.util.List;

/**
 * Created by user on 7/18/2017.
 */

public class PerformanceCurrentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SharedPreferencesManager sharedPreferencesManager;
    private List<PerformanceCurrentModel> performanceCurrentModelList;
    private PerformanceCurrentHeader performanceCurrentHeader;
    private Activity myActivity;
    private Context context;
    private String activeStudent;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, subjectScore, newBadge;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            subject = (TextView) view.findViewById(R.id.subject);
            subjectScore = (TextView) view.findViewById(R.id.subjectscore);
            newBadge = (TextView) view.findViewById(R.id.newbadge);
            this.view = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView term, year, average, classAverage, maxAverage, className, school;
        Button printThisResult;
        LinearLayout termLayout, yearLayout;

        public HeaderViewHolder(View view) {
            super(view);
            term = (TextView) view.findViewById(R.id.term);
            year = (TextView) view.findViewById(R.id.year);
            average = (TextView) view.findViewById(R.id.average);
            classAverage = (TextView) view.findViewById(R.id.classaverage);
            maxAverage = (TextView) view.findViewById(R.id.maxaverage);
            className = (TextView) view.findViewById(R.id.classname);
            school = (TextView) view.findViewById(R.id.school);
            printThisResult = (Button) view.findViewById(R.id.printthisresult);
            termLayout = (LinearLayout) view.findViewById(R.id.termlayout);
            yearLayout = (LinearLayout) view.findViewById(R.id.yearlayout);
        }
    }

    public PerformanceCurrentAdapter(List<PerformanceCurrentModel> performanceCurrentModelList, PerformanceCurrentHeader performanceCurrentHeader, Activity myActivity, Context context, String activeStudent) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.performanceCurrentModelList = performanceCurrentModelList;
        this.performanceCurrentHeader = performanceCurrentHeader;
        this.myActivity = myActivity;
        this.context = context;
        this.activeStudent = activeStudent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.performance_current_row, parent, false);
                return new PerformanceCurrentAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.performance_current_header, parent, false);
                return new PerformanceCurrentAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.performance_current_row, parent, false);
                return new PerformanceCurrentAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).term.setText(performanceCurrentHeader.getTerm());
            ((HeaderViewHolder) holder).year.setText(performanceCurrentHeader.getYear());
            ((HeaderViewHolder) holder).average.setText(TypeConverterClass.convStringToIntString(performanceCurrentHeader.getTermAverage()));
            ((HeaderViewHolder) holder).classAverage.setText(TypeConverterClass.convStringToIntString(performanceCurrentHeader.getClassAverage()));
            ((HeaderViewHolder) holder).maxAverage.setText(TypeConverterClass.convStringToIntString(performanceCurrentHeader.getMaxPossibleAverage()));
            ((HeaderViewHolder) holder).className.setText(performanceCurrentHeader.getClassName());
            ((HeaderViewHolder) holder).school.setText(performanceCurrentHeader.getSchool());

            ((HeaderViewHolder) holder).termLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditTermActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Term", performanceCurrentHeader.getTerm());
                    intent.putExtras(bundle);
                    ((Activity)context).startActivityForResult(intent, 0);
                }
            });

            ((HeaderViewHolder) holder).yearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditYearActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Year", performanceCurrentHeader.getYear());
                    intent.putExtras(bundle);
                    ((Activity)context).startActivityForResult(intent, 1);
                }
            });

        } else if (holder instanceof MyViewHolder) {
            final PerformanceCurrentModel performanceCurrentModel = performanceCurrentModelList.get(position);

            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                if (performanceCurrentModel.isNew()) {
                    ((MyViewHolder) holder).newBadge.setVisibility(View.VISIBLE);
                } else {
                    ((MyViewHolder) holder).newBadge.setVisibility(View.GONE);
                }
            }

            ((MyViewHolder) holder).subject.setText(performanceCurrentModel.getSubject());
            ((MyViewHolder) holder).subjectScore.setText(TypeConverterClass.convStringToIntString(String.valueOf(performanceCurrentModel.getCurrentScore())));
            ((MyViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent I = new Intent(context, PerformanceCurrentDetailActivity.class);
                    bundle.putString("Active Student", performanceCurrentHeader.getStudent());
                    bundle.putString("Subject", performanceCurrentModel.getSubject());
                    bundle.putString("Term", performanceCurrentHeader.getTerm());
                    bundle.putString("Year", performanceCurrentHeader.getYear());
                    I.putExtras(bundle);
                    context.startActivity(I);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return performanceCurrentModelList.size();
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
        return position == performanceCurrentModelList.size () + 1;
    }
}
