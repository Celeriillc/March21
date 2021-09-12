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

import com.celerii.celerii.Activities.EMeeting.Parent.ParentEMeetingMessageBoardActivity;
import com.celerii.celerii.Activities.EMeeting.Teacher.TeacherEMeetingMessageBoardActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.models.EMeetingScheduledMeetingsListModel;

import java.util.List;

public class ParentEMeetingScheduledMeetingsListAdapter extends RecyclerView.Adapter<ParentEMeetingScheduledMeetingsListAdapter.MyViewHolder>{
    private List<EMeetingScheduledMeetingsListModel> eMeetingScheduledMeetingsListModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView meetingTitle, schoolTime;
        public ImageView pic;
        public LinearLayout picClipper;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            meetingTitle = (TextView) view.findViewById(R.id.meetingtitle);
            schoolTime = (TextView) view.findViewById(R.id.schooltime);
            pic = (ImageView) view.findViewById(R.id.pic);
            picClipper = (LinearLayout) view.findViewById(R.id.picclipper);
            clickableView = view;
        }
    }

    public ParentEMeetingScheduledMeetingsListAdapter(List<EMeetingScheduledMeetingsListModel> eMeetingScheduledMeetingsListModelList, Context context) {
        this.eMeetingScheduledMeetingsListModelList = eMeetingScheduledMeetingsListModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_meeting_scheduled_meetings_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EMeetingScheduledMeetingsListModel eMeetingScheduledMeetingsListModel = eMeetingScheduledMeetingsListModelList.get(position);

        String meetingTitle = eMeetingScheduledMeetingsListModel.getScheduledMeetingTitle();
        String date = Date.getDate();
        String schoolTime;
        if (Date.compareDates(date, eMeetingScheduledMeetingsListModel.getDateScheduled())) {
            schoolTime = eMeetingScheduledMeetingsListModel.getSchoolName() + " - " + Date.getRelativeTimeSpan(eMeetingScheduledMeetingsListModel.getDateScheduled()) + " - " + Date.DateFormatMMDDYYYY(eMeetingScheduledMeetingsListModel.getDateScheduled()) + " by " + Date.DateFormatHHMM(eMeetingScheduledMeetingsListModel.getDateScheduled());
        } else {
            schoolTime = eMeetingScheduledMeetingsListModel.getSchoolName() + " - " + Date.getRelativeTimeSpanForward(eMeetingScheduledMeetingsListModel.getDateScheduled()) + " - " + Date.DateFormatMMDDYYYY(eMeetingScheduledMeetingsListModel.getDateScheduled()) + " by " + Date.DateFormatHHMM(eMeetingScheduledMeetingsListModel.getDateScheduled());
        }
        holder.meetingTitle.setText(meetingTitle);
        holder.schoolTime.setText(schoolTime);
        holder.picClipper.setClipToOutline(true);
        holder.picClipper.setClipToOutline(true);
        Drawable textDrawable;
        if (!eMeetingScheduledMeetingsListModel.getSchoolName().isEmpty()) {
            String[] nameArray = eMeetingScheduledMeetingsListModel.getSchoolName().replaceAll("\\s+", " ").trim().split(" ");
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
                bundle.putString("Scheduled Meeting ID", eMeetingScheduledMeetingsListModel.getScheduledMeetingID());
                bundle.putString("Scheduled Meeting Link", eMeetingScheduledMeetingsListModel.getMeetingLink());
                bundle.putString("Scheduled Meeting State", "Scheduled");
                bundle.putString("Scheduled Meeting Scheduled Date", eMeetingScheduledMeetingsListModel.getDateScheduled());
                Intent I = new Intent(context, ParentEMeetingMessageBoardActivity.class);
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eMeetingScheduledMeetingsListModelList.size();
    }
}
