package com.celerii.celerii.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.SubscriptionModel;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SubscriptionModel> subscriptionModelList;
    private SubscriptionModel subscriptionModel;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;
    SharedPreferencesManager sharedPreferencesManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView status, tier, date, expiry;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            status = (TextView) view.findViewById(R.id.status);
            tier = (TextView) view.findViewById(R.id.tier);
            date = (TextView) view.findViewById(R.id.date);
            expiry = (TextView) view.findViewById(R.id.expiry);
            this.view = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView status, tier, date, expiry, errorLayoutText;
        RelativeLayout errorLayout;
        LinearLayout chiefLayout;
        public Button subscribe;

        public HeaderViewHolder(View view) {
            super(view);
            status = (TextView) view.findViewById(R.id.status);
            tier = (TextView) view.findViewById(R.id.tier);
            date = (TextView) view.findViewById(R.id.date);
            expiry = (TextView) view.findViewById(R.id.expiry);
            errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
            errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            subscribe = (Button) view.findViewById(R.id.subscribe);
        }
    }

    public SubscriptionAdapter(List<SubscriptionModel> subscriptionModelList, SubscriptionModel subscriptionModel, Context context) {
        this.subscriptionModelList = subscriptionModelList;
        this.subscriptionModel = subscriptionModel;
        this.context = context;
        sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_row, parent, false);
                return new SubscriptionAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_header, parent, false);
                return new SubscriptionAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_row, parent, false);
                return new SubscriptionAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder){
            if (subscriptionModelList.size() <= 1){
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                String errorMessage = "There are no previous subscription records";
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
            } else {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            if (!Date.compareDates(Date.getDate(), subscriptionModel.getExpiryDate())) {
                ((HeaderViewHolder) holder).status.setText("Active");
                ((HeaderViewHolder) holder).subscribe.setText("Cancel Subscription");
                ((HeaderViewHolder) holder).subscribe.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_accent));
                ((HeaderViewHolder) holder).subscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                ((HeaderViewHolder) holder).status.setText("Inactive");
                ((HeaderViewHolder) holder).subscribe.setText("Subscribe");
                ((HeaderViewHolder) holder).subscribe.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple));
                ((HeaderViewHolder) holder).subscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            ((HeaderViewHolder) holder).tier.setText(subscriptionModel.getSubscriptionTier());
            ((HeaderViewHolder) holder).date.setText(Date.getFormalDocumentDate(subscriptionModel.getSubscriptionDate()));
            ((HeaderViewHolder) holder).expiry.setText(Date.getFormalDocumentDate(subscriptionModel.getExpiryDate()));

        } else if (holder instanceof MyViewHolder){
            final SubscriptionModel subscriptionModel = subscriptionModelList.get(position);

            if (!Date.compareDates(Date.getDate(), subscriptionModel.getExpiryDate())) {
                ((MyViewHolder) holder).status.setText("Active");
            } else {
                ((MyViewHolder) holder).status.setText("Inactive");
            }
            ((MyViewHolder) holder).tier.setText(subscriptionModel.getSubscriptionTier());
            ((MyViewHolder) holder).date.setText(Date.getFormalDocumentDate(subscriptionModel.getSubscriptionDate()));
            ((MyViewHolder) holder).expiry.setText(Date.getFormalDocumentDate(subscriptionModel.getExpiryDate()));
        }
    }

    @Override
    public int getItemCount() {
        return subscriptionModelList.size();
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
        return position == subscriptionModelList.size () + 1;
    }
}
