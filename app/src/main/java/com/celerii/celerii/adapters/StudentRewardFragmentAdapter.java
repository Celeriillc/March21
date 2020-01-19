package com.celerii.celerii.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.Activities.StudentBehaviouralPerformance.AddYourRewardActivity;
import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.models.BehaviouralRecordModel;
import com.celerii.celerii.models.TeacherRewardModel;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DELL on 5/5/2019.
 */

public class StudentRewardFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TeacherRewardModel> teacherRewardModelList;
    private String rewardType;
    String studentID;
    String studentName;
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

    public StudentRewardFragmentAdapter(List<TeacherRewardModel> teacherRewardModelList, String rewardType, String studentID, String studentName, Context context) {
        this.teacherRewardModelList = teacherRewardModelList;
        this.rewardType = rewardType;
        this.studentID = studentID;
        this.studentName = studentName;
        this.context = context;

        sharedPreferencesManager = new SharedPreferencesManager(context);

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
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
                    String time = Date.getDate();
                    String sortableDate = Date.convertToSortableDate(time);
                    String activeClass = sharedPreferencesManager.getActiveClass().split(" ")[0];
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

                    if (rewardType.equals("Reward")) {
                        String point = "1";
                        behaviouralRecordModel = new BehaviouralRecordModel(activeClass, mFirebaseUser.getUid(), studentID, pushKey, term, year, time,
                                sortableDate, year_term, term_year, class_year_term, class_term_year, point, rewardType, teacherRewardModel.getReward(), isNew);
                        updater.put("BehaviouralRecord/BehaviouralRecord/Reward/" + pushKey, behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordStudent/" + studentID + "/Reward/" + pushKey + "/", behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordTeacher/" + mFirebaseUser.getUid() + "/Reward/" + pushKey + "/", behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordClass/" + activeClass + "/Reward/" + pushKey + "/", behaviouralRecordModel);

                    } else {
                        String point = "-1";
                        behaviouralRecordModel = new BehaviouralRecordModel(activeClass, mFirebaseUser.getUid(), studentID, pushKey, term, year, time,
                                sortableDate, year_term, term_year, class_year_term, class_term_year, point, rewardType, teacherRewardModel.getReward(), isNew);
                        updater.put("BehaviouralRecord/BehaviouralRecord/" + pushKey, behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordStudent/" + studentID + "/Punishment/" + pushKey + "/", behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordTeacher/" + mFirebaseUser.getUid() + "/Punishment/" + pushKey + "/", behaviouralRecordModel);
                        updater.put("BehaviouralRecord/BehaviouralRecordClass/" + activeClass + "/Punishment/" + pushKey + "/", behaviouralRecordModel);

                    }

                    showDialogWithMessage("Your behavioural report has been added for " + studentName);

                    mDatabaseReference.updateChildren(updater);
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

    void showDialogWithMessage (String messageString) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_unary_message_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView message = (TextView) dialog.findViewById(R.id.dialogmessage);
        TextView OK = (TextView) dialog.findViewById(R.id.optionone);
        dialog.show();

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
}
