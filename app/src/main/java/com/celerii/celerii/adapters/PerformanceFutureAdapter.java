package com.celerii.celerii.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.PerformanceFutureModel;
import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

/**
 * Created by user on 7/18/2017.
 */

public class PerformanceFutureAdapter extends RecyclerView.Adapter<PerformanceFutureAdapter.MyViewHolder> {

    private List<PerformanceFutureModel> performanceFutureModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, averageScoreLabel, averageScore, futureScoreLabel, futureScore;
        public View view;
        public ImageView img;

        public MyViewHolder(final View view) {
            super(view);
            subject = (TextView) view.findViewById(R.id.subject);
            averageScoreLabel = (TextView) view.findViewById(R.id.averagescorelabel);
            averageScore = (TextView) view.findViewById(R.id.averagescore);
            futureScoreLabel = (TextView) view.findViewById(R.id.forecastscorelabel);
            futureScore = (TextView) view.findViewById(R.id.forecastscore);
            img = (ImageView) view.findViewById(R.id.subjectimg);
            this.view = view;
        }
    }

    public PerformanceFutureAdapter(List<PerformanceFutureModel> performanceFutureModelList, Context context) {
        this.performanceFutureModelList = performanceFutureModelList;
        this.context = context;
    }

    @Override
    public PerformanceFutureAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.performance_future_row, parent, false);
        return new PerformanceFutureAdapter.MyViewHolder(itemView);
    }

    public void onBindViewHolder(PerformanceFutureAdapter.MyViewHolder holder, int position) {
        final PerformanceFutureModel performanceFutureModel = performanceFutureModelList.get(position);

        holder.subject.setText(performanceFutureModel.getSubject());
        holder.averageScore.setText(String.valueOf(performanceFutureModel.getAverageScore()));
        holder.futureScore.setText(String.valueOf(performanceFutureModel.getForecastScore()));

        String letter = String.valueOf(performanceFutureModel.getSubject().charAt(0));
//
//        ColorGenerator generator = ColorGenerator.MATERIAL;
//        int color;
//
//        if (parentAttendanceRow.getStatus() == "Present"){
//            color = Color.rgb(56,142,60);
//        }
//        else if (parentAttendanceRow.getStatus() == "Absent"){
//            color = Color.rgb(211,47,47);
//        }
//        else{
//            color = Color.rgb(251,192,45);
//        }
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(letter, Color.rgb(211,47,47));
        holder.img.setImageDrawable(textDrawable);

    }

    @Override
    public int getItemCount() {
        return performanceFutureModelList.size();
    }
}
