package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
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
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Day;
import com.celerii.celerii.helperClasses.Month;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.ParentAttendanceHeader;
import com.celerii.celerii.models.ParentAttendanceRow;
import com.celerii.celerii.models.SubscriptionModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by user on 7/15/2017.
 */

public class ParentAttendanceRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SharedPreferencesManager sharedPreferencesManager;
    private List<ParentAttendanceRow> parentAttendanceRowList;
    private ParentAttendanceHeader parentAttendanceHeader;
    private String studentName;
    private Activity myActivity;
    private Context context;

    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView className, date, newBadge, status;
//        public ImageView datePic, attendanceMarker;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            date = (TextView) view.findViewById(R.id.attendancedate);
            newBadge = (TextView) view.findViewById(R.id.newbadge);
            status = (TextView) view.findViewById(R.id.status);
//            datePic = (ImageView) view.findViewById(R.id.datepicture);
//            attendanceMarker = (ImageView) view.findViewById(R.id.attendanceMarker);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView term, year, subject, noDataText;
        LinearLayout termLayout, yearLayout, subjectLayout, noDataLayout, chiefLayout;
        public HeaderViewHolder(View view) {
            super(view);
            term = (TextView) view.findViewById(R.id.term);
            year = (TextView) view.findViewById(R.id.year);
            subject = (TextView) view.findViewById(R.id.subject);
            noDataText = (TextView) view.findViewById(R.id.nodatatext);
            termLayout = (LinearLayout) view.findViewById(R.id.termlayout);
            yearLayout = (LinearLayout) view.findViewById(R.id.yearlayout);
            subjectLayout = (LinearLayout) view.findViewById(R.id.subjectlayout);
            noDataLayout = (LinearLayout) view.findViewById(R.id.nodatalayout);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
        }
    }

    public ParentAttendanceRowAdapter(List<ParentAttendanceRow> parentAttendanceRowList, ParentAttendanceHeader parentAttendanceHeader, String studentName, Activity myActivity, Context context) {
        this.sharedPreferencesManager = new SharedPreferencesManager(context);
        this.parentAttendanceRowList = parentAttendanceRowList;
        this.parentAttendanceHeader = parentAttendanceHeader;
        this.studentName = studentName;
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
                String message = "There is no " + "<b>" + parentAttendanceHeader.getSubject() + "</b>" + " attendance recorded for the "  + "<b>" + Term.Term(parentAttendanceHeader.getTerm()) + "</b>" + " of " +  "<b>" +  parentAttendanceHeader.getYear() + "</b>";
                ((HeaderViewHolder) holder).noDataText.setText(Html.fromHtml(message));
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

        } else if (holder instanceof MyViewHolder) {
            final ParentAttendanceRow parentAttendanceRow = parentAttendanceRowList.get(position);
            final Bundle b = new Bundle();

            ((MyViewHolder) holder).className.setText(parentAttendanceRow.getClassName());

            Boolean isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
            Gson gson = new Gson();
            String subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationTeachers();
            Type type = new TypeToken<HashMap<String, SubscriptionModel>>() {}.getType();
            HashMap<String, SubscriptionModel> subscriptionModelMap = gson.fromJson(subscriptionModelJSON, type);
            SubscriptionModel subscriptionModel = new SubscriptionModel();
            if (subscriptionModelMap != null) {
                subscriptionModel = subscriptionModelMap.get(parentAttendanceHeader.getStudentID());
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
                    ArrayList<SubscriptionModel> subscriptionModelList = subscriptionModelMapParent.get(parentAttendanceHeader.getStudentID());
                    String latestSubscriptionDate = "0000/00/00 00:00:00:000";
                    for (SubscriptionModel subscriptionModel1: subscriptionModelList) {
                        if (Date.compareDates(subscriptionModel1.getExpiryDate(), latestSubscriptionDate)) {
                            subscriptionModel = subscriptionModel1;
                            latestSubscriptionDate = subscriptionModel1.getExpiryDate();
                        }
                    }
                }
            }
            Boolean isExpired = Date.compareDates(parentAttendanceRow.getDate(), subscriptionModel.getExpiryDate());

            if (isOpenToAll) {
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
                b.putBoolean("isSubscribed", true);
            } else {
                if (!isExpired) {
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
                    b.putBoolean("isSubscribed", true);
                } else {
                    ((MyViewHolder)holder).status.setText(R.string.not_subscribed_long);
                    b.putBoolean("isSubscribed", false);
                }
            }

//            if (activeAccount.equals("Parent")) {
                if (parentAttendanceRow.getNew()) {
                    ((MyViewHolder) holder).newBadge.setVisibility(View.VISIBLE);
                } else {
                    ((MyViewHolder) holder).newBadge.setVisibility(View.GONE);
                }
//            }

            String[] datearray = parentAttendanceRow.getDate().split(" ")[0].split("/");
            Calendar c = Calendar.getInstance();
            String month = Month.Month(Integer.parseInt(datearray[1]) - 1);
            String day = datearray[2];
            c.set(Integer.parseInt(datearray[0]), Integer.parseInt(datearray[1]) - 1, Integer.parseInt(datearray[2]));
            final String dayOfWeek = Day.Day(c.get(Calendar.DAY_OF_WEEK));

            ((MyViewHolder)holder).date.setText(month + " " + day + " (" + dayOfWeek + ")");

            ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b.putString("key", parentAttendanceRow.getKey());
                    b.putString("ID", parentAttendanceRow.getStudentID());
                    b.putString("name", studentName);
                    b.putString("accountType", "Parent");
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
