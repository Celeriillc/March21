package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditSubjectsActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.Activities.StudentAttendance.TeacherTakeAttendanceActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.TeacherAttendanceHeader;
import com.celerii.celerii.models.TeacherAttendanceRow;
import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 8/18/2017.
 */

public class TeacherTakeAttendanceRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    boolean isExpanded;
    private List<TeacherAttendanceRow> teacherAttendanceRowList;
    private Context context;
    private TeacherAttendanceHeader teacherAttendanceHeader;
    TeacherTakeAttendanceRowAdapter teacherTakeAttendanceRowAdapter;
    private Activity myActivity;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView studentName;
        public ImageView studentPic, attendanceMarker;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            studentName = (TextView) view.findViewById(R.id.kidname);
            studentPic = (ImageView) view.findViewById(R.id.kidPicture);
            attendanceMarker = (ImageView) view.findViewById(R.id.attendanceMarker);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView teacher, className, subject, term, date, markAllText;
        LinearLayout teacherLayout, classLayout, dateLayout, termLayout, subjectLayout, markAllLayout;

        public HeaderViewHolder(View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            teacher = (TextView) view.findViewById(R.id.teacher);
            subject = (TextView) view.findViewById(R.id.subject);
            term = (TextView) view.findViewById(R.id.term);
            date = (TextView) view.findViewById(R.id.date);
            markAllText = (TextView) view.findViewById(R.id.markalltext);
            classLayout = (LinearLayout) view.findViewById(R.id.classlayout);
            teacherLayout = (LinearLayout) view.findViewById(R.id.teacherlayout);
            dateLayout = (LinearLayout) view.findViewById(R.id.datelayout);
            termLayout = (LinearLayout) view.findViewById(R.id.termlayout);
            subjectLayout = (LinearLayout) view.findViewById(R.id.subjectlayout);
            markAllLayout = (LinearLayout) view.findViewById(R.id.markalllayout);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        Button saveToCloud;

        public FooterViewHolder(View view) {
            super(view);
            saveToCloud = (Button) view.findViewById(R.id.savetocloud);
        }
    }

    public TeacherTakeAttendanceRowAdapter(List<TeacherAttendanceRow> teacherAttendanceRowList, TeacherAttendanceHeader teacherAttendanceHeader,
                                           Activity myActivity, Context context) {
        this.teacherAttendanceRowList = teacherAttendanceRowList;
        this.teacherAttendanceHeader = teacherAttendanceHeader;
        this.myActivity = myActivity;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_attendance_row, parent, false);
                return new TeacherTakeAttendanceRowAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_take_attendance_header, parent, false);
                return new TeacherTakeAttendanceRowAdapter.HeaderViewHolder(rowView);
            case Footer:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_take_attendance_footer, parent, false);
                return new TeacherTakeAttendanceRowAdapter.FooterViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_attendance_row, parent, false);
                return new TeacherTakeAttendanceRowAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder) {

            String term = Term.Term(teacherAttendanceHeader.getTerm());
            ((HeaderViewHolder)holder).className.setText(teacherAttendanceHeader.getClassName());
            ((HeaderViewHolder) holder).teacher.setText(teacherAttendanceHeader.getTeacher());
            ((HeaderViewHolder)holder).subject.setText(teacherAttendanceHeader.getSubject());
            ((HeaderViewHolder)holder).term.setText((term));
            ((HeaderViewHolder)holder).date.setText(Date.DateFormatMMDDYYYY(teacherAttendanceHeader.getDate()));

            ((HeaderViewHolder) holder).subjectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditSubjectsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Active Class", teacherAttendanceHeader.getClassID());
                    bundle.putString("Subject", teacherAttendanceHeader.getSubject());
                    bundle.putString("Activity", "WriteAttendance");
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 1);
                }
            });

            ((HeaderViewHolder) holder).dateLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment().setThemeCustom(R.style.MyCustomBetterPickersDialogs);;
                    cdp.show(((TeacherTakeAttendanceActivity)context).getSupportFragmentManager(), "Material Calendar Example");

                    cdp.setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                        @Override
                        public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                            Intent informationIntent = new Intent("Date Information");
                            informationIntent.putExtra("Year", String.valueOf(year));
                            informationIntent.putExtra("Month", String.valueOf(monthOfYear + 1));
                            informationIntent.putExtra("Day", String.valueOf(dayOfMonth));
                            LocalBroadcastManager.getInstance(context).sendBroadcast(informationIntent);
                        }
                    });


                }
            });

            ((HeaderViewHolder) holder).termLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditTermActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Term", teacherAttendanceHeader.getTerm());
                    intent.putExtras(bundle);
                    myActivity.startActivityForResult(intent, 3);
                }
            });

            ((HeaderViewHolder) holder).markAllLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((HeaderViewHolder) holder).markAllText.getText().equals("Mark All Present")){
                        ((HeaderViewHolder) holder).markAllText.setText("Mark All Absent");
                        for(int i = 0; i < teacherAttendanceRowList.size(); i++) {
                            teacherAttendanceRowList.get(i).setAttendanceStatus("Present");
                        }
                        notifyDataSetChanged();
                    } else if (((HeaderViewHolder) holder).markAllText.getText().equals("Mark All Absent")){
                        ((HeaderViewHolder) holder).markAllText.setText("Mark All Late to Class");
                        for(int i = 0; i < teacherAttendanceRowList.size(); i++) {
                            teacherAttendanceRowList.get(i).setAttendanceStatus("Absent");
                        }
                        notifyDataSetChanged();
                    } else if (((HeaderViewHolder) holder).markAllText.getText().equals("Mark All Late to Class")){
                        ((HeaderViewHolder) holder).markAllText.setText("Mark All Present");
                        for(int i = 0; i < teacherAttendanceRowList.size(); i++) {
                            teacherAttendanceRowList.get(i).setAttendanceStatus("Late");
                        }
                        notifyDataSetChanged();
                    }
                }
            });
        } else if(holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).saveToCloud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof TeacherTakeAttendanceActivity) {
                        ((TeacherTakeAttendanceActivity)context).saveToCloud();
                    }
                }
            });
        } else {
            final TeacherAttendanceRow teacherAttendanceRow = teacherAttendanceRowList.get(position);

            ((MyViewHolder)holder).studentName.setText(teacherAttendanceRow.getName());

            Glide.with(context)
                    .load(teacherAttendanceRow.getImageURL())
                    .crossFade()
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop().bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder)holder).studentPic);

            if (teacherAttendanceRow.getAttendanceStatus().equals("Present")) {
                ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_present_24dp);
                animation(((MyViewHolder)holder).attendanceMarker);
            } else if (teacherAttendanceRow.getAttendanceStatus().equals("Absent")) {
                ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_absent_24dp);
                animation(((MyViewHolder)holder).attendanceMarker);
            } else if (teacherAttendanceRow.getAttendanceStatus().equals("Late")) {
                ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_late_24dp);
                animation(((MyViewHolder)holder).attendanceMarker);
            }

            ((MyViewHolder)holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (teacherAttendanceRow.getAttendanceStatus().equals("Present")){
                        teacherAttendanceRow.setAttendanceStatus("Absent");
                        ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_absent_24dp);
                        animation(((MyViewHolder)holder).attendanceMarker);
                    }
                    else if (teacherAttendanceRow.getAttendanceStatus().equals("Absent")){
                        teacherAttendanceRow.setAttendanceStatus("Late");
                        ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_late_24dp);
                        animation(((MyViewHolder)holder).attendanceMarker);
                    }
                    else if (teacherAttendanceRow.getAttendanceStatus().equals("Late")){
                        teacherAttendanceRow.setAttendanceStatus("Present");
                        ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_present_24dp);
                        animation(((MyViewHolder)holder).attendanceMarker);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return teacherAttendanceRowList.size();
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
        return position == teacherAttendanceRowList.size () - 1;
    }

    public void animation(final View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.15f, 0.0f, 1.15f,
                Animation.RELATIVE_TO_SELF, 0.75f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimation(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimation(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(200);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    private Animation prepareAnimation(Animation animation){
        animation.setRepeatCount(0);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}
