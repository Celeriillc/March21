package com.celerii.celerii.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
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
import com.celerii.celerii.Activities.Profiles.TeacherProfileOneActivity;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.DisconnectionModel;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.ParentSchoolConnectionRequest;
import com.celerii.celerii.models.SearchExistingIncomingAndOutgoingConnections;
import com.celerii.celerii.models.SearchHistoryRow;
import com.celerii.celerii.models.SearchResultsRow;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.TeacherSchoolConnectionRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DELL on 9/2/2017.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.MyViewHolder> {

    SharedPreferencesManager sharedPreferencesManager;
    private List<SearchResultsRow> searchResultsRowList;
    private Context context;
    private SearchExistingIncomingAndOutgoingConnections searchExistingIncomingAndOutgoingConnections;
    public HashMap<String, ArrayList<String>> guardians;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    int classesToBeRemovedCounter = 0;
    CustomProgressDialogOne customProgressDialogOne;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView entityName, entityLocationClass;
        private ImageView entityPic;
        private LinearLayout entityPicClipper;
        Button sendRequest;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            entityName = (TextView) view.findViewById(R.id.entityname);
            entityLocationClass = (TextView) view.findViewById(R.id.entitylocation_class);
            entityPic = (ImageView) view.findViewById(R.id.entitypic);
            entityPicClipper = (LinearLayout) view.findViewById(R.id.entitypictureclipper);
            sendRequest = (Button) view.findViewById(R.id.sendrequest);
            clickableView = view;
        }
    }

    public SearchResultsAdapter(List<SearchResultsRow> searchResultsRowList, Context context, SearchExistingIncomingAndOutgoingConnections
            searchExistingIncomingAndOutgoingConnections) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.searchResultsRowList = searchResultsRowList;
        this.context = context;
        this.searchExistingIncomingAndOutgoingConnections = searchExistingIncomingAndOutgoingConnections;
        guardians = new HashMap<>();
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        customProgressDialogOne = new CustomProgressDialogOne(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_row, parent, false);
        return new MyViewHolder(itemView);
    }

    int counter = 0;
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final SearchResultsRow searchResultsRow = searchResultsRowList.get(position);

        holder.entityName.setText(searchResultsRow.getEntityName());
        if (searchResultsRow.getEntityAddress().isEmpty()) {
            holder.entityLocationClass.setVisibility(View.GONE);
        }
        holder.entityLocationClass.setText(searchResultsRow.getEntityAddress());
        holder.entityPicClipper.setClipToOutline(true);
        final String entityId = searchResultsRow.getEntityId();
        final String entityName = searchResultsRow.getEntityName();

        Drawable textDrawable;
        if (!searchResultsRow.getEntityName().isEmpty()) {
            String[] nameArray = searchResultsRow.getEntityName().replaceAll("\\s+", " ").trim().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
            }
            holder.entityPic.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
        }

        if (!searchResultsRow.getEntityPic().isEmpty()) {
            Glide.with(context)
                    .load(searchResultsRow.getEntityPic())
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .crossFade()
                    .centerCrop().bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.entityPic);
        }

        if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
            if (searchResultsRow.getEntityType().equals("School")) {
                holder.sendRequest.setVisibility(View.VISIBLE);

                if (searchExistingIncomingAndOutgoingConnections.getExistingConnections().contains(entityId)) {
                    holder.sendRequest.setText("Disconnect");
                    holder.sendRequest.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
                    holder.sendRequest.setTextColor(ContextCompat.getColor(context, R.color.black));
                } else if (searchExistingIncomingAndOutgoingConnections.getPendingIncomingRequests().contains(entityId)) {
                    holder.sendRequest.setText("Connect");
                    holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
                    holder.sendRequest.setTextColor(Color.WHITE);
                } else if (searchExistingIncomingAndOutgoingConnections.getPendingOutgoingRequests().contains(entityId)) {
                    holder.sendRequest.setText("Revoke");
                    holder.sendRequest.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
                    holder.sendRequest.setTextColor(ContextCompat.getColor(context, R.color.black));
                } else {
                    holder.sendRequest.setText("Connect");
                    holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
                    holder.sendRequest.setTextColor(Color.WHITE);
                }

            } else {
                holder.sendRequest.setVisibility(View.GONE);
            }
        }
        else if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
            if (searchResultsRow.getEntityType().equals("Student")) {
                holder.sendRequest.setVisibility(View.VISIBLE);

                if (searchExistingIncomingAndOutgoingConnections.getExistingConnections().contains(entityId)) {
                    holder.sendRequest.setText("Disconnect");
                    holder.sendRequest.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
                    holder.sendRequest.setTextColor(ContextCompat.getColor(context, R.color.black));
                } else if (searchExistingIncomingAndOutgoingConnections.getPendingIncomingRequests().contains(entityId)) {
//                    holder.sendRequest.setText("Respond");
//                    holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
//                    holder.sendRequest.setTextColor(Color.WHITE);
                    holder.sendRequest.setVisibility(View.GONE);
                } else if (searchExistingIncomingAndOutgoingConnections.getPendingOutgoingRequests().contains(entityId)) {
                    holder.sendRequest.setText("Revoke");
                    holder.sendRequest.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
                    holder.sendRequest.setTextColor(ContextCompat.getColor(context, R.color.black));
                } else {
                    holder.sendRequest.setText("Connect");
                    holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
                    holder.sendRequest.setTextColor(Color.WHITE);
                }
            } else {
                holder.sendRequest.setVisibility(View.GONE);
            }
        }

        holder.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    CustomToast.blueBackgroundToast(context, "Your device is not connected to the internet. Check your connection and try again.");
                    return;
                }

                if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {

                    if (holder.sendRequest.getText().equals("Connect")) {

                        if (searchExistingIncomingAndOutgoingConnections.getPendingIncomingRequests().size() == 0) {
                            customProgressDialogOne.show();
                            holder.sendRequest.setText("Revoke");
                            holder.sendRequest.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
                            holder.sendRequest.setTextColor(ContextCompat.getColor(context, R.color.black));

                            mDatabaseReference = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid()).child(entityId).push();
                            String refKey = mDatabaseReference.getKey();

                            String timeSent = Date.getDate();
                            String sorttableTimeSent = Date.convertToSortableDate(timeSent);
                            TeacherSchoolConnectionRequest teacherSchoolConnectionRequest = new TeacherSchoolConnectionRequest("Pending", timeSent, sorttableTimeSent, mFirebaseUser.getUid(), entityId);
                            NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), entityId, "School", "Teacher", timeSent, sorttableTimeSent, refKey, "ConnectionRequest", "", "", false);

                            Map<String, Object> newRequestMap = new HashMap<String, Object>();
                            mDatabaseReference = mFirebaseDatabase.getReference();
                            newRequestMap.put("Teacher To School Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + refKey, teacherSchoolConnectionRequest);
                            newRequestMap.put("Teacher To School Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + refKey, teacherSchoolConnectionRequest);
                            newRequestMap.put("NotificationSchool/" + entityId + "/" + refKey, notificationModel);

                            mDatabaseReference.updateChildren(newRequestMap);
                            searchExistingIncomingAndOutgoingConnections.getPendingOutgoingRequests().add(entityId);
                            notifyDataSetChanged();
                            customProgressDialogOne.dismiss();
                        } else {
                            customProgressDialogOne.show();
                            holder.sendRequest.setText("Disconnect");
                            holder.sendRequest.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
                            holder.sendRequest.setTextColor(ContextCompat.getColor(context, R.color.black));
                            String time = Date.getDate();
                            String sortableTime = Date.convertToSortableDate(time);

                            final String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(entityId).push().getKey();
                            final NotificationModel notification = new NotificationModel(mFirebaseUser.getUid(), entityId, "School", "Teacher", time, sortableTime, notificationPushID, "Connection", "", "", false);

                            mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(entityId);
                            mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Map<String, Object> newConnectionMap = new HashMap<String, Object>();
                                    DatabaseReference newRef = mFirebaseDatabase.getReference();
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            String pendingRequestKey = postSnapshot.getKey();
                                            newConnectionMap.put("School To Teacher Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + pendingRequestKey + "/" + "status", "Accepted");
                                            newConnectionMap.put("School To Teacher Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Accepted");
                                            newConnectionMap.put("NotificationTeacher/" + mFirebaseUser.getUid() + "/" + pendingRequestKey, null);
                                        }
                                    }
                                    newConnectionMap.put("School Teacher/" + entityId + "/" + mFirebaseUser.getUid(), true);
                                    newConnectionMap.put("Teacher School/" + mFirebaseUser.getUid() + "/" + entityId, true);
                                    newConnectionMap.put("NotificationSchool/" + entityId + "/" + notificationPushID, notification);
                                    newRef.updateChildren(newConnectionMap);
                                    searchExistingIncomingAndOutgoingConnections.getExistingConnections().add(entityId);
                                    searchExistingIncomingAndOutgoingConnections.getPendingIncomingRequests().remove(entityId);
                                    notifyDataSetChanged();
                                    customProgressDialogOne.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }
                    else if (holder.sendRequest.getText().equals("Revoke")) {

                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.custom_dialog_request_connection);
                        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
                        Button cancel = (Button) dialog.findViewById(R.id.cancel);
                        Button action = (Button) dialog.findViewById(R.id.action);
                        try {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        } catch (Exception e) {
                            return;
                        }
