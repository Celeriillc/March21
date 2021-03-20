package com.celerii.celerii.adapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditSubjectsActivity;
import com.celerii.celerii.Activities.StudentAttendance.AttendanceDetailActivity;
import com.celerii.celerii.Activities.StudentAttendance.TeacherAttendanceActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.SubscriptionModel;
import com.celerii.celerii.models.TeacherAttendanceHeader;
import com.celerii.celerii.models.TeacherAttendanceRow;
import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 8/15/2017.
 */

public class TeacherAttendanceRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SharedPreferencesManager sharedPreferencesManager;
    DatabaseReference mDatabaseReference;
    private List<TeacherAttendanceRow> teacherAttendanceRowList;
    private Context context;
    private TeacherAttendanceHeader teacherAttendanceHeader;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView studentName, subscriptionFlag;
        public ImageView studentPic, attendanceMarker;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            studentName = (TextView) view.findViewById(R.id.kidname);
            subscriptionFlag = (TextView) view.findViewById(R.id.subscriptionflag);
            studentPic = (ImageView) view.findViewById(R.id.kidPicture);
            attendanceMarker = (ImageView) view.findViewById(R.id.attendanceMarker);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView className, subject, term, date, teacher, noOfstudents, noOfBoys, noOfGirls;
        LinearLayout classLayout, subjectLayout, dayLayout, dateLayout, teacherLayout, noOfStudentsLayout, noOfBoysLayout, noOfGirlsLayout, chiefLayout, deleteRecordLayout;
        RelativeLayout errorLayout;
        Button deleteRecord;
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
            deleteRecordLayout = (LinearLayout) view.findViewById(R.id.deleterecordlayout);

            deleteRecord = (Button) view.findViewById(R.id.deleterecord);
        }
    }

    public TeacherAttendanceRowAdapter(List<TeacherAttendanceRow> teacherAttendanceRowList, TeacherAttendanceHeader teacherAttendanceHeader,
                                       Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.mDatabaseReference = FirebaseDatabase.getInstance().getReference();
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
                ((HeaderViewHolder) holder).deleteRecordLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                String errorMessage = "There is no " + "<b>" + teacherAttendanceHeader.getSubject() + "</b>" + " attendance information recorded for " + "<b>" + teacherAttendanceHeader.getClassName() + "</b>" + " on the " + "<b>" + Date.getFormalDocumentDate(teacherAttendanceHeader.getDate()) + "</b>" + ".";
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
            } else {
                String myID = sharedPreferencesManager.getMyUserID();
                if (myID.equals(teacherAttendanceHeader.getTeacherID())) {
                    ((HeaderViewHolder) holder).deleteRecordLayout.setVisibility(View.VISIBLE);
                } else {
                    ((HeaderViewHolder) holder).deleteRecordLayout.setVisibility(View.GONE);
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
                    Button delete = (Button) dialog.findViewById(R.id.delete);
                    Button cancel = (Button) dialog.findViewById(R.id.cancel);
                    try {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    } catch (Exception e) {
                        return;
                    }

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteAttendanceRecord();
                            dialog.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        else {
            final TeacherAttendanceRow teacherAttendanceRow = teacherAttendanceRowList.get(position);
            final Bundle b = new Bundle();

            ((MyViewHolder)holder).studentName.setText(teacherAttendanceRow.getName());

            Drawable textDrawable;
            if (!teacherAttendanceRow.getName().isEmpty()) {
                String[] nameArray = teacherAttendanceRow.getName().replaceAll("\\s+", " ").split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
                }
                ((MyViewHolder) holder).studentPic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
            }

            if (!teacherAttendanceRow.getImageURL().isEmpty()) {
                Glide.with(context)
                        .load(teacherAttendanceRow.getImageURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder) holder).studentPic);
            }

            Boolean isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
            Gson gson = new Gson();
            String subscriptionModelJSON = sharedPreferencesManager.getSubscriptionInformationTeachers();
            Type type = new TypeToken<HashMap<String, SubscriptionModel>>() {}.getType();
            HashMap<String, SubscriptionModel> subscriptionModelMap = gson.fromJson(subscriptionModelJSON, type);
            SubscriptionModel subscriptionModel = new SubscriptionModel();
            if (subscriptionModelMap != null) {
                subscriptionModel = subscriptionModelMap.get(teacherAttendanceRow.getStudentID());
                if (subscriptionModel == null) {
                    subscriptionModel = new SubscriptionModel();
                }
            }
            Boolean isExpired = Date.compareDates(teacherAttendanceRow.getDate(), subscriptionModel.getExpiryDate());

            if (isOpenToAll) {
                if (teacherAttendanceRow.getAttendanceStatus().equals("Present")) {
                    ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_present_24dp);
                } else if (teacherAttendanceRow.getAttendanceStatus().equals("Absent")) {
                    ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_absent_24dp);
                } else if (teacherAttendanceRow.getAttendanceStatus().equals("Late")) {
                    ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_late_24dp);
                }
                b.putBoolean("isSubscribed", true);
            } else {
                if (!isExpired) {
                    if (teacherAttendanceRow.getAttendanceStatus().equals("Present")) {
                        ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_present_24dp);
                    } else if (teacherAttendanceRow.getAttendanceStatus().equals("Absent")) {
                        ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_absent_24dp);
                    } else if (teacherAttendanceRow.getAttendanceStatus().equals("Late")) {
                        ((MyViewHolder)holder).attendanceMarker.setImageResource(R.drawable.ic_attendance_late_24dp);
                    }
                    b.putBoolean("isSubscribed", true);
                } else {
                    ((MyViewHolder)holder).attendanceMarker.setVisibility(View.GONE);
                    ((MyViewHolder) holder).subscriptionFlag.setText(R.string.not_subscribed_long);
                    ((MyViewHolder) holder).subscriptionFlag.setVisibility(View.VISIBLE);
                    b.putBoolean("isSubscribed", false);
                }
            }

            ((MyViewHolder)holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    b.putString("date", teacherAttendanceRow.getDate());
                    b.putString("status", teacherAttendanceRow.getAttendanceStatus());
                    b.putString("remark", teacherAttendanceRow.getRemark());
                    b.putString("term", teacherAttendanceRow.getTerm());
                    b.putString("day", teacherAttendanceRow.getDay());
                    b.putString("key", teacherAttendanceRow.getKey());
                    b.putString("ID", teacherAttendanceRow.getStudentID());
                    b.putString("name", teacherAttendanceRow.getName());
                    b.putString("accountType", "Teacher");
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
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            showDialogWithMessage("Internet is down, check your connection and try again");
            return;
        }

        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(context);
        final String attendanceKey = teacherAttendanceHeader.getKey();
        final String activeClass = teacherAttendanceHeader.getClassID();
        final String activeClassName = teacherAttendanceHeader.getClassName();
        final ArrayList<String> parentsList = new ArrayList<>();
        final Map<String, Object> deleteAttendance = new HashMap<String, Object>();

        progressDialog.show();

        Gson gson = new Gson();
        ArrayList<Class> myClasses = new ArrayList<>();
        String myClassesJSON = sharedPreferencesManager.getMyClasses();
        Type type = new TypeToken<ArrayList<Class>>() {}.getType();
        myClasses = gson.fromJson(myClassesJSON, type);

        if (myClasses != null) {
            boolean hasClass = false;
            for (Class classInstance: myClasses) {
                if (classInstance.getID().equals(activeClass)) {
                    hasClass = true;
                    break;
                }
            }

            if (hasClass) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("AttendanceParentRecipients").child(attendanceKey);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                parentsList.add(postSnapshot.getKey());
                            }
                        }

                        deleteAttendance.put("AttendanceClass/" + activeClass + "/" + attendanceKey, null);
                        for (int i = 0; i < teacherAttendanceRowList.size(); i++) {
                            if (!teacherAttendanceRowList.get(i).getStudentID().equals("")) {
                                deleteAttendance.put("AttendanceClass-Students/" + activeClass + "/" + attendanceKey + "/Students/" + teacherAttendanceRowList.get(i).getStudentID(), null);
                                deleteAttendance.put("AttendanceStudent/" + teacherAttendanceRowList.get(i).getStudentID() + "/" + attendanceKey, null);
                            }
                        }

                        for (String parentID: parentsList) {
                            deleteAttendance.put("AttendanceParentRecipients/" + attendanceKey + "/" + parentID, null);
                            deleteAttendance.put("NotificationParent/" + parentID + "/" + attendanceKey, null);
                        }

                        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        mDatabaseReference.updateChildren(deleteAttendance, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    progressDialog.dismiss();
                                    showDialogWithMessageAndClose("Attendance record has been deleted");
                                } else {
                                    progressDialog.dismiss();
                                    String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                    ShowDialogWithMessage.showDialogWithMessage(context, message);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                progressDialog.dismiss();
                String message = "You can't delete this record at this time because you're currently not connected to " + "<b>" + activeClassName + "</b>" + ".";
                showDialogWithMessage(message);
            }
        } else {
            progressDialog.dismiss();
            String message = "You can't delete this record at this time because you're currently not connected to " + "<b>" + activeClassName + "</b>" + ".";
            showDialogWithMessage(message);
        }
    }

    private void showDialogWithMessageAndClose (String messageString) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

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

    private void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
