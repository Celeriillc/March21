package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.Activities.Events.EventDetailActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Day;
import com.celerii.celerii.helperClasses.Time;
import com.celerii.celerii.models.EventsRow;
import com.celerii.celerii.helperClasses.Date;
import com.amulyakhare.textdrawable.TextDrawable;

import java.util.Calendar;
import java.util.List;

/**
 * Created by user on 7/27/2017.
 */

public class EventRowAdapter extends RecyclerView.Adapter<EventRowAdapter.MyViewHolder>{
    private List<EventsRow> eventsRowsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTitle, eventDate, eventDescription, eventTime;
        public ImageView dayImage;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            eventTitle = (TextView) view.findViewById(R.id.eventtitle);
            eventDate = (TextView) view.findViewById(R.id.eventdate);
            eventDescription = (TextView) view.findViewById(R.id.eventdescription);
            eventTime = (TextView) view.findViewById(R.id.eventtime);
            dayImage = (ImageView) view.findViewById(R.id.dateimg);
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final EventsRow eventRow = eventsRowsList.get(position);

        String[] datearray = eventRow.getEventDate().split(" ")[0].split("/");
        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(datearray[0]), Integer.parseInt(datearray[1]) - 1, Integer.parseInt(datearray[2]));
        final int day = c.get(Calendar.DAY_OF_WEEK);

        holder.eventTitle.setText(eventRow.getEventTitle());
        holder.eventDate.setText(Day.Day(day) + ", " + Date.DateFormatMMDDYYYY(eventRow.getEventDate()));
        holder.eventTime.setText(Time.TimeFormatHHMM(eventRow.getEventDate()));
        holder.eventDescription.setText(eventRow.getEventDescription());

        String letter = String.valueOf(Date.getDay(eventRow.getEventDate()));
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(letter, Color.rgb(56,142,60));
        holder.dayImage.setImageDrawable(textDrawable);

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("date", Day.Day(day) + ", " + Date.DateFormatMMDDYYYY(eventRow.getEventDate()));
                b.putString("time", Time.TimeFormatHHMM(eventRow.getEventDate()));
                b.putString("description", eventRow.getEventDescription());
                b.putString("title", eventRow.getEventTitle());
                b.putString("school", eventRow.getSchool());
                b.putString("key", eventRow.getKey());
                Intent I = new Intent(context, EventDetailActivity.class);
                I.putExtras(b);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsRowsList.size();
    }
}
