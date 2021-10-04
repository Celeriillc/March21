package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EditYearActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.Activities.StudentPerformance.Current.PerformanceCurrentDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.PerformanceCurrentHeader;
import com.celerii.celerii.models.PerformanceCurrentModel;
import com.celerii.celerii.models.SubscriptionModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
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
    private String parentActivity;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, caScore, examScore, subjectScore, newBadge;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            subject = (TextView) view.findViewById(R.id.subject);
            caScore = (TextView) view.findViewById(R.id.cascore);
            examScore = (TextView) view.findViewById(R.id.examscore);
            subjectScore = (TextView) view.findViewById(R.id.subjectscore);
            newBadge = (TextView) view.findViewById(R.id.newbadge);
            this.view = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView term, year, average, classAverage, maxAverage, className, school, noDataText;
        Button printThisResult;
        ProgressBar termAverageProgressBar, classAverageProgressBar, maxObtainableProgressBar;
        LinearLayout termLayout, yearLayout, noDataLayout, chiefLayout;

        public HeaderViewHolder(View view) {
            super(view);
            term = (TextView) view.findViewById(R.id.term);
            year = (TextView) view.findViewById(R.id.year);
            average = (TextView) view.findViewById(R.id.termaveragescore);
            classAverage = (TextView) view.findViewById(R.id.classaveragescore);
            maxAverage = (TextView) view.findViewById(R.id.maxobtainablescore);
            className = (TextView) view.findViewById(R.id.classname);
            school = (TextView) view.findViewById(R.id.school);
            noDataText = (TextView) view.findViewById(R.id.nodatatext);
            printThisResult = (Button) view.findViewById(R.id.printthisresult);
            termAverageProgressBar = (ProgressBar) view.findViewById(R.id.termaverageprogressbar);
            classAverageProgressBar = (ProgressBar) view.findViewById(R.id.classaverageprogressbar);
            maxObtainableProgressBar = (ProgressBar) view.findViewById(R.id.maxobtainableprogressbar);
            termLayout = (LinearLayout) view.findViewById(R.id.termlayout);
            yearLayout = (LinearLayout) view.findViewById(R.id.yearlayout);
            noDataLayout = (LinearLayout) view.findViewById(R.id.nodatalayout);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
        }
    }

    public PerformanceCurrentAdapter(List<PerformanceCurrentModel> performanceCurrentModelList, PerformanceCurrentHeader performanceCurrentHeader, Activity myActivity, Context context, String activeStudent, String parentActivity) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.performanceCurrentModelList = performanceCurrentModelList;
        this.performanceCurrentHeader = performanceCurrentHeader;
        this.myActivity = myActivity;
        this.context = context;
        this.activeStudent = activeStudent;
        this.parentActivity = parentActivity;
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
            ((HeaderViewHolder) holder).term.setText(Term.Term(performanceCurrentHeader.getTerm()));
            ((HeaderViewHolder) holder).year.setText(performanceCurrentHeader.getYear());
            int termAverage = 0;
            int classAverage = 0;
            int maxAverage = 0;
            if (!performanceCurrentHeader.getTermAverage().trim().isEmpty()) {
                termAverage = Integer.parseInt(performanceCurrentHeader.getTermAverage());
            }
            if (!performanceCurrentHeader.getClassAverage().trim().isEmpty()) {
                classAverage = Integer.parseInt(performanceCurrentHeader.getClassAverage());
            }
            if (!performanceCurrentHeader.getMaxPossibleAverage().trim().isEmpty()) {
                maxAverage = Integer.parseInt(performanceCurrentHeader.getMaxPossibleAverage());
            }
            ((HeaderViewHolder) holder).termAverageProgressBar.setProgress(termAverage);
            ((HeaderViewHolder) holder).classAverageProgressBar.setProgress(classAverage);
            ((HeaderViewHolder) holder).maxObtainableProgressBar.setProgress(maxAverage);

            String termAverageString = String.valueOf(termAverage) + "%";
            String classAverageString = String.valueOf(classAverage) + "%";
            String maxAverageString = String.valueOf(maxAverage) + "%";

            ((HeaderViewHolder) holder).average.setText(termAverageString);
            ((HeaderViewHolder) holder).classAverage.setText(classAverageString);
            ((HeaderViewHolder) holder).maxAverage.setText(maxAverageString);

            ((HeaderViewHolder) holder).className.setText(performanceCurrentHeader.getClassName());
            ((HeaderViewHolder) holder).school.setText(performanceCurrentHeader.getSchool());

            if (performanceCurrentModelList.size() <= 1){
                ((HeaderViewHolder) holder).noDataLayout.setVisibility(View.VISIBLE);
                String message = "There are no academic records for the "  + "<b>" + Term.Term(performanceCurrentHeader.getTerm()) + "</b>" + " of " +  "<b>" +  performanceCurrentHeader.getYear() + "</b>" + " yet";

                if (!performanceCurrentHeader.getErrorMessage().equals("")) {
                    ((HeaderViewHolder) holder).noDataText.setText(Html.fromHtml(performanceCurrentHeader.getErrorMessage()));
                } else {
                    ((HeaderViewHolder) holder).noDataText.setText(Html.fromHtml(message));
                }

                ((HeaderViewHolder) holder).chiefLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                ((HeaderViewHolder) holder).noDataLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

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

            ((HeaderViewHolder) holder).printThisResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else if (holder instanceof MyViewHolder) {
            final PerformanceCurrentModel performanceCurrentModel = performanceCurrentModelList.get(position);

            ((MyViewHolder) holder).subject.setText(performanceCurrentModel.getSubject());
            if (parentActivity != null) {
                if (parentActivity.equals("Parent")) {
                    if (performanceCurrentModel.isNew()) {
                        ((MyViewHolder) holder).newBadge.setVisibility(View.VISIBLE);
                    } else {
                        ((MyViewHolder) holder).newBadge.setVisibility(View.GONE);
                    }
                }
            } else {
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    if (performanceCurrentModel.isNew()) {
                        ((MyViewHolder) holder).newBadge.setVisibility(View.VISIBLE);
                    } else {
                        ((MyViewHolder) holder).newBadge.setVisibility(View.GONE);
                    }
                }
            }

            Boolean isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
            Gson gson = new Gson();
            String subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationTeachers();
            Type type = new TypeToken<HashMap<String, SubscriptionModel>>() {}.getType();
            HashMap<String, SubscriptionModel> subscriptionModelMap = gson.fromJson(subscriptionModelJSON, type);
            SubscriptionModel subscriptionModel = new SubscriptionModel();
            if (subscriptionModelMap != null) {
                subscriptionModel = subscriptionModelMap.get(performanceCurrentHeader.getStudent());
                if (subscriptionModel == null) {
                    subscriptionModel = new SubscriptionModel();
                }
            }
            if (subscriptionModel.getStudentAccount().equals("")) {
                gson = new Gson();
                subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationParents();
                type = new TypeToken<HashMap<String, ArrayList<SubscriptionModel>>>() {}.getType();
                HashMap<String, ArrayList<SubscriptionModel>> subscriptionModelMapParent = gson.fromJson(subscriptionModelJSON, type);
                subscriptionModel = new SubscriptionModel();
                if (subscriptionModelMapParent != null) {
                    ArrayList<SubscriptionModel> subscriptionModelList = subscriptionModelMapParent.get(performanceCurrentHeader.getStudent());
                    String latestSubscriptionDate = "0000/00/00 00:00:00:000";
                    for (SubscriptionModel subscriptionModel1: subscriptionModelList) {
                        if (Date.compareDates(subscriptionModel1.getExpiryDate(), latestSubscriptionDate)) {
                            subscriptionModel = subscriptionModel1;
                            latestSubscriptionDate = subscriptionModel1.getExpiryDate();
                        }
                    }
                }
            }
            Boolean isExpired = Date.compareDates(performanceCurrentModel.getDate(), subscriptionModel.getExpiryDate());

            String caScoreString = "";
            String examScoreString = "";
            String subjectScoreString = "";

            if (isOpenToAll) {
                caScoreString = String.valueOf(performanceCurrentModel.getCaScore()) + "%";
                examScoreString = String.valueOf(performanceCurrentModel.getExamScore()) + "%";
                subjectScoreString = String.valueOf(performanceCurrentModel.getCurrentScore()) + "%";
            } else {
                if (!isExpired) {
                    caScoreString = String.valueOf(performanceCurrentModel.getCaScore()) + "%";
                    examScoreString = String.valueOf(performanceCurrentModel.getExamScore()) + "%";
                    subjectScoreString = String.valueOf(performanceCurrentModel.getCurrentScore()) + "%";
                } else {
                    caScoreString = examScoreString = subjectScoreString = context.getString(R.string.not_subscribed_short);
                }
            }

            ((MyViewHolder) holder).caScore.setText(caScoreString);
            ((MyViewHolder) holder).examScore.setText(examScoreString);
            ((MyViewHolder) holder).subjectScore.setText(subjectScoreString);

            ((MyViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent I = new Intent(context, PerformanceCurrentDetailActivity.class);
                    bundle.putString("Active Student", performanceCurrentHeader.getStudent());
                    bundle.putString("Subject", performanceCurrentModel.getSubject());
                    bundle.putString("Term", performanceCurrentHeader.getTerm());
                    bundle.putString("Year", performanceCurrentHeader.getYear());
                    bundle.putString("parentActivity", parentActivity);
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
