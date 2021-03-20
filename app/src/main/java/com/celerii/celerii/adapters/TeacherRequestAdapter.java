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
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.TeacherSchoolConnectionRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class TeacherRequestAdapter extends RecyclerView.Adapter<TeacherRequestAdapter.MyViewHolder> {
    private List<TeacherSchoolConnectionRequest> teacherSchoolConnectionRequestList;
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

    public TeacherRequestAdapter(List<TeacherSchoolConnectionRequest> teacherSchoolConnectionRequestList, Context context) {
        this.teacherSchoolConnectionRequestList = teacherSchoolConnectionRequestList;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        customProgressDialogOne = new CustomProgressDialogOne(context);
    }

    @Override
    public TeacherRequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parent_request_row, parent, false);
        return new TeacherRequestAdapter.MyViewHolder(itemView);
    }

    public void onBindViewHolder(TeacherRequestAdapter.MyViewHolder holder, int position) {
        final TeacherSchoolConnectionRequest teacherSchoolConnectionRequest = teacherSchoolConnectionRequestList.get(position);
        String time = Date.getRelativeTimeSpan(teacherSchoolConnectionRequest.getTimeSent());
        String notification;

        holder.connectionRequest.setVisibility(View.INVISIBLE);
        if (teacherSchoolConnectionRequest.getSender().equals(mFirebaseUser.getUid())) {
            notification = "Your request to connect to " + "<b>" + teacherSchoolConnectionRequest.getSchoolName() + "</b>" + " hasn't been responded to yet.";
        } else {
            notification = "<b>" + teacherSchoolConnectionRequest.getSchoolName() + "</b>" + " has requested to connect to your account.";
            holder.connectionRequest.setVisibility(View.VISIBLE);
        }

        holder.notification.setText(Html.fromHtml(notification));
        holder.time.setText(time);
        holder.profilePictureClipper.setClipToOutline(true);

        Drawable textDrawable;
        if (!teacherSchoolConnectionRequest.getSchoolName().isEmpty()) {
            String[] nameArray = teacherSchoolConnectionRequest.getSchoolName().replaceAll("\\s+", " ").split(" ");
            if (nameArray.length == 1) {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0]);
            } else {
                textDrawable = CreateTextDrawable.createTextDrawable(context, nameArray[0], nameArray[1]);
            }
            holder.pic.setImageDrawable(textDrawable);
        } else {
            textDrawable = CreateTextDrawable.createTextDrawable(context, "NA");
        }

        if (!teacherSchoolConnectionRequest.getSchoolProfilePictureURL().isEmpty()) {
            Glide.with(context)
                    .load(teacherSchoolConnectionRequest.getSchoolProfilePictureURL())
                    .placeholder(textDrawable)
                    .error(textDrawable)
                    .centerCrop()
                    .into(holder.pic);
        }

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialogOne.show();

                String time = Date.getDate();
                String sortableTime = Date.convertToSortableDate(time);
                final String entityId = teacherSchoolConnectionRequest.getSchool();

                final String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(entityId).push().getKey();
                final NotificationModel notification = new NotificationModel(mFirebaseUser.getUid(), entityId, "School", "Teacher", time, sortableTime, notificationPushID, "Connection", "", "", false);

                mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(entityId).child(teacherSchoolConnectionRequest.getKey());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> newConnectionMap = new HashMap<String, Object>();
                        DatabaseReference newRef = mFirebaseDatabase.getReference();
                        if (dataSnapshot.exists()) {
                            String pendingRequestKey = dataSnapshot.getKey();
                            newConnectionMap.put("School To Teacher Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + pendingRequestKey + "/" + "status", "Accepted");
                            newConnectionMap.put("School To Teacher Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Accepted");
                            newConnectionMap.put("NotificationTeacher/" + mFirebaseUser.getUid() + "/" + pendingRequestKey, null);
                        }
                        newConnectionMap.put("School Teacher/" + entityId + "/" + mFirebaseUser.getUid(), true);
                        newConnectionMap.put("Teacher School/" + mFirebaseUser.getUid() + "/" + entityId, true);
                        newConnectionMap.put("NotificationSchool/" + entityId + "/" + notificationPushID, notification);
                        newRef.updateChildren(newConnectionMap);
                        customProgressDialogOne.dismiss();
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

                String time = Date.getDate();
                String sortableTime = Date.convertToSortableDate(time);
                final String entityId = teacherSchoolConnectionRequest.getSchool();

                final String notificationPushID = mFirebaseDatabase.getReference().child("NotificationSchool").child(entityId).push().getKey();
                final NotificationModel notification = new NotificationModel(mFirebaseUser.getUid(), entityId, "School", "Teacher", time, sortableTime, notificationPushID, "ConnectionRequestDeclined", "", "", false);

                mDatabaseReference = mFirebaseDatabase.getReference("School To Teacher Request Teacher").child(mFirebaseUser.getUid()).child(entityId).child(teacherSchoolConnectionRequest.getKey());
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> newDeclinedMap = new HashMap<String, Object>();
                        DatabaseReference newRef = mFirebaseDatabase.getReference();
                        if (dataSnapshot.exists()) {
                            String pendingRequestKey = dataSnapshot.getKey();
                            newDeclinedMap.put("School To Teacher Request Teacher/" + mFirebaseUser.getUid() + "/" + entityId + "/" + pendingRequestKey + "/" + "status", "Declined");
                            newDeclinedMap.put("School To Teacher Request School/" + entityId + "/" + mFirebaseUser.getUid() + "/" + pendingRequestKey + "/" + "status", "Declined");
                            newDeclinedMap.put("NotificationTeacher/" + mFirebaseUser.getUid() + "/" + pendingRequestKey, null);
                        }
                        newDeclinedMap.put("NotificationSchool/" + entityId + "/" + notificationPushID, notification);
                        newRef.updateChildren(newDeclinedMap);

                        customProgressDialogOne.dismiss();
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
        return teacherSchoolConnectionRequestList.size();
    }
}
