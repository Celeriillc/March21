package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.GalleryDetailForSingleImageActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.models.EClassroomMessageBoardModel;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ParentEClassroomMessageBoardAdapter extends RecyclerView.Adapter<ParentEClassroomMessageBoardAdapter.MyViewHolder>{
    private List<EClassroomMessageBoardModel> eClassroomMessageBoardModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, time;
        public ImageView messageStatus, otherProfilePic, imageFile;
        public LinearLayout imageClipper, profilePictureClipper, layout;
        public View divider;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message_text);
            time = (TextView) view.findViewById(R.id.time);
            imageClipper = (LinearLayout) view.findViewById(R.id.imageClipper);
            profilePictureClipper = (LinearLayout) view.findViewById(R.id.profilepictureclipper);
            layout = (LinearLayout) view.findViewById(R.id.bubble_layout);
            messageStatus = (ImageView) view.findViewById(R.id.messagestatus);
            otherProfilePic = (ImageView) view.findViewById(R.id.otherprofilepic);
            imageFile = (ImageView) view.findViewById(R.id.imagefile);
            divider = view.findViewById(R.id.divider);
        }
    }

    public ParentEClassroomMessageBoardAdapter(List<EClassroomMessageBoardModel> eClassroomMessageBoardModelList, Context context) {
        this.eClassroomMessageBoardModelList = eClassroomMessageBoardModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row, parent, false);
        return new MyViewHolder(itemView);
    }

    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final EClassroomMessageBoardModel eClassroomMessageBoardModel = eClassroomMessageBoardModelList.get(position);

        holder.imageClipper.setClipToOutline(true);
        holder.profilePictureClipper.setClipToOutline(true);
        holder.message.setText(Html.fromHtml(eClassroomMessageBoardModel.getMessage()));
        holder.time.setText(Date.getRelativeTimeSpan(eClassroomMessageBoardModel.getDate()));
        holder.name.setText(eClassroomMessageBoardModel.getSenderName());

        if (eClassroomMessageBoardModel.getFileURL().isEmpty()) {
            holder.message.setVisibility(View.VISIBLE);
            holder.imageClipper.setVisibility(View.GONE);
        } else {
            holder.message.setVisibility(View.GONE);
            holder.imageClipper.setVisibility(View.VISIBLE);
        }

        if (position == eClassroomMessageBoardModelList.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        Drawable textDrawable;
        if (!eClassroomMessageBoardModel.getSenderName().isEmpty()) {
            String[] nameArray = eClassroomMessageBoardModel.getSenderName().replaceAll("\\s+", " ").trim().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 40);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 40);
            }
            holder.otherProfilePic.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 40);
        }

        if (!eClassroomMessageBoardModel.getSenderProfilePictureURL().isEmpty()) {
            Glide.with(context)
                    .load(eClassroomMessageBoardModel.getSenderProfilePictureURL())
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.otherProfilePic);
        }

        Glide.with(context)
                .load(eClassroomMessageBoardModel.getFileURL())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.imageFile);

        holder.imageFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("ImageURL", eClassroomMessageBoardModel.getFileURL());
                Intent I = new Intent(context, GalleryDetailForSingleImageActivity.class);
                I.putExtras(b);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((MyViewHolder) holder).imageFile.setTransitionName("imageTransition");
                    Pair<View, String> pair1 = Pair.create((View) ((MyViewHolder) holder).imageFile, ((MyViewHolder) holder).imageFile.getTransitionName());

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, ((MyViewHolder) holder).imageFile, ((MyViewHolder) holder).imageFile.getTransitionName());
                    context.startActivity(I, optionsCompat.toBundle());
                }
                else {
                    context.startActivity(I);
                }
            }
        });

    }

    public int getItemCount() {
        return eClassroomMessageBoardModelList.size();
    }
}