//                        dialog.getWindow().setLayout((19 * width) / 20, RecyclerView.LayoutParams.WRAP_CONTENT);

                        String messageString = "Do you want to revoke your request to connect to " + "<b>" + entityName + "</b>" + "?";
                        message.setText(Html.fromHtml(messageString));

                        action.setText("Revoke");

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                                customProgressDialogOne.show();
                                holder.sendRequest.setEnabled(false);

                                mDatabaseReference = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid()).child(entityId);
                                mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            Map<String, Object> newRequestMap = new HashMap<String, Object>();
                                            DatabaseReference newRef = mFirebaseDatabase.getReference();
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                String pendingRequestKey = postSnapshot.getKey();
                                                newRequestMap.put("Teacher To School Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + pendingRequestKey + "/" + "status", "Revoked");
                                                newRequestMap.put("Teacher To School Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Revoked");
                                                newRequestMap.put("NotificationSchool/" + entityId + "/" + pendingRequestKey, null);
                                            }

                                            newRef.updateChildren(newRequestMap);
                                        }

                                        holder.sendRequest.setText("Connect");
                                        holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
                                        holder.sendRequest.setTextColor(Color.WHITE);
                                        searchExistingIncomingAndOutgoingConnections.getPendingOutgoingRequests().remove(entityId);
                                        holder.sendRequest.setEnabled(true);
                                        notifyDataSetChanged();
                                        customProgressDialogOne.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                    }
                    else if (holder.sendRequest.getText().equals("Disconnect")) {

                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.custom_dialog_request_connection);
                        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
                        Button cancel = (Button) dialog.findViewById(R.id.cancel);
                        Button action = (Button) dialog.findViewById(R.id.action);
                        try {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        } catch (Exception e) {
                            return;
                        }
