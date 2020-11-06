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
import com.celerii.celerii.models.MessageList;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by user on 7/5/2017.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder>{

    private List<MessageList> messageListList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, time;
        public ImageView profilePic, messageStatus, noOfmesages;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
//            noOfmesages = (ImageView) view.findViewById(R.id.messagenumber);
            time = (TextView) view.findViewById(R.id.time);
            profilePic = (ImageView) view.findViewById(R.id.pic);
//            messageStatus = (ImageView) view.findViewById(R.id.messageStatus);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, ChatActivity.class);
                    context.startActivity(I);
                }
            });

            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public MessageListAdapter(List<MessageList> messageListList, Context context) {
        this.messageListList = messageListList;
        this.context = context;
    }

    @Override
    public MessageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list_row, parent, false);
        return new MessageListAdapter.MyViewHolder(itemView);
    }

    public void onBindViewHolder(MessageListAdapter.MyViewHolder holder, int position) {

        MessageList messageList = messageListList.get(position);

        holder.name.setText(messageList.getName());
        holder.message.setText(messageList.getMessage());
        holder.time.setText(messageList.getTime());
//        if (messageList.getNoOfMessages() != 0){
//            holder.noOfmesages.setText(String.valueOf(messageList.getNoOfMessages()));
//        }
//        else {
//            holder.noOfmesages.setVisibility(View.GONE);
//        }

        if (!messageList.getProfilepicUrl().isEmpty()) {
            Glide.with(context)
                    .load(messageList.getProfilepicUrl())
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.profilePic);
        }

//        holder.messageStatus.setImageResource(R.drawable.ic_not_sent_black_24dp);
    }

    @Override
    public int getItemCount() {
        return messageListList.size();
    }

    /**
     * Created by user on 7/15/2017.
     */

    public static class ParentAttendanceRowAdapter {
    }
}
