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

import com.bumptech.glide.Glide;
import com.celerii.celerii.Activities.ELibrary.ELibraryStudentPerformanceDetailActivity;
import com.celerii.celerii.Activities.Settings.FAQActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.models.ELibraryAssignmentStudentPerformanceModel;
import com.celerii.celerii.models.ELibraryStudentPerformanceHomeModel;
import com.celerii.celerii.models.FAQModel;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ELibraryStudentPerformanceHomeAdapter extends RecyclerView.Adapter<ELibraryStudentPerformanceHomeAdapter.MyViewHolder> {

    private List<ELibraryStudentPerformanceHomeModel> eLibraryStudentPerformanceHomeModelList;
    private String assignmentID;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView studentName, score;
        public LinearLayout studentProfilePictureClipper;
        public ImageView studentProfilePicture;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            studentName = (TextView) view.findViewById(R.id.studentname);
            score = (TextView) view.findViewById(R.id.score);
            studentProfilePictureClipper = (LinearLayout) view.findViewById(R.id.clipper);
            studentProfilePicture = (ImageView) view.findViewById(R.id.studentpic);
            clickableView = view;
        }
    }

    public ELibraryStudentPerformanceHomeAdapter(List<ELibraryStudentPerformanceHomeModel> eLibraryStudentPerformanceHomeModelList, String assignmentID, Context context) {
        this.eLibraryStudentPerformanceHomeModelList = eLibraryStudentPerformanceHomeModelList;
        this.assignmentID = assignmentID;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_library_student_performance_home_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ELibraryStudentPerformanceHomeModel eLibraryStudentPerformanceHomeModel = eLibraryStudentPerformanceHomeModelList.get(position);

        holder.studentName.setText(eLibraryStudentPerformanceHomeModel.getStudentName());
        holder.score.setText(eLibraryStudentPerformanceHomeModel.getScore() + "%");
        holder.studentProfilePictureClipper.setClipToOutline(true);

        Drawable textDrawable;
        if (!eLibraryStudentPerformanceHomeModel.getStudentName().isEmpty()) {
            String[] nameArray = eLibraryStudentPerformanceHomeModel.getStudentName().replaceAll("\\s+", " ").trim().split(" ");
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
                .load(eLibraryStudentPerformanceHomeModel.getStudentProfilePictureURL())
                .placeholder(textDrawable)
                .error(textDrawable)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(context))
                .into((holder).studentProfilePicture);


        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("assignmentID", assignmentID);
                bundle.putString("studentID", eLibraryStudentPerformanceHomeModel.getStudentID());
                bundle.putString("studentName", eLibraryStudentPerformanceHomeModel.getStudentName());
                bundle.putString("score", eLibraryStudentPerformanceHomeModel.getScore());
                Intent I = new Intent(context, ELibraryStudentPerformanceDetailActivity.class);
                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eLibraryStudentPerformanceHomeModelList.size();
    }
}
