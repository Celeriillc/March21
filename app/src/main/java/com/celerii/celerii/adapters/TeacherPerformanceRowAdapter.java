package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.TeacherPerformance.TeacherPerformanceActivityMain;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.models.TeacherPerformanceHeader;
import com.celerii.celerii.models.TeacherPerformanceRow;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * Created by DELL on 8/19/2017.
 */

public class TeacherPerformanceRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<TeacherPerformanceRow> teacherPerformanceRowList;
    private Context context;
    private TeacherPerformanceHeader teacherPerformanceHeader;
//    public static final int Header = 1;
//    public static final int Normal = 2;
//    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView pastScore, currentScore, futureScore, subject;
        public ImageView subjectPic, progressImg;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            pastScore = (TextView) view.findViewById(R.id.previousaveragescore);
//            currentScore = (TextView) view.findViewById(R.id.currentaveragescore);
//            futureScore = (TextView) view.findViewById(R.id.projectedaveragescore);
            subject = (TextView) view.findViewById(R.id.subject);
            subjectPic = (ImageView) view.findViewById(R.id.subjectimg);
//            progressImg = (ImageView) view.findViewById(R.id.progressimg);
            clickableView = view;
        }
    }

//    public class HeaderViewHolder extends RecyclerView.ViewHolder {
//        TextView pastScoreHint, currentScoreHint, futureScoreHint;
//        LinearLayout header;
//
//        public HeaderViewHolder(View view) {
//            super(view);
//            pastScoreHint = (TextView) view.findViewById(R.id.previousaveragescorehint);
//            currentScoreHint = (TextView) view.findViewById(R.id.currentaveragescorehint);
//            futureScoreHint = (TextView) view.findViewById(R.id.projectedaveragescorehint);
//            header = (LinearLayout) view.findViewById(R.id.header);
//        }
//    }

    public TeacherPerformanceRowAdapter(List<TeacherPerformanceRow> teacherPerformanceRowList, TeacherPerformanceHeader teacherPerformanceHeader,
                              Context context) {
        this.teacherPerformanceRowList = teacherPerformanceRowList;
        this.teacherPerformanceHeader = teacherPerformanceHeader;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
//            case Normal:
//                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_performance_row, parent, false);
//                return new TeacherPerformanceRowAdapter.MyViewHolder(rowView);
//            case Header:
//                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_performance_row_header, parent, false);
//                return new TeacherPerformanceRowAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_performance_row, parent, false);
                return new TeacherPerformanceRowAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final TeacherPerformanceRow teacherPerformanceRow = teacherPerformanceRowList.get(position);

        if (!teacherPerformanceRow.isWithValue()){
            ((MyViewHolder) holder).clickableView.setVisibility(View.GONE);
            return;
        }

        String score = teacherPerformanceRow.getPreviousScore() + "%";
        ((MyViewHolder) holder).pastScore.setText(score);
//        ((MyViewHolder) holder).currentScore.setText(teacherPerformanceRow.getCurrentScore());
//        ((MyViewHolder) holder).futureScore.setText(teacherPerformanceRow.getProjectedScore());
        ((MyViewHolder) holder).subject.setText(teacherPerformanceRow.getSubject());

//        String previousScoreString = teacherPerformanceRow.getPreviousScore();
//        String currentScoreString = teacherPerformanceRow.getCurrentScore();
//
//        if (previousScoreString.equals("NA")){
//            previousScoreString = "0";
//        }
//        if (currentScoreString.equals("NA")){
//            currentScoreString = "0";
//        }

//        Double previousScore = (Double.valueOf(previousScoreString));
//        Double currentScore = (Double.valueOf(currentScoreString));
        String letter = String.valueOf(teacherPerformanceRow.getSubject().charAt(0));

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = Color.GRAY;

        Drawable textDrawable = TextDrawable.builder()
                .buildRound(letter, color);

//        if (previousScore > currentScore){
//            ((MyViewHolder) holder).progressImg.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
//            textDrawable = TextDrawable.builder()
//                    .buildRound(letter, Color.argb(255, 255, 0, 0));
//        } else if (previousScore < currentScore){
//            ((MyViewHolder) holder).progressImg.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
//
//        } else {
//            ((MyViewHolder) holder).progressImg.setImageResource(R.drawable.ic_attendance_late_24dp);
//        }

        textDrawable = CreateTextDrawable.createTextDrawable(context, letter,40);
        ((MyViewHolder) holder).subjectPic.setImageDrawable(textDrawable);

        ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Subject", teacherPerformanceRow.getSubject());
                Intent I = new Intent(context, TeacherPerformanceActivityMain.class);
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teacherPerformanceRowList.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//
//        if(isPositionHeader (position)) {
//            return Header;
//        } else if(isPositionFooter (position)) {
//            return Footer;
//        }
//        return Normal;
//    }
//
//    private boolean isPositionHeader (int position) {
//        return position == 0;
//    }
//
//    private boolean isPositionFooter (int position) {
//        return position == teacherPerformanceRowList.size () + 1;
//    }
}
