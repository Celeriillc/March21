package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.Activities.StudentPerformance.History.AcademicRecordDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.HistoryPerformanceBody;
import com.celerii.celerii.models.HistoryPerformanceHeader;
import com.github.mikephil.charting.charts.Chart;
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by user on 7/23/2017.
 */

public class HistoryPerformanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<HistoryPerformanceBody> historyPerformanceBodyList;
    private HistoryPerformanceHeader historyPerformanceHeader;
    SharedPreferencesManager sharedPreferencesManager;
    HashMap<Integer, String> chartDataLabel = new HashMap<Integer, String>();
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView className, term, year, score;
        View newBadge;
        public ImageView isIncrease;
        View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            term = (TextView) view.findViewById(R.id.term);
            year = (TextView) view.findViewById(R.id.year);
            score = (TextView) view.findViewById(R.id.score);
            newBadge = (View) view.findViewById(R.id.newbadge);
            isIncrease = (ImageView) view.findViewById(R.id.movefromlastscore);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView subject, averageScore, lastScore;
        LineChart chart;
        View clickableView;

        public HeaderViewHolder(View view) {
            super(view);
//            subject = (TextView) view.findViewById(R.id.subjectname);
            averageScore = (TextView) view.findViewById(R.id.averagescore);
//            lastScore = (TextView) view.findViewById(R.id.movefrompreviousscore);
            chart = (LineChart) view.findViewById(R.id.historychart);
            clickableView = view;
        }
    }

    public HistoryPerformanceAdapter(List<HistoryPerformanceBody> historyPerformanceBodyList, HistoryPerformanceHeader historyPerformanceHeader, Context context) {
        this.historyPerformanceBodyList = historyPerformanceBodyList;
        this.historyPerformanceHeader = historyPerformanceHeader;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_performance_body, parent, false);
                return new HistoryPerformanceAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_performance_header, parent, false);
                return new HistoryPerformanceAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_performance_body, parent, false);
                return new HistoryPerformanceAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof HeaderViewHolder){
            HistoryPerformanceHeader historyPerformanceHeader = this.historyPerformanceHeader;

            ((HeaderViewHolder) holder).averageScore.setText(TypeConverterClass.convStringToIntString(historyPerformanceHeader.getAverageScore()) + "%");

            final String[] xArray = historyPerformanceHeader.getxList();
            Double[] yArray = historyPerformanceHeader.getyList();
            final String[] xArrayModified = new String[xArray.length];
            for (int i = 0; i < xArrayModified.length; i++) {
                xArrayModified[i] = xArray[i].split("_")[0];
            }

            if (xArray != null ) {
                if (xArray.length > 1) {
                    IAxisValueFormatter formatter = new IAxisValueFormatter() {

                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return xArrayModified[(int) value];
                        }
                    };

                    XAxis xAxis = ((HeaderViewHolder) holder).chart.getXAxis();
                    xAxis.setGranularity(3f); // minimum axis-step (interval) is 1
                    xAxis.setValueFormatter(formatter);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawAxisLine(false);
                    xAxis.setDrawGridLines(false);
                    xAxis.setTextColor(Color.GRAY);

                    YAxis yAxis = ((HeaderViewHolder) holder).chart.getAxisLeft();
                    yAxis.setDrawGridLines(false);
                    yAxis.setDrawAxisLine(false);
                    yAxis.setGranularity(25f);
                    yAxis.setAxisMinimum(0f);
                    yAxis.setAxisMaximum(100f);
                    yAxis.setTextColor(Color.GRAY);

                    Legend legend = ((HeaderViewHolder) holder).chart.getLegend();
                    legend.setEnabled(false);

                    List<Entry> entries = new ArrayList<Entry>();
                    for (int i = 0; i < xArray.length; i++) {
                        entries.add(new Entry(i, yArray[i].floatValue()));
                        chartDataLabel.put(i, xArray[i]);
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "");
                    dataSet.setColor(Color.RED);
                    dataSet.setLineWidth(4f);
                    dataSet.setCircleColor(Color.RED);
                    LineData lineData = new LineData(dataSet);
                    lineData.setDrawValues(false);

                    Paint p = ((HeaderViewHolder) holder).chart.getPaint(Chart.PAINT_INFO);

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
                    IMarker marker = new YourMarkerView(context, chartDataLabel, R.layout.marker_view);
                    ((HeaderViewHolder) holder).chart.setMarker(marker);
                    ((HeaderViewHolder) holder).chart.invalidate(); // refresh
                }
            }
        }
        else if (holder instanceof MyViewHolder){
            final HistoryPerformanceBody historyPerformanceBody = this.historyPerformanceBodyList.get(position);

            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                if (historyPerformanceBody.isNew()) {
                    ((MyViewHolder) holder).newBadge.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).className.setTypeface(null, Typeface.BOLD);
                    ((MyViewHolder) holder).term.setTypeface(null, Typeface.BOLD);
                    ((MyViewHolder) holder).score.setTypeface(null, Typeface.BOLD);
                    ((MyViewHolder) holder).year.setTypeface(null, Typeface.BOLD);
                } else {
                    ((MyViewHolder) holder).newBadge.setVisibility(View.GONE);
                    ((MyViewHolder) holder).className.setTypeface(null, Typeface.NORMAL);
                    ((MyViewHolder) holder).term.setTypeface(null, Typeface.NORMAL);
                    ((MyViewHolder) holder).score.setTypeface(null, Typeface.NORMAL);
                    ((MyViewHolder) holder).year.setTypeface(null, Typeface.NORMAL);
                }
            }

            ((MyViewHolder) holder).className.setText(historyPerformanceBody.getClassName());
            ((MyViewHolder) holder).term.setText(Term.Term(historyPerformanceBody.getTerm()));
            ((MyViewHolder) holder).score.setText(historyPerformanceBody.getScoreNormalized());
            ((MyViewHolder) holder).year.setText(historyPerformanceBody.getYear());

            if (historyPerformanceBody.getIsIncrease().equals("true")){
                ((MyViewHolder) holder).isIncrease.setImageResource(R.drawable.ic_triangle_up);
            } else if (historyPerformanceBody.getIsIncrease().equals("false")) {
                ((MyViewHolder) holder).isIncrease.setImageResource(R.drawable.ic_triangle_down);
                ((MyViewHolder) holder).isIncrease.setScaleY(-1);
            } else {
                ((MyViewHolder) holder).isIncrease.setImageResource(R.drawable.ic_attendance_late_24dp);
            }

            ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    historyPerformanceBody.setNew(false);
                    notifyDataSetChanged();
                    Bundle bundle = new Bundle();
                    Intent I = new Intent(context, AcademicRecordDetailActivity.class);
                    bundle.putString("Active Student", historyPerformanceBody.getStudent());
                    bundle.putString("Subject", historyPerformanceBody.getSubject());
                    bundle.putString("Term", historyPerformanceBody.getTerm());
                    bundle.putString("Year", historyPerformanceBody.getYear());
                    I.putExtras(bundle);
                    context.startActivity(I);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return historyPerformanceBodyList.size();
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
        return position == historyPerformanceBodyList.size () + 1;
    }
}

class YourMarkerView extends MarkerView {

    private TextView term, year, score;
    HashMap<Integer, String> chartDataLabel;

    public YourMarkerView(Context context, HashMap<Integer, String> chartDataLabel, int layoutResource) {
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
