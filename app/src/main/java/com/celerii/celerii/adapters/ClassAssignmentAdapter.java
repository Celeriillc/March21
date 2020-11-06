package com.celerii.celerii.adapters;

import android.content.Context;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.ClassAssignment;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 7/2/2017.
 */

public class ClassAssignmentAdapter extends RecyclerView.Adapter<ClassAssignmentAdapter.MyViewHolder>{

    private List<ClassAssignment> classAssignmentList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView poster, classreciepient, timestamp, timeDue, dueStatus, story, url, noOfViews, noOfComments, storyOptions;
        public ImageView storyimage, profilepic, commentbutton;

        public MyViewHolder(final View view) {
            super(view);
            poster = (TextView) view.findViewById(R.id.name);
            classreciepient = (TextView) view.findViewById(R.id.classreciepient);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            timeDue = (TextView) view.findViewById(R.id.txttimedue);
            dueStatus = (TextView) view.findViewById(R.id.txtduestatus);
            story = (TextView) view.findViewById(R.id.txtstory);
            url = (TextView) view.findViewById(R.id.txtUrl);
            noOfViews = (TextView) view.findViewById(R.id.viewnumber);
            noOfComments = (TextView) view.findViewById(R.id.commentnumber);
            storyimage = (ImageView) view.findViewById(R.id.storyimage);
            profilepic = (ImageView) view.findViewById(R.id.profilePic);
            commentbutton = (ImageView) view.findViewById(R.id.commentbutton);
            storyOptions = (TextView) view.findViewById(R.id.options);

            poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            profilepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            storyimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            commentbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            storyOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, v, Gravity.CENTER);
                    popupMenu.inflate(R.menu.class_story_menu);
                    popupMenu.show();
                }
            });
        }
    }

    public ClassAssignmentAdapter(List<ClassAssignment> classAssignmentList, Context context) {
        this.classAssignmentList = classAssignmentList;
        this.context = context;
    }

    @Override
    public ClassAssignmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_assignment_row, parent, false);
        return new ClassAssignmentAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClassAssignmentAdapter.MyViewHolder holder, int position) {

        ClassAssignment classAssignment = classAssignmentList.get(position);

        if (!classAssignment.getTeacherID().isEmpty()){
            holder.poster.setText(classAssignment.getTeacherID());
        }
        else {
            holder.poster.setVisibility(View.GONE);
        }

        if (!classAssignment.getClassReciepient().isEmpty()) {
            holder.classreciepient.setText(classAssignment.getClassReciepient());
        }
        else{
            holder.classreciepient.setVisibility(View.GONE);
        }

        if (!classAssignment.getTime().isEmpty()) {
//            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
//                    Long.parseLong(classStory.getTime()),
//                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
//            holder.timestamp.setText(timeAgo);
            holder.timestamp.setText(classAssignment.getTime());
        }
        else{
            holder.timestamp.setVisibility(View.GONE);
        }

        if (!classAssignment.getDueStatus().isEmpty()) {
            holder.dueStatus.setText("Assignment is " + classAssignment.getDueStatus());
        }
        else{
            holder.dueStatus.setVisibility(View.GONE);
        }

        if (!classAssignment.getTimeDue().isEmpty()) {
            holder.timeDue.setText("Assignment is due: " + classAssignment.getTimeDue());
        }
        else{
            holder.timeDue.setVisibility(View.GONE);
        }

        if (!classAssignment.getStory().isEmpty()) {
            holder.story.setText(classAssignment.getStory());
        }
        else{
            holder.story.setVisibility(View.GONE);
        }

        if (!classAssignment.getUrl().isEmpty()) {
            holder.url.setText(classAssignment.getUrl());
        }
        else {
            holder.url.setVisibility(View.GONE);
        }

        String views = String.valueOf(classAssignment.getNoOfViews());
        holder.noOfViews.setText(views + " Views");
        String comments = String.valueOf(classAssignment.getNumberOfComments());
        holder.noOfComments.setText(comments + " Comments");

        if (!classAssignment.getImageURL().isEmpty()){
            Glide.with(context)
                    .load(classAssignment.getImageURL())
                    .centerCrop()
                    .into(holder.storyimage);
        }
        else{
            holder.storyimage.setVisibility(View.GONE);
        }

        if (!classAssignment.getProfilePicURL().isEmpty()) {
            Glide.with(context)
                    .load(classAssignment.getProfilePicURL())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.profilepic);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.profilepic);
        }

    }

    @Override
    public int getItemCount() {
        return classAssignmentList.size();
    }

    public void animateHeart(final ImageView view) {
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
