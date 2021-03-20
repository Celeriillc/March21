package com.celerii.celerii.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.TeacherPerformance.TeacherViewResultDetailWithDeleteActivity;
import com.celerii.celerii.helperClasses.CheckNetworkConnectivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.FirebaseErrorMessages;
import com.celerii.celerii.helperClasses.SharedPreferencesManager;
import com.celerii.celerii.helperClasses.ShowDialogWithMessage;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecord;
import com.celerii.celerii.models.AcademicRecordTeacher;
import com.celerii.celerii.models.Class;
import com.celerii.celerii.models.KidScoreForTeachersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DELL on 1/20/2019.
 */

public class TeacherAcademicRecordDetailAdapter extends RecyclerView.Adapter<TeacherAcademicRecordDetailAdapter.MyViewHolder>{

    SharedPreferencesManager sharedPreferencesManager;
    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    boolean connected;

    private List<AcademicRecordTeacher> AcademicRecordTeacherList;
    private ArrayList<KidScoreForTeachersModel> kidScoreForTeachersModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView examType, score, className, date, maxObtainable, term, year, viewDetails, delete, resultAsAt;
        public ImageView examTypeIcon;
        public View clickableView;

        public MyViewHolder(final View view) {
            super(view);
            examType = (TextView) view.findViewById(R.id.examtype);
            score = (TextView) view.findViewById(R.id.averagescore);
            className = (TextView) view.findViewById(R.id.classname);
            date = (TextView) view.findViewById(R.id.date);
            maxObtainable = (TextView) view.findViewById(R.id.maxobtainable);
            term = (TextView) view.findViewById(R.id.term);
            year = (TextView) view.findViewById(R.id.year);
            resultAsAt = (TextView) view.findViewById(R.id.resultasat);
            examTypeIcon = (ImageView) view.findViewById(R.id.examtypeicon);
            viewDetails = (TextView) view.findViewById(R.id.viewdetails);
            delete = (TextView) view.findViewById(R.id.delete);
            clickableView = view;
        }
    }

    public TeacherAcademicRecordDetailAdapter(List<AcademicRecordTeacher> AcademicRecordTeacherList, Context context) {
        this.AcademicRecordTeacherList = AcademicRecordTeacherList;
        this.context = context;
        sharedPreferencesManager = new SharedPreferencesManager(context);
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_academic_record_detail, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AcademicRecordTeacher academicRecordTeacher = AcademicRecordTeacherList.get(position);

        holder.examType.setText(academicRecordTeacher.getTestType());
        holder.score.setText(TypeConverterClass.convStringToIntString(academicRecordTeacher.getClassAverage()));
        holder.className.setText(academicRecordTeacher.getClassName());
        holder.date.setText(Date.DateFormatMMDDYYYY(academicRecordTeacher.getDate()));
        holder.maxObtainable.setText(TypeConverterClass.convStringToIntString(academicRecordTeacher.getMaxObtainable()));
        holder.term.setText(Term.Term(academicRecordTeacher.getTerm()));
        holder.year.setText(academicRecordTeacher.getAcademicYear());

        if (academicRecordTeacher.getTestType().equals("Continuous Assessment")) {
            holder.examTypeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_accent));
            holder.resultAsAt.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else if (academicRecordTeacher.getTestType().equals("Examination")) {
            holder.examTypeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_primary_purple));
            holder.resultAsAt.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryPurple));
        } else {
            holder.examTypeIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_button_kilogarm_orange));
            holder.resultAsAt.setTextColor(ContextCompat.getColor(context, R.color.colorKilogarmOrange));
        }

        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TeacherViewResultDetailWithDeleteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("subject_year_term", academicRecordTeacher.getSubject_AcademicYear_Term());
                bundle.putString("RecordID", academicRecordTeacher.getRecordKey());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog_layout_delete_academic_record_teacher);
                Button delete = (Button) dialog.findViewById(R.id.delete);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                try {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                } catch (Exception e) {
                    return;
                }

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteNewAcademicRecord(academicRecordTeacher);
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return AcademicRecordTeacherList.size();
    }

    int numberOfRemainingResults = 0;
    private void deleteNewAcademicRecord(final AcademicRecordTeacher academicRecordTeacher) {
        if (!CheckNetworkConnectivity.isNetworkAvailable(context)) {
            showDialogWithMessage("Internet is down, check your connection and try again");
            return;
        }

        final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(context);
        progressDialog.show();
        final String subject_year_term = academicRecordTeacher.getSubject_AcademicYear_Term();
        final String key = academicRecordTeacher.getRecordKey();
        final String teacherID = academicRecordTeacher.getTeacherID();
        final String classID = academicRecordTeacher.getClassID();
        final String className = academicRecordTeacher.getClassName();
        final String schoolID = academicRecordTeacher.getSchoolID();

        final HashMap<String, Object> deleteMap = new HashMap<>();
        final ArrayList<String> parentsList = new ArrayList<>();
        final ArrayList<String> studentsList = new ArrayList<>();

        Gson gson = new Gson();
        ArrayList<Class> myClasses = new ArrayList<>();
        String myClassesJSON = sharedPreferencesManager.getMyClasses();
        Type type = new TypeToken<ArrayList<Class>>() {}.getType();
        myClasses = gson.fromJson(myClassesJSON, type);

        if (myClasses != null) {
            boolean hasClass = false;
            for (Class classInstance: myClasses) {
                if (classInstance.getID().equals(classID)) {
                    hasClass = true;
                    break;
                }
            }

            if (hasClass) {
                mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordTeacher").child(teacherID).child(subject_year_term);
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        numberOfRemainingResults = 0;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if (!postSnapshot.getKey().equals(key)) {
                                    numberOfRemainingResults++;
                                }
                            }
                        }

                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordParentRecipients").child(key);
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        parentsList.add(postSnapshot.getKey());
                                    }
                                }

                                mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordTeacher-Student").child(teacherID).child(subject_year_term).child(key).child("Students");
                                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                                studentsList.add(postSnapshot.getKey());
                                            }
                                        }

                                        deleteMap.put("AcademicRecordClass/" + classID + "/" + subject_year_term + "/" + key, null);
                                        deleteMap.put("AcademicRecordTeacher/" +  teacherID + "/" + subject_year_term + "/" + key, null);

                                        for (String parent: parentsList) {
                                            deleteMap.put("AcademicRecordParentRecipients/" + key + "/" + parent, null);
                                            deleteMap.put("NotificationParent/" + parent + "/" + key, null);
                                        }

                                        for (String student: studentsList) {
                                            deleteMap.put("AcademicRecordStudent/" + student + "/" + subject_year_term + "/" + key, null);
                                            deleteMap.put("AcademicRecordClass-Student/" + classID + "/" + subject_year_term + "/" + key, null);
                                            deleteMap.put("AcademicRecordTeacher-Student/" + teacherID + "/" + subject_year_term + "/" + key, null);
                                        }

                                        DatabaseReference updateRef = mFirebaseDatabase.getReference();
                                        updateRef.updateChildren(deleteMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError == null) {
                                                    if (numberOfRemainingResults == 0) {
                                                        progressDialog.dismiss();
                                                        ((Activity) context).finish();
//                                                AcademicRecordTeacherList.remove(academicRecordTeacher);
//                                                notifyDataSetChanged();
                                                    } else {
                                                        progressDialog.dismiss();
//                                                AcademicRecordTeacherList.remove(academicRecordTeacher);
//                                                notifyDataSetChanged();
                                                    }
                                                } else {
//                                            String message = "This academic record could not be deleted. Ensure you have the permission to delete it";
                                                    progressDialog.dismiss();
                                                    String message = FirebaseErrorMessages.getErrorMessage(databaseError.getCode());
                                                    ShowDialogWithMessage.showDialogWithMessage(context, message);
                                                }
                                            }
                                        });
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                progressDialog.dismiss();
                String message = "You can't delete this record at this time because you're currently not connected to " + "<b>" + className + "</b>" + ".";
                showDialogWithMessage(message);
            }
        } else {
            progressDialog.dismiss();
            String message = "You can't delete this record at this time because you're currently not connected to " + "<b>" + className + "</b>" + ".";
            showDialogWithMessage(message);
        }


    }

    private double newClassAverage = 0.0;
    private double newMaxObtainable = 0.0;
    private double newPercentageOfTotal = 0.0;
    private void deleteAcademicRecord(final AcademicRecordTeacher academicRecordTeacher){
        if ((CheckNetworkConnectivity.isNetworkAvailable(context))) {
            final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(context);
            progressDialog.show();

            kidScoreForTeachersModelList = new ArrayList<>();
            final String recordID = academicRecordTeacher.getRecordKey();
            final Map<String, Object> deleteMap = new HashMap<String, Object>();
            final DatabaseReference updateRef = mFirebaseDatabase.getReference();
            String classId = academicRecordTeacher.getClassID();
            final String subject = academicRecordTeacher.getSubject();
            String year = academicRecordTeacher.getAcademicYear();
            String term = academicRecordTeacher.getTerm();
            final String subject_year_term = subject + "_" + year + "_" + term;
            final String class_subject_year_term = classId + "_" + subject + "_" + year + "_" + term;

            mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecord").child("AcademicRecordTeacher-Student").child(mFirebaseUser.getUid()).child(recordID).child("Students");
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        kidScoreForTeachersModelList.clear();

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            final KidScoreForTeachersModel kidScoreForTeachersModel = new KidScoreForTeachersModel();
                            kidScoreForTeachersModel.setKidScore(postSnapshot.getValue(String.class));
                            kidScoreForTeachersModel.setKidID(postSnapshot.getKey());
                            kidScoreForTeachersModelList.add(kidScoreForTeachersModel);
                        }

                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecord").child("AcademicRecordClass").child(academicRecordTeacher.getClassID());
                        mDatabaseReference.orderByChild("subject_AcademicYear_Term").equalTo(subject_year_term).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int numberOfRemainingResults = 0;
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (!postSnapshot.getKey().equals(recordID)) {
                                            numberOfRemainingResults++;
                                        }
                                    }
                                }

                                if (numberOfRemainingResults == 0) {
                                    for (int i = 0; i < kidScoreForTeachersModelList.size(); i++) {
                                        String kidKey = kidScoreForTeachersModelList.get(i).getKidID();
                                        deleteMap.put("AcademicRecord/AcademicRecordStudent/" + kidKey + "/" + recordID, null);
                                        deleteMap.put("AcademicRecordTotal/AcademicRecordStudent/" + kidKey + "/" + class_subject_year_term, null);
                                    }
                                    deleteMap.put("AcademicRecord/AcademicRecord/" + academicRecordTeacher.getSchoolID() + "/" + recordID, null);
                                    deleteMap.put("AcademicRecord/AcademicRecord-Student/" + academicRecordTeacher.getSchoolID() + "/" + recordID, null);
                                    deleteMap.put("AcademicRecord/AcademicRecordClass/" + academicRecordTeacher.getClassID() + "/" + recordID, null);
                                    deleteMap.put("AcademicRecord/AcademicRecordClass-Student/" + academicRecordTeacher.getClassID() + "/" + recordID, null);

                                    deleteMap.put("AcademicRecordTotal/AcademicRecord/" + academicRecordTeacher.getSchoolID() + "/" + class_subject_year_term, null);
                                    deleteMap.put("AcademicRecordTotal/AcademicRecord-Student/" + academicRecordTeacher.getSchoolID() + "/" + class_subject_year_term, null);
                                    deleteMap.put("AcademicRecordTotal/AcademicRecordClass/" + academicRecordTeacher.getClassID() + "/" + class_subject_year_term, null);
                                    deleteMap.put("AcademicRecordTotal/AcademicRecordClass-Student/" + academicRecordTeacher.getClassID() + "/" + class_subject_year_term, null);

                                    if (academicRecordTeacher.getTeacherID().equals(mFirebaseUser.getUid())) {
                                        deleteMap.put("AcademicRecord/AcademicRecordTeacher/" + academicRecordTeacher.getTeacherID() + "/" + recordID, null);
                                        deleteMap.put("AcademicRecord/AcademicRecordTeacher-Student/" + academicRecordTeacher.getTeacherID() + "/" + recordID, null);
                                        deleteMap.put("AcademicRecordTotal/AcademicRecordTeacher/" + academicRecordTeacher.getTeacherID() + "/" + class_subject_year_term, null);
                                        deleteMap.put("AcademicRecordTotal/AcademicRecordTeacher-Student/" + academicRecordTeacher.getTeacherID() + "/" + class_subject_year_term, null);
                                    }

                                    updateRef.updateChildren(deleteMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            progressDialog.dismiss();
                                            ((Activity) context).finish();
                                        }
                                    });
                                } else {
                                    mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordTotal").child("AcademicRecordClass").child(academicRecordTeacher.getClassID()).child(subject_year_term);
                                    mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                AcademicRecord academicRecord = dataSnapshot.getValue(AcademicRecord.class);
                                                newClassAverage = TypeConverterClass.convStringToDouble(academicRecord.getClassAverage()) - TypeConverterClass.convStringToDouble(academicRecordTeacher.getClassAverage());
                                                newMaxObtainable = TypeConverterClass.convStringToDouble(academicRecord.getMaxObtainable()) - TypeConverterClass.convStringToDouble(academicRecordTeacher.getPercentageOfTotal());
                                                newPercentageOfTotal = TypeConverterClass.convStringToDouble(academicRecord.getPercentageOfTotal()) - TypeConverterClass.convStringToDouble(academicRecordTeacher.getPercentageOfTotal());
                                                deleteMap.put("AcademicRecordTotal/AcademicRecord/" + academicRecordTeacher.getSchoolID() + "/" + class_subject_year_term + "/classAverage", String.valueOf(newClassAverage));
                                                deleteMap.put("AcademicRecordTotal/AcademicRecord/" + academicRecordTeacher.getSchoolID() + "/" + class_subject_year_term + "/maxObtainable", String.valueOf(newMaxObtainable));
                                                deleteMap.put("AcademicRecordTotal/AcademicRecord/" + academicRecordTeacher.getSchoolID() + "/" + class_subject_year_term + "/percentageOfTotal", String.valueOf(newPercentageOfTotal));
                                                deleteMap.put("AcademicRecordTotal/AcademicRecordClass/" + academicRecordTeacher.getClassID() + "/" + class_subject_year_term + "/classAverage", String.valueOf(newClassAverage));
                                                deleteMap.put("AcademicRecordTotal/AcademicRecordClass/" + academicRecordTeacher.getClassID() + "/" + class_subject_year_term + "/maxObtainable", String.valueOf(newMaxObtainable));
                                                deleteMap.put("AcademicRecordTotal/AcademicRecordClass/" + academicRecordTeacher.getClassID() + "/" + class_subject_year_term + "/percentageOfTotal", String.valueOf(newPercentageOfTotal));
                                                if (academicRecordTeacher.getTeacherID().equals(mFirebaseUser.getUid())) {
                                                    deleteMap.put("AcademicRecordTotal/AcademicRecordTeacher/" + academicRecordTeacher.getTeacherID() + "/" + class_subject_year_term + "/classAverage", String.valueOf(newClassAverage));
                                                    deleteMap.put("AcademicRecordTotal/AcademicRecordTeacher/" + academicRecordTeacher.getTeacherID() + "/" + class_subject_year_term + "/maxObtainable", String.valueOf(newMaxObtainable));
                                                    deleteMap.put("AcademicRecordTotal/AcademicRecordTeacher/" + academicRecordTeacher.getTeacherID() + "/" + class_subject_year_term + "/percentageOfTotal", String.valueOf(newPercentageOfTotal));
                                                }
                                            }

                                            mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordTotal").child("AcademicRecordClass-Student").child(academicRecordTeacher.getClassID()).child(class_subject_year_term).child("Students");
                                            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                            String kidKey = postSnapshot.getKey();
                                                            Double kidScore = TypeConverterClass.convStringToDouble(postSnapshot.getValue(String.class));
                                                            for (int i = 0; i < kidScoreForTeachersModelList.size(); i++) {
                                                                if (kidKey.equals(kidScoreForTeachersModelList.get(i).getKidID())) {
                                                                    double normalizedKidScore = (TypeConverterClass.convStringToDouble(kidScoreForTeachersModelList.get(i).getKidScore()) / TypeConverterClass.convStringToDouble(academicRecordTeacher.getMaxObtainable())) * TypeConverterClass.convStringToDouble(academicRecordTeacher.getPercentageOfTotal());
                                                                    double newKidScore = kidScore - normalizedKidScore;
                                                                    deleteMap.put("AcademicRecordTotal/AcademicRecord-Student/" + academicRecordTeacher.getSchoolID() + "/" + class_subject_year_term + "/Students/" + kidKey, String.valueOf(newKidScore));
                                                                    deleteMap.put("AcademicRecordTotal/AcademicRecordClass-Student/" + academicRecordTeacher.getClassID() + "/" + class_subject_year_term + "/Students/" + kidKey, String.valueOf(newKidScore));
                                                                    if (academicRecordTeacher.getTeacherID().equals(mFirebaseUser.getUid())) {
                                                                        deleteMap.put("AcademicRecordTotal/AcademicRecordTeacher-Student/" + academicRecordTeacher.getTeacherID() + "/" + class_subject_year_term + "/Students/" + kidKey, String.valueOf(newKidScore));
                                                                    }
                                                                    deleteMap.put("AcademicRecordTotal/AcademicRecordStudent/" + kidKey + "/" + class_subject_year_term + "/score", String.valueOf(newKidScore));
                                                                    deleteMap.put("AcademicRecordTotal/AcademicRecordStudent/" + kidKey + "/" + class_subject_year_term + "/maxObtainable", String.valueOf(newMaxObtainable));
                                                                    deleteMap.put("AcademicRecordTotal/AcademicRecordStudent/" + kidKey + "/" + class_subject_year_term + "/percentageOfTotal", String.valueOf(newPercentageOfTotal));
                                                                }
                                                            }
                                                        }
                                                    }

                                                    for (int i = 0; i < kidScoreForTeachersModelList.size(); i++) {
                                                        String kidKey = kidScoreForTeachersModelList.get(i).getKidID();
                                                        deleteMap.put("AcademicRecord/AcademicRecordStudent/" + kidKey + "/" + recordID, null);
                                                    }
                                                    deleteMap.put("AcademicRecord/AcademicRecord/" + academicRecordTeacher.getSchoolID() + "/" + recordID, null);
                                                    deleteMap.put("AcademicRecord/AcademicRecord-Student/" + academicRecordTeacher.getSchoolID() + "/" + recordID, null);
                                                    deleteMap.put("AcademicRecord/AcademicRecordClass/" + academicRecordTeacher.getClassID() + "/" + recordID, null);
                                                    deleteMap.put("AcademicRecord/AcademicRecordClass-Student/" + academicRecordTeacher.getClassID() + "/" + recordID, null);
                                                    if (academicRecordTeacher.getTeacherID().equals(mFirebaseUser.getUid())) {
                                                        deleteMap.put("AcademicRecord/AcademicRecordTeacher/" + academicRecordTeacher.getTeacherID() + "/" + recordID, null);
                                                        deleteMap.put("AcademicRecord/AcademicRecordTeacher-Student/" + academicRecordTeacher.getTeacherID() + "/" + recordID, null);
                                                    }

                                                    updateRef.updateChildren(deleteMap, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            progressDialog.dismiss();
//                                                        ((Activity)context).finish();
                                                        }
                                                    });
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
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            CustomToast.whiteBackgroundBottomToast(context, "Internet is down, check your connection and try again");
        }
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
