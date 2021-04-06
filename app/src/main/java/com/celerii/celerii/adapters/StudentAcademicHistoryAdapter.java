package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EditYearActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.StudentAcademicHistoryHeaderModel;
import com.celerii.celerii.models.StudentAcademicHistoryRowModel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 8/21/2017.
 */

public class StudentAcademicHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<StudentAcademicHistoryRowModel> studentAcademicHistoryRowModelList;
    private Context context;
    private StudentAcademicHistoryHeaderModel studentAcademicHistoryHeaderModel;
    private Activity myActivity;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

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

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        Button term, year;
        RelativeLayout errorLayout;
        TextView errorLayoutText;
        Button errorLayoutButton;

        public HeaderViewHolder(View view) {
            super(view);
            term = (Button) view.findViewById(R.id.term);
            year = (Button) view.findViewById(R.id.year);
            errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
            errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
            errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        }
    }

    public StudentAcademicHistoryAdapter(List<StudentAcademicHistoryRowModel> studentAcademicHistoryRowModelList, StudentAcademicHistoryHeaderModel studentAcademicHistoryHeaderModel, Activity myActivity, Context context) {
        this.studentAcademicHistoryRowModelList = studentAcademicHistoryRowModelList;
        this.studentAcademicHistoryHeaderModel = studentAcademicHistoryHeaderModel;
        this.myActivity = myActivity;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_academic_history_row, parent, false);
                return new StudentAcademicHistoryAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_academic_history_header, parent, false);
                return new StudentAcademicHistoryAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_academic_history_row, parent, false);
                return new StudentAcademicHistoryAdapter.MyViewHolder(rowView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).term.setText(Term.Term(studentAcademicHistoryHeaderModel.getTerm()));
            ((HeaderViewHolder) holder).year.setText(studentAcademicHistoryHeaderModel.getYear());

            if (studentAcademicHistoryRowModelList.size() <= 1) {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                String message = studentAcademicHistoryHeaderModel.getClassName() + " doesn't contain any students. You can change the active class to another with students in the " + "<b>" + "More" + "</b>" + " area";
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(message));
            } else {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.GONE);
            }

            ((HeaderViewHolder) holder).term.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditTermActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Term", studentAcademicHistoryHeaderModel.getTerm());
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 0);
                }
            });

            ((HeaderViewHolder) holder).year.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditYearActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Year", studentAcademicHistoryHeaderModel.getYear());
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 1);
                }
            });
        } else if (holder instanceof MyViewHolder) {
            final StudentAcademicHistoryRowModel studentAcademicHistoryRowModel = studentAcademicHistoryRowModelList.get(position);

            ((MyViewHolder) holder).kidName.setText(studentAcademicHistoryRowModel.getName());
            String average = "0%";
            if (studentAcademicHistoryRowModel.getAverage() != null) {
                if (!studentAcademicHistoryRowModel.getAverage().equals("")) {
                    average = studentAcademicHistoryRowModel.getAverage() + "%";
                }
            }
            ((MyViewHolder) holder).average.setText(average);
            ((MyViewHolder) holder).clipper.setClipToOutline(true);

            Drawable textDrawable;
            if (!studentAcademicHistoryRowModel.getName().isEmpty()) {
                String[] nameArray = studentAcademicHistoryRowModel.getName().replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 40);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 40);
                }
                ((MyViewHolder) holder).kidPic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 40);
            }

            if (!studentAcademicHistoryRowModel.getImageURL().isEmpty()) {
                Glide.with(context)
                        .load(studentAcademicHistoryRowModel.getImageURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder) holder).kidPic);
            }

            final String kidNo = studentAcademicHistoryRowModel.getStudentID();
            final String kidName = studentAcademicHistoryRowModel.getName();

            ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return studentAcademicHistoryRowModelList.size();
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
        return position == studentAcademicHistoryRowModelList.size () + 1;
    }
}

