package com.celerii.celerii.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.celerii.celerii.Activities.Comment.CommentStoryActivity;
import com.celerii.celerii.Activities.Events.EventDetailActivity;
import com.celerii.celerii.Activities.Home.NotificationDetailActivity;
import com.celerii.celerii.Activities.Home.Parent.ParentsRequestActivity;
import com.celerii.celerii.Activities.Home.Teacher.TeacherRequestActivity;
import com.celerii.celerii.Activities.Profiles.ParentProfileActivity;
import com.celerii.celerii.Activities.Profiles.SchoolProfile.SchoolProfileActivity;
import com.celerii.celerii.Activities.StudentAttendance.ParentAttendanceActivity;
import com.celerii.celerii.Activities.StudentBehaviouralPerformance.BehaviouralResultActivity;
import com.celerii.celerii.Activities.StudentPerformance.StudentPerformanceForParentsActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CreateTextDrawable;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.models.NotificationModel;
import com.bumptech.glide.Glide;
import com.celerii.celerii.models.ParentSchoolConnectionRequest;
import com.celerii.celerii.models.Student;
import com.celerii.celerii.models.Teacher;
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
 * Created by user on 7/3/2017.
 */

public class ClassNotificationAdapter extends RecyclerView.Adapter<ClassNotificationAdapter.MyViewHolder>{
    private SharedPreferencesManager sharedPreferencesManager;
    private List<NotificationModel> notificationModelList;
    private Context context;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    CustomProgressDialogOne customProgressDialogOne;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView notification, time, accept, decline;
        public ImageView pic, notificationType;
        public LinearLayout connectionRequest, profilePictureClipper;
        public LinearLayout notificationLayout;
        public View view;

