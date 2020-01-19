package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.celerii.celerii.Activities.EditProfiles.EditParentProfileActivity;
import com.celerii.celerii.Activities.Events.EventsRowActivity;
import com.celerii.celerii.Activities.Home.Parent.MoreParentFragment;
import com.celerii.celerii.Activities.Intro.IntroSlider;
import com.celerii.celerii.Activities.Newsletters.NewsletterRowActivity;
import com.celerii.celerii.Activities.StudentAttendance.ParentAttendanceActivity;
import com.celerii.celerii.Activities.Profiles.ParentProfileActivity;
import com.celerii.celerii.Activities.StudentBehaviouralPerformance.BehaviouralResultActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Settings.SettingsActivityParent;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.Activities.Profiles.StudentProfileActivity;
import com.celerii.celerii.Activities.Utility.SwitchActivityParentTeacher;
import com.celerii.celerii.Activities.Home.Teacher.TeacherMainActivityTwo;
import com.celerii.celerii.Activities.Timetable.TeacherTimetableActivity;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.MoreParentsHeaderModel;
import com.celerii.celerii.models.MoreParentsModel;
import com.celerii.celerii.models.Parent;
import com.celerii.celerii.models.Teacher;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 11/21/2017.
 */

public class MoreParentsAdapter extends RecyclerView.Adapter<MoreParentsAdapter.MyViewHolder> {
    private List<MoreParentsModel> moreParentsModelList;
    private Context context;
    MoreParentFragment mFragment;
    private int lastSelectedPosition;
    SharedPreferencesManager sharedPreferencesManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView childName;
        public ImageView childPic;
        public RadioButton selectedChild;
        public LinearLayout parentView;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            childName = (TextView) view.findViewById(R.id.childname);
            childPic = (ImageView) view.findViewById(R.id.childpic);
            parentView = (LinearLayout) view.findViewById(R.id.parentview);
//            selectedChild = (RadioButton) view.findViewById(R.id.selectedchild);
            clickableView = view;
        }
    }

    public MoreParentsAdapter(List<MoreParentsModel> moreParentsModelList,
                              Context context, MoreParentFragment mFragment) {
        this.moreParentsModelList = moreParentsModelList;
        this.context = context;
        this.mFragment = mFragment;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        if (moreParentsModelList.size() == 0){
            lastSelectedPosition = -1;
        }
    }

    @Override
    public MoreParentsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;

        rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_body_parent, parent, false);
        return new MoreParentsAdapter.MyViewHolder(rowView);
    }

    //TODO: Monitor outPosition's performance
    private int outPosition = 0;
    public void onBindViewHolder(final MoreParentsAdapter.MyViewHolder holder, int position) {
        outPosition = position;

        final MoreParentsModel moreParentsModel = moreParentsModelList.get(position);

        if (moreParentsModelList.size() == 0){
            return;
        }

        String activeKid = null;
        activeKid = sharedPreferencesManager.getActiveKid();
        if (activeKid != null) {
            activeKid = activeKid.split(" ")[0];
        }

        if (activeKid == null){
            lastSelectedPosition = 1;
        }

        if (activeKid != null) {
            if (activeKid.equals(moreParentsModel.getChildId())) {
                lastSelectedPosition = position;
            }
        }

        (holder).childName.setText(moreParentsModel.getChildName());
        if (lastSelectedPosition == position) {
//            (holder).selectedChild.setChecked(true);
            holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.round_button_gradient));
            holder.childName.setTextColor(ContextCompat.getColor(context, R.color.white));
            sharedPreferencesManager.setActiveKid(moreParentsModel.getChildId() + " " + moreParentsModel.getChildName());
            mFragment.loadFooter();
        } else {
            holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_white_with_purple_border));
            holder.childName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
//            (holder).selectedChild.setChecked(false);
        }

        Glide.with(context)
                .load(moreParentsModel.getChildPicUrl())
                .centerCrop()
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((holder).childPic);

//        (holder).selectedChild.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        (holder).clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = outPosition;

                String id = moreParentsModel.getChildId();
                String name = moreParentsModel.getChildName();
                String kid = id + " " + name;

                sharedPreferencesManager.setActiveKid(kid);

                notifyDataSetChanged();
//                Intent I = new Intent(context, StudentProfileActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("childID", moreParentsModel.getChildId());
//                I.putExtras(bundle);
//                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moreParentsModelList.size();
    }
}