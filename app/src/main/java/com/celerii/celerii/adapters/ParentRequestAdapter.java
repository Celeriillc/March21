package com.celerii.celerii.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.ParentSchoolConnectionRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ParentRequestAdapter extends RecyclerView.Adapter<ParentRequestAdapter.MyViewHolder>{
    private List<ParentSchoolConnectionRequest> parentSchoolConnectionRequestList;
    private Context context;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    CustomProgressDialogOne customProgressDialogOne;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView notification, time;
        public ImageButton accept, decline;
        public ImageView pic;
        public LinearLayout connectionRequest, profilePictureClipper;
        public LinearLayout notificationLayout;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            notification = (TextView) view.findViewById(R.id.notification);
            pic = (ImageView) view.findViewById(R.id.pic);
            time = (TextView) view.findViewById(R.id.time);
            connectionRequest = (LinearLayout) view.findViewById(R.id.connectionrequest);
            profilePictureClipper = (LinearLayout) view.findViewById(R.id.profilepictureclipper);
            notificationLayout = (LinearLayout) view.findViewById(R.id.notificationlayout);
            accept = (ImageButton) view.findViewById(R.id.accept);
            decline = (ImageButton) view.findViewById(R.id.decline);
            this.view = view;
        }
    }

    public ParentRequestAdapter(List<ParentSchoolConnectionRequest> parentSchoolConnectionRequestList, Context context) {
        this.parentSchoolConnectionRequestList = parentSchoolConnectionRequestList;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        customProgressDialogOne = new CustomProgressDialogOne(context);
    }

    @Override
    public ParentRequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parent_request_row, parent, false);
        return new ParentRequestAdapter.MyViewHolder(itemView);
    }

    public void onBindViewHolder(ParentRequestAdapter.MyViewHolder holder, int position) {
        final ParentSchoolConnectionRequest parentSchoolConnectionRequest = parentSchoolConnectionRequestList.get(position);
        String time = Date.getRelativeTimeSpan(parentSchoolConnectionRequest.getTimeSent());
        String notification;

        holder.connectionRequest.setVisibility(View.INVISIBLE);
        if (parentSchoolConnectionRequest.getRequestSenderID().equals(mFirebaseUser.getUid())) {
            notification = "Your request to connect to " + "<b>" + parentSchoolConnectionRequest.getStudentName() + "</b>" + "'s account hasn't been responded to yet.";
            holder.notification.setText(Html.fromHtml(notification));

            Drawable textDrawable;
            if (!parentSchoolConnectionRequest.getStudentName().isEmpty()) {
                String[] nameArray = parentSchoolConnectionRequest.getStudentName().replaceAll("\\s+", " ").split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
                }
                holder.pic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
            }

            if (!parentSchoolConnectionRequest.getStudentProfilePictureURL().isEmpty()) {
                Glide.with(context)
                        .load(parentSchoolConnectionRequest.getStudentProfilePictureURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .into(holder.pic);
            }
        } else {
            notification = "<b>" + parentSchoolConnectionRequest.getRequestSenderName() + "</b>" + " has requested to connect to " + "<b>" + parentSchoolConnectionRequest.getStudentName() + "</b>" + "'s account.";
            holder.notification.setText(Html.fromHtml(notification));
            holder.connectionRequest.setVisibility(View.VISIBLE);

            Drawable textDrawable;
            if (!parentSchoolConnectionRequest.getRequestSenderName().isEmpty()) {
                String[] nameArray = parentSchoolConnectionRequest.getRequestSenderName().replaceAll("\\s+", " ").split(" ");
                if (nameArray.length == 1) {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
                } else {
                    textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
                }
                holder.pic.setImageDrawable(textDrawable);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
            }

            if (!parentSchoolConnectionRequest.getRequestSenderProfilePictureURL().isEmpty()) {
                Glide.with(context)
                        .load(parentSchoolConnectionRequest.getRequestSenderProfilePictureURL())
                        .placeholder(textDrawable)
                        .error(textDrawable)
                        .centerCrop()
                        .into(holder.pic);
            }
        }

        holder.time.setText(time);
        holder.profilePictureClipper.setClipToOutline(true);

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customProgressDialogOne.show();

                DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("Student Connection Request Recipients").child(mFirebaseUser.getUid()).child(parentSchoolConnectionRequest.getRequestKey());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String time = Date.getDate();
                            String sorttableTime = Date.convertToSortableDate(time);

                            Map<String, Object> newConnectionMap = new HashMap<String, Object>();
                            DatabaseReference newConnectionRef = mFirebaseDatabase.getReference();
                            String requestKey = dataSnapshot.getKey();
                            ParentSchoolConnectionRequest parentSchoolConnectionRequest = dataSnapshot.getValue(ParentSchoolConnectionRequest.class);

                            ArrayList<String> recipients = parentSchoolConnectionRequest.getRequestReciepients();
                            newConnectionMap.put("Student Connection Request Sender/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey + "/requestStatus", "Accepted");
                            newConnectionMap.put("Student Connection Request Sender/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey + "/requestResponder", mFirebaseUser.getUid());
                            newConnectionMap.put("Student Connection Request Sender/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey + "/requestResponse", "Accepted");
                            NotificationModel senderNotification = new NotificationModel(parentSchoolConnectionRequest.getRequestSenderID(), parentSchoolConnectionRequest.getRequestSenderID(), parentSchoolConnectionRequest.getRequestSenderAccountType(), parentSchoolConnectionRequest.getRequestSenderAccountType(), time, sorttableTime, requestKey, "Connection", parentSchoolConnectionRequest.getStudentProfilePictureURL(), parentSchoolConnectionRequest.getStudentID(), false);

                            if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                newConnectionMap.put("Student Parent/" + parentSchoolConnectionRequest.getStudentID() + "/" + parentSchoolConnectionRequest.getRequestSenderID(), true);
                                newConnectionMap.put("Parents Students/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + parentSchoolConnectionRequest.getStudentID(), true);
                                newConnectionMap.put("NotificationParent/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey, senderNotification);
                            } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                newConnectionMap.put("Student School/" + parentSchoolConnectionRequest.getStudentID() + "/" + parentSchoolConnectionRequest.getRequestSenderID(), true);
                                newConnectionMap.put("School Students/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + parentSchoolConnectionRequest.getStudentID(), true);
                                newConnectionMap.put("NotificationSchool/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey, senderNotification);
                            }

                            if (recipients == null) return;
                            if (recipients.size() == 0) return;

                            for (int i = 0; i < recipients.size(); i++) {
                                String recipientID = recipients.get(i).split(" ")[0];
                                String recipientAccountType = recipients.get(i).split(" ")[1];

                                newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestStatus", "Accepted");
                                newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestResponder", mFirebaseUser.getUid());
                                newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestResponse", "Accepted");

                                NotificationModel notification = new NotificationModel(parentSchoolConnectionRequest.getRequestSenderID(), recipientID, recipientAccountType, parentSchoolConnectionRequest.getRequestSenderAccountType(), time, sorttableTime, requestKey, "Connection", parentSchoolConnectionRequest.getStudentProfilePictureURL(), parentSchoolConnectionRequest.getStudentID(), false);
                                if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                    newConnectionMap.put("NotificationParent/" + recipientID + "/" + requestKey, notification);
                                } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                    newConnectionMap.put("NotificationSchool/" + recipientID + "/" + requestKey, notification);
                                }
                            }

                            newConnectionRef.updateChildren(newConnectionMap);
                            customProgressDialogOne.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customProgressDialogOne.show();

                DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("Student Connection Request Recipients").child(mFirebaseUser.getUid()).child(parentSchoolConnectionRequest.getRequestKey());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String time = Date.getDate();
                            String sorttableTime = Date.convertToSortableDate(time);

                            Map<String, Object> newConnectionMap = new HashMap<String, Object>();
                            DatabaseReference newConnectionRef = mFirebaseDatabase.getReference();
                            String requestKey = dataSnapshot.getKey();
                            ParentSchoolConnectionRequest parentSchoolConnectionRequest = dataSnapshot.getValue(ParentSchoolConnectionRequest.class);
                            NotificationModel notification = new NotificationModel(mFirebaseUser.getUid(), parentSchoolConnectionRequest.getRequestSenderID(), parentSchoolConnectionRequest.getRequestSenderAccountType(), "Parent", time, sorttableTime, requestKey, "ConnectionRequestDeclined", parentSchoolConnectionRequest.getStudentProfilePictureURL(), parentSchoolConnectionRequest.getStudentID(), false);

                            if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                newConnectionMap.put("NotificationParent/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey, notification);
                            } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                newConnectionMap.put("NotificationSchool/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey, notification);
                            }

                            ArrayList<String> recipients = parentSchoolConnectionRequest.getRequestReciepients();
                            newConnectionMap.put("Student Connection Request Sender/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey + "/requestStatus", "Declined");
                            newConnectionMap.put("Student Connection Request Sender/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey + "/requestResponder", mFirebaseUser.getUid());
                            newConnectionMap.put("Student Connection Request Sender/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey + "/requestResponse", "Declined");

                            if (recipients == null) return;
                            if (recipients.size() == 0) return;

                            for (int i = 0; i < recipients.size(); i++) {
                                String recipientID = recipients.get(i).split(" ")[0];

                                newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestStatus", "Declined");
                                newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestResponder", mFirebaseUser.getUid());
                                newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestResponse", "Declined");
                                newConnectionMap.put("NotificationSchool/" + recipientID + "/" + requestKey, null);
                                newConnectionMap.put("NotificationParent/" + recipientID + "/" + requestKey, null);
                            }

                            newConnectionRef.updateChildren(newConnectionMap);
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

    @Override
    public int getItemCount() {
        return parentSchoolConnectionRequestList.size();
    }
}
