package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

import com.celerii.celerii.Activities.Inbox.ChatActivity;
import com.celerii.celerii.Activities.Inbox.Parent.ParentMessageHome;
import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Inbox.Teacher.TeacherMessageHome;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
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
        public ImageView profilePic, unReadMessagesOne, /*unReadMessagesTwo,*/ messageStatus, noOfmesages;
        public LinearLayout profilePictureClipper;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
            time = (TextView) view.findViewById(R.id.time);
            profilePic = (ImageView) view.findViewById(R.id.pic);
            unReadMessagesOne = (ImageView) view.findViewById(R.id.unreadmessagesone);
//            unReadMessagesTwo = (ImageView) view.findViewById(R.id.unreadmessagestwo);
            profilePictureClipper = (LinearLayout) view.findViewById(R.id.profilepictureclipper);
            this.view = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sendANewMessage, chiefLayout;
        RelativeLayout errorLayout;
        TextView errorLayoutText;
        Button errorLayoutButton;

        public HeaderViewHolder(View view) {
            super(view);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            sendANewMessage = (LinearLayout) view.findViewById(R.id.sendanewmessage);
            errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
            errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
            errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
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
        if(holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).sendANewMessage.setVisibility(View.GONE);
            if (inboxList.size() <= 1) {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                String errorMessage = "";
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    errorMessage = "You don't have any messages at this time. To start a new conversation with your children's teachers or school, tap the " + "<b>" + "Send a new message" + "</b>" + " header above or click the " + "<b>" + "Send a new message" + "</b>" + " button below";
                } else {
                    errorMessage = "You don't have any messages at this time. To start a new conversation with your student's parents, your colleagues or school, tap the " + "<b>" + "Send a new message" + "</b>" + " header above or click the " + "<b>" + "Send a new message" + "</b>" + " button below";
                }
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
                ((HeaderViewHolder) holder).errorLayoutButton.setText("Send a new message");
                ((HeaderViewHolder) holder).errorLayoutButton.setVisibility(View.VISIBLE);
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

            ((HeaderViewHolder) holder).errorLayoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sharedPreferencesManager.getActiveAccount().equals("Parent")){
                        Intent I = new Intent(context, ParentMessageHome.class);
                        Bundle b = new Bundle();
                        b.putString("ID", sharedPreferencesManager.getMyUserID());
                        I.putExtras(b);
                        ((Activity) context).startActivity(I);
                    } else {
                        Intent I = new Intent(context, TeacherMessageHome.class);
                        Bundle b = new Bundle();
                        b.putString("ID", sharedPreferencesManager.getMyUserID());
                        I.putExtras(b);
                        ((Activity) context).startActivity(I);
                    }
                }
            });

        } else if (holder instanceof MyViewHolder) {
            final MessageList messageList = inboxList.get(position);

            ((MyViewHolder) holder).name.setText(messageList.getName());
            ((MyViewHolder) holder).message.setText(messageList.getMessage());
            ((MyViewHolder) holder).time.setText(Date.getRelativeTimeSpan(messageList.getTime()));
            ((MyViewHolder) holder).profilePictureClipper.setClipToOutline(true);

            if (messageList.getReceiverID().equals(sharedPreferencesManager.getMyUserID())) {
                if (!messageList.isSeen()) {
                    ((MyViewHolder) holder).unReadMessagesOne.setVisibility(View.VISIBLE);
                } else {
                    ((MyViewHolder) holder).unReadMessagesOne.setVisibility(View.INVISIBLE);
                }
            }
            else {
                ((MyViewHolder) holder).unReadMessagesOne.setVisibility(View.INVISIBLE);
            }

            Drawable textDrawable;
            if (!messageList.getName().isEmpty()) {
                String[] nameArray = messageList.getName().replaceAll("\\s+", " ").trim().split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
                }
                ((MyViewHolder) holder).profilePic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
            }

            Glide.with(context)
                    .load(messageList.getProfilepicUrl())
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder) holder).profilePic);

            ((MyViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, ChatActivity.class);
                    Bundle b = new Bundle();
                    b.putString("ID", messageList.getReceiverID());
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
