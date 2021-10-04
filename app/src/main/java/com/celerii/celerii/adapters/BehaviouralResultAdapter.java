package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.celerii.celerii.Activities.EditTermAndYearInfo.EditYearActivity;
import com.celerii.celerii.Activities.EditTermAndYearInfo.EnterResultsEditTermActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.BehaviouralResultRowModel;
import com.celerii.celerii.models.BehaviouralResultsHeaderModel;
import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

/**
 * Created by DELL on 5/9/2019.
 */

public class BehaviouralResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SharedPreferencesManager sharedPreferencesManager;
    private List<BehaviouralResultRowModel> behaviouralResultRowModelList;
    private BehaviouralResultsHeaderModel behaviouralResultsHeaderModel;
    private Context context;
    private Activity activity;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView reward, className, newBadge, point;
        public ImageView pointPic;

        public MyViewHolder(final View view) {
            super(view);
            pointPic = (ImageView) view.findViewById(R.id.pointpic);
            reward = (TextView) view.findViewById(R.id.reward);
            className = (TextView) view.findViewById(R.id.classname);
            newBadge = (TextView) view.findViewById(R.id.newbadge);
            point = (TextView) view.findViewById(R.id.point);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView term, year, totalPointsEarned, totalPointsFined, pointsEarnedThisTerm, pointsFinedThisTerm;
        LinearLayout chiefLayout;
        RelativeLayout errorLayout;
        TextView errorLayoutText;
        Button errorLayoutButton;

        public HeaderViewHolder(View view) {
            super(view);
            term = (TextView) view.findViewById(R.id.term);
            year = (TextView) view.findViewById(R.id.year);
            totalPointsEarned = (TextView) view.findViewById(R.id.totalpointsearned);
            totalPointsFined = (TextView) view.findViewById(R.id.totalpointsfined);
            pointsEarnedThisTerm = (TextView) view.findViewById(R.id.pointsearnedthisterm);
            pointsFinedThisTerm = (TextView) view.findViewById(R.id.pointsfinedthisterm);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
            errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
            errorLayoutButton = (Button) errorLayout.findViewById(R.id.errorlayoutbutton);
        }
    }

    public BehaviouralResultAdapter(List<BehaviouralResultRowModel> behaviouralResultRowModelList, BehaviouralResultsHeaderModel behaviouralResultsHeaderModel, Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.behaviouralResultRowModelList = behaviouralResultRowModelList;
        this.behaviouralResultsHeaderModel = behaviouralResultsHeaderModel;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.behavioural_result_row, parent, false);
                return new BehaviouralResultAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.behavioural_result_header, parent, false);
                return new BehaviouralResultAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.behavioural_result_row, parent, false);
                return new BehaviouralResultAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {

            ((HeaderViewHolder) holder).term.setText(Term.Term(behaviouralResultsHeaderModel.getTerm()));
            ((HeaderViewHolder) holder).year.setText(behaviouralResultsHeaderModel.getYear());
            ((HeaderViewHolder) holder).totalPointsEarned.setText(behaviouralResultsHeaderModel.getTotalPointsEarned());
            ((HeaderViewHolder) holder).totalPointsFined.setText(behaviouralResultsHeaderModel.getTotalPointsFined());
            ((HeaderViewHolder) holder).pointsEarnedThisTerm.setText(behaviouralResultsHeaderModel.getPointsEarnedThisTerm());
            ((HeaderViewHolder) holder).pointsFinedThisTerm.setText(behaviouralResultsHeaderModel.getPointsFinedThisTerm());

            if (behaviouralResultRowModelList.size() <= 1) {
                String errorMessage = "There are no behavioural records for the "  + "<b>" + Term.Term(behaviouralResultsHeaderModel.getTerm()) + "</b>" + " of " +  "<b>" +  behaviouralResultsHeaderModel.getYear() + "</b>" + " yet";
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

                if (!behaviouralResultsHeaderModel.getErrorMessage().equals("")) {
                    ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(behaviouralResultsHeaderModel.getErrorMessage()));
                } else {
                    ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
                }

                ((HeaderViewHolder) holder).errorLayoutButton.setVisibility(View.GONE);
            } else {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            ((HeaderViewHolder) holder).term.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EnterResultsEditTermActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Term", behaviouralResultsHeaderModel.getTerm());
                    intent.putExtras(bundle);
                    ((Activity)context).startActivityForResult(intent, 0);
                }
            });

            ((HeaderViewHolder) holder).year.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditYearActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Year", behaviouralResultsHeaderModel.getYear());
                    intent.putExtras(bundle);
                    ((Activity)context).startActivityForResult(intent, 1);
                }
            });

        } else if (holder instanceof MyViewHolder) {
            BehaviouralResultRowModel behaviouralResultRowModel = behaviouralResultRowModelList.get(position);

            ((MyViewHolder) holder).reward.setText(behaviouralResultRowModel.getReward());
            ((MyViewHolder) holder).className.setText(behaviouralResultRowModel.getClassName());
            ((MyViewHolder) holder).point.setText(behaviouralResultRowModel.getPoint());

//            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                if (behaviouralResultRowModel.getNew()) {
                    ((MyViewHolder) holder).newBadge.setVisibility(View.VISIBLE);
                } else {
                    ((MyViewHolder) holder).newBadge.setVisibility(View.GONE);
                }
//            }

            TextDrawable textDrawable;
            if (behaviouralResultRowModel.getPoint().equals("+1")) {
                textDrawable = TextDrawable.builder().buildRound(behaviouralResultRowModel.getPoint(), context.getResources().getColor(R.color.colorPrimaryPurple));
            } else {
                textDrawable = TextDrawable.builder().buildRound(behaviouralResultRowModel.getPoint(), context.getResources().getColor(R.color.colorAccent));
            }
            ((MyViewHolder) holder).pointPic.setImageDrawable(textDrawable);
        }
    }

    @Override
    public int getItemCount() {
        return behaviouralResultRowModelList.size();
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
        return position == behaviouralResultRowModelList.size () + 1;
    }
}