        public MyViewHolder(final View view) {
            super(view);
            notification = (TextView) view.findViewById(R.id.notification);
            pic = (ImageView) view.findViewById(R.id.pic);
            notificationType = (ImageView) view.findViewById(R.id.notificationtype);
            time = (TextView) view.findViewById(R.id.time);
            connectionRequest = (LinearLayout) view.findViewById(R.id.connectionrequest);
            profilePictureClipper = (LinearLayout) view.findViewById(R.id.profilepictureclipper);
            notificationLayout = (LinearLayout) view.findViewById(R.id.notificationlayout);
            accept = (TextView) view.findViewById(R.id.accept);
            decline = (TextView) view.findViewById(R.id.decline);
            this.view = view;
        }
    }

    public ClassNotificationAdapter(List<NotificationModel> notificationModelList, Context context) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        this.notificationModelList = notificationModelList;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        customProgressDialogOne = new CustomProgressDialogOne(context);
    }

    @Override
    public ClassNotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_story_notification, parent, false);
        return new ClassNotificationAdapter.MyViewHolder(itemView);
    }

    public void onBindViewHolder(ClassNotificationAdapter.MyViewHolder holder, int position) {
        final NotificationModel notificationModel = notificationModelList.get(position);
        final String notificationType = notificationModel.getNotificationType();
        String notificationSubject = notificationModel.getFromName();
        String time = Date.getRelativeTimeSpan(notificationModel.getTime());
        String notification;

        holder.connectionRequest.setVisibility(View.GONE);
        if (notificationType.equals("ClassPost")){
            notification = "<b>" + notificationSubject + "</b>" + " created a new class post for " + "<b>" + notificationModel.getObject() + "</b>";
        } else if (notificationType.equals("AssignmentPost")){
            notification = "<b>" + notificationSubject + "</b>" + " posted a new class assignment for " + "<b>" + notificationModel.getObject() + "</b>";
        } else if (notificationType.equals("Like")){
            notification = "<b>" + notificationSubject + "</b>" + " liked your class post for " + "<b>" + notificationModel.getObject() + "</b>";
        } else if (notificationType.equals("Comment")){
            notification = "<b>" + notificationSubject + "</b>" + " commented on your class post for " + "<b>" + notificationModel.getObject() + "</b>";
        } else if (notificationType.equals("Event")){
            notification = "<b>" + notificationSubject + "</b>" + " created a new school event for you.";
        } else if (notificationType.equals("Newsletter")){
            notification = "<b>" + notificationSubject + "</b>" + " published a new school newsletter for you.";
        } else if (notificationType.equals("ConnectionRequest")){ //TODO: Remove
            if (notificationModel.getToAccountType().equals("Parent")) {
                notification = "<b>" + notificationSubject + "</b>" + " has requested to connect to " + "<b>" + notificationModel.getObjectName() + "</b>" + "'s account";
            } else {
                notification = "<b>" + notificationSubject + "</b>" + " has requested to connect to your account";
            }
//            holder.connectionRequest.setVisibility(View.VISIBLE);
        } else if (notificationType.equals("ConnectionRequestDeclined")){
            if (notificationModel.getToAccountType().equals("Parent")) {
                notification = "Your request to connect to " + "<b>" + notificationModel.getObjectName() + "</b>" + "'s account has been declined by " + "<b>" + notificationSubject + "</b>";
            } else {
                notification = "Your request to connect to " + "<b>" + notificationSubject + "</b>" + " has been declined by them";
            }
        } else if (notificationType.equals("Disconnection")){
            if (notificationModel.getToAccountType().equals("Parent")) {
                if (notificationModel.getFromID().equals(mFirebaseUser.getUid())) {
                    notification = "You have disconnected from " + "<b>" + notificationModel.getObjectName() + "</b>" + "'s account";
                } else {
                    notification = "<b>" + notificationSubject + "</b>" + " has disconnected from " + "<b>" + notificationModel.getObjectName() + "</b>" + "'s account";
                }
            } else {
                notification = "<b>" + notificationSubject + "</b>" + " has disconnected from your account";
            }
        } else if (notificationType.equals("Connection")){
            if (notificationModel.getToAccountType().equals("Parent")) {
                if (notificationModel.getFromID().equals(mFirebaseUser.getUid())) {
                    notification = "You have connected to " + "<b>" + notificationModel.getObjectName() + "</b>" + "'s account";
                } else {
                    notification = "<b>" + notificationSubject + "</b>" + " has connected to " + "<b>" + notificationModel.getObjectName() + "</b>" + "'s account";
                }
            } else {
                notification = "<b>" + notificationSubject + "</b>" + " has connected to your account";
            }
        } else if (notificationType.equals("NewResultPost")) {
            notification = "<b>" + notificationSubject + "</b>" + " has posted a new academic result for " + "<b>" + notificationModel.getObjectName() + "</b>";
        } else if (notificationType.equals("NewBehaviouralPost")){
            notification = "<b>" + notificationSubject + "</b>" + " has posted a new behavioural result for " + "<b>" + notificationModel.getObjectName() + "</b>";
        } else if (notificationType.equals("NewAttendancePost")){
            notification = "<b>" + notificationSubject + "</b>" + " has posted a new attendance record for " + "<b>" + notificationModel.getObjectName() + "</b>";
        } else {
            notification = "";
        }

        holder.notification.setText(Html.fromHtml(notification));
        holder.time.setText(time);
        holder.profilePictureClipper.setClipToOutline(true);

        if (!notificationModel.getNotificationImageURL().isEmpty()) {
            holder.notificationType.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(notificationModel.getNotificationImageURL())
                    .centerCrop()
                    .into(holder.notificationType);
        } else {
            holder.notificationType.setVisibility(View.INVISIBLE);
        }

        String picName = notificationSubject;
        String picPictureURL = notificationModel.getFromProfilePicture();

        if (notificationModel.getToAccountType().equals("Parent")) {
            if (notificationType.equals("ConnectionRequest") || notificationType.equals("ConnectionRequestDeclined") || notificationType.equals("Disconnection") ||
                    notificationType.equals("Connection") || notificationType.equals("NewResultPost") || notificationType.equals("NewBehaviouralPost") ||
                    notificationType.equals("NewAttendancePost")) {
                picName = notificationModel.getObjectName();
                picPictureURL = notificationModel.getNotificationImageURL();
            }
        }

        Drawable textDrawable;
        if (!picName.isEmpty()) {
            String[] nameArray = picName.replaceAll("\\s+", " ").trim().split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], 50);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1], 50);
            }
            holder.pic.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA", 50);
        }

        if (!picPictureURL.isEmpty()) {
            Glide.with(context)
                    .load(picPictureURL)
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.pic);
        }

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customProgressDialogOne.show();

                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("Student Connection Request Recipients").child(mFirebaseUser.getUid()).child(notificationModel.getActivityID());
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

                                if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                    newConnectionMap.put("Student Parent/" + parentSchoolConnectionRequest.getStudentID() + "/" + parentSchoolConnectionRequest.getRequestSenderID(), true);
                                    newConnectionMap.put("Parents Students/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + parentSchoolConnectionRequest.getStudentID(), true);
                                } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                    newConnectionMap.put("Student School/" + parentSchoolConnectionRequest.getStudentID() + "/" + parentSchoolConnectionRequest.getRequestSenderID(), true);
                                    newConnectionMap.put("School Students/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + parentSchoolConnectionRequest.getStudentID(), true);
                                }

                                if (recipients == null) return;
                                if (recipients.size() == 0) return;

                                for (int i = 0; i < recipients.size(); i++) {
                                    String recipientID = recipients.get(i).split(" ")[0];
                                    String recipientAccountType = recipients.get(i).split(" ")[1];

                                    NotificationModel notification;
                                    newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestStatus", "Accepted");
                                    newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestResponder", mFirebaseUser.getUid());
                                    newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestResponse", "Accepted");
                                    newConnectionMap.put("NotificationSchool/" + recipientID + "/" + requestKey, null);
                                    newConnectionMap.put("NotificationParent/" + recipientID + "/" + requestKey, null);
                                    newConnectionMap.put("NotificationSchool/" + mFirebaseUser.getUid() + "/" + requestKey, null);
                                    newConnectionMap.put("NotificationParent/" + mFirebaseUser.getUid() + "/" + requestKey, null);

                                    if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                        notification = new NotificationModel(parentSchoolConnectionRequest.getRequestSenderID(), recipientID, "Parent", sharedPreferencesManager.getActiveAccount(), time, sorttableTime, requestKey, "Connection", notificationModel.getNotificationImageURL(), notificationModel.getObject(), false);
                                        newConnectionMap.put("NotificationParent/" + recipientID + "/" + requestKey, notification);
                                    } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                        notification = new NotificationModel(parentSchoolConnectionRequest.getRequestSenderID(), recipientID, "School", sharedPreferencesManager.getActiveAccount(), time, sorttableTime, requestKey, "Connection", notificationModel.getNotificationImageURL(), notificationModel.getObject(), false);
                                        newConnectionMap.put("NotificationSchool/" + recipientID + "/" + requestKey, notification);
                                    }
                                }

