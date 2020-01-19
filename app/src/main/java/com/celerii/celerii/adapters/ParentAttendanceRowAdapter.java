package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.StudentAttendance.AttendanceDetailActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EditYearActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditSubjectsActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Day;
import com.celerii.celerii.helperClasses.Month;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.ParentAttendanceHeader;
import com.celerii.celerii.models.ParentAttendanceRow;

import java.util.Calendar;
import java.util.List;


/**
 * Created by user on 7/15/2017.
 */

public class ParentAttendanceRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ParentAttendanceRow> parentAttendanceRowList;
    private ParentAttendanceHeader parentAttendanceHeader;
    private Activity myActivity;
    private Context context;

    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView className, date, status;
//        public ImageView datePic, attendanceMarker;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            date = (TextView) view.findViewById(R.id.attendancedate);
            status = (TextView) view.findViewById(R.id.status);
//            datePic = (ImageView) view.findViewById(R.id.datepicture);
//            attendanceMarker = (ImageView) view.findViewById(R.id.attendanceMarker);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView term, year, subject;
        LinearLayout termLayout, yearLayout, subjectLayout, noDataLayout, chiefLayout;
        public HeaderViewHolder(View view) {
            super(view);
            term = (TextView) view.findViewById(R.id.term);
            year = (TextView) view.findViewById(R.id.year);
            subject = (TextView) view.findViewById(R.id.subject);
            termLayout = (LinearLayout) view.findViewById(R.id.termlayout);
            yearLayout = (LinearLayout) view.findViewById(R.id.yearlayout);
            subjectLayout = (LinearLayout) view.findViewById(R.id.subjectlayout);
            noDataLayout = (LinearLayout) view.findViewById(R.id.nodatalayout);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
        }
    }

    public ParentAttendanceRowAdapter(List<ParentAttendanceRow> parentAttendanceRowList, ParentAttendanceHeader parentAttendanceHeader, Activity myActivity, Context context) {
        this.parentAttendanceRowList = parentAttendanceRowList;
        this.parentAttendanceHeader = parentAttendanceHeader;
        this.myActivity = myActivity;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_attendance_row, parent, false);
                return new ParentAttendanceRowAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_attendance_header, parent, false);
                return new ParentAttendanceRowAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_attendance_row, parent, false);
                return new ParentAttendanceRowAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).subject.setText(parentAttendanceHeader.getSubject());
            ((HeaderViewHolder) holder).term.setText(Term.Term(parentAttendanceHeader.getTerm()));
            ((HeaderViewHolder) holder).year.setText(parentAttendanceHeader.getYear());

            if (parentAttendanceRowList.size() <= 1){
                ((HeaderViewHolder) holder).noDataLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).chiefLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                ((HeaderViewHolder) holder).noDataLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            ((HeaderViewHolder) holder).subjectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditSubjectsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Active Class", parentAttendanceHeader.getClassID());
                    bundle.putString("Subject", parentAttendanceHeader.getSubject());
                    bundle.putString("Activity", "ParentReadAttendance");
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 1);
                }
            });

            ((HeaderViewHolder) holder).termLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditTermActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Term", parentAttendanceHeader.getTerm());
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 2);
                }
            });

            ((HeaderViewHolder) holder).yearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditYearActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Year", parentAttendanceHeader.getYear());
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 3);
                }
            });

        } else if (holder instanceof MyViewHolder){
            final ParentAttendanceRow parentAttendanceRow = parentAttendanceRowList.get(position);

            ((MyViewHolder) holder).className.setText(parentAttendanceRow.getClassName());
            if (parentAttendanceRow.getAttendanceStatus().equals("Present")) {
                ((MyViewHolder)holder).status.setText("Present");
                ((MyViewHolder)holder).status.setTextColor(context.getResources().getColor(R.color.colorPrimaryPurple));
            } else if (parentAttendanceRow.getAttendanceStatus().equals("Absent")) {
                ((MyViewHolder)holder).status.setText("Absent");
                ((MyViewHolder)holder).status.setTextColor(context.getResources().getColor(R.color.colorAccent));
            } else if (parentAttendanceRow.getAttendanceStatus().equals("Late")) {
                ((MyViewHolder)holder).status.setText("Came in Late");
                ((MyViewHolder)holder).status.setTextColor(context.getResources().getColor(R.color.colorDeepGray));
            }

            String[] datearray = parentAttendanceRow.getDate().split(" ")[0].split("/");
            Calendar c = Calendar.getInstance();
            String month = Month.Month(Integer.parseInt(datearray[1]) - 1);
            String day = datearray[2];
            c.set(Integer.parseInt(datearray[0]), Integer.parseInt(datearray[1]) - 1, Integer.parseInt(datearray[2]));
            final String dayOfWeek = Day.Day(c.get(Calendar.DAY_OF_WEEK));

            ((MyViewHolder)holder).date.setText(month + " " + day + " (" + dayOfWeek + ")");

//            String letter = String.valueOf(parentAttendanceRow.getAttendanceStatus().charAt(0));

//            int color;

//            if (parentAttendanceRow.getAttendanceStatus().equals("Present")){
//                color = Color.rgb(56,142,60);
//            }
//            else if (parentAttendanceRow.getAttendanceStatus().equals("Absent")){
//                color = Color.rgb(211,47,47);
//            }
//            else{
//                color = Color.rgb(251,192,45);
//            }
//            TextDrawable textDrawable = TextDrawable.builder()
//                    .buildRound(day, color);
//            ((MyViewHolder)holder).datePic.setImageDrawable(textDrawable);

            ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("key", parentAttendanceRow.getKey());
                    b.putString("ID", parentAttendanceRow.getStudentID());
                    Intent I = new Intent(context, AttendanceDetailActivity.class);
                    I.putExtras(b);
                    context.startActivity(I);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return parentAttendanceRowList.size();
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
        return position == parentAttendanceRowList.size () + 1;
    }
}
