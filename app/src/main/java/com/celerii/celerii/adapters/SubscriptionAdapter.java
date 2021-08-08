package com.celerii.celerii.adapters;

import android.app.Dialog;
import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    private String childID;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;
    SharedPreferencesManager sharedPreferencesManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView status, /*tier, date,*/ expiry;
        public ImageView image;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            status = (TextView) view.findViewById(R.id.status);
//            tier = (TextView) view.findViewById(R.id.tier);
//            date = (TextView) view.findViewById(R.id.date);
            expiry = (TextView) view.findViewById(R.id.expiry);
            image = (ImageView) view.findViewById(R.id.image);
            this.view = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView /*status, tier, date, expiry,*/ errorLayoutText;
        RelativeLayout errorLayout;
        LinearLayout chiefLayout;
        public Button subscribe;
        View headerDividerView;

        public HeaderViewHolder(View view) {
            super(view);
//            status = (TextView) view.findViewById(R.id.status);
//            tier = (TextView) view.findViewById(R.id.tier);
//            date = (TextView) view.findViewById(R.id.date);
//            expiry = (TextView) view.findViewById(R.id.expiry);
            errorLayout = (RelativeLayout) view.findViewById(R.id.errorlayout);
            errorLayoutText = (TextView) errorLayout.findViewById(R.id.errorlayouttext);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            subscribe = (Button) view.findViewById(R.id.subscribe);
            headerDividerView = view.findViewById(R.id.headerdividerview);
        }
    }

    public SubscriptionAdapter(List<SubscriptionModel> subscriptionModelList, SubscriptionModel subscriptionModel, String childID, Context context) {
        this.subscriptionModelList = subscriptionModelList;
        this.subscriptionModel = subscriptionModel;
        this.childID = childID;
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
            if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                ((HeaderViewHolder) holder).subscribe.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).headerDividerView.setVisibility(View.VISIBLE);
            } else {
                ((HeaderViewHolder) holder).subscribe.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).headerDividerView.setVisibility(View.GONE);
            }

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
//                ((HeaderViewHolder) holder).status.setText("Active");
                ((HeaderViewHolder) holder).subscribe.setText("Cancel Subscription");
                ((HeaderViewHolder) holder).subscribe.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_button_accent));
//                ((HeaderViewHolder) holder).subscribe.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
            } else {
//                ((HeaderViewHolder) holder).status.setText("Inactive");
                ((HeaderViewHolder) holder).subscribe.setText("Subscribe");
                ((HeaderViewHolder) holder).subscribe.setBackground(ContextCompat.getDrawable(context, R.drawable.roundedbutton));
//                ((HeaderViewHolder) holder).subscribe.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int f = 0;
//                    }
//                });
            }

            ((HeaderViewHolder) holder).subscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean isOpenToAll = sharedPreferencesManager.getIsOpenToAll();
                    String messageString;
                    if (isOpenToAll) {
                        messageString = "Celerii is currently in beta and is open to all, this functionality is unavailable.";
                    } else {
                        messageString = "Please update your application from the Google Play Store to subscribe or unsubscribe your child from a Celerii plan.";
                    }
                    showDialogWithMessage(messageString);
                }
            });
        } else if (holder instanceof MyViewHolder){
            final SubscriptionModel subscriptionModel = subscriptionModelList.get(position);

            if (!Date.compareDates(Date.getDate(), subscriptionModel.getExpiryDate())) {
                ((MyViewHolder) holder).status.setText("Active");
                ((MyViewHolder) holder).image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_attendance_present_24dp));
            } else {
                ((MyViewHolder) holder).status.setText("Expired");
                ((MyViewHolder) holder).image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_attendance_absent_24dp));
            }
//            ((MyViewHolder) holder).tier.setText(subscriptionModel.getSubscriptionTier());
//            ((MyViewHolder) holder).date.setText(Date.getFormalDocumentDate(subscriptionModel.getSubscriptionDate()));
            ((MyViewHolder) holder).expiry.setText(Date.getFormalDocumentDate(subscriptionModel.getExpiryDate()));

            ((MyViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                    int width = metrics.widthPixels;
                    int height = metrics.heightPixels;
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_subscription_detail_dialog);
                    TextView statusField = (TextView) dialog.findViewById(R.id.status);
                    TextView tier = (TextView) dialog.findViewById(R.id.tier);
                    TextView date = (TextView) dialog.findViewById(R.id.date);
                    TextView expiry = (TextView) dialog.findViewById(R.id.expiry);
                    Button close = (Button) dialog.findViewById(R.id.close);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    String status = "";
                    if (!Date.compareDates(Date.getDate(), subscriptionModel.getExpiryDate())) {
                        status = "Active";
                    } else {
                        status = "Expired";
                    }

                    statusField.setText(status);
                    tier.setText(subscriptionModel.getTier());
                    date.setText(Date.getFormalDocumentDate(subscriptionModel.getSubscriptionDate()));
                    expiry.setText(Date.getFormalDocumentDate(subscriptionModel.getExpiryDate()));

                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
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

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        Button OK = (Button) dialog.findViewById(R.id.optionone);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            return;
        }

        message.setText(messageString);

        OK.setText("OK");

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