//                                newConnectionRef.updateChildren(newConnectionMap);
                                customProgressDialogOne.dismiss();
                            } else {

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                    String time = Date.getDate();
                    String sorttableTime = Date.convertToSortableDate(time);
                    final String entityId = notificationModel.getFromID();

                    final String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(entityId).push().getKey();
                    final NotificationModel notification = new NotificationModel(mFirebaseUser.getUid(), entityId, "School", "Teacher", time, sorttableTime, notificationPushID, "Connection", "", "", false);


                    mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(entityId).child(notificationModel.getActivityID());
                    mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Map<String, Object> newConnectionMap = new HashMap<String, Object>();
                                DatabaseReference newRef = mFirebaseDatabase.getReference();
                                String pendingRequestKey = dataSnapshot.getKey();
                                newConnectionMap.put("School To Teacher Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + pendingRequestKey + "/" + "status", "Accepted");
                                newConnectionMap.put("School To Teacher Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Accepted");
                                newConnectionMap.put("School Teacher/" + entityId + "/" + mFirebaseUser.getUid(), true);
                                newConnectionMap.put("Teacher School/" + mFirebaseUser.getUid() + "/" + entityId, true);
                                newConnectionMap.put("NotificationTeacher/" + mFirebaseUser.getUid() + "/" + pendingRequestKey, null);
                                newConnectionMap.put("NotificationSchool/" + entityId + "/" + notificationPushID, notification);
                                newRef.updateChildren(newConnectionMap);
                            }

                            customProgressDialogOne.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customProgressDialogOne.show();

                if (sharedPreferencesManager.getActiveAccount().equals("Parent")) {
                    DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("Student Connection Request Recipients").child(mFirebaseUser.getUid()).child(notificationModel.getActivityID());
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

                                if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {
                                    NotificationModel notification = new NotificationModel(mFirebaseUser.getUid(), parentSchoolConnectionRequest.getRequestSenderID(), "Parent", "Parent", time, sorttableTime, requestKey, "ConnectionRequestDeclined", notificationModel.getNotificationImageURL(), notificationModel.getObject(), false);
                                    newConnectionMap.put("NotificationParent/" + parentSchoolConnectionRequest.getRequestSenderID() + "/" + requestKey, notification);
                                } else if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("School")) {
                                    NotificationModel notification = new NotificationModel(parentSchoolConnectionRequest.getRequestSenderID(), parentSchoolConnectionRequest.getRequestSenderID(), "School", "Parent", time, sorttableTime, requestKey, "ConnectionRequestDeclined", notificationModel.getNotificationImageURL(), notificationModel.getObject(), false);
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
                                    String recipientAccountType = recipients.get(i).split(" ")[1];

                                    newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestStatus", "Declined");
                                    newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestResponder", mFirebaseUser.getUid());
                                    newConnectionMap.put("Student Connection Request Recipients/" + recipientID + "/" + requestKey + "/requestResponse", "Declined");
                                    newConnectionMap.put("NotificationSchool/" + recipientID + "/" + requestKey, null);
                                    newConnectionMap.put("NotificationParent/" + recipientID + "/" + requestKey, null);

                                    if (parentSchoolConnectionRequest.getRequestSenderAccountType().equals("Parent")) {

                                    }
                                }

//                                newConnectionRef.updateChildren(newConnectionMap);
//                                customProgressDialogOne.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                    String time = Date.getDate();
                    String sorttableTime = Date.convertToSortableDate(time);
                    final String entityId = notificationModel.getFromID();

                    final String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(entityId).push().getKey();
                    final NotificationModel notification = new NotificationModel(mFirebaseUser.getUid(), entityId, "School", "Teacher", time, sorttableTime, notificationPushID, "ConnectionRequestDeclined", "", "", false);


                    mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(entityId).child(notificationModel.getActivityID());
                    mDatabaseReference.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Map<String, Object> newDeclinedMap = new HashMap<String, Object>();
                                DatabaseReference newRef = mFirebaseDatabase.getReference();
                                String pendingRequestKey = dataSnapshot.getKey();
                                newDeclinedMap.put("School To Teacher Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + pendingRequestKey + "/" + "status", "Declined");
                                newDeclinedMap.put("School To Teacher Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Declined");
                                newDeclinedMap.put("NotificationTeacher/" + mFirebaseUser.getUid() + "/" + pendingRequestKey, null);
                                newDeclinedMap.put("NotificationSchool/" + entityId + "/" + notificationPushID, notification);
                                newRef.updateChildren(newDeclinedMap);
                            }

                            customProgressDialogOne.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        holder.notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationType.equals("ClassPost")){
                    Bundle b = new Bundle();
                    b.putString("postKey", notificationModel.getActivityID());
                    Intent I = new Intent(context, CommentStoryActivity.class);
                    I.putExtras(b);
                    context.startActivity(I);
                } else if (notificationType.equals("Like")){
                    Bundle b = new Bundle();
                    b.putString("postKey", notificationModel.getActivityID());
                    Intent I = new Intent(context, CommentStoryActivity.class);
                    I.putExtras(b);
                    context.startActivity(I);
                } else if (notificationType.equals("Comment")){
                    Bundle b = new Bundle();
                    b.putString("postKey", notificationModel.getActivityID());
                    Intent I = new Intent(context, CommentStoryActivity.class);
                    I.putExtras(b);
                    context.startActivity(I);
                } else if (notificationType.equals("Event")){
                    Bundle b = new Bundle();
                    b.putString("Event ID", notificationModel.getActivityID());
                    b.putString("Color Number", String.valueOf(0));
                    Intent I = new Intent(context, EventDetailActivity.class);
                    I.putExtras(b);
                    context.startActivity(I);
                } else if (notificationType.equals("ConnectionRequest")) {
                    if (notificationModel.getFromAccountType().equals("Parent")) {
                        Intent I = new Intent(context, ParentsRequestActivity.class);
                        context.startActivity(I);
                    } else {
                        Intent I = new Intent(context, TeacherRequestActivity.class);
                        context.startActivity(I);
                    }
                } else if (notificationType.equals("Connection")) {
                    if (notificationModel.getFromAccountType().equals("Parent")) {
                        Intent intent;
                        Bundle b = new Bundle();
                        if (notificationModel.getFromAccountType().equals("Parent")) {
                            intent = new Intent(context, ParentProfileActivity.class);
                            b.putString("parentID", notificationModel.getFromID());
                        } else {
                            intent = new Intent(context, SchoolProfileActivity.class);
                            b.putString("School ID", notificationModel.getFromID());
                        }
                        intent.putExtras(b);
                        context.startActivity(intent);

                    } else if (sharedPreferencesManager.getActiveAccount().equals("Teacher")) {
                        Intent I = new Intent(context, SchoolProfileActivity.class);
                        Bundle b = new Bundle();
                        b.putString("School ID", notificationModel.getFromID());
                        I.putExtras(b);
                        context.startActivity(I);
                    }
                } else if (notificationType.equals("NewResultPost")) {
                    Bundle b = new Bundle();
                    Student student = new Student(notificationModel.getObjectName(), notificationModel.getObject());
                    Gson gson = new Gson();
                    String activeKid = gson.toJson(student);
                    b.putString("Child ID", activeKid);
                    Intent I = new Intent(context, StudentPerformanceForParentsActivity.class);
                    I.putExtras(b);
                    context.startActivity(I);
                } else if (notificationType.equals("NewBehaviouralPost")) {
                    Bundle b = new Bundle();
                    Student student = new Student(notificationModel.getObjectName(), notificationModel.getObject());
                    Gson gson = new Gson();
                    String activeKid = gson.toJson(student);
                    b.putString("ChildID", activeKid);
                    Intent I = new Intent(context, BehaviouralResultActivity.class);
                    I.putExtras(b);
                    context.startActivity(I);
                } else if (notificationType.equals("NewAttendancePost")) {
                    Bundle b = new Bundle();
                    Student student = new Student(notificationModel.getObjectName(), notificationModel.getObject());
                    Gson gson = new Gson();
                    String activeKid = gson.toJson(student);
                    b.putString("Child ID", activeKid);
                    Intent I = new Intent(context, ParentAttendanceActivity.class);
                    I.putExtras(b);
                    context.startActivity(I);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }
}
