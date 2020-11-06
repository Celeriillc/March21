package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.TeacherPerformance.TeacherAcademicRecordDetailActivity;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.TeacherPerformanceHeaderMain;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by DELL on 8/20/2017.
 */

public class TeacherPerformanceMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<TeacherPerformanceRowMain> teacherPerformanceRowMainList;
    private Context context;
    private TeacherPerformanceHeaderMain teacherPerformanceHeaderMain;
    HashMap<Integer, String> chartDataLabel = new HashMap<Integer, String>();
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView term, year, score, className;
        public ImageView isIncrease;
        View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            term = (TextView) view.findViewById(R.id.term);
            year = (TextView) view.findViewById(R.id.year);
            score = (TextView) view.findViewById(R.id.score);
            className = (TextView) view.findViewById(R.id.classname);
            isIncrease = (ImageView) view.findViewById(R.id.movefromlastscore);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        View previousScoreView, currentScoreView, projectedScoreView;
        TextView previousScore, currentScore, projectedScore, upFrom, downFrom;
        TextView previousClass, previousMaxObtainable, previousTerm, previousYear, previousLabel;
        TextView currentClass, currentMaxObtainable, currentTerm, currentYear, currentLabel;
        TextView projectedClass, projectedMaxObtainable, projectedTerm, projectedYear, projectedLabel;
        LineChart chart;
        LinearLayout upfromLayout, downfromLayout;

        public HeaderViewHolder(View view) {
            super(view);

//            previousScoreView = (View) view.findViewById(R.id.pastaveragescore);
            currentScoreView = (View) view.findViewById(R.id.currentaveragescore);
//            projectedScoreView = (View) view.findViewById(R.id.projectedaveragescore);

//            previousScore = (TextView) previousScoreView.findViewById(R.id.score);
            currentScore = (TextView) currentScoreView.findViewById(R.id.score);
//            projectedScore = (TextView) projectedScoreView.findViewById(R.id.score);
//            upFrom = (TextView) currentScoreView.findViewById(R.id.upfrom);
//            downFrom = (TextView) currentScoreView.findViewById(R.id.downfrom);

//            previousClass = (TextView) previousScoreView.findViewById(R.id.classname);
//            previousMaxObtainable = (TextView) previousScoreView.findViewById(R.id.maxobtainable);
//            previousTerm = (TextView) previousScoreView.findViewById(R.id.term);
//            previousYear = (TextView) previousScoreView.findViewById(R.id.year);
//            previousLabel = (TextView) previousScoreView.findViewById(R.id.label);

//            currentClass = (TextView) currentScoreView.findViewById(R.id.classname);
//            currentMaxObtainable = (TextView) currentScoreView.findViewById(R.id.maxobtainable);
//            currentTerm = (TextView) currentScoreView.findViewById(R.id.term);
//            currentYear = (TextView) currentScoreView.findViewById(R.id.year);
            currentLabel = (TextView) currentScoreView.findViewById(R.id.label);

//            projectedClass = (TextView) projectedScoreView.findViewById(R.id.classname);
//            projectedMaxObtainable = (TextView) projectedScoreView.findViewById(R.id.maxobtainable);
//            projectedTerm = (TextView) projectedScoreView.findViewById(R.id.term);
//            projectedYear = (TextView) projectedScoreView.findViewById(R.id.year);
//            projectedLabel = (TextView) projectedScoreView.findViewById(R.id.label);

            chart = (LineChart) view.findViewById(R.id.historychart);

//            upfromLayout = (LinearLayout) currentScoreView.findViewById(R.id.arrowupfrom);
//            downfromLayout = (LinearLayout) currentScoreView.findViewById(R.id.arrowdownfrom);
        }
    }

    public TeacherPerformanceMainAdapter(List<TeacherPerformanceRowMain> teacherPerformanceRowMainList, TeacherPerformanceHeaderMain teacherPerformanceHeaderMain, Context context) {
        this.teacherPerformanceRowMainList = teacherPerformanceRowMainList;
        this.teacherPerformanceHeaderMain = teacherPerformanceHeaderMain;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_performance_row_main, parent, false);
                return new TeacherPerformanceMainAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_performance_header_main, parent, false);
                return new TeacherPerformanceMainAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_performance_row_main, parent, false);
                return new TeacherPerformanceMainAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof HeaderViewHolder){
            TeacherPerformanceHeaderMain teacherPerformanceHeaderMain = this.teacherPerformanceHeaderMain;
//            ((HeaderViewHolder) holder).projectedScoreView.setVisibility(View.GONE);

//            ((HeaderViewHolder) holder).previousScore.setText(appendPercentage(teacherPerformanceHeaderMain.getPreviousScore()));
            ((HeaderViewHolder) holder).currentScore.setText(appendPercentage(teacherPerformanceHeaderMain.getCurrentScore()));
            ((HeaderViewHolder) holder).currentLabel.setText("Overall Average Score");
//            ((HeaderViewHolder) holder).projectedScore.setText(appendPercentage(teacherPerformanceHeaderMain.getProjectedScore()));

//            ((HeaderViewHolder) holder).upFrom.setText(teacherPerformanceHeaderMain.getPreviousScore());
//            ((HeaderViewHolder) holder).downFrom.setText(teacherPerformanceHeaderMain.getPreviousScore());

//            String previousScoreString = teacherPerformanceHeaderMain.getPreviousScore();
//            String currentScoreString = teacherPerformanceHeaderMain.getCurrentScore();

//            if (previousScoreString.equals("NA") || previousScoreString.equals("")){
//                previousScoreString = "0";
//            }
//            if (currentScoreString.equals("NA") || currentScoreString.equals("")){
//                currentScoreString = "0";
//            }

//            Double previousScore = (Double.valueOf(previousScoreString));
//            Double currentScore = (Double.valueOf(currentScoreString));

//            if (previousScore > currentScore) {
//                ((HeaderViewHolder) holder).downfromLayout.setVisibility(View.VISIBLE);
//                ((HeaderViewHolder) holder).downFrom.setVisibility(View.VISIBLE);
//            } else if (previousScore < currentScore) {
//                ((HeaderViewHolder) holder).upfromLayout.setVisibility(View.VISIBLE);
//                ((HeaderViewHolder) holder).upFrom.setVisibility(View.VISIBLE);
//            }

//            ((HeaderViewHolder) holder).previousClass.setText(teacherPerformanceHeaderMain.getPreviousClass());
//            ((HeaderViewHolder) holder).previousMaxObtainable.setText(teacherPerformanceHeaderMain.getPreviousMaxObtainable());
//            String term = Term.Term(teacherPerformanceHeaderMain.getPreviousTerm());
//            ((HeaderViewHolder) holder).previousTerm.setText(term);
//            ((HeaderViewHolder) holder).previousYear.setText(teacherPerformanceHeaderMain.getPreviousYear());
//            ((HeaderViewHolder) holder).previousLabel.setText("Previous Average Score");

//            ((HeaderViewHolder) holder).currentClass.setText(teacherPerformanceHeaderMain.getCurrentClass());
//            ((HeaderViewHolder) holder).currentMaxObtainable.setText(teacherPerformanceHeaderMain.getCurrentMaxObtainable());
//            String term = Term.Term(teacherPerformanceHeaderMain.getCurrentTerm());
//            ((HeaderViewHolder) holder).currentTerm.setText(term);
//            ((HeaderViewHolder) holder).currentYear.setText(teacherPerformanceHeaderMain.getCurrentYear());
//            ((HeaderViewHolder) holder).currentLabel.setText("Average Score");

//            ((HeaderViewHolder) holder).projectedClass.setText(teacherPerformanceHeaderMain.getProjectedClass());
//            ((HeaderViewHolder) holder).projectedMaxObtainable.setText(teacherPerformanceHeaderMain.getProjectedMaxObtainable());
//            term = Term.Term(teacherPerformanceHeaderMain.getProjectedTerm());
//            ((HeaderViewHolder) holder).projectedTerm.setText(term);
//            ((HeaderViewHolder) holder).projectedYear.setText(teacherPerformanceHeaderMain.getProjectedYear());
//            ((HeaderViewHolder) holder).projectedLabel.setText("Projected Average Score");

            final String[] xArray = teacherPerformanceHeaderMain.getxList();
            Double[] yArray = teacherPerformanceHeaderMain.getyList();
            final String[] xArrayModified = new String[xArray.length];
            for (int i = 0; i < xArrayModified.length; i++) {
                xArrayModified[i] = xArray[i].split("_")[0];
            }

            if (xArray.length > 1) {
                IndexAxisValueFormatter formatter = new  IndexAxisValueFormatter() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return xArrayModified[(int) value];
                    }
                };

                XAxis xAxis = ((HeaderViewHolder) holder).chart.getXAxis();
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawAxisLine(false);
                xAxis.setDrawGridLines(false);
                xAxis.setTextColor(Color.GRAY);
                xAxis.setEnabled(false);

                YAxis yAxis = ((HeaderViewHolder) holder).chart.getAxisLeft();
                yAxis.setDrawGridLines(false);
                yAxis.setDrawAxisLine(false);
                yAxis.setGranularity(25f);
                yAxis.setAxisMinimum(0f);
                yAxis.setAxisMaximum(100f);
                yAxis.setTextColor(Color.GRAY);
                yAxis.setEnabled(false);

                Legend legend = ((HeaderViewHolder) holder).chart.getLegend();
                legend.setEnabled(false);

                List<Entry> entries = new ArrayList<Entry>();
                for (int i = 0; i < xArray.length; i++) {
                    entries.add(new Entry(i, yArray[i].floatValue()));
                    chartDataLabel.put(i, xArray[i]);
                }

                LineDataSet dataSet = new LineDataSet(entries, "");
                dataSet.setColor(ContextCompat.getColor(context, R.color.colorTransparentPurple));
                dataSet.setLineWidth(1f);
                dataSet.setCircleColor(ContextCompat.getColor(context, R.color.colorTransparentPurple));
                dataSet.setDrawFilled(true);
                dataSet.setFillDrawable(ContextCompat.getDrawable(context, R.drawable.fade_accent_for_chart));
                LineData lineData = new LineData(dataSet);
                lineData.setDrawValues(false);

                ((HeaderViewHolder) holder).chart.setData(lineData);
                ((HeaderViewHolder) holder).chart.setBackgroundColor(Color.WHITE);
                ((HeaderViewHolder) holder).chart.getAxisRight().setEnabled(false);
                ((HeaderViewHolder) holder).chart.getDescription().setEnabled(true);
                ((HeaderViewHolder) holder).chart.setDrawBorders(false);
                ((HeaderViewHolder) holder).chart.setVisibleXRangeMaximum(6);
                ((HeaderViewHolder) holder).chart.setNoDataText("");
                ((HeaderViewHolder) holder).chart.getDescription().setEnabled(false);
                ((HeaderViewHolder) holder).chart.getXAxis().setSpaceMin(0.05f);
                ((HeaderViewHolder) holder).chart.getXAxis().setSpaceMax(0.05f);
                IMarker marker = new TeacherPerformanceMarkerView(context, chartDataLabel, R.layout.marker_view);
                ((HeaderViewHolder) holder).chart.setMarker(marker);
                ((HeaderViewHolder) holder).chart.invalidate(); // refresh
            }

        }
        else if (holder instanceof MyViewHolder){
            final TeacherPerformanceRowMain teacherPerformanceRowMain = this.teacherPerformanceRowMainList.get(position);

            ((MyViewHolder) holder).className.setText(teacherPerformanceRowMain.getClassName());
            ((MyViewHolder) holder).term.setText(Term.TermShort(teacherPerformanceRowMain.getTerm()));
            ((MyViewHolder) holder).year.setText(teacherPerformanceRowMain.getYear());
            ((MyViewHolder) holder).score.setText(Double.valueOf(teacherPerformanceRowMain.getScore()).intValue() + "%");

            if (teacherPerformanceRowMain.getIncrease().equals("true")){
                ((MyViewHolder) holder).isIncrease.setImageResource(R.drawable.ic_triangle_up);
            } else if (teacherPerformanceRowMain.getIncrease().equals("false")){
                ((MyViewHolder) holder).isIncrease.setImageResource(R.drawable.ic_triangle_down);
                ((MyViewHolder) holder).isIncrease.setScaleY(-1);
            }
            else {
                ((MyViewHolder) holder).isIncrease.setImageResource(R.drawable.ic_attendance_late_24dp);
            }

            ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent I = new Intent(context, TeacherAcademicRecordDetailActivity.class);
                    bundle.putString("Active Teacher", teacherPerformanceRowMain.getTeacherID());
                    bundle.putString("Subject", teacherPerformanceRowMain.getSubject());
                    bundle.putString("Term", teacherPerformanceRowMain.getTerm());
                    bundle.putString("Year", teacherPerformanceRowMain.getYear());
                    bundle.putString("Class", teacherPerformanceRowMain.getClassID());
                    I.putExtras(bundle);
                    context.startActivity(I);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return teacherPerformanceRowMainList.size();
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
        return position == teacherPerformanceRowMainList.size () + 1;
    }

    private String appendPercentage(String inputString){
        if (inputString.equals("NA")){
            return inputString;
        } else {
            return inputString + "%";
        }
    }
}

class TeacherPerformanceMarkerView extends MarkerView {

    private TextView term, year, score;
    HashMap<Integer, String> chartDataLabel;

    public TeacherPerformanceMarkerView(Context context, HashMap<Integer, String> chartDataLabel, int layoutResource) {
        super(context, layoutResource);

        this.chartDataLabel = chartDataLabel;

        // find your layout components
        term = (TextView) findViewById(R.id.term);
        year = (TextView) findViewById(R.id.year);
        score = (TextView) findViewById(R.id.score);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        Integer xIndex = (int) e.getX();
        String x = chartDataLabel.get(xIndex);
        String y = TypeConverterClass.convStringToIntString(String.valueOf(e.getY()));
        String yearString = x.split("_")[0];
        String termString = x.split("_")[1];

        if (term.equals("10")) termString = "1";
        termString = Term.TermShort(termString);

        term.setText("Term : " + termString);
        year.setText("Year : " + yearString);
        score.setText("Score : " + y + "%");

        // this will perform necessary layouting
        super.refreshContent(e, highlight);
    }

    private MPPointF mOffset;

    @Override
    public MPPointF getOffset() {

        if(mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = new MPPointF(-(getWidth() / 2), -getHeight());
        }

        return mOffset;
    }
}