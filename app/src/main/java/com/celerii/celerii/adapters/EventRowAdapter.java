package com.celerii.celerii.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Events.EventDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Day;
import com.celerii.celerii.helperClasses.Time;
import com.celerii.celerii.models.EventsRow;
import com.celerii.celerii.helperClasses.Date;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by user on 7/27/2017.
 */

public class EventRowAdapter extends RecyclerView.Adapter<EventRowAdapter.MyViewHolder>{
    private List<EventsRow> eventsRowsList;
    private Context context;
//    int eventDescriptionHeight = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTitle, eventDescription, eventTime, eventSchool;
        public ImageView timeIcon;
//        public LinearLayout background;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            eventTitle = (TextView) view.findViewById(R.id.eventtitle);
            eventDescription = (TextView) view.findViewById(R.id.eventdescription);
            eventTime = (TextView) view.findViewById(R.id.eventtime);
            eventSchool = (TextView) view.findViewById(R.id.eventschool);
            timeIcon = (ImageView) view.findViewById(R.id.timeicon);
//            background = (LinearLayout) view.findViewById(R.id.background);
            clickableView = view;
        }
    }

    public EventRowAdapter(List<EventsRow> eventsRowsList, Context context) {
        this.eventsRowsList = eventsRowsList;
        this.context = context;
    }

    @Override
    public EventRowAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.events_row, parent, false);
        return new EventRowAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final EventsRow eventRow = eventsRowsList.get(position);

        String[] datearray = eventRow.getEventDate().split(" ")[0].split("/");
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(datearray[0]), Integer.parseInt(datearray[1]) - 1, Integer.parseInt(datearray[2]));
        final int day = c.get(Calendar.DAY_OF_WEEK);

        holder.eventTitle.setText(eventRow.getEventTitle());
        holder.eventTime.setText(Date.getRelativeTimeSpanForward(eventRow.getEventDate()));
        holder.eventSchool.setText(eventRow.getSchoolID());
        holder.eventDescription.setText(eventRow.getEventDescription());
//        holder.eventDescription2.setText(eventRow.getEventDescription());
        final int randomNum = ThreadLocalRandom.current().nextInt(0, 8 + 1);
        if (randomNum == 0) { holder.timeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.event_card_primary_purple)); }
        else if (randomNum == 1) { holder.timeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.event_card_accent)); }
        else if (randomNum == 2) { holder.timeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.event_card_instagram_blue)); }
        else if (randomNum == 3) { holder.timeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.event_card_teal_green)); }
        else if (randomNum == 4) { holder.timeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.event_card_kilogarm_yellow)); }
        else if (randomNum == 5) { holder.timeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.event_card_kilogarm_orange)); }
        else if (randomNum == 6) { holder.timeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.event_card_dark_gray)); }
        else if (randomNum == 7) { holder.timeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.event_card_green)); }
        else { holder.timeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.event_card_accent_secondary)); }

//        holder.eventDescription.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                eventDescriptionHeight = holder.eventDescription2.getHeight();
//                holder.eventDescription2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                holder.eventDescription2.setVisibility(View.GONE);
//            }
//        });

//        String letter = String.valueOf(Date.getDay(eventRow.getEventDate()));
//        TextDrawable textDrawable = TextDrawable.builder()
//                .buildRound(letter, Color.rgb(56,142,60));
//        holder.dayImage.setImageDrawable(textDrawable);

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                holder.eventDescription.setMaxLines(Integer.MAX_VALUE);
//                int descriptionHeight = holder.eventDescription.getHeight();// - holder.eventDescription.getLineHeight();
//                expand(holder.background, eventDescriptionHeight);

                Intent I = new Intent(context, EventDetailActivity.class);
                Bundle b = new Bundle();
                b.putString("Event ID", eventRow.getKey());
                b.putString("Color Number", String.valueOf(randomNum));
                I.putExtras(b);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsRowsList.size();
    }

    private void expand(LinearLayout mLinearLayout, int eventDescriptionHeight) {
        //set Visible
        mLinearLayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mLinearLayout.measure(widthSpec, heightSpec);
        int height = mLinearLayout.getMeasuredHeight();

        ValueAnimator mAnimator = slideAnimator(mLinearLayout.getHeight(), height + eventDescriptionHeight, mLinearLayout);
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final LinearLayout mLinearLayout) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                layoutParams.height = value;
                mLinearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
