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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.ELibrary.ELibraryStudentPerformanceDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.models.EClassroomParticipantsModel;
import com.celerii.celerii.models.ELibraryStudentPerformanceHomeModel;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class EClassroomParticipantsAdapter extends RecyclerView.Adapter<EClassroomParticipantsAdapter.MyViewHolder> {

    private List<EClassroomParticipantsModel> eClassroomParticipantsModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public LinearLayout profilePictureClipper;
        public ImageView studentProfilePicture, wasPresent;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            profilePictureClipper = (LinearLayout) view.findViewById(R.id.clipper);
            studentProfilePicture = (ImageView) view.findViewById(R.id.pic);
            wasPresent = (ImageView) view.findViewById(R.id.waspresent);
            clickableView = view;
        }
    }

    public EClassroomParticipantsAdapter(List<EClassroomParticipantsModel> eClassroomParticipantsModelList, Context context) {
        this.eClassroomParticipantsModelList = eClassroomParticipantsModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_classroom_participants_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final EClassroomParticipantsModel eClassroomParticipantsModel = eClassroomParticipantsModelList.get(position);

        holder.name.setText(eClassroomParticipantsModel.getName());
        holder.profilePictureClipper.setClipToOutline(true);

        if (eClassroomParticipantsModel.getWasPresent()) {
            holder.wasPresent.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_radio_checked_black_24dp));
        } else {
            holder.wasPresent.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_cancel_24));
        }

        Drawable textDrawable;
        if (!eClassroomParticipantsModel.getName().isEmpty()) {
            String[] nameArray = eClassroomParticipantsModel.getName().replaceAll("\\s+", " ").trim().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 50);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 50);
            }
            (holder).studentProfilePicture.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 50);
        }

        Glide.with(context)
                .load(eClassroomParticipantsModel.getProfilePictureURL())
                .placeholder(textDrawable)
                .error(textDrawable)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(context))
                .into((holder).studentProfilePicture);
    }

    @Override
    public int getItemCount() {
        return eClassroomParticipantsModelList.size();
    }
}
