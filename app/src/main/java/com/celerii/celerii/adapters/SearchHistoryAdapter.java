package com.celerii.celerii.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.SchoolProfileActivity;
import com.celerii.celerii.Activities.Profiles.StudentProfileActivity;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.SearchHistoryHeader;
import com.celerii.celerii.models.SearchHistoryRow;
import com.celerii.celerii.models.Student;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 9/1/2017.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SharedPreferencesManager sharedPreferencesManager;
    private List<SearchHistoryRow> searchHistoryRowList;
    private List<String> connectedStudents;
    private SearchHistoryHeader searchHistoryHeader;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView entityName, entityAddress;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            entityName = (TextView) view.findViewById(R.id.entityname);
            entityAddress = (TextView) view.findViewById(R.id.entitylocation);
            clickableView = view;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header, errorLayoutText;
        public LinearLayout chiefLayout, errorLayout;
        public ImageView errorLayoutImage;

        public HeaderViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
            errorLayoutText = (TextView) view.findViewById(R.id.errorlayouttext);
            chiefLayout = (LinearLayout) view.findViewById(R.id.chieflayout);
            errorLayout = (LinearLayout) view.findViewById(R.id.errorlayout);
            errorLayoutImage = (ImageView) view.findViewById(R.id.errorlayoutimage);
        }
    }

    public SearchHistoryAdapter(List<SearchHistoryRow> searchHistoryRowList, ArrayList<String> connectedStudents, SearchHistoryHeader searchHistoryHeader, Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.searchHistoryRowList = searchHistoryRowList;
        this.connectedStudents = connectedStudents;
        this.searchHistoryHeader = searchHistoryHeader;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_row, parent, false);
                return new SearchHistoryAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_header, parent, false);
                return new SearchHistoryAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_row, parent, false);
                return new SearchHistoryAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).header.setText(searchHistoryHeader.getHeader());

            if (searchHistoryRowList.size() <= 1) {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.VISIBLE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                String errorMessage = "";
                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    errorMessage = "Use the text field above to search for your child and connect with their account. You can also search for a school, a location or any of your children's teachers to profile or send a message";
                } else {
                    errorMessage = "Use the text field above to search for your school and connect with their account. Connecting to a school gives you access to their classes and all their students.";
                }
                ((HeaderViewHolder) holder).errorLayoutText.setText(Html.fromHtml(errorMessage));
            } else {
                ((HeaderViewHolder) holder).errorLayout.setVisibility(View.GONE);
                ((HeaderViewHolder) holder).chiefLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

        }
        else {
            final SearchHistoryRow searchHistoryRow = searchHistoryRowList.get(position);

            ((MyViewHolder)holder).entityName.setText(searchHistoryRow.getEntityName());
            ((MyViewHolder)holder).entityAddress.setText(searchHistoryRow.getEntityAddress());

            if (searchHistoryRow.getEntityAddress().isEmpty()) {
                ((MyViewHolder)holder).entityAddress.setVisibility(View.GONE);
            }

            ((MyViewHolder)holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchHistoryRow.getEntityType().equals("Student")){
                        String entityName = searchHistoryRow.getEntityName();
                        if (connectedStudents.contains(searchHistoryRow.getEntityId())) {
                            Bundle b = new Bundle();
                            Gson gson = new Gson();
                            Student student = new Student(searchHistoryRow.getEntityName(), searchHistoryRow.getEntityId(), "");
                            String studentCred = gson.toJson(student);
                            b.putString("childID", studentCred);
                            Intent I = new Intent(context, StudentProfileActivity.class);
                            I.putExtras(b);
                            context.startActivity(I);
                        } else {
                            String messageString = "You don't have the permission to view " + "<b>" + entityName + "</b>" + "'s information. If you" +
                                    " know " + "<b>" + entityName + "</b>" + " and would like to access their information, send a connection request to their school by using" +
                                    " the " + "<b>" + "Connect" + "</b>" + " button.";
                            showDialogWithMessage(Html.fromHtml(messageString));
                        }
                    } else if (searchHistoryRow.getEntityType().equals("School")){
                        Bundle b = new Bundle();
                        b.putString("schoolID", searchHistoryRow.getEntityId());
                        Intent I = new Intent(context, SchoolProfileActivity.class);
                        I.putExtras(b);
                        context.startActivity(I);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return searchHistoryRowList.size();
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
        return position == searchHistoryRowList.size () + 1;
    }

    void showDialogWithMessage (Spanned messageString) {
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