//                        dialog.getWindow().setLayout((19 * width) / 20, RecyclerView.LayoutParams.WRAP_CONTENT);

                        String messageString = "Disconnecting would restrict your access to all " + "<b>" + entityName + "</b>" + "'s information, including class and " +
                                "student information. To regain access, you'll need to send a new request. Do you wish to disconnect?";
                        message.setText(Html.fromHtml(messageString));

                        action.setText("Disconnect");

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                                customProgressDialogOne.show();
                                holder.sendRequest.setEnabled(false);

                                String time = Date.getDate();
                                String sorttableTime = Date.convertToSortableDate(time);

                                final Map<String, Object> newDisconnectionMap = new HashMap<String, Object>();
                                final DatabaseReference newDisconnectionRef = mFirebaseDatabase.getReference();
                                String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(entityId).push().getKey();
                                String disconnectionKey = mFirebaseDatabase.getReference("Teacher To School Request Teacher").child(mFirebaseUser.getUid()).child(entityId).push().getKey();
                                TeacherSchoolConnectionRequest teacherSchoolConnectionRequest = new TeacherSchoolConnectionRequest("Disconnected", time, sorttableTime, mFirebaseUser.getUid(), entityId);
                                NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), entityId, "School", "Teacher", time, sorttableTime, notificationPushID, "Disconnection", "", "", false);

                                newDisconnectionMap.put("Teacher School/" + mFirebaseUser.getUid() + "/" + entityId, null);
                                newDisconnectionMap.put("School Teacher/" + entityId + "/" + mFirebaseUser.getUid(), null);
                                newDisconnectionMap.put("Teacher To School Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + disconnectionKey, teacherSchoolConnectionRequest);
                                newDisconnectionMap.put("Teacher To School Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + disconnectionKey, teacherSchoolConnectionRequest);
                                newDisconnectionMap.put("NotificationSchool/" + entityId + "/" + notificationPushID, notificationModel);

                                mDatabaseReference = mFirebaseDatabase.getReference().child("School Class").child(entityId);
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                newDisconnectionMap.put("Teacher Class/" + mFirebaseUser.getUid() + "/" + postSnapshot.getKey(), null);
                                                newDisconnectionMap.put("Class Teacher/" + postSnapshot.getKey() + "/" + mFirebaseUser.getUid(), null);
                                            }
                                        }

                                        holder.sendRequest.setText("Connect");
                                        holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
                                        holder.sendRequest.setTextColor(Color.WHITE);
                                        newDisconnectionRef.updateChildren(newDisconnectionMap);
                                        searchExistingIncomingAndOutgoingConnections.getExistingConnections().remove(entityId);
                                        holder.sendRequest.setEnabled(true);
                                        notifyDataSetChanged();
                                        customProgressDialogOne.dismiss();
                                        String message = "You've been successfully disconnected from " + "<b>" + entityName + "</b>" + "'s account. You will no longer have access to or receive notifications from their account. To reconnect, use the search button to send a fresh connection request";
                                        showDialogWithMessage(Html.fromHtml(message));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                    }
                    else if (holder.sendRequest.getText().equals("Respond")) {

                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.custom_dialog_request_connection);
                        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
                        Button cancel = (Button) dialog.findViewById(R.id.cancel);
                        Button action = (Button) dialog.findViewById(R.id.action);
                        try {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        } catch (Exception e) {
                            return;
                        }
