package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.Activities.Settings.FAQActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.models.EClassroomScheduledClassesListModel;
import com.celerii.celerii.models.FAQModel;

import java.util.List;

public class EClassroomScheduledClassesListAdapter extends RecyclerView.Adapter<EClassroomScheduledClassesListAdapter.MyViewHolder>{

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

    public EClassroomScheduledClassesListAdapter(List<EClassroomScheduledClassesListModel> eClassroomScheduledClassesListModelList, Context context) {
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
        String dateTime = Date.getRelativeTimeSpanForward(eClassroomScheduledClassesListModel.getDateScheduled()) + " - " + Date.DateFormatMMDDYYYYHHMM(eClassroomScheduledClassesListModel.getDateScheduled());
        holder.classSubject.setText(classSubject);
        holder.dateTime.setText(dateTime);

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("header", header);
//                bundle.putString("body", body);
//                Intent I = new Intent(context, FAQActivity.class);
//                I.putExtras(bundle);
//                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eClassroomScheduledClassesListModelList.size();
    }
}
