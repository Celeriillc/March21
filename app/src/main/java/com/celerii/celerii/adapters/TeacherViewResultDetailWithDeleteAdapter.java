package com.celerii.celerii.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celerii.celerii.R;
import com.celerii.celerii.helperClasses.CustomProgressDialogOne;
import com.celerii.celerii.helperClasses.CustomToast;
import com.celerii.celerii.helperClasses.Date;
import com.celerii.celerii.helperClasses.Term;
import com.celerii.celerii.helperClasses.TypeConverterClass;
import com.celerii.celerii.models.AcademicRecord;
import com.celerii.celerii.models.AcademicRecordTeacher;
import com.celerii.celerii.models.KidScoreForTeachersModel;
import com.bumptech.glide.Glide;
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

/**
 * Created by DELL on 1/23/2019.
 */

public class TeacherViewResultDetailWithDeleteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    FirebaseAuth auth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    String recordID;
    boolean connected;

    private List<KidScoreForTeachersModel> kidScoreForTeachersModelList;
    private AcademicRecordTeacher academicRecordTeacher;
    private Context context;
    public static final int Header = 1;
    public static final int Normal = 2;
    public static final int Footer = 3;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView kidName, kidScore;
        public ImageView kidPicture;

        public MyViewHolder(final View view) {
            super(view);
            kidName = (TextView) view.findViewById(R.id.kidname);
            kidScore = (TextView) view.findViewById(R.id.kidscore);
            kidPicture = (ImageView) view.findViewById(R.id.kidpicture);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView deleteRecord, teacher, className, subject, testType, date, year, term, classAverage, maximumObtainable, percentageOfTotal;

        public HeaderViewHolder(View view) {
            super(view);
            deleteRecord = (TextView) view.findViewById(R.id.deleterecord);
            teacher = (TextView) view.findViewById(R.id.teacher);
            className = (TextView) view.findViewById(R.id.classname);
            subject = (TextView) view.findViewById(R.id.subject);
            testType = (TextView) view.findViewById(R.id.testtype);
            date = (TextView) view.findViewById(R.id.date);
            year = (TextView) view.findViewById(R.id.year);
            term = (TextView) view.findViewById(R.id.term);
            classAverage = (TextView) view.findViewById(R.id.classaverage);
            maximumObtainable = (TextView) view.findViewById(R.id.maximumscore);
            percentageOfTotal = (TextView) view.findViewById(R.id.percentageoftotal);
        }
    }

    public TeacherViewResultDetailWithDeleteAdapter(List<KidScoreForTeachersModel> kidScoreForTeachersModelList, AcademicRecordTeacher academicRecordTeacher, String recordID, Context context) {
        this.kidScoreForTeachersModelList = kidScoreForTeachersModelList;
        this.academicRecordTeacher = academicRecordTeacher;
        this.context = context;

        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseUser = auth.getCurrentUser();
        this.recordID = recordID;

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView;
        switch (viewType) {
            case Normal:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_view_result_detail_with_delete_row, parent, false);
                return new TeacherViewResultDetailWithDeleteAdapter.MyViewHolder(rowView);
            case Header:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_view_result_detail_with_delete_header, parent, false);
                return new TeacherViewResultDetailWithDeleteAdapter.HeaderViewHolder(rowView);
            default:
                rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_view_result_detail_with_delete_row, parent, false);
                return new TeacherViewResultDetailWithDeleteAdapter.MyViewHolder(rowView);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).teacher.setText(academicRecordTeacher.getTeacherName());
            ((HeaderViewHolder) holder).className.setText(academicRecordTeacher.getClassName());
            ((HeaderViewHolder) holder).subject.setText(academicRecordTeacher.getSubject());
            ((HeaderViewHolder) holder).testType.setText(academicRecordTeacher.getTestType());
            ((HeaderViewHolder) holder).date.setText(Date.DateFormatMMDDYYYY(academicRecordTeacher.getDate()));
            ((HeaderViewHolder) holder).year.setText(academicRecordTeacher.getAcademicYear());
            ((HeaderViewHolder) holder).term.setText(Term.Term(academicRecordTeacher.getTerm()));
            ((HeaderViewHolder) holder).classAverage.setText(TypeConverterClass.convStringToIntString(academicRecordTeacher.getClassAverage()));
            ((HeaderViewHolder) holder).maximumObtainable.setText(academicRecordTeacher.getMaxObtainable());
            ((HeaderViewHolder) holder).percentageOfTotal.setText(academicRecordTeacher.getPercentageOfTotal());

            ((HeaderViewHolder) holder).deleteRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            deleteAcademicRecord();
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        else if (holder instanceof MyViewHolder){
            KidScoreForTeachersModel kidScoreForTeachersModel = kidScoreForTeachersModelList.get(position);

            ((MyViewHolder) holder).kidName.setText(kidScoreForTeachersModel.getKidName());
            ((MyViewHolder) holder).kidScore.setText(kidScoreForTeachersModel.getKidScore());

            Glide.with(context)
                    .load(kidScoreForTeachersModel.getKidProfilePicture())
                    .placeholder(R.drawable.profileimageplaceholder)
                    .error(R.drawable.profileimageplaceholder)
                    .centerCrop()
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(((MyViewHolder) holder).kidPicture);
        }
    }

    @Override
    public int getItemCount() {
        return kidScoreForTeachersModelList.size();
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
        return position == kidScoreForTeachersModelList.size () + 1;
    }

    private double newClassAverage = 0.0;
    private double newMaxObtainable = 0.0;
    private double newPercentageOfTotal = 0.0;
    private void deleteAcademicRecord(){
        if (connected) {
            final CustomProgressDialogOne progressDialog = new CustomProgressDialogOne(context);
            progressDialog.show();

            final Map<String, Object> deleteMap = new HashMap<String, Object>();
            final DatabaseReference updateRef = mFirebaseDatabase.getReference();
            String classId = academicRecordTeacher.getClassID();
            final String subject = academicRecordTeacher.getSubject();
            String year = academicRecordTeacher.getAcademicYear();
            String term = academicRecordTeacher.getTerm();
            final String subject_year_term = subject + "_" + year + "_" + term;
            final String class_subject_year_term = classId + "_" + subject + "_" + year + "_" + term;

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
                        mDatabaseReference = mFirebaseDatabase.getReference().child("AcademicRecordTotal").child("AcademicRecordClass").child(academicRecordTeacher.getClassID()).child(class_subject_year_term);
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
                                                ((Activity) context).finish();
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
        } else {
            CustomToast.whiteBackgroundBottomToast(context, "Internet is down, check your connection and try again");
        }
    }
}
