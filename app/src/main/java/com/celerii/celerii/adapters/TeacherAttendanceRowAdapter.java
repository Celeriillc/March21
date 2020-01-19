package com.celerii.celerii.adapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditSubjectsActivity;
import com.celerii.celerii.Activities.StudentAttendance.AttendanceDetailActivity;
import com.celerii.celerii.Activities.StudentAttendance.TeacherAttendanceActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.TeacherAttendanceHeader;
import com.celerii.celerii.models.TeacherAttendanceRow;
import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 8/15/2017.
 */

public class TeacherAttendanceRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SharedPreferencesManager sharedPreferencesManager;
    boolean isExpanded;
    private List<TeacherAttendanceRow> teacherAttendanceRowList;
    private Context context;
    private TeacherAttendanceHeader teacherAttendanceHeader;
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
        TextView className, subject, term, date, teacher, noOfstudents, noOfBoys, noOfGirls;
        LinearLayout classLayout, subjectLayout, dayLayout, dateLayout, teacherLayout, noOfStudentsLayout, noOfBoysLayout, noOfGirlsLayout, chiefLayout, deleteRecord;
        RelativeLayout errorLayout;
        TextView errorLayoutText;

        public HeaderViewHolder(View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            subject = (TextView) view.findViewById(R.id.subject);
            term = (TextView) view.findViewById(R.id.term);
            date = (TextView) view.findViewById(R.id.date);
            teacher = (TextView) view.findViewById(R.id.teacher);
            noOfstudents = (TextView) view.findViewById(R.id.noofstudents);
            noOfBoys = (TextView) view.findViewById(R.id.noofboys);
            noOfGirls = (TextView) view.findViewById(R.id.noofgirls);

            classLayout = (LinearLayout) view.findViewById(R.id.classlayout);
            subjectLayout = (LinearLayout) view.findViewById(R.id.subjectlayout);
            dayLayout = (LinearLayout) view.findViewById(R.id.termlayout);
            dateLayout = (LinearLayout) view.findViewById(R.id.datelayout);
            teacherLayout = (LinearLayout) view.findViewById(R.id.teacherlayout);
            noOfStudentsLayout = (LinearLayout) view.findViewById(R.id.noofstudentslayout);
            noOfBoysLayout = (LinearLayout) view.findViewById(R.id.noofboyslayout);
            noOfGirlsLayout = (LinearLayout) view.findViewById(R.id.noofgirlslayout);

            errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
            errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            deleteRecord = (LinearLayout) view.findViewById(R.id.deleterecord);
        }
    }

    public TeacherAttendanceRowAdapter(List<TeacherAttendanceRow> teacherAttendanceRowList, TeacherAttendanceHeader teacherAttendanceHeader,
                                       Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.teacherAttendanceRowList = teacherAttendanceRowList;
        this.teacherAttendanceHeader = teacherAttendanceHeader;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_attendance_row, parent, false);
                return new TeacherAttendanceRowAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_attendance_header, parent, false);
                return new TeacherAttendanceRowAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_attendance_row, parent, false);
                return new TeacherAttendanceRowAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder) {
            if (teacherAttendanceRowList.size() <= 1){
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).deleteRecord.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                String errorMessage = "There is no " + "<b>" + teacherAttendanceHeader.getSubject() + "</b>" + " attendance information recorded for " + "<b>" + teacherAttendanceHeader.getClassName() + "</b>" + " on the " + Date.getFormalDocumentDate(teacherAttendanceHeader.getDate()) + ".";
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
            } else {
                String myID = sharedPreferencesManager.getMyUserID();
                if (myID.equals(teacherAttendanceHeader.getTeacherID())) {
                    ((HeaderViewHolder) holder).deleteRecord.setVisibility(View.VISIBLE);
                } else {
                    ((HeaderViewHolder) holder).deleteRecord.setVisibility(View.GONE);
                }
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            ((HeaderViewHolder)holder).className.setText(teacherAttendanceHeader.getClassName());
            ((HeaderViewHolder)holder).subject.setText(teacherAttendanceHeader.getSubject());
            if (teacherAttendanceHeader.getTerm().equals("")) {
                ((HeaderViewHolder)holder).term.setText("");
            } else {
                ((HeaderViewHolder)holder).term.setText(Term.Term(teacherAttendanceHeader.getTerm()));
            }
            ((HeaderViewHolder)holder).date.setText(Date.DateFormatMMDDYYYY(teacherAttendanceHeader.getDate()));
            ((HeaderViewHolder)holder).teacher.setText(teacherAttendanceHeader.getTeacher());
            ((HeaderViewHolder)holder).noOfstudents.setText(teacherAttendanceHeader.getNoOfStudents());
            ((HeaderViewHolder)holder).noOfBoys.setText(teacherAttendanceHeader.getNoOfBoys());
            ((HeaderViewHolder)holder).noOfGirls.setText(teacherAttendanceHeader.getNoOfGirls());

            ((HeaderViewHolder) holder).subjectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditSubjectsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Active Class", teacherAttendanceHeader.getClassID());
                    bundle.putString("Subject", teacherAttendanceHeader.getSubject());
                    bundle.putString("Activity", "WriteAttendance");
                    intent.putExtras(bundle);
                    ((Activity)context).startActivityForResult(intent, 1);
                }
            });

            ((HeaderViewHolder) holder).dateLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment().setThemeCustom(R.style.MyCustomBetterPickersDialogs);
                    cdp.show(((TeacherAttendanceActivity)context).getSupportFragmentManager(), "Material Calendar Example");

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

            ((HeaderViewHolder) holder).deleteRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_dialog_layout_delete_academic_record_teacher);
                    TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
                    TextView delete = (TextView) dialog.findViewById(R.id.delete);
                    dialog.show();

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteAttendanceRecord();
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        else {
            final TeacherAttendanceRow teacherAttendanceRow = teacherAttendanceRowList.get(position);

            ((MyViewHolder)holder).studentName.setText(teacherAttendanceRow.getName());

            if (!teacherAttendanceRow.getImageURL().isEmpty()) {
                Glide.with(context)
                        .load(teacherAttendanceRow.getImageURL())
                        .crossFade()
                        .placeholder(R.drawable.profileimageplaceholder)
                        .error(R.drawable.profileimageplaceholder)
                        .centerCrop().bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder)holder).studentPic);
            } else {
                Glide.with(context)
                        .load(R.drawable.profileimageplaceholder)
                        .crossFade()
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder)holder).studentPic);
            }

            if (teacherAttendanceRow.getAttendanceStatus().equals("Present")) {
                ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_present_24dp);
            } else if (teacherAttendanceRow.getAttendanceStatus().equals("Absent")) {
                ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_absent_24dp);
            } else if (teacherAttendanceRow.getAttendanceStatus().equals("Late")) {
                ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_late_24dp);
            }


            ((MyViewHolder)holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("date", teacherAttendanceRow.getDate());
                    b.putString("status", teacherAttendanceRow.getAttendanceStatus());
                    b.putString("remark", teacherAttendanceRow.getRemark());
                    b.putString("term", teacherAttendanceRow.getTerm());
                    b.putString("day", teacherAttendanceRow.getDay());
                    b.putString("key", teacherAttendanceRow.getKey());
                    b.putString("ID", teacherAttendanceRow.getStudentID());
                    Intent I = new Intent(context, AttendanceDetailActivity.class);
                    I.putExtras(b);
                    context.startActivity(I);
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
        return position == teacherAttendanceRowList.size () + 1;
    }

    private void expand(LinearLayout mLinearLayout) {
        //set Visible
        mLinearLayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mLinearLayout.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight(), mLinearLayout);
        mAnimator.start();
    }

    private void collapse(final LinearLayout mLinearLayout) {
        int finalHeight = mLinearLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, mLinearLayout);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                mLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationStart(Animator animation) {

            }
        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final LinearLayout mLinearLayout) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                layoutParams.height = value;
                mLinearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void deleteAttendanceRecord() {
        String attendanceKey = teacherAttendanceHeader.getKey();
        String activeClass = teacherAttendanceHeader.getClassID();

        Map<String, Object> deleteAttendance = new HashMap<String, Object>();
        deleteAttendance.put("AttendenceClass/" + activeClass + "/" + attendanceKey, null);
        for (int i = 0; i < teacherAttendanceRowList.size(); i++) {
            if (teacherAttendanceRowList.get(i).getStudentID() != null) {
                deleteAttendance.put("AttendenceClass-Students/" + activeClass + "/" + attendanceKey + "/Students/" + teacherAttendanceRowList.get(i).getStudentID(), null);
                deleteAttendance.put("AttendenceStudent/" + teacherAttendanceRowList.get(i).getStudentID() + "/" + attendanceKey, null);
            }
        }

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.updateChildren(deleteAttendance, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                showDialogWithMessage("Attendance record has been deleted");
            }
        });
    }

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        TextView OK = (TextView) dialog.findViewById(R.id.optionone);
        dialog.show();

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ((Activity)context).finish();
            }
        });
    }
}
