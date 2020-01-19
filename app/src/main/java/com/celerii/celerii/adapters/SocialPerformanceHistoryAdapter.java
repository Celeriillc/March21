package com.celerii.celerii.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.models.SocialPerformanceHistoryHeader;
import com.celerii.celerii.models.SocialPerformanceHistoryRow;
import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

/**
 * Created by DELL on 9/3/2017.
 */

public class SocialPerformanceHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SocialPerformanceHistoryRow> socialPerformanceHistoryRowList;
    private SocialPerformanceHistoryHeader socialPerformanceHistoryHeader;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, class_teacher, point;
        public ImageView pointImage;

        public MyViewHolder(final View view) {
            super(view);
            pointImage = (ImageView) view.findViewById(R.id.behavoirpic);
            title = (TextView) view.findViewById(R.id.behavoirtitle);
            class_teacher = (TextView) view.findViewById(R.id.class_teacher);
            point = (TextView) view.findViewById(R.id.pointawarded);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView pointsAwared, pointsFined, totalPoints, priTemp, secTemp;

        public HeaderViewHolder(View view) {
            super(view);
            pointsAwared = (TextView) view.findViewById(R.id.pointsawarded);
            pointsFined = (TextView) view.findViewById(R.id.pointsfined);
            totalPoints = (TextView) view.findViewById(R.id.totalpoints);
            priTemp = (TextView) view.findViewById(R.id.primarytemperament);
            secTemp = (TextView) view.findViewById(R.id.secondarytemperament);
        }
    }

    public SocialPerformanceHistoryAdapter(List<SocialPerformanceHistoryRow> socialPerformanceHistoryRowList, SocialPerformanceHistoryHeader socialPerformanceHistoryHeader, Context context) {
        this.socialPerformanceHistoryRowList = socialPerformanceHistoryRowList;
        this.socialPerformanceHistoryHeader = socialPerformanceHistoryHeader;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_performance_history_row, parent, false);
                return new SocialPerformanceHistoryAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_performance_history_header, parent, false);
                return new SocialPerformanceHistoryAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_performance_history_row, parent, false);
                return new SocialPerformanceHistoryAdapter.MyViewHolder(rowView);
        }

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).pointsAwared.setText("+" + socialPerformanceHistoryHeader.getPointsAwarded());
            ((HeaderViewHolder) holder).pointsFined.setText("-" + socialPerformanceHistoryHeader.getPointsFined());
            ((HeaderViewHolder) holder).totalPoints.setText("+/-" + socialPerformanceHistoryHeader.getTotalPoints());
            ((HeaderViewHolder) holder).priTemp.setText("+" + socialPerformanceHistoryHeader.getPrimaryTemp());
            ((HeaderViewHolder) holder).secTemp.setText("+" + socialPerformanceHistoryHeader.getSecondaryTemp());
        }
        else if (holder instanceof MyViewHolder) {
            SocialPerformanceHistoryRow socialPerformanceHistoryRow = socialPerformanceHistoryRowList.get(position);

            ((MyViewHolder) holder).title.setText(socialPerformanceHistoryRow.getTitle());
            ((MyViewHolder) holder).class_teacher.setText(socialPerformanceHistoryRow.getClassName() + " (" + socialPerformanceHistoryRow.getTeacher() + ")");
            ((MyViewHolder) holder).point.setText(socialPerformanceHistoryRow.getPoint());

            String letter = socialPerformanceHistoryRow.getPoint();
            TextDrawable textDrawable;
            if (socialPerformanceHistoryRow.getPointInt() > 0){
                textDrawable= TextDrawable.builder()
                        .buildRound(letter, Color.rgb(0,255,0));
            } else{
                textDrawable = TextDrawable.builder()
                        .buildRound(letter, Color.rgb(255,0,0));
            }

            ((MyViewHolder) holder).pointImage.setImageDrawable(textDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return socialPerformanceHistoryRowList.size();
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
        return position == socialPerformanceHistoryRowList.size () + 1;
    }
}
