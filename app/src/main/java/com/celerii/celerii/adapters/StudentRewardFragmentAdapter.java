package com.celerii.celerii.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.Activities.StudentBehaviouralPerformance.AddYourRewardActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.BehaviouralRecordModel;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.ClassesStudentsAndParentsModel;
import com.celerii.celerii.models.NotificationModel;
import com.celerii.celerii.models.TeacherRewardModel;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DELL on 5/5/2019.
 */

public class StudentRewardFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TeacherRewardModel> teacherRewardModelList;
    private HashMap<String, ArrayList<String>> studentParentList = new HashMap<String, ArrayList<String>>();
    private String rewardType;
    String studentID;
    String studentName;
    String studentPicURL;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    SharedPreferencesManager sharedPreferencesManager;

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView reward;
        public ImageView pointPic;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            pointPic = (ImageView) view.findViewById(R.id.pointpic);
            reward = (TextView) view.findViewById(R.id.reward);
            clickableView = view;
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        Button addNewReward;

        public FooterViewHolder(View view) {
            super(view);
            addNewReward = (Button) view.findViewById(R.id.addnewreward);
        }
    }

    public StudentRewardFragmentAdapter(List<TeacherRewardModel> teacherRewardModelList, String rewardType, String studentID, String studentName, String studentPicURL, Context context) {
        this.teacherRewardModelList = teacherRewardModelList;
        this.rewardType = rewardType;
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentPicURL = studentPicURL;
        this.context = context;

        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        Gson gson = new Gson();
        ArrayList<ClassesStudentsAndParentsModel> classesStudentsAndParentsModelList = new ArrayList<>();
        String myClassesStudentsParentsJSON = sharedPreferencesManager.getClassesStudentParent();
        Type type = new TypeToken<ArrayList<ClassesStudentsAndParentsModel>>() {}.getType();
        classesStudentsAndParentsModelList = gson.fromJson(myClassesStudentsParentsJSON, type);

        if (classesStudentsAndParentsModelList == null) {

        } else {
            studentParentList.clear();
            for (ClassesStudentsAndParentsModel classesStudentsAndParentsModel: classesStudentsAndParentsModelList) {
//                String studentID = classesStudentsAndParentsModel.getStudentID();
                String parentID = classesStudentsAndParentsModel.getParentID();

                try {
                    if (!studentParentList.get(studentID).contains(parentID)) {
                        studentParentList.get(studentID).add(parentID);
                    }
                } catch (Exception e) {
                    studentParentList.put(studentID, new ArrayList<String>());
                    studentParentList.get(studentID).add(parentID);
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_reward_fragment_row, parent, false);
                return new StudentRewardFragmentAdapter.MyViewHolder(rowView);
            case Footer:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_reward_fragment_footer, parent, false);
                return new StudentRewardFragmentAdapter.FooterViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_reward_fragment_row, parent, false);
                return new StudentRewardFragmentAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            final TeacherRewardModel teacherRewardModel = teacherRewardModelList.get(position);

            ((MyViewHolder) holder).reward.setText(teacherRewardModel.getReward());

            TextDrawable textDrawable;
            if (rewardType.equals("Reward")) {
                textDrawable = TextDrawable.builder().buildRound(teacherRewardModel.getPoint(), context.getResources().getColor(R.color.colorPrimaryPurple));
            } else {
                textDrawable = TextDrawable.builder().buildRound(teacherRewardModel.getPoint(), context.getResources().getColor(R.color.colorAccent));
            }
            ((MyViewHolder) holder).pointPic.setImageDrawable(textDrawable);

            ((MyViewHolder) holder).clickableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
                        showDialogWithMessage("Internet is down, check your connection and try again");
                        return;
                    }

                    String date = Date.getDate();
                    String sortableDate = Date.convertToSortableDate(date);
                    Gson gson = new Gson();
                    Type type = new TypeToken<Class>() {}.getType();
                    Class activeClassModel = gson.fromJson(sharedPreferencesManager.getActiveClass(), type);
                    String activeClass = activeClassModel.getID();
                    String year = Date.getYear();
                    String term = Term.getTermShort();
                    String term_year = term + "_" + year;
                    String year_term = year + "_" + term;
                    String class_term_year = activeClass + "_" + term + "_" + year;
                    String class_year_term = activeClass + "_" + year + "_" + term;
                    Boolean isNew = true;

                    mDatabaseReference = mFirebaseDatabase.getReference().child("BehaviouralRecord").child("BehaviouralRecord").push();
                    String pushKey = mDatabaseReference.getKey();
                    Map<String, Object> updater = new HashMap<String, Object>();
                    mDatabaseReference = mFirebaseDatabase.getReference();

                    BehaviouralRecordModel behaviouralRecordModel;

                    final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(context);
                    progressDialog.show();

                    if (rewardType.equals("Reward")) {
                        String point = "1";
                        behaviouralRecordModel = new BehaviouralRecordModel(activeClass, mFirebaseUser.getUid(), studentID, pushKey, term, year, date,
                                sortableDate, year_term, term_year, class_year_term, class_term_year, point, rewardType, teacherRewardModel.getReward(), isNew);
                        updater.put("BehaviouralRecord/BehaviouralRecord/Reward/" + pushKey, behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordStudent/" + studentID + "/Reward/" + pushKey + "/", behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordTeacher/" + mFirebaseUser.getUid() + "/Reward/" + pushKey + "/", behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordClass/" + activeClass + "/Reward/" + pushKey + "/", behaviouralRecordModel);

                    } else {
                        String point = "-1";
                        behaviouralRecordModel = new BehaviouralRecordModel(activeClass, mFirebaseUser.getUid(), studentID, pushKey, term, year, date,
                                sortableDate, year_term, term_year, class_year_term, class_term_year, point, rewardType, teacherRewardModel.getReward(), isNew);
                        updater.put("BehaviouralRecord/BehaviouralRecord/" + pushKey, behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordStudent/" + studentID + "/Punishment/" + pushKey + "/", behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordTeacher/" + mFirebaseUser.getUid() + "/Punishment/" + pushKey + "/", behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordClass/" + activeClass + "/Punishment/" + pushKey + "/", behaviouralRecordModel);

                    }

                    ArrayList<String> parentIDList = studentParentList.get(studentID);
                    if (parentIDList != null) {
                        for (int j = 0; j < parentIDList.size(); j++) {
                            String parentID = parentIDList.get(j);
                            NotificationModel notificationModel = new NotificationModel(auth.getCurrentUser().getUid(), parentID,
                                    "Parent", sharedPreferencesManager.getActiveAccount(), date, sortableDate, pushKey,
                                    "NewBehaviouralPost", studentPicURL, studentID, studentName, false);
                            updater.put("BehaviouralRecord/BehaviouralRecordParentNotification/" + parentID + "/" + studentID + "/status", true);
                            updater.put("BehaviouralRecord/BehaviouralRecordParentNotification/" + parentID + "/" + studentID + "/" + pushKey + "/status", true);
                            updater.put("BehaviouralRecord/BehaviouralRecordParentRecipients/" + pushKey + "/" + parentID, true);
                            updater.put("NotificationParent/" + parentID + "/" + pushKey, notificationModel);
                            updater.put("Notification Badges/Parents/" + parentID + "/Notifications/status", true);
                            updater.put("Notification Badges/Parents/" + parentID + "/More/status", true);
                        }
                    }

                    mDatabaseReference.updateChildren(updater, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                progressDialog.dismiss();
                                showDialogWithMessageAndClose("Your behavioural report has been added for " + studentName);
                            } else {
                                showDialogWithMessage("Your behavioural report for " + studentName + " could not be added");
                            }
                        }
                    });
                }
            });

        } else if (holder instanceof FooterViewHolder){

            if (rewardType.equals("Reward")) {
                ((FooterViewHolder) holder).addNewReward.setText("Add New Reward");
                ((FooterViewHolder) holder).addNewReward.setTextColor(context.getResources().getColor(R.color.colorPrimaryPurple));
                ((FooterViewHolder) holder).addNewReward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("Tag", "Reward");
                        bundle.putString("Target", "Student");
                        Intent intent = new Intent(context, AddYourRewardActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            } else {
                ((FooterViewHolder) holder).addNewReward.setText("Add New Punishment");
                ((FooterViewHolder) holder).addNewReward.setTextColor(context.getResources().getColor(R.color.colorAccent));
                ((FooterViewHolder) holder).addNewReward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("Tag", "Punishment");
                        bundle.putString("Target", "Student");
                        Intent intent = new Intent(context, AddYourRewardActivity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return teacherRewardModelList.size();
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
        return position == teacherRewardModelList.size () - 1;
    }

    void showDialogWithMessageAndClose (String messageString) {
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
                ((Activity)context).finish();
            }
        });
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
