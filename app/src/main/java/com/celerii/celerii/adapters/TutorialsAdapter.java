package com.celerii.celerii.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.celerii.celerii.Activities.Settings.BrowserActivityForInfo;
import com.celerii.celerii.R;
import com.celerii.celerii.models.TutorialModel;

import java.util.List;

public class TutorialsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TutorialModel> tutorialModelList;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tutorialTitle;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            tutorialTitle = (TextView) view.findViewById(R.id.tutorialtitle);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tutorialHeader;

        public HeaderViewHolder(View view) {
            super(view);

            tutorialHeader = (TextView) view.findViewById(R.id.tutorialheader);
        }
    }

    public TutorialsAdapter(List<TutorialModel> tutorialModelList, Context context) {
        this.tutorialModelList = tutorialModelList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutorial_row, parent, false);
                return new TutorialsAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutorial_header, parent, false);
                return new TutorialsAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutorial_row, parent, false);
                return new TutorialsAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {
            final TutorialModel tutorialModel = tutorialModelList.get(position);

            ((MyViewHolder) holder).tutorialTitle.setText(tutorialModel.getTutorialTitle());

            ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BrowserActivityForInfo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Header", tutorialModel.getTutorialTitle());
                    bundle.putString("URL", tutorialModel.getTutorialLink());
                    intent.putExtras(bundle);
                    ((Activity)context).startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tutorialModelList.size();
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
        return position == tutorialModelList.size () + 1;
    }
}