//                        dialog.getWindow().setLayout((19 * width) / 20, RecyclerView.LayoutParams.WRAP_CONTENT);

                        String messageString = "<b>" + entityName + "</b>" + " sent you a connection request, accepting this request will give you access to their students, classes and data. Do you " +
                                "wish to accept this request?";
                        message.setText(Html.fromHtml(messageString));

                        action.setText("Accept");
                        cancel.setText("Decline");

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                customProgressDialogOne.show();
                                holder.sendRequest.setEnabled(false);

                                String time = Date.getDate();
                                String sorttableTime = Date.convertToSortableDate(time);

                                final String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(entityId).push().getKey();
                                final NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), entityId, "School", "Teacher", time, sorttableTime, notificationPushID, "ConnectionRequestDeclined", "", "", false);


                                mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(entityId);
                                mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            Map<String, Object> newRequestMap = new HashMap<String, Object>();
                                            DatabaseReference newRef = mFirebaseDatabase.getReference();
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                String pendingRequestKey = postSnapshot.getKey();
                                                newRequestMap.put("School To Teacher Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + pendingRequestKey + "/" + "status", "Declined");
                                                newRequestMap.put("School To Teacher Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Declined");
                                                newRequestMap.put("NotificationSchool/" + entityId + "/" + pendingRequestKey, notificationModel);
                                            }
                                            newRef.updateChildren(newRequestMap);
                                        }

                                        holder.sendRequest.setText("Connect");
                                        holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
                                        holder.sendRequest.setTextColor(Color.WHITE);
                                        searchExistingIncomingAndOutgoingConnections.getPendingIncomingRequests().remove(entityId);
                                        holder.sendRequest.setEnabled(true);
                                        notifyDataSetChanged();
                                        customProgressDialogOne.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                                customProgressDialogOne.show();
                                holder.sendRequest.setEnabled(false);

                                String time = Date.getDate();
                                String sorttableTime = Date.convertToSortableDate(time);

                                final Map<String, Object> newConnectionMap = new HashMap<String, Object>();
                                final DatabaseReference newConnectionRef = mFirebaseDatabase.getReference();
                                final String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(entityId).push().getKey();
                                final NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), entityId, "School", "Teacher", time, sorttableTime, notificationPushID, "Connection", "", "", false);

                                mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(entityId);
                                mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                String pendingRequestKey = postSnapshot.getKey();
                                                newConnectionMap.put("School To Teacher Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + pendingRequestKey + "/" + "status", "Accepted");
                                                newConnectionMap.put("School To Teacher Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Accepted");
                                                newConnectionMap.put("School Teacher/" + entityId + "/" + mFirebaseUser.getUid(), true);
                                                newConnectionMap.put("Teacher School/" + mFirebaseUser.getUid() + "/" + entityId, true);
                                                newConnectionMap.put("NotificationSchool/" + entityId + "/" + pendingRequestKey, notificationModel);
                                            }
                                        }

                                        holder.sendRequest.setText("Disconnect");
                                        holder.sendRequest.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
                                        holder.sendRequest.setTextColor(ContextCompat.getColor(context, R.color.black));
                                        newConnectionRef.updateChildren(newConnectionMap);
                                        searchExistingIncomingAndOutgoingConnections.getExistingConnections().add(entityId);
                                        searchExistingIncomingAndOutgoingConnections.getPendingIncomingRequests().remove(entityId);
                                        holder.sendRequest.setEnabled(true);
                                        notifyDataSetChanged();
                                        customProgressDialogOne.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }

                }
                else if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {

                    if (holder.sendRequest.getText().equals("Connect")) {

                        customProgressDialogOne.show();
                        holder.sendRequest.setText("Revoke");
                        holder.sendRequest.setBackgroundResource(R.drawable.rounded_button_white_light_gray);
                        holder.sendRequest.setTextColor(ContextCompat.getColor(context, R.color.black));

                        mDatabaseReference = mFirebaseDatabase.getReference("Student Connection Request Sender").child(mFirebaseUser.getUid()).push();
                        final String refKey = mDatabaseReference.getKey();

                        final Map<String, Object> newRequestMap = new HashMap<String, Object>();

                        mDatabaseReference = mFirebaseDatabase.getReference("Student Parent").child(entityId);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (!guardians.containsKey(entityId)) {
                                            guardians.put(entityId, new ArrayList<String>());
                                        }
                                        guardians.get(entityId).add(postSnapshot.getKey() + " Parent");
                                    }
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference("Student School").child(entityId);
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                if (!guardians.containsKey(entityId)) {
                                                    guardians.put(entityId, new ArrayList<String>());
                                                }
                                                guardians.get(entityId).add(postSnapshot.getKey() + " School");
                                            }
                                        }

                                        String timeSent = Date.getDate();
                                        String sorttableTimeSent = Date.convertToSortableDate(timeSent);
                                        ParentSchoolConnectionRequest parentSchoolConnectionRequest = new ParentSchoolConnectionRequest("Pending", timeSent, sorttableTimeSent, mFirebaseUser.getUid(), "Parent", entityId, refKey, "", "", guardians.get(entityId));

                                        newRequestMap.put("Student Connection Request Sender/" + mFirebaseUser.getUid() + "/" + refKey, parentSchoolConnectionRequest);

                                        if (guardians.get(entityId) != null && guardians.get(entityId).size() != 0) {
                                            for (int i = 0; i < guardians.get(entityId).size(); i++) {
                                                String recipientID = guardians.get(entityId).get(i).split(" ")[0];
                                                String recipientAccountType = guardians.get(entityId).get(i).split(" ")[1];

                                                if (!recipientID.equals(mFirebaseUser.getUid())) {
                                                    NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), recipientID, recipientAccountType, "Parent", timeSent, sorttableTimeSent, refKey, "ConnectionRequest", searchResultsRow.getEntityPic(), entityName, false);

                                                    newRequestMap.put("Student Connection Request Recipients/" + recipientID + "/" + refKey, parentSchoolConnectionRequest);
                                                    if (recipientAccountType.equals("School")) {
                                                        newRequestMap.put("NotificationSchool/" + recipientID + "/" + refKey, notificationModel);
                                                    } else if (recipientAccountType.equals("Parent")) {
                                                        newRequestMap.put("NotificationParent/" + recipientID + "/" + refKey, notificationModel);
                                                    }
                                                }
                                            }
                                        } else {
                                            //Todo: lost student account
                                        }

                                        mDatabaseReference = mFirebaseDatabase.getReference();
                                        mDatabaseReference.updateChildren(newRequestMap);
                                        searchExistingIncomingAndOutgoingConnections.getPendingOutgoingRequests().add(entityId);
                                        notifyDataSetChanged();
                                        customProgressDialogOne.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    else if (holder.sendRequest.getText().equals("Revoke")) {

                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.custom_dialog_request_connection);
                        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
                        Button cancel = (Button) dialog.findViewById(R.id.cancel);
                        Button action = (Button) dialog.findViewById(R.id.action);
                        try {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        } catch (Exception e) {
                            return;
                        }

                        String messageString = "Do you want to revoke your request to connect to " + "<b>" + entityName + "</b>" + "'s profile?";
                        message.setText(Html.fromHtml(messageString));

                        action.setText("Revoke");

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                customProgressDialogOne.show();
                                holder.sendRequest.setEnabled(false);

                                final ArrayList<String> pendingRequestKeys = new ArrayList<String>();
                                mDatabaseReference = mFirebaseDatabase.getReference("Student Connection Request Sender").child(mFirebaseUser.getUid());
                                mDatabaseReference.orderByChild("requestStatus").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Map<String, Object> newRequestMap = new HashMap<String, Object>();
                                            DatabaseReference newRef = mFirebaseDatabase.getReference();
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                String pendingRequestKey = postSnapshot.getKey();
                                                ParentSchoolConnectionRequest parentSchoolConnectionRequest = postSnapshot.getValue(ParentSchoolConnectionRequest.class);
                                                if (parentSchoolConnectionRequest.getStudentID().equals(entityId))
                                                {
                                                    ArrayList<String> recipients = parentSchoolConnectionRequest.getRequestReciepients();
                                                    newRequestMap.put("Student Connection Request Sender/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/requestStatus", "Revoked");

                                                    if (recipients == null) continue;
                                                    if (recipients.size() == 0) continue;

                                                    for (int i = 0; i < recipients.size(); i++) {
                                                        String recipientID = recipients.get(i).split(" ")[0];
                                                        String recipientAccountType = recipients.get(i).split(" ")[1];

                                                        newRequestMap.put("Student Connection Request Recipients/" + recipientID + "/" + pendingRequestKey + "/requestStatus", "Revoked");
                                                        newRequestMap.put("NotificationSchool/" + recipientID + "/" + pendingRequestKey, null);
                                                        newRequestMap.put("NotificationParent/" + recipientID + "/" + pendingRequestKey, null);
                                                    }
                                                    break;
                                                }
                                            }

                                            holder.sendRequest.setText("Connect");
                                            holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
                                            holder.sendRequest.setTextColor(Color.WHITE);
                                            newRef.updateChildren(newRequestMap);
                                            searchExistingIncomingAndOutgoingConnections.getPendingOutgoingRequests().remove(entityId);
                                            holder.sendRequest.setEnabled(true);
                                            notifyDataSetChanged();
                                            customProgressDialogOne.dismiss();
                                        } else {
                                            holder.sendRequest.setEnabled(true);
                                            holder.sendRequest.setText("Connect");
                                            holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
                                            holder.sendRequest.setTextColor(Color.WHITE);
                                            customProgressDialogOne.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                    }
                    else if (holder.sendRequest.getText().equals("Disconnect")) {
                        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.custom_dialog_request_connection);
                        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
                        Button cancel = (Button) dialog.findViewById(R.id.cancel);
                        Button action = (Button) dialog.findViewById(R.id.action);
                        try {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        } catch (Exception e) {
                            return;
                        }

                        String messageString = "Disconnecting would restrict your access to " + "<b>" + entityName + "</b>" + "'s information, including class stories and " +
                                "attendance information. To regain access, you'll need to send a new request to their school. Do you wish to disconnect?";
                        message.setText(Html.fromHtml(messageString));

                        cancel.setText("Cancel");
                        action.setText("Disconnect");

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                customProgressDialogOne.show();
                                holder.sendRequest.setEnabled(false);

                                final String timeSent = Date.getDate();
                                final String sorttableTimeSent = Date.convertToSortableDate(timeSent);

                                final Map<String, Object> newDisconnectionMap = new HashMap<String, Object>();
                                DatabaseReference newDisconnectionRef = mFirebaseDatabase.getReference().child("Disconnection Subject").child(mFirebaseUser.getUid()).push();
                                final String disconnectionRefKey = newDisconnectionRef.getKey();
                                DisconnectionModel disconnectionModel = new DisconnectionModel(mFirebaseUser.getUid(), entityId, disconnectionRefKey, timeSent, sorttableTimeSent);

                                newDisconnectionMap.put("Parents Students/" + mFirebaseUser.getUid() + "/" + entityId, null);
                                newDisconnectionMap.put("Student Parent/" + entityId + "/" + mFirebaseUser.getUid(), null);
                                newDisconnectionMap.put("Disconnection Subject/" + mFirebaseUser.getUid() + "/" + disconnectionRefKey, disconnectionModel);
                                newDisconnectionMap.put("Disconnection Object/" + entityId + "/" + disconnectionRefKey, disconnectionModel);

                                mDatabaseReference = mFirebaseDatabase.getReference("Student Parent").child(entityId);
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                if (!guardians.containsKey(entityId)) {
                                                    guardians.put(entityId, new ArrayList<String>());
                                                }
                                                guardians.get(entityId).add(postSnapshot.getKey() + " Parent");
                                            }
                                        }

                                        mDatabaseReference = mFirebaseDatabase.getReference("Student School").child(entityId);
                                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        if (!guardians.containsKey(entityId)) {
                                                            guardians.put(entityId, new ArrayList<String>());
                                                        }
                                                        guardians.get(entityId).add(postSnapshot.getKey() + " School");
                                                    }
                                                }

                                                if (guardians.get(entityId) != null) {
                                                    if (guardians.get(entityId).size() != 0) {
                                                        for (int i = 0; i < guardians.get(entityId).size(); i++) {
                                                            String recipientID = guardians.get(entityId).get(i).split(" ")[0];
                                                            String recipientAccountType = guardians.get(entityId).get(i).split(" ")[1];

                                                            if (!recipientID.equals(mFirebaseUser.getUid())) {
                                                                NotificationModel notificationModel = new NotificationModel(mFirebaseUser.getUid(), recipientID, recipientAccountType, "Parent", timeSent, sorttableTimeSent, disconnectionRefKey, "Disconnection", searchResultsRow.getEntityPic(), entityId, false);

                                                                if (recipientAccountType.equals("School")) {
                                                                    newDisconnectionMap.put("NotificationSchool/" + recipientID + "/" + disconnectionRefKey, notificationModel);
                                                                } else if (recipientAccountType.equals("Parent")) {
                                                                    newDisconnectionMap.put("NotificationParent/" + recipientID + "/" + disconnectionRefKey, notificationModel);
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    //Todo: lost student account
                                                }

                                                DatabaseReference newDisconnectionRef = mFirebaseDatabase.getReference();
                                                holder.sendRequest.setText("Connect");
                                                holder.sendRequest.setBackgroundResource(R.drawable.roundedbutton);
                                                holder.sendRequest.setTextColor(Color.WHITE);
                                                newDisconnectionRef.updateChildren(newDisconnectionMap);
                                                searchExistingIncomingAndOutgoingConnections.getExistingConnections().remove(entityId);
                                                notifyDataSetChanged();
                                                holder.sendRequest.setEnabled(true);
                                                customProgressDialogOne.dismiss();
                                                sharedPreferencesManager.deleteActiveKid();
                                                String message = "You've been successfully disconnected from " + "<b>" + entityName + "</b>" + "'s account. You will no longer have access to or receive notifications from their account. To reconnect, use the search button to send a fresh connection request";
                                                showDialogWithMessage(Html.fromHtml(message));
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

        holder.clickableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    if (searchResultsRow.getEntityType().equals("Student")){
                        if (!searchExistingIncomingAndOutgoingConnections.getExistingConnections().contains(entityId)) {
                            String messageString = "You don't have the permission to view " + "<b>" + entityName + "</b>" + "'s information. If you" +
                                    " know " + "<b>" + entityName + "</b>" + " and would like to access their information, send a connection request to their school by using" +
                                    " the " + "<b>" + "Connect" + "</b>" + " button.";
                            showDialogWithMessage(Html.fromHtml(messageString));
                            return;
                        }
                    }
                }

                if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                    CustomToast.blueBackgroundToast(context, "Your device is not connected to the internet. Check your connection and try again.");
                    return;
                }

                Bundle bundle = new Bundle();
                Intent I;
                if (searchResultsRow.getEntityType().equals("School")){
                    bundle.putString("schoolID", entityId);
                    I = new Intent(context, SchoolProfileActivity.class);
                }
                else if (searchResultsRow.getEntityType().equals("Student")) {
                    I = new Intent(context, StudentProfileActivity.class);
                    Gson gson = new Gson();
                    Student student = new Student(searchResultsRow.getEntityName(), searchResultsRow.getEntityId(), searchResultsRow.getEntityPic());
                    String studentCred = gson.toJson(student);
                    bundle.putString("childID", studentCred);
                }
                else {
                    bundle.putString("ID", entityId);
                    I = new Intent(context, TeacherProfileOneActivity.class);
                }

                String time = Date.getDate();

                SearchHistoryRow searchHistoryRow = new SearchHistoryRow(entityId, searchResultsRow.getEntityName(), searchResultsRow.getEntityAddress(), searchResultsRow.getEntityType(), time);
                Map<String, Object> searchHistoryObject = new HashMap<String, Object>();

                if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                    searchHistoryObject.put("MySearchHistory/Teachers/" + mFirebaseUser.getUid() + "/" + entityId, searchHistoryRow);
                }
                else if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    searchHistoryObject.put("MySearchHistory/Parents/" + mFirebaseUser.getUid() + "/" + entityId, searchHistoryRow);
                }

                mDatabaseReference = mFirebaseDatabase.getReference();
                mDatabaseReference.updateChildren(searchHistoryObject);

                I.putExtras(bundle);
                context.startActivity(I);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResultsRowList.size();
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
