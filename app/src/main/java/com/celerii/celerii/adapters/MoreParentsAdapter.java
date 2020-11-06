package com.celerii.celerii.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Home.Parent.MoreParentFragment;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.Student;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 11/21/2017.
 */

public class MoreParentsAdapter extends RecyclerView.Adapter<MoreParentsAdapter.MyViewHolder> {
    private List<Student> moreParentsModelList;
    private Context context;
    MoreParentFragment mFragment;
    private int lastSelectedPosition;
    SharedPreferencesManager sharedPreferencesManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView childName;
        public ImageView childPic;
        public LinearLayout parentView, childPicClipper;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            childName = (TextView) view.findViewById(R.id.childname);
            childPic = (ImageView) view.findViewById(R.id.childpic);
            parentView = (LinearLayout) view.findViewById(R.id.parentview);
            childPicClipper = (LinearLayout) view.findViewById(R.id.childpicclipper);
            clickableView = view;
        }
    }

    public MoreParentsAdapter(List<Student> moreParentsModelList, Context context, MoreParentFragment mFragment) {
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

        final Student moreParentsModel = moreParentsModelList.get(position);

        Student activeKid = null;
        Gson gson = new Gson();
        String activeKidJSON = sharedPreferencesManager.getActiveKid();
        Type type = new TypeToken<Student>() {}.getType();
        activeKid = gson.fromJson(activeKidJSON, type);

        if (activeKid.getStudentID().equals(moreParentsModel.getStudentID())) {
            holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple));
            holder.childName.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.parentView.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_white_with_purple_border));
            holder.childName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
        }

        holder.childPicClipper.setClipToOutline(true);
        holder.childName.setText(moreParentsModel.getFirstName() + " " + moreParentsModel.getLastName());

        Glide.with(context)
                .load(moreParentsModel.getImageURL())
                .centerCrop()
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .bitmapTransform(new CropCircleTransformation(context))
                .into((holder).childPic);

        (holder).clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String json = gson.toJson(moreParentsModel);
                sharedPreferencesManager.setActiveKid(json);

                notifyDataSetChanged();
                mFragment.loadFooter();
            }
        });
    }

    @Override
    public int getItemCount() {
        return moreParentsModelList.size();
    }
}