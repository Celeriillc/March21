package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.celerii.celerii.Activities.StudentBehaviouralPerformance.StudentRewardHome;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.models.ManageKidsModel;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 8/14/2017.
 */

public class TeacherHomeClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<ManageKidsModel> manageKidsModelList;
    public String className;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView kidName;
        public ImageView kidPic;
        public LinearLayout kidPicClipper;
        public Button viewProfile;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            kidName = (TextView) view.findViewById(R.id.kidname);
            kidPic = (ImageView) view.findViewById(R.id.kidpic);
            kidPicClipper = (LinearLayout) view.findViewById(R.id.kidpicclipper);
//            viewProfile = (Button) view.findViewById(R.id.viewprofile);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        Button rewardClass, punishClass;
        LinearLayout chiefLayout;
        RelativeLayout errorLayout;
        TextView errorLayoutText;

        public HeaderViewHolder(View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            rewardClass = (Button) view.findViewById(R.id.rewardclass);
            punishClass = (Button) view.findViewById(R.id.punishclass);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
            errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        }
    }

    public TeacherHomeClassAdapter(List<ManageKidsModel> manageKidsModelList, String className, Context context) {
        this.manageKidsModelList = manageKidsModelList;
        this.className = className;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_home_class_row, parent, false);
                return new TeacherHomeClassAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_home_class_header, parent, false);
                return new TeacherHomeClassAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_home_class_row, parent, false);
                return new TeacherHomeClassAdapter.MyViewHolder(rowView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).className.setText(className);
            if (manageKidsModelList.size() <= 1) {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                String errorMessage = className + " does not have any students to display. Go to " + "<b>" + "More" + "</b>" + " (the 3 horizontal dots below) to change the active class to another with students";
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
            } else {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            ((HeaderViewHolder) holder).rewardClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            ((HeaderViewHolder) holder).punishClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else if (holder instanceof MyViewHolder) {
            final ManageKidsModel manageKidsModel = manageKidsModelList.get(position);

            ((MyViewHolder) holder).kidName.setText(manageKidsModel.getName());
            ((MyViewHolder) holder).kidPicClipper.setClipToOutline(true);

            Drawable textDrawable;
            if (!manageKidsModel.getName().isEmpty()) {
                String[] nameArray = manageKidsModel.getName().replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 100);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 100);
                }
                ((MyViewHolder) holder).kidPic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 100);
            }

            if (!manageKidsModel.getPicURL().isEmpty()) {
                Glide.with(context)
                        .load(manageKidsModel.getPicURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(((MyViewHolder) holder).kidPic);
            }

            ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("kidnumber", manageKidsModel.getID());
                    bundle.putString("studentID", manageKidsModel.getID());
                    bundle.putString("studentName", manageKidsModel.getName());
                    bundle.putString("studentPicURL", manageKidsModel.getPicURL());
                    Intent I = new Intent(context, StudentRewardHome.class);
                    I.putExtras(bundle);
                    context.startActivity(I);
                }
            });

//            ((MyViewHolder) holder).viewProfile.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent I = new Intent(context, StudentProfileActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("childID", manageKidsModel.getID());
//                    I.putExtras(bundle);
//                    context.startActivity(I);
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return manageKidsModelList.size();
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
        return position == manageKidsModelList.size () - 1;
    }
}
