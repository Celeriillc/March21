package com.celerii.celerii.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.Class;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 9/11/2017.
 */

public class ClassListAdapterHorizontal extends RecyclerView.Adapter<ClassListAdapterHorizontal.MyViewHolder> {

    private List<Class> classList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView className;
        public ImageView classPic, classTickedIndicator;
        public LinearLayout clipper;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            className = (TextView) view.findViewById(R.id.classname);
            classPic = (ImageView) view.findViewById(R.id.classpic);
            classTickedIndicator = (ImageView) view.findViewById(R.id.classtickedindicator);
            clipper = (LinearLayout) view.findViewById(R.id.clipper);
            clickableView = view;
        }
    }

    public ClassListAdapterHorizontal(List<Class> classList, Context context) {
        this.classList = classList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_to_post_to, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Class aClass = classList.get(position);
        holder.clipper.setClipToOutline(true);

        holder.className.setText(aClass.getClassName());
        try {
            Glide.with(context)
                .load(aClass.getClassPicURL())
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.classPic);
        } catch (Exception e) {

        }

        if (aClass.isTicked()){
            holder.classTickedIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.classTickedIndicator.setVisibility(View.GONE);
        }


        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aClass.isTicked()){
                    holder.classTickedIndicator.setVisibility(View.GONE);
                    animateOff(holder.classTickedIndicator);
                    aClass.setTicked(false);
                } else {
                    holder.classTickedIndicator.setVisibility(View.VISIBLE);
                    animateOn(holder.classTickedIndicator);
                    aClass.setTicked(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void animateOn(final ImageView view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.15f, 0.0f, 1.15f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimationOn(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimationOn(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(200);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    private Animation prepareAnimationOn(Animation animation){
        animation.setRepeatCount(0);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }

    public void animateOff(final ImageView view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimationOff(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        prepareAnimationOff(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(200);
        animation.setFillAfter(false);

        view.startAnimation(animation);
    }

    private Animation prepareAnimationOff(Animation animation){
        animation.setRepeatCount(0);
//        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}
