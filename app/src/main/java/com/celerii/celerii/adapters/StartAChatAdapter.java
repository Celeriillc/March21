package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.Activities.Inbox.ChatActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.models.StartAChatModel;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 11/20/2017.
 */

public class StartAChatAdapter extends RecyclerView.Adapter<StartAChatAdapter.MyViewHolder> {
    private List<StartAChatModel> startAChatModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, otherInfo;
        public ImageView profilePic;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            otherInfo = (TextView) view.findViewById(R.id.otherinfo);
            profilePic = (ImageView) view.findViewById(R.id.profilepic);
            clickableView = view;
        }
    }

    public StartAChatAdapter(List<StartAChatModel> startAChatModelList, Context context) {
        this.startAChatModelList = startAChatModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.start_a_chat_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StartAChatModel startAChatModel = startAChatModelList.get(position);

        holder.name.setText(startAChatModel.getName());
        holder.otherInfo.setText(startAChatModel.getOtherInfo());
        Glide.with(context)
                .load(startAChatModel.getProfilePicURL())
                .placeholder(R.drawable.profileimageplaceholder)
                .error(R.drawable.profileimageplaceholder)
                .centerCrop()
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.profilePic);


        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(context, ChatActivity.class);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return startAChatModelList.size();
    }


}
