package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.Activities.EClassroom.Teacher.TeacherEClassroomMessageBoardActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.models.EClassroomScheduledClassesListModel;

import java.util.List;

public class TeacherEClassroomConcludedClassesListAdapter extends RecyclerView.Adapter<TeacherEClassroomConcludedClassesListAdapter.MyViewHolder>{

    private List<EClassroomScheduledClassesListModel> eClassroomScheduledClassesListModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView classSubject, dateTime;
        public ImageView pic;
        public LinearLayout picClipper;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            classSubject = (TextView) view.findViewById(R.id.classsubject);
            dateTime = (TextView) view.findViewById(R.id.datetime);
            pic = (ImageView) view.findViewById(R.id.pic);
            picClipper = (LinearLayout) view.findViewById(R.id.picclipper);
            clickableView = view;
        }
    }

    public TeacherEClassroomConcludedClassesListAdapter(List<EClassroomScheduledClassesListModel> eClassroomScheduledClassesListModelList, Context context) {
        this.eClassroomScheduledClassesListModelList = eClassroomScheduledClassesListModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_classroom_scheduled_classes_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EClassroomScheduledClassesListModel eClassroomScheduledClassesListModel = eClassroomScheduledClassesListModelList.get(position);

        String classSubject = eClassroomScheduledClassesListModel.getClassName() + " - " + eClassroomScheduledClassesListModel.getSubject();
        String date = Date.getDate();
        String dateTime;
        if (Date.compareDates(date, eClassroomScheduledClassesListModel.getDateScheduled())) {
            dateTime = Date.getRelativeTimeSpan(eClassroomScheduledClassesListModel.getDateScheduled()) + " - " + Date.DateFormatMMDDYYYY(eClassroomScheduledClassesListModel.getDateScheduled()) + " by " + Date.DateFormatHHMM(eClassroomScheduledClassesListModel.getDateScheduled());
        } else {
            dateTime = Date.getRelativeTimeSpanForward(eClassroomScheduledClassesListModel.getDateScheduled()) + " - " + Date.DateFormatMMDDYYYY(eClassroomScheduledClassesListModel.getDateScheduled()) + " by " + Date.DateFormatHHMM(eClassroomScheduledClassesListModel.getDateScheduled());
        }
        holder.classSubject.setText(classSubject);
        holder.dateTime.setText(dateTime);
        holder.picClipper.setClipToOutline(true);
        Drawable textDrawable;
        if (!eClassroomScheduledClassesListModel.getSubject().isEmpty()) {
            String[] nameArray = eClassroomScheduledClassesListModel.getSubject().replaceAll("\\s+", " ").trim().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 60);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 60);
            }
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 60);
        }
        holder.pic.setImageDrawable(textDrawable);

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Scheduled Class ID", eClassroomScheduledClassesListModel.getScheduledClassID());
                bundle.putString("Scheduled Class Link", eClassroomScheduledClassesListModel.getClassLink());
                bundle.putString("Scheduled Class State", "Concluded");
                bundle.putString("Scheduled Class Scheduled Date", eClassroomScheduledClassesListModel.getDateScheduled());
                Intent I = new Intent(context, TeacherEClassroomMessageBoardActivity.class);
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eClassroomScheduledClassesListModelList.size();
    }
}
