package com.celerii.celerii.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.Activities.TeacherPerformance.TeacherViewResultDetailWithDeleteActivity;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecord;
import com.celerii.celerii.models.AcademicRecordTeacher;
import com.celerii.celerii.models.KidScoreForTeachersModel;
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

/**
 * Created by DELL on 1/20/2019.
 */

public class TeacherAcademicRecordDetailAdapter extends RecyclerView.Adapter<TeacherAcademicRecordDetailAdapter.MyViewHolder>{

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    boolean connected;

    private List<AcademicRecordTeacher> AcademicRecordTeacherList;
    private ArrayList<KidScoreForTeachersModel> kidScoreForTeachersModelList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView examType, score, className, date, maxObtainable, term, year, viewDetails, delete;
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
            viewDetails = (TextView) view.findViewById(R.id.viewdetails);
            delete = (TextView) view.findViewById(R.id.delete);
            clickableView = view;
        }
    }

    public TeacherAcademicRecordDetailAdapter(List<AcademicRecordTeacher> AcademicRecordTeacherList, Context context) {
        this.AcademicRecordTeacherList = AcademicRecordTeacherList;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                connected = snapshot.getValue(Boolean.class);
                if (connected) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
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

        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TeacherViewResultDetailWithDeleteActivity.class);
                Bundle bundle = new Bundle();
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
                TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
                TextView delete = (TextView) dialog.findViewById(R.id.delete);
                dialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAcademicRecord(academicRecordTeacher);
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


    private double newClassAverage = 0.0;
    private double newMaxObtainable = 0.0;
    private double newPercentageOfTotal = 0.0;
    private void deleteAcademicRecord(final AcademicRecordTeacher academicRecordTeacher){
        if (connected) {
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
}
