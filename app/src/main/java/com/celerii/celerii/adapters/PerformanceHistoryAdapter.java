package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.Activities.StudentPerformance.History.HistoryPerformanceActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.PerformanceHistoryModel;
import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

/**
 * Created by user on 7/18/2017.
 */

public class PerformanceHistoryAdapter extends RecyclerView.Adapter<PerformanceHistoryAdapter.MyViewHolder> {

    private List<PerformanceHistoryModel> performanceHistoryModelList;
    SharedPreferencesManager sharedPreferencesManager;
    private Context context;
    private String activeStudent;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, averageScoreLabel, averageScore, newBadge;
        public View view;
        public ImageView imageBadge;

        public MyViewHolder(final View view) {
            super(view);
            imageBadge = (ImageView) view.findViewById(R.id.subjectimg);
            subject = (TextView) view.findViewById(R.id.subject);
            averageScoreLabel = (TextView) view.findViewById(R.id.averagescorelabel);
            averageScore = (TextView) view.findViewById(R.id.averagescore);
            newBadge = (TextView) view.findViewById(R.id.newbadge);
            this.view = view;
        }
    }

    public PerformanceHistoryAdapter(List<PerformanceHistoryModel> performanceHistoryModelList, Context context, String activeStudent) {
        this.performanceHistoryModelList = performanceHistoryModelList;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.context = context;
        this.activeStudent = activeStudent;
    }

    @Override
    public PerformanceHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.performance_history_row, parent, false);
        return new PerformanceHistoryAdapter.MyViewHolder(itemView);
    }

    public void onBindViewHolder(PerformanceHistoryAdapter.MyViewHolder holder, int position) {
        final PerformanceHistoryModel performanceHistoryModel = performanceHistoryModelList.get(position);

        if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            if (performanceHistoryModel.isNew()) {
                holder.newBadge.setVisibility(View.VISIBLE);
                holder.averageScoreLabel.setTypeface(null, Typeface.BOLD);
            } else {
                holder.newBadge.setVisibility(View.GONE);
                holder.averageScoreLabel.setTypeface(null, Typeface.NORMAL);
            }
        }

        holder.subject.setText(performanceHistoryModel.getSubject());
        String Score = String.valueOf(performanceHistoryModel.getAverageScore());
        holder.averageScore.setText(Score);

        String letter = String.valueOf(performanceHistoryModel.getSubject().charAt(0));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performanceHistoryModel.setNew(false);
                notifyDataSetChanged();
                Intent I = new Intent(context, HistoryPerformanceActivity.class);
                Bundle b = new Bundle();
                b.putString("Active Student", activeStudent);
                b.putString("Subject", performanceHistoryModel.getSubject());
                I.putExtras(b);
                context.startActivity(I);
            }
        });

        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(letter, Color.rgb(211,47,47));
        holder.imageBadge.setImageDrawable(textDrawable);

    }

    @Override
    public int getItemCount() {
        return performanceHistoryModelList.size();
    }
}
