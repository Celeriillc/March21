package com.celerii.celerii.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.celerii.celerii.Activities.Home.Teacher.TeacherCreateClassPostActivity;
import com.celerii.celerii.Activities.StudentPerformance.EnterResultsActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.models.TeacherCreateClassPostAddImageModel;

import java.util.ArrayList;

public class TeacherCreateClassPostAddImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<TeacherCreateClassPostAddImageModel> teacherCreateClassPostAddImageModelList;
    private Context context;
    private static final int Header = 1;
    private static final int Normal = 2;
    private static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView storyImage, delete;
        public RelativeLayout imageLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            storyImage = itemView.findViewById(R.id.storyimage);
            delete = itemView.findViewById(R.id.delete);
            imageLayout = itemView.findViewById(R.id.imagelayout);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public ImageView storyImage, icon;
        public RelativeLayout imageLayout;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);

            storyImage = itemView.findViewById(R.id.storyimage);
            icon = itemView.findViewById(R.id.icon);
            imageLayout = itemView.findViewById(R.id.imagelayout);
        }
    }

    public TeacherCreateClassPostAddImageAdapter(ArrayList<TeacherCreateClassPostAddImageModel> teacherCreateClassPostAddImageModelList, Context context) {
        this.teacherCreateClassPostAddImageModelList = teacherCreateClassPostAddImageModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_create_class_post_add_image_row, parent, false);
                return new MyViewHolder(rowView);
            case Footer:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_create_class_post_add_image_footer, parent, false);
                return new FooterViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_create_class_post_add_image_row, parent, false);
                return new MyViewHolder(rowView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).imageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof TeacherCreateClassPostActivity) {
                        ((TeacherCreateClassPostActivity) context).addNewImage();
                    }
                }
            });
        } else if (holder instanceof MyViewHolder) {
            TeacherCreateClassPostAddImageModel teacherCreateClassPostAddImageModel = teacherCreateClassPostAddImageModelList.get(position);
            ((MyViewHolder) holder).imageLayout.setClipToOutline(true);
            if (teacherCreateClassPostAddImageModel.getBitmap() != null) {
                ((MyViewHolder) holder).storyImage.setImageBitmap(null);
                ((MyViewHolder) holder).storyImage.setImageBitmap(teacherCreateClassPostAddImageModel.getBitmap());
            }
            ((MyViewHolder) holder).delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateOff(((MyViewHolder) holder).imageLayout);
                    teacherCreateClassPostAddImageModelList.remove(teacherCreateClassPostAddImageModel);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return teacherCreateClassPostAddImageModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionFooter (position)) {
            return Footer;
        }
        return Normal;
    }

    private boolean isPositionFooter (int position) {
        return position == teacherCreateClassPostAddImageModelList.size() - 1;
    }

    public void animateOn(final RelativeLayout view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.15f, 0.0f, 1.15f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimationOn(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimationOn(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(300);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    private Animation prepareAnimationOn(Animation animation){
        animation.setRepeatCount(0);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    public void animateOff(final RelativeLayout view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimationOff(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        prepareAnimationOff(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(2000);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    private Animation prepareAnimationOff(Animation animation){
        animation.setRepeatCount(0);
//        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}
