package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Inbox.ChatActivity;
import com.celerii.celerii.Activities.Inbox.Parent.ParentMessageHome;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Inbox.Teacher.TeacherMessageHome;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.MessageList;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 11/6/2017.
 */

public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MessageList> inboxList;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;
    SharedPreferencesManager sharedPreferencesManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, time;
        public ImageView profilePic, unReadMessagesOne, unReadMessagesTwo, messageStatus, noOfmesages;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
            time = (TextView) view.findViewById(R.id.time);
            profilePic = (ImageView) view.findViewById(R.id.pic);
            unReadMessagesOne = (ImageView) view.findViewById(R.id.unreadmessagesone);
            unReadMessagesTwo = (ImageView) view.findViewById(R.id.unreadmessagestwo);
            this.view = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sendANewMessage, chiefLayout;
        RelativeLayout errorLayout;
        TextView errorLayoutText;

        public HeaderViewHolder(View view) {
            super(view);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            sendANewMessage = (LinearLayout) view.findViewById(R.id.sendanewmessage);
            errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
            errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
        }
    }

    public InboxAdapter(List<MessageList> inboxList, Context context) {
        this.inboxList = inboxList;
        this.context = context;
        sharedPreferencesManager = new SharedPreferencesManager(context);
//        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_row, parent, false);
                return new InboxAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_header, parent, false);
                return new InboxAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_row, parent, false);
                return new InboxAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder){
            if (inboxList.size() <= 1){
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                String errorMessage = "";
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    errorMessage = "You don't have any messages at this time. To start a new conversation with your children's teachers or school, tap the " + "<b>" + "Send a new message" + "</b>" + " button";
                } else {
                    errorMessage = "You don't have any messages at this time. To start a new conversation with your student's parents, your colleagues or school, tap the " + "<b>" + "Send a new message" + "</b>" + " button";
                }
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
            } else {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            ((HeaderViewHolder) holder).sendANewMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedPreferencesManager.getActiveAccount().equals("Parent")){
                        Intent I = new Intent(context, ParentMessageHome.class);
                        Bundle b = new Bundle();
                        b.putString("ID", sharedPreferencesManager.getMyUserID());
                        I.putExtras(b);
                        context.startActivity(I);
                    } else {
                        Intent I = new Intent(context, TeacherMessageHome.class);
                        Bundle b = new Bundle();
                        b.putString("ID", sharedPreferencesManager.getMyUserID());
                        I.putExtras(b);
                        context.startActivity(I);
                    }
                }
            });

        } else if (holder instanceof MyViewHolder){
            final MessageList messageList = inboxList.get(position);

            ((MyViewHolder) holder).name.setText(messageList.getName());
            ((MyViewHolder) holder).message.setText(messageList.getMessage());
            ((MyViewHolder) holder).time.setText(Date.getRelativeTimeSpanShort(messageList.getTime()));

            if (messageList.getRecieverID().equals(sharedPreferencesManager.getMyUserID())) {
                if (!messageList.isSeen()) {
                    ((MyViewHolder) holder).unReadMessagesOne.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).unReadMessagesTwo.setVisibility(View.VISIBLE);
                    ((MyViewHolder) holder).message.setTypeface(null, Typeface.BOLD);
                    ((MyViewHolder) holder).time.setTypeface(null, Typeface.BOLD);
                } else {
                    ((MyViewHolder) holder).unReadMessagesOne.setVisibility(View.INVISIBLE);
                    ((MyViewHolder) holder).unReadMessagesTwo.setVisibility(View.INVISIBLE);
                    ((MyViewHolder) holder).message.setTypeface(null, Typeface.NORMAL);
                    ((MyViewHolder) holder).time.setTypeface(null, Typeface.NORMAL);
                }
            }
            else{
                ((MyViewHolder) holder).unReadMessagesOne.setVisibility(View.INVISIBLE);
                ((MyViewHolder) holder).unReadMessagesTwo.setVisibility(View.INVISIBLE);
                ((MyViewHolder) holder).message.setTypeface(null, Typeface.NORMAL);
                ((MyViewHolder) holder).time.setTypeface(null, Typeface.NORMAL);
            }

            Glide.with(context)
                    .load(messageList.getProfilepicUrl())
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder) holder).profilePic);

            ((MyViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, ChatActivity.class);
                    Bundle b = new Bundle();
                    b.putString("ID", messageList.getOtherParty());
                    b.putString("name", messageList.getName());
                    I.putExtras(b);
                    context.startActivity(I);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
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
        return position == inboxList.size () + 1;
    }
}
